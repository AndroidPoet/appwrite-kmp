package io.appwrite.storage

import io.appwrite.client.Appwrite
import io.appwrite.client.ServiceBase
import io.appwrite.core.models.AppwriteFile
import io.appwrite.core.models.FileList
import io.appwrite.core.models.ImageGravity
import io.appwrite.core.query.QueryBuilder
import io.appwrite.core.query.buildQuery
import io.appwrite.core.result.AppwriteError
import io.appwrite.core.result.AppwriteResult
import io.appwrite.core.types.BucketId
import io.appwrite.core.types.FileId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * File storage service.
 *
 * Usage:
 * ```
 * val storage = appwrite.storage
 *
 * // List files
 * storage.listFiles(bucketId) {
 *     limit(10)
 * }
 *
 * // Upload with progress
 * val file = InputFile.fromBytes(bytes, "photo.jpg", "image/jpeg")
 * storage.upload(bucketId, FileId.unique(), file).collect { state ->
 *     when (state) {
 *         is UploadState.Progress -> updateUI(state)
 *         is UploadState.Complete -> onDone(state.file)
 *         is UploadState.Failed -> onError(state.error)
 *     }
 * }
 *
 * // Download
 * val bytes = storage.download(bucketId, fileId)
 *
 * // Preview with transforms
 * val thumbnail = storage.preview(bucketId, fileId) {
 *     width = 200
 *     height = 200
 *     gravity = ImageGravity.Center
 *     quality = 80
 * }
 * ```
 */
class Storage(appwrite: Appwrite) : ServiceBase(appwrite.transport) {

    suspend fun listFiles(
        bucketId: BucketId,
        query: (QueryBuilder.() -> Unit)? = null,
    ): AppwriteResult<FileList> = get(
        path = "/storage/buckets/${bucketId.raw}/files",
        params = buildMap {
            if (query != null) put("queries", buildQuery(query))
        },
    )

    suspend fun getFile(
        bucketId: BucketId,
        fileId: FileId,
    ): AppwriteResult<AppwriteFile> = get(
        path = "/storage/buckets/${bucketId.raw}/files/${fileId.raw}",
    )

    suspend fun updateFile(
        bucketId: BucketId,
        fileId: FileId,
        name: String? = null,
        permissions: List<String>? = null,
    ): AppwriteResult<AppwriteFile> = put(
        path = "/storage/buckets/${bucketId.raw}/files/${fileId.raw}",
        params = buildMap {
            if (name != null) put("name", name)
            if (permissions != null) put("permissions", permissions)
        },
    )

    suspend fun deleteFile(
        bucketId: BucketId,
        fileId: FileId,
    ): AppwriteResult<Unit> = delete(
        path = "/storage/buckets/${bucketId.raw}/files/${fileId.raw}",
    )

    // ── Upload ─────────────────────────────────────────────────

    /**
     * Upload a file using Appwrite's chunked upload protocol.
     *
     * Returns a [Flow] of [UploadState] that emits progress for each chunk
     * and completes with the final [AppwriteFile] object.
     *
     * Chunk size is 5 MB. For files smaller than 5 MB a single chunk is sent.
     * The first chunk carries `fileId`; subsequent chunks also send `x-appwrite-id`
     * so the server can stitch them together.
     */
    fun upload(
        bucketId: BucketId,
        fileId: FileId,
        file: InputFile,
        permissions: List<String>? = null,
    ): Flow<UploadState> = flow {
        try {
            val bytes = when (file.sourceType) {
                InputFile.SourceType.BYTES -> file.data
                    ?: error("InputFile created with fromBytes but data is null")
                InputFile.SourceType.PATH ->
                    error("Path-based uploads require platform-specific file reading")
            }

            val totalBytes = bytes.size.toLong()
            val chunksTotal = ((totalBytes + CHUNK_SIZE - 1) / CHUNK_SIZE).toInt()
            val path = "/storage/buckets/${bucketId.raw}/files"
            var currentFileId = fileId.raw

            for (chunkIndex in 0 until chunksTotal) {
                val start = chunkIndex.toLong() * CHUNK_SIZE
                val end = minOf(start + CHUNK_SIZE, totalBytes) - 1
                val chunk = bytes.copyOfRange(start.toInt(), (end + 1).toInt())
                val contentRange = "bytes $start-$end/$totalBytes"

                val params = buildMap {
                    put("fileId", currentFileId)
                    if (permissions != null) {
                        put("permissions", permissions.joinToString(","))
                    }
                }

                val extraHeaders = buildMap {
                    if (chunkIndex > 0) {
                        put("x-appwrite-id", currentFileId)
                    }
                }

                val result = uploadChunk<AppwriteFile>(
                    path = path,
                    params = params,
                    fileParamName = "file",
                    fileName = file.filename,
                    mimeType = file.mimeType,
                    chunk = chunk,
                    contentRange = contentRange,
                    extraHeaders = extraHeaders,
                )

                when (result) {
                    is AppwriteResult.Failure -> {
                        emit(UploadState.Failed(result.error))
                        return@flow
                    }
                    is AppwriteResult.Success -> {
                        val appwriteFile = result.data

                        // After first chunk, capture the server-assigned ID (for unique() IDs)
                        if (chunkIndex == 0 && currentFileId == "unique()") {
                            currentFileId = appwriteFile.id
                        }

                        emit(
                            UploadState.Progress(
                                bytesUploaded = end + 1,
                                totalBytes = totalBytes,
                                chunksUploaded = chunkIndex + 1,
                                chunksTotal = chunksTotal,
                            )
                        )

                        // Final chunk — emit the complete file
                        if (chunkIndex == chunksTotal - 1) {
                            emit(UploadState.Complete(appwriteFile))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emit(
                UploadState.Failed(
                    AppwriteError(
                        message = e.message ?: "Unknown upload error",
                        code = 0,
                        type = "unknown",
                    )
                )
            )
        }
    }

    private companion object {
        const val CHUNK_SIZE = 5L * 1024 * 1024 // 5 MB
    }

    // ── Download & Preview ───────────────────────────────────

    suspend fun download(
        bucketId: BucketId,
        fileId: FileId,
    ): AppwriteResult<ByteArray> = downloadRaw(
        path = "/storage/buckets/${bucketId.raw}/files/${fileId.raw}/download",
    )

    suspend fun view(
        bucketId: BucketId,
        fileId: FileId,
    ): AppwriteResult<ByteArray> = downloadRaw(
        path = "/storage/buckets/${bucketId.raw}/files/${fileId.raw}/view",
    )

    suspend fun preview(
        bucketId: BucketId,
        fileId: FileId,
        config: PreviewConfigBuilder.() -> Unit = {},
    ): AppwriteResult<ByteArray> {
        val params = PreviewConfigBuilder().apply(config).build()
        return downloadRaw(
            path = "/storage/buckets/${bucketId.raw}/files/${fileId.raw}/preview",
            params = params,
        )
    }
}

class PreviewConfigBuilder {
    var width: Int? = null
    var height: Int? = null
    var gravity: ImageGravity? = null
    var quality: Int? = null
    var borderWidth: Int? = null
    var borderColor: String? = null
    var borderRadius: Int? = null
    var opacity: Double? = null
    var rotation: Int? = null
    var background: String? = null

    internal fun build(): Map<String, Any?> = buildMap {
        width?.let { put("width", it) }
        height?.let { put("height", it) }
        gravity?.let { put("gravity", it.value) }
        quality?.let { put("quality", it) }
        borderWidth?.let { put("borderWidth", it) }
        borderColor?.let { put("borderColor", it) }
        borderRadius?.let { put("borderRadius", it) }
        opacity?.let { put("opacity", it) }
        rotation?.let { put("rotation", it) }
        background?.let { put("background", it) }
    }
}

/**
 * Extension property: `appwrite.storage`
 */
val Appwrite.storage: Storage get() = Storage(this)
