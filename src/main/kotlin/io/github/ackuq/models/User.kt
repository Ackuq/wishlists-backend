package io.github.ackuq.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.util.*

object Users: Table() {
    val uuid: Column<UUID> = uuid("uuid").autoGenerate().primaryKey()
    val email: Column<String> = varchar("email", 100).uniqueIndex()
    val passwordHash: Column<String> = varchar("password_hash", 60)
}

@Serializable
data class User(val uuid: String, val email: String, val passwordHash: String)

@Serializable
data class UserPayload(val email: String, val password: String)