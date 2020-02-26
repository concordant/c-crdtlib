[![pipeline status](https://gitlab.inria.fr/concordant/software/crdtlib-kotlin/badges/master/pipeline.svg)](https://gitlab.inria.fr/concordant/software/crdtlib-kotlin/commits/master)

# CRDT lib

CRDT library in kotlin for use in concordant.

## Javascript generator

Use _gradle_ for generating Javascript version.

You can also use the *kotlin_to_nodejs.sh* script that will configure everything to work using with
Node.js. You can now use the crdtlib module with JavaScript as describe in the *test_nodejs.js*
file.

## Typescript generator

For generating the TypeScript interface, just do: _mvn package_. Then copy/paste the output into a
_crdtlib.d.ts_ file. To use it add _/// \<reference path="path/to/crdtlib.d.ts" />_ at the
beginning of the TypeScript file in which you want to use the crdt library.
