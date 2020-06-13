package dell.antonio

import dell.antonio.model.*
import org.bson.types.*
import org.springframework.data.mongodb.repository.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*

@Repository
interface UserRepository: ReactiveMongoRepository<User, ObjectId> {

}
