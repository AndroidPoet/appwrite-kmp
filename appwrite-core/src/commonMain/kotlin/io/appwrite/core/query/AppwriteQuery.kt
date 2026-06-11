package io.appwrite.core.query

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Static query builder that mirrors the Appwrite Web/JS SDK `Query` class.
 *
 * Each helper returns a JSON-encoded string in the modern Appwrite wire format:
 *
 * ```json
 * {"method":"equal","attribute":"name","values":["Alice"]}
 * ```
 *
 * This is the format required by the `x-appwrite-response-format: 1.8.0`
 * endpoints. Usage mirrors the JS SDK one-to-one:
 *
 * ```
 * databases.listDocuments(db, col, queries = listOf(
 *     Query.equal("status", "active"),
 *     Query.greaterThan("age", 18),
 *     Query.orderDesc("createdAt"),
 *     Query.limit(25),
 * ))
 * ```
 *
 * For a type-safe Kotlin DSL alternative see [QueryBuilder] / [buildQuery].
 */
public object Query {

    private val json: Json = Json

    // ── Comparison ───────────────────────────────────────────────────

    public fun equal(attribute: String, value: Any): String = encode("equal", attribute, value)

    public fun equal(attribute: String, values: List<Any>): String = encode("equal", attribute, values)

    public fun notEqual(attribute: String, value: Any): String = encode("notEqual", attribute, value)

    public fun lessThan(attribute: String, value: Any): String = encode("lessThan", attribute, value)

    public fun lessThanEqual(attribute: String, value: Any): String = encode("lessThanEqual", attribute, value)

    public fun greaterThan(attribute: String, value: Any): String = encode("greaterThan", attribute, value)

    public fun greaterThanEqual(attribute: String, value: Any): String = encode("greaterThanEqual", attribute, value)

    public fun between(attribute: String, start: Any, end: Any): String =
        encode("between", attribute, listOf(start, end))

    public fun notBetween(attribute: String, start: Any, end: Any): String =
        encode("notBetween", attribute, listOf(start, end))

    // ── Null checks ──────────────────────────────────────────────────

    public fun isNull(attribute: String): String = encode("isNull", attribute, null)

    public fun isNotNull(attribute: String): String = encode("isNotNull", attribute, null)

    // ── String operations ────────────────────────────────────────────

    public fun startsWith(attribute: String, value: String): String = encode("startsWith", attribute, value)

    public fun notStartsWith(attribute: String, value: String): String = encode("notStartsWith", attribute, value)

    public fun endsWith(attribute: String, value: String): String = encode("endsWith", attribute, value)

    public fun notEndsWith(attribute: String, value: String): String = encode("notEndsWith", attribute, value)

    public fun contains(attribute: String, value: String): String = encode("contains", attribute, value)

    public fun contains(attribute: String, values: List<Any>): String = encode("contains", attribute, values)

    public fun notContains(attribute: String, value: String): String = encode("notContains", attribute, value)

    public fun notContains(attribute: String, values: List<Any>): String = encode("notContains", attribute, values)

    public fun search(attribute: String, value: String): String = encode("search", attribute, value)

    public fun notSearch(attribute: String, value: String): String = encode("notSearch", attribute, value)

    // ── Selection / ordering ─────────────────────────────────────────

    public fun select(attributes: List<String>): String = encode("select", null, attributes)

    public fun select(vararg attributes: String): String = select(attributes.toList())

    public fun orderAsc(attribute: String): String = encode("orderAsc", attribute, null)

    public fun orderDesc(attribute: String): String = encode("orderDesc", attribute, null)

    public fun orderRandom(): String = encode("orderRandom", null, null)

    // ── Pagination ───────────────────────────────────────────────────

    public fun cursorAfter(documentId: String): String = encode("cursorAfter", null, documentId)

    public fun cursorBefore(documentId: String): String = encode("cursorBefore", null, documentId)

    public fun limit(limit: Int): String = encode("limit", null, limit)

    public fun offset(offset: Int): String = encode("offset", null, offset)

    // ── Time helpers ─────────────────────────────────────────────────

    public fun createdBefore(value: String): String = encode("createdBefore", null, value)

    public fun createdAfter(value: String): String = encode("createdAfter", null, value)

    public fun updatedBefore(value: String): String = encode("updatedBefore", null, value)

    public fun updatedAfter(value: String): String = encode("updatedAfter", null, value)

    // ── Logical operators ────────────────────────────────────────────

    /** Combine sub-queries (already-encoded JSON strings) with logical AND. */
    public fun and(queries: List<String>): String = encodeNested("and", queries)

    /** Combine sub-queries (already-encoded JSON strings) with logical OR. */
    public fun or(queries: List<String>): String = encodeNested("or", queries)

    // ── Internals ────────────────────────────────────────────────────

    private fun encode(method: String, attribute: String?, value: Any?): String {
        val obj = buildJsonObject {
            put("method", method)
            if (attribute != null) put("attribute", attribute)
            if (value != null) {
                put("values", valuesArray(value))
            }
        }
        return json.encodeToString(JsonObject.serializer(), obj)
    }

    private fun encodeNested(method: String, queries: List<String>): String {
        val obj = buildJsonObject {
            put("method", method)
            put(
                "values",
                buildJsonArray {
                    queries.forEach { q -> add(json.parseToJsonElement(q)) }
                },
            )
        }
        return json.encodeToString(JsonObject.serializer(), obj)
    }

    /** Normalise a single value or a list into the `values` JSON array. */
    private fun valuesArray(value: Any): JsonArray = when (value) {
        is List<*> -> buildJsonArray { value.forEach { add(toElement(it)) } }
        else -> buildJsonArray { add(toElement(value)) }
    }

    private fun toElement(value: Any?): JsonElement = when (value) {
        null -> JsonNull
        is JsonElement -> value
        is String -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        is Int -> JsonPrimitive(value)
        is Long -> JsonPrimitive(value)
        is Double -> JsonPrimitive(value)
        is Float -> JsonPrimitive(value)
        is Number -> JsonPrimitive(value)
        else -> JsonPrimitive(value.toString())
    }
}
