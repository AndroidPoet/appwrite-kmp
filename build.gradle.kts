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
    // Kotlin/JVM- und Android-Bytecode konsistent auf 17 halten
    // (passend zu den Android compileOptions, sonst: "Inconsistent JVM-target compatibility")
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}