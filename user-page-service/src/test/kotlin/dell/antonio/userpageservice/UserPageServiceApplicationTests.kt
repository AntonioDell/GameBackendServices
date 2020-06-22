package dell.antonio.userpageservice

import dell.antonio.*
import org.junit.jupiter.api.*
import org.springframework.boot.test.context.*

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = [UserPageServiceApplication::class])
class UserPageServiceApplicationTests {

    @Test
    fun contextLoads() {
    }

}
