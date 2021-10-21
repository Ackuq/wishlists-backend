package io.github.ackuq.controllers

import io.github.ackuq.conf.JwtConfig
import io.github.ackuq.models.UpdateUserDTO
import io.github.ackuq.models.User
import io.github.ackuq.models.UserCredentials
import io.github.ackuq.models.UserDTO
import io.github.ackuq.services.UserService
import io.ktor.features.*
import org.mindrot.jbcrypt.BCrypt
import java.util.*

object UserController {

    suspend fun getAllUsers(): List<User> {
        return UserService.getAllUsers()
    }

    suspend fun register(payload: UserCredentials): String {
        val hashedPassword = BCrypt.hashpw(payload.password, BCrypt.gensalt())
        val databasePayload = UserCredentials(payload.email, hashedPassword)
        val user = UserService.createUser(databasePayload)
        return JwtConfig.generateToken(user)
    }

    suspend fun login(payload: UserCredentials): String {
        val user = UserService.getUserByEmail(payload.email)
        when {
            user === null -> {
                throw NotFoundException("User not found")
            }
            !BCrypt.checkpw(payload.password, user.passwordHash) -> {
                throw BadRequestException("Passwords does not match")
            }
            else -> {
                return JwtConfig.generateToken(user)
            }
        }

    }

    suspend fun getUserByUUID(uuid: String): User {
        return UserService.getUserByUUID(UUID.fromString(uuid)) ?: throw NotFoundException("User not found")
    }

    suspend fun getUserByEmail(email: String): User {
        return UserService.getUserByEmail(email) ?: throw NotFoundException("User not found")
    }


    suspend fun updateUser(updateUser: UpdateUserDTO, user: User): User {
        return UserService.updateUser(updateUser, user)
    }
}