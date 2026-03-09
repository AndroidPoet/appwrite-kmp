package io.appwrite.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Document(
    @SerialName("\$id") val id: String,
    @SerialName("\$collectionId") val collectionId: String,
    @SerialName("\$databaseId") val databaseId: String,
    @SerialName("\$createdAt") val createdAt: String,
    @SerialName("\$updatedAt") val updatedAt: String,
    @SerialName("\$permissions") val permissions: List<String> = emptyList(),
    val data: JsonObject = JsonObject(emptyMap()),
)

@Serializable
data class DocumentList(
    val total: Int,
    val documents: List<Document>,
)
