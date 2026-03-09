package io.appwrite.core.types

import kotlin.jvm.JvmInline

@JvmInline
value class ProjectId(val raw: String)

@JvmInline
value class UserId(val raw: String) {
    companion object {
        fun unique() = UserId("unique()")
    }
}

@JvmInline
value class SessionId(val raw: String) {
    companion object {
        val current = SessionId("current")
    }
}

@JvmInline
value class DatabaseId(val raw: String)

@JvmInline
value class CollectionId(val raw: String)

@JvmInline
value class DocumentId(val raw: String) {
    companion object {
        fun unique() = DocumentId("unique()")
    }
}

@JvmInline
value class BucketId(val raw: String)

@JvmInline
value class FileId(val raw: String) {
    companion object {
        fun unique() = FileId("unique()")
    }
}

@JvmInline
value class TeamId(val raw: String) {
    companion object {
        fun unique() = TeamId("unique()")
    }
}

@JvmInline
value class MembershipId(val raw: String)

@JvmInline
value class FunctionId(val raw: String)

@JvmInline
value class ExecutionId(val raw: String)

@JvmInline
value class TopicId(val raw: String)

@JvmInline
value class TargetId(val raw: String) {
    companion object {
        fun unique() = TargetId("unique()")
    }
}

@JvmInline
value class IdentityId(val raw: String)
