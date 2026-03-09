package io.appwrite.client.persistence

import platform.Foundation.NSUserDefaults

actual class SessionStore actual constructor() {
    private val defaults = NSUserDefaults.standardUserDefaults
    private val prefix = "io.appwrite.session."

    actual fun save(key: String, value: String) {
        defaults.setObject(value, forKey = prefix + key)
    }

    actual fun load(key: String): String? = defaults.stringForKey(prefix + key)

    actual fun remove(key: String) {
        defaults.removeObjectForKey(prefix + key)
    }

    actual fun clear() {
        defaults.dictionaryRepresentation().keys
            .filterIsInstance<String>()
            .filter { it.startsWith(prefix) }
            .forEach { defaults.removeObjectForKey(it) }
    }
}
