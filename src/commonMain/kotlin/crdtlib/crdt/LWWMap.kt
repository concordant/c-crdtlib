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
* This class is a delta-based CRDT map implementing last writer wins (LWW) to resolve conflicts.
* It is serializable to JSON and respect the following schema:
* {
*   "_type": "LWWMap",
*   "_metadata": {
*       "entries": {
*           // $key is a string
*           (( "$key": Timestamp.toJson(), )*( "$key": Timestamp.toJson() ))?
*       }
*   }
*   // $key is a string and $value can be Boolean, double, integer or string
*   ( , "$key": "$value" )*
* }
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
    constructor()

    /**
     * Gets the Boolean value corresponding to a given key.
     * @param key the key that should be looked for.
     * @return the Boolean value associated to the key, or null if the key is not present in the map
     * or last operation is a delete.
     */
    @Name("getBoolean")
    fun getBoolean(key: String): Boolean? {
        return this.entries[key + BOOLEAN]?.first?.toBoolean()
    }

    /**
     * Gets the double value corresponding to a given key.
     * @param key the key that should be looked for.
     * @return the double value associated to the key, or null if the key is not present in the map
     * or last operation is a delete.
     */
    @Name("getDouble")
    fun getDouble(key: String): Double? {
        return this.entries[key + DOUBLE]?.first?.toDoubleOrNull()
    }

    /**
     * Gets the integer value corresponding to a given key.
     * @param key the key that should be looked for.
     * @return the integer value associated to the key, or null if the key is not present in the map
     * or last operation is a delete.
     */
    @Name("getInt")
    fun getInt(key: String): Int? {
        return this.entries[key + INTEGER]?.first?.toIntOrNull()
    }

    /**
     * Gets the string value corresponding to a given key.
     * @param key the key that should be looked for.
     * @return the string value associated to the key, or null if the key is not present in the map
     * or last operation is a delete.
     */
    @Name("getString")
    fun getString(key: String): String? {
        return this.entries[key + STRING]?.first
    }

    /**
     * Gets an iterator containing the Boolean value currently stored in the map.
     * @return an iterator over the Boolean value stored in the map.
     */
    @Name("iteratorBoolean")
    fun iteratorBoolean(): Iterator<Pair<String, Boolean>> {
        return this.entries.filter { (k, v) -> k.endsWith(BOOLEAN) && v.first != null }
            .map { (k, v) -> Pair(k.removeSuffix(BOOLEAN), v.first.toBoolean()) }.iterator()
    }

    /**
     * Gets an iterator containing the double value currently stored in the map.
     * @return an iterator over the double value stored in the map.
     */
    @Name("iteratorDouble")
    fun iteratorDouble(): Iterator<Pair<String, Double>> {
        return this.entries.filter { (k, v) -> k.endsWith(DOUBLE) && v.first != null }
            .map { (k, v) -> Pair(k.removeSuffix(DOUBLE), v.first!!.toDouble()) }.iterator()
    }

    /**
     * Gets an iterator containing the integer value currently stored in the map.
     * @return an iterator over the integer value stored in the map.
     */
    @Name("iteratorInt")
    fun iteratorInt(): Iterator<Pair<String, Int>> {
        return this.entries.filter { (k, v) -> k.endsWith(INTEGER) && v.first != null }
            .map { (k, v) -> Pair(k.removeSuffix(INTEGER), v.first!!.toInt()) }.iterator()
    }

    /**
     * Gets an iterator containing the string value currently stored in the map.
     * @return an iterator over the string value stored in the map.
     */
    @Name("iteratorString")
    fun iteratorString(): Iterator<Pair<String, String>> {
        return this.entries.filter { (k, v) -> k.endsWith(STRING) && v.first != null }
            .map { (k, v) -> Pair(k.removeSuffix(STRING), v.first!!) }.iterator()
    }

    /**
     * Puts a key / Boolean value pair into the map.
     * @param key the key that is targeted.
     * @param value the Boolean value that should be assigned to the key.
     * @param ts the timestamp of this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("setBoolean")
    fun put(key: String, value: Boolean?, ts: Timestamp): LWWMap {
        val op = LWWMap()
        val currentTs = this.entries[key + BOOLEAN]?.second
        if (currentTs == null || currentTs < ts) {
            this.entries[key + BOOLEAN] = Pair(value?.toString(), ts)
            op.entries[key + BOOLEAN] = Pair(value?.toString(), ts)
        }
        return op
    }

    /**
     * Puts a key / double value pair into the map.
     * @param key the key that is targeted.
     * @param value the double value that should be assigned to the key.
     * @param ts the timestamp of this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("setDouble")
    fun put(key: String, value: Double?, ts: Timestamp): LWWMap {
        val op = LWWMap()
        val currentTs = this.entries[key + DOUBLE]?.second
        if (currentTs == null || currentTs < ts) {
            this.entries[key + DOUBLE] = Pair(value?.toString(), ts)
            op.entries[key + DOUBLE] = Pair(value?.toString(), ts)
        }
        return op
    }

    /**
     * Puts a key / integer value pair into the map.
     * @param key the key that is targeted.
     * @param value the integer value that should be assigned to the key.
     * @param ts the timestamp of this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("setInt")
    fun put(key: String, value: Int?, ts: Timestamp): LWWMap {
        val op = LWWMap()
        val currentTs = this.entries[key + INTEGER]?.second
        if (currentTs == null || currentTs < ts) {
            this.entries[key + INTEGER] = Pair(value?.toString(), ts)
            op.entries[key + INTEGER] = Pair(value?.toString(), ts)
        }
        return op
    }

    /**
     * Puts a key / string value pair into the map.
     * @param key the key that is targeted.
     * @param value the string value that should be assigned to the key.
     * @param ts the timestamp of this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("setString")
    fun put(key: String, value: String?, ts: Timestamp): LWWMap {
        val op = LWWMap()
        val currentTs = this.entries[key + STRING]?.second
        if (currentTs == null || currentTs < ts) {
            this.entries[key + STRING] = Pair(value, ts)
            op.entries[key + STRING] = Pair(value, ts)
        }
        return op
    }

    /**
     * Deletes a given key / Boolean value pair if it is present in the map and has not yet been
     * deleted.
     * @param key the key that should be deleted.
     * @param ts the timestamp linked to this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("deleteBoolean")
    fun deleteBoolean(key: String, ts: Timestamp): LWWMap {
        return put(key, null as Boolean?, ts)
    }

    /**
     * Deletes a given key / double value pair if it is present in the map and has not yet been
     * deleted.
     * @param key the key that should be deleted.
     * @param ts the timestamp linked to this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("deleteDouble")
    fun deleteDouble(key: String, ts: Timestamp): LWWMap {
        return put(key, null as Double?, ts)
    }

    /**
     * Deletes a given key / integer value pair if it is present in the map and has not yet been
     * deleted.
     * @param key the key that should be deleted.
     * @param ts the timestamp linked to this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("deleteInt")
    fun deleteInt(key: String, ts: Timestamp): LWWMap {
        return put(key, null as Int?, ts)
    }

    /**
     * Deletes a given key / string value pair if it is present in the map and has not yet been
     * deleted.
     * @param key the key that should be deleted.
     * @param ts the timestamp linked to this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("deleteString")
    fun deleteString(key: String, ts: Timestamp): LWWMap {
        return put(key, null as String?, ts)
    }

    /**
     * Generates a delta of operations recorded and not already present in a given context.
     * @param vv the context used as starting point to generate the delta.
     * @return the corresponding delta of operations.
     */
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

    /**
     * Merges information contained in a given delta into the local replica, the merge is unilateral
     * and only the local replica is modified.
     * A foreign operation (i.e., put or delete) is applied iff last locally stored operation has a
     * smaller timestamp compared to the foreign one, or there is no local operation recorded.
     * @param delta the delta that should be merged with the local replica.
     */
    override fun merge(delta: DeltaCRDT) {
        if (delta !is LWWMap) throw IllegalArgumentException("LWWMap unsupported merge argument")

        for ((key, meta) in delta.entries) {
            val value = meta.first
            val ts = meta.second
            val localTs = this.entries[key]?.second
            if (localTs == null || localTs < ts) {
                this.entries[key] = Pair(value, ts)
            }
        }
    }

    /**
     * Serializes this crdt map to a json string.
     * @return the resulted json string.
     */
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
         * Deserializes a given json string in a crdt map.
         * @param json the given json string.
         * @return the resulted crdt map.
         */
        @Name("fromJson")
        fun fromJson(json: String): LWWMap {
            val jsonSerializer = JsonLWWMapSerializer(serializer())
            return Json.decodeFromString(jsonSerializer, json)
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
        return JsonObject(mapOf("_type" to JsonPrimitive("LWWMap"), "_metadata" to metadata).plus(values))
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        val metadata = element.jsonObject["_metadata"]!!.jsonObject
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
