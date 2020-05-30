package dell.antonio.userpageservice

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		classes = [UserPageServiceApplication::class])
@AutoConfigureWebTestClient
class UserPageServiceApplicationTests {

	@Test
	fun contextLoads() {
	}

}
