# Conflict-Free Replicated Datatypes (CRDT) library

[![pipeline status](https://gitlab.inria.fr/concordant/software/crdtlib-kotlin/badges/master/pipeline.svg)](
https://gitlab.inria.fr/concordant/software/crdtlib-kotlin/commits/master)
[![License](https://img.shields.io/badge/license-MIT-green)](
https://opensource.org/licenses/MIT)

CRDT library in Kotlin for the Concordant platform API.
This project is based on [Kotlin multiplatform feature](
https://kotlinlang.org/docs/reference/multiplatform.html).

The Kotlin code is compiled to both JVM Bytecode and Javascript;
see respective Getting Started sections
for [Java and Gradle](#java-and-gradle)
and [JavaScript/TypeScript and NPM](#javascript-typescript-and-npm)
below.

See also the [full documentation of the master branch on GitLab pages](
https://concordant.gitlabpages.inria.fr/software/c-crdtlib/c-crdtlib).


## Getting Started

This library is delivered as both a Maven package and an NPM package
in a private [Gitlab Packages registry](
https://gitlab.inria.fr/concordant/software/c-crdtlib/-/packages).

To use it, you will need to authenticate to Gitlab, using either:
- a Gitlab [deploy token](
  https://docs.gitlab.com/ee/user/project/deploy_tokens/)
  with at least the `read_package_registry` scope, or
- a Gitlab [personal access token](
  https://docs.gitlab.com/ee/user/profile/personal_access_tokens.html)
  with at least the `read_api` scope.

### Kotlin and Gradle

To setup authentication, add the token to your gradle properties file
`~/.gradle/gradle.properties`:
``` shell
gitlabToken=<deployOrPersonalToken>
```

Then, in your project configuration build file `build.gradle.kts`:
- Ensure one of `jcenter()` or `mavenCentral()`
  is listed in the `repositories{}` for usual packages.
- Add JitPack and Gitlab to repositories:
``` kotlin
repositories {
    maven(url = "https://jitpack.io")
    maven {
        url = uri("https://gitlab.inria.fr/api/v4/projects/18591/packages/maven")
        credentials(HttpHeaderCredentials::class) {
            // set to "Deploy-Token" here if appropriate
            name = "Private-Token"
            val gitlabToken: String by project
            value = gitlabToken
        }
        authentication {
            create<HttpHeaderAuthentication>("header")
        }
    }
}
```
- Add the c-crdtlib Maven package as a dependency:
``` kotlin
dependencies {
    implementation("concordant:c-crdtlib:x.y.z")
}
```

#### Usage
``` kotlin
// consider refining to avoid wildcard import
import crdtlib.*

fun main() {
    // an Environment is required to manage Timestamps
    // (aka Operation IDs)
    val env = SimpleEnvironment(ClientUId("myClientUid"))

    // create and use a PNCounter:
    val ctr = PNCounter(env)
    ctr.increment(5)
    ctr.decrement(2)
    println(ctr.get())    // 3

    // Now a MVRegister, with two replicas:
    val lMVReg = MVRegister(env)
    lMVReg.assign("A")

    // simulate a remote client with an environment:
    val renv = SimpleEnvironment(ClientUId("remoteClientUid"))
    val rMVReg = MVRegister(env)

    // Merge local state to remote (using full state merge)
    // SimpleEnvironment automatically updates clock on merge
    rMVReg.merge(lMVReg)

    println(rMVReg.get())    // [A]

    // Modify both replicas concurrently
    lMVReg.assign("L")
    println(lMVReg.get())    // [L]

    rMVReg.assign("R")
    println(rMVReg.get())    // [R]

    // …then merge, using deltas

    // local -> remote
    // Use remote environment state as origin for delta
    val rVV = renv.getState()
    rMVReg.merge(lMVReg.generateDelta(rVV))
    println(rMVReg.get())    // [L, R]

    // remote -> local
    val lVV = lenv.getState()
    rMVReg.merge(lMVReg.generateDelta(lVV))
    println(lMVReg.get())    // [L, R]
}
```
### JavaScript/TypeScript and NPM

#### Installation
First setup authentication:
``` shell
$ npm config set @concordant:registry "https://gitlab.inria.fr/api/v4/packages/npm/"
$ npm config set '//gitlab.inria.fr/api/v4/packages/npm/:_authToken' "<deployOrPersonalToken>"
```

Install the package:
``` shell
$ npm i @concordant/c-crdtlib
```

#### Usage

As JavaScript does not support overloading,
some method names differ from Kotlin names.

``` typescript
// Import the package
import {crdtlib} from @concordant/c-crdtlib;

// an Environment is required to manage Timestamps
// (aka Operation IDs)
const env = new crdtlib.utils.SimpleEnvironment(
            new crdtlib.utils.ClientUId("myClientUid"))

// create and use a PNCounter:
const ctr = new crdtlib.crdt.PNCounter(env)
ctr.increment(5)
ctr.decrement(2)
console.log(ctr.get())    // 3

// Now a MVRegister, with two replicas:
const lMVReg = new crdtlib.crdt.MVRegister(env)
lMVReg.assign("A")

// simulate a remote client with an environment:
const renv = new crdtlib.crdt.SimpleEnvironment(
            new crdtlib.utils.ClientUId("remoteClientUid"))
const rMVReg = new crdtlib.crdt.MVRegister(env)

// Merge local state to remote (using full state merge)
// SimpleEnvironment automatically updates clock on merge
rMVReg.merge(lMVReg)
console.log(rMVReg.get())    // [A]

// Modify both replicas concurrently
lMVReg.assign("L")
console.log(lMVReg.get())    // [L]

rMVReg.assign("R")
console.log(rMVReg.get())    // [R]

// …then merge, using deltas

// local -> remote
// Use remote environment state as origin for delta
const rVV = renv.getState()
rMVReg.merge(lMVReg.generateDelta(rVV))
console.log(rMVReg.get())    // [L, R]

// remote -> local
const lVV = lenv.getState()
rMVReg.merge(lMVReg.generateDelta(lVV))
console.log(lMVReg.get())    // [L, R]
```
