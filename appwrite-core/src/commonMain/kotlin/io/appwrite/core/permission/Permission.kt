package io.appwrite.core.permission

/**
 * Permission helper mirroring the Appwrite Web SDK `Permission` class.
 *
 * Combine with [Role] to build permission strings:
 *
 * ```
 * permissions = listOf(
 *     Permission.read(Role.any()),
 *     Permission.update(Role.user("507f1f77bcf86cd799439011")),
 *     Permission.delete(Role.team("admins", "owner")),
 * )
 * ```
 */
public object Permission {
    public fun read(role: String): String = "read(\"$role\")"

    public fun write(role: String): String = "write(\"$role\")"

    public fun create(role: String): String = "create(\"$role\")"

    public fun update(role: String): String = "update(\"$role\")"

    public fun delete(role: String): String = "delete(\"$role\")"
}
