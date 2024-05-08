package test.initialcapacity.emailverifier.notification

import io.initialcapacity.emailverifier.notification.Emailer
import io.initialcapacity.emailverifier.notification.NotificationDataGateway
import io.initialcapacity.emailverifier.notification.Notifier
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class NotifierTest {
    private val db by lazy {
        Database.connect(
            url = "jdbc:postgresql://localhost:5555/notification_test?user=emailverifier&password=emailverifier"
        )
    }

    @Before
    fun setUp() {
        transaction(db) {
            exec("delete from notifications")
        }
    }

    @Test
    fun testNotify() = runBlocking {
        val uuid = UUID.fromString("aaaaaaaa-866f-47a6-90d4-359e866da123")
        val gateway = NotificationDataGateway(db)
        val emailer = mockk<Emailer>()
        coEvery { emailer.send(any(), any(), any()) } returns true
        val notifier = Notifier(gateway, emailer)


        notifier.notify("a@example.com", uuid)

        assertEquals(uuid, gateway.find("a@example.com"))
        coVerify { emailer.send("a@example.com", "Confirmation code", "Your confirmation code is $uuid") }
    }
}
