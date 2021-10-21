package io.github.ackuq.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import java.util.*

object WishLists : Table("wish_lists") {
    val id = integer("id").autoIncrement().primaryKey()
    val owner = (uuid("owner_id") references Users.uuid)
}

data class WishList(val id: Int, val owner: UUID)

@Serializable
data class WishListDTO(val id: Int, val owner: String)

@Serializable
data class NewWishListPayload(val owner: String)

fun WishList.toWishListDTO() = WishListDTO(id, owner.toString())