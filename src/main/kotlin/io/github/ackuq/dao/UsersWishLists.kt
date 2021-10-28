package io.github.ackuq.dao

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object UsersWishLists : Table("users_wish_lists") {
    var user = reference("user_id", Users, ReferenceOption.CASCADE)
    var wishList = reference("wish_list_id", WishLists, ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(user, wishList, name = "pk_users_wish_lists")
}

