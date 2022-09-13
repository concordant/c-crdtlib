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
 *
 * A simple environment generating increasing monotonic timestamps
 *
 * For simplicity, it also:
 * - Provides access to the last submitted delta
 * - Automatically updates itself on merge operations to ensure
 *   a generated timestamp is always greater than any known timestamp
 *
 * @property uid the client unique identifier associated with this environment.
 */
open class SimpleEnvironment(uid: ClientUId) : Environment(uid) {

    /**
     * A version vector storing this environment causal context.
     */
    private val currentState: VersionVector = VersionVector()

    /**
     * Store the last obj and delta submitted via onWrite()
     */
    private var lastWrite: Pair<DeltaCRDT, DeltaCRDT>? = null

    /**
     * Pop (return and delete) the last delta submitted via onWrite()
     * Throws NullPointerException if last submitted delta
     * has already been popped.
     * @return the last write as a Pair(obj, delta)
     */
    fun popWrite(): Pair<DeltaCRDT, DeltaCRDT> {
        val d = lastWrite
        lastWrite = null
        return d!!
    }

    override fun getState(): VersionVector {
        return this.currentState.copy()
    }

    override fun tick(): Timestamp {
        val lastCnt = this.currentState.max()
        if (lastCnt == Timestamp.CNT_MAX_VALUE) {
            throw RuntimeException("Timestamp counter has reached Timestamp.CNT_MAX_VALUE")
        }
        val ts = Timestamp(this.uid, lastCnt + 1)
        this.update(ts)
        return ts
    }

    override fun update(ts: Timestamp) {
        this.currentState.update(ts)
    }

    override fun update(vv: VersionVector) {
        this.currentState.update(vv)
    }

    override fun onWrite(obj: DeltaCRDT, delta: DeltaCRDT) {
        this.lastWrite = Pair(obj, delta)
    }

    override fun onMerge(obj: DeltaCRDT, delta: DeltaCRDT, lastTs: Timestamp?) {
        if (lastTs != null) this.update(lastTs)
    }
}
