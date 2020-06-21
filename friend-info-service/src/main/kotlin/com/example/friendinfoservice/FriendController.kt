package com.example.friendinfoservice

import com.fasterxml.jackson.databind.*
import com.github.fge.jsonpatch.*
import org.bson.types.*
import org.springframework.beans.factory.annotation.*
import org.springframework.format.annotation.*
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.*
import java.time.*
import javax.validation.*

@RestController
@RequestMapping("/friends")
class FriendController(@Autowired val userFriendsRepository: UserFriendsRepository,
                       @Autowired val objectMapper: ObjectMapper) {


    @GetMapping("/{id}")
    fun getAllFriends(@PathVariable("id") id: ObjectId,
                      @RequestParam("friendsSince")
                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) friendsSince: LocalDate? = null): Mono<UserFriends> {
        println("ADA: getAllFriends was called!")
        return if (friendsSince != null) {
            userFriendsRepository.findLatestFriendsSince(id, friendsSince)
        } else {
            userFriendsRepository.findById(id)
        }.defaultIfEmpty(UserFriends(id)).map {
            it
        }
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUserFriends(@PathVariable("id") id: ObjectId,
                          @Valid @RequestBody userFriends: UserFriends): Mono<UserFriends> =
            if (userFriends.id != id) {
                userFriendsRepository.save(UserFriends(id, userFriends.friends))
            } else {
                userFriendsRepository.save(userFriends)
            }


    @PatchMapping("/{id}", consumes = ["application/json-patch+json"])
    fun addFriend(@PathVariable("id") id: ObjectId,
                  @Valid @RequestBody jsonPatch: JsonPatch): Mono<UserFriends> = userFriendsRepository
            .findById(id)
            .map {
                val userFriendsJson = objectMapper.convertValue(it, JsonNode::class.java)
                val patchedUserFriends = jsonPatch.apply(userFriendsJson)
                objectMapper.convertValue(patchedUserFriends, UserFriends::class.java)
            }
            .flatMap {
                userFriendsRepository.save(it)
            }
}
