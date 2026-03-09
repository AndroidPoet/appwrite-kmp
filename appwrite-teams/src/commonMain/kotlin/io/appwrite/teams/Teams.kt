package io.appwrite.teams

import io.appwrite.client.Appwrite
import io.appwrite.client.ServiceBase
import io.appwrite.core.models.Membership
import io.appwrite.core.models.MembershipList
import io.appwrite.core.models.Team
import io.appwrite.core.models.TeamList
import io.appwrite.core.query.QueryBuilder
import io.appwrite.core.query.buildQuery
import io.appwrite.core.result.AppwriteResult
import io.appwrite.core.types.MembershipId
import io.appwrite.core.types.TeamId
import io.appwrite.core.types.UserId

/**
 * Teams service — manage teams, memberships, and preferences.
 *
 * Usage:
 * ```
 * val teams = appwrite.teams
 *
 * // List teams
 * teams.list { limit(10) }
 *
 * // Create team
 * teams.create(TeamId.unique(), "Engineering")
 *
 * // Manage memberships
 * teams.createMembership(teamId, roles = listOf("developer"), email = "dev@example.com")
 * ```
 */
class Teams(appwrite: Appwrite) : ServiceBase(appwrite.transport) {

    // ── Teams ─────────────────────────────────────────────────

    suspend fun list(
        query: (QueryBuilder.() -> Unit)? = null,
    ): AppwriteResult<TeamList> = get(
        path = "/teams",
        params = buildMap {
            if (query != null) put("queries", buildQuery(query))
        },
    )

    suspend fun create(
        teamId: TeamId = TeamId.unique(),
        name: String,
        roles: List<String>? = null,
    ): AppwriteResult<Team> = post(
        path = "/teams",
        params = buildMap {
            put("teamId", teamId.raw)
            put("name", name)
            if (roles != null) put("roles", roles)
        },
    )

    suspend fun get(teamId: TeamId): AppwriteResult<Team> =
        get(path = "/teams/${teamId.raw}")

    suspend fun updateName(
        teamId: TeamId,
        name: String,
    ): AppwriteResult<Team> = put(
        path = "/teams/${teamId.raw}",
        params = mapOf("name" to name),
    )

    suspend fun delete(teamId: TeamId): AppwriteResult<Unit> =
        delete(path = "/teams/${teamId.raw}")

    // ── Preferences ───────────────────────────────────────────

    suspend fun getPrefs(teamId: TeamId): AppwriteResult<Map<String, String>> =
        get(path = "/teams/${teamId.raw}/prefs")

    suspend fun updatePrefs(
        teamId: TeamId,
        prefs: Map<String, String>,
    ): AppwriteResult<Map<String, String>> = put(
        path = "/teams/${teamId.raw}/prefs",
        params = mapOf("prefs" to prefs),
    )

    // ── Memberships ───────────────────────────────────────────

    suspend fun listMemberships(
        teamId: TeamId,
        query: (QueryBuilder.() -> Unit)? = null,
    ): AppwriteResult<MembershipList> = get(
        path = "/teams/${teamId.raw}/memberships",
        params = buildMap {
            if (query != null) put("queries", buildQuery(query))
        },
    )

    suspend fun createMembership(
        teamId: TeamId,
        roles: List<String>,
        email: String? = null,
        userId: UserId? = null,
        phone: String? = null,
        url: String? = null,
        name: String? = null,
    ): AppwriteResult<Membership> = post(
        path = "/teams/${teamId.raw}/memberships",
        params = buildMap {
            put("roles", roles)
            if (email != null) put("email", email)
            if (userId != null) put("userId", userId.raw)
            if (phone != null) put("phone", phone)
            if (url != null) put("url", url)
            if (name != null) put("name", name)
        },
    )

    suspend fun getMembership(
        teamId: TeamId,
        membershipId: MembershipId,
    ): AppwriteResult<Membership> =
        get(path = "/teams/${teamId.raw}/memberships/${membershipId.raw}")

    suspend fun updateMembership(
        teamId: TeamId,
        membershipId: MembershipId,
        roles: List<String>,
    ): AppwriteResult<Membership> = patch(
        path = "/teams/${teamId.raw}/memberships/${membershipId.raw}",
        params = mapOf("roles" to roles),
    )

    suspend fun updateMembershipStatus(
        teamId: TeamId,
        membershipId: MembershipId,
        userId: UserId,
        secret: String,
    ): AppwriteResult<Membership> = patch(
        path = "/teams/${teamId.raw}/memberships/${membershipId.raw}/status",
        params = mapOf("userId" to userId.raw, "secret" to secret),
    )

    suspend fun deleteMembership(
        teamId: TeamId,
        membershipId: MembershipId,
    ): AppwriteResult<Unit> =
        delete(path = "/teams/${teamId.raw}/memberships/${membershipId.raw}")
}

/**
 * Extension property: `appwrite.teams`
 */
val Appwrite.teams: Teams get() = Teams(this)
