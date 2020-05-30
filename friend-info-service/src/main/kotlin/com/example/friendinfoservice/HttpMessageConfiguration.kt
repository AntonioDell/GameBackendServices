package com.example.friendinfoservice

import com.fasterxml.jackson.datatype.jsr353.*
import org.springframework.beans.factory.annotation.*
import org.springframework.context.annotation.*
import org.springframework.http.codec.*
import org.springframework.web.reactive.config.*

@Configuration
class HttpMessageConfiguration(
        @Autowired val jsonPatchHttpMessageConverter: JsonPatchHttpMessageConverter)
    : WebFluxConfigurer {

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        configurer.customCodecs().register(jsonPatchHttpMessageConverter)
    }

    @Bean
    fun jsr353Module(): JSR353Module {
        return JSR353Module()
    }
}
