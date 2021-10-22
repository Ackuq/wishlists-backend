package io.github.ackuq

import io.github.ackuq.dao.Users
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object SchemaDefinition {

    fun createSchema() {
        transaction {
            SchemaUtils.create(Users)
        }
    }
}