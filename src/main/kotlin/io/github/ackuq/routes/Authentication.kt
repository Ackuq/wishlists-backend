package io.github.ackuq.routes

import io.github.ackuq.conf.AuthorizationException
import io.github.ackuq.controllers.UserController
import io.github.ackuq.models.UserPayload
import io.github.ackuq.services.UserService
import io.github.ackuq.utils.handleApiSuccess
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.routing.*
import java.util.*

fun Application.authenticationRoutes() {


    fun Route.authRoute() {
        route("/auth") {
            route("/login") {
                post {
                    val userPayload = call.receive<UserPayload>()
                    val token = UserController.login(userPayload)
                    handleApiSuccess(token, HttpStatusCode.OK, call)
                }
            }
            route("/register") {
                post {
                    val newUser = call.receive<UserPayload>()
                    val token = UserController.register(newUser)
                    handleApiSuccess(token, HttpStatusCode.Created, call)
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
                    val users = UserController.getAllUsers()
                    handleApiSuccess(users, HttpStatusCode.OK, call)
                }
            }
            authenticate {
                route("/me") {
                    get {
                        val principal = call.principal<UserIdPrincipal>()
                        val uuid = principal?.name ?: throw BadRequestException("Invalid token")
                        println(uuid)
                        val user = UserService.getUserByUUID(UUID.fromString(uuid))
                            ?: throw NotFoundException("User not found")
                        handleApiSuccess(user, HttpStatusCode.OK, call)
                    }
                }
                route("/{uuid}") {
                    get {
                        val principal = call.principal<UserIdPrincipal>()
                        val requesterUUID = principal?.name ?: throw BadRequestException("Invalid token")
                        val uuid = call.parameters["uuid"] ?: throw BadRequestException("No UUID specified")
                        val user = UserController.getUserByUUID(requesterUUID)
                        // TODO: Admins should be able to access this
                        if (user.uuid != uuid) {
                            throw AuthorizationException("Not authorized to see this page")
                        } else {
                            val uuidUser = UserController.getUserByUUID(uuid)
                            handleApiSuccess(uuidUser, HttpStatusCode.OK, call)
                        }
                    }
                }
            }
        }
    }
}