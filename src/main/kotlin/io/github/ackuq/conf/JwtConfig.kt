package io.github.ackuq.conf

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import io.github.ackuq.models.User
import io.ktor.config.*
import java.util.*

object JwtConfig {
    private val appConfig = HoconApplicationConfig(ConfigFactory.load())
    private val secret = appConfig.property("jwt.secret").getString()
    private val issuer = appConfig.property("jwt.issuer").getString()
    private val algorithm = Algorithm.HMAC256(secret)

    private const val validityInMs = 36_000_00 * 10 // 10 hours

    val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

    fun generateToken(user: User): String =
        JWT.create()
            .withIssuer(issuer)
            .withClaim("uuid", user.uuid.toString())
            .withExpiresAt(getExpiration())
            .sign(algorithm)

}