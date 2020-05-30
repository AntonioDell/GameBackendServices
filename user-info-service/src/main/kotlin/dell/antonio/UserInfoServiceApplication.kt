package dell.antonio

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UserInfoServiceApplication

fun main(args: Array<String>) {
    runApplication<UserInfoServiceApplication>(*args)
}
