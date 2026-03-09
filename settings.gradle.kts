pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "appwrite-kmp"

include(":appwrite-core")
include(":appwrite-client")
include(":appwrite-auth")
include(":appwrite-database")
include(":appwrite-storage")
include(":appwrite-realtime")
include(":appwrite-teams")
include(":appwrite-functions")
include(":appwrite-locale")
include(":appwrite-avatars")
