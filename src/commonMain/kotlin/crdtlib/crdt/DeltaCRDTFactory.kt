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

package crdtlib.crdt

import crdtlib.utils.Environment
import crdtlib.utils.Name

/**
 * A delta-CRDT factory.
 */
class DeltaCRDTFactory {

    companion object {

        /**
         * Create an empty delta crdt of a given [crdtType]
         * with associated environment.
         */
        @Name("createDeltaCRDT")
        fun createDeltaCRDT(crdtType: String, env: Environment? = null): DeltaCRDT {
            when (crdtType) {
                "PNCounter" -> {
                    if (env != null) return PNCounter(env)
                    return PNCounter()
                }
                "BCounter" -> {
                    if (env != null) return BCounter(env)
                    return BCounter()
                }
                "LWWRegister" -> {
                    if (env != null) return LWWRegister(env)
                    return LWWRegister()
                }
                "MVRegister" -> {
                    if (env != null) return MVRegister(env)
                    return MVRegister()
                }
                "Ratchet" -> {
                    if (env != null) return Ratchet(env)
                    return Ratchet()
                }
                "RGA" -> {
                    if (env != null) return RGA(env)
                    return RGA()
                }
                "LWWMap" -> {
                    if (env != null) return LWWMap(env)
                    return LWWMap()
                }
                "MVMap" -> {
                    if (env != null) return MVMap(env)
                    return MVMap()
                }
                "Map" -> {
                    if (env != null) return Map(env)
                    return Map()
                }
                else -> {
                    throw IllegalArgumentException("Unknown DeltaCRDT type: $crdtType")
                }
            }
        }
    }
}
