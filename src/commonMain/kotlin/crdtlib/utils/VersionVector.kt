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

import kotlin.math.absoluteValue
import kotlinx.serialization.*
import kotlinx.serialization.json.*


/**
* This class represents a version vector.
*/
@Serializable
class VersionVector {

    /**
    * A mutable map storing for each datacenter the greatest timestamp value seen until now.
    */
    private val entries: MutableMap<DCId, Int>

    /**
    * Default constructor.
    */
    constructor() {
        this.entries = mutableMapOf<DCId, Int>()
    }

    /**
    * Copy constructor.
    */
    constructor(vv: VersionVector) {
        this.entries = mutableMapOf<DCId, Int>()
        entries.putAll(vv.entries)
    }

    /**
    * Gets the maximal value stored in this version vector.
    * @return the maximal value.
    */
    @Name("maxVal")
    fun maxVal(): Int {
        return entries.values.maxBy { it.absoluteValue } ?: 0
    }

    /**
    * Adds a given timestamp to this version vector.
    * @param ts the given timestamp.
    */
    @Name("addTS")
    fun addTS(ts: Timestamp) {
        val curCnt = entries.getOrElse(ts.id, { 0 })
        if(curCnt < ts.cnt)
            entries[ts.id] = ts.cnt
    }

    /**
    * Returns if a given timestamp is included in the version vector.
    * @param ts the given timestamp.
    * @return true if the timestamp is included in the version vector, false otherwise.
    */
    @Name("includesTS")
    fun includesTS(ts: Timestamp): Boolean {
        val cnt = entries.getOrElse(ts.id, { 0 })
        return cnt >= ts.cnt
    }

    /**
    * Updates this version vector with a given version vector by taking the maximum value for each
    * entry.
    * @param vv the given version vector used for update.
    */
    @Name("pointWiseMax")
    fun pointWiseMax(vv: VersionVector) {
        for((k, v) in vv.entries)
            if(entries.getOrElse(k, { 0 }) < v)
                entries.put(k, v)
    }

    /**
    * Checks that this version vector is smaller or equals than a given version vector.
    * @param vv the given version vector used for comparison.
    * @return true if this version vector is smaller or equals than the other one, false otherwise.
    */
    @Name("isSmallerOrEquals")
    fun isSmallerOrEquals(vv: VersionVector): Boolean {
        for((k, v) in vv.entries) {
            val localV = entries.getOrElse(k, { 0 })
            if(localV > v) return false
        }

        for ((k, localV) in this.entries) {
            val v = vv.entries.getOrElse(k, { 0 })
            if(localV > v) return false
        }

        return true
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
    * Serializes this version vector to a json string.
    * @return the resulted json string.
    */
    @Name("toJson")
    fun toJson(): String {
        val JSON = Json(JsonConfiguration.Stable)
        return JSON.stringify(VersionVector.serializer(), this)
    }

    companion object {
        /**
        * Deserializes a given json string in a version vector object.
        * @param json the given json string.
        * @return the resulted version vector.
        */
        @Name("fromJson")
        fun fromJson(json: String): VersionVector {
            val JSON = Json(JsonConfiguration.Stable)
            return JSON.parse(VersionVector.serializer(), json)
        }
    }
}
