package io.github.ackuq.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.ackuq.models.ApiError
import io.github.ackuq.models.User
import io.github.ackuq.models.UserPayload
import io.github.ackuq.services.UserService
import io.github.ackuq.utils.handleApiError
import io.github.ackuq.utils.handleApiException
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.SerializationException
import java.lang.Exception
import java.util.*

fun Application.authenticationRoutes() {
    val issuer = environment.config.property("jwt.issuer").getString()
    val secret = environment.config.property("jwt.secret").getString()

    fun generateToken(user: User): String {
        return JWT.create().withIssuer(issuer).withClaim("email", user.email)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000)).sign(
                Algorithm.HMAC256(secret)
            )
    }

    fun Route.authRoute() {
        route("/auth") {
            route("/login") {
                post {
                    try {
                        val userPayload = call.receive<UserPayload>()
                        val user = UserService.getUserByEmail(userPayload.email)
                        when {
                            user == null -> {
                                handleApiError("User not found", HttpStatusCode.NotFound, call)
                            }
                            user.password != userPayload.password -> {
                                handleApiError("Passwords does not match", HttpStatusCode.BadRequest, call)
                            }
                            else -> {
                                val token = generateToken(user)
                                call.respond(HttpStatusCode.Created, hashMapOf("token" to token))
                            }
                        }
                    } catch (ex: SerializationException) {
                        handleApiException(ex, HttpStatusCode.BadRequest, call)
                    } catch (ex: Exception) {
                        handleApiException(ex, HttpStatusCode.InternalServerError, call)
                    }

                }
            }
            route("/register") {
                post {
                    try {
                        val newUser = call.receive<UserPayload>()
                        UserService.createUser(newUser)
                        val user = UserService.getUserByEmail(newUser.email) ?: throw Exception("Something went wront")
                        val token = generateToken(user)
                        call.respond(HttpStatusCode.Created, hashMapOf("token" to token))
                    } catch (ex: SerializationException) {
                        handleApiException(ex, HttpStatusCode.BadRequest, call)
                    } catch (ex: Exception) {
                        handleApiException(ex, HttpStatusCode.InternalServerError, call)
                    }
                }
            }
        }
    }

    routing {
        route("/users") {
            authRoute()
            // TODO: Admin route
            route("/") {
                get {
                    val users = UserService.getAllUsers()
                    call.respond(HttpStatusCode.OK, hashMapOf("results" to users))
                }
            }
            authenticate {
                route("/me") {
                    get {
                        val principal = call.principal<UserIdPrincipal>()
                        val email = principal?.name ?: error("No principal decoded")
                        val user = UserService.getUserByEmail(email) ?: error("User not found")
                        call.respond(HttpStatusCode.OK, user)
                    }
                }
                route("/{uuid}") {
                    get {
                        val principal = call.principal<UserIdPrincipal>()
                        val email = principal?.name ?: error("No principal decoded")
                        val uuid = call.parameters["uuid"]
                        val user = UserService.getUserByEmail(email) ?: error("User not found")
                        // TODO: Admins should be able to access this
                        if (user.uuid != uuid) {
                            handleApiError("Not authorized to see this page", HttpStatusCode.Unauthorized, call)
                        } else {
                            val uuidUser = UserService.getUserByUUID(UUID.fromString(uuid))
                            if (uuidUser == null) {
                                call.respond(
                                    HttpStatusCode.NotFound,
                                    ApiError(HttpStatusCode.NotFound.value, "No user found")
                                )
                            } else {
                                call.respond(HttpStatusCode.OK, uuidUser)
                            }
                        }
                    }
                }
            }
        }
    }
}