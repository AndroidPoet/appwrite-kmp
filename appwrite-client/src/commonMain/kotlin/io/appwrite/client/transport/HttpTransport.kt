package io.appwrite.client.transport

import io.appwrite.client.AppwriteConfig
import io.appwrite.core.result.AppwriteError
import io.appwrite.core.result.AppwriteException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class HttpTransport constructor(val config: AppwriteConfig) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        coerceInputValues = true
    }

    @PublishedApi
    internal val client = HttpClient {
        install(ContentNegotiation) {
            json(this@HttpTransport.json)
        }

        if (config.logging) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.BODY
            }
        }

        defaultRequest {
            url(config.endpoint)
            header("x-appwrite-project", config.projectId.raw)
            header("x-appwrite-response-format", "1.8.0")
            header("x-sdk-name", "appwrite-kmp")
            header("x-sdk-platform", "kmp")
            header("x-sdk-language", "kotlin")
            header("x-sdk-version", "0.1.0")
            contentType(ContentType.Application.Json)
        }
    }

    @PublishedApi
    internal val customHeaders = mutableMapOf<String, String>()

    fun addHeader(key: String, value: String) {
        customHeaders[key] = value
    }

    fun setSession(token: String) {
        customHeaders["x-appwrite-session"] = token
    }

    fun clearSession() {
        customHeaders.remove("x-appwrite-session")
    }

    fun setJwt(token: String) {
        customHeaders["x-appwrite-jwt"] = token
    }

    fun setLocale(locale: String) {
        customHeaders["x-appwrite-locale"] = locale
    }

    @PublishedApi
    internal suspend fun executeRequest(
        method: HttpMethod,
        path: String,
        params: Map<String, Any?>,
    ): HttpResponse = client.request {
        this.method = method.toKtor()

        url {
            // takeFrom übernimmt Schema, Host UND den Basis-Pfad (z. B. /v1) aus dem Endpoint.
            // Ohne das verliert appendPathSegments das /v1 -> Server antwortet 404.
            takeFrom(config.endpoint)
            appendPathSegments(path.removePrefix("/"))

            if (method == HttpMethod.GET) {
                params.forEach { entry ->
                    when (val value = entry.value) {
                        is List<*> -> value.forEach { item ->
                            parameter("${entry.key}[]", item.toString())
                        }
                        null -> {}
                        else -> parameter(entry.key, value.toString())
                    }
                }
            }
        }

        customHeaders.forEach { entry -> header(entry.key, entry.value) }

        if (method != HttpMethod.GET && params.isNotEmpty()) {
            // Als JsonObject senden statt als roher Map<String, Any?>: kotlinx.serialization
            // kann eine heterogen typisierte Map (String + Boolean + Map ...) nicht direkt
            // serialisieren ("Serializing collections of different element types").
            setBody(params.toJsonBody())
        }
    }

    @PublishedApi
    internal suspend fun handleErrorResponse(response: HttpResponse) {
        val body = try {
            response.body<JsonObject>()
        } catch (_: Exception) {
            null
        }
        throw AppwriteException(
            AppwriteError(
                message = body?.get("message")?.jsonPrimitive?.content
                    ?: "Unknown error (${response.status.value})",
                code = response.status.value,
                type = body?.get("type")?.jsonPrimitive?.content ?: "unknown",
            )
        )
    }


    var onSessionUpdated: ((String) -> Unit)? = null
    @PublishedApi
    internal suspend inline fun <reified T> call(
        method: HttpMethod,
        path: String,
        params: Map<String, Any?> = emptyMap(),
    ): T {
        val response = executeRequest(method, path, params)
        if (!response.status.isSuccess()) {
            handleErrorResponse(response)
        }

        // --- START DES NEUEN CODES ---
        // Nach jedem Request prüfen wir, ob Appwrite uns ein Session-Cookie mitgeschickt hat.
        val cookies = response.setCookie()
        // Der Cookie heißt bei Appwrite "a_session_" gefolgt von der Projekt-ID
        val sessionCookie = cookies.find {
            it.name.startsWith("a_session_${config.projectId.raw}")
        }

        if (sessionCookie != null) {
            val token = sessionCookie.value
            // 1. Token für den aktuellen Laufzeit-Client im Header speichern
            setSession(token)
            // 2. Token an die Appwrite-Klasse melden, damit er ins SharedPreferences/SessionStore geschrieben wird
            onSessionUpdated?.invoke(token)
        }
        // --- ENDE DES NEUEN CODES ---

        return response.body()
    }

    suspend fun callRaw(
        method: HttpMethod,
        path: String,
        params: Map<String, Any?> = emptyMap(),
    ): ByteArray {
        val response = executeRequest(method, path, params)
        if (!response.status.isSuccess()) {
            handleErrorResponse(response)
        }
        return response.body()
    }

    suspend inline fun <reified T> uploadChunk(
        path: String,
        params: Map<String, String>,
        fileParamName: String,
        fileName: String,
        mimeType: String,
        chunk: ByteArray,
        contentRange: String,
        extraHeaders: Map<String, String> = emptyMap(),
    ): T {
        val response = client.submitFormWithBinaryData(
            formData = formData {
                params.forEach { (key, value) ->
                    append(key, value)
                }
                append(fileParamName, chunk, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                    append(HttpHeaders.ContentType, mimeType)
                })
            },
        ) {
            url {
                takeFrom(config.endpoint)
                appendPathSegments(path.removePrefix("/"))
            }
            method = io.ktor.http.HttpMethod.Post
            header(HttpHeaders.ContentRange, contentRange)
            customHeaders.forEach { entry -> header(entry.key, entry.value) }
            extraHeaders.forEach { (key, value) -> header(key, value) }
        }
        if (!response.status.isSuccess()) {
            handleErrorResponse(response)
        }
        return response.body()
    }

    fun close() {
        client.close()
    }
}

/** Baut aus einer gemischt-typisierten Param-Map ein JsonObject (null-Werte werden ausgelassen). */
private fun Map<String, Any?>.toJsonBody(): JsonObject = buildJsonObject {
    for ((key, value) in this@toJsonBody) {
        if (value != null) put(key, value.toJsonElement())
    }
}

private fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is JsonElement -> this
    is String -> JsonPrimitive(this)
    is Boolean -> JsonPrimitive(this)
    is Number -> JsonPrimitive(this)
    is Map<*, *> -> buildJsonObject {
        for ((k, v) in this@toJsonElement) {
            if (k != null) put(k.toString(), v.toJsonElement())
        }
    }
    is Iterable<*> -> buildJsonArray {
        for (item in this@toJsonElement) add(item.toJsonElement())
    }
    else -> JsonPrimitive(toString())
}

@PublishedApi
internal fun HttpMethod.toKtor(): io.ktor.http.HttpMethod = when (this) {
    HttpMethod.GET -> io.ktor.http.HttpMethod.Get
    HttpMethod.POST -> io.ktor.http.HttpMethod.Post
    HttpMethod.PUT -> io.ktor.http.HttpMethod.Put
    HttpMethod.PATCH -> io.ktor.http.HttpMethod.Patch
    HttpMethod.DELETE -> io.ktor.http.HttpMethod.Delete
}
