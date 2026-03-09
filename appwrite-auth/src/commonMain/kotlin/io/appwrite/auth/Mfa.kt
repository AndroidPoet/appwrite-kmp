package io.appwrite.auth

import io.appwrite.client.Appwrite
import io.appwrite.client.ServiceBase
import io.appwrite.core.models.*
import io.appwrite.core.result.AppwriteResult

/**
 * Multi-factor authentication management.
 *
 * Usage:
 * ```
 * // Enable MFA
 * auth.mfa.enable()
 *
 * // Set up TOTP authenticator
 * val setup = auth.mfa.createAuthenticator(AuthenticationFactor.Totp)
 *
 * // Verify with OTP from authenticator app
 * auth.mfa.verifyAuthenticator(AuthenticationFactor.Totp, otp = "123456")
 *
 * // During login, complete MFA challenge
 * val challenge = auth.mfa.createChallenge(AuthenticationFactor.Totp)
 * auth.mfa.verifyChallenge(challenge.data.id, otp = "123456")
 * ```
 */
class Mfa(appwrite: Appwrite) : ServiceBase(appwrite.transport) {

    suspend fun enable(): AppwriteResult<User> =
        patch(path = "/account/mfa", params = mapOf("mfa" to true))

    suspend fun disable(): AppwriteResult<User> =
        patch(path = "/account/mfa", params = mapOf("mfa" to false))

    suspend fun listFactors(): AppwriteResult<MfaFactors> =
        get(path = "/account/mfa/factors")

    suspend fun createAuthenticator(
        type: AuthenticationFactor = AuthenticationFactor.Totp,
    ): AppwriteResult<MfaChallenge> = post(
        path = "/account/mfa/authenticators/${type.value}",
    )

    suspend fun verifyAuthenticator(
        type: AuthenticationFactor = AuthenticationFactor.Totp,
        otp: String,
    ): AppwriteResult<User> = put(
        path = "/account/mfa/authenticators/${type.value}",
        params = mapOf("otp" to otp),
    )

    suspend fun deleteAuthenticator(
        type: AuthenticationFactor = AuthenticationFactor.Totp,
    ): AppwriteResult<Unit> = delete(
        path = "/account/mfa/authenticators/${type.value}",
    )

    suspend fun createChallenge(
        factor: AuthenticationFactor,
    ): AppwriteResult<MfaChallenge> = post(
        path = "/account/mfa/challenge",
        params = mapOf("factor" to factor.value),
    )

    suspend fun verifyChallenge(
        challengeId: String,
        otp: String,
    ): AppwriteResult<Session> = put(
        path = "/account/mfa/challenge",
        params = mapOf("challengeId" to challengeId, "otp" to otp),
    )

    suspend fun getRecoveryCodes(): AppwriteResult<MfaRecoveryCodes> =
        get(path = "/account/mfa/recovery-codes")

    suspend fun createRecoveryCodes(): AppwriteResult<MfaRecoveryCodes> =
        post(path = "/account/mfa/recovery-codes")

    suspend fun regenerateRecoveryCodes(): AppwriteResult<MfaRecoveryCodes> =
        patch(path = "/account/mfa/recovery-codes")
}
