package test.initialcapacity.emailverifier.confirmation

import io.initialcapacity.emailverifier.registrationrequest.RegistrationRequestDataGateway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RegistrationRequestDataGatewayTest {
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
    fun testGet() {
        val uuid = UUID.fromString("11111111-e89b-12d3-a456-426614174000")

        gateway.save("email@example.com", uuid)

        assertEquals(uuid, gateway.find("email@example.com"))
        assertEquals(null, gateway.find("not_there@example.com"))
    }
}
