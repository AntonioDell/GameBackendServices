package dell.antonio.model

import java.util.*

data class UserPage(val id: UUID, val userInfo: UserInfo = UserInfo(), val friendRelations: List<FriendRelation> = listOf())
