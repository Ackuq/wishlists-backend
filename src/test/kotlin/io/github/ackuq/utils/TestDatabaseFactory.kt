package io.github.ackuq.utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.ackuq.conf.IDatabaseFactory
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

class TestDatabaseFactory : IDatabaseFactory {

    private val dbURL = "jdbc:h2:mem:test;DATABASE_TO_UPPER=false;MODE=PostgreSQL"
    private val dbUserName = "sa"
    private val dbPassword = ""

    lateinit var dataSource: HikariDataSource

    override fun init() {
        Database.connect(hikari())
        val flyway = Flyway.configure().dataSource(dbURL, dbUserName, dbPassword).load()
        flyway.info()
        flyway.migrate()
    }

    fun close() {
        dataSource.close()
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = dbURL
        config.username = dbUserName
        config.password = dbPassword
        config.maximumPoolSize = 1
        config.isAutoCommit = true
        config.validate()
        dataSource = HikariDataSource(config)
        return dataSource
    }
}
