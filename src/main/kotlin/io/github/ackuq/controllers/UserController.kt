package io.github.ackuq.controllers

import io.github.ackuq.conf.JwtConfig
import io.github.ackuq.dto.UserCredentials
import io.github.ackuq.services.UserService
import io.ktor.features.*
import org.mindrot.jbcrypt.BCrypt

object UserController {
    fun register(payload: UserCredentials): String {
        val hashedPassword = BCrypt.hashpw(payload.password, BCrypt.gensalt())
        val databasePayload = UserCredentials(payload.email, hashedPassword)
        val user = UserService.createUser(databasePayload)
        return JwtConfig.generateToken(user)
    }

    fun login(payload: UserCredentials): String {
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
}