package com.example.friendinfoservice

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.domain.Example
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Flux
import java.time.LocalDate


@DataMongoTest
@ExtendWith(SpringExtension::class)
class UserFriendsRepositoryTest(@Autowired val repository: UserFriendsRepository) {
    private val id1 = 1L

    private val today = LocalDate.now()

    private val relation1 = FriendRelation(2, today.minusDays(1))
    private val relation2 = FriendRelation(3, today.minusDays(2))
    private val relation3 = FriendRelation(4, today.minusDays(3))

    @BeforeEach
    fun setUp() {

        repository.deleteAll().block()
        repository
                .saveAll(Flux.just(UserFriends(id1, mutableMapOf(
                        relation1.friendId to relation1,
                        relation2.friendId to relation2,
                        relation3.friendId to relation3))))
                .then()
                .block()
    }


    @Test
    fun findOneByExampleTest() {
        val userFriends = repository.findOne(Example.of(UserFriends(id1)))
        assertThat(userFriends).isNotNull
    }

    @Test
    fun findLatestFriendsSinceTest() {
        val userFriends = repository.findLatestFriendsSince(id1, today.minusDays(2)).block()
        assertThat(userFriends?.friends).containsExactlyEntriesOf(mapOf(
                relation1.friendId to relation1,
                relation2.friendId to relation2))
    }
}
