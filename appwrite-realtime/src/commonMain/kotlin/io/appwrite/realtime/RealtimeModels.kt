package io.appwrite.realtime

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Public event emitted to subscribers.
 */
@Serializable
data class RealtimeEvent(
    val events: List<String>,
    val channels: List<String>,
    val timestamp: String,
    val payload: JsonObject,
)

// ── Internal wire-format models ──────────────────────────────────────

/**
 * Top-level JSON envelope from the Appwrite realtime server.
 *
 * ```json
 * { "type": "event", "data": { ... } }
 * ```
 */
@Serializable
internal data class RealtimeMessage(
    val type: String,
    val data: JsonObject,
)

/**
 * Payload of a `"connected"` message.
 *
 * ```json
 * { "channels": ["account", "databases.main.collections.users.documents"], "user": null }
 * ```
 */
@Serializable
internal data class RealtimeConnectionData(
    val channels: List<String> = emptyList(),
    val user: JsonObject? = null,
)

/**
 * Deserialized form of a `"type": "error"` message data.
 */
@Serializable
internal data class RealtimeErrorData(
    val code: Int = 0,
    val message: String = "",
)

/**
 * Thrown into a realtime [Flow] when the server emits a `"type":"error"`
 * message (for example an invalid channel or an unauthorized subscription).
 */
public class RealtimeException(
    public val code: Int,
    message: String,
) : Exception(message)
