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

import crdtlib.utils.Environment
import crdtlib.utils.Json
import crdtlib.utils.Name
import crdtlib.utils.Timestamp
import crdtlib.utils.VersionVector
import kotlinx.serialization.*
import kotlinx.serialization.json.*

/**
 * A delta-based CRDT Last Writer Wins (LWW) register.
 *
 * Only the last written value is retained: the previous value (if any)
 * is discarded.
 *
 * When merging, only the value associated with the greatest timestamp
 * is retained.
 * A deletion is represented as a null value and handled the same way.
 *
 * Its JSON serialization respects the following schema:
 * ```json
 * {
 *   "type": "LWWRegister",
 *   "metadata": Timestamp.toJson(),
 *   "value": $value
 * }
 * ```
 */
@Serializable
class LWWRegister : DeltaCRDT {

    /**
     * The string value stored in the register.
     */
    @Required
    var value: String? = null

    /**
     * The timestamp associated to the value.
     */
    @Required
    var ts: Timestamp? = null

    /**
     * Constructs an empty LWWRegister instance.
     */
    constructor() : super()
    constructor(env: Environment) : super(env)

    /**
     * Constructs a LWWRegister instance initialized with a given [value]
     * and environment.
     */
    constructor(value: String?, env: Environment) : super(env) {
        this.value = value
        this.ts = env.tick()
    }

    override fun copy(): LWWRegister {
        val copy = LWWRegister(this.value, this.env)
        copy.ts = this.ts
        return copy
    }

    /**
     * Gets the value currently stored in the register.
     */
    @Name("get")
    fun get(): String? {
        onRead()
        return value
    }

    /**
     * Assigns a given [value](v) to the register.
     *
     * @return the delta corresponding to this operation.
     */
    @Name("set")
    fun assign(v: String): LWWRegister {
        val ts = env.tick()
        val currentTs = this.ts
        if (currentTs == null || currentTs < ts) {
            this.ts = ts
            this.value = v
        }
        val delta = this.copy()
        onWrite(delta)
        return delta
    }

    override fun generateDelta(vv: VersionVector): LWWRegister {
        if (this.ts != null && !vv.contains(this.ts!!)) {
            return this.copy()
        }
        return LWWRegister()
    }

    override fun merge(delta: DeltaCRDT) {
        if (delta !is LWWRegister) throw IllegalArgumentException("LWWRegister unsupported merge argument")

        val currentTs = this.ts
        val deltaTs = delta.ts
        if (currentTs == null || (deltaTs != null && currentTs < deltaTs)) {
            this.value = delta.value
            this.ts = delta.ts
        }
        onMerge(delta, delta.ts)
    }

    override fun toJson(): String {
        val jsonSerializer = JsonLWWRegisterSerializer(serializer())
        return Json.encodeToString(jsonSerializer, this)
    }

    companion object {
        /**
         * Get the type name for serialization.
         * @return the type as a string.
         */
        @Name("getType")
        fun getType(): String {
            return "LWWRegister"
        }

        /**
         * Deserializes a given json string in a crdt LWW register.
         * @param json the given json string.
         * @return the resulted LWW register.
         */
        @Name("fromJson")
        fun fromJson(json: String, env: Environment? = null): LWWRegister {
            val jsonSerializer = JsonLWWRegisterSerializer(serializer())
            val obj = Json.decodeFromString(jsonSerializer, json)
            if (env != null) obj.env = env
            return obj
        }
    }
}

/**
* This class is a json transformer for LWWRegister, it allows the separation between data and metadata.
*/
class JsonLWWRegisterSerializer(serializer: KSerializer<LWWRegister>) :
    JsonTransformingSerializer<LWWRegister>(serializer) {

    override fun transformSerialize(element: JsonElement): JsonElement {
        val value = element.jsonObject["value"] as JsonElement
        val metadata = element.jsonObject["ts"] as JsonElement
        return JsonObject(mapOf("type" to JsonPrimitive("LWWRegister"), "metadata" to metadata, "value" to value))
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        val value = element.jsonObject["value"] as JsonElement
        val ts = element.jsonObject["metadata"] as JsonElement
        return JsonObject(mapOf("value" to value, "ts" to ts))
    }
}
