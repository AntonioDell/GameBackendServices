package dell.antonio.userpageservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient

@SpringBootApplication
class UserPageServiceApplication

@Bean
fun getWebClientBuilder() = WebClient.builder()

fun main(args: Array<String>) {
    runApplication<UserPageServiceApplication>(*args)
}
