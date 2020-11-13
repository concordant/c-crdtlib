/*
* Copyright © 2020, Concordant and contributors.
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
* associated documentation files (the "Software"), to deal in the Software without restriction,
* including without limitation the rights to use, copy, modify, merge, publish, distribute,
* sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all copies or
* substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
* NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package crdtlib

import crdtlib.crdt.*
import crdtlib.utils.*
import me.ntrrgc.tsGenerator.TypeScriptGenerator
import java.io.File

fun main() {
    File("build/js/packages/c-crdtlib-nodeJs/kotlin/c-crdtlib.d.ts").printWriter().use {
        with(it) {
            println("declare module 'c-crdtlib' {")
            println()
            println("export var crdtlib;")
            println("export namespace crdtlib {")
            println()
            println("export namespace utils {")
            println()
            println(
                TypeScriptGenerator(
                    rootClasses = setOf(
                        ClientUId::class,
                        Environment::class,
                        SimpleEnvironment::class,
                        Timestamp::class,
                        UnexpectedTypeException::class,
                        VersionVector::class
                    )
                ).definitionsText
            )
            println("}")
            println()
            println("export namespace crdt {")
            println()
            println(
                TypeScriptGenerator(
                    rootClasses = setOf(
                        DeltaCRDT::class,
                        LWWMap::class,
                        LWWRegister::class,
                        MVRegister::class,
                        PNCounter::class,
                        RGA::class,
                        Ratchet::class
                    )
                ).definitionsText
            )
            println("}")
            println("}")
            println("}")
        }
    }
}
