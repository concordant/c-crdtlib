plugins {
    kotlin("multiplatform") version "1.3.72" 
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

kotlin {
    jvm()
    js("nodeJs") {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    useFirefox()
                }
            }
        }
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib")) 
                implementation(kotlin("reflect")) 
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
            }
        }

        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        js("nodeJs").compilations["main"].defaultSourceSet  {
            dependencies {
                implementation(kotlin("stdlib-js")) 
            }
        }

        js("nodeJs").compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test-js")) 
            }
        }
    }
}
