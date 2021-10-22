package io.github.ackuq.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(val uuid: String, val email: String, val role: Role)

@Serializable
data class UserCredentials(val email: String, val password: String)

@Serializable
data class UpdateUserDTO(val email: String? = null)


@Serializable
enum class Role {
    Admin,
    Customer
}