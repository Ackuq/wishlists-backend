package io.github.ackuq.utils

import io.github.ackuq.module
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication

fun withTestServer(block: TestApplicationEngine.() -> Unit) {
    withTestApplication({
        module(TestDatabaseFactory())
    }, block)
}
