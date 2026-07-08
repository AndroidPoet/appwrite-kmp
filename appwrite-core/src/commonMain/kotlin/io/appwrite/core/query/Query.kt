package io.appwrite.core.query

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Type-safe query builder for Appwrite.
 *
 * Encodes queries in the modern Appwrite JSON format
 * (`{"method":"equal","attribute":"x","values":[...]}`) — the legacy string
 * syntax (`equal("x", [...])`) is rejected by current servers with
 * "Invalid query: Syntax error".
 *
 * Usage:
 * ```
 * val queries = buildQuery {
 *     where("age" greaterThan 18)
 *     where("status" oneOf listOf("active", "pending"))
 *     orderBy("createdAt", descending = true)
 *     limit(25)
 *     offset(10)
 *     select("name", "email")
 * }
 * ```
 */
class QueryBuilder {
    private val queries = mutableListOf<String>()

    fun where(condition: QueryCondition) {
        queries.add(condition.encode())
    }

    fun orderBy(attribute: String, descending: Boolean = false) {
        val method = if (descending) "orderDesc" else "orderAsc"
        queries.add(encodeQuery(method, attribute = attribute))
    }

    fun limit(count: Int) {
        queries.add(encodeQuery("limit", values = listOf(count)))
    }

    fun offset(count: Int) {
        queries.add(encodeQuery("offset", values = listOf(count)))
    }

    fun cursorAfter(documentId: String) {
        queries.add(encodeQuery("cursorAfter", values = listOf(documentId)))
    }

    fun cursorBefore(documentId: String) {
        queries.add(encodeQuery("cursorBefore", values = listOf(documentId)))
    }

    fun select(vararg attributes: String) {
        queries.add(encodeQuery("select", values = attributes.toList()))
    }

    fun build(): List<String> = queries.toList()
}

sealed class QueryCondition {
    abstract fun encode(): String
}

// Infix builders for clean DSL syntax
infix fun String.equal(value: Any): QueryCondition = ComparisonCondition("equal", this, value)
infix fun String.notEqual(value: Any): QueryCondition = ComparisonCondition("notEqual", this, value)
infix fun String.greaterThan(value: Any): QueryCondition = ComparisonCondition("greaterThan", this, value)
infix fun String.greaterThanEqual(value: Any): QueryCondition = ComparisonCondition("greaterThanEqual", this, value)
infix fun String.lessThan(value: Any): QueryCondition = ComparisonCondition("lessThan", this, value)
infix fun String.lessThanEqual(value: Any): QueryCondition = ComparisonCondition("lessThanEqual", this, value)
infix fun String.contains(value: String): QueryCondition = ComparisonCondition("contains", this, value)
infix fun String.startsWith(value: String): QueryCondition = ComparisonCondition("startsWith", this, value)
infix fun String.endsWith(value: String): QueryCondition = ComparisonCondition("endsWith", this, value)
infix fun String.oneOf(values: List<Any>): QueryCondition = ArrayCondition("equal", this, values)
infix fun String.between(range: Pair<Any, Any>): QueryCondition = BetweenCondition(this, range.first, range.second)

fun String.isNull(): QueryCondition = NullCondition("isNull", this)
fun String.isNotNull(): QueryCondition = NullCondition("isNotNull", this)

fun search(attribute: String, query: String): QueryCondition = ComparisonCondition("search", attribute, query)

private class ComparisonCondition(
    private val method: String,
    private val attribute: String,
    private val value: Any,
) : QueryCondition() {
    override fun encode(): String = encodeQuery(method, attribute = attribute, values = listOf(value))
}

private class ArrayCondition(
    private val method: String,
    private val attribute: String,
    private val values: List<Any>,
) : QueryCondition() {
    override fun encode(): String = encodeQuery(method, attribute = attribute, values = values)
}

private class BetweenCondition(
    private val attribute: String,
    private val start: Any,
    private val end: Any,
) : QueryCondition() {
    override fun encode(): String = encodeQuery("between", attribute = attribute, values = listOf(start, end))
}

private class NullCondition(
    private val method: String,
    private val attribute: String,
) : QueryCondition() {
    override fun encode(): String = encodeQuery(method, attribute = attribute)
}

/** Builds the Appwrite query JSON; kotlinx.serialization handles the escaping. */
private fun encodeQuery(method: String, attribute: String? = null, values: List<Any>? = null): String {
    val fields = buildMap<String, JsonElement> {
        put("method", JsonPrimitive(method))
        if (attribute != null) put("attribute", JsonPrimitive(attribute))
        if (values != null) put("values", JsonArray(values.map { it.toJsonPrimitive() }))
    }
    return JsonObject(fields).toString()
}

private fun Any.toJsonPrimitive(): JsonPrimitive = when (this) {
    is Boolean -> JsonPrimitive(this)
    is Number -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    else -> JsonPrimitive(toString())
}

fun buildQuery(block: QueryBuilder.() -> Unit): List<String> =
    QueryBuilder().apply(block).build()
