package com.example.friendinfoservice

import org.junit.jupiter.api.*
import org.springframework.boot.test.autoconfigure.web.reactive.*
import org.springframework.boot.test.context.*

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = [FriendInfoServiceApplication::class])
@AutoConfigureWebTestClient
//@DirtiesContext
class FriendInfoServiceApplicationTests {

    @Test
    fun contextLoads() {
    }

}
