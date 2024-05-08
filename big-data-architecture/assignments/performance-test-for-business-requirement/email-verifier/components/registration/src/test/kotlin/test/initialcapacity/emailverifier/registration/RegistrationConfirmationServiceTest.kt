package test.initialcapacity.emailverifier.registration

import io.initialcapacity.emailverifier.registration.RegistrationConfirmationService
import io.initialcapacity.emailverifier.registration.RegistrationDataGateway
import io.initialcapacity.emailverifier.registrationrequest.RegistrationRequestDataGateway
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import java.util.*
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RegistrationConfirmationServiceTest {
    private val uuid = UUID.fromString("55555555-1d21-442e-8fc0-a2259ec09190")

    private val requestGateway = mockk<RegistrationRequestDataGateway>()
    private val registrationGateway = mockk<RegistrationDataGateway>()
    private val service = RegistrationConfirmationService(requestGateway, registrationGateway)

    @Before
    fun setUp() {
        every { requestGateway.find("there@example.com") } returns uuid
        every { requestGateway.find("not-there@example.com") } returns null
        every { registrationGateway.save(any()) } returns Unit
    }

    @Test
    fun testConfirm() {
        assertTrue(service.confirm("there@example.com", uuid))

        verify { registrationGateway.save("there@example.com") }
    }

    @Test
    fun testConfirmNotThere() {
        assertFalse(service.confirm("not-there@example.com", uuid))

        verify(exactly = 0) { registrationGateway.save("there@example.com") }
    }

    @Test
    fun testConfirmNoMatch() {
        assertFalse(service.confirm("there@example.com", UUID.fromString("eeeeeeee-1d21-442e-8fc0-a2259ec09190")))

        verify(exactly = 0) { registrationGateway.save("there@example.com") }
    }
}
