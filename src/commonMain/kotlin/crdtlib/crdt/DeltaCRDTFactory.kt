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

/**
* This class repressents a delta crdt factory.
*/
class DeltaCRDTFactory {

    companion object {

        /**
         * Create an empty delta crdt of a given type.
         * @param crdtType the given delta crdt type.
         * @return an empty delta crdt of the given type.
         */
        @Name("createDeltaCRDT")
        fun createDeltaCRDT(crdtType: String): DeltaCRDT {
            when (crdtType) {
                "PNCounter" -> {
                    return PNCounter()
                }
                "BCounter" -> {
                    return BCounter()
                }
                "LWWRegister" -> {
                    return LWWRegister()
                }
                "MVRegister" -> {
                    return MVRegister()
                }
                "Ratchet" -> {
                    return Ratchet()
                }
                "RGA" -> {
                    return RGA()
                }
                "LWWMap" -> {
                    return LWWMap()
                }
                "MVMap" -> {
                    return MVMap()
                }
                "Map" -> {
                    return Map()
                }
                else -> {
                    throw IllegalArgumentException("Unknown DeltaCRDT type: $crdtType")
                }
            }
        }
    }
}
