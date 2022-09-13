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

import kotlinx.serialization.*

/**
 * This class represents a version vector.
 */
@Serializable
class VersionVector {

    /**
     * A mutable map storing for each client the greatest timestamp value seen until now.
     */
    @Required
    private val entries: MutableMap<ClientUId, Int> = mutableMapOf()

    private fun get(uid: ClientUId): Int {
        return this.entries[uid] ?: Timestamp.CNT_MIN_VALUE
    }

    /**
     * Default constructor.
     */
    constructor()

    /**
     * Copy constructor.
     */
    constructor(vv: VersionVector) {
        this.entries.putAll(vv.entries)
    }

    /**
     * Gets the maximal value stored in this version vector.
     * @return the maximal value or null if there are no values.
     */
    @Name("max")
    fun max(): Int {
        return this.entries.values.maxOrNull() ?: Timestamp.CNT_MIN_VALUE
    }

    /**
     * Returns if a given timestamp is contained in the version vector.
     * @param ts the given timestamp.
     * @return true if the timestamp is contained in the version vector, false otherwise.
     */
    @Name("contains")
    fun contains(ts: Timestamp): Boolean {
        val cnt = this.get(ts.uid)
        return cnt >= ts.cnt
    }

    /**
     * Adds a given timestamp to this version vector.
     * @param ts the given timestamp.
     */
    @Name("updateTs")
    fun update(ts: Timestamp) {
        val curCnt = this.get(ts.uid)
        if (curCnt < ts.cnt) this.entries[ts.uid] = ts.cnt
    }

    /**
     * Updates this version vector with a given version vector by taking the maximum value for each
     * entry.
     * @param vv the given version vector used for update.
     */
    @Name("updateVv")
    fun update(vv: VersionVector) {
        for ((k, v) in vv.entries) {
            val curCnt = this.get(k)
            if (curCnt < v) this.entries[k] = v
        }
    }

    /**
     * Checks that this version vector is smaller than or equal a given version vector.
     * @param vv the given version vector used for comparison.
     * @return true if this version vector is smaller than or equal the other one, false otherwise.
     */
    @Name("isSmallerOrEquals")
    fun isSmallerOrEquals(vv: VersionVector): Boolean {
        for ((k, localV) in this.entries) {
            val v = vv.get(k)
            if (localV > v) return false
        }
        return true
    }

    /**
     * Checks that this version vector is strictly smaller than a given version vector.
     * @param vv the given version vector used for comparison.
     * @return true if this version vector is smaller than the other one, false otherwise.
     */
    @Name("isSmaller")
    fun isSmaller(vv: VersionVector): Boolean {
        var isEqual = this.entries.isNotEmpty()
        for ((k, localV) in this.entries) {
            val v = vv.get(k)
            if (localV > v) return false
            isEqual = isEqual && (localV == v)
        }
        return !isEqual
    }

    /**
     * Checks that this version vector is greater than or equal a given version vector.
     * @param vv the given version vector used for comparison.
     * @return true if this version vector is greater than or equal the other one, false otherwise.
     */
    @Name("isGreaterOrEquals")
    fun isGreaterOrEquals(vv: VersionVector): Boolean {
        for ((k, v) in vv.entries) {
            val localV = this.get(k)
            if (localV < v) return false
        }
        return true
    }

    /**
     * Checks that this version vector is strictly greater than a given version vector.
     * @param vv the given version vector used for comparison.
     * @return true if this version vector is greater than the other one, false otherwise.
     */
    @Name("isGreater")
    fun isGreater(vv: VersionVector): Boolean {
        var isEqual = vv.entries.isNotEmpty()
        for ((k, v) in vv.entries) {
            val localV = this.get(k)
            if (localV < v) return false
            isEqual = isEqual && (localV == v)
        }
        return !isEqual
    }

    /**
     * Overrides the equals function of object
     * @param other object to compare with
     * @return true if both objects are version vectors with equal entries
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VersionVector) return false

        if (entries != other.entries) return false
        return true
    }

    /**
     * Checks that this version vector is not comparable to a given version vector.
     * @param vv the given version vector to compare with.
     * @return true if this version vector is not comparable to the other one, false otherwise.
     */
    @Name("isNotComparable")
    fun isNotComparable(vv: VersionVector): Boolean {
        var isSmaller = false
        var isLarger = false
        for ((k, localV) in this.entries) {
            val v = vv.get(k)
            if (localV > v)
            // one entry in this object is larger
                isLarger = true
            else if (localV < v)
            // one entry in the other vv is larger
                isSmaller = true
            if (isSmaller && isLarger)
                return true
        }

        // there are entries in the vv that this one does not have
        if (isLarger && !this.entries.keys.containsAll(vv.entries.keys))
            return true

        // all entries are either (smaller or equal) or (larger or equal)
        return false
    }

    /**
     * Copies this version vector.
     * @return a copy of this version vector.
     */
    @Name("copy")
    fun copy(): VersionVector {
        return VersionVector(this)
    }

    /**
     * Overrides the hashCode function of object
     * @return a hash code value for this object.
     */
    override fun hashCode(): Int {
        return entries.hashCode()
    }

    /**
     * Overrides the toString function of object
     * @return a string representation of the object.
     */
    override fun toString(): String {
        return "VersionVector(entries='$entries')"
    }

    /**
     * Serializes this version vector to a json string.
     * @return the resulted json string.
     */
    @Name("toJson")
    fun toJson(): String {
        return Json.encodeToString(serializer(), this)
    }

    companion object {
        /**
         * Deserializes a given json string in a version vector object.
         * @param json the given json string.
         * @return the resulted version vector.
         */
        @Name("fromJson")
        fun fromJson(json: String): VersionVector {
            return Json.decodeFromString(serializer(), json)
        }
    }
}
