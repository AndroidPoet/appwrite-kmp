package io.appwrite.functions

import io.appwrite.client.Appwrite
import io.appwrite.client.ServiceBase
import io.appwrite.core.models.Execution
import io.appwrite.core.models.ExecutionList
import io.appwrite.core.query.QueryBuilder
import io.appwrite.core.query.buildQuery
import io.appwrite.core.result.AppwriteResult
import io.appwrite.core.types.ExecutionId
import io.appwrite.core.types.FunctionId

/**
 * Functions service — list and create executions.
 *
 * Usage:
 * ```
 * val functions = appwrite.functions
 *
 * // Execute a function
 * functions.createExecution(FunctionId("my-func"), body = """{"key":"value"}""")
 *
 * // List executions
 * functions.listExecutions(FunctionId("my-func")) { limit(25) }
 * ```
 */
class Functions(appwrite: Appwrite) : ServiceBase(appwrite.transport) {

    suspend fun listExecutions(
        functionId: FunctionId,
        query: (QueryBuilder.() -> Unit)? = null,
    ): AppwriteResult<ExecutionList> = get(
        path = "/functions/${functionId.raw}/executions",
        params = buildMap {
            if (query != null) put("queries", buildQuery(query))
        },
    )

    suspend fun createExecution(
        functionId: FunctionId,
        body: String? = null,
        async: Boolean? = null,
        path: String? = null,
        method: String? = null,
        headers: Map<String, String>? = null,
        scheduledAt: String? = null,
    ): AppwriteResult<Execution> = post(
        path = "/functions/${functionId.raw}/executions",
        params = buildMap {
            if (body != null) put("body", body)
            if (async != null) put("async", async)
            if (path != null) put("path", path)
            if (method != null) put("method", method)
            if (headers != null) put("headers", headers)
            if (scheduledAt != null) put("scheduledAt", scheduledAt)
        },
    )

    suspend fun getExecution(
        functionId: FunctionId,
        executionId: ExecutionId,
    ): AppwriteResult<Execution> =
        get(path = "/functions/${functionId.raw}/executions/${executionId.raw}")
}

/**
 * Extension property: `appwrite.functions`
 */
val Appwrite.functions: Functions get() = Functions(this)
