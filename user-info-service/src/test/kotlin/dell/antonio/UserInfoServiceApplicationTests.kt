package dell.antonio

import org.junit.jupiter.api.*
import org.springframework.boot.test.autoconfigure.web.reactive.*
import org.springframework.boot.test.context.*

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = [UserInfoServiceApplication::class])
@AutoConfigureWebTestClient
class UserInfoServiceApplicationTests {

    @Test
    fun contextLoads() {
    }

}
