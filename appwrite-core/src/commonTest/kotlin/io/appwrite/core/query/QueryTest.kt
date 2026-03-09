package io.appwrite.core.query

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QueryTest {

    @Test
    fun test_equal_generatesCorrectQuery() {
        val condition = "name" equal "Alice"

        val encoded = condition.encode()

        assertEquals("""equal("name", ["Alice"])""", encoded)
    }

    @Test
    fun test_notEqual_generatesCorrectQuery() {
        val condition = "status" notEqual "banned"

        val encoded = condition.encode()

        assertEquals("""notEqual("status", ["banned"])""", encoded)
    }

    @Test
    fun test_greaterThan_withNumber() {
        val condition = "age" greaterThan 18

        val encoded = condition.encode()

        assertEquals("""greaterThan("age", [18])""", encoded)
    }

    @Test
    fun test_lessThan_withNumber() {
        val condition = "price" lessThan 100.5

        val encoded = condition.encode()

        assertEquals("""lessThan("price", [100.5])""", encoded)
    }

    @Test
    fun test_contains_withString() {
        val condition = "bio" contains "kotlin"

        val encoded = condition.encode()

        assertEquals("""contains("bio", ["kotlin"])""", encoded)
    }

    @Test
    fun test_startsWith_withString() {
        val condition = "email" startsWith "admin"

        val encoded = condition.encode()

        assertEquals("""startsWith("email", ["admin"])""", encoded)
    }

    @Test
    fun test_endsWith_withString() {
        val condition = "file" endsWith ".pdf"

        val encoded = condition.encode()

        assertEquals("""endsWith("file", [".pdf"])""", encoded)
    }

    @Test
    fun test_oneOf_withList() {
        val condition = "role" oneOf listOf("admin", "editor")

        val encoded = condition.encode()

        assertEquals("""equal("role", ["admin","editor"])""", encoded)
    }

    @Test
    fun test_between_withRange() {
        val condition = "score" between (10 to 90)

        val encoded = condition.encode()

        assertEquals("""between("score", 10, 90)""", encoded)
    }

    @Test
    fun test_isNull_generatesCorrectQuery() {
        val condition = "deletedAt".isNull()

        val encoded = condition.encode()

        assertEquals("""isNull("deletedAt")""", encoded)
    }

    @Test
    fun test_isNotNull_generatesCorrectQuery() {
        val condition = "email".isNotNull()

        val encoded = condition.encode()

        assertEquals("""isNotNull("email")""", encoded)
    }

    @Test
    fun test_search_generatesCorrectQuery() {
        val condition = search("title", "kotlin multiplatform")

        val encoded = condition.encode()

        assertEquals("""search("title", ["kotlin multiplatform"])""", encoded)
    }

    @Test
    fun test_orderBy_ascending() {
        val queries = buildQuery {
            orderBy("createdAt")
        }

        assertEquals(1, queries.size)
        assertEquals("""orderAsc("createdAt")""", queries[0])
    }

    @Test
    fun test_orderBy_descending() {
        val queries = buildQuery {
            orderBy("updatedAt", descending = true)
        }

        assertEquals(1, queries.size)
        assertEquals("""orderDesc("updatedAt")""", queries[0])
    }

    @Test
    fun test_limit_generatesCorrectQuery() {
        val queries = buildQuery {
            limit(25)
        }

        assertEquals(1, queries.size)
        assertEquals("limit(25)", queries[0])
    }

    @Test
    fun test_offset_generatesCorrectQuery() {
        val queries = buildQuery {
            offset(50)
        }

        assertEquals(1, queries.size)
        assertEquals("offset(50)", queries[0])
    }

    @Test
    fun test_select_withMultipleFields() {
        val queries = buildQuery {
            select("name", "email", "age")
        }

        assertEquals(1, queries.size)
        assertEquals("""select(["name","email","age"])""", queries[0])
    }

    @Test
    fun test_buildQuery_combinesMultipleConditions() {
        val queries = buildQuery {
            where("age" greaterThan 18)
            where("status" equal "active")
            orderBy("name")
            limit(10)
        }

        assertEquals(4, queries.size)
        assertEquals("""greaterThan("age", [18])""", queries[0])
        assertEquals("""equal("status", ["active"])""", queries[1])
        assertEquals("""orderAsc("name")""", queries[2])
        assertEquals("limit(10)", queries[3])
    }

    @Test
    fun test_cursorAfter_generatesCorrectQuery() {
        val queries = buildQuery {
            cursorAfter("abc123")
        }

        assertEquals(1, queries.size)
        assertEquals("""cursorAfter("abc123")""", queries[0])
    }
}
