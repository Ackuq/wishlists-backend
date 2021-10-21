package io.github.ackuq

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.ackuq.conf.IDatabaseFactory
import org.jetbrains.exposed.sql.Database

class TestDatabaseFactory : IDatabaseFactory {

    lateinit var dataSource: HikariDataSource

    override fun init() {
        Database.connect(hikari())
        SchemaDefinition.createSchema()
    }

    fun close() {
        dataSource.close()
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:mem:;DATABASE_TO_UPPER=false;MODE=MYSQL"
        config.maximumPoolSize = 2
        config.isAutoCommit = true
        config.validate()
        dataSource = HikariDataSource(config)
        return dataSource
    }

}