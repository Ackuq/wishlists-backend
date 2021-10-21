package io.github.ackuq.conf

import io.github.ackuq.models.Role
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*

fun handleAuthentication(uuid: String, roleOrdinal: Int?, roles: List<Int>?): Principal? {
    if (uuid == "") {
        return null
    }

    if (roles != null) {
        if (roleOrdinal == null || roleOrdinal !in roles) {
            return null
        }
    }

    return UserIdPrincipal(uuid)
}

fun Application.configureJWT() {
    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
            validate {
                val uuid = it.payload.getClaim("uuid").asString()
                handleAuthentication(uuid, null, null)
            }
        }
        jwt("admin") {
            verifier(JwtConfig.verifier)
            validate {
                val uuid = it.payload.getClaim("uuid").asString()
                val roleOrdinal = it.payload.getClaim("role").asInt()
                handleAuthentication(uuid, roleOrdinal, listOf(Role.Admin.ordinal))
            }
        }
    }
}

