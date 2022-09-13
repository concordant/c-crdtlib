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

package crdtlib.crdt

import crdtlib.utils.Environment
import crdtlib.utils.Json
import crdtlib.utils.Name
import crdtlib.utils.VersionVector
import kotlinx.serialization.*
import kotlinx.serialization.json.*

/**
 * A delta-based CRDT join semi-lattice ratchet.
 *
 * A join (or upper) semi-lattice is a partially ordered set
 * endowed with a join operation: for any two elements,
 * the result of the join operation is their greatest upper bound
 * with respect to the partial order.
 *
 * A Ratchet is a register whose value is the join of all assigned values:
 * when assigning or merging, the retained value is the join
 * of the two values.
 * Note that join is not max: on a partially ordered set,
 * the join of two values is not necessarily one of them.
 *
 * This is a sample implementation using Strings,
 * with default (total) ordering.
 *
 * Its JSON serialization respects the following schema:
 * ```json
 * {
 *   "type": "Ratchet",
 *   "value": $value
 * }
 * ```
 */
@Serializable
class Ratchet : DeltaCRDT {
    /*
     * The value stored in the ratchet.
     */
    @Required
    var value: String? = null

    /**
     * Constructs an empty Ratchet instance.
     */
    constructor() : super()

    /**
     * Constructs an empty Ratchet instance with provided environment.
     */
    constructor(env: Environment) : super(env)

    /**
     * Constructs a Ratchet instance with initial [value] and environment.
     */
    constructor(value: String?, env: Environment? = null) : super(env) {
        this.value = value
    }

    override fun copy(): Ratchet {
        return Ratchet(this.value, this.env)
    }

    /**
     * Gets the value stored in the ratchet.
     */
    @Name("get")
    fun get(): String? {
        onRead()
        return this.value
    }

    /**
     * Assigns a given [value] to the ratchet.
     *
     * This is a no-op if [value] is not greater than current value.
     */
    @Name("set")
    fun assign(value: String?): Ratchet {
        val delta = Ratchet(this.value)
        if (value != null && this.value.orEmpty() <= value) {
            delta.value = value
        }
        onWrite(delta)
        // if x == null and value == "", then x.orEmpty() == value
        if (value != null && this.value.orEmpty() <= value) {
            this.value = value
        }
        return delta
    }

    override fun generateDelta(vv: VersionVector): Ratchet {
        return Ratchet(this.value)
    }

    override fun merge(delta: DeltaCRDT) {
        if (delta !is Ratchet) throw IllegalArgumentException("Ratchet unsupported merge argument")

        // if x == null and value == "", then x.orEmpty() == value
        if (delta.value != null &&
            this.value.orEmpty() <= delta.value.orEmpty()
        ) {
            this.value = delta.value
        }
        onMerge(delta, null)
    }

    override fun toJson(): String {
        val jsonSerializer = JsonRatchetSerializer(serializer())
        return Json.encodeToString(jsonSerializer, this)
    }

    companion object {
        /**
         * Get the type name for serialization.
         * @return the type as a string.
         */
        @Name("getType")
        fun getType(): String {
            return "Ratchet"
        }

        /**
         * Deserializes a given json string in a crdt ratchet.
         * @param json the given json string.
         * @return the resulted ratchet.
         */
        @Name("fromJson")
        fun fromJson(json: String, env: Environment? = null): Ratchet {
            val jsonSerializer = JsonRatchetSerializer(serializer())
            val obj = Json.decodeFromString(jsonSerializer, json)
            if (env != null) obj.env = env
            return obj
        }
    }
}

/**
* This class is a json transformer for Ratchet, it allows the separation between data and metadata.
*/
class JsonRatchetSerializer(serializer: KSerializer<Ratchet>) :
    JsonTransformingSerializer<Ratchet>(serializer) {

    override fun transformSerialize(element: JsonElement): JsonElement {
        return JsonObject(mapOf("type" to JsonPrimitive("Ratchet"), "value" to element.jsonObject["value"] as JsonElement))
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        return JsonObject(mapOf("value" to element.jsonObject["value"] as JsonElement))
    }
}
