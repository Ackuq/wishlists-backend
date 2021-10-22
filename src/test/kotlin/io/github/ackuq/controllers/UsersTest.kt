package io.github.ackuq.controllers

import io.github.ackuq.TestDatabaseFactory
import io.github.ackuq.dto.Role
import io.github.ackuq.dto.UserCredentials
import io.github.ackuq.services.UserService
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


class UsersTest {

    private val databaseFactory: TestDatabaseFactory = TestDatabaseFactory()

    @BeforeTest
    fun setup() {
        databaseFactory.init()
    }

    @AfterTest
    fun tearDown() {
        databaseFactory.close()
    }

    @Test
    fun createUser(): Unit =
        runBlocking {
            val userCredentials = UserCredentials("test@testson.com", "pass")

            UserController.register(userCredentials)

            val user = UserService.getUserByEmail(userCredentials.email)!!
            val users = UserService.getAllUsers()


            assertEquals(1, users.size)
            assertEquals(user.id.value, users.first().id.value)
            assertEquals(Role.Customer, user.role)
            assertEquals(userCredentials.email, user.email)
        }

}