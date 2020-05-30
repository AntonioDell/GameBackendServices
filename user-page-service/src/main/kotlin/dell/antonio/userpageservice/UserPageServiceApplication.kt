package dell.antonio.userpageservice

import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.context.annotation.*
import org.springframework.web.reactive.config.*
import org.springframework.web.reactive.function.client.*

@SpringBootApplication
@EnableWebFlux
class UserPageServiceApplication {

    @Bean
    fun getWebClientBuilder() = WebClient.builder()
}

fun main(args: Array<String>) {
    runApplication<UserPageServiceApplication>(*args)
}
