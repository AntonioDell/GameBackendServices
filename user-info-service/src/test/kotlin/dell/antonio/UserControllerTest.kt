package dell.antonio

import com.fasterxml.jackson.databind.*
import dell.antonio.model.*
import org.assertj.core.api.Assertions.*
import org.bson.types.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.autoconfigure.web.reactive.*
import org.springframework.boot.test.context.*
import org.springframework.data.domain.*
import org.springframework.http.*
import org.springframework.test.context.junit.jupiter.*
import org.springframework.test.web.reactive.server.*
import java.time.*

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = [UserInfoServiceApplication::class])
@ExtendWith(SpringExtension::class)
@AutoConfigureWebTestClient
class UserControllerTest(@Autowired val client: WebTestClient,
                         @Autowired val repository: UserRepository,
                         @Autowired val objectMapper: ObjectMapper) {

    final val user1Id = ObjectId()
    private final val user2Id = ObjectId()
    val user1 = User(
            user1Id,
            "Uncle Bob",
            Address("Teststreet", "1a", "1337", "Testcity"))
    val user2 = User(
            user2Id,
            "Terry Crews",
            Address("Testlane", "1", "7331", "Testvillage"))

    @BeforeEach
    fun setup() {
        repository.deleteAll()
                .thenMany(repository.saveAll(listOf(user1, user2)))
                .blockLast(Duration.ofSeconds(10))
    }

    @Nested
    inner class GetUser {
        @Test
        fun `it returns a given repository entry`() {
            client.get().uri("/users/$user1Id")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody<User>()
                    .isEqualTo(user1)
        }

        @Test
        fun `it returns a default entry with the given id, when no entry is found`() {
            val defaultId = ObjectId()
            client.get().uri("/users/$defaultId")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody<User>()
                    .isEqualTo(User(defaultId))
        }
    }

    @Nested
    inner class GetAllUsers {
        @Test
        fun `it returns all entities in a repository`() {
            val allUsersJson = objectMapper.convertValue(
                    mapOf("list" to listOf(user1, user2)),
                    JsonNode::class.java)

            client.get().uri("/users")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody<JsonNode>()
                    .isEqualTo(allUsersJson)
        }

        @Test
        fun `it returns a map with an empty list, if no entities exist`() {
            repository.deleteAll().block()
            val emptyMapJson = objectMapper.convertValue(
                    mapOf("list" to listOf<User>()),
                    JsonNode::class.java)
            client.get().uri("/users")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody<JsonNode>()
                    .isEqualTo(emptyMapJson)
        }
    }

    @Nested
    inner class CreateUser {
        @Test
        fun `it adds the given new user to the repository`() {
            val addedUserName = "Added Username"
            val newUser = User(null, addedUserName,
                    Address("TS", "1", "999", "TC"))

            client.put().uri("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(newUser)
                    .exchange()
                    .expectStatus().isOk
            assertThat(repository.findOne(Example.of(
                    User(null, addedUserName),
                    ExampleMatcher.matchingAny())).block()!!.userName)
                    .isEqualTo(addedUserName)
        }

        @Test
        fun `it returns the given new user with an id`() {
            val newUser = User(null, "Added Username",
                    Address("TS", "1", "999", "TC"))

            val returnedUser = client.put().uri("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(newUser)
                    .exchange()
                    .expectBody<User>()
                    .returnResult()
                    .responseBody
            assertThat(returnedUser!!.userName).isEqualTo(newUser.userName)
            assertThat(returnedUser.id).isNotNull()
        }

        @Test
        fun `it returns a bad request if userName is empty`() {
            val newUser = User(null, "",
                    Address("TS", "1", "999", "TC"))
            client.put().uri("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(newUser)
                    .exchange()
                    .expectStatus().isBadRequest

        }
    }
}
