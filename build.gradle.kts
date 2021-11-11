val ktorVersion = "1.6.5"
val kotlinVersion = "1.5.31"
val logbackVersion = "1.2.6"
val postgresqlVersion = "42.3.1"
val hikariCPVersion = "5.0.0"
val exposedVersion = "0.36.2"
val flywayVersion = "8.0.4"
val h2Version = "1.4.200"
val kompendiumVersion = "1.9.1"
val jbcryptVersion = "0.4"
val reflectionsVersion = "0.10.2"

plugins {
    application
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31"
    id("org.flywaydb.flyway") version "8.0.4"
    id("com.jetbrains.exposed.gradle.plugin") version "0.2.1"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
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
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    // WebSockets
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    // JSON serializations
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    // Authentication
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    // BCrypt
    implementation("org.mindrot:jbcrypt:$jbcryptVersion")
    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    // Postgres connector
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    // HikariCP JDBC connection pool
    implementation("com.zaxxer:HikariCP:$hikariCPVersion")
    // Exposed as SQL framework
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    // Flyway for DB migrations
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    // API generation
    implementation("io.bkbn:kompendium-core:$kompendiumVersion")
    implementation("io.bkbn:kompendium-auth:$kompendiumVersion")
    // Test dependencies
    testImplementation("org.reflections:reflections:$reflectionsVersion")
    testImplementation("com.h2database:h2:$h2Version")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of("11"))
    }
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }

    create("stage") {
        dependsOn("installDist")
    }
}

flyway {
    url = System.getenv("DB_URL")
    user = System.getenv("DB_USER")
    password = System.getenv("DB_PASSWORD")
}

exposedCodeGeneratorConfig {
    configFilename = "exposed-generation-conf.yml"
    user = System.getenv("DB_USER")
    password = System.getenv("DB_PASSWORD")
    databaseName = "wishlists"
    databaseDriver = "postgresql"
}
