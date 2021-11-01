package io.github.ackuq.dto

import kotlinx.serialization.Serializable

@Serializable
data class WishListDTO(
    val id: Int,
    val title: String,
    val description: String?,
    val users: List<String>,
    val products: List<WishListProductDTO>
)

@Serializable
data class CreateWishListPayload(
    val title: String,
    val description: String? = null
)

@Serializable
data class EditWishListPayload(
    val users: List<String>? = null,
    val title: String? = null,
    val description: String? = null
)
