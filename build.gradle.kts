val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31"
    id("org.flywaydb.flyway") version "8.0.1"
}

group = "io.github.ackuq"
version = "0.0.1"
application {
    mainClass.set("io.github.ackuq.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Core packets
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    // WebSockets
    implementation("io.ktor:ktor-websockets:$ktor_version")
    // JSON serializations
    implementation("io.ktor:ktor-serialization:$ktor_version")
    // Session handling
    implementation("io.ktor:ktor-server-sessions:$ktor_version")
    // Authentication
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")
    // Postgres connector
    implementation("org.postgresql:postgresql:42.3.0")
    // HikariCP JDBC connection pool
    implementation("com.zaxxer:HikariCP:5.0.0")
    // Exposed as SQL framework
    implementation("org.jetbrains.exposed:exposed:0.17.14")
    // Flyway for DB migrations
    implementation("org.flywaydb:flyway-core:5.0.3")
    // Test dependencies
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
}

tasks {
    val javaVersion = "11"

    compileKotlin {
        kotlinOptions { jvmTarget = javaVersion }
    }
    compileTestKotlin {
        kotlinOptions { jvmTarget = javaVersion }
    }
}

flyway {
    url = System.getenv("DB_URL")
    user = System.getenv("DB_USER")
    password = System.getenv("DB_PASSWORD")
    baselineOnMigrate = true
    locations = arrayOf("filesystem:src/main/resources/db/migration")
}