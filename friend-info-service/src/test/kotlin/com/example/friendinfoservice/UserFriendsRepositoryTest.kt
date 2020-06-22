package com.example.friendinfoservice

/* TODO: Fix tests
@DataMongoTest
@ExtendWith(SpringExtension::class)
class UserFriendsRepositoryTest(@Autowired val repository: UserFriendsRepository) {
    private val id1 = ObjectId()

    private val today = LocalDate.now()

    private val relation1 = FriendRelation(ObjectId(), today.minusDays(1))
    private val relation2 = FriendRelation(ObjectId(), today.minusDays(2))
    private val relation3 = FriendRelation(ObjectId(), today.minusDays(3))

    @BeforeEach
    fun setUp() {
        repository.deleteAll().block()
        repository.saveAll(Flux.just(UserFriends(id1, mutableMapOf(
                relation1.friendId to relation1,
                relation2.friendId to relation2,
                relation3.friendId to relation3))))
                .then()
                .block()
    }


    @Test
    fun findOneByExampleTest() {
        val userFriends = repository.findOne(Example.of(UserFriends(id1))).block()
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
*/
