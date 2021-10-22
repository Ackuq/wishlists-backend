package io.github.ackuq.dto

import kotlinx.serialization.Serializable

@Serializable
data class WishListDTO(val id: Int, val users: List<String>)

@Serializable
data class WishListPayload(val users: List<String> = emptyList())