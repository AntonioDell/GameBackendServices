package dell.antonio

import dell.antonio.model.*
import org.springframework.cloud.netflix.hystrix.*
import org.springframework.stereotype.*
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.*

@Service
class UserInfoService(val webClientBuilder: WebClient.Builder,
                      val gameBackendUri: GameBackendUri) {


    fun getUserInfo(userId: String): Mono<UserInfo> {
        val call = webClientBuilder
                .baseUrl(gameBackendUri.users.toString())
                .build()
                .get()
                .uri("/users/$userId")
                .retrieve()
                .bodyToMono(UserInfo::class.java)
        return HystrixCommands.from(call)
                .fallback(getFallbackUserInfo(userId))
                .commandName("getUserInfo")
                .toMono()
    }

    fun getFallbackUserInfo(userId: String) = Mono.just(UserInfo("No user found"))


}
