package dell.antonio

import dell.antonio.model.*
import org.springframework.beans.factory.annotation.*
import org.springframework.cloud.netflix.hystrix.*
import org.springframework.stereotype.*
import org.springframework.web.reactive.function.client.*
import org.springframework.web.util.*
import reactor.core.publisher.*

@Service
class UserFriendsService(@Autowired val webClientBuilder: WebClient.Builder,
                         @Autowired val gameBackendUri: GameBackendUri) {

    fun getUserFriends(userId: String): Mono<MutableMap<String, FriendRelation>> {
        val friendsUri = UriComponentsBuilder.fromUri(gameBackendUri.friends)
                .pathSegment(userId)
                .build()
                .toUri()
        val call = webClientBuilder.build()
                .get()
                .uri(friendsUri)
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
