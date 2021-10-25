package io.github.ackuq.conf

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import io.github.ackuq.dao.User
import io.github.ackuq.dto.UserCredentials
import io.github.ackuq.services.UserService
import io.ktor.config.*
import io.ktor.features.*
import org.mindrot.jbcrypt.BCrypt
import java.util.*

object JwtConfig {
    private val appConfig = HoconApplicationConfig(ConfigFactory.load())
    private val secret = appConfig.property("jwt.secret").getString()
    private val issuer = appConfig.property("jwt.issuer").getString()
    private val algorithm = Algorithm.HMAC256(secret)

    private const val validityInMs = 36_000_00 * 10 // 10 hours

    val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

    private fun generateToken(user: User): String =
        JWT.create()
            .withIssuer(issuer)
            .withClaim("uuid", user.id.value.toString())
            .withExpiresAt(getExpiration())
            .sign(algorithm)

    fun registerCustomer(userCredentials: UserCredentials): String {
        val hashedPassword = BCrypt.hashpw(userCredentials.password, BCrypt.gensalt())
        val databasePayload = UserCredentials(userCredentials.email, hashedPassword)
        val user = UserService.createUser(databasePayload)
        return generateToken(user)
    }

    fun loginUser(userCredentials: UserCredentials): String {
        val user = UserService.getUserByEmail(userCredentials.email)
        when {
            user === null -> {
                throw NotFoundException("User not found")
            }
            !BCrypt.checkpw(userCredentials.password, user.passwordHash) -> {
                throw BadRequestException("Passwords does not match")
            }
            else -> {
                return generateToken(user)
            }
        }
    }

}