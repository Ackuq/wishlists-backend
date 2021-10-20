package io.github.ackuq.conf

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*

fun Application.configureJWT() {
    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
            validate {
                val uuid = it.payload.getClaim("uuid").asString()
                if(uuid != "") {
                    UserIdPrincipal(uuid)
                } else {
                    null
                }
            }
        }
    }

}

