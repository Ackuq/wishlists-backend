package io.github.ackuq.routes

import io.bkbn.kompendium.annotations.Param
import io.bkbn.kompendium.annotations.ParamType
import io.bkbn.kompendium.auth.Notarized.notarizedAuthenticate
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.github.ackuq.conf.AuthorizationException
import io.github.ackuq.conf.SecurityConfigurations
import io.github.ackuq.dao.User
import io.github.ackuq.dto.Role
import io.github.ackuq.dto.UpdateUserDTO
import io.github.ackuq.dto.UserDTO
import io.github.ackuq.services.UserService
import io.github.ackuq.utils.SimpleResponseInfo
import io.github.ackuq.utils.exceptionInfo
import io.github.ackuq.utils.getInfo
import io.github.ackuq.utils.handleApiSuccess
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.principal
import io.ktor.features.BadRequestException
import io.ktor.features.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.routing.routing
import java.util.UUID

fun Route.users() {
    notarizedAuthenticate(SecurityConfigurations.admin) {
        route("") {
            notarizedGet(
                getInfo<Unit, List<UserDTO>>(
                    summary = "Get users",
                    description = "Received all the users",
                    responseInfo = SimpleResponseInfo(
                        HttpStatusCode.OK,
                        listOf(
                            UserDTO(
                                "UUID",
                                "Nomen",
                                "Nescio",
                                "nomen@nescio.com",
                                Role.Customer,
                                emptyList(),
                                emptyList()
                            )
                        ),
                    ),
                    throws = setOf(exceptionInfo(HttpStatusCode.Unauthorized, "Not authorized to see page"))
                )
            ) {
                val users = UserService.getAllUsers()
                handleApiSuccess(users.map { it.toDTO() }, HttpStatusCode.OK, call)
            }
        }
    }
}

data class UserParams(
    @Param(type = ParamType.PATH)
    val uuid: String
)

fun Route.user() {
    notarizedAuthenticate(SecurityConfigurations.default) {
        route("/{uuid}") {
            notarizedGet(
                getInfo<UserParams, UserDTO>(
                    summary = "Get user",
                    description = "Receive the user with specified uuid",
                    responseInfo = SimpleResponseInfo(
                        HttpStatusCode.OK,
                        UserDTO(
                            "UUID",
                            "Nomen",
                            "nescio",
                            "nome@nescio.com",
                            Role.Customer,
                            emptyList(),
                            emptyList()
                        ),
                    ),
                    throws = setOf(
                        exceptionInfo(HttpStatusCode.BadRequest, "Bad request"),
                        exceptionInfo(HttpStatusCode.Unauthorized, "Not authorized to see page"),
                        exceptionInfo(HttpStatusCode.NotFound, "User not found")
                    )
                )
            ) {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val uuid = call.parameters["uuid"] ?: throw BadRequestException("No UUID specified")
                if (user.id.value.toString() != uuid && user.role != Role.Admin) {
                    throw AuthorizationException("Not authorized to see this page")
                } else {
                    val uuidUser =
                        UserService.getUserByUUID(UUID.fromString(uuid)) ?: throw NotFoundException("User not found")
                    handleApiSuccess(uuidUser.toDTO(), HttpStatusCode.OK, call)
                }
            }
        }
    }
}

fun Route.me() {
    notarizedAuthenticate(SecurityConfigurations.default) {
        route("/me") {
            notarizedGet(
                getInfo<Unit, UserDTO>(
                    summary = "Get authenticated user",
                    description = "Receive the user currently authenticated",
                    responseInfo = SimpleResponseInfo(
                        HttpStatusCode.OK,
                        UserDTO(
                            "UUID",
                            "Nomen",
                            "Nescio",
                            "nome@nescio.com",
                            Role.Customer,
                            emptyList(),
                            emptyList()
                        ),
                    ),
                    throws = setOf(exceptionInfo(HttpStatusCode.Unauthorized, "Not authorized to proceed"))
                )
            ) {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                handleApiSuccess(user.toDTO(), HttpStatusCode.OK, call)
            }
            put {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val updateUser = call.receive<UpdateUserDTO>()
                val newUser = UserService.updateUser(user, updateUser)
                handleApiSuccess(newUser.toDTO(), HttpStatusCode.OK, call)
            }
        }
    }
}

fun Application.usersRoutes() {
    routing {
        route("api/v1/users") {
            users()
            user()
            me()
        }
    }
}
