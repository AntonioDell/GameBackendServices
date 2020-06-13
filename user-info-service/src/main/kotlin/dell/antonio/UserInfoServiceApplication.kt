package dell.antonio

import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.data.mongodb.repository.config.*


@SpringBootApplication
@EnableReactiveMongoRepositories
class UserInfoServiceApplication

fun main(args: Array<String>) {
    runApplication<UserInfoServiceApplication>(*args)
}
