package io.appwrite.core.query

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Verifies the static [Query] helper emits the modern Appwrite JSON wire
 * format, matching the Appwrite Web/JS SDK byte-for-byte.
 */
class AppwriteQueryTest {
    @Test
    fun equal_singleString_wrapsInValuesArray() {
        assertEquals(
            """{"method":"equal","attribute":"name","values":["Alice"]}""",
            Query.equal("name", "Alice"),
        )
    }

    @Test
    fun equal_listOfValues() {
        assertEquals(
            """{"method":"equal","attribute":"role","values":["admin","editor"]}""",
            Query.equal("role", listOf("admin", "editor")),
        )
    }

    @Test
    fun equal_withNumber_isNotQuoted() {
        assertEquals(
            """{"method":"equal","attribute":"age","values":[18]}""",
            Query.equal("age", 18),
        )
    }

    @Test
    fun equal_withBoolean() {
        assertEquals(
            """{"method":"equal","attribute":"active","values":[true]}""",
            Query.equal("active", true),
        )
    }

    @Test
    fun greaterThan_withNumber() {
        assertEquals(
            """{"method":"greaterThan","attribute":"age","values":[18]}""",
            Query.greaterThan("age", 18),
        )
    }

    @Test
    fun between_emitsTwoValues() {
        assertEquals(
            """{"method":"between","attribute":"score","values":[10,90]}""",
            Query.between("score", 10, 90),
        )
    }

    @Test
    fun isNull_hasNoValues() {
        assertEquals(
            """{"method":"isNull","attribute":"deletedAt"}""",
            Query.isNull("deletedAt"),
        )
    }

    @Test
    fun isNotNull_hasNoValues() {
        assertEquals(
            """{"method":"isNotNull","attribute":"email"}""",
            Query.isNotNull("email"),
        )
    }

    @Test
    fun search() {
        assertEquals(
            """{"method":"search","attribute":"title","values":["kotlin"]}""",
            Query.search("title", "kotlin"),
        )
    }

    @Test
    fun startsWith() {
        assertEquals(
            """{"method":"startsWith","attribute":"email","values":["admin"]}""",
            Query.startsWith("email", "admin"),
        )
    }

    @Test
    fun select_hasNoAttribute() {
        assertEquals(
            """{"method":"select","values":["name","email"]}""",
            Query.select("name", "email"),
        )
    }

    @Test
    fun orderDesc_hasAttributeButNoValues() {
        assertEquals(
            """{"method":"orderDesc","attribute":"createdAt"}""",
            Query.orderDesc("createdAt"),
        )
    }

    @Test
    fun orderRandom_hasNeitherAttributeNorValues() {
        assertEquals(
            """{"method":"orderRandom"}""",
            Query.orderRandom(),
        )
    }

    @Test
    fun limit_hasValueButNoAttribute() {
        assertEquals(
            """{"method":"limit","values":[25]}""",
            Query.limit(25),
        )
    }

    @Test
    fun offset() {
        assertEquals(
            """{"method":"offset","values":[10]}""",
            Query.offset(10),
        )
    }

    @Test
    fun cursorAfter() {
        assertEquals(
            """{"method":"cursorAfter","values":["abc123"]}""",
            Query.cursorAfter("abc123"),
        )
    }

    @Test
    fun or_nestsEncodedQueries() {
        val encoded =
            Query.or(
                listOf(
                    Query.equal("status", "active"),
                    Query.equal("status", "pending"),
                ),
            )
        assertEquals(
            """{"method":"or","values":[{"method":"equal","attribute":"status","values":["active"]},{"method":"equal","attribute":"status","values":["pending"]}]}""",
            encoded,
        )
    }

    @Test
    fun and_nestsEncodedQueries() {
        val encoded =
            Query.and(
                listOf(
                    Query.greaterThan("age", 18),
                    Query.lessThan("age", 65),
                ),
            )
        assertEquals(
            """{"method":"and","values":[{"method":"greaterThan","attribute":"age","values":[18]},{"method":"lessThan","attribute":"age","values":[65]}]}""",
            encoded,
        )
    }
}
