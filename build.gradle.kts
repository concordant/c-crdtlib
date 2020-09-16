// Copyright © 2020, Concordant and contributors.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
// associated documentation files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge, publish, distribute,
// sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or
// substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
// NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
// OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

    jvm() {
        withJava()
        val jvmJar by tasks.getting(org.gradle.jvm.tasks.Jar::class) {
            doFirst {
                manifest {
                    attributes["Main-Class"] = "crdtlib.GenerateTSKt"
                }
                from(configurations.getByName("runtimeClasspath").map { if (it.isDirectory) it else zipTree(it) })
            }
        }
    }

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
                implementation("io.kotest:kotest-property:4.1.1")
                implementation("io.kotest:kotest-assertions-core:4.1.1")
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
                implementation(kotlin("stdlib-jdk8"))
                implementation(kotlin("test-junit"))
                implementation("io.kotest:kotest-property-jvm:4.1.1")
                implementation("io.kotest:kotest-runner-junit5-jvm:4.1.1")
                implementation("io.kotest:kotest-assertions-core-jvm:4.1.1")

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
                implementation("io.kotest:kotest-property-js:4.1.1")
                implementation("io.kotest:kotest-core-js:4.1.1")
                implementation("io.kotest:kotest-assertions-core-js:4.1.1")
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

tasks.withType<Test> { useJUnitPlatform() }

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
