package io.appwrite.client

import io.appwrite.client.persistence.SessionStore
import io.appwrite.client.transport.HttpTransport
import io.appwrite.core.types.ProjectId

/**
 * Main entry point for the Appwrite KMP SDK.
 *
 * Usage:
 * ```
 * val appwrite = Appwrite("my-project-id") {
 *     endpoint = "https://my-appwrite.example.com/v1"
 *     logging = true
 * }
 *
 * // Access services
 * appwrite.auth.signInWithEmail(email, password)
 * appwrite.databases[dbId].collections[collId].documents.list { ... }
 * ```
 */
class Appwrite(
    projectId: String,
    configure: AppwriteConfigBuilder.() -> Unit = {},
) {
    val config: AppwriteConfig = AppwriteConfigBuilder()
        .apply(configure)
        .build(ProjectId(projectId))

    val transport = HttpTransport(config)

    /**
     * Optional session store for persisting sessions across app restarts.
     * Set to a [SessionStore] instance to enable persistence, or leave null for in-memory only.
     */
    var sessionStore: SessionStore? = null
        set(value) {
            field = value
            value?.load(SESSION_KEY)?.let { transport.setSession(it) }
        }

    fun setSession(token: String) {
        transport.setSession(token)
        sessionStore?.save(SESSION_KEY, token)
    }

    fun clearSession() {
        transport.clearSession()
        sessionStore?.remove(SESSION_KEY)
    }

    fun setJwt(token: String) {
        transport.setJwt(token)
    }

    fun setLocale(locale: String) {
        transport.setLocale(locale)
    }

    fun close() {
        transport.close()
    }

    private companion object {
        const val SESSION_KEY = "session_token"
    }
}
