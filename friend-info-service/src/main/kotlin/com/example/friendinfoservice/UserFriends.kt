package com.example.friendinfoservice

import com.fasterxml.jackson.databind.annotation.*
import com.fasterxml.jackson.databind.ser.std.*
import org.bson.types.*
import org.springframework.data.annotation.*
import org.springframework.data.mongodb.core.mapping.*
import java.time.*

@Document
data class UserFriends(@JsonSerialize(using = ToStringSerializer::class) @Id val id: ObjectId,
                       val friends: MutableMap<ObjectId, FriendRelation> = mutableMapOf())

data class FriendRelation(@JsonSerialize(using = ToStringSerializer::class) val friendId: ObjectId,
                          var since: LocalDate = LocalDate.now())
