package com.example.friendinfoservice

import com.fasterxml.jackson.databind.*
import com.github.fge.jsonpatch.*
import org.springframework.beans.factory.annotation.*
import org.springframework.format.annotation.*
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.*
import java.time.*
import javax.validation.*

@RestController
@RequestMapping("/friends")
class FriendController {

    @Autowired
    final lateinit var userFriendsRepository: UserFriendsRepository

    @Autowired
    final lateinit var objectMapper: ObjectMapper

    @GetMapping("/{id}")
    fun getAllFriends(@PathVariable("id") id: Long,
                      @RequestParam("friendsSince")
                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) friendsSince: LocalDate? = null)
            : Mono<UserFriends> {
        val foundUserFriends = if (friendsSince != null) {
            userFriendsRepository.findLatestFriendsSince(id, friendsSince)
        } else {
            userFriendsRepository.findById(id)
        }

        return foundUserFriends.defaultIfEmpty(UserFriends(id)).map {
            it
        }
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUserFriends(@PathVariable("id") id: Long,
                          @Valid @RequestBody userFriends: UserFriends): Mono<UserFriends> {
        return if (userFriends.id != id) {
            userFriendsRepository.save(UserFriends(id, userFriends.friends))
        } else {
            userFriendsRepository.save(userFriends)
        }
    }

    @PatchMapping("/{id}", consumes = ["application/json-patch+json"])
    fun addFriend(@PathVariable("id") id: Long,
                  @Valid @RequestBody jsonPatch: JsonPatch): Mono<UserFriends> {

        return userFriendsRepository.findById(id)
                .map {
                    val userFriendsJson = objectMapper.convertValue(it, JsonNode::class.java)
                    val patchedUserFriends = jsonPatch.apply(userFriendsJson)
                    objectMapper.convertValue(patchedUserFriends, UserFriends::class.java)
                }
                .flatMap {
                    userFriendsRepository.save(it)
                }
    }
}
