# Conflict-Free Replicated Datatypes (CRDT) library

[![](https://gitlab.inria.fr/concordant/software/crdtlib-kotlin/badges/master/pipeline.svg)](
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

This library is delivered as both a [Maven package]() and an [NPM package](https://www.npmjs.com/package/@concordant/c-crdtlib).

### Kotlin and Gradle

In your project configuration build file `build.gradle.kts`:
- Ensure `mavenCentral()`
  is listed in the `repositories{}` for usual packages.
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

## CRDT Object API

The Concordant CRDT library currently provides three main classes of CRDTs:
- Counters: store an integer value which can be decremented and incremented.
- Registers: store a string value which can be reassigned.
- Collections: store multiple string values which can be reassigned.

All object types support the following methods:

* `toJson` serialises the object into a JSON string representing its current
  state.
* `DeltaCRDT.fromJson` takes a JSON string and converts it to an object
  state of its corresponding type (and fails if the JSON does not parse
  properly).

The following sections briefly describes each type specificities.

**Note:** interfaces described here are not stable yet
  and will change in the next version of the CRDTlib.

### Counters

`PNCounter` is an integer counter, with increment and decrement operations.

`BCounter` is a PNCounter whose value cannot become negative.

Counters provides an `increment(nb)` and `decrement(nb)` methods to modify
their value; their content can be retrieved using the `get()` method.

### Registers

`LWWRegister` stores a string; supports assignment; concurrent assignments
resolve to a single value.

`MVRegister` stores a string; supports assignment; concurrent assignments are
all retained.

`Ratchet` is a register with values taken from a custom semi-lattice.
Concurrent assignments retain their least upper bound.
Only string values with default ordering are currently supported.

All registers support assignment using the `assign(value)` method; their
content can be retrieved using the `get()` method.

### Collections

`RGA` is an ordered sequence of strings.
- `insertAt(idx, elem)` inserts an element to the RGA.
- `removeAt(idx)` removes an element.
- `getAt(idx)` returns the element at a particular index.
- `get()` returns the whole RGA content as a list.
- `iterator()` returns an iterator over the RGA.

#### Maps

Map types currently only support
`Int`, `Double`, `Boolean`, and `String` values.
Specific methods are provided for each type (ex.: `getString(key)`),
denoted here by `method<TYPE>()`.
Note that a map may contain values of different types under the same key.

`LWWMap` maps strings to LWW scalar values.
- `put(key, value)` adds or updates an element to the map.
- `delete<TYPE>(key)` removes an element from the map.
- `get<TYPE>(key)` retrieves the values associated with `key`.
- `iterator<TYPE>()` returns an iterator on the map key-value pairs.

`MVMap` maps strings to MV scalar values.
It supports the same methods as LWWMap,
but values returned by `get<TYPE>(key)` and `iterator<TYPE>()`
are multi-values (sets).

`Map` maps strings to LWW, MV and PNcounter values.
Specific methods are provided for each merging strategy
(`LWW`, `MV` or `Cnt`)
denoted here by `method<MS>()` (ex.: `getCnt(key)`).
- `put<MS>(key, value)` adds or updates an element to the map.
- `delete<MS><TYPE>(key)` removes an element from the map.
- `get<MS><TYPE>(key)` retrieves the values associated with `key`.
- `iterator<MS><TYPE>()` returns an iterator on the map key-value pairs.
- `increment(key, nb)`, `decrement(key, nb)`
  increments/decrements the corresponding PNCounter value.
