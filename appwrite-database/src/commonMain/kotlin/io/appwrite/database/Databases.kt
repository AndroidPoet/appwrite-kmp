package io.appwrite.database

import io.appwrite.client.Appwrite
import io.appwrite.client.ServiceBase
import io.appwrite.core.models.Document
import io.appwrite.core.models.DocumentList
import io.appwrite.core.query.QueryBuilder
import io.appwrite.core.query.buildQuery
import io.appwrite.core.result.AppwriteResult
import io.appwrite.core.types.CollectionId
import io.appwrite.core.types.DatabaseId
import io.appwrite.core.types.DocumentId

/**
 * Database service with scoped navigation.
 *
 * Usage:
 * ```
 * val db = appwrite.databases
 *
 * // List documents with type-safe query
 * db.listDocuments(dbId, collId) {
 *     where("status" equal "active")
 *     orderBy("createdAt", descending = true)
 *     limit(25)
 * }
 *
 * // Scoped access
 * val collection = db[DatabaseId("main")][CollectionId("users")]
 * collection.list { where("age" greaterThan 18) }
 * ```
 */
class Databases(appwrite: Appwrite) : ServiceBase(appwrite.transport) {

    operator fun get(databaseId: DatabaseId) = DatabaseScope(databaseId, this)

    // ── Document CRUD ────────────────────────────────────────

    suspend fun listDocuments(
        databaseId: DatabaseId,
        collectionId: CollectionId,
        query: (QueryBuilder.() -> Unit)? = null,
    ): AppwriteResult<DocumentList> = get(
        path = "/databases/${databaseId.raw}/collections/${collectionId.raw}/documents",
        params = buildMap {
            if (query != null) put("queries", buildQuery(query))
        },
    )

    suspend fun getDocument(
        databaseId: DatabaseId,
        collectionId: CollectionId,
        documentId: DocumentId,
        queries: List<String>? = null,
    ): AppwriteResult<Document> = get(
        path = "/databases/${databaseId.raw}/collections/${collectionId.raw}/documents/${documentId.raw}",
        params = buildMap {
            if (queries != null) put("queries", queries)
        },
    )

    suspend fun createDocument(
        databaseId: DatabaseId,
        collectionId: CollectionId,
        documentId: DocumentId = DocumentId.unique(),
        data: Map<String, Any?>,
        permissions: List<String>? = null,
    ): AppwriteResult<Document> = post(
        path = "/databases/${databaseId.raw}/collections/${collectionId.raw}/documents",
        params = buildMap {
            put("documentId", documentId.raw)
            put("data", data)
            if (permissions != null) put("permissions", permissions)
        },
    )

    suspend fun updateDocument(
        databaseId: DatabaseId,
        collectionId: CollectionId,
        documentId: DocumentId,
        data: Map<String, Any?>? = null,
        permissions: List<String>? = null,
    ): AppwriteResult<Document> = patch(
        path = "/databases/${databaseId.raw}/collections/${collectionId.raw}/documents/${documentId.raw}",
        params = buildMap {
            if (data != null) put("data", data)
            if (permissions != null) put("permissions", permissions)
        },
    )

    suspend fun deleteDocument(
        databaseId: DatabaseId,
        collectionId: CollectionId,
        documentId: DocumentId,
    ): AppwriteResult<Unit> = delete(
        path = "/databases/${databaseId.raw}/collections/${collectionId.raw}/documents/${documentId.raw}",
    )

    // ── Atomic Operations ────────────────────────────────────

    suspend fun incrementAttribute(
        databaseId: DatabaseId,
        collectionId: CollectionId,
        documentId: DocumentId,
        attribute: String,
        value: Number = 1,
    ): AppwriteResult<Document> = patch(
        path = "/databases/${databaseId.raw}/collections/${collectionId.raw}/documents/${documentId.raw}/increment",
        params = mapOf("attribute" to attribute, "value" to value),
    )

    suspend fun decrementAttribute(
        databaseId: DatabaseId,
        collectionId: CollectionId,
        documentId: DocumentId,
        attribute: String,
        value: Number = 1,
    ): AppwriteResult<Document> = patch(
        path = "/databases/${databaseId.raw}/collections/${collectionId.raw}/documents/${documentId.raw}/decrement",
        params = mapOf("attribute" to attribute, "value" to value),
    )
}

/**
 * Scoped access to a specific database.
 */
class DatabaseScope(
    private val databaseId: DatabaseId,
    private val databases: Databases,
) {
    operator fun get(collectionId: CollectionId) =
        CollectionScope(databaseId, collectionId, databases)
}

/**
 * Scoped access to a specific collection.
 */
class CollectionScope(
    private val databaseId: DatabaseId,
    private val collectionId: CollectionId,
    private val databases: Databases,
) {
    suspend fun list(
        query: (QueryBuilder.() -> Unit)? = null,
    ) = databases.listDocuments(databaseId, collectionId, query)

    suspend fun get(documentId: DocumentId) =
        databases.getDocument(databaseId, collectionId, documentId)

    suspend fun create(
        documentId: DocumentId = DocumentId.unique(),
        data: Map<String, Any?>,
        permissions: List<String>? = null,
    ) = databases.createDocument(databaseId, collectionId, documentId, data, permissions)

    suspend fun update(
        documentId: DocumentId,
        data: Map<String, Any?>? = null,
        permissions: List<String>? = null,
    ) = databases.updateDocument(databaseId, collectionId, documentId, data, permissions)

    suspend fun delete(documentId: DocumentId) =
        databases.deleteDocument(databaseId, collectionId, documentId)
}

/**
 * Extension property: `appwrite.databases`
 */
val Appwrite.databases: Databases get() = Databases(this)
