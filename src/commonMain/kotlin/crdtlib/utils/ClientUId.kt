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

import kotlinx.serialization.*

/**
 * This class represents a client unique identifier (UId).
 * @property name the name associated with the client.
 */
@Serializable
data class ClientUId(private val name: String) {

    /**
     * Compares this client name to a given other client name.
     * @param other the other instance of client unique id.
     * @return the results of the comparison between the two client names.
     */
    @Name("compareTo")
    operator fun compareTo(other: ClientUId): Int {
        return this.name.compareTo(other.name)
    }

    /**
     * Serializes this client unique id to a json string.
     * @return the resulted json string.
     */
    @Name("toJson")
    fun toJson(): String {
        return Json.encodeToString(serializer(), this)
    }

    companion object {
        /**
         * Deserializes a given json string in a client id object.
         * @param json the given json string.
         * @return the resulted client id.
         */
        @Name("fromJson")
        fun fromJson(json: String): ClientUId {
            return Json.decodeFromString(serializer(), json)
        }
    }
}
