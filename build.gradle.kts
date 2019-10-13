import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
//import me.ntrrgc.tsGenerator.TypeScriptGenerator

buildscript {
    repositories {
        mavenCentral();
        maven { setUrl("https://repository.mulesoft.org/nexus/content/repositories/public/") }
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.3.50"))
    }
}

plugins {
    id("kotlin2js") version "1.3.50"
}

repositories {
    jcenter()
    maven { setUrl("https://repository.mulesoft.org/nexus/content/repositories/public/") }
}

dependencies {
    compile(kotlin("stdlib-js"))
    testCompile(kotlin("test-js"))
}


tasks {
    compileKotlin2Js {
        kotlinOptions {
            outputFile = "${sourceSets.main.get().output.resourcesDir}/crdtlib.js"
            sourceMap = true
        }
    }
    val unpackKotlinJsStdlib by registering {
        group = "build"
        description = "Unpack the Kotlin JavaScript standard library"
        val outputDir = file("$buildDir/$name/lib")
        inputs.property("compileClasspath", configurations.compileClasspath.get())
        outputs.dir(outputDir)
        doLast {
            val kotlinStdLibJar = configurations.compileClasspath.get().single {
                it.name.matches(Regex("kotlin-stdlib-js-.+\\.jar"))
            }
            copy {
                includeEmptyDirs = false
                from(zipTree(kotlinStdLibJar))
                into(outputDir)
                include("**/*.js")
                exclude("META-INF/**")
            }
            val kotlinTestLibJar = configurations.testCompileClasspath.get().single {
                it.name.matches(Regex("kotlin-test-js-.+\\.jar"))
            }
            copy {
                includeEmptyDirs = false
                from(zipTree(kotlinTestLibJar))
                into(outputDir)
                include("**/*.js")
                exclude("META-INF/**")
            }
        }
    }
    val assembleWeb by registering(Copy::class) {
        group = "build"
        description = "Assemble the web application"
        includeEmptyDirs = false
        from(unpackKotlinJsStdlib)
        from(sourceSets.main.get().output) {
            exclude("**/*.kjsm")
        }
        into("$buildDir/web")
    }
    assemble {
        dependsOn(assembleWeb)
    }
}

/*tasks.register("generate") {
    dependsOn("build")
    doLast {
        println(TypeScriptGenerator(
            rootClasses = setOf(
                crdtlib.utils.DCId::class
            )
        ).definitionsText)
    }
}*/

