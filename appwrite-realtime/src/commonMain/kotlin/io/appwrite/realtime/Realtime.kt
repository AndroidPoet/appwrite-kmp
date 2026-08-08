package io.appwrite.realtime

import io.appwrite.client.Appwrite
import io.appwrite.core.types.BucketId
import io.appwrite.core.types.CollectionId
import io.appwrite.core.types.DatabaseId
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/**
 * Realtime subscription service backed by Appwrite's WebSocket protocol.
 *
 * Every call to [subscribe] (or the convenience helpers) returns a **cold** [Flow]
 * that opens a WebSocket connection when collected and tears it down on cancellation.
 *
 * Usage:
 * ```
 * appwrite.realtime
 *     .documents(DatabaseId("main"), CollectionId("users"))
 *     .onEach { event -> updateUI(event) }
 *     .launchIn(viewModelScope)
 *
 * appwrite.realtime
 *     .subscribe("account")
 *     .collect { event -> handleEvent(event) }
 * ```
 */
class Realtime(
    private val appwrite: Appwrite,
) {
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }

    // ── Public API ───────────────────────────────────────────────────

    /**
     * Subscribe to one or more Appwrite realtime channels.
     *
     * The returned flow is **cold**: a WebSocket connection is opened when
     * collection starts and closed when the collector cancels. Automatic
     * reconnection with exponential back-off is built in.
     */
    fun subscribe(vararg channels: String): Flow<RealtimeEvent> =
        callbackFlow {
            require(channels.isNotEmpty()) { "At least one channel is required" }

            val client = HttpClient { install(WebSockets) }
            val wsUrl = buildWsUrl(channels.toList())

            var backoff = INITIAL_BACKOFF_MS
            var fatal: RealtimeException? = null

            try {
                while (isActive && fatal == null) {
                    try {
                        client.webSocket(wsUrl) {
                            // Connection succeeded — reset back-off.
                            backoff = INITIAL_BACKOFF_MS

                            // Heartbeat: send ping every 20 seconds.
                            val heartbeat =
                                launch {
                                    while (isActive) {
                                        delay(HEARTBEAT_INTERVAL_MS)
                                        send(Frame.Text("""{"type":"ping"}"""))
                                    }
                                }

                            try {
                                for (frame in incoming) {
                                    if (frame is Frame.Text) {
                                        handleFrame(frame.readText())?.let { event ->
                                            trySend(event)
                                        }
                                    }
                                }
                            } catch (e: RealtimeException) {
                                // Server-reported error (bad channel, unauthorized) —
                                // surface to the collector and stop reconnecting.
                                fatal = e
                                return@webSocket
                            } finally {
                                heartbeat.cancel()
                            }
                        }
                        // Server closed the connection gracefully — reconnect.
                    } catch (_: CancellationException) {
                        throw CancellationException("Flow cancelled")
                    } catch (_: Exception) {
                        // Connection failed or dropped — back off and retry.
                    }

                    if (!isActive || fatal != null) break
                    delay(backoff)
                    backoff = (backoff * 2).coerceAtMost(MAX_BACKOFF_MS)
                }
            } finally {
                client.close()
            }

            // Close the flow: surface a server error, or complete normally on cancel.
            close(fatal)

            awaitClose { client.close() }
        }

    /** Subscribe to document changes in a collection. */
    fun documents(
        databaseId: DatabaseId,
        collectionId: CollectionId,
    ): Flow<RealtimeEvent> = subscribe(RealtimeChannels.documents(databaseId, collectionId))

    /** Subscribe to account-level events (session changes, preferences, etc.). */
    fun account(): Flow<RealtimeEvent> = subscribe(RealtimeChannels.ACCOUNT)

    /** Subscribe to file events in a storage bucket. */
    fun files(bucketId: BucketId): Flow<RealtimeEvent> =
        subscribe(RealtimeChannels.files(bucketId))

    /** Subscribe to file events in a storage bucket (string overload). */
    fun files(bucketId: String): Flow<RealtimeEvent> =
        subscribe("buckets.$bucketId.files")

    /** Subscribe to events for a single team. */
    fun team(teamId: io.appwrite.core.types.TeamId): Flow<RealtimeEvent> =
        subscribe(RealtimeChannels.team(teamId))

    /** Subscribe to execution events for a single function. */
    fun executions(functionId: io.appwrite.core.types.FunctionId): Flow<RealtimeEvent> =
        subscribe(RealtimeChannels.executions(functionId))

    // ── Internals ────────────────────────────────────────────────────

    /**
     * Build the full WebSocket URL from the REST endpoint.
     *
     * `https://cloud.appwrite.io/v1` → `wss://cloud.appwrite.io/v1/realtime?project=…&channels[]=…`
     */
    private fun buildWsUrl(channels: List<String>): String {
        val endpoint = appwrite.config.endpoint.trimEnd('/')
        val wsBase =
            endpoint
                .replace("https://", "wss://")
                .replace("http://", "ws://")

        val params =
            buildString {
                append("project=${appwrite.config.projectId.raw}")
                channels.forEach { ch ->
                    append("&channels[]=$ch")
                }
            }

        return "$wsBase/realtime?$params"
    }

    /**
     * Parse a text frame and return a [RealtimeEvent] if the message
     * is of type `"event"`, or `null` for connection/heartbeat control
     * messages. Throws [RealtimeException] for `"error"` messages.
     */
    private fun handleFrame(text: String): RealtimeEvent? {
        val message =
            try {
                json.decodeFromString<RealtimeMessage>(text)
            } catch (_: Exception) {
                // Unrecognised payload — ignore rather than tear down the stream.
                return null
            }
        return when (message.type) {
            "event" ->
                runCatching {
                    json.decodeFromString(RealtimeEvent.serializer(), message.data.toString())
                }.getOrNull()

            "error" -> {
                val error =
                    runCatching {
                        json.decodeFromString(RealtimeErrorData.serializer(), message.data.toString())
                    }.getOrNull()
                throw RealtimeException(
                    code = error?.code ?: 0,
                    message = error?.message ?: "Realtime connection error",
                )
            }

            else -> null // "connected", "pong", "response" — not surfaced to subscribers
        }
    }

    companion object {
        private const val HEARTBEAT_INTERVAL_MS = 20_000L
        private const val INITIAL_BACKOFF_MS = 1_000L
        private const val MAX_BACKOFF_MS = 60_000L
    }
}

/**
 * Extension property: `appwrite.realtime`
 */
val Appwrite.realtime: Realtime get() = Realtime(this)
