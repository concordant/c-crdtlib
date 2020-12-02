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
* This class is a delta-based CRDT map implementing last writer wins (MV) to resolve conflicts.
* It is serializable to JSON and respect the following schema:
* {
*   "_type": "MVMap",
*   "_metadata": {
*       "entries": {
*           // $key is a string
*           (( "$key": [ ( Timestamp.toJson() )*( , Timestamp.toJson() )? ] )*( , "$key": [ ( Timestamp.toJson() )*( , Timestamp.toJson() )? ] ))?
*       },
*       "causalContext": VersionVector.toJson()
*   }
*   // $key is a string and $value can be Boolean, double, integer or string
*   ( , "$key": [
*           (( T.toJson(), )*( T.toJson() ))?
*   ] )*
* }
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
    private var causalContext: VersionVector = VersionVector()

    /**
     * Default constructor.
     */
    constructor()

    /**
     * Constructor initializing the causal context.
     */
    constructor(cc: VersionVector) {
        this.causalContext = cc
    }

    /**
     * Gets the set of Boolean values corresponding to a given key.
     * @param key the key that should be looked for.
     * @return the set of Boolean values associated to the key, or null if the key is not present in
     * the map or last operation is a delete.
     */
    @Name("getBoolean")
    fun getBoolean(key: String): Set<Boolean?>? {
        val setOfValues = this.entries[key + BOOLEAN]?.map { it.first?.toBoolean() }?.toSet()
        if (setOfValues == mutableSetOf(null)) return null
        return setOfValues
    }

    /**
     * Gets the set of double values corresponding to a given key.
     * @param key the key that should be looked for.
     * @return the set of double values associated to the key, or null if the key is not present in
     * the map or last operation is a delete.
     */
    @Name("getDouble")
    fun getDouble(key: String): Set<Double?>? {
        val setOfValues = this.entries[key + DOUBLE]?.map { it.first?.toDouble() }?.toSet()
        if (setOfValues == mutableSetOf(null)) return null
        return setOfValues
    }

    /**
     * Gets the set of integer values corresponding to a given key.
     * @param key the key that should be looked for.
     * @return the set of integer values associated to the key, or null if the key is not present in
     * the map or last operation is a delete.
     */
    @Name("getInt")
    fun getInt(key: String): Set<Int?>? {
        val setOfValues = this.entries[key + INTEGER]?.map { it.first?.toInt() }?.toSet()
        if (setOfValues == mutableSetOf(null)) return null
        return setOfValues
    }

    /**
     * Gets the set of string of values corresponding to a given key.
     * @param key the key that should be looked for.
     * @return the set of string values associated to the key, or null if the key is not present in
     * the map or last operation is a delete.
     */
    @Name("getString")
    fun getString(key: String): Set<String?>? {
        val setOfValues = this.entries[key + STRING]?.map { it.first }?.toSet()
        if (setOfValues == mutableSetOf(null)) return null
        return setOfValues
    }

    /**
     * Gets an iterator containing the Boolean values currently stored in the map.
     * @return an iterator over the Boolean values stored in the map.
     */
    @Name("iteratorBoolean")
    fun iteratorBoolean(): Iterator<Pair<String, Set<Boolean?>>> {
        return this.entries.asSequence().filter { (k, _) -> k.endsWith(BOOLEAN) }
            .map { (k, v) -> Pair(k.removeSuffix(BOOLEAN), v.map { it.first?.toBoolean() }.toSet()) }
            .filter { (_, v) -> v != mutableSetOf(null) }.iterator()
    }

    /**
     * Gets an iterator containing the double values currently stored in the map.
     * @return an iterator over the double values stored in the map.
     */
    @Name("iteratorDouble")
    fun iteratorDouble(): Iterator<Pair<String, Set<Double?>>> {
        return this.entries.asSequence().filter { (k, _) -> k.endsWith(DOUBLE) }
            .map { (k, v) -> Pair(k.removeSuffix(DOUBLE), v.map { it.first?.toDouble() }.toSet()) }
            .filter { (_, v) -> v != mutableSetOf(null) }.iterator()
    }

    /**
     * Gets an iterator containing the integer values currently stored in the map.
     * @return an iterator over the integer values stored in the map.
     */
    @Name("iteratorInt")
    fun iteratorInt(): Iterator<Pair<String, Set<Int?>>> {
        return this.entries.asSequence().filter { (k, _) -> k.endsWith(INTEGER) }
            .map { (k, v) -> Pair(k.removeSuffix(INTEGER), v.map { it.first?.toInt() }.toSet()) }
            .filter { (_, v) -> v != mutableSetOf(null) }.iterator()
    }

    /**
     * Gets an iterator containing the string values currently stored in the map.
     * @return an iterator over the string values stored in the map.
     */
    @Name("iteratorString")
    fun iteratorString(): Iterator<Pair<String, Set<String?>>> {
        return this.entries.asSequence().filter { (k, _) -> k.endsWith(STRING) }
            .map { (k, v) -> Pair(k.removeSuffix(STRING), v.map { it.first }.toSet()) }
            .filter { (_, v) -> v != mutableSetOf(null) }.iterator()
    }

    /**
     * Puts a key / Boolean value pair into the map.
     * @param key the key that is targeted.
     * @param value the Boolean value that should be assigned to the key.
     * @param ts the timestamp of this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("setBoolean")
    fun put(key: String, value: Boolean?, ts: Timestamp): MVMap {
        val op = MVMap()
        if (!this.causalContext.contains(ts)) {
            var meta = this.entries[key + BOOLEAN]
            if (meta == null) meta = mutableSetOf()
            else meta.clear()
            meta.add(Pair(value?.toString(), ts))

            this.entries[key + BOOLEAN] = meta
            op.entries[key + BOOLEAN] = meta.toMutableSet()
            this.causalContext.update(ts)
            op.causalContext.update(ts)
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
    fun put(key: String, value: Double?, ts: Timestamp): MVMap {
        val op = MVMap()
        if (!this.causalContext.contains(ts)) {
            var meta = this.entries[key + DOUBLE]
            if (meta == null) meta = mutableSetOf()
            else meta.clear()
            meta.add(Pair(value?.toString(), ts))

            this.entries[key + DOUBLE] = meta
            op.entries[key + DOUBLE] = meta.toMutableSet()
            this.causalContext.update(ts)
            op.causalContext.update(ts)
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
    fun put(key: String, value: Int?, ts: Timestamp): MVMap {
        val op = MVMap()
        if (!this.causalContext.contains(ts)) {
            var meta = this.entries[key + INTEGER]
            if (meta == null) meta = mutableSetOf()
            else meta.clear()
            meta.add(Pair(value?.toString(), ts))

            this.entries[key + INTEGER] = meta
            op.entries[key + INTEGER] = meta.toMutableSet()
            this.causalContext.update(ts)
            op.causalContext.update(ts)
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
    fun put(key: String, value: String?, ts: Timestamp): MVMap {
        val op = MVMap()
        if (!this.causalContext.contains(ts)) {
            var meta = this.entries[key + STRING]
            if (meta == null) meta = mutableSetOf()
            else meta.clear()
            meta.add(Pair(value, ts))

            this.entries[key + STRING] = meta
            op.entries[key + STRING] = meta.toMutableSet()
            this.causalContext.update(ts)
            op.causalContext.update(ts)
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
    fun deleteBoolean(key: String, ts: Timestamp): MVMap {
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
    fun deleteDouble(key: String, ts: Timestamp): MVMap {
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
    fun deleteInt(key: String, ts: Timestamp): MVMap {
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
    fun deleteString(key: String, ts: Timestamp): MVMap {
        return put(key, null as String?, ts)
    }

    /**
     * Generates a delta of operations recorded and not already present in a given context.
     * @param vv the context used as starting point to generate the delta.
     * @return the corresponding delta of operations.
     */
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

    /**
     * Merges information contained in a given delta into the local replica, the merge is unilateral
     * and only the local replica is modified.
     * A foreign (local) value is kept iff it is contained in the local (foreign) replica or its
     * associated timestamp is not included in the local (foreign) causal context.
     * @param delta the delta that should be merged with the local replica.
     */
    override fun merge(delta: DeltaCRDT) {
        if (delta !is MVMap) throw IllegalArgumentException("MVMap unsupported merge argument")

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
                    if (!this.causalContext.contains(ts)) {
                        keptEntries.add(Pair(value, ts))
                    }
                }
            } else {
                for ((value, ts) in foreignEntries) {
                    keptEntries.add(Pair(value, ts))
                }
            }
            this.entries[key] = keptEntries
        }
        this.causalContext.update(delta.causalContext)
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
         * Deserializes a given json string in a crdt map.
         * @param json the given json string.
         * @return the resulted crdt map.
         */
        @Name("fromJson")
        fun fromJson(json: String): MVMap {
            val jsonSerializer = JsonMVMapSerializer(serializer())
            return Json.decodeFromString(jsonSerializer, json)
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
        return JsonObject(mapOf("_type" to JsonPrimitive("MVMap"), "_metadata" to metadata).plus(values))
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        val metadata = element.jsonObject["_metadata"]!!.jsonObject
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
