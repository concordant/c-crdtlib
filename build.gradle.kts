import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("multiplatform") version "1.3.72"
    kotlin("plugin.serialization") version "1.3.72"
    id("org.jetbrains.dokka") version "0.10.0"
}

repositories {
    jcenter()
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

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("com.github.ntrrgc:ts-generator:1.1.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val nodeJsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js")) 
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.20.0")
            }
        }

        val nodeJsTest by getting {
            dependencies {
                implementation(kotlin("test-js")) 
            }
        }
    }

    tasks {
        val dokka by getting(DokkaTask::class) {

            outputFormat = "html"
            outputDirectory = "$buildDir/docs"

            multiplatform {
                register("common") {}
            }
        }
    }
}
