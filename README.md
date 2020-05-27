[![pipeline status](https://gitlab.inria.fr/concordant/software/crdtlib-kotlin/badges/master/pipeline.svg)](https://gitlab.inria.fr/concordant/software/crdtlib-kotlin/commits/master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# CRDT lib

CRDT library in Kotlin for the Concordant platform API.

## Getting started

### Requirements

- The project is built using the Kotlin command-line compiler, [follow the official documentation to install it](https://kotlinlang.org/docs/tutorials/command-line.html);
- Download and install Maven from [Apache Maven website](http://maven.apache.org/download.cgi#Installation);
- Make sure you have the latest version of Node.js (required if you want to generate Javascript/Typescript): [see the official installation guide](https://nodejs.org/en/download/)

### Javascript generator

You can also use the script *kotlin_to_nodejs.sh* that will configure everything to work using with
Node.js. You can now use the CRDTlib module with JavaScript as describe in the *test_nodejs.js*
file.

If you want only to generate JavaScript code and tests you can run _mvn package_.

### Typescript generator

For generating the TypeScript interface, just do: _mvn package_. Then copy/paste the output into a
_crdtlib.d.ts_ file. To use it add _/// \<reference path="path/to/crdtlib.d.ts" />_ at the
beginning of the TypeScript file in which you want to use the CRDT library.