package com.example.friendinfoservice

import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.data.mongodb.repository.config.*


@SpringBootApplication
@EnableReactiveMongoRepositories
class FriendInfoServiceApplication

fun main(args: Array<String>) {
    runApplication<FriendInfoServiceApplication>(*args)
}
