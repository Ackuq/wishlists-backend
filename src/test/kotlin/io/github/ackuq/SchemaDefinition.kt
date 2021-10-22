package io.github.ackuq

import io.github.ackuq.dao.Users
import io.github.ackuq.dao.UsersWishLists
import io.github.ackuq.dao.WishLists
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object SchemaDefinition {

    fun createSchema() {
        transaction {
            SchemaUtils.create(Users, WishLists, UsersWishLists)
        }
    }
}