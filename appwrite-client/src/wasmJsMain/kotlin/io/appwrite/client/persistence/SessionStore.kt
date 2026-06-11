package io.appwrite.client.persistence

/**
 * wasmJs session persistence backed by the browser `window.localStorage`.
 *
 * Implemented via small `js(...)` shims so the module does not need the
 * optional `kotlinx-browser` DOM artifact on its classpath.
 */
actual class SessionStore actual constructor() {
    private val prefix = "io.appwrite.session."

    actual fun save(key: String, value: String) {
        lsSet(prefix + key, value)
    }

    actual fun load(key: String): String? = lsGet(prefix + key)

    actual fun remove(key: String) {
        lsRemove(prefix + key)
    }

    actual fun clear() {
        val count = lsLength()
        val keys = buildList {
            for (i in 0 until count) {
                lsKey(i)?.let { add(it) }
            }
        }
        keys.filter { it.startsWith(prefix) }.forEach { lsRemove(it) }
    }
}

private fun lsSet(key: String, value: String): Unit =
    js("{ localStorage.setItem(key, value); }")

private fun lsGet(key: String): String? =
    js("localStorage.getItem(key)")

private fun lsRemove(key: String): Unit =
    js("{ localStorage.removeItem(key); }")

private fun lsLength(): Int =
    js("localStorage.length")

private fun lsKey(index: Int): String? =
    js("localStorage.key(index)")
