package dell.antonio

import dell.antonio.model.Address
import dell.antonio.model.User
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/users")
class UserController {

    @GetMapping("/{id}")
    fun getUser(@PathVariable("id") id: UUID) =
            User(id, "Mr. T",
                    Address(
                            "A-Team-Street",
                            "1",
                            "11111",
                            "A-Team-City"))

    @GetMapping("/")
    fun getAllUsers() =
            mapOf("list" to listOf(
                    User(UUID.randomUUID(), "Mr. A", Address(
                            "A-Team-Street",
                            "1",
                            "11111",
                            "A-Team-City")),
                    User(UUID.randomUUID(), "Mr. B", Address(
                            "A-Team-Street",
                            "2",
                            "11111",
                            "A-Team-City")),
                    User(UUID.randomUUID(), "Mr. C", Address(
                            "A-Team-Street",
                            "3",
                            "11111",
                            "A-Team-City"))))

    @PutMapping("/{id}")
    fun createUser(@PathVariable("id") id: UUID) {

    }
}
