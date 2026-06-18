package io.appwrite.core.types

import kotlinx.datetime.Clock
import kotlin.random.Random

/**
 * ID helper mirroring the Appwrite Web SDK `ID` class.
 *
 * ```
 * ID.unique()              // server-friendly time-ordered unique id
 * ID.custom("my-id")       // pass-through of a caller-provided id
 * ```
 *
 * [unique] reproduces the JS SDK algorithm: a hex-encoded Unix-seconds prefix,
 * a 5-character hex millisecond component, and [padding] random hex characters.
 */
public object ID {
    /** Returns [id] unchanged — use for caller-supplied identifiers. */
    public fun custom(id: String): String = id

    /**
     * Generates a unique, roughly time-ordered ID with [padding] trailing
     * random hex characters (default 7, matching the JS SDK).
     */
    public fun unique(padding: Int = 7): String {
        val now = Clock.System.now()
        val seconds = now.epochSeconds
        val millis = (now.nanosecondsOfSecond / 1_000_000)

        val baseId = seconds.toString(16)
        // 5-character zero-padded hex of the millisecond component.
        val msHex = millis.toString(16).padStart(5, '0')

        val random =
            buildString {
                repeat(padding) {
                    append(HEX[Random.nextInt(16)])
                }
            }
        return baseId + msHex + random
    }

    private const val HEX = "0123456789abcdef"
}
