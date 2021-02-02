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

description = "Concordant Conflict-Free Replicated Datatypes (CRDT) library"
group = "concordant"
version = "1.0.4"

plugins {
    kotlin("multiplatform") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
    id("org.jetbrains.dokka") version "1.4.10.2"
    id("maven-publish")
    id("lt.petuska.npm.publish") version "1.0.2"
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

// Kotlin build config, per target
kotlin {
    // do not remove, even if empty
    jvm() {
        // uncomment if project contains Java source files
        //        withJava()
    }

    // Define "nodeJS" platform
    js("nodeJs") {
        // build for nodeJS
        nodejs {}
    }

    // Dependencies, per source set
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
                implementation("io.kotest:kotest-property:4.3.1")
                implementation("io.kotest:kotest-assertions-core:4.3.1")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("com.github.ntrrgc:ts-generator:1.1.1")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation("io.kotest:kotest-runner-junit5-jvm:4.3.1")
            }
        }

        val nodeJsTest by getting {
            dependencies {
                implementation("io.kotest:kotest-framework-engine:4.3.1")
            }
        }
    }

    tasks {
        register<JavaExec>("tsgen") {
            group = "build"
            description = "Generate .d.ts description file"
            dependsOn("compileKotlinJvm")
            dependsOn("compileKotlinNodeJs")
            val mainClasses = kotlin.targets["jvm"].compilations["main"]
            classpath = configurations["jvmRuntimeClasspath"] + mainClasses.output.classesDirs
            main = "crdtlib.GenerateTSKt"
            outputs.file("$buildDir/js/packages/c-crdtlib-nodeJs/kotlin/c-crdtlib.d.ts")
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
            group = "verification"
            description = "Install Pre-commit linting hook"
            from("pre-commit")
            into(".git/hooks")
            // Kotlin does not support octal litterals
            fileMode = 7 * 64 + 7 * 8 + 7
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("kotest.framework.timeout", 5000)
    systemProperty("kotest.framework.invocation.timeout", 4000)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

publishing {
    repositories {
        maven {
            name = "Gitlab"
            url = uri(
                "https://gitlab.inria.fr/api/v4/projects/" +
                    "${System.getenv("CI_PROJECT_ID")}/packages/maven"
            )
            credentials(HttpHeaderCredentials::class) {
                name = "Job-Token"
                value = System.getenv("CI_JOB_TOKEN")
            }
            authentication {
                create<HttpHeaderAuthentication>("header")
            }
        }
    }
}

npmPublishing {
    organization = group as String
    repositories {
        repository("Gitlab") {
            registry = uri("https://gitlab.inria.fr/api/v4/projects/${System.getenv("CI_PROJECT_ID")}/packages/npm")
            authToken = System.getenv("CI_JOB_TOKEN")
        }
    }
    publications {
        val nodeJs by getting {
            packageJson {
                types = "c-crdtlib.d.ts"
                "description" to project.description
            }
        }
    }
}

// tasks dependencies
tasks {
    named("nodeJsMainClasses") {
        dependsOn("tsgen")
    }
    assemble {
        dependsOn("installGitHook")
    }
}
