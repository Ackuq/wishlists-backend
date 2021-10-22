package io.github.ackuq.dao

import io.github.ackuq.dto.Role
import io.github.ackuq.dto.UserDTO
import io.ktor.auth.*
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object Users : UUIDTable(name = "users", columnName = "uuid") {
    val email: Column<String> = varchar("email", 255).uniqueIndex()
    val firstName: Column<String?> = varchar("first_name", 100).nullable()
    val lastName: Column<String?> = varchar("last_name", 100).nullable()
    val passwordHash: Column<String> = varchar("password_hash", 60)
    val role: Column<Role> = enumeration("role", Role::class)
}

class User(uuid: EntityID<UUID>) : UUIDEntity(uuid), Principal {
    companion object : UUIDEntityClass<User>(Users)

    var email by Users.email
    var passwordHash by Users.passwordHash
    var role by Users.role
    var wishLists by WishList via UsersWishLists
    val ownedWishLists by WishList referrersOn WishLists.owner

    fun toDTO() = transaction {
        UserDTO(
            this@User.id.value.toString(),
            this@User.email,
            this@User.role,
            this@User.ownedWishLists.map { it.id.value },
            this@User.wishLists.map { it.id.value }
        )
    }
}

