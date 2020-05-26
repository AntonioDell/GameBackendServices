package com.example.friendinfoservice

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.core.MethodParameter
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import javax.json.JsonPatch

class JsonPatchArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == JsonPatch::class
    }

    override fun resolveArgument(parameter: MethodParameter, bindingContext: BindingContext, exchange: ServerWebExchange): Mono<Any> {
        return exchange.request.body.toMono().map {
            jacksonObjectMapper().readValue(it.asInputStream(), JsonPatch::class.java)
        }
    }

}
