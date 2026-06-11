package io.appwrite.core.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class IDTest {

    @Test
    fun custom_returnsInputUnchanged() {
        assertEquals("my-id", ID.custom("my-id"))
    }

    @Test
    fun unique_isHexAndNonEmpty() {
        val id = ID.unique()
        assertTrue(id.isNotEmpty())
        assertTrue(id.all { it in "0123456789abcdef" }, "Expected lowercase hex, got: $id")
    }

    @Test
    fun unique_defaultPaddingLength() {
        // base seconds hex + 5 hex ms chars + 7 random padding chars.
        // seconds hex is at least 8 chars for current epoch, so total >= 20.
        val id = ID.unique()
        assertTrue(id.length >= 8 + 5 + 7, "Unexpected length ${id.length} for $id")
    }

    @Test
    fun unique_customPaddingChangesLength() {
        val short = ID.unique(padding = 0)
        val long = ID.unique(padding = 12)
        assertEquals(12, long.length - short.length)
    }

    @Test
    fun unique_isMostlyDistinct() {
        val ids = List(50) { ID.unique() }.toSet()
        // Random padding makes collisions astronomically unlikely.
        assertNotEquals(1, ids.size)
        assertTrue(ids.size >= 49)
    }
}
