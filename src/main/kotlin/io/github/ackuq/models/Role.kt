package io.github.ackuq.models

import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    Admin,
    Customer
}