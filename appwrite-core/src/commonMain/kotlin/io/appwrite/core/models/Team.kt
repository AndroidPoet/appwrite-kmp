package io.appwrite.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Team(
    @SerialName("\$id") val id: String,
    @SerialName("\$createdAt") val createdAt: String,
    @SerialName("\$updatedAt") val updatedAt: String,
    val name: String,
    val total: Int = 0,
    val prefs: Map<String, String> = emptyMap(),
)

@Serializable
data class TeamList(
    val total: Int,
    val teams: List<Team>,
)

@Serializable
data class Membership(
    @SerialName("\$id") val id: String,
    @SerialName("\$createdAt") val createdAt: String,
    @SerialName("\$updatedAt") val updatedAt: String,
    val userId: String,
    val userName: String = "",
    val userEmail: String = "",
    val teamId: String,
    val teamName: String = "",
    val invited: String = "",
    val joined: String = "",
    val confirm: Boolean = false,
    val mfa: Boolean = false,
    val roles: List<String> = emptyList(),
)

@Serializable
data class MembershipList(
    val total: Int,
    val memberships: List<Membership>,
)
