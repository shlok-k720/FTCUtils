import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://repo.dairy.foundation/releases/")
    google()
}

android {
    namespace = "com.rmsftc.utils.ftc"
    compileSdk = 30

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_1_8
    }
}

dependencies {
    // The core types are part of this module's public API.
    api(project(":core"))
    compileOnly(libs.pedropathing.core)

    // The telemetry types are part of this module's public API.
    api(project(":telemetry"))

    // Kotlin types appear in the public API, so the stdlib must reach consumers.
    api(kotlin("stdlib"))

    // Provided by the host robot app at runtime. Compile against the same
    // version the consuming Quickstart uses; never use implementation here.
    compileOnly(libs.bundles.ftc)

    implementation(libs.sloth)

    implementation(libs.jnanoid)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = project.group.toString()
            artifactId = "ftc"
            version = project.version.toString()

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}