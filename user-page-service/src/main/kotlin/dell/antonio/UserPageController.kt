package dell.antonio

import dell.antonio.model.*
import org.springframework.beans.factory.annotation.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@RestController
@RequestMapping("/user-page")
class UserPageController {
    @Autowired
    final lateinit var webClientBuilder: WebClient.Builder

    @GetMapping("/{userId}")
    fun getUserPage(@PathVariable("userId") userId: UUID): UserPage {

        val userInfo = webClientBuilder.build()
                .get()
                .uri("http://localhost:8101/users/$userId")
                .retrieve()
                .bodyToMono(UserInfo::class.java)

        val friendsList = webClientBuilder.build()
                .get()
                .uri("http://localhost:8102/friends/$userId")
                .retrieve()
                .bodyToMono(UserFriends::class.java)
                .map { it.friends }



        val userPage = userInfo.zipWith(friendsList)
                .map {
                    UserPage(userId, it.t1, it.t2)
                }
                .block()

        return userPage ?: UserPage(userId)
    }
}
