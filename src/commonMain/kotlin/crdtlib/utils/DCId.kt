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
* This class represents a datacenter (DC) identifier (id).
* @property name the name associated with the DC.
**/
@Serializable
data class DCId(val name: String) {

    /**
    * Compares this DC name to a given other datacenter name.
    * @param other the other instance of datacenter id.
    * @return the results of the comparison between the two DC name.
    **/
    @Name("compareTo")
    operator fun compareTo(other: DCId): Int {
        return this.name.compareTo(other.name)
    }

    /**
    * Serializes this datacenter id to a json string.
    * @return the resulted json string.
    */
    @Name("toJson")
    fun toJson(): String {
        return Json.stringify(DCId.serializer(), this)
    }

    companion object {
        /**
        * Deserializes a given json string in a datacenter id object.
        * @param json the given json string.
        * @return the resulted datacenter id.
        */
        @Name("fromJson")
        fun fromJson(json: String): DCId {
            return Json.parse(DCId.serializer(), json)
        }
    }
}
