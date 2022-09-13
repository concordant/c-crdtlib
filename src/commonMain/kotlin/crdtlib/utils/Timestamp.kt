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
 * This class represents a timestamp.
 * @property uid the client unique id.
 * @property cnt the value associated to the timestamp.
 */
@Serializable
data class Timestamp(val uid: ClientUId, val cnt: Int) {

    /**
     * Compares this timestamp to a given other timestamp.
     * First comparison is made on their values and if equal on their client unique ids.
     * @param other the other instance of timestamp.
     * @return the results of the comparison between the two timestamp.
     */
    @Name("compareTo")
    operator fun compareTo(other: Timestamp): Int {
        if (this.cnt != other.cnt)
            return this.cnt.compareTo(other.cnt)
        return this.uid.compareTo(other.uid)
    }

    /**
     * Serializes this timestamp to a json string.
     * @return the resulted json string.
     */
    @Name("toJson")
    fun toJson(): String {
        return Json.encodeToString(serializer(), this)
    }

    companion object {

        /**
         * Constant for timestamp counter minimum value
         */
        const val CNT_MIN_VALUE = Int.MIN_VALUE

        /**
         * Constant for timestamp counter maximum value
         */
        const val CNT_MAX_VALUE = Int.MAX_VALUE

        /**
         * Deserializes a given json string in a timestamp object.
         * @param json the given json string.
         * @return the resulted timestamp.
         */
        @Name("fromJson")
        fun fromJson(json: String): Timestamp {
            return Json.decodeFromString(serializer(), json)
        }
    }
}
