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

import crdtlib.utils.Name
import crdtlib.utils.VersionVector

/**
* Interface for delta based CRDT
*/
abstract class DeltaCRDT {

    /**
     * Generates the delta from a given version vector by calling the protected abstract method.
     * @param vv the given version vector.
     * @return the delta from the version vector.
     */
    @Name("generateDelta")
    abstract fun generateDelta(vv: VersionVector): DeltaCRDT

    /**
     * Merges a given delta into this CRDT by calling the protected method.
     * @param delta the delta to be merge.
     */
    @Name("merge")
    abstract fun merge(delta: DeltaCRDT)

    /**
     * Serializes this delta crdt to a json string.
     * @return the resulted json string.
     */
    @Name("toJson")
    abstract fun toJson(): String

    companion object {
        /**
         * Deserializes a given json string in the corresponding crdt type.
         * @param json the given json string.
         * @return the resulted delta crdt.
         */
        fun fromJson(json: String): DeltaCRDT {
            val regex = """"_type":"(\w+)",""".toRegex()
            val matchResult = regex.find(json)
            val crdtType = matchResult?.groups?.get(1)?.value
            when (crdtType) {
                "PNCounter" -> {
                    return PNCounter.fromJson(json)
                }
                "BCounter" -> {
                    return BCounter.fromJson(json)
                }
                "LWWRegister" -> {
                    return LWWRegister.fromJson(json)
                }
                "MVRegister" -> {
                    return MVRegister.fromJson(json)
                }
                "Ratchet" -> {
                    return Ratchet.fromJson(json)
                }
                "RGA" -> {
                    return RGA.fromJson(json)
                }
                "LWWMap" -> {
                    return LWWMap.fromJson(json)
                }
                "MVMap" -> {
                    return MVMap.fromJson(json)
                }
                "Map" -> {
                    return Map.fromJson(json)
                }
                else -> {
                    throw IllegalArgumentException("DeltaCRDT cannot deserialize type: $crdtType")
                }
            }
        }
    }
}
