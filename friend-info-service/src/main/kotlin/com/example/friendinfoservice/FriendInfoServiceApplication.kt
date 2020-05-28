package com.example.friendinfoservice

import com.fasterxml.jackson.datatype.jsr353.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.context.annotation.*
import org.springframework.data.mongodb.repository.config.*
import org.springframework.http.codec.*
import org.springframework.web.reactive.config.*


@SpringBootApplication
@EnableReactiveMongoRepositories
class FriendInfoServiceApplication {
    @Bean
    fun webFluxConfigurer(@Autowired jsonPatchHttpMessageConverter: JsonPatchHttpMessageConverter): WebFluxConfigurer {
        return object : WebFluxConfigurer {
            override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
                configurer.customCodecs().register(jsonPatchHttpMessageConverter)
            }
        }
    }

    @Bean
    fun jsr353Module(): JSR353Module {
        return JSR353Module()
    }
}

fun main(args: Array<String>) {
    runApplication<FriendInfoServiceApplication>(*args)
}
