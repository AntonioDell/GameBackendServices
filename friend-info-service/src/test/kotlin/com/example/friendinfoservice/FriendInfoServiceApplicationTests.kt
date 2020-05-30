package com.example.friendinfoservice

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = [FriendInfoServiceApplication::class])
@AutoConfigureWebTestClient
class FriendInfoServiceApplicationTests {

    @Test
    fun contextLoads() {
    }

}
