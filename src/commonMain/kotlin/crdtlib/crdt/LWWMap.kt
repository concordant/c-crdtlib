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
 * A delta-based CRDT map implementing last writer wins (LWW) policy.
 *
 * Each entry behaves like a [LWWRegister]:
 * only the last written value is retained.
 *
 * When merging, for each key,
 * only the value associated with the greatest timestamp is retained.
 * A deletion is represented as a null value and handled the same way.
 *
 * Its JSON serialization respects the following schema:
 * ```json
 * {
 *   "type": "LWWMap",
 *   "metadata": {
 *       "entries": {
 *           // $key is a string
 *           (( "$key": Timestamp.toJson(), )*( "$key": Timestamp.toJson() ))?
 *       }
 *   }
 *   // $key is a string and $value can be Boolean, double, integer or string
 *   ( , "$key": "$value" )*
 * }
 * ```
 */
@Serializable
class LWWMap : DeltaCRDT {

    /**
     * A mutable map storing metadata relative to each key.
     */
    @Required
    private val entries: MutableMap<String, Pair<String?, Timestamp>> = mutableMapOf()

    /**
     * Default constructor.
     */
    constructor() : super()
    constructor(env: Environment) : super(env)

    override fun copy(): LWWMap {
        val copy = LWWMap(this.env)
        copy.entries.putAll(this.entries.toMutableMap())
        return copy
    }

    /**
     * Get the Boolean value corresponding to a given [key],
     * or null if the key is not present in the map.
     */
    @Name("getBoolean")
    fun getBoolean(key: String): Boolean? {
        onRead()
        return this.entries[key + BOOLEAN]?.first?.toBoolean()
    }

    /**
     * Get the Double value corresponding to a given [key],
     * or null if the key is not present in the map.
     */
    @Name("getDouble")
    fun getDouble(key: String): Double? {
        onRead()
        return this.entries[key + DOUBLE]?.first?.toDoubleOrNull()
    }

    /**
     * Get the Int value corresponding to a given [key],
     * or null if the key is not present in the map.
     */
    @Name("getInt")
    fun getInt(key: String): Int? {
        onRead()
        return this.entries[key + INTEGER]?.first?.toIntOrNull()
    }

    /**
     * Get the String value corresponding to a given [key],
     * or null if the key is not present in the map.
     */
    @Name("getString")
    fun getString(key: String): String? {
        onRead()
        return this.entries[key + STRING]?.first
    }

    /**
     * Gets an iterator over the Boolean values in the map.
     */
    @Name("iteratorBoolean")
    fun iteratorBoolean(): Iterator<Pair<String, Boolean>> {
        onRead()
        return this.entries.asSequence().filter { (k, v) -> k.endsWith(BOOLEAN) && v.first != null }
            .map { (k, v) -> Pair(k.removeSuffix(BOOLEAN), v.first.toBoolean()) }.iterator()
    }

    /**
     * Gets an iterator over the Double values in the map.
     */
    @Name("iteratorDouble")
    fun iteratorDouble(): Iterator<Pair<String, Double>> {
        onRead()
        return this.entries.asSequence().filter { (k, v) -> k.endsWith(DOUBLE) && v.first != null }
            .map { (k, v) -> Pair(k.removeSuffix(DOUBLE), v.first!!.toDouble()) }.iterator()
    }

    /**
     * Gets an iterator over the Int values in the map.
     */
    @Name("iteratorInt")
    fun iteratorInt(): Iterator<Pair<String, Int>> {
        onRead()
        return this.entries.asSequence().filter { (k, v) -> k.endsWith(INTEGER) && v.first != null }
            .map { (k, v) -> Pair(k.removeSuffix(INTEGER), v.first!!.toInt()) }.iterator()
    }

    /**
     * Gets an iterator over the String values in the map.
     */
    @Name("iteratorString")
    fun iteratorString(): Iterator<Pair<String, String>> {
        onRead()
        return this.entries.asSequence().filter { (k, v) -> k.endsWith(STRING) && v.first != null }
            .map { (k, v) -> Pair(k.removeSuffix(STRING), v.first!!) }.iterator()
    }

    /**
     * Puts a [key] / Boolean [value] pair into the map.
     *
     * If [value] is null, the [key] is deleted.
     * @return the delta corresponding to this operation.
     */
    @Name("setBoolean")
    fun put(key: String, value: Boolean?): LWWMap {
        val op = LWWMap()
        val currentTs = this.entries[key + BOOLEAN]?.second
        val ts = env.tick()
        if (currentTs == null || currentTs < ts) {
            op.entries[key + BOOLEAN] = Pair(value?.toString(), ts)
        }
        onWrite(op)
        if (currentTs == null || currentTs < ts) {
            this.entries[key + BOOLEAN] = Pair(value?.toString(), ts)
        }
        return op
    }

    /**
     * Puts a [key] / Double [value] pair into the map.
     *
     * If [value] is null, the [key] is deleted.
     * @return the delta corresponding to this operation.
     */
    @Name("setDouble")
    fun put(key: String, value: Double?): LWWMap {
        val op = LWWMap()
        val currentTs = this.entries[key + DOUBLE]?.second
        val ts = env.tick()
        if (currentTs == null || currentTs < ts) {
            op.entries[key + DOUBLE] = Pair(value?.toString(), ts)
        }
        onWrite(op)
        if (currentTs == null || currentTs < ts) {
            this.entries[key + DOUBLE] = Pair(value?.toString(), ts)
        }
        return op
    }

    /**
     * Puts a [key] / Int [value] pair into the map.
     *
     * If [value] is null, the [key] is deleted.
     * @return the delta corresponding to this operation.
     */
    @Name("setInt")
    fun put(key: String, value: Int?): LWWMap {
        val op = LWWMap()
        val currentTs = this.entries[key + INTEGER]?.second
        val ts = env.tick()
        if (currentTs == null || currentTs < ts) {
            op.entries[key + INTEGER] = Pair(value?.toString(), ts)
        }
        onWrite(op)
        if (currentTs == null || currentTs < ts) {
            this.entries[key + INTEGER] = Pair(value?.toString(), ts)
        }
        return op
    }

    /**
     * Puts a [key] / String [value] pair into the map.
     *
     * If [value] is null, the [key] is deleted.
     * @return the delta corresponding to this operation.
     */
    @Name("setString")
    fun put(key: String, value: String?): LWWMap {
        val op = LWWMap()
        val currentTs = this.entries[key + STRING]?.second
        val ts = env.tick()
        if (currentTs == null || currentTs < ts) {
            op.entries[key + STRING] = Pair(value, ts)
        }
        onWrite(op)
        if (currentTs == null || currentTs < ts) {
            this.entries[key + STRING] = Pair(value, ts)
        }
        return op
    }

    /**
     * Removes the specified [key] / Boolean value pair from this map.
     *
     * @return the delta corresponding to this operation.
     */
    @Name("deleteBoolean")
    fun deleteBoolean(key: String): LWWMap {
        return put(key, null as Boolean?)
    }

    /**
     * Removes the specified [key] / Double value pair from this map.
     *
     * @return the delta corresponding to this operation.
     */
    @Name("deleteDouble")
    fun deleteDouble(key: String): LWWMap {
        return put(key, null as Double?)
    }

    /**
     * Removes the specified [key] / Int value pair from this map.
     *
     * @return the delta corresponding to this operation.
     */
    @Name("deleteInt")
    fun deleteInt(key: String): LWWMap {
        return put(key, null as Int?)
    }

    /**
     * Removes the specified [key] / String value pair from this map.
     *
     * @return the delta corresponding to this operation.
     */
    @Name("deleteString")
    fun deleteString(key: String): LWWMap {
        return put(key, null as String?)
    }

    override fun generateDelta(vv: VersionVector): LWWMap {
        val delta = LWWMap()
        for ((key, meta) in this.entries) {
            val value = meta.first
            val ts = meta.second
            if (!vv.contains(ts)) {
                delta.entries[key] = Pair(value, ts)
            }
        }
        return delta
    }

    override fun merge(delta: DeltaCRDT) {
        if (delta !is LWWMap) throw IllegalArgumentException("LWWMap unsupported merge argument")

        var lastTs: Timestamp? = null
        for ((key, meta) in delta.entries) {
            if (lastTs == null || lastTs < meta.second) {
                lastTs = meta.second
            }
            val value = meta.first
            val ts = meta.second
            val localTs = this.entries[key]?.second
            if (localTs == null || localTs < ts) {
                this.entries[key] = Pair(value, ts)
            }
        }
        onMerge(delta, lastTs)
    }

    override fun toJson(): String {
        val jsonSerializer = JsonLWWMapSerializer(serializer())
        return Json.encodeToString(jsonSerializer, this)
    }

    companion object {
        /**
         * Constant value for key fields' separator.
         */
        private const val SEPARATOR = "%"

        /**
         * Constant suffix value for key associated to a value of type Boolean.
         */
        const val BOOLEAN = SEPARATOR + "BOOLEAN"

        /**
         * Constant suffix value for key associated to a value of type double.
         */
        const val DOUBLE = SEPARATOR + "DOUBLE"

        /**
         * Constant suffix value for key associated to a value of type integer.
         */
        const val INTEGER = SEPARATOR + "INTEGER"

        /**
         * Constant suffix value for key associated to a value of type string.
         */
        const val STRING = SEPARATOR + "STRING"

        /**
         * Get the type name for serialization.
         * @return the type as a string.
         */
        @Name("getType")
        fun getType(): String {
            return "LWWMap"
        }

        /**
         * Deserializes a given json string in a crdt map.
         * @param json the given json string.
         * @return the resulted crdt map.
         */
        @Name("fromJson")
        fun fromJson(json: String, env: Environment? = null): LWWMap {
            val jsonSerializer = JsonLWWMapSerializer(serializer())
            val obj = Json.decodeFromString(jsonSerializer, json)
            if (env != null) obj.env = env
            return obj
        }
    }
}

/**
* This class is a json transformer for LWWMap, it allows the separation between data and metadata.
*/
class JsonLWWMapSerializer(serializer: KSerializer<LWWMap>) :
    JsonTransformingSerializer<LWWMap>(serializer) {

    override fun transformSerialize(element: JsonElement): JsonElement {
        val values = mutableMapOf<String, JsonElement>()
        val entries = mutableMapOf<String, JsonElement>()
        for ((key, entry) in element.jsonObject["entries"]!!.jsonObject) {
            var value = entry.jsonObject["first"]!!.jsonPrimitive
            when {
                key.endsWith(LWWMap.BOOLEAN) -> {
                    value = JsonPrimitive(value.booleanOrNull)
                }
                key.endsWith(LWWMap.DOUBLE) -> {
                    value = JsonPrimitive(value.doubleOrNull)
                }
                key.endsWith(LWWMap.INTEGER) -> {
                    value = JsonPrimitive(value.intOrNull)
                }
            }
            values[key] = value as JsonElement
            entries[key] = entry.jsonObject["second"]!!.jsonObject
        }
        val metadata = JsonObject(mapOf("entries" to JsonObject(entries.toMap())))
        return JsonObject(mapOf("type" to JsonPrimitive("LWWMap"), "metadata" to metadata).plus(values))
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        val metadata = element.jsonObject["metadata"]!!.jsonObject
        val entries = mutableMapOf<String, JsonElement>()
        for ((key, entry) in metadata["entries"]!!.jsonObject) {
            var value = element.jsonObject[key]!!.jsonPrimitive
            if (value !is JsonNull && !key.endsWith(LWWMap.STRING)) {
                value = JsonPrimitive(value.toString())
            }
            val tmpEntry = JsonObject(mapOf("first" to value as JsonElement, "second" to entry))
            entries[key] = tmpEntry
        }
        return JsonObject(mapOf("entries" to JsonObject(entries)))
    }
}
