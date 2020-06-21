package com.example.friendinfoservice

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.*

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = [FriendInfoServiceApplication::class])
@AutoConfigureWebTestClient
@DirtiesContext
class FriendInfoServiceApplicationTests {

    @Test
    fun contextLoads() {
    }

}
