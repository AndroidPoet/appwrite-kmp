package io.appwrite.realtime

import io.appwrite.core.types.BucketId
import io.appwrite.core.types.CollectionId
import io.appwrite.core.types.DatabaseId
import io.appwrite.core.types.DocumentId
import io.appwrite.core.types.FileId
import io.appwrite.core.types.FunctionId
import io.appwrite.core.types.TeamId

/**
 * Type-safe builders for the Appwrite realtime channel name strings.
 *
 * ```
 * appwrite.realtime.subscribe(RealtimeChannels.documents(db, col))
 * appwrite.realtime.subscribe(RealtimeChannels.ACCOUNT, RealtimeChannels.files(bucket))
 * ```
 *
 * Mirrors the channels documented at
 * https://appwrite.io/docs/apis/realtime/channels.
 */
public object RealtimeChannels {
    /** All account-scoped events for the current session. */
    public const val ACCOUNT: String = "account"

    /** All document events across every collection. */
    public const val DOCUMENTS: String = "documents"

    /** All file events across every bucket. */
    public const val FILES: String = "files"

    /** All team events. */
    public const val TEAMS: String = "teams"

    /** All membership events. */
    public const val MEMBERSHIPS: String = "memberships"

    /** All function execution events. */
    public const val EXECUTIONS: String = "executions"

    /** Every document in a collection. */
    public fun documents(databaseId: DatabaseId, collectionId: CollectionId): String =
        "databases.${databaseId.raw}.collections.${collectionId.raw}.documents"

    /** A single document in a collection. */
    public fun document(
        databaseId: DatabaseId,
        collectionId: CollectionId,
        documentId: DocumentId,
    ): String = "databases.${databaseId.raw}.collections.${collectionId.raw}.documents.${documentId.raw}"

    /** Every file in a bucket. */
    public fun files(bucketId: BucketId): String = "buckets.${bucketId.raw}.files"

    /** A single file in a bucket. */
    public fun file(bucketId: BucketId, fileId: FileId): String =
        "buckets.${bucketId.raw}.files.${fileId.raw}"

    /** A single team. */
    public fun team(teamId: TeamId): String = "teams.${teamId.raw}"

    /** Memberships of a single team. */
    public fun memberships(teamId: TeamId): String = "memberships.${teamId.raw}"

    /** Executions of a single function. */
    public fun executions(functionId: FunctionId): String = "functions.${functionId.raw}"
}
