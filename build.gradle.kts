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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
    id("org.jetbrains.dokka") version "0.10.0"
}

repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://jitpack.io")
}

configurations {
    val ktlint by creating
}
dependencies {
    "ktlint"("com.pinterest:ktlint:0.39.0")
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

        commonMain {
            dependencies {
                implementation(kotlin("reflect"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")
            }
        }

        commonTest {
            dependencies {
                implementation("io.kotest:kotest-property:4.3.0")
                implementation("io.kotest:kotest-assertions-core:4.3.0")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("com.github.ntrrgc:ts-generator:1.1.1")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation("io.kotest:kotest-runner-junit5-jvm:4.3.0")
            }
        }

        val nodeJsTest by getting {
            dependencies {
                implementation("io.kotest:kotest-core-js:4.2.0.RC2")
            }
        }
    }

    tasks {
        dokka {
            outputFormat = "html"
            outputDirectory = "$buildDir/docs"

            multiplatform {
                register("common") {}
            }
        }
        register<JavaExec>("ktlint") {
            group = "verification"
            description = "Ktlint: check"
            classpath = configurations["ktlint"]
            main = "com.pinterest.ktlint.Main"
        }
        register<JavaExec>("ktlintFix") {
            group = "verification"
            description = "Ktlint: fix"
            classpath = configurations["ktlint"]
            main = "com.pinterest.ktlint.Main"
            args("-F")
        }
        register<Copy>("installGitHook") {
            from("pre-commit")
            into(".git/hooks")
            // Kotlin does not support octal litterals
            fileMode = 7 * 64 + 7 * 8 + 7
        }
    }
}
tasks.getByPath("assemble").dependsOn("installGitHook")

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("kotest.framework.timeout", 5000)
    systemProperty("kotest.framework.invocation.timeout", 4000)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
