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
* This abstract class represents a contextual environment.
*/
abstract class Environment {

    /**
    * Protected abstract method generating a monotonically increasing timestamp.
    * @return the generated timestamp.
    */
    protected abstract fun getNewTimestampProtected(): Timestamp

    /**
    * Protected abstract method getting the current state associated with the environment.
    * @return the current state.
    */
    protected abstract fun getCurrentStateProtected(): VersionVector

    /**
    * Protected abstract method updating the current state with the given timestamp.
    * @param ts the given timestamp.
    */
    protected abstract fun updateStateTSProtected(ts: Timestamp)

    /**
    * Protected abstract method updating the current state with the given version vector.
    * @param vv the given version vector.
    */
    protected abstract fun updateStateVVProtected(vv: VersionVector)

    /**
    * Generates a monotonically increasing timestamp.
    * This trick is used to be able to force the method name in the generated javascript.
    * @return the generated timestamp.
    */
    @Name("getNewTimestamp")
    fun getNewTimestamp(): Timestamp {
        return getNewTimestampProtected()
    }

    /**
    * Gets the current state associated with the environment.
    * This trick is used to be able to force the method name in the generated javascript.
    * @return the current state.
    */
    @Name("getCurrentState")
    fun getCurrentState(): VersionVector {
        return getCurrentStateProtected()
    }

    /**
    * Updates the current state with the given timestamp.
    * This trick is used to be able to force the method name in the generated javascript.
    * @param ts the given timestamp.
    */
    @Name("updateStateTS")
    fun updateStateTS(ts: Timestamp) {
        return updateStateTSProtected(ts)
    }

    /**
    * Updates the current state with the given version vector.
    * This trick is used to be able to force the method name in the generated javascript.
    * @param vv the given version vector.
    */
    @Name("updateStateVV")
    fun updateStateVV(vv: VersionVector) {
        return updateStateVVProtected(vv)
    }
}
