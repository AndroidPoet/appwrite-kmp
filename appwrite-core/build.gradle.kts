import io.androidpoet.appwrite.Configuration

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.nexusPlugin)
}

apply(from = "$rootDir/scripts/publish-module.gradle.kts")

mavenPublishing {
    val artifactId = "appwrite-core"
    coordinates(
        Configuration.artifactGroup,
        artifactId,
        rootProject.extra.get("libVersion").toString(),
    )
    pom {
        name.set(artifactId)
        description.set("Core models, typed IDs, result types, and query DSL for Appwrite KMP SDK")
    }
}

kotlin {
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
