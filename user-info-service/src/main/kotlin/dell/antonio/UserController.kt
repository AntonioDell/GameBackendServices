package dell.antonio

import dell.antonio.model.*
import org.springframework.beans.factory.annotation.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(@Autowired val repository: UserRepository) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable("id") id: Long) =
            repository.findById(id).defaultIfEmpty(User(id))


    @GetMapping("/")
    fun getAllUsers() =
            mapOf("list" to listOf(
                    User(1, "Mr. A", Address(
                            "A-Team-Street",
                            "1",
                            "11111",
                            "A-Team-City")),
                    User(2, "Mr. B", Address(
                            "A-Team-Street",
                            "2",
                            "11111",
                            "A-Team-City")),
                    User(3, "Mr. C", Address(
                            "A-Team-Street",
                            "3",
                            "11111",
                            "A-Team-City"))))
}
