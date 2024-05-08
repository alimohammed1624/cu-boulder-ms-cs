package test.initialcapacity.emailverifier.registrationserver

import io.initialcapacity.emailverifier.rabbitsupport.RabbitExchange
import io.initialcapacity.emailverifier.rabbitsupport.buildConnectionFactory
import io.initialcapacity.emailverifier.registration.RegistrationDataGateway
import io.initialcapacity.emailverifier.registrationrequest.RegistrationRequestDataGateway
import io.initialcapacity.emailverifier.registrationserver.module
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.Database
import java.net.URI

fun testApp(block: suspend ApplicationTestBuilder.() -> Unit) {
    val db by lazy {
        Database.connect(
            url = "jdbc:postgresql://localhost:5555/registration_test?user=emailverifier&password=emailverifier"
        )
    }

    val connectionFactory = buildConnectionFactory(URI("amqp://localhost:5672"))

    val requestExchange = RabbitExchange(
        name = "test-request-exchange",
        type = "direct",
        routingKeyGenerator = { _: String -> "42" },
        bindingKey = "42"
    )

    testApplication {
        application {
            module(RegistrationRequestDataGateway(db), RegistrationDataGateway(db), connectionFactory, requestExchange)
        }
        block()
    }
}
