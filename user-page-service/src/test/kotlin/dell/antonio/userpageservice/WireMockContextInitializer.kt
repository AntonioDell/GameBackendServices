package dell.antonio.userpageservice


import com.github.tomakehurst.wiremock.*
import com.github.tomakehurst.wiremock.core.*
import org.springframework.boot.test.util.*
import org.springframework.context.*
import org.springframework.context.event.*

class WireMockContextInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {

        val wmServer = WireMockServer(WireMockConfiguration().dynamicPort())
        wmServer.start()

        applicationContext.beanFactory.registerSingleton("wireMock", wmServer)

        applicationContext.addApplicationListener {
            if (it is ContextClosedEvent) {
                wmServer.stop()
            }
        }

        TestPropertyValues
                .of("game-backend-uri.users=http://localhost:${wmServer.port()}/users",
                        "game-backend-uri.friends=http://localhost:${wmServer.port()}/friends")
                .applyTo(applicationContext)
    }
}
