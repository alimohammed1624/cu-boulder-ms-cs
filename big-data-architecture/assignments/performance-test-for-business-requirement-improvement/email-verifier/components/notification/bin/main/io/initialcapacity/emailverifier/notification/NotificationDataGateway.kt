package io.initialcapacity.emailverifier.notification

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class NotificationDataGateway(private val db: Database) {
    fun find(email: String): UUID? = transaction(db) {
        NotificationTable
            .select { NotificationTable.email eq email }
            .singleOrNull()?.get(NotificationTable.confirmationCode)
    }

    fun save(email: String, confirmationCode: UUID) = transaction(db) {
        NotificationTable.insert {
            it[NotificationTable.email] = email
            it[NotificationTable.confirmationCode] = confirmationCode
        }
    }
}

private object NotificationTable : LongIdTable() {
    val email = text(name = "email")
    val confirmationCode = uuid(name = "confirmation_code")
    override val tableName = "notifications"
}
