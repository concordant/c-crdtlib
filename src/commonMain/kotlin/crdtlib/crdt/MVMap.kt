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
 * A delta-based CRDT map implementing multi-value (MV) policy.
 *
 * Each entry behaves like a [MVRegister]:
 * values written concurrently are all kept until a write replaces them;
 * accessors return the set of current values.
 * A write (or delete) replaces every *visible* (local or merged) value.
 *
 * On merging, for each key, a value is kept iff either:
 * - it is in both replicas/deltas, or
 * - it is in one replica and its associated timestamp is not
 *   in the causal context of the other replica.
 *
 * Its JSON serialization respects the following schema:
 * ```json
 * {
 *   "type": "MVMap",
 *   "metadata": {
 *       "entries": {
 *           // $key is a string
 *           (( "$key": [ ( Timestamp.toJson() )*( , Timestamp.toJson() )? ] )*( , "$key": [ ( Timestamp.toJson() )*( , Timestamp.toJson() )? ] ))?
 *       },
 *       "causalContext": VersionVector.toJson()
 *   }
 *   // $key is a string and $value can be a Boolean, Double, Int or String
 *   ( , "$key": [
 *           (( T.toJson(), )*( T.toJson() ))?
 *   ] )*
 * }
 * ```
 */
@Serializable
class MVMap : DeltaCRDT {

    /**
     * A mutable map storing metadata relative to each key.
     */
    @Required
    private val entries: MutableMap<String, MutableSet<Pair<String?, Timestamp>>> = mutableMapOf()

    /**
     * A causal context summarizing executed operations.
     */
    @Required
    private val causalContext: VersionVector = VersionVector()

    /**
     * Default constructor.
     */
    constructor() : super()
    constructor(env: Environment) : super(env)

    /**
     * Constructor initializing the causal context.
     */
    constructor(cc: VersionVector, env: Environment) : super(env) {
        this.causalContext.update(cc)
    }

    override fun copy(): MVMap {
        val copy = MVMap(this.env)
        copy.entries.putAll(this.entries.toMutableMap())
        copy.causalContext.update(this.causalContext)
        return copy
    }

    /**
     * Gets the set of Boolean values corresponding to a given [key],
     * or null if the key is not present in the map.
     *
     * A delete concurrent with writes appears as a null value
     * in the returned set.
     */
    @Name("getBoolean")
    fun getBoolean(key: String): Set<Boolean?>? {
        onRead()
        val setOfValues = this.entries[key + BOOLEAN]?.map { it.first?.toBoolean() }?.toSet()
        if (setOfValues == mutableSetOf(null)) return null
        return setOfValues
    }

    /**
     * Gets the set of double values corresponding to a given [key],
     * or null if the key is not present in the map.
     *
     * A delete concurrent with writes appears as a null value
     * in the returned set.
     */
    @Name("getDouble")
    fun getDouble(key: String): Set<Double?>? {
        onRead()
        val setOfValues = this.entries[key + DOUBLE]?.map { it.first?.toDouble() }?.toSet()
        if (setOfValues == mutableSetOf(null)) return null
        return setOfValues
    }

    /**
     * Gets the set of integer values corresponding to a given [key],
     * or null if the key is not present in the map.
     *
     * A delete concurrent with writes appears as a null value
     * in the returned set.
     */
    @Name("getInt")
    fun getInt(key: String): Set<Int?>? {
        onRead()
        val setOfValues = this.entries[key + INTEGER]?.map { it.first?.toInt() }?.toSet()
        if (setOfValues == mutableSetOf(null)) return null
        return setOfValues
    }

    /**
     * Gets the set of string of values corresponding to a given [key],
     * or null if the key is not present in the map.
     *
     * A delete concurrent with writes appears as a null value
     * in the returned set.
     */
    @Name("getString")
    fun getString(key: String): Set<String?>? {
        onRead()
        val setOfValues = this.entries[key + STRING]?.map { it.first }?.toSet()
        if (setOfValues == mutableSetOf(null)) return null
        return setOfValues
    }

    /**
     * Returns an iterator over the Boolean entries in the map.
     *
     * @see getBoolean for a description of each entry.
     */
    @Name("iteratorBoolean")
    fun iteratorBoolean(): Iterator<Pair<String, Set<Boolean?>>> {
        onRead()
        return this.entries.asSequence().filter { (k, _) -> k.endsWith(BOOLEAN) }
            .map { (k, v) -> Pair(k.removeSuffix(BOOLEAN), v.map { it.first?.toBoolean() }.toSet()) }
            .filter { (_, v) -> v != mutableSetOf(null) }.iterator()
    }

    /**
     * Returns an iterator over the Double entries in the map.
     *
     * @see getDouble for a description of each entry.
     */
    @Name("iteratorDouble")
    fun iteratorDouble(): Iterator<Pair<String, Set<Double?>>> {
        onRead()
        return this.entries.asSequence().filter { (k, _) -> k.endsWith(DOUBLE) }
            .map { (k, v) -> Pair(k.removeSuffix(DOUBLE), v.map { it.first?.toDouble() }.toSet()) }
            .filter { (_, v) -> v != mutableSetOf(null) }.iterator()
    }

    /**
     * Returns an iterator over the Int entries in the map.
     *
     * @see getInt for a description of each entry.
     */
    @Name("iteratorInt")
    fun iteratorInt(): Iterator<Pair<String, Set<Int?>>> {
        onRead()
        return this.entries.asSequence().filter { (k, _) -> k.endsWith(INTEGER) }
            .map { (k, v) -> Pair(k.removeSuffix(INTEGER), v.map { it.first?.toInt() }.toSet()) }
            .filter { (_, v) -> v != mutableSetOf(null) }.iterator()
    }

    /**
     * Returns an iterator over the String entries in the map.
     *
     * @see getString for a description of each entry.
     */
    @Name("iteratorString")
    fun iteratorString(): Iterator<Pair<String, Set<String?>>> {
        onRead()
        return this.entries.asSequence().filter { (k, _) -> k.endsWith(STRING) }
            .map { (k, v) -> Pair(k.removeSuffix(STRING), v.map { it.first }.toSet()) }
            .filter { (_, v) -> v != mutableSetOf(null) }.iterator()
    }

    /**
     * Puts a [key] / Boolean [value] pair into the map.
     *
     * If [value] is null, the [key] is deleted.
     * @return the delta corresponding to this operation.
     */
    @Name("setBoolean")
    fun put(key: String, value: Boolean?): MVMap {
        val op = MVMap()
        val ts = env.tick()
        if (!this.causalContext.contains(ts)) {
            op.entries[key + BOOLEAN] = mutableSetOf(Pair(value?.toString(), ts))
            op.causalContext.update(ts)
        }
        onWrite(op)
        if (!this.causalContext.contains(ts)) {
            this.entries[key + BOOLEAN] = mutableSetOf(Pair(value?.toString(), ts))
            this.causalContext.update(ts)
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
    fun put(key: String, value: Double?): MVMap {
        val op = MVMap()
        val ts = env.tick()
        if (!this.causalContext.contains(ts)) {
            op.entries[key + DOUBLE] = mutableSetOf(Pair(value?.toString(), ts))
            op.causalContext.update(ts)
        }
        onWrite(op)
        if (!this.causalContext.contains(ts)) {
            this.entries[key + DOUBLE] = mutableSetOf(Pair(value?.toString(), ts))
            this.causalContext.update(ts)
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
    fun put(key: String, value: Int?): MVMap {
        val op = MVMap()
        val ts = env.tick()
        if (!this.causalContext.contains(ts)) {
            op.entries[key + INTEGER] = mutableSetOf(Pair(value?.toString(), ts))
            op.causalContext.update(ts)
        }
        onWrite(op)
        if (!this.causalContext.contains(ts)) {
            this.entries[key + INTEGER] = mutableSetOf(Pair(value?.toString(), ts))
            this.causalContext.update(ts)
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
    fun put(key: String, value: String?): MVMap {
        val op = MVMap()
        val ts = env.tick()
        if (!this.causalContext.contains(ts)) {
            op.entries[key + STRING] = mutableSetOf(Pair(value, ts))
            op.causalContext.update(ts)
        }
        onWrite(op)
        if (!this.causalContext.contains(ts)) {
            this.entries[key + STRING] = mutableSetOf(Pair(value, ts))
            this.causalContext.update(ts)
        }
        return op
    }

    /**
     * Removes the specified [key] / Boolean value pair from this map.
     *
     * @return the delta corresponding to this operation.
     */
    @Name("deleteBoolean")
    fun deleteBoolean(key: String): MVMap {
        return put(key, null as Boolean?)
    }

    /**
     * Removes the specified [key] / Double value pair from this map.
     *
     * @return the delta corresponding to this operation.
     */
    @Name("deleteDouble")
    fun deleteDouble(key: String): MVMap {
        return put(key, null as Double?)
    }

    /**
     * Removes the specified [key] / Int value pair from this map.
     *
     * @return the delta corresponding to this operation.
     */
    @Name("deleteInt")
    fun deleteInt(key: String): MVMap {
        return put(key, null as Int?)
    }

    /**
     * Removes the specified [key] / String value pair from this map.
     *
     * @return the delta corresponding to this operation.
     */
    @Name("deleteString")
    fun deleteString(key: String): MVMap {
        return put(key, null as String?)
    }

    override fun generateDelta(vv: VersionVector): MVMap {
        val delta = MVMap()
        for ((key, meta) in this.entries) {
            if (meta.any { !vv.contains(it.second) }) {
                delta.entries[key] = meta.toMutableSet()
            }
        }
        delta.causalContext.update(this.causalContext)
        return delta
    }

    override fun merge(delta: DeltaCRDT) {
        if (delta !is MVMap) throw IllegalArgumentException("MVMap unsupported merge argument")

        var lastTs: Timestamp? = null
        for ((key, foreignEntries) in delta.entries) {

            val keptEntries = mutableSetOf<Pair<String?, Timestamp>>()
            val localEntries = this.entries[key]
            if (localEntries != null) {
                for ((value, ts) in localEntries) {
                    if (!delta.causalContext.contains(ts) || foreignEntries.any { it.second == ts }) {
                        keptEntries.add(Pair(value, ts))
                    }
                }
                for ((value, ts) in foreignEntries) {
                    if (lastTs == null || lastTs < ts) {
                        lastTs = ts
                    }
                    if (!this.causalContext.contains(ts)) {
                        keptEntries.add(Pair(value, ts))
                    }
                }
            } else {
                for ((value, ts) in foreignEntries) {
                    if (lastTs == null || lastTs < ts) {
                        lastTs = ts
                    }
                    keptEntries.add(Pair(value, ts))
                }
            }
            this.entries[key] = keptEntries
        }
        this.causalContext.update(delta.causalContext)
        onMerge(delta, lastTs)
    }

    /**
     * Serializes this crdt map to a json string.
     * @return the resulted json string.
     */
    override fun toJson(): String {
        val jsonSerializer = JsonMVMapSerializer(serializer())
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
            return "MVMap"
        }

        @Name("fromJson")
        fun fromJson(json: String, env: Environment? = null): MVMap {
            val jsonSerializer = JsonMVMapSerializer(serializer())
            val obj = Json.decodeFromString(jsonSerializer, json)
            if (env != null) obj.env = env
            return obj
        }
    }
}

/**
* This class is a json transformer for MVMap, it allows the separation between data and metadata.
*/
class JsonMVMapSerializer(serializer: KSerializer<MVMap>) :
    JsonTransformingSerializer<MVMap>(serializer) {

    override fun transformSerialize(element: JsonElement): JsonElement {
        val values = mutableMapOf<String, JsonElement>()
        val entries = mutableMapOf<String, JsonElement>()
        val causalContext = element.jsonObject["causalContext"]!!.jsonObject
        for ((key, entry) in element.jsonObject["entries"]!!.jsonObject) {
            val value = mutableListOf<JsonElement>()
            val meta = mutableListOf<JsonElement>()
            for (tmpPair in entry.jsonArray) {
                when {
                    key.endsWith(MVMap.BOOLEAN) -> {
                        value.add(JsonPrimitive(tmpPair.jsonObject["first"]?.jsonPrimitive?.booleanOrNull) as JsonElement)
                    }
                    key.endsWith(MVMap.DOUBLE) -> {
                        value.add(JsonPrimitive(tmpPair.jsonObject["first"]?.jsonPrimitive?.doubleOrNull) as JsonElement)
                    }
                    key.endsWith(MVMap.INTEGER) -> {
                        value.add(JsonPrimitive(tmpPair.jsonObject["first"]?.jsonPrimitive?.intOrNull) as JsonElement)
                    }
                    else -> {
                        value.add(tmpPair.jsonObject["first"] as JsonElement)
                    }
                }
                meta.add(tmpPair.jsonObject["second"]!!.jsonObject)
            }
            values[key] = JsonArray(value)
            entries[key] = JsonArray(meta)
        }
        val metadata = JsonObject(mapOf("entries" to JsonObject(entries.toMap()), "causalContext" to causalContext))
        return JsonObject(mapOf("type" to JsonPrimitive("MVMap"), "metadata" to metadata).plus(values))
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        val metadata = element.jsonObject["metadata"]!!.jsonObject
        val causalContext = metadata["causalContext"]!!.jsonObject
        val entries = mutableMapOf<String, JsonElement>()
        for ((key, meta) in metadata["entries"]!!.jsonObject) {
            val values = element.jsonObject[key]!!.jsonArray
            val tmpEntries = mutableListOf<JsonElement>()
            for ((idx, ts) in meta.jsonArray.withIndex()) {
                var value = values[idx]
                if (value !is JsonNull && !key.endsWith(MVMap.STRING)) {
                    value = JsonPrimitive(value.toString())
                }
                val tmpEntry = JsonObject(mapOf("first" to value, "second" to ts))
                tmpEntries.add(tmpEntry)
            }
            entries[key] = JsonArray(tmpEntries)
        }
        return JsonObject(mapOf("entries" to JsonObject(entries), "causalContext" to causalContext))
    }
}
