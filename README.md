# Conflict-Free Replicated Datatypes (CRDT) library

[![pipeline status](https://gitlab.inria.fr/concordant/software/crdtlib-kotlin/badges/master/pipeline.svg)](https://gitlab.inria.fr/concordant/software/crdtlib-kotlin/commits/master)
[![License](https://img.shields.io/badge/license-MIT-green)](https://opensource.org/licenses/MIT)

CRDT library in Kotlin for the Concordant platform API. This project is based on [Kotlin
multiplatform feature](https://kotlinlang.org/docs/reference/multiplatform.html). The Kotlin code is
compiled to JVM Bytecode and to Javascript.

Documentation of the master branch is automatically updated and deployed on the [GitLab](https://concordant.gitlabpages.inria.fr/software/c-crdtlib/c-crdtlib).

## Requirements

- Download and install Gradle from [Gradle website](https://gradle.org/install/);
- Make sure you have the latest version of Node.js (required if you want to generate Javascript):
  [see the official installation guide](https://nodejs.org/en/download/).

## Project overview

The code is in the directory *src/*. This directory contains multiple directories:

- *commonMain*: contains source code that is common to all platform;
- *commonTest*: contains source code that is common to all platform;
- *jvmMain*: contains specific source code for JVM target;
- *jvmTest*: contains specific source code for JVM target tests;
- *jsMain*: contains specific source code for JavaScript target;
- *jsTest*: contains specific source code for JavaScript target tests.

## Usage

This library is delivered as an NPM package and a Maven in [Gitlab Packages](
https://gitlab.inria.fr/concordant/software/c-crdtlib/-/packages) (as a private
registry).

### NPM install

Get a deploy token or personal access token from Gitlab,
with at least the read_package_registry scope.  
Then setup authentication:
``` shell
$ npm config set @concordant:registry "https://gitlab.inria.fr/api/v4/packages/npm/"
$ npm config set '//gitlab.inria.fr/api/v4/packages/npm/:_authToken' "<deployOrPersonalToken>"
```

Install the package:
``` shell
$ npm i @concordant/c-crdtlib
```

And use it:
``` typescript
import * from @concordant/c-crdtlib;
```

### Gradle install

Get a deploy token or personal access token from Gitlab, with at least the
read_package_registry scope.  
Then add the token to your gradle propeties file *~/.gradle/gradle.properties*:
```
gitLabPrivateToken="<deployOrPersonalToken>"
```

In you project's configuration build file *build.gradle.kts* add the GitLab
registry as a repository:
```
repositories {
    maven {
        url = uri("https://gitlab.inria.fr/api/v4/projects/18591/packages/maven")
        credentials(HttpHeaderCredentials::class) {
            name = "Deploy-Token"
            val gitLabPrivateToken: String by project
            value = gitLabPrivateToken
        }
        authentication {
            create<HttpHeaderAuthentication>("header")
        }
    }
}
```

And add the maven package as a dependency:
```
dependencies {
    implementation("concordant:c-crdtlib:x.y.z")
}
```

## Build project

The building is managed through the use of Gradle. Kotlin sources (code and tests) are compiled to
JVM Bytecode and to Javascript in the form of a Node.js package.

*gradle assemble*:
- compiles code and tests to JVM Bytecode;
- compiles code and tests to Javascript (Node.js package);
- creates a TypeScript interface
- assembles a NPM package

*gradle allTests*:
- runs JVM test suite;
- runs Node.js test suite in a server like manner;
- a report containing all tests results can be found in the file
  *build/reports/tests/allTests/index.html*.

*gradle pack*:
- pack the NPM package

*gradle publish*:
- publish the NPM package to the Gitlab Packages registry
  (requires authentication ; better use it via CI pipelines).

*gradle dokkaHtml*:
- creates the documentation from code comments;
- documentation is accessible at *build/dokka/html/crdtlib/index.html*.
See `gradle tasks` for variants other than HTML.

*gradle clean*:
- cleans the project.

*gradle tasks*:
- show all available tasks with descriptions.
