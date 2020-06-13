package dell.antonio.model

import com.fasterxml.jackson.databind.annotation.*
import com.fasterxml.jackson.databind.ser.std.*
import org.bson.types.*
import org.springframework.data.annotation.*
import org.springframework.data.mongodb.core.mapping.*
import javax.validation.constraints.*

@Document
data class User(
        @JsonSerialize(using = ToStringSerializer::class) @Id val id: ObjectId? = null,
        @get:NotBlank(message = "Username is mandatory.") val userName: String = "",
        val address: Address = Address())

data class Address(
        val street: String = "",
        val houseNumber: String = "",
        val cityCode: String = "",
        val cityName: String = "")
