package io.appwrite.core.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class IdsTest {

    @Test
    fun test_userId_unique_generatesUniqueMarker() {
        val id = UserId.unique()

        assertEquals("unique()", id.raw)
    }

    @Test
    fun test_documentId_unique_generatesUniqueMarker() {
        val id = DocumentId.unique()

        assertEquals("unique()", id.raw)
    }

    @Test
    fun test_sessionId_current_returnsCurrentMarker() {
        val id = SessionId.current

        assertEquals("current", id.raw)
    }

    @Test
    fun test_ids_preserveRawValue() {
        val projectId = ProjectId("proj_123")
        val userId = UserId("user_456")
        val databaseId = DatabaseId("db_789")
        val collectionId = CollectionId("col_abc")
        val bucketId = BucketId("bkt_def")

        assertEquals("proj_123", projectId.raw)
        assertEquals("user_456", userId.raw)
        assertEquals("db_789", databaseId.raw)
        assertEquals("col_abc", collectionId.raw)
        assertEquals("bkt_def", bucketId.raw)
    }

    @Test
    fun test_ids_equality() {
        val a = UserId("same")
        val b = UserId("same")
        val c = UserId("different")

        assertEquals(a, b)
        assertNotEquals(a, c)
    }
}
