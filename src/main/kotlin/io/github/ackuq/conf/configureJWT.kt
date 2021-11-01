package io.github.ackuq.conf

import io.bkbn.kompendium.auth.KompendiumAuth.notarizedJwt
import io.github.ackuq.dto.Role
import io.github.ackuq.services.UserService
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.Principal
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
        notarizedJwt {
            verifier(JwtConfig.verifier)
            validate {
                val uuid = it.payload.getClaim("uuid").asString()
                handleAuthentication(uuid, null)
            }
        }
        notarizedJwt("admin") {
            verifier(JwtConfig.verifier)
            validate {
                val uuid = it.payload.getClaim("uuid").asString()
                handleAuthentication(uuid, listOf(Role.Admin))
            }
        }
    }
}
