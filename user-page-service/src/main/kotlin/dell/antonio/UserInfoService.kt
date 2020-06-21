package dell.antonio

import dell.antonio.model.*
import mu.*
import org.springframework.cloud.circuitbreaker.resilience4j.*
import org.springframework.stereotype.*
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.*

private val log = KotlinLogging.logger {}

@Service
class UserInfoService(val webClientBuilder: WebClient.Builder,
                      val gameBackendUri: GameBackendUri,
                      @Suppress("SpringJavaInjectionPointsAutowiringInspection")
                      val circuitBreakerFactory: ReactiveResilience4JCircuitBreakerFactory) {


    fun getUserInfo(userId: String): Mono<UserInfo> {
        return webClientBuilder
                .baseUrl(gameBackendUri.users.toString())
                .build()
                .get()
                .uri("/users/$userId")
                .retrieve()
                .bodyToMono(UserInfo::class.java)
                .transform { circuitBreakerFactory.create("getUserInfo").run(it) { getFallbackUserInfo(userId) } }
    }

    fun getFallbackUserInfo(userId: String) = Mono.just(UserInfo("No user found"))


}
