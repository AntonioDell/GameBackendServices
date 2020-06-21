package dell.antonio.model

import java.time.*

data class UserFriends(val id: String?,
                       val friends: MutableMap<String, FriendRelation> = mutableMapOf())

data class FriendRelation(val friendId: String,
                          var since: LocalDate = LocalDate.now())

