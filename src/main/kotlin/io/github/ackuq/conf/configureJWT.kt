package io.github.ackuq.conf

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*

fun Application.configureJWT() {
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()

    install(Authentication) {
        jwt {
            verifier(JWT.require(Algorithm.HMAC256(secret)).withIssuer(issuer).build())
            validate { jwtCredential ->
                if(jwtCredential.payload.getClaim("email").asString() != "") {
                    UserIdPrincipal(jwtCredential.payload.getClaim("email").asString())
                } else {
                    null
                }
            }
        }
    }
}
