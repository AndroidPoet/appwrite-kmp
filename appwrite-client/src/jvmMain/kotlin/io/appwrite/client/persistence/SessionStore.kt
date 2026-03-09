package io.appwrite.client.persistence

import java.util.prefs.Preferences

actual class SessionStore actual constructor() {
    private val prefs = Preferences.userNodeForPackage(SessionStore::class.java)

    actual fun save(key: String, value: String) {
        prefs.put(key, value)
    }

    actual fun load(key: String): String? = prefs.get(key, null)

    actual fun remove(key: String) {
        prefs.remove(key)
    }

    actual fun clear() {
        prefs.clear()
    }
}
