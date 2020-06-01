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


    @GetMapping
    fun getAllUsers() =
            repository.findAll()
                    .collectList()
                    .map { mapOf("list" to it) }
}
