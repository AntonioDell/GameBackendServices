package com.example.friendinfoservice

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.json.Json

@ExtendWith(SpringExtension::class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = [FriendInfoServiceApplication::class])
@AutoConfigureWebTestClient
class FriendControllerTest(@Autowired val client: WebTestClient,
                           @Autowired val repository: UserFriendsRepository) {

    private final val today: LocalDate = LocalDate.now()

    private final val friendRelation2Id = 2L
    private final val friendsSince2DaysAgo = mutableMapOf(
            friendRelation2Id to FriendRelation(friendRelation2Id, today.minusDays(1)),
            3L to FriendRelation(3L, today.minusDays(2)))

    private final val userWithoutFriends = UserFriends(1L)
    private final val userWithMultipleFriends = UserFriends(
            11L,
            mutableMapOf(4L to FriendRelation(4L, today.minusDays(3)))
                    .also { it.putAll(friendsSince2DaysAgo) })

    @BeforeEach
    fun setup() {
        repository.deleteAll()
                .thenMany(repository.saveAll(listOf(
                        userWithoutFriends,
                        userWithMultipleFriends
                )))
                .blockLast(Duration.ofSeconds(10))
    }

    @Test
    fun getAllFriends_givenARepositoryEntry_returnsIt() {

        client.get().uri("/friends/${userWithMultipleFriends.id}")
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody<UserFriends>()
                .isEqualTo(userWithMultipleFriends)
    }

    @Test
    fun getAllFriends_givenSinceQuery_returnsOnlyMatchingFriendRelations() {
        val sinceDate = today.minusDays(2)
        val sinceDateFormatted = sinceDate.format(DateTimeFormatter.ISO_DATE)

        client.get().uri(
                "/friends/${userWithMultipleFriends.id}" +
                        "?friendsSince=${sinceDateFormatted}")
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody<UserFriends>()
                .isEqualTo(UserFriends(userWithMultipleFriends.id, friendsSince2DaysAgo))
    }

    @Test
    fun getAllFriends_givenUnknownId_returnsEmptyUserFriendsWithGivenId() {
        val unknownId = 1000L

        client.get().uri("/friends/$unknownId")
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody<UserFriends>()
                .isEqualTo(UserFriends(unknownId))
    }

    @Test
    fun createUserFriends_givenUserFriendsNotSavedInRepo_returnsGivenUserFriends() {
        val newId = 1000L
        val newUserFriends = UserFriends(
                newId,
                mutableMapOf(1001L to FriendRelation(1001L, today)))

        client.put().uri("/friends/$newId")
                .bodyValue(newUserFriends)
                .exchange()
                .expectBody<UserFriends>()
                .isEqualTo(newUserFriends)
    }

    @Test
    fun createUserFriends_givenUserFriendsNotSavedInRepo_expectItToBeAddedToRepo() {
        val newId = 1000L
        val newUserFriends = UserFriends(
                newId,
                mutableMapOf(1001L to FriendRelation(1001L, today)))

        client.put().uri("/friends/$newId")
                .bodyValue(newUserFriends)
                .exchange()
        assertThat(repository.findById(newId).block()).isEqualTo(newUserFriends)
    }


    @Test
    fun createUserFriends_givenMismatchingIds_returnsUserFriendsWithUriId() {
        val uriId = 1000L
        val friendRelations = mutableMapOf(1001L to FriendRelation(1001L, today))
        val requestBody = UserFriends(2000L, friendRelations)

        client.put().uri("/friends/$uriId")
                .bodyValue(requestBody)
                .exchange()
                .expectBody<UserFriends>()
                .isEqualTo(UserFriends(uriId, friendRelations))
    }

    @Test
    fun updateUserFriends_givenFriendToAddToExistingUserFriends_expectItToBeAddedToRepo() {
        val friendRelationToAdd = FriendRelation(1000L, today)

        client.patch().uri("/friends/${userWithMultipleFriends.id}")
                .bodyValue(friendRelationToAdd)
                .exchange()
        assertThat(repository.findById(userWithMultipleFriends.id).block()?.friends)
                .containsKey(1000L)
    }

    @Test
    fun updateUserFriends_givenFriendToAddToExistingUserFriends_returnsModifiedUserFriends() {
        val friendRelationToAdd = FriendRelation(1000L, today)

        val expectedFriends = userWithMultipleFriends.friends.toMutableMap()
        expectedFriends[1000L] = friendRelationToAdd
        client.patch().uri("/friends/${userWithMultipleFriends.id}")
                .bodyValue(friendRelationToAdd)
                .exchange()
                .expectBody<UserFriends>()
                .isEqualTo(UserFriends(userWithMultipleFriends.id, expectedFriends))
    }

    @Test
    fun updateUserFriends_givenExistingFriendRelation_expectItToBeModifiedAndSaved() {
        val modifiedDate = LocalDate.now().plusMonths(1)
        val modifySincePatch = Json.createPatchBuilder().replace(
                "/friends/$friendRelation2Id/since",
                modifiedDate.format(DateTimeFormatter.ISO_DATE))
                .build()

        val expectedFriends = userWithMultipleFriends.friends.toMutableMap()
        expectedFriends.replace(friendRelation2Id, FriendRelation(friendRelation2Id, modifiedDate))

        client.patch().uri("/friends/${userWithMultipleFriends.id}")
                .contentType(MediaType.valueOf("application/json-patch+json"))
                .bodyValue(modifySincePatch)
                .exchange()
                .expectBody<UserFriends>()
                .isEqualTo(UserFriends(userWithMultipleFriends.id, expectedFriends))
    }

}
