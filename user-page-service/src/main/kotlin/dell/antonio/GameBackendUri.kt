package dell.antonio

import org.springframework.boot.context.properties.*
import java.net.*

@ConstructorBinding
@ConfigurationProperties(prefix = "game-backend-uri")
class GameBackendUri(val users: URI, val friends: URI)
