import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("multiplatform") version "1.3.72"
    kotlin("plugin.serialization") version "1.3.72"
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

kotlin {

    jvm()
    js("nodeJs") {
        nodejs {}
    }

    sourceSets {

        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(kotlin("reflect"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:0.20.0")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("com.github.ntrrgc:ts-generator:1.1.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
            }
        }

        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(kotlin("test-junit"))
                implementation("io.kotest:kotest-property-jvm:4.1.1")
                implementation("io.kotest:kotest-runner-junit5-jvm:4.1.1")
                implementation("io.kotest:kotest-assertions-core-jvm:4.1.1")

            }
        }

        js("nodeJs").compilations["main"].defaultSourceSet  {
            dependencies {
                implementation(kotlin("stdlib-js")) 
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.20.0")
            }
        }

        js("nodeJs").compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

tasks.withType<Test> { useJUnitPlatform() }

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}