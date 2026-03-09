package io.appwrite.core.query

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

/**
 * Type-safe query builder for Appwrite.
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
        queries.add("""$method("$attribute")""")
    }

    fun limit(count: Int) {
        queries.add("""limit($count)""")
    }

    fun offset(count: Int) {
        queries.add("""offset($count)""")
    }

    fun cursorAfter(documentId: String) {
        queries.add("""cursorAfter("$documentId")""")
    }

    fun cursorBefore(documentId: String) {
        queries.add("""cursorBefore("$documentId")""")
    }

    fun select(vararg attributes: String) {
        val attrs = attributes.joinToString(",") { "\"$it\"" }
        queries.add("select([$attrs])")
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
    override fun encode(): String {
        val encoded = encodeValue(value)
        return """$method("$attribute", [$encoded])"""
    }
}

private class ArrayCondition(
    private val method: String,
    private val attribute: String,
    private val values: List<Any>,
) : QueryCondition() {
    override fun encode(): String {
        val encoded = values.joinToString(",") { encodeValue(it) }
        return """$method("$attribute", [$encoded])"""
    }
}

private class BetweenCondition(
    private val attribute: String,
    private val start: Any,
    private val end: Any,
) : QueryCondition() {
    override fun encode(): String {
        return """between("$attribute", ${encodeValue(start)}, ${encodeValue(end)})"""
    }
}

private class NullCondition(
    private val method: String,
    private val attribute: String,
) : QueryCondition() {
    override fun encode(): String = """$method("$attribute")"""
}

private fun encodeValue(value: Any): String = when (value) {
    is String -> "\"$value\""
    is Boolean -> value.toString()
    is Number -> value.toString()
    else -> "\"$value\""
}

fun buildQuery(block: QueryBuilder.() -> Unit): List<String> =
    QueryBuilder().apply(block).build()
