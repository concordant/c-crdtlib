#! /bin/bash

# Install the Kotlin module for JavaScript
npm install kotlin

# Compile the Kotlin crdtlib into Javascript. Sources are put in the node_modules directory to be
# used using Node.js
kotlinc-js -output node_modules/crdtlib/crdtlib.js -meta-info src/main/kotlin/crdtlib/*/*.kt -module-kind commonjs

# Create a package.json file for the crdtlib module. It should be put in the node_modules/crdtlib/
# directory, and specify the dependency to the kotlin module.
echo '{
  "name": "crdtlib",
  "version": "1.0.0",
  "description": "CRDT library in javascript for use in concordant.",
  "main": "crdtlib.js",
  "scripts": {
    "test": "test"
  },
  "repository": {
    "type": "git",
    "url": "git@gitlab.inria.fr:concordant/software/crdtlib-kotlin.git"
  },
  "author": "",
  "license": "MIT",
  "dependencies": {
    "kotlin": "1.3.61"
  }
}' > node_modules/crdtlib/package.json