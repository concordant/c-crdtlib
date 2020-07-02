# CRDT lib

[![pipeline status](https://gitlab.inria.fr/concordant/software/crdtlib-kotlin/badges/master/pipeline.svg)](https://gitlab.inria.fr/concordant/software/crdtlib-kotlin/commits/master)
[![License](https://img.shields.io/badge/license-MIT-green)](https://opensource.org/licenses/MIT)

CRDT library in Kotlin for the Concordant platform API. This project is based on Kotlin multiplatform feature.

## Getting started

### Requirements

- Download and install Gradle from [Gradle website](https://gradle.org/install/);
- Make sure you have the latest version of Node.js (required if you want to generate Javascript/Typescript): [see the official installation guide](https://nodejs.org/en/download/).

### Build project

The building is managed through the use of Gradle. Kotlin sources (code and tests) are compiled to JVM Bytecode and to Javascript in the form of a Node.js package. The command *gradle build* launches the following tasks:

- compile code and tests to JVM Bytecode;
- compile code and tests to Javascript (Node.js package);
- run JVM test suite;
- run Node.js test suite in a server like manner.

A report containing all tests result can be found in the file *build/reports/tests/allTests/index.html*.

### Ready to test Node.js environment

You can use the script *kotlin_to_nodejs.sh* that will configure everything to use the Node.js generated package in the root directory of the project. You can now use the CRDTlib module with Javascript as described in the *test_nodejs.js* file.
