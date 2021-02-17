## Requirements

- The kotlin-multiplatform gradle plugin needs [Gradle](
  https://gradle.org/install/) 6.0 or later.
- Make sure you have a recent version of [Node.js](
  https://nodejs.org/en/download/) (required to generate Javascript).

## Files

### Build, configuration, metadata

- `LICENSE`
- `README.md` Doc: user
- `README.dev.md` Doc: developer
- `.git/` Config: Git
- `.gitignore`
- `settings.gradle.kts` Config: Gradle: project name
- `build.gradle.kts` Config: Gradle: metadata, dependencies,
  publications, config (Kotlin, kotest, ktlint)
- `pre-commit` Git pre-commit lint script;
  installed by `gradle assemble` (task `installGitHook`)
- `.editorconfig` Config: ktlint (& IDE)
- `.gitlab-ci.yml` Config: continuous integration

### Code

- `src/` Code
  - `commonMain/` Source code common to all platforms
  - `commonTest/` Tests  code common to all platforms
    - `kotlin/crdtlib/utils/`
      - `CustomGenerators.kt` Generators for property-based tests
    - `**/*PropTest.kt` Property-based tests
    - `**/*Test.kt` Tests
  - `jvmMain/` Source code for JVM target
    - `kotlin/crdtlib/GenerateTS.kt`
      TS typings (`*.d.ts`) generator
  - `nodeJsMain/` Source code for JavaScript target

### Artifacts (untracked)

- `build/` Project artifacts
  - `reports/tests/allTests/` Tests results,
    created and populated by `gradle allTests`
  - `build/dokka/html/crdtlib/` Auto-generated documentation,
    created and populated by `gradle dokkaHtml`
  - `libs/` Maven packages
  - `publications/npm/nodeJs/` NPM package
  - … (non exhaustive)
- `.gradle/` Gradle project cache

## Build project

The build is managed by Gradle.
Kotlin sources (code and tests) are compiled to JVM Bytecode,
and to Javascript as a Node.js package.

`gradle assemble`:
- compiles code and tests to JVM Bytecode;
- compiles code and tests to Javascript (Node.js module);
- creates a TypeScript interface
- assembles Maven packages

`gradle allTests`:
- runs JVM test suite;
- runs Node.js test suite in a server like manner;
- generate tests reports.

`gradle pack`:
- pack the NPM package

`gradle publish`:
- publish the Maven and NPM packages to the Gitlab Packages registry
  (requires authentication ; better use it via CI pipelines).

`gradle dokkaHtml`:
- creates the HTML documentation from code comments.
  See `gradle tasks` for variants other than HTML.

`gradle clean`:
- cleans the project.

`gradle tasks`:
- show all available tasks with descriptions.

## Getting Started with private Gitlab packages registry

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

### JavaScript/TypeScript and NPM

#### Installation
First setup authentication:
``` shell
$ npm config set @concordant:registry "https://gitlab.inria.fr/api/v4/packages/npm/"
$ npm config set '//gitlab.inria.fr/api/v4/packages/npm/:_authToken' "<deployOrPersonalToken>"
```
