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

package crdtlib.utils

/**
* This class represents a simple environment generating increasing monotonic timestamps.
* @property uid the datacenter unique identifier associated with this environment.
*/
class SimpleEnvironment(private val uid: DCUId) : Environment() {

    /**
    * A version vector storing this environment causal context.
    */
    private var curState: VersionVector = VersionVector()

    /**
    * Generates a monotonically increasing timestamp.
    * @return the generated timestamp.
    */
    override fun getNewTimestampProtected(): Timestamp {
        val lastTs = curState.maxVal()
        if (lastTs == null) return Timestamp(uid, Timestamp.CNT_MIN_VALUE)
        if (lastTs == Timestamp.CNT_MAX_VALUE) {
            throw RuntimeException("Timestamp counter has reached Timestamp.CNT_MAX_VALUE")
        }
        return Timestamp(uid, lastTs + 1)
    }

    /**
    * Gets the current state associated with the environment.
    * @return the current state.
    */
    override fun getCurrentStateProtected(): VersionVector {
        return curState.copy()
    }

    /**
    * Updates the current state with the given timestamp.
    * @param ts the given timestamp.
    */
    override fun updateStateTSProtected(ts: Timestamp) {
        curState.addTS(ts)
    }

    /**
    * Updates the current state with the given version vector.
    * @param vv the given version vector.
    */
    override fun updateStateVVProtected(vv: VersionVector) {
        curState.pointWiseMax(vv)
    }
}
