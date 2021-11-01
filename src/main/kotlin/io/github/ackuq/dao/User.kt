package io.github.ackuq.dao

import io.github.ackuq.dto.Role
import io.github.ackuq.dto.UserDTO
import io.ktor.auth.Principal
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object Users : UUIDTable(name = "users", columnName = "uuid") {
    val email: Column<String> = varchar("email", 255)
    val firstName: Column<String?> = varchar("first_name", 100).nullable()
    val lastName: Column<String?> = varchar("last_name", 100).nullable()
    val passwordHash: Column<String> = varchar("password_hash", 60)
    val role: Column<Role> = enumeration("role", Role::class).default(Role.Customer)
}

class User(uuid: EntityID<UUID>) : UUIDEntity(uuid), Principal {
    companion object : UUIDEntityClass<User>(Users)

    var email by Users.email
    val firstName by Users.firstName
    val lastName by Users.lastName
    var passwordHash by Users.passwordHash
    var role by Users.role
    var wishLists by WishList via UsersWishLists
    val ownedWishLists by WishList referrersOn WishLists.owner

    fun toDTO() = transaction {
        UserDTO(
            uuid = this@User.id.value.toString(),
            firstName = this@User.firstName,
            lastName = this@User.lastName,
            email = this@User.email,
            role = this@User.role,
            ownedWishLists = this@User.ownedWishLists.map { it.id.value },
            memberWishLists = this@User.wishLists.map { it.id.value }
        )
    }
}
