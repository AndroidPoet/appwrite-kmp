package io.appwrite.core.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Appwrite document.
 *
 * The REST API returns document ATTRIBUTES at the TOP LEVEL of the JSON object
 * (next to the `$`-prefixed metadata) — there is no `data` wrapper on the wire.
 * [DocumentSerializer] therefore collects every non-`$` field into [data] on
 * deserialization (and inlines them again on serialization), so consumers get
 * a stable `document.data` view.
 */
@Serializable(with = DocumentSerializer::class)
data class Document(
    val id: String,
    val collectionId: String,
    val databaseId: String,
    val createdAt: String,
    val updatedAt: String,
    val permissions: List<String> = emptyList(),
    val data: JsonObject = JsonObject(emptyMap()),
)

object DocumentSerializer : KSerializer<Document> {
    override val descriptor: SerialDescriptor = JsonObject.serializer().descriptor

    override fun deserialize(decoder: Decoder): Document {
        val obj = (decoder as JsonDecoder).decodeJsonElement().jsonObject

        fun meta(key: String): String =
            (obj["\$$key"] as? JsonPrimitive)?.contentOrNull ?: ""

        val permissions = (obj["\$permissions"] as? JsonArray)
            ?.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
            ?: emptyList()

        return Document(
            id = meta("id"),
            collectionId = meta("collectionId"),
            databaseId = meta("databaseId"),
            createdAt = meta("createdAt"),
            updatedAt = meta("updatedAt"),
            permissions = permissions,
            // Attributes live at the top level; everything without a $ prefix is payload.
            data = JsonObject(obj.filterKeys { !it.startsWith("\$") }),
        )
    }

    override fun serialize(encoder: Encoder, value: Document) {
        val obj = buildJsonObject {
            put("\$id", JsonPrimitive(value.id))
            put("\$collectionId", JsonPrimitive(value.collectionId))
            put("\$databaseId", JsonPrimitive(value.databaseId))
            put("\$createdAt", JsonPrimitive(value.createdAt))
            put("\$updatedAt", JsonPrimitive(value.updatedAt))
            put("\$permissions", JsonArray(value.permissions.map { JsonPrimitive(it) }))
            value.data.forEach { (key, element) -> put(key, element) }
        }
        (encoder as JsonEncoder).encodeJsonElement(obj)
    }
}

@Serializable
data class DocumentList(
    val total: Int,
    val documents: List<Document>,
)
