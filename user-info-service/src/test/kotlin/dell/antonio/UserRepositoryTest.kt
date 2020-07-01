package dell.antonio

import dell.antonio.model.*
import org.assertj.core.api.Assertions
import org.bson.types.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.autoconfigure.data.mongo.*
import org.springframework.data.domain.*
import org.springframework.test.context.junit.jupiter.*
import java.time.*

@DataMongoTest
@ExtendWith(SpringExtension::class)
class UserRepositoryTest(@Autowired val userRepository: UserRepository) {

    private val user1Id = ObjectId()
    private val user1 = User(user1Id, "Testusername")

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
                .then(userRepository.save(user1))
                .block(Duration.ofSeconds(5))
    }

    @Test
    fun `it finds one by the id`() {
        val user = userRepository.findOne(Example.of(User(user1Id), ExampleMatcher.matchingAny()))
                .block(Duration.ofSeconds(2))

        Assertions.assertThat(user).isEqualTo(user1)
    }

}
