package io.github.ackuq

import io.github.ackuq.models.Users
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object SchemaDefinition {

    fun createSchema() {
        transaction {
            SchemaUtils.create(Users)
        }
    }
}