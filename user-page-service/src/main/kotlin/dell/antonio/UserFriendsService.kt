package dell.antonio

import dell.antonio.model.*
import mu.*
import org.springframework.cloud.circuitbreaker.resilience4j.*
import org.springframework.stereotype.*
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.*

private val log = KotlinLogging.logger {}

@Service
class UserFriendsService(val webClientBuilder: WebClient.Builder,
                         val gameBackendUri: GameBackendUri,
                         @Suppress("SpringJavaInjectionPointsAutowiringInspection")
                         val circuitBreakerFactory: ReactiveResilience4JCircuitBreakerFactory
) {

    fun getUserFriends(userId: String): Mono<MutableMap<String, FriendRelation>> {

        return webClientBuilder
                .baseUrl(gameBackendUri.friends.toString())
                .build()
                .get()
                .uri("/friends/$userId")
                .retrieve()
                .bodyToMono(UserFriends::class.java)
                .map { it.friends }
                .transform { circuitBreakerFactory.create("getUserFriends").run(it) { getFallbackUserFriends(userId) } }

    }

    fun getFallbackUserFriends(userId: String): Mono<MutableMap<String, FriendRelation>> =
            Mono.just(mutableMapOf())

}
