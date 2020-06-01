package dell.antonio

import dell.antonio.model.*
import org.springframework.data.mongodb.repository.*
import org.springframework.stereotype.*

@Repository
interface UserRepository: ReactiveMongoRepository<User, Long> {

}
