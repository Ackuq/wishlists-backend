package io.github.ackuq.utils

import io.github.ackuq.module
import io.ktor.server.testing.*

fun withTestServer(block: TestApplicationEngine.() -> Unit) {
    withTestApplication({
        module(TestDatabaseFactory())
    }, block)
}