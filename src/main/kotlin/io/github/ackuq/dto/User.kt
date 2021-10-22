package io.github.ackuq.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(val uuid: String, val email: String, val role: Role, val ownedWishLists: List<Int>, val memberWishLists: List<Int>)

@Serializable
data class UserCredentials(val email: String, val password: String)

@Serializable
data class UpdateUserDTO(val email: String? = null)


@Serializable
enum class Role {
    Admin,
    Customer
}