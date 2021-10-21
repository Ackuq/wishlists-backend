package io.github.ackuq.routes

import io.github.ackuq.conf.AuthorizationException
import io.github.ackuq.controllers.UserController
import io.github.ackuq.models.Role
import io.github.ackuq.models.User
import io.github.ackuq.utils.handleApiSuccess
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*


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
                        val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                        handleApiSuccess(user, HttpStatusCode.OK, call)
                    }
                }
                route("/{uuid}/") {
                    get {
                        val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                        val uuid = call.parameters["uuid"] ?: throw BadRequestException("No UUID specified")
                        if (user.uuid != uuid && user.role != Role.Admin) {
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