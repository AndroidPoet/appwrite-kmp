plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.nexusPlugin) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.kover)
}

// Published modules whose test coverage we aggregate.
val publishedModules =
    listOf(
        "appwrite-core",
        "appwrite-client",
        "appwrite-auth",
        "appwrite-database",
        "appwrite-storage",
        "appwrite-realtime",
        "appwrite-teams",
        "appwrite-functions",
        "appwrite-locale",
        "appwrite-avatars",
    )

// ktlint engine version shared by every Spotless format below.
val ktlintVersion = libs.versions.ktlint.get()

// ktlint reads these from .editorconfig in the IDE; pass them to Spotless's
// bundled engine too so the Gradle check matches editor behaviour exactly.
// Line length is owned by detekt (MaxLineLength); ktlint's own reporting rule
// is disabled to avoid double-reporting.
val ktlintOverrides =
    mapOf(
        "ktlint_standard_max-line-length" to "disabled",
        "ktlint_standard_function-signature" to "disabled",
        // Some files (e.g. AppwriteQuery.kt holding `object Query`) deliberately
        // pair an Appwrite-prefixed filename with a short public type; the
        // matching short name is already taken by a sibling file.
        "ktlint_standard_filename" to "disabled",
    )

// Format the root build script and shared config files too, not just modules.
apply(plugin = "com.diffplug.spotless")
extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(ktlintVersion)
            .editorConfigOverride(ktlintOverrides)
    }
    format("misc") {
        target("*.md", ".gitignore", "config/**/*.yml")
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
    }
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    // Track coverage on every published module's JVM target.
    if (name in publishedModules) {
        apply(plugin = "org.jetbrains.kotlinx.kover")
    }

    // Auto-format + lint Kotlin via ktlint (ktlint_official style, see .editorconfig).
    apply(plugin = "com.diffplug.spotless")
    extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("src/**/*.kt")
            targetExclude("**/build/**")
            ktlint(ktlintVersion)
                .editorConfigOverride(ktlintOverrides)
            trimTrailingWhitespace()
            endWithNewline()
        }
        kotlinGradle {
            target("*.gradle.kts")
            ktlint(ktlintVersion)
                .editorConfigOverride(ktlintOverrides)
        }
    }

    extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        parallel = true
        buildUponDefaultConfig = true
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        baseline = file("detekt-baseline.xml")
        // Analyze every Kotlin source set declared by the modules, not just JVM main.
        source.setFrom(
            files(
                "src/commonMain/kotlin",
                "src/commonTest/kotlin",
                "src/jvmMain/kotlin",
                "src/iosMain/kotlin",
                "src/wasmJsMain/kotlin",
            ).filter { it.exists() },
        )
    }
}

// Aggregate coverage across all published modules.
dependencies {
    publishedModules.forEach { kover(project(":$it")) }
}

// Coverage gate: fail the build if aggregate line coverage regresses below the
// floor. iOS/Wasm/desktop code can't be unit-tested without a host, so the
// floor tracks the common logic that can. Run `./gradlew koverVerify`.
kover {
    reports {
        verify {
            rule("Aggregate line coverage") {
                minBound(10)
            }
        }
    }
}
