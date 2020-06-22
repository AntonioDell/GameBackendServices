package dell.antonio

import dell.antonio.model.*
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.*


@RestController
@RequestMapping("/user-page")
class UserPageController(val userInfoService: UserInfoService,
                         val userFriendsService: UserFriendsService) {

    @GetMapping("/{userId}")
    fun getUserPage(@PathVariable("userId") userId: String): Mono<UserPage> {

        return userInfoService.getUserInfo(userId)
                .zipWith(userFriendsService.getUserFriends(userId))
                .map {
                    UserPage(userId, it.t1, it.t2)
                }
    }


}
