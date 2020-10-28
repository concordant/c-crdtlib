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
     * Protected abstract method getting the state associated with the environment.
     * @return the current state.
     */
    protected abstract fun getStateProtected(): VersionVector

    /**
     * Protected abstract method generating a monotonically increasing timestamp.
     * @return the generated timestamp.
     */
    protected abstract fun tickProtected(): Timestamp

    /**
     * Protected abstract method updating the state with the given timestamp.
     * @param ts the given timestamp.
     */
    protected abstract fun updateProtected(ts: Timestamp)

    /**
     * Protected abstract method updating the state with the given version vector.
     * @param vv the given version vector.
     */
    protected abstract fun updateProtected(vv: VersionVector)

    /**
     * Gets the state associated with the environment.
     * This trick is used to be able to force the method name in the generated javascript.
     * @return the current state.
     */
    @Name("getState")
    fun getState(): VersionVector {
        return getStateProtected()
    }

    /**
     * Generates a monotonically increasing timestamp.
     * This trick is used to be able to force the method name in the generated javascript.
     * @return the generated timestamp.
     */
    @Name("tick")
    fun tick(): Timestamp {
        return tickProtected()
    }

    /**
     * Updates the state with the given timestamp.
     * This trick is used to be able to force the method name in the generated javascript.
     * @param ts the given timestamp.
     */
    @Name("updateTs")
    fun update(ts: Timestamp) {
        return updateProtected(ts)
    }

    /**
     * Updates the state with the given version vector.
     * This trick is used to be able to force the method name in the generated javascript.
     * @param vv the given version vector.
     */
    @Name("updateVv")
    fun update(vv: VersionVector) {
        return updateProtected(vv)
    }
}
