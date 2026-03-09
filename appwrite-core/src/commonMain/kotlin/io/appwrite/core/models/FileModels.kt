package io.appwrite.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppwriteFile(
    @SerialName("\$id") val id: String,
    @SerialName("\$createdAt") val createdAt: String,
    @SerialName("\$updatedAt") val updatedAt: String,
    @SerialName("\$permissions") val permissions: List<String> = emptyList(),
    val bucketId: String,
    val name: String,
    val signature: String = "",
    val mimeType: String = "",
    val sizeOriginal: Long = 0,
    val chunksTotal: Int = 0,
    val chunksUploaded: Int = 0,
)

@Serializable
data class FileList(
    val total: Int,
    val files: List<AppwriteFile>,
)

data class UploadProgress(
    val id: String,
    val progress: Double,
    val sizeUploaded: Long,
    val chunksTotal: Int,
    val chunksUploaded: Int,
)
