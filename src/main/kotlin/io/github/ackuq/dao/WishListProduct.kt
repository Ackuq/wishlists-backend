package io.github.ackuq.dao

import io.github.ackuq.dto.WishListProductDTO
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

object WishListProducts : IntIdTable("wish_list_product", "id") {
    val title: Column<String> = varchar("title", 1000)
    val description: Column<String?> = text("description").nullable()
    val link: Column<String?> = text("link").nullable()
    val claimedBy = reference("claimed_by", Users).nullable()
    val wishList = reference("wish_list", WishLists)
}


class WishListProduct(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WishListProduct>(WishListProducts)

    var title by WishListProducts.title
    var description by WishListProducts.description
    var link by WishListProducts.link
    var claimedBy by User optionalReferencedOn WishListProducts.claimedBy
    var wishListId by WishListProducts.wishList
    var wishList by WishList referencedOn WishListProducts.wishList

    fun toDTO() = transaction {
        WishListProductDTO(
            id = this@WishListProduct.id.value,
            title = this@WishListProduct.title,
            description = this@WishListProduct.description,
            link = this@WishListProduct.link,
            claimedBy = this@WishListProduct.claimedBy?.email,
            wishListId = this@WishListProduct.wishListId.value
        )
    }
}