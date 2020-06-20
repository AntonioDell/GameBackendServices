package dell.antonio

import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.autoconfigure.web.servlet.*
import org.springframework.boot.context.properties.*
import org.springframework.cloud.client.circuitbreaker.*
import org.springframework.context.annotation.*
import org.springframework.web.reactive.config.*
import org.springframework.web.reactive.function.client.*

@SpringBootApplication(exclude = [WebMvcAutoConfiguration::class])
@ConfigurationPropertiesScan
@EnableWebFlux
@EnableCircuitBreaker
class UserPageServiceApplication {

    @Bean
    fun getWebClientBuilder() = WebClient.builder()
}

fun main(args: Array<String>) {
    runApplication<UserPageServiceApplication>(*args)
}
