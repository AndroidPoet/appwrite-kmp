package io.appwrite.storage

/**
 * KMP-friendly file input abstraction for uploads.
 *
 * Usage:
 * ```
 * val file = InputFile.fromBytes(bytes, "photo.jpg", "image/jpeg")
 * val file = InputFile.fromPath("/path/to/file.pdf", "doc.pdf", "application/pdf")
 * ```
 */
class InputFile private constructor(
    val filename: String,
    val mimeType: String,
    val sourceType: SourceType,
    val data: ByteArray? = null,
    val path: String? = null,
) {
    enum class SourceType { BYTES, PATH }

    companion object {
        fun fromBytes(
            bytes: ByteArray,
            filename: String,
            mimeType: String = "application/octet-stream",
        ) = InputFile(
            filename = filename,
            mimeType = mimeType,
            sourceType = SourceType.BYTES,
            data = bytes,
        )

        fun fromPath(
            path: String,
            filename: String,
            mimeType: String = "application/octet-stream",
        ) = InputFile(
            filename = filename,
            mimeType = mimeType,
            sourceType = SourceType.PATH,
            path = path,
        )
    }
}
