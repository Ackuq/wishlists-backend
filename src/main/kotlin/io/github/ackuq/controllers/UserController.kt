package io.github.ackuq.controllers

import io.github.ackuq.conf.JwtConfig
import io.github.ackuq.models.User
import io.github.ackuq.models.UserPayload
import io.github.ackuq.services.UserService
import io.ktor.features.*
import java.util.*
import org.mindrot.jbcrypt.BCrypt

object UserController {

    suspend fun getAllUsers(): List<User> {
        return UserService.getAllUsers()
    }

    suspend fun register(payload: UserPayload): String {
        val hashedPassword = BCrypt.hashpw(payload.password, BCrypt.gensalt())
        val databasePayload = UserPayload(payload.email, hashedPassword)
        val uuid = UserService.createUser(databasePayload)
        return JwtConfig.generateToken(uuid.toString())
    }

    suspend fun login(payload: UserPayload): String {
        val user = UserService.getUserByEmail(payload.email)
        when {
            user === null -> {
                throw NotFoundException("User not found")
            }
            !BCrypt.checkpw(payload.password, user.passwordHash) -> {
                throw BadRequestException("Passwords does not match")
            }
            else -> {
                return JwtConfig.generateToken(user.uuid)
            }
        }

    }

    suspend fun getUserByUUID(uuid: String): User {
        return UserService.getUserByUUID(UUID.fromString(uuid)) ?: throw NotFoundException("User not found")
    }

    suspend fun getUserByEmail(email: String): User {
        return UserService.getUserByEmail(email) ?: throw NotFoundException("User not found")
    }

}