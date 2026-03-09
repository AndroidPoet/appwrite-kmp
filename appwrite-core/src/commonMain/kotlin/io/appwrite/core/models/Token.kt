package io.appwrite.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Token(
    @SerialName("\$id") val id: String,
    @SerialName("\$createdAt") val createdAt: String,
    val userId: String,
    val secret: String,
    val expire: String,
    val phrase: String = "",
)

@Serializable
data class MfaChallenge(
    @SerialName("\$id") val id: String,
    @SerialName("\$createdAt") val createdAt: String,
    val userId: String,
    val expire: String,
)

@Serializable
data class MfaFactors(
    val totp: Boolean = false,
    val phone: Boolean = false,
    val email: Boolean = false,
    val recoveryCode: Boolean = false,
)

@Serializable
data class MfaRecoveryCodes(
    val recoveryCodes: List<String>,
)

@Serializable
data class Identity(
    @SerialName("\$id") val id: String,
    @SerialName("\$createdAt") val createdAt: String,
    @SerialName("\$updatedAt") val updatedAt: String,
    val userId: String,
    val provider: String,
    val providerUid: String,
    val providerEmail: String = "",
    val providerAccessToken: String = "",
    val providerAccessTokenExpiry: String = "",
    val providerRefreshToken: String = "",
)

@Serializable
data class IdentityList(
    val total: Int,
    val identities: List<Identity>,
)

@Serializable
data class Log(
    val event: String,
    val userId: String,
    val userEmail: String = "",
    val userName: String = "",
    val ip: String = "",
    val time: String,
    val osCode: String = "",
    val osName: String = "",
    val clientType: String = "",
    val clientCode: String = "",
    val clientName: String = "",
    val clientVersion: String = "",
    val clientEngine: String = "",
    val deviceName: String = "",
    val deviceBrand: String = "",
    val deviceModel: String = "",
    val countryCode: String = "",
    val countryName: String = "",
)

@Serializable
data class LogList(
    val total: Int,
    val logs: List<Log>,
)
