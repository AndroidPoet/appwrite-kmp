package io.appwrite.auth

import io.appwrite.client.Appwrite
import io.appwrite.client.ServiceBase
import io.appwrite.core.models.*
import io.appwrite.core.result.AppwriteResult
import io.appwrite.core.types.TargetId
import io.appwrite.core.types.UserId

/**
 * Authentication service — sign-in flows, session management, verification.
 *
 * Scoped by concern:
 * - `auth.signIn*` — create sessions
 * - `auth.signOut*` — destroy sessions
 * - `auth.sessions` — manage existing sessions
 * - `auth.mfa` — multi-factor authentication
 * - `auth.verify` — email/phone verification
 * - `auth.recovery` — password recovery
 */
class Auth(appwrite: Appwrite) : ServiceBase(appwrite.transport) {

    val mfa = Mfa(appwrite)
    val verify = Verify(appwrite)
    val recovery = Recovery(appwrite)

    // ── Sign Up ──────────────────────────────────────────────

    suspend fun signUp(
        userId: UserId = UserId.unique(),
        email: String,
        password: String,
        name: String? = null,
    ): AppwriteResult<User> = post(
        path = "/account",
        params = buildMap {
            put("userId", userId.raw)
            put("email", email)
            put("password", password)
            if (name != null) put("name", name)
        },
    )

    // ── Sign In ──────────────────────────────────────────────

    suspend fun signInWithEmail(
        email: String,
        password: String,
    ): AppwriteResult<Session> = post(
        path = "/account/sessions/email",
        params = mapOf("email" to email, "password" to password),
    )

    suspend fun signInAnonymous(): AppwriteResult<Session> = post(
        path = "/account/sessions/anonymous",
    )

    suspend fun createEmailToken(
        userId: UserId = UserId.unique(),
        email: String,
        phrase: Boolean? = null,
    ): AppwriteResult<Token> = post(
        path = "/account/tokens/email",
        params = buildMap {
            put("userId", userId.raw)
            put("email", email)
            if (phrase != null) put("phrase", phrase)
        },
    )

    suspend fun createPhoneToken(
        userId: UserId = UserId.unique(),
        phone: String,
    ): AppwriteResult<Token> = post(
        path = "/account/tokens/phone",
        params = mapOf("userId" to userId.raw, "phone" to phone),
    )

    suspend fun createMagicUrlToken(
        userId: UserId = UserId.unique(),
        email: String,
        url: String? = null,
        phrase: Boolean? = null,
    ): AppwriteResult<Token> = post(
        path = "/account/tokens/magic-url",
        params = buildMap {
            put("userId", userId.raw)
            put("email", email)
            if (url != null) put("url", url)
            if (phrase != null) put("phrase", phrase)
        },
    )

    suspend fun createSession(
        userId: UserId,
        secret: String,
    ): AppwriteResult<Session> = post(
        path = "/account/sessions/token",
        params = mapOf("userId" to userId.raw, "secret" to secret),
    )

    // ── Sessions ─────────────────────────────────────────────

    suspend fun getSession(
        sessionId: String = "current",
    ): AppwriteResult<Session> = get(path = "/account/sessions/$sessionId")

    suspend fun listSessions(): AppwriteResult<SessionList> =
        get(path = "/account/sessions")

    suspend fun refreshSession(
        sessionId: String = "current",
    ): AppwriteResult<Session> = patch(path = "/account/sessions/$sessionId")

    suspend fun signOut(
        sessionId: String = "current",
    ): AppwriteResult<Unit> = delete(path = "/account/sessions/$sessionId")

    suspend fun signOutAll(): AppwriteResult<Unit> =
        delete(path = "/account/sessions")

    // ── Account Info ─────────────────────────────────────────

    suspend fun getAccount(): AppwriteResult<User> =
        get(path = "/account")

    suspend fun updateName(name: String): AppwriteResult<User> =
        patch(path = "/account/name", params = mapOf("name" to name))

    suspend fun updateEmail(
        email: String,
        password: String,
    ): AppwriteResult<User> = patch(
        path = "/account/email",
        params = mapOf("email" to email, "password" to password),
    )

    suspend fun updatePassword(
        password: String,
        oldPassword: String? = null,
    ): AppwriteResult<User> = patch(
        path = "/account/password",
        params = buildMap {
            put("password", password)
            if (oldPassword != null) put("oldPassword", oldPassword)
        },
    )

    suspend fun updatePhone(
        phone: String,
        password: String,
    ): AppwriteResult<User> = patch(
        path = "/account/phone",
        params = mapOf("phone" to phone, "password" to password),
    )

    suspend fun updateStatus(): AppwriteResult<User> =
        patch(path = "/account/status")

    // ── Preferences ──────────────────────────────────────────

    suspend fun getPrefs(): AppwriteResult<Map<String, String>> =
        get(path = "/account/prefs")

    suspend fun updatePrefs(
        prefs: Map<String, String>,
    ): AppwriteResult<User> = patch(
        path = "/account/prefs",
        params = mapOf("prefs" to prefs),
    )

    // ── Identity ─────────────────────────────────────────────

    suspend fun listIdentities(
        queries: List<String>? = null,
    ): AppwriteResult<IdentityList> = get(
        path = "/account/identities",
        params = buildMap {
            if (queries != null) put("queries", queries)
        },
    )

    suspend fun deleteIdentity(
        identityId: String,
    ): AppwriteResult<Unit> = delete(path = "/account/identities/$identityId")

    // ── JWT ──────────────────────────────────────────────────

    suspend fun createJwt(): AppwriteResult<Jwt> =
        post(path = "/account/jwts")

    // ── Logs ─────────────────────────────────────────────────

    suspend fun listLogs(
        queries: List<String>? = null,
    ): AppwriteResult<LogList> = get(
        path = "/account/logs",
        params = buildMap {
            if (queries != null) put("queries", queries)
        },
    )

    // ── Push Targets ─────────────────────────────────────────

    suspend fun createPushTarget(
        targetId: TargetId = TargetId.unique(),
        identifier: String,
        providerId: String? = null,
    ): AppwriteResult<Target> = post(
        path = "/account/targets",
        params = buildMap {
            put("targetId", targetId.raw)
            put("identifier", identifier)
            if (providerId != null) put("providerId", providerId)
        },
    )

    suspend fun updatePushTarget(
        targetId: TargetId,
        identifier: String,
    ): AppwriteResult<Target> = put(
        path = "/account/targets/${targetId.raw}",
        params = mapOf("identifier" to identifier),
    )

    suspend fun deletePushTarget(
        targetId: TargetId,
    ): AppwriteResult<Unit> = delete(path = "/account/targets/${targetId.raw}")
}
