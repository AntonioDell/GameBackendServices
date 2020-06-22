package dell.antonio.discoveryserver

import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.cloud.netflix.eureka.server.*

@SpringBootApplication
@EnableEurekaServer
class DiscoveryServerApplication

fun main(args: Array<String>) {
    runApplication<DiscoveryServerApplication>(*args)
}
