package io.appwrite.storage

import io.appwrite.core.models.AppwriteFile
import io.appwrite.core.result.AppwriteError

/**
 * Represents the state of a chunked file upload, emitted via `Flow<UploadState>`.
 */
sealed interface UploadState {

    data class Progress(
        val bytesUploaded: Long,
        val totalBytes: Long,
        val chunksUploaded: Int,
        val chunksTotal: Int,
    ) : UploadState

    data class Complete(val file: AppwriteFile) : UploadState

    data class Failed(val error: AppwriteError) : UploadState
}
