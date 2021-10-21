package io.github.ackuq

import io.ktor.server.testing.*

fun withTestServer(block: TestApplicationEngine.() -> Unit) {
    withTestApplication({
        module(TestDatabaseFactory())
    }, block)
}