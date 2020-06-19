package dell.antonio

import dell.antonio.model.*
import org.springframework.beans.factory.annotation.*
import org.springframework.cloud.netflix.hystrix.*
import org.springframework.stereotype.*
import org.springframework.web.reactive.function.client.*
import org.springframework.web.util.*
import reactor.core.publisher.*

@Service
class UserInfoService(@Autowired val webClientBuilder: WebClient.Builder,
                      @Autowired val gameBackendUri: GameBackendUri) {


    fun getUserInfo(userId: String): Mono<UserInfo> {
        val usersUri = UriComponentsBuilder.fromUri(gameBackendUri.users)
                .pathSegment(userId)
                .build()
                .toUri()
        val call = webClientBuilder.build()
                .get()
                .uri(usersUri)
                .retrieve()
                .bodyToMono(UserInfo::class.java)
        return HystrixCommands.from(call)
                .fallback(getFallbackUserInfo(userId))
                .commandName("getUserInfo")
                .toMono()
    }

    fun getFallbackUserInfo(userId: String) = Mono.just(UserInfo("No user found"))


}
