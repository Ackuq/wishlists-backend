package io.github.ackuq.routes

import io.github.ackuq.conf.AuthorizationException
import io.github.ackuq.controllers.UserController
import io.github.ackuq.services.UserService
import io.github.ackuq.utils.handleApiSuccess
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import java.util.*


fun Application.usersRoutes() {
    routing {
        route("/users") {
            authenticate("admin") {
                route("/") {
                    get {
                        val users = UserController.getAllUsers()
                        handleApiSuccess(users, HttpStatusCode.OK, call)
                    }
                }
            }

            authenticate {
                route("/me/") {
                    get {
                        val principal = call.principal<UserIdPrincipal>()
                        val uuid = principal?.name ?: throw BadRequestException("Invalid token")
                        println(uuid)
                        val user = UserService.getUserByUUID(UUID.fromString(uuid))
                            ?: throw NotFoundException("User not found")
                        handleApiSuccess(user, HttpStatusCode.OK, call)
                    }
                }
                route("/{uuid}/") {
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