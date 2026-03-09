package io.appwrite.avatars

import io.appwrite.client.Appwrite
import io.appwrite.client.ServiceBase
import io.appwrite.core.result.AppwriteResult

/**
 * Avatars service — browser icons, credit cards, flags, favicons, QR codes, and more.
 *
 * All methods return raw image bytes (`ByteArray`).
 *
 * Usage:
 * ```
 * val avatars = appwrite.avatars
 *
 * // Get a QR code
 * val qrBytes = avatars.getQR("https://example.com")
 *
 * // Get user initials avatar
 * val initialsBytes = avatars.getInitials(name = "John Doe", width = 100, height = 100)
 * ```
 */
class Avatars(appwrite: Appwrite) : ServiceBase(appwrite.transport) {

    suspend fun getBrowser(
        code: String,
        width: Int? = null,
        height: Int? = null,
        quality: Int? = null,
    ): AppwriteResult<ByteArray> = downloadRaw(
        path = "/avatars/browsers/$code",
        params = buildMap {
            if (width != null) put("width", width)
            if (height != null) put("height", height)
            if (quality != null) put("quality", quality)
        },
    )

    suspend fun getCreditCard(
        code: String,
        width: Int? = null,
        height: Int? = null,
        quality: Int? = null,
    ): AppwriteResult<ByteArray> = downloadRaw(
        path = "/avatars/credit-cards/$code",
        params = buildMap {
            if (width != null) put("width", width)
            if (height != null) put("height", height)
            if (quality != null) put("quality", quality)
        },
    )

    suspend fun getFavicon(
        url: String,
    ): AppwriteResult<ByteArray> = downloadRaw(
        path = "/avatars/favicon",
        params = mapOf("url" to url),
    )

    suspend fun getFlag(
        code: String,
        width: Int? = null,
        height: Int? = null,
        quality: Int? = null,
    ): AppwriteResult<ByteArray> = downloadRaw(
        path = "/avatars/flags/$code",
        params = buildMap {
            if (width != null) put("width", width)
            if (height != null) put("height", height)
            if (quality != null) put("quality", quality)
        },
    )

    suspend fun getImage(
        url: String,
        width: Int? = null,
        height: Int? = null,
    ): AppwriteResult<ByteArray> = downloadRaw(
        path = "/avatars/image",
        params = buildMap {
            put("url", url)
            if (width != null) put("width", width)
            if (height != null) put("height", height)
        },
    )

    suspend fun getInitials(
        name: String? = null,
        width: Int? = null,
        height: Int? = null,
        background: String? = null,
        color: String? = null,
    ): AppwriteResult<ByteArray> = downloadRaw(
        path = "/avatars/initials",
        params = buildMap {
            if (name != null) put("name", name)
            if (width != null) put("width", width)
            if (height != null) put("height", height)
            if (background != null) put("background", background)
            if (color != null) put("color", color)
        },
    )

    suspend fun getQR(
        text: String,
        size: Int? = null,
        margin: Int? = null,
        download: Boolean? = null,
    ): AppwriteResult<ByteArray> = downloadRaw(
        path = "/avatars/qr",
        params = buildMap {
            put("text", text)
            if (size != null) put("size", size)
            if (margin != null) put("margin", margin)
            if (download != null) put("download", download)
        },
    )
}

/**
 * Extension property: `appwrite.avatars`
 */
val Appwrite.avatars: Avatars get() = Avatars(this)
