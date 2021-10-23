package io.github.ackuq.dao

import io.github.ackuq.dto.WishListDTO
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

object WishLists : IntIdTable(name = "wish_lists", columnName = "id") {
    val owner = reference("owner_id", Users)
    val title: Column<String> = varchar("title", 1000)
    val description: Column<String?> = text("description").nullable()
}

class WishList(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WishList>(WishLists)

    var title by WishLists.title
    var description by WishLists.description

    var owner by User referencedOn WishLists.owner
    var users by User via UsersWishLists
    val products by WishListProduct referrersOn WishListProducts.wishList

    fun toDTO(): WishListDTO = transaction {
        WishListDTO(
            id = this@WishList.id.value,
            title = this@WishList.title,
            description = this@WishList.description,
            users = this@WishList.users.map { it.email },
            products = this@WishList.products.map { it.toDTO() })
    }
}
