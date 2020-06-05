#! /bin/bash

# Compile the entire project
#mvn package
gradle build

# Install the Kotlin module for JavaScript
npm install kotlin

# Move the JavaScript generated code to node_modules
cp -r build/js/packages/crdtlib-kotlin-nodeJs node_modules/crdtlib
