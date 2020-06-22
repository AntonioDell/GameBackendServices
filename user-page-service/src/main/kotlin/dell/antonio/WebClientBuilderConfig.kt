package dell.antonio

import org.springframework.cloud.client.loadbalancer.*
import org.springframework.context.annotation.*
import org.springframework.web.reactive.function.client.*

@Configuration
@Profile("!test")
class WebClientBuilderConfig {
    @Bean
    @LoadBalanced
    fun webClientBuilder() = WebClient.builder()
}
