package com.example.friendinfoservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@SpringBootApplication
@EnableReactiveMongoRepositories
@EnableWebFlux
@Configuration
class FriendInfoServiceApplication : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(JsonPatchArgumentResolver())
    }
}

fun main(args: Array<String>) {
    runApplication<FriendInfoServiceApplication>(*args)
}
