package test.initialcapacity.emailverifier.registration

import io.initialcapacity.emailverifier.registration.RegistrationDataGateway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

class RegistrationDataGatewayTest {
    private val db by lazy {
        Database.connect(
            url = "jdbc:postgresql://localhost:5555/registration_test?user=emailverifier&password=emailverifier"
        )
    }
    private val gateway = RegistrationDataGateway(db)

    @Before
    fun setUp() {
        transaction(db) {
            exec("delete from registrations")
        }
    }

    @Test
    fun testSave() {
        gateway.save("email@example.com")

        val storedEmail = transaction(db) {
            exec("select email from registrations where email = 'email@example.com'") {
                it.next()
                it.getString("email")
            }
        }

        assertEquals("email@example.com", storedEmail)
    }
}
