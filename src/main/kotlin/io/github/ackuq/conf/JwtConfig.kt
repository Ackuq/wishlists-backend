package io.github.ackuq.conf

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import io.github.ackuq.dao.User
import io.github.ackuq.dto.TokenDTO
import io.github.ackuq.dto.UserCredentialsDTO
import io.github.ackuq.services.UserService
import io.ktor.config.HoconApplicationConfig
import io.ktor.features.BadRequestException
import org.mindrot.jbcrypt.BCrypt
import java.util.Date

object JwtConfig {
    private val appConfig = HoconApplicationConfig(ConfigFactory.load())
    private val secret = appConfig.property("jwt.secret").getString()
    private val issuer = appConfig.property("jwt.issuer").getString()
    private val algorithm = Algorithm.HMAC256(secret)

    private const val accessTokenValidity = 1000 * 60 * 60 * 10 // 10 hours

    val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()

    private fun getAccessTokenExpiration() = Date(System.currentTimeMillis() + accessTokenValidity)

    private fun generateToken(user: User): TokenDTO = TokenDTO(
        accessToken = JWT.create()
            .withIssuer(issuer)
            .withClaim("uuid", user.id.value.toString())
            .withExpiresAt(getAccessTokenExpiration())
            .sign(algorithm)
    )

    fun registerCustomer(userCredentials: UserCredentialsDTO): TokenDTO {
        val hashedPassword = BCrypt.hashpw(userCredentials.password, BCrypt.gensalt())
        val databasePayload = UserCredentialsDTO(userCredentials.email, hashedPassword)
        val user = UserService.createUser(databasePayload)
        return generateToken(user)
    }

    private const val loginFailMessage = "User with email-password pair not found"
    fun loginUser(userCredentials: UserCredentialsDTO): TokenDTO {
        val user = UserService.getUserByEmail(userCredentials.email)
        when {
            user === null -> {
                throw BadRequestException(loginFailMessage)
            }
            !BCrypt.checkpw(userCredentials.password, user.passwordHash) -> {
                throw BadRequestException(loginFailMessage)
            }
            else -> {
                return generateToken(user)
            }
        }
    }
}
