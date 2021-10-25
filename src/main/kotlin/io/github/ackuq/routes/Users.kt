package io.github.ackuq.routes

import io.bkbn.kompendium.Notarized.notarizedGet
import io.bkbn.kompendium.annotations.KompendiumParam
import io.bkbn.kompendium.annotations.ParamType
import io.github.ackuq.conf.AuthorizationException
import io.github.ackuq.dao.User
import io.github.ackuq.dto.Role
import io.github.ackuq.dto.UpdateUserDTO
import io.github.ackuq.dto.UserDTO
import io.github.ackuq.services.UserService
import io.github.ackuq.utils.SimpleResponseInfo
import io.github.ackuq.utils.getInfo
import io.github.ackuq.utils.handleApiSuccess
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.routing.*
import java.util.*


fun Route.users() {
    authenticate("admin") {
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
                    throws = setOf(AuthorizationException::class)
                )
            ) {
                val users = UserService.getAllUsers()
                handleApiSuccess(users.map { it.toDTO() }, HttpStatusCode.OK, call)
            }
        }
    }
}

data class UserParams(
    @KompendiumParam(ParamType.PATH, "UUID of the user, as a string") val uuid: String
)

fun Route.user() {
    authenticate {
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
                    throws = setOf(AuthorizationException::class, BadRequestException::class, NotFoundException::class)
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
    authenticate {
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
                    throws = setOf(AuthorizationException::class)
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