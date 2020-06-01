package dell.antonio.model

import org.springframework.data.annotation.*
import org.springframework.data.mongodb.core.mapping.*
import java.util.*

@Document
data class User(
        @Id val id: Long,
        val userName: String = "",
        val address: Address = Address())

data class Address(
        val street: String = "",
        val houseNumber: String = "",
        val cityCode: String = "",
        val cityName: String = "")
