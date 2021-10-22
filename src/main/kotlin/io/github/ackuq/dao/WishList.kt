package io.github.ackuq.dao

import io.github.ackuq.dto.WishListDTO
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object WishLists : IntIdTable(name = "wish_lists", columnName = "id") {
    val owner = reference("owner_id", Users)
}

class WishList(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WishList>(WishLists)

    var owner by User referencedOn WishLists.owner

    fun toDTO() = WishListDTO(this.id.value)
}
