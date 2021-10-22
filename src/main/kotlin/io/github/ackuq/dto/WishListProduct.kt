package io.github.ackuq.dto

import kotlinx.serialization.Serializable

@Serializable
data class WishListProductDTO(
    val id: Int,
    val title: String,
    val description: String?,
    val link: String?,
    val claimedBy: String?,
    val wishListId: Int
)

@Serializable
data class WishListProductPayload(
    val title: String,
    val description: String?,
    val link: String?,
    val wishListId: Int
)

@Serializable
data class CreateWishListProductPayload(
    val title: String,
    val description: String? = null,
    val link: String? = null,
)

@Serializable
data class EditWishListProductPayload(
    val title: String? = null,
    val description: String? = null,
    val link: String? = null,
)