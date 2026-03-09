package io.appwrite.client

import io.appwrite.core.types.ProjectId

data class AppwriteConfig(
    val projectId: ProjectId,
    val endpoint: String = "https://cloud.appwrite.io/v1",
    val selfSigned: Boolean = false,
    val logging: Boolean = false,
)

class AppwriteConfigBuilder {
    var endpoint: String = "https://cloud.appwrite.io/v1"
    var selfSigned: Boolean = false
    var logging: Boolean = false

    internal fun build(projectId: ProjectId) = AppwriteConfig(
        projectId = projectId,
        endpoint = endpoint,
        selfSigned = selfSigned,
        logging = logging,
    )
}
