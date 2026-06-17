plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.nexusPlugin) apply false
}
subprojects {
    // Falls das Signing-Plugin geladen wurde, schalte es aus
    plugins.withId("maven-publish") {
        tasks.withType<org.gradle.plugins.signing.Sign>().configureEach {
            enabled = false
        }
    }
}