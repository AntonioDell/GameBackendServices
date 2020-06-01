package dell.antonio

import dell.antonio.model.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.autoconfigure.web.reactive.*
import org.springframework.boot.test.context.*
import org.springframework.test.annotation.*
import org.springframework.test.context.junit.jupiter.*
import org.springframework.test.web.reactive.server.*
import java.time.*

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = [UserInfoServiceApplication::class])
@ExtendWith(SpringExtension::class)
@AutoConfigureWebTestClient
class UserControllerTest(@Autowired val client: WebTestClient,
                         @Autowired val repository: UserRepository) {

    val user1Id = 1L
    val user1 = User(
            user1Id,
            "Uncle Bob",
            Address("Teststreet", "1a", "1337", "Testcity"))

    @BeforeEach
    fun setup() {
        repository.deleteAll()
                .thenMany<User>(repository.saveAll(listOf(user1)))
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
            client.get().uri("/users/999")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody<User>()
                    .isEqualTo(User(999))
        }
    }

}
