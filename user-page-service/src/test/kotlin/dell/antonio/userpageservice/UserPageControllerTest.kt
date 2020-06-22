package dell.antonio.userpageservice

import com.fasterxml.jackson.databind.*
import com.github.tomakehurst.wiremock.client.WireMock.*
import dell.antonio.*
import dell.antonio.model.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.autoconfigure.web.reactive.*
import org.springframework.boot.test.context.*
import org.springframework.cloud.contract.wiremock.*
import org.springframework.http.*
import org.springframework.test.context.junit.jupiter.*
import org.springframework.test.web.reactive.server.*
import java.time.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = [UserPageServiceApplication::class])
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8104)
class UserPageControllerTest(@Autowired val client: WebTestClient,
                             @Autowired val objectMapper: ObjectMapper) {


    @AfterEach
    fun afterEach() {
        reset()
    }

    private fun stubResponse(url: String, responseBody: JsonNode?, responseStatus: Int = HttpStatus.OK.value()) {
        stubFor(get(urlPathMatching(url))
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

        client.get().uri("/user-page/$userId")
                .exchange()
                .expectBody<UserPage>()
                .isEqualTo(UserPage(userId, mockUserInfo, mockUserFriends.friends))
    }

    @Nested
    inner class FaultTolerance {

        @Test
        fun `it creates a default user page if all services are faulty`() {
            val userId = "UserId"
            stubResponse("/users/$userId", null, HttpStatus.SERVICE_UNAVAILABLE.value())
            stubResponse("/friends/$userId", null, HttpStatus.SERVICE_UNAVAILABLE.value())

            client.get().uri("/user-page/$userId")
                    .exchange()
                    .expectBody<UserPage>()
                    .isEqualTo(UserPage(userId, UserInfo("No user found")))
        }


        @Test
        fun `it creates a user page with empty friends if friends service is faulty`() {
            val userId = "UserId"
            val userInfo = UserInfo("Test", Address("Test", "Test", "Test", "Test"))
            stubResponse("/users/$userId", objectMapper.convertValue(userInfo, JsonNode::class.java))
            stubResponse("/friends/$userId", null, HttpStatus.SERVICE_UNAVAILABLE.value())

            client.get().uri("/user-page/$userId")
                    .exchange()
                    .expectBody<UserPage>()
                    .isEqualTo(UserPage(userId, userInfo))
        }

        @Test
        fun `it creates a user page without user infos if user info service is faulty`() {
            val userId = "UserId"
            val userFriends = UserFriends(userId, mutableMapOf("AFriendId" to FriendRelation(
                    "AFriendId",
                    LocalDate.now().minusMonths(1))))
            stubResponse("/users/$userId", null, HttpStatus.SERVICE_UNAVAILABLE.value())
            stubResponse("/friends/$userId", objectMapper.convertValue(userFriends, JsonNode::class.java))

            client.get().uri("/user-page/$userId")
                    .exchange()
                    .expectBody<UserPage>()
                    .isEqualTo(UserPage(userId, UserInfo("No user found"), userFriends.friends))

            verify(getRequestedFor(urlPathMatching("/friends/$userId")))

        }
    }


}
