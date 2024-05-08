package test.initialcapacity.emailverifier.notification

import io.initialcapacity.emailverifier.notification.NotificationDataGateway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NotificationDataGatewayTest {
    private val db by lazy {
        Database.connect(
            url = "jdbc:postgresql://localhost:5555/notification_test?user=emailverifier&password=emailverifier"
        )
    }

    private val gateway = NotificationDataGateway(db)

    @Before
    fun setUp() {
        transaction(db) {
            exec("delete from notifications")
        }
    }

    @Test
    fun testSave() {
        val uuid = UUID.fromString("020b82f6-866f-47a6-90d4-359e866da123")

        gateway.save("a@example.com", uuid)

        assertEquals(uuid, gateway.find("a@example.com"))
    }

    @Test
    fun find_notFound() {
        assertNull(gateway.find("notthere@example.com"))
    }
}
