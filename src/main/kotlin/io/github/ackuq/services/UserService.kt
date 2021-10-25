package io.github.ackuq.services

import io.github.ackuq.dao.User
import io.github.ackuq.dao.Users
import io.github.ackuq.dto.Role
import io.github.ackuq.dto.UpdateUserDTO
import io.github.ackuq.dto.UserCredentials
import io.ktor.features.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object UserService {
    fun createUser(newUser: UserCredentials): User = transaction {
        if (getUserByEmail(newUser.email) == null) {
            User.new {
                email = newUser.email
                passwordHash = newUser.password
                role = Role.Customer
            }
        } else {
            throw BadRequestException("User with this email already exists")
        }
    }

    fun getAllUsers(): List<User> = transaction {
        User.all().toList()
    }

    fun getUsers(uuids: List<UUID>): List<User> = transaction {
        User.forIds(uuids).toList()
    }

    fun getUserByEmail(email: String): User? = transaction {
        User.find { Users.email eq email }.firstOrNull()
    }

    fun getUserByUUID(uuid: UUID): User? = transaction {
        User.findById(uuid)
    }

    fun updateUser(user: User, userDTO: UpdateUserDTO): User = transaction {
        if (userDTO.email !== null) {
            user.email = userDTO.email
        }
        user
    }

}