package com.example.friendinfoservice

import com.fasterxml.jackson.databind.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.autoconfigure.web.reactive.*
import org.springframework.boot.test.context.*
import org.springframework.http.*
import org.springframework.test.context.junit.jupiter.*
import org.springframework.test.web.reactive.server.*
import java.time.*
import java.time.format.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = [FriendInfoServiceApplication::class])
@AutoConfigureWebTestClient
class FriendControllerTest(@Autowired val client: WebTestClient,
                           @Autowired val repository: UserFriendsRepository,
                           @Autowired val objectMapper: ObjectMapper) {

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

    @Nested
    inner class GetAllFriends {

        @Test
        fun `it returns a given repository entry`() {
            client.get().uri("/friends/${userWithMultipleFriends.id}")
                    .exchange()
                    .expectStatus().isOk
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody<UserFriends>()
                    .isEqualTo(userWithMultipleFriends)
        }

        @Test
        fun `it returns only matching friend relations, when a query is given`() {
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
        fun `it returns an empty entry with the given id, when the given id is unknown`() {
            val unknownId = 1000L

            client.get().uri("/friends/$unknownId")
                    .exchange()
                    .expectStatus().isOk
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody<UserFriends>()
                    .isEqualTo(UserFriends(unknownId))
        }
    }

    @Nested
    inner class CreateUserFriends {

        @Test
        fun `it returns a given entity`() {
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
        fun `it adds the given entity to the repository`() {
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
        fun `it returns the entity with the URI id, when URI id and entity id don't match`() {
            val uriId = 1000L
            val friendRelations = mutableMapOf(1001L to FriendRelation(1001L, today))
            val requestBody = UserFriends(2000L, friendRelations)

            client.put().uri("/friends/$uriId")
                    .bodyValue(requestBody)
                    .exchange()
                    .expectBody<UserFriends>()
                    .isEqualTo(UserFriends(uriId, friendRelations))
        }

    }


    @Nested
    inner class UpdateUserFriends {

        private fun createJsonPatch(op: String, path: String, value: String): String {
            val formattedValue = if (value[0] == '{' || value[0] == '[') value else "\"$value\""
            return """
                [{"op": "$op", "path": "$path", "value": $formattedValue}]
            """.trimIndent()
        }

        @Test
        fun `it adds a new friend to an existing repository entity`() {
            val newFriendId = 999L
            val friendRelationToAdd = FriendRelation(newFriendId, today)

            val addFriendPatch = createJsonPatch(
                    "add",
                    "/friends/$newFriendId",
                    objectMapper.convertValue(friendRelationToAdd, JsonNode::class.java).toPrettyString())

            client.patch().uri("/friends/${userWithMultipleFriends.id}")
                    .contentType(MediaType.valueOf("application/json-patch+json"))
                    .bodyValue(addFriendPatch)
                    .exchange()
            assertThat(repository.findById(userWithMultipleFriends.id).block()?.friends)
                    .containsKey(newFriendId)
        }

        @Test
        fun `it modifies an existing friend inside an entity and returns the entity`() {
            val modifiedDate = LocalDate.now().plusMonths(1)
            val modifySincePatch = createJsonPatch(
                    "replace",
                    "/friends/${friendRelation2Id}/since",
                    modifiedDate.format(DateTimeFormatter.ISO_DATE))

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

}
