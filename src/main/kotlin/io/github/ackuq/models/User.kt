package io.github.ackuq.models

import io.ktor.auth.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.util.*

object Users : Table("users") {
    val uuid: Column<UUID> = uuid("uuid").autoGenerate().primaryKey()
    val email: Column<String> = varchar("email", 100).uniqueIndex()
    val passwordHash: Column<String> = varchar("password_hash", 60)
    val role: Column<Role> = enumeration("role", Role::class)
}

data class User(val uuid: UUID, val email: String, val passwordHash: String, val role: Role) : Principal

@Serializable
data class UserCredentials(val email: String, val password: String)

@Serializable
data class UserDTO(val uuid: String, val email: String, val role: Role)

@Serializable
data class UpdateUserDTO(val email: String? = null)

fun User.toUserDTO() = UserDTO(uuid.toString(), email, role)

@Serializable
enum class Role {
    Admin,
    Customer
}