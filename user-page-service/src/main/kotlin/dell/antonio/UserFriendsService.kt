package dell.antonio

import dell.antonio.model.*
import org.springframework.cloud.netflix.hystrix.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.*

@Service
class UserFriendsService(val webClientBuilder: WebClient.Builder,
                         val gameBackendUri: GameBackendUri
) {

    fun getUserFriends(userId: String): Mono<MutableMap<String, FriendRelation>> {
        val call = webClientBuilder
                .baseUrl(gameBackendUri.friends.toString())
                .build()
                .get()
                .uri("/friends/$userId")
                .retrieve()
                .bodyToMono(UserFriends::class.java)
                .map { it.friends }

        return HystrixCommands.from(call)
                .fallback(getFallbackUserFriends(userId))
                .commandName("getUserFriends")
                .toMono()
    }

    fun getFallbackUserFriends(userId: String): Mono<MutableMap<String, FriendRelation>> =
            Mono.just(mutableMapOf())

}
