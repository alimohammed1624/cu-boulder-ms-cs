package test.initialcapacity.emailverifier.confirmation

import io.initialcapacity.emailverifier.registrationrequest.RegistrationRequestService
import io.initialcapacity.emailverifier.registrationrequest.RegistrationRequestDataGateway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import test.initialcapacity.emailverifier.testsupport.assertJsonEquals
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RegistrationRegisterServiceTest {
    private val db by lazy {
        Database.connect(
            url = "jdbc:postgresql://localhost:5555/registration_test?user=emailverifier&password=emailverifier"
        )
    }
    private val gateway = RegistrationRequestDataGateway(db)

    @Before
    fun setUp() {
        transaction(db) {
            exec("delete from registration_requests")
        }
    }

    @Test
    fun testGenerateCodeAndPublish() {
        val uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
        var publishedMessage: String? = null

        val publish = { message: String -> publishedMessage = message }
        val uuidProvider = {  -> uuid }

        val expectedMessage = """
            {
              "email":"test@example.com",
              "confirmationCode":"123e4567-e89b-12d3-a456-426614174000"
            }
            """.trimIndent()

        val service = RegistrationRequestService(
            gateway,
            publish,
            uuidProvider,
        )

        service.generateCodeAndPublish("test@example.com")

        assertEquals(uuid, gateway.find("test@example.com"))
        assertJsonEquals(expectedMessage, publishedMessage)
    }
}
