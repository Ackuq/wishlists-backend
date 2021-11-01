package io.github.ackuq

import io.github.ackuq.utils.TestDatabaseFactory
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.checkMappingConsistence
import org.jetbrains.exposed.sql.transactions.transaction
import org.reflections.Reflections
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SchemaTest {

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
    fun testSchema() {
        val tables =
            Reflections("io.github.ackuq").getSubTypesOf(Table::class.java).mapNotNull { it.kotlin.objectInstance }
                .toTypedArray()

        transaction {
            assertEquals(emptyList(), SchemaUtils.statementsRequiredToActualizeScheme(*tables))
            assertEquals(emptyList(), SchemaUtils.addMissingColumnsStatements(*tables))
            assertEquals(emptyList(), checkMappingConsistence(*tables))
        }
    }
}
