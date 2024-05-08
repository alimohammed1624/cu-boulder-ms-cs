package io.initialcapacity.emailverifier.registration

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class RegistrationDataGateway(private val db: Database) {
    fun save(email: String): Unit = transaction(db) {
        RegistrationTable.insert {
            it[RegistrationTable.email] = email
        }
    }
}

private object RegistrationTable : LongIdTable() {
    val email = text(name = "email")
    override val tableName = "registrations"
}
