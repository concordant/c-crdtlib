/*
* MIT License
*
* Copyright © 2022, Concordant and contributors.
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

package crdtlib.utils

import crdtlib.crdt.DeltaCRDT

/**
 * This abstract class represents a contextual environment.
 */
abstract class Environment {

    /**
     * The client uid linked to this environment.
     */
    val uid: ClientUId

    constructor(uid: ClientUId) {
        this.uid = uid
    }

    /**
     * Gets the state associated with the environment.
     *
     * @return the current state.
     */
    @Name("getState")
    abstract fun getState(): VersionVector

    /**
     * Generates a monotonically increasing timestamp.
     *
     * @return the generated timestamp.
     */
    @Name("tick")
    abstract fun tick(): Timestamp

    /**
     * Updates the state with the given timestamp.
     *
     * @param ts the given timestamp.
     */
    @Name("updateTs")
    abstract fun update(ts: Timestamp)

    /**
     * Updates the state with the given version vector.
     *
     * @param vv the given version vector.
     */
    @Name("updateVv")
    abstract fun update(vv: VersionVector)

    /**
     * Hook method called by CRDTs on every read operation
     *
     * Note: a delta generation is not considered as an operation
     *       and MUST NOT trigger onRead.
     * @param obj the accessed object.
     */
    @Name("onRead")
    open fun onRead(obj: DeltaCRDT) {}

    /**
     * Hook method called by CRDTs on every write operation
     *
     * Note: a merge is not considered as an operation,
     *       and MUST trigger [onMerge] instead.
     * @param obj the modified object.
     * @param delta the delta from this operation.
     */
    @Name("onWrite")
    open fun onWrite(obj: DeltaCRDT, delta: DeltaCRDT) {}

    /**
     * Hook method called by CRDTs on every merge
     *
     * Note: a merge is not considered as an operation
     *       and MUST NOT trigger [onWrite].
     * @param obj the object merge target.
     * @param delta the delta to be merged.
     * @param lastTs the foreign timestamp with greater value,
     * or null if delta does not carry timestamps.
     */
    @Name("onMerge")
    open fun onMerge(obj: DeltaCRDT, delta: DeltaCRDT, lastTs: Timestamp?) {}
}
