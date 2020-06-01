package dell.antonio

import dell.antonio.model.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.autoconfigure.data.mongo.*
import org.springframework.test.context.junit.jupiter.*


@DataMongoTest
@ExtendWith(SpringExtension::class)
class UserRepositoryTest(@Autowired val repository: UserRepository) {

    val userId1 = 1L
    val user1 = User(
            userId1,
            "Test User 1",
            Address("Teststreet", "1a", "99999", "Testcity"))

    @BeforeEach
    fun setup() {
        repository.deleteAll().then(repository.save(user1)).block()
    }

    @Test
    fun `it retrieves a saved instance by id`() {
        val foundUser = repository.findById(userId1)
        assertThat(foundUser).isEqualTo(user1)
    }
}
