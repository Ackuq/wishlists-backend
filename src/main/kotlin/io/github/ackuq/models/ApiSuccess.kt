package io.github.ackuq.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ApiSuccess(val status: Int, val result: @Contextual Any)
