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
abstract class DeltaCRDT<CrdtT> : Delta<CrdtT> {

    /**
    * Protected abstract method generating the delta from a given version vector.
    * @param vv the given version vector.
    * @return the delta from the version vector.
    */
    protected abstract fun generateDeltaProtected(vv: VersionVector): Delta<CrdtT>

    /**
    * Protected abstract methods merging a given delta into this CRDT.
    * @param delta the delta to be merge.
    */
    protected abstract fun mergeProtected(delta: Delta<CrdtT>)

    /**
    * Generates the delta from a given version vector by calling the protected abstract method.
    * This trick is used to be able to force the method name in the generated javascript.
    * @param vv the given version vector.
    * @return the delta from the version vector.
    */
    @Name("generateDelta")
    fun generateDelta(vv: VersionVector): Delta<CrdtT> {
        return generateDeltaProtected(vv)
    }

    /**
    * Merges a given delta into this CRDT by calling the protected method.
    * This trick is used to be able to force the method name in the generated javascript.
    * @param delta the delta to be merge.
    */
    @Name("merge")
    fun merge(delta: Delta<CrdtT>) {
        mergeProtected(delta)
    }
}
