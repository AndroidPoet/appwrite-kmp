package io.appwrite.client

import io.appwrite.client.transport.HttpMethod
import io.appwrite.client.transport.HttpTransport
import io.appwrite.core.result.AppwriteResult

/**
 * Base class for all service implementations.
 * Provides safe HTTP call wrappers that return [AppwriteResult].
 */
abstract class ServiceBase(@PublishedApi internal val transport: HttpTransport) {

    protected suspend inline fun <reified T> get(
        path: String,
        params: Map<String, Any?> = emptyMap(),
    ): AppwriteResult<T> = AppwriteResult.catching {
        transport.call<T>(HttpMethod.GET, path, params)
    }

    protected suspend inline fun <reified T> post(
        path: String,
        params: Map<String, Any?> = emptyMap(),
    ): AppwriteResult<T> = AppwriteResult.catching {
        transport.call<T>(HttpMethod.POST, path, params)
    }

    protected suspend inline fun <reified T> put(
        path: String,
        params: Map<String, Any?> = emptyMap(),
    ): AppwriteResult<T> = AppwriteResult.catching {
        transport.call<T>(HttpMethod.PUT, path, params)
    }

    protected suspend inline fun <reified T> patch(
        path: String,
        params: Map<String, Any?> = emptyMap(),
    ): AppwriteResult<T> = AppwriteResult.catching {
        transport.call<T>(HttpMethod.PATCH, path, params)
    }

    protected suspend inline fun <reified T> delete(
        path: String,
        params: Map<String, Any?> = emptyMap(),
    ): AppwriteResult<T> = AppwriteResult.catching {
        transport.call<T>(HttpMethod.DELETE, path, params)
    }

    protected suspend fun downloadRaw(
        path: String,
        params: Map<String, Any?> = emptyMap(),
    ): AppwriteResult<ByteArray> = AppwriteResult.catching {
        transport.callRaw(HttpMethod.GET, path, params)
    }

    protected suspend inline fun <reified T> uploadChunk(
        path: String,
        params: Map<String, String>,
        fileParamName: String,
        fileName: String,
        mimeType: String,
        chunk: ByteArray,
        contentRange: String,
        extraHeaders: Map<String, String> = emptyMap(),
    ): AppwriteResult<T> = AppwriteResult.catching {
        transport.uploadChunk<T>(
            path = path,
            params = params,
            fileParamName = fileParamName,
            fileName = fileName,
            mimeType = mimeType,
            chunk = chunk,
            contentRange = contentRange,
            extraHeaders = extraHeaders,
        )
    }
}
