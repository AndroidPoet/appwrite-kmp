package io.appwrite.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Session(
    @SerialName("\$id") val id: String,
    @SerialName("\$createdAt") val createdAt: String,
    @SerialName("\$updatedAt") val updatedAt: String,
    val userId: String,
    val expire: String,
    val current: Boolean,
    val provider: String = "",
    val providerUid: String = "",
    val providerAccessToken: String = "",
    val providerAccessTokenExpiry: String = "",
    val providerRefreshToken: String = "",
    val ip: String = "",
    val osCode: String = "",
    val osName: String = "",
    val clientType: String = "",
    val clientCode: String = "",
    val clientName: String = "",
    val clientVersion: String = "",
    val clientEngine: String = "",
    val clientEngineVersion: String = "",
    val deviceName: String = "",
    val deviceBrand: String = "",
    val deviceModel: String = "",
    val countryCode: String = "",
    val countryName: String = "",
    val factor: String = "",
    val secret: String = "",
    val mfaUpdatedAt: String = "",
)

@Serializable
data class SessionList(
    val total: Int,
    val sessions: List<Session>,
)

@Serializable
data class Jwt(
    val jwt: String,
)
