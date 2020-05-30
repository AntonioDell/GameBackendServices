package dell.antonio.model

import java.util.*

data class User(
        val id: UUID,
        val userName: String,
        val address: Address)

data class Address(
        val street: String,
        val houseNumber: String,
        val cityCode: String,
        val cityName: String)
