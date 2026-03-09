package io.appwrite.auth

import io.appwrite.client.Appwrite

/**
 * Extension property for clean access: `appwrite.auth`
 */
val Appwrite.auth: Auth get() = Auth(this)
