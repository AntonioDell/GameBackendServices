package dell.antonio.userpageservice

import org.springframework.context.annotation.*
import org.springframework.web.reactive.function.client.*

@Configuration
@Profile("test")
class WebClientBuilderConfigWithoutLoadBalancer {

    @Bean
    fun webClientBuilder() = WebClient.builder()
}
