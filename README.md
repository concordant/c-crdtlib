# Conflict-Free Replicated Datatypes (CRDT) library

[![pipeline status](https://gitlab.inria.fr/concordant/software/crdtlib-kotlin/badges/master/pipeline.svg)](https://gitlab.inria.fr/concordant/software/crdtlib-kotlin/commits/master)
[![License](https://img.shields.io/badge/license-MIT-green)](https://opensource.org/licenses/MIT)

CRDT library in Kotlin for the Concordant platform API. This project is based on [Kotlin
multiplatform feature](https://kotlinlang.org/docs/reference/multiplatform.html). The Kotlin code is
compiled to JVM Bytecode and to Javascript.

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

## Build project

The building is managed through the use of Gradle. Kotlin sources (code and tests) are compiled to
JVM Bytecode and to Javascript in the form of a Node.js package.  To simplify stuffs, a *Makefile*
is provided.

*make build*:
- compiles code and tests to JVM Bytecode;
- compiles code and tests to Javascript (Node.js package);
- creates a TypeScript interface.

*make test*:
- runs JVM test suite;
- runs Node.js test suite in a server like manner;
- a report containing all tests results can be found in the file
  *build/reports/tests/allTests/index.html*.

*make deploy*:
- creates a npm package ready to be used;
- the package is in the *deploy/npm/* directory.

*make doc*:
- creates the documentation from code comments;
- documentation is accessible at *build/docs/crdtlib-kotlin/index.html*.

*make clean*:
- cleans the project.
