import io.androidpoet.appwrite.Configuration

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.nexusPlugin)
}

apply(from = "$rootDir/scripts/publish-module.gradle.kts")

mavenPublishing {
    val artifactId = "appwrite-teams"
    coordinates(
        Configuration.artifactGroup,
        artifactId,
        rootProject.extra.get("libVersion").toString(),
    )
    pom {
        name.set(artifactId)
        description.set("Appwrite KMP SDK teams — team and membership management")
    }
}

kotlin {
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":appwrite-core"))
            implementation(project(":appwrite-client"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
