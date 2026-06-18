package io.appwrite.core.permission

/**
 * Role helper mirroring the Appwrite Web SDK `Role` class. Produces the role
 * strings consumed by [Permission].
 *
 * ```
 * Role.any()                       // "any"
 * Role.user("id")                  // "user:id"
 * Role.user("id", "verified")      // "user:id/verified"
 * Role.users()                     // "users"
 * Role.users("verified")           // "users/verified"
 * Role.guests()                    // "guests"
 * Role.team("id")                  // "team:id"
 * Role.team("id", "owner")         // "team:id/owner"
 * Role.member("id")                // "member:id"
 * Role.label("admin")              // "label:admin"
 * ```
 */
public object Role {
    /** Grants access to anyone. */
    public fun any(): String = "any"

    /** Grants access to a specific user by ID, optionally scoped to a [status]. */
    public fun user(id: String, status: String = ""): String =
        if (status.isEmpty()) "user:$id" else "user:$id/$status"

    /** Grants access to all users, optionally scoped to a [status] (e.g. "verified"). */
    public fun users(status: String = ""): String =
        if (status.isEmpty()) "users" else "users/$status"

    /** Grants access to unauthenticated (guest) users. */
    public fun guests(): String = "guests"

    /** Grants access to a team by ID, optionally scoped to a team [role]. */
    public fun team(id: String, role: String = ""): String =
        if (role.isEmpty()) "team:$id" else "team:$id/$role"

    /** Grants access to a specific team member by membership ID. */
    public fun member(id: String): String = "member:$id"

    /** Grants access to users carrying a specific [name] label. */
    public fun label(name: String): String = "label:$name"
}
