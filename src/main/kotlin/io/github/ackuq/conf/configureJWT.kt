package io.github.ackuq.conf

import io.github.ackuq.models.Role
import io.github.ackuq.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import java.util.*

suspend fun handleAuthentication(uuid: String, roles: List<Role>?): Principal? {
    if (uuid == "") {
        return null
    }

    val user = UserService.getUserByUUID(UUID.fromString(uuid)) ?: return null

    if (roles != null) {
        if (user.role !in roles) {
            return null
        }
    }

    return user
}

fun Application.configureJWT() {
    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
            validate {
                val uuid = it.payload.getClaim("uuid").asString()
                handleAuthentication(uuid, null)
            }
        }
        jwt("admin") {
            verifier(JwtConfig.verifier)
            validate {
                val uuid = it.payload.getClaim("uuid").asString()
                handleAuthentication(uuid, listOf(Role.Admin))
            }
        }
    }
}

