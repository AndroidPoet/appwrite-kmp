package io.appwrite.core.permission

import kotlin.test.Test
import kotlin.test.assertEquals

class PermissionTest {

    @Test
    fun permission_read_wrapsRole() {
        assertEquals("""read("any")""", Permission.read(Role.any()))
    }

    @Test
    fun permission_write_wrapsRole() {
        assertEquals("""write("users")""", Permission.write(Role.users()))
    }

    @Test
    fun permission_create_update_delete() {
        assertEquals("""create("guests")""", Permission.create(Role.guests()))
        assertEquals("""update("user:abc")""", Permission.update(Role.user("abc")))
        assertEquals("""delete("team:admins/owner")""", Permission.delete(Role.team("admins", "owner")))
    }

    @Test
    fun role_any() {
        assertEquals("any", Role.any())
    }

    @Test
    fun role_user_withoutStatus() {
        assertEquals("user:abc", Role.user("abc"))
    }

    @Test
    fun role_user_withStatus() {
        assertEquals("user:abc/verified", Role.user("abc", "verified"))
    }

    @Test
    fun role_users_withoutStatus() {
        assertEquals("users", Role.users())
    }

    @Test
    fun role_users_withStatus() {
        assertEquals("users/verified", Role.users("verified"))
    }

    @Test
    fun role_guests() {
        assertEquals("guests", Role.guests())
    }

    @Test
    fun role_team_withoutRole() {
        assertEquals("team:admins", Role.team("admins"))
    }

    @Test
    fun role_team_withRole() {
        assertEquals("team:admins/owner", Role.team("admins", "owner"))
    }

    @Test
    fun role_member() {
        assertEquals("member:m1", Role.member("m1"))
    }

    @Test
    fun role_label() {
        assertEquals("label:vip", Role.label("vip"))
    }
}
