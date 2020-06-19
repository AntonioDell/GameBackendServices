package dell.antonio

import dell.antonio.model.*
import org.springframework.beans.factory.annotation.*
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.*

@RestController
@RequestMapping("/user-page")
class UserPageController(@Autowired val userInfoService: UserInfoService,
                         @Autowired val userFriendsService: UserFriendsService) {

    @GetMapping("/{userId}")
    fun getUserPage(@PathVariable("userId") userId: String): Mono<UserPage> {
        val userPage = userInfoService.getUserInfo(userId)
                .zipWith(userFriendsService.getUserFriends(userId))
                .map {
                    UserPage(userId, it.t1, it.t2)
                }

        return userPage.defaultIfEmpty(UserPage(userId))
    }


}
