package io.github.ackuq.services

import io.github.ackuq.conf.DatabaseFactory.dbQuery
import io.github.ackuq.models.*
import org.jetbrains.exposed.sql.*
import java.util.*

object UserService {
    suspend fun createUser(newUser: UserCredentials): User = dbQuery {
        Users.insert {
            it[email] = newUser.email
            it[passwordHash] = newUser.password
            it[role] = Role.Customer
        }.resultedValues!!.first().let { toUser(it) }
    }

    suspend fun getAllUsers(): List<User> = dbQuery {
        Users.selectAll().map { toUser(it) }
    }

    suspend fun getUserByEmail(email: String): User? = dbQuery {
        Users.select {
            (Users.email eq email)
        }.mapNotNull { toUser(it) }.singleOrNull()
    }

    suspend fun getUserByUUID(uuid: UUID): User? = dbQuery {
        Users.select {
            (Users.uuid eq uuid)
        }.mapNotNull { toUser(it) }.singleOrNull()
    }

    suspend fun updateUser(userDTO: UpdateUserDTO, user: User): User = dbQuery {
        Users.update({Users.uuid eq user.uuid}) {
            it[email] = userDTO.email ?: user.email
        }
        Users.select { Users.uuid eq user.uuid }.first().let { toUser(it) }
    }

    private fun toUser(row: ResultRow): User =
        User(
            uuid = row[Users.uuid],
            email = row[Users.email],
            passwordHash = row[Users.passwordHash],
            role = row[Users.role]
        )
}