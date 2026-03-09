package io.appwrite.auth

import io.appwrite.client.Appwrite
import io.appwrite.client.ServiceBase
import io.appwrite.core.models.Token
import io.appwrite.core.result.AppwriteResult

/**
 * Password recovery flow.
 */
class Recovery(appwrite: Appwrite) : ServiceBase(appwrite.transport) {

    suspend fun createRecovery(
        email: String,
        url: String,
    ): AppwriteResult<Token> = post(
        path = "/account/recovery",
        params = mapOf("email" to email, "url" to url),
    )

    suspend fun confirmRecovery(
        userId: String,
        secret: String,
        password: String,
    ): AppwriteResult<Token> = put(
        path = "/account/recovery",
        params = mapOf(
            "userId" to userId,
            "secret" to secret,
            "password" to password,
        ),
    )
}
