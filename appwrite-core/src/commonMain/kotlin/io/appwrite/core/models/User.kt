package io.appwrite.core.models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("\$id") val id: String,
    @SerialName("\$createdAt") val createdAt: String,
    @SerialName("\$updatedAt") val updatedAt: String,
    val name: String,
    val email: String,
    val phone: String,
    val emailVerification: Boolean,
    val phoneVerification: Boolean,
    val status: Boolean,
    val labels: List<String> = emptyList(),
    val passwordUpdate: String = "",
    val registration: String = "",
    val prefs: Map<String, String> = emptyMap(),
    val accessedAt: String = "",
    val mfa: Boolean = false,
    val targets: List<Target> = emptyList(),
)

@Serializable
data class Target(
    @SerialName("\$id") val id: String,
    @SerialName("\$createdAt") val createdAt: String,
    @SerialName("\$updatedAt") val updatedAt: String,
    val name: String = "",
    val userId: String = "",
    val providerId: String = "",
    val providerType: String = "",
    val identifier: String = "",
)
