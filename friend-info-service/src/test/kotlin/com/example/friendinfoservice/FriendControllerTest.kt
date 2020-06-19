package com.example.friendinfoservice

import com.fasterxml.jackson.databind.*
import com.github.fge.jackson.jsonpointer.*
import com.github.fge.jsonpatch.*
import org.assertj.core.api.Assertions.*
import org.bson.types.*
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
    private final val friendRelation2Id = ObjectId()
    private final val friendRelation3Id = ObjectId()
    private final val friendRelation4Id = ObjectId()
    private final val friendsSince2DaysAgo = mutableMapOf(
            friendRelation2Id to FriendRelation(friendRelation2Id, today.minusDays(1)),
            friendRelation3Id to FriendRelation(friendRelation3Id, today.minusDays(2)))

    private final val userWithoutFriends = UserFriends(ObjectId())
    private final val userWithMultipleFriends = UserFriends(
            ObjectId(),
            mutableMapOf(friendRelation4Id to FriendRelation(friendRelation4Id, today.minusDays(3)))
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
            val unknownId = ObjectId()

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
            val newId = ObjectId()
            val newId2 = ObjectId()
            val newUserFriends = UserFriends(
                    newId,
                    mutableMapOf(newId2 to FriendRelation(newId2, today)))

            client.put().uri("/friends/$newId")
                    .bodyValue(newUserFriends)
                    .exchange()
                    .expectBody<UserFriends>()
                    .isEqualTo(newUserFriends)
        }

        @Test
        fun `it adds the given entity to the repository`() {
            val newId = ObjectId()
            val newId2 = ObjectId()
            val newUserFriends = UserFriends(
                    newId,
                    mutableMapOf(newId2 to FriendRelation(newId2, today)))

            client.put().uri("/friends/$newId")
                    .bodyValue(newUserFriends)
                    .exchange()
            assertThat(repository.findById(newId).block()).isEqualTo(newUserFriends)
        }

        @Test
        fun `it returns the entity with the URI id, when URI id and entity id don't match`() {
            val uriId = ObjectId()
            val newId2 = ObjectId()

            val friendRelations = mutableMapOf(newId2 to FriendRelation(newId2, today))
            val requestBody = UserFriends(ObjectId(), friendRelations)

            client.put().uri("/friends/$uriId")
                    .bodyValue(requestBody)
                    .exchange()
                    .expectBody<UserFriends>()
                    .isEqualTo(UserFriends(uriId, friendRelations))
        }

    }


    @Nested
    inner class UpdateUserFriends {

        @Test
        fun `it adds a new friend to an existing repository entity`() {
            val newFriendId = ObjectId();
            val friendRelationToAdd = FriendRelation(newFriendId, today)

            val addFriendPatch = JsonPatch(listOf(AddOperation(
                    JsonPointer("/friends/$newFriendId"),
                    objectMapper.convertValue(friendRelationToAdd, JsonNode::class.java))))

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
            val modifySincePatch = JsonPatch(listOf(ReplaceOperation(
                    JsonPointer("/friends/${friendRelation2Id}/since"),
                    objectMapper.convertValue(modifiedDate, JsonNode::class.java))))

            val expectedFriends = userWithMultipleFriends.friends.toMutableMap()
            expectedFriends.replace(friendRelation2Id, FriendRelation(friendRelation2Id, modifiedDate))

            client.patch().uri("/friends/${userWithMultipleFriends.id}")
                    .contentType(MediaType.valueOf("application/json-patch+json"))
                    .bodyValue(modifySincePatch)
                    .exchange()
                    .expectBody<UserFriends>()
                    .isEqualTo(UserFriends(userWithMultipleFriends.id, expectedFriends))
        }

        @Test
        fun `it removes an existing friend inside an entity and returns the entity`() {
            val removeJsonPatch = JsonPatch(listOf(RemoveOperation(
                    JsonPointer("/friends/$friendRelation2Id"))))
            val expectedFriends = userWithMultipleFriends.friends.toMutableMap()
            expectedFriends.remove(friendRelation2Id)

            client.patch().uri("/friends/${userWithMultipleFriends.id}")
                    .contentType(MediaType.valueOf("application/json-patch+json"))
                    .bodyValue(removeJsonPatch)
                    .exchange()
                    .expectBody<UserFriends>()
                    .isEqualTo(UserFriends(userWithMultipleFriends.id, expectedFriends))
        }

        @Test
        fun `it allows for multiple json patch operations to be chained`() {
            val modifiedDate = LocalDate.now().plusMonths(1)
            val multiplePatchOperations = JsonPatch(listOf(
                    RemoveOperation(JsonPointer("/friends/$friendRelation2Id")),
                    ReplaceOperation(
                            JsonPointer("/friends/$friendRelation3Id/since"),
                            objectMapper.convertValue(modifiedDate, JsonNode::class.java))))

            val expectedFriends = userWithMultipleFriends.friends.toMutableMap()
            expectedFriends.remove(friendRelation2Id)
            expectedFriends.replace(friendRelation3Id, FriendRelation(friendRelation3Id, modifiedDate))

            client.patch().uri("/friends/${userWithMultipleFriends.id}")
                    .contentType(MediaType.valueOf("application/json-patch+json"))
                    .bodyValue(multiplePatchOperations)
                    .exchange()
                    .expectBody<UserFriends>()
                    .isEqualTo(UserFriends(userWithMultipleFriends.id, expectedFriends))
        }
    }

}
