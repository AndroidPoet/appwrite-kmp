package io.appwrite.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Execution(
    @SerialName("\$id") val id: String,
    @SerialName("\$createdAt") val createdAt: String,
    @SerialName("\$updatedAt") val updatedAt: String,
    val functionId: String,
    val trigger: String = "",
    val status: String = "",
    val requestMethod: String = "",
    val requestPath: String = "",
    val requestHeaders: List<ExecutionHeader> = emptyList(),
    val responseStatusCode: Int = 0,
    val responseBody: String = "",
    val responseHeaders: List<ExecutionHeader> = emptyList(),
    val logs: String = "",
    val errors: String = "",
    val duration: Double = 0.0,
    val scheduledAt: String? = null,
)

@Serializable
data class ExecutionHeader(val name: String, val value: String)

@Serializable
data class ExecutionList(val total: Int, val executions: List<Execution>)
