# Appwrite KMP SDK

A Kotlin Multiplatform SDK for [Appwrite](https://appwrite.io) — built from scratch with type safety, coroutines, and multiplatform support at its core.

> **Targets:** Android, iOS, JVM

## Why another SDK?

The official Appwrite Android SDK is tightly coupled to Android (OkHttp, Gson, SharedPreferences). This SDK is designed for Kotlin Multiplatform from day one:

- **Errors as values** — `AppwriteResult<T>` instead of thrown exceptions
- **Type-safe IDs** — `DatabaseId`, `CollectionId`, `FileId` — no more string mix-ups
- **Query DSL** — `where("age" greaterThan 18)` instead of raw strings
- **Flow-based realtime** — auto-reconnect, exponential backoff, coroutine-scoped
- **Chunked uploads as Flow** — `Flow<UploadState>` with progress tracking
- **Modular** — pull in only what you need

## Installation

```kotlin
// build.gradle.kts
dependencies {
    // Core (required)
    implementation("io.github.androidpoet:appwrite-client:<version>")

    // Pick the services you need
    implementation("io.github.androidpoet:appwrite-auth:<version>")
    implementation("io.github.androidpoet:appwrite-database:<version>")
    implementation("io.github.androidpoet:appwrite-storage:<version>")
    implementation("io.github.androidpoet:appwrite-realtime:<version>")
    implementation("io.github.androidpoet:appwrite-teams:<version>")
    implementation("io.github.androidpoet:appwrite-functions:<version>")
    implementation("io.github.androidpoet:appwrite-locale:<version>")
    implementation("io.github.androidpoet:appwrite-avatars:<version>")
}
```

## Quick Start

### Initialize

```kotlin
val appwrite = Appwrite("your-project-id") {
    endpoint = "https://cloud.appwrite.io/v1"  // or your self-hosted instance
}

// Optional: persist sessions across app restarts
appwrite.sessionStore = SessionStore()
```

### Authentication

```kotlin
import io.appwrite.auth.auth

// Sign up
val user = appwrite.auth.signUp(
    email = "user@example.com",
    password = "password123",
    name = "Jane Doe",
)

// Sign in
when (val result = appwrite.auth.signInWithEmail("user@example.com", "password123")) {
    is AppwriteResult.Success -> println("Session: ${result.data.id}")
    is AppwriteResult.Failure -> println("Error: ${result.error.message}")
}

// MFA
appwrite.auth.mfa.enable()
appwrite.auth.mfa.createAuthenticator(AuthenticationFactor.Totp)
appwrite.auth.mfa.verifyAuthenticator(AuthenticationFactor.Totp, otp = "123456")

// Sign out
appwrite.auth.signOut()
```

### Databases

```kotlin
import io.appwrite.database.databases

val db = appwrite.databases

// Scoped access
val users = db[DatabaseId("main")][CollectionId("users")]

// Create
users.create(
    data = mapOf("name" to "Jane", "age" to 28, "status" to "active"),
)

// Query with DSL
val result = users.list {
    where("age" greaterThan 18)
    where("status" equal "active")
    orderBy("name")
    limit(25)
}

// Update
users.update(
    documentId = DocumentId("abc123"),
    data = mapOf("status" to "inactive"),
)
```

### Storage

```kotlin
import io.appwrite.storage.storage

// Upload with progress tracking
val file = InputFile.fromBytes(imageBytes, "photo.jpg", "image/jpeg")

appwrite.storage.upload(BucketId("photos"), FileId.unique(), file)
    .collect { state ->
        when (state) {
            is UploadState.Progress -> println("${state.chunksUploaded}/${state.chunksTotal}")
            is UploadState.Complete -> println("Uploaded: ${state.file.id}")
            is UploadState.Failed -> println("Error: ${state.error.message}")
        }
    }

// Download
val bytes = appwrite.storage.download(BucketId("photos"), FileId("abc123"))

// Preview with transforms
val thumbnail = appwrite.storage.preview(BucketId("photos"), FileId("abc123")) {
    width = 200
    height = 200
    gravity = ImageGravity.Center
    quality = 80
}
```

### Realtime

```kotlin
import io.appwrite.realtime.realtime

// Subscribe to document changes — cold Flow, auto-reconnects
appwrite.realtime
    .documents(DatabaseId("main"), CollectionId("messages"))
    .onEach { event -> updateUI(event.payload) }
    .launchIn(viewModelScope) // auto-cleanup on scope cancellation

// Subscribe to account events
appwrite.realtime.account()
    .collect { event -> handleAccountEvent(event) }
```

### Teams

```kotlin
import io.appwrite.teams.teams

// Create team
appwrite.teams.create(TeamId.unique(), name = "Engineering")

// Invite member
appwrite.teams.createMembership(
    teamId = TeamId("eng-team"),
    roles = listOf("developer"),
    email = "dev@example.com",
)
```

### Functions

```kotlin
import io.appwrite.functions.functions

// Execute a serverless function
val execution = appwrite.functions.createExecution(
    functionId = FunctionId("send-welcome-email"),
    body = """{"userId": "abc123"}""",
)
```

## Architecture

```
┌─────────────────────────────────────┐
│  Public API  (what devs touch)      │  DSL builders, typed IDs, Flows
├─────────────────────────────────────┤
│  Domain      (business logic)       │  Session management, query builder,
│                                     │  chunked upload orchestration
├─────────────────────────────────────┤
│  Protocol    (Appwrite specifics)   │  Header injection, error mapping,
│                                     │  response deserialization
├─────────────────────────────────────┤
│  Transport   (HTTP/WS engine)       │  Ktor client, expect/actual
└─────────────────────────────────────┘
```

### Modules

| Module | Description |
|---|---|
| `appwrite-core` | Models, typed IDs, `AppwriteResult`, query DSL |
| `appwrite-client` | `Appwrite` entry point, Ktor transport, session persistence |
| `appwrite-auth` | Authentication, sessions, MFA, verification, recovery |
| `appwrite-database` | Document CRUD with scoped navigation, atomic ops |
| `appwrite-storage` | File upload (chunked with Flow), download, preview |
| `appwrite-realtime` | WebSocket subscriptions as Kotlin Flows |
| `appwrite-teams` | Team and membership management |
| `appwrite-functions` | Serverless function execution |
| `appwrite-locale` | Languages, countries, currencies, timezones |
| `appwrite-avatars` | Generated avatars, flags, QR codes |

### Key Design Decisions

**Errors as values, not exceptions**
```kotlin
sealed interface AppwriteResult<out T> {
    data class Success<T>(val data: T) : AppwriteResult<T>
    data class Failure(val error: AppwriteError) : AppwriteResult<Nothing>
}
```

**Value class IDs prevent string mix-ups**
```kotlin
// Compiler catches this — DatabaseId and CollectionId are different types
fun getDocument(databaseId: DatabaseId, collectionId: CollectionId, documentId: DocumentId)
```

**Session persistence is opt-in and platform-aware**
```kotlin
// JVM: java.util.prefs.Preferences
// iOS: NSUserDefaults
appwrite.sessionStore = SessionStore()
```

## Tech Stack

| Concern | Library |
|---|---|
| HTTP | [Ktor](https://ktor.io/) |
| Serialization | [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) |
| Async | [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines) |
| Date/Time | [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime) |

## Compatibility

- **Appwrite Server**: 1.8.x+
- **Kotlin**: 2.1+
- **Platforms**: Android, iOS (arm64, x64, simulator), JVM

## Contributing

1. Fork the repo
2. Create a feature branch (`git checkout -b feature/amazing-thing`)
3. Make your changes
4. Run tests: `./gradlew jvmTest`
5. Open a PR

## License

[MIT](LICENSE)
