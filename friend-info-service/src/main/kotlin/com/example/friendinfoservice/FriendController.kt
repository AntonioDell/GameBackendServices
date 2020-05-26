package com.example.friendinfoservice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.time.LocalDate
import javax.json.JsonPatch
import javax.validation.Valid

@RestController
@RequestMapping("/friends")
class FriendController {

    @Autowired
    final lateinit var userFriendsRepository: UserFriendsRepository

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

        return foundUserFriends.defaultIfEmpty(UserFriends(id))
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
                    it
                }
                .flatMap {
                    userFriendsRepository.save(it)
                }
    }
}
