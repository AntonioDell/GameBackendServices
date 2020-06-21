package dell.antonio

import io.github.resilience4j.circuitbreaker.*
import io.github.resilience4j.timelimiter.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.autoconfigure.web.servlet.*
import org.springframework.boot.context.properties.*
import org.springframework.cloud.circuitbreaker.resilience4j.*
import org.springframework.cloud.client.circuitbreaker.*
import org.springframework.cloud.client.loadbalancer.*
import org.springframework.context.annotation.*
import org.springframework.web.reactive.config.*
import org.springframework.web.reactive.function.client.*
import java.time.*

@SpringBootApplication(exclude = [WebMvcAutoConfiguration::class])
@ConfigurationPropertiesScan
@EnableWebFlux
class UserPageServiceApplication {

    @Bean
    fun defaultCustomizer(): Customizer<ReactiveResilience4JCircuitBreakerFactory> {
        return Customizer { factory: ReactiveResilience4JCircuitBreakerFactory ->
            factory.configureDefault { id: String? ->
                Resilience4JConfigBuilder(id)
                        .timeLimiterConfig(TimeLimiterConfig.custom()
                                .timeoutDuration(Duration.ofSeconds(1))
                                .build())
                        .circuitBreakerConfig(CircuitBreakerConfig.custom()
                                .slidingWindowSize(10)
                                .failureRateThreshold(33.3F)
                                .slowCallRateThreshold(33.3F)
                                .build())
                        .build()
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<UserPageServiceApplication>(*args)
}
