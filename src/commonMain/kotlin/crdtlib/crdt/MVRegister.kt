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
import crdtlib.utils.Timestamp
import crdtlib.utils.VersionVector
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

/**
* This class is a delta-based CRDT multi-value register.
* It is serializable to JSON and respect the following schema:
* {
*   "_type": "MVRegister",
*   "_metadata": VersionVector.toJson(),
*   "value": [
*       (( $value, )*( $value ))?
*   ]
* }
*/
@Serializable
class MVRegister : DeltaCRDT<MVRegister> {

    /**
     * A mutable set storing the different values with their associated timestamp.
     */
    @Required
    var entries: MutableSet<Pair<String, Timestamp>> = mutableSetOf()

    /**
     * A version vector summarizing the entries seen by all values.
     */
    @Required
    val causalContext: VersionVector = VersionVector()

    /**
     * Default constructor creating a empty register.
     */
    constructor()

    /**
     * Constructor creating a register initialized with a given value.
     * @param value the value to be put in the register.
     * @param ts the associated timestamp.
     */
    constructor(value: String, ts: Timestamp) {
        this.entries = mutableSetOf(Pair(value, ts))
        this.causalContext.update(ts)
    }

    /**
     * Constructor creating a copy of a given register.
     * @param other the register that should be copy.
     */
    constructor(other: MVRegister) {
        this.entries = other.entries.toMutableSet()
        this.causalContext.update(other.causalContext)
    }

    constructor(entries: Set<Pair<String, Timestamp>>, causalContext: VersionVector) {
        this.entries = entries.toMutableSet()
        this.causalContext.update(causalContext)
    }

    /**
     * Gets the set of values currently stored in the register.
     * @return the set of values stored.
     */
    @Name("get")
    fun get(): Set<String> {
        return this.entries.map { it.first }.toSet()
    }

    /**
     * Assigns a given value to the register.
     * This value overload all others and the causal context is updated with the given timestamp.
     * Assign is not effective if the associated timestamp is already included in the causal context.
     * @param value the value that should be assigned.
     * @param ts the timestamp associated to the operation.
     * @return the delta corresponding to this operation.
     */
    @Name("set")
    fun assign(value: String, ts: Timestamp): MVRegister {
        if (!this.causalContext.contains(ts)) {
            this.entries.clear()
            this.entries.add(Pair(value, ts))
            this.causalContext.update(ts)
        }
        return MVRegister(this)
    }

    /**
     * Generates a delta of operations recorded and not already present in a given context.
     * @param vv the context used as starting point to generate the delta.
     * @return the corresponding delta of operations.
     */
    override fun generateDelta(vv: VersionVector): MVRegister {
        return MVRegister(this)
    }

    /**
     * Merges information contained in a given delta into the local replica, the merge is unilateral
     * and only the local replica is modified.
     * A foreign (local) value is kept iff it is contained in the local (foreign) replica or its
     * associated timestamp is not included in the local (foreign) causal context.
     * @param delta the delta that should be merge with the local replica.
     */
    override fun merge(delta: MVRegister) {
        val keptEntries = mutableSetOf<Pair<String, Timestamp>>()
        for ((value, ts) in this.entries) {
            if (!delta.causalContext.contains(ts) || delta.entries.any { it.second == ts }) {
                keptEntries.add(Pair(value, ts))
            }
        }
        for ((value, ts) in delta.entries) {
            if (!this.causalContext.contains(ts) || this.entries.any { it.second == ts }) {
                keptEntries.add(Pair(value, ts))
            }
        }

        this.entries = keptEntries
        this.causalContext.update(delta.causalContext)
    }

    /**
     * Serializes this crdt MV register to a json string.
     * @return the resulted json string.
     */
    override fun toJson(): String {
        val jsonSerializer = JsonMVRegisterSerializer(serializer())
        return Json.encodeToString<MVRegister>(jsonSerializer, this)
    }

    companion object {
        /**
         * Deserializes a given json string in a crdt MV register.
         * @param json the given json string.
         * @return the resulted MV register.
         */
        @Name("fromJson")
        fun fromJson(json: String): MVRegister {
            val jsonSerializer = JsonMVRegisterSerializer(serializer())
            return Json.decodeFromString(jsonSerializer, json)
        }
    }
}

/**
* This class is a json transformer for MVRegister, it allows the separation between data and metadata.
*/
class JsonMVRegisterSerializer(private val serializer: KSerializer<MVRegister>) :
    JsonTransformingSerializer<MVRegister>(serializer) {

    override fun transformSerialize(element: JsonElement): JsonElement {
        val entries = mutableListOf<JsonElement>()
        val value = mutableListOf<JsonElement>()
        for (tmpPair in element.jsonObject["entries"]!!.jsonArray) {
            value.add(tmpPair.jsonObject["first"] as JsonElement)
            entries.add(tmpPair.jsonObject["second"]!!.jsonObject)
        }
        val metadata = JsonObject(mapOf("entries" to JsonArray(entries), "causalContext" to element.jsonObject["causalContext"]!!.jsonObject))
        return JsonObject(mapOf("_type" to JsonPrimitive("MVRegister"), "_metadata" to metadata, "value" to JsonArray(value)))
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        val entries = mutableListOf<JsonElement>()
        val metadata = element.jsonObject["_metadata"]!!.jsonObject
        val value = element.jsonObject["value"]!!.jsonArray
        var idxValue = 0
        for (tmpEntry in metadata["entries"]!!.jsonArray) {
            entries.add(JsonObject(mapOf("first" to value[idxValue], "second" to tmpEntry)))
            idxValue++
        }
        val causalContext = metadata["causalContext"]!!.jsonObject
        return JsonObject(mapOf("entries" to JsonArray(entries), "causalContext" to causalContext))
    }
}
