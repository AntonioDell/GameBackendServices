package com.example.friendinfoservice

import org.bson.types.*
import org.springframework.data.mongodb.repository.*
import org.springframework.stereotype.*
import reactor.core.publisher.*
import java.time.*


@Repository
interface UserFriendsRepository : ReactiveMongoRepository<UserFriends, ObjectId> {

    @Aggregation(
            "{\$match: {_id : ?0}}",
            "{\$addFields: {friends: {\$objectToArray: '\$friends'}}}",
            "{\$addFields: " +
                    "{friends: " +
                    "{\$filter: " +
                    "{" +
                    "input: '\$friends', " +
                    "as: 'friend', " +
                    "cond:{\$gte:['\$\$friend.v.since', ?1]}" +
                    "}" +
                    "}" +
                    "}" +
                    "}",
            "{\$addFields: {friends: {\$arrayToObject: '\$friends'}}}")
    fun findLatestFriendsSince(id: ObjectId, since: LocalDate): Mono<UserFriends>
}
