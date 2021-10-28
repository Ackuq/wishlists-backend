package io.github.ackuq

import io.github.ackuq.dao.Users
import io.github.ackuq.dao.UsersWishLists
import io.github.ackuq.dao.WishListProducts
import io.github.ackuq.dao.WishLists
import io.github.ackuq.utils.TestDatabaseFactory
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.checkMappingConsistence
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SchemaTest {

    private val databaseFactory: TestDatabaseFactory = TestDatabaseFactory()

    private val tables: Array<Table> = arrayOf(Users, WishLists, WishListProducts, UsersWishLists)

    @BeforeTest
    fun setup() {
        databaseFactory.init()
    }

    @AfterTest
    fun tearDown() {
        databaseFactory.close()
    }

    @Test
    fun testSchema() {
        transaction {
            assertEquals(emptyList(), SchemaUtils.statementsRequiredToActualizeScheme(*tables))
            assertEquals(emptyList(), SchemaUtils.addMissingColumnsStatements(*tables))
            assertEquals(emptyList(), checkMappingConsistence(*tables))
        }
    }
}