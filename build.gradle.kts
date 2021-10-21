val ktor_version = "1.6.4"
val kotlin_version = "1.5.31"
val logback_version = "1.2.6"
val postgresql_version = "42.3.0"
val hikariCP_version = "5.0.0"
val exposed_version = "0.17.14"
val flyway_version = "8.0.2"

plugins {
    application
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31"
    id("org.flywaydb.flyway") version "8.0.2"
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
    // Authentication
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    // BCrypt
    implementation("org.mindrot:jbcrypt:0.4")
    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")
    // Postgres connector
    implementation("org.postgresql:postgresql:$postgresql_version")
    // HikariCP JDBC connection pool
    implementation("com.zaxxer:HikariCP:$hikariCP_version")
    // Exposed as SQL framework
    implementation("org.jetbrains.exposed:exposed:$exposed_version")
    // Flyway for DB migrations
    implementation("org.flywaydb:flyway-core:$flyway_version")
    // Test dependencies
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
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
    baselineOnMigrate = true
    locations = arrayOf("filesystem:src/main/resources/db/migration")
}
