package test.initialcapacity.emailverifier.registrationserver

import io.initialcapacity.emailverifier.rabbitsupport.RabbitExchange
import io.initialcapacity.emailverifier.rabbitsupport.RabbitQueue
import io.initialcapacity.emailverifier.rabbitsupport.buildConnectionFactory
import io.initialcapacity.emailverifier.rabbitsupport.declareAndBind
import io.initialcapacity.emailverifier.registration.RegistrationDataGateway
import io.initialcapacity.emailverifier.registrationrequest.RegistrationRequestDataGateway
import io.initialcapacity.emailverifier.registrationserver.listenForRegistrationRequests
import io.initialcapacity.emailverifier.registrationserver.registrationServer
import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Before
import test.initialcapacity.emailverifier.testsupport.assertMessageReceived
import java.net.URI
import java.util.*
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class RegistrationRequestTest {
    private val db by lazy {
        Database.connect(
            url = "jdbc:postgresql://localhost:5555/registration_test?user=emailverifier&password=emailverifier"
        )
    }

    private val connectionFactory = buildConnectionFactory(URI("amqp://localhost:5672"))
    private val notificationExchange = RabbitExchange(
        name = "test-notification-exchange",
        type = "direct",
        routingKeyGenerator = { _: String -> "42" },
        bindingKey = "42",
    )
    private val notificationQueue = RabbitQueue("test-notification-queue")
    private val requestExchange = RabbitExchange(
        name = "test-request-exchange",
        type = "direct",
        routingKeyGenerator = { _: String -> "42" },
        bindingKey = "42",
    )
    private val requestQueue = RabbitQueue("test-request-queue")

    private val requestGateway = RegistrationRequestDataGateway(db)
    private val registrationGateway = RegistrationDataGateway(db)

    private val regServer = registrationServer(
        port = 9120,
        connectionFactory = connectionFactory,
        registrationRequestExchange = requestExchange,
        registrationRequestGateway = requestGateway,
        registrationGateway = registrationGateway,
    )

    private val client = HttpClient(Java)

    @Before
    fun setUp() {
        transaction(db) {
            exec("delete from registration_requests")
            exec("delete from registrations")
        }

        connectionFactory.declareAndBind(notificationExchange, notificationQueue)
        connectionFactory.declareAndBind(requestExchange, requestQueue)
        regServer.start(wait = false)
    }

    @After
    fun tearDown() {
        regServer.stop(50, 50)
    }

    @Test
    fun testRegistration(): Unit = runBlocking {
        listenForRegistrationRequests(
            registrationRequestDataGateway = requestGateway,
            connectionFactory = connectionFactory,
            registrationNotificationExchange = notificationExchange,
            registrationRequestQueue = requestQueue,
            uuidProvider = { UUID.fromString("cccccccc-1d21-442e-8fc0-a2259ec09190") }
        )

        val status = client.post("http://localhost:9120/request-registration") {
            headers {
                contentType(ContentType.Application.Json)
                setBody("""{"email": "user@example.com"}""")
            }
        }.status

        assertTrue(status.isSuccess())

        val expectedMessage = """
            {
              "email" : "user@example.com",
              "confirmationCode" : "cccccccc-1d21-442e-8fc0-a2259ec09190"
            }
        """.trimIndent()

        connectionFactory.assertMessageReceived(
            queue = notificationQueue,
            message = expectedMessage,
            timeout = 500.milliseconds
        )
    }
}
