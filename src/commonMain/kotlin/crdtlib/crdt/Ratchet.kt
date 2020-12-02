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

package crdtlib.crdt

import crdtlib.utils.Json
import crdtlib.utils.Name
import crdtlib.utils.VersionVector
import kotlinx.serialization.*
import kotlinx.serialization.json.*

/**
* This class is a delta-based CRDT join semi-lattice ratchet.
* A join (or upper) semi-lattice is a set of values on which a partial order is defined such that
* the result of a merge operation for any two elements is the greatest upper bound of the elements
* with respect to this partial order.
* It is serializable to JSON and respect the following schema:
* {
*   "_type": "Ratchet",
*   "value": $value
* }
* @property value the stored value.
*/
@Serializable
class Ratchet(var value: String) : DeltaCRDT() {

    /**
     * Gets the value stored in the ratchet.
     * @return the value stored in the ratchet.
     */
    @Name("get")
    fun get(): String {
        return this.value
    }

    /**
     * Assigns a given value to the ratchet.
     * This passed value overload the already present one iff it is greater.
     * @param value the value that should be assigned.
     * @return the delta corresponding to this operation.
     */
    @Name("set")
    fun assign(value: String): Ratchet {
        if (this.value < value) this.value = value
        return Ratchet(this.value)
    }

    /**
     * Generates a delta of operations recorded and not already present in a given context.
     * @param vv the context used as starting point to generate the delta.
     * @return the corresponding delta of operations.
     */
    override fun generateDelta(vv: VersionVector): Ratchet {
        return Ratchet(this.value)
    }

    /**
     * Merges information contained in a given delta into the local replica, the merge is unilateral
     * and only the local replica is modified.
     * A foreign value is kept iff it is greater than the local one.
     * @param delta the delta that should be merge with the local replica.
     */
    override fun merge(delta: DeltaCRDT) {
        if (delta !is Ratchet) throw IllegalArgumentException("Ratchet unsupported merge argument")

        if (this.value < delta.value) this.value = delta.value
    }

    /**
     * Serializes this crdt ratchet to a json string.
     * @return the resulted json string.
     */
    override fun toJson(): String {
        val jsonSerializer = JsonRatchetSerializer(serializer())
        return Json.encodeToString(jsonSerializer, this)
    }

    companion object {
        /**
         * Deserializes a given json string in a crdt ratchet.
         * @param json the given json string.
         * @return the resulted ratchet.
         */
        @Name("fromJson")
        fun fromJson(json: String): Ratchet {
            val jsonSerializer = JsonRatchetSerializer(serializer())
            return Json.decodeFromString(jsonSerializer, json)
        }
    }
}

/**
* This class is a json transformer for Ratchet, it allows the separation between data and metadata.
*/
class JsonRatchetSerializer(serializer: KSerializer<Ratchet>) :
    JsonTransformingSerializer<Ratchet>(serializer) {

    override fun transformSerialize(element: JsonElement): JsonElement {
        return JsonObject(mapOf("_type" to JsonPrimitive("Ratchet"), "value" to element.jsonObject["value"] as JsonElement))
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        return JsonObject(mapOf("value" to element.jsonObject["value"] as JsonElement))
    }
}
