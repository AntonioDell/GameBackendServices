package dell.antonio

import dell.antonio.model.*
import org.bson.types.*
import org.springframework.beans.factory.annotation.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*
import javax.validation.*

@RestController
@RequestMapping("/users")
class UserController(@Autowired val repository: UserRepository) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable("id") id: ObjectId) =
            repository.findById(id).defaultIfEmpty(User(id))


    @GetMapping
    fun getAllUsers() =
            repository.findAll()
                    .collectList()
                    .map { mapOf("list" to it) }

    @PutMapping
    fun createUser(@Valid @RequestBody newUser: User) =
            repository.save(newUser)
}
