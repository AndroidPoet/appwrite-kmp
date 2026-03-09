package io.appwrite.auth

import io.appwrite.client.Appwrite
import io.appwrite.client.ServiceBase
import io.appwrite.core.models.Token
import io.appwrite.core.models.User
import io.appwrite.core.result.AppwriteResult

/**
 * Email and phone verification flows.
 */
class Verify(appwrite: Appwrite) : ServiceBase(appwrite.transport) {

    suspend fun createEmailVerification(
        url: String,
    ): AppwriteResult<Token> = post(
        path = "/account/verification",
        params = mapOf("url" to url),
    )

    suspend fun confirmEmailVerification(
        userId: String,
        secret: String,
    ): AppwriteResult<Token> = put(
        path = "/account/verification",
        params = mapOf("userId" to userId, "secret" to secret),
    )

    suspend fun createPhoneVerification(): AppwriteResult<Token> =
        post(path = "/account/verification/phone")

    suspend fun confirmPhoneVerification(
        userId: String,
        secret: String,
    ): AppwriteResult<Token> = put(
        path = "/account/verification/phone",
        params = mapOf("userId" to userId, "secret" to secret),
    )
}
