package dell.antonio.userpageservice

import com.fasterxml.jackson.databind.*
import com.github.tomakehurst.wiremock.*
import com.github.tomakehurst.wiremock.client.WireMock.*
import dell.antonio.*
import dell.antonio.model.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.autoconfigure.web.reactive.*
import org.springframework.boot.test.context.*
import org.springframework.http.*
import org.springframework.test.context.*
import org.springframework.test.web.reactive.server.*
import java.time.*

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = [UserPageServiceApplication::class])
@ContextConfiguration(initializers = [WireMockContextInitializer::class])
@AutoConfigureWebTestClient
class UserPageControllerTest(@Autowired val webTestClient: WebTestClient,
                             @Autowired val objectMapper: ObjectMapper) {
    @Autowired
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private lateinit var wireMockServer: WireMockServer

    @Value("\${game-backend-uri.users}")
    lateinit var usersUrl: String

    @Value("\${game-backend-uri.friends}")
    lateinit var friendsUrl: String

    @AfterEach
    fun afterEach() {
        wireMockServer.resetAll()
    }

    private fun stubResponse(url: String, responseBody: JsonNode?, responseStatus: Int = HttpStatus.OK.value()) {
        wireMockServer.stubFor(get(url)
                .willReturn(
                        aResponse()
                                .withStatus(responseStatus)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withJsonBody(responseBody))
        )
    }

    @Test
    fun `it creates a user page from the responses of users and friends services`() {
        val mockUserInfo = UserInfo(
                "Mr. Test",
                Address("Teststreet",
                        "1a",
                        "12345",
                        "Testcity"))
        val friendId = "FriendId"
        val userId = "UserId"
        val mockUserFriends = UserFriends(userId, mutableMapOf(
                friendId to FriendRelation(
                        friendId,
                        LocalDate.now().minusMonths(1))))

        stubResponse("/users/$userId", objectMapper.convertValue(mockUserInfo, JsonNode::class.java))
        stubResponse("/friends/$userId", objectMapper.convertValue(mockUserFriends, JsonNode::class.java))

        webTestClient.get().uri("/user-page/$userId")
                .exchange()
                .expectBody<UserPage>()
                .isEqualTo(UserPage(userId, mockUserInfo, mockUserFriends.friends))
    }

    @Test
    fun `it creates a default user page if the users and friends services are unavailable`() {
        val userId = "UserId"
        stubResponse("/users/$userId", null, HttpStatus.SERVICE_UNAVAILABLE.value())
        stubResponse("/friends/$userId", null, HttpStatus.SERVICE_UNAVAILABLE.value())

        webTestClient.get().uri("/user-page/$userId")
                .exchange()
                .expectBody<UserPage>()
                .isEqualTo(UserPage(userId, UserInfo("No user found")))
    }


    @Test
    fun `it creates a user page with empty friends if the friends service is unavailable`() {
        val userId = "UserId"
        val userInfo = UserInfo("Test", Address("Test", "Test", "Test", "Test"))
        stubResponse("/users/$userId", objectMapper.convertValue(userInfo, JsonNode::class.java))
        stubResponse("/friends/$userId", null, HttpStatus.SERVICE_UNAVAILABLE.value())

        webTestClient.get().uri("/user-page/$userId")
                .exchange()
                .expectBody<UserPage>()
                .isEqualTo(UserPage(userId, userInfo))
    }

    @Test
    fun `it creates a user page with empty user infos but friends if only user info service is unavailable`() {
        val userId = "UserId"
        val userFriends = UserFriends(userId, mutableMapOf("friendId" to FriendRelation(
                "friendId",
                LocalDate.now().minusMonths(1))))
        stubResponse("$usersUrl/users/$userId", null, HttpStatus.SERVICE_UNAVAILABLE.value())
        stubResponse("$friendsUrl/friends/$userId", objectMapper.convertValue(userFriends, JsonNode::class.java))

        webTestClient.get().uri("/user-page/$userId")
                .exchange()
                .expectBody<UserPage>()
                .isEqualTo(UserPage(userId, UserInfo("No user found"), userFriends.friends))
    }

}
