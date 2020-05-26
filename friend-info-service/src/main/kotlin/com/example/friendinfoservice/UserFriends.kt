package com.example.friendinfoservice

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document
data class UserFriends(@Id val id: Long, val friends: MutableMap<Long, FriendRelation> = mutableMapOf())

data class FriendRelation(val friendId: Long, var since: LocalDate = LocalDate.now())
