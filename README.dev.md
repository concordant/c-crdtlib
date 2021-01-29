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
