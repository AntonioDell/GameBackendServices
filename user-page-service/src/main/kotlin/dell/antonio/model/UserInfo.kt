package dell.antonio.model

data class UserInfo(val userName: String = "", val address: Address = Address())

data class Address(val street: String = "", val houseNumber: String = "", val cityCode: String = "", val cityName: String = "")
