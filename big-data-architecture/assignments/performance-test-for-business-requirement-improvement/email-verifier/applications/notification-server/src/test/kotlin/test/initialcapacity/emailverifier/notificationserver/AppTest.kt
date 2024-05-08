package test.initialcapacity.emailverifier.notificationserver

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import io.initialcapacity.emailverifier.fakesendgridendpoints.fakeSendgridRoutes
import io.initialcapacity.emailverifier.notificationserver.DatabaseConfiguration
import io.initialcapacity.emailverifier.notificationserver.start
import io.initialcapacity.emailverifier.rabbitsupport.RabbitExchange
import io.initialcapacity.emailverifier.rabbitsupport.RabbitQueue
import io.initialcapacity.emailverifier.rabbitsupport.buildConnectionFactory
import io.initialcapacity.emailverifier.rabbitsupport.publish
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Before
import test.initialcapacity.emailverifier.testsupport.MockServer
import test.initialcapacity.emailverifier.testsupport.assertJsonEquals
import java.net.URI
import java.net.URL
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

@ExperimentalCoroutinesApi
class AppTest {
    private val sendgridUrl = URL("http://localhost:9021")
    private val rabbitUri = URI("amqp://localhost:5672")

    private val sendgridServer = MockServer(
        port = 9021,
        module = { fakeSendgridRoutes("super-secret") },
    )
    private val dbConfig =
        DatabaseConfiguration("jdbc:postgresql://localhost:5555/notification_test?user=emailverifier&password=emailverifier")

    @Before
    fun setUp() {
        sendgridServer.start()
        transaction(dbConfig.db) {
            exec("delete from notifications")
        }
    }

    @After
    fun tearDown() {
        sendgridServer.stop()
    }

    @Test
    fun testApp() = runBlocking {
        val connectionFactory = buildConnectionFactory(rabbitUri)
        val exchange = RabbitExchange(
            "notification-test-exchange",
            type = "direct",
            routingKeyGenerator = { _: String -> "42" },
            bindingKey = "42",
        )
        val notificationPublisher = publish(connectionFactory, exchange)

        start(
            sendgridUrl = sendgridUrl,
            sendgridApiKey = "super-secret",
            fromAddress = "from@example.com",
            connectionFactory = connectionFactory,
            registrationNotificationExchange = exchange,
            registrationNotificationQueue = RabbitQueue("notification-test-queue"),
            dbConfig = dbConfig
        )

        notificationPublisher("""{"email": "to@example.com", "confirmationCode": "33333333-e89b-12d3-a456-426614174000"}""")

        val expectedCall = """
            {
                "personalizations": [{"to":[{"email": "to@example.com"}]}],
                "from": {"email": "from@example.com"},
                "subject": "Confirmation code",
                "content": [{
                    "type": "text/plain",
                    "value": "Your confirmation code is 33333333-e89b-12d3-a456-426614174000"
                }]
            }"""

        val receivedCall = sendgridServer.waitForCall(2.seconds)

        assertJsonEquals(expectedCall, receivedCall)
    }
}
