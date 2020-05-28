package com.example.friendinfoservice

import com.fasterxml.jackson.databind.*
import com.github.fge.jsonpatch.*
import org.springframework.beans.factory.annotation.*
import org.springframework.core.*
import org.springframework.core.io.buffer.*
import org.springframework.http.*
import org.springframework.http.codec.*
import org.springframework.stereotype.*
import reactor.core.publisher.*

@Component
class JsonPatchHttpMessageConverter(@Autowired val objectMapper: ObjectMapper)
    : HttpMessageReader<JsonPatch> {

    override fun getReadableMediaTypes(): List<MediaType> {
        return listOf(MediaType.valueOf("application/json-patch+json"))
    }

    override fun canRead(elementType: ResolvableType, mediaType: MediaType?): Boolean {
        return MediaType.valueOf("application/json-patch+json").includes(mediaType)
    }

    override fun read(elementType: ResolvableType, message: ReactiveHttpInputMessage, hints: Map<String, Any>): Flux<JsonPatch> {
        return message.body.map { buffer: DataBuffer ->
            //TODO error handling
            objectMapper.readValue(buffer.asInputStream(), JsonPatch::class.java)
        }
    }

    override fun readMono(elementType: ResolvableType, message: ReactiveHttpInputMessage, hints: Map<String, Any>): Mono<JsonPatch> {
        return DataBufferUtils.join(message.body).map { buffer: DataBuffer ->
            //TODO error handling
            objectMapper.readValue(buffer.asInputStream(), JsonPatch::class.java)
        }
    }
}
