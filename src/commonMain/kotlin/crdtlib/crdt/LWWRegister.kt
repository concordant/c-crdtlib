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
import kotlinx.serialization.*
import kotlinx.serialization.json.*

/**
* This class is a delta-based CRDT last writer wins (LWW) register.
* It is serializable to JSON and respects the following schema:
* {
*   "_type": "LWWRegister",
*   "_metadata": Timestamp.toJson(),
*   "value": $value
* }
* @property value the value stored in the register.
* @property value the timestamp associated to the value.
*/
@Serializable
class LWWRegister(var value: String, var ts: Timestamp) : DeltaCRDT<LWWRegister>() {

    /**
     * Constructor creating a copy of a given register.
     * @param other the register that should be copy.
     */
    constructor(other: LWWRegister) : this(other.value, other.ts)

    /**
     * Gets the value currently stored in the register.
     * @return value stored in the register.
     */
    @Name("get")
    fun get(): String {
        return value
    }

    /**
     * Assigns a given value to the register.
     * Assign is not effective if the associated timestamp is smaller (older) than the current one.
     * @param value the value that should be assigned.
     * @param ts the timestamp associated to the operation.
     * @return the delta corresponding to this operation.
     */
    @Name("set")
    fun assign(v: String, ts: Timestamp): LWWRegister {
        if (this.ts < ts) {
            this.ts = ts
            this.value = v
        }
        return LWWRegister(this)
    }

    /**
     * Generates a delta of operations recorded and not already present in a given context.
     * @param vv the context used as starting point to generate the delta.
     * @return the corresponding delta of operations.
     */
    override fun generateDelta(vv: VersionVector): LWWRegister {
        return LWWRegister(this)
    }

    /**
     * Merges information contained in a given delta into the local replica, the merge is unilateral
     * and only the local replica is modified.
     * The foreign value wins iff its associated timestamp is greater than the current one.
     * @param delta the delta that should be merge with the local replica.
     */
    override fun merge(delta: LWWRegister) {
        if (this.ts < delta.ts) {
            this.value = delta.value
            this.ts = delta.ts
        }
    }

    /**
     * Serializes this crdt LWW register to a json string.
     * @return the resulted json string.
     */
    override fun toJson(): String {
        val jsonSerializer = JsonLWWRegisterSerializer(serializer())
        return Json.encodeToString<LWWRegister>(jsonSerializer, this)
    }

    companion object {
        /**
         * Deserializes a given json string in a crdt LWW register.
         * @param json the given json string.
         * @return the resulted LWW register.
         */
        @Name("fromJson")
        fun fromJson(json: String): LWWRegister {
            val jsonSerializer = JsonLWWRegisterSerializer(serializer())
            return Json.decodeFromString(jsonSerializer, json)
        }
    }
}

/**
* This class is a json transformer for LWWRegister, it allows the separation between data and metadata.
*/
class JsonLWWRegisterSerializer(private val serializer: KSerializer<LWWRegister>) :
    JsonTransformingSerializer<LWWRegister>(serializer) {

    override fun transformSerialize(element: JsonElement): JsonElement {
        val value = element.jsonObject["value"] as JsonElement
        val metadata = element.jsonObject["ts"]!!.jsonObject
        return JsonObject(mapOf("_type" to JsonPrimitive("LWWRegister"), "_metadata" to metadata, "value" to value))
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        val value = element.jsonObject["value"] as JsonElement
        val ts = element.jsonObject["_metadata"]!!.jsonObject
        return JsonObject(mapOf("value" to value, "ts" to ts))
    }
}
