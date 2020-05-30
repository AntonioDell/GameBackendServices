package dell.antonio.model

import java.time.LocalDate
import java.util.*

data class UserFriends(val userId: UUID, val friends: List<FriendRelation> = listOf())

data class FriendRelation(val userId: UUID, val since: LocalDate )
