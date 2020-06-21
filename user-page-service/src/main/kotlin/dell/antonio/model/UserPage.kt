package dell.antonio.model

data class UserPage(val id: String?,
                    val userInfo: UserInfo = UserInfo(),
                    val friends: Map<String, FriendRelation> = mapOf())
