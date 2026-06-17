package io.appwrite.client.persistence

import android.content.Context
import android.content.SharedPreferences

actual class SessionStore actual constructor() {
    
    private val prefs: SharedPreferences = AndroidContextProvider.context.getSharedPreferences(
        "io.appwrite.session",
        Context.MODE_PRIVATE
    )

    actual fun save(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    actual fun load(key: String): String? {
        return prefs.getString(key, null)
    }

    actual fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    actual fun clear() {
        prefs.edit().clear().apply()
    }
}