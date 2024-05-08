package io.initialcapacity.emailverifier.registrationrequest

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class RegistrationRequestDataGateway(private val db: Database) {
    fun find(email: String): UUID? = transaction(db) {
        RegistrationRequestTable
            .select { RegistrationRequestTable.email eq email }
            .singleOrNull()?.get(RegistrationRequestTable.confirmationCode)
    }

    fun save(email: String, confirmationCode: UUID) = transaction(db) {
        RegistrationRequestTable.insert {
            it[RegistrationRequestTable.email] = email
            it[RegistrationRequestTable.confirmationCode] = confirmationCode
        }
    }
}

private object RegistrationRequestTable : LongIdTable() {
    val email = text(name = "email")
    val confirmationCode = uuid(name = "confirmation_code")
    override val tableName = "registration_requests"
}
