package io.github.ackuq.conf

import io.github.ackuq.dto.Role
import io.github.ackuq.services.UserService
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.Principal
import io.ktor.auth.jwt.jwt
import java.util.UUID

fun handleAuthentication(uuid: String, roles: List<Role>?): Principal? {
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
        jwt(SecurityConfigurations.Names.DEFAULT) {
            verifier(JwtConfig.verifier)
            validate {
                val uuid = it.payload.getClaim("uuid").asString()
                handleAuthentication(uuid, null)
            }
        }
        jwt(SecurityConfigurations.Names.ADMIN) {
            verifier(JwtConfig.verifier)
            validate {
                val uuid = it.payload.getClaim("uuid").asString()
                handleAuthentication(uuid, listOf(Role.Admin))
            }
        }
    }
}
