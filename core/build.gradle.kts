import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    kotlin("jvm")
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_1_8
    }
}

dependencies {
    // Kotlin types appear in the public API (LimiterConfig), so the stdlib
    // must be visible to consumers too. kotlin("stdlib") follows the plugin version.
    api(kotlin("stdlib"))

    // The geometry family (Pose/Vector/Vector2D/DriveVector) extends Pedro
    // Pathing core classes. Pedro core is pure Java (no Android types), so
    // it compiles against this JVM module. Provided by the consuming robot
    // app at runtime — consumers using the geometry family declare Pedro
    // themselves, as they already do; never use implementation here.
    compileOnly(libs.pedropathing.core)
    testImplementation(libs.pedropathing.core)

    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = project.group.toString()
            artifactId = "core"
            version = project.version.toString()

            afterEvaluate {
                from(components["java"])
            }
        }
    }
}