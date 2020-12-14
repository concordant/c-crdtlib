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
*   "type": "Map",
*   "metadata": {
*       "lwwMap": {
*           // $key is a string
*           ( "$key": Timestamp.toJson() )*( , "$key": Timestamp.toJson() )?
*       },
*       "mvMap": {
*           "entries": {
*               // $key is a string
*               (( "$key": [ ( Timestamp.toJson() )*( , Timestamp.toJson() )? ] )*( , "$key": [ ( Timestamp.toJson() )*( , Timestamp.toJson() )? ] ))?
*           },
*           "causalContext": VersionVector.toJson()
*       },
*       "cntMap": {
*           ( "$key": PNCounter.toJson() )*( , "$key": PNCounter.toJson() )?
*       }
*   }
*   // $key is a string and $value can be Boolean, double, integer, string or array
*   ( , "$key": "$value" )*
* }
*/
@Serializable
class Map : DeltaCRDT {

    /**
     * A LWW map storing key / value pairs that should be merged using LWW.
     */
    private val lwwMap: LWWMap = LWWMap()

    /**
     * A LWW map storing key / value pairs that should be merged using LWW.
     */
    private val mvMap: MVMap = MVMap()

    @Required
    private val cntMap: MutableMap<String, PNCounter> = mutableMapOf()

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
    @Name("getLWWBoolean")
    fun getLWWBoolean(key: String): Boolean? {
        return this.lwwMap.getBoolean(key)
    }

    /**
     * Gets the Boolean value corresponding to a given key.
     * @param key the key that should be looked for.
     * @return the Boolean value associated to the key, or null if the key is not present in the map
     * or last operation is a delete.
     */
    @Name("getLWWDouble")
    fun getLWWDouble(key: String): Double? {
        return this.lwwMap.getDouble(key)
    }

    /**
     * Gets the integer value corresponding to a given key.
     * @param key the key that should be looked for.
     * @return the integer value associated to the key, or null if the key is not present in the map
     * or last operation is a delete.
     */
    @Name("getLWWInt")
    fun getLWWInt(key: String): Int? {
        return this.lwwMap.getInt(key)
    }

    /**
     * Gets the string value corresponding to a given key.
     * @param key the key that should be looked for.
     * @return the string value associated to the key, or null if the key is not present in the map
     * or last operation is a delete.
     */
    @Name("getLWWString")
    fun getLWWString(key: String): String? {
        return this.lwwMap.getString(key)
    }

    /**
     * Gets the set of Boolean values corresponding to a given key.
     * @param key the key that should be looked for.
     * @return the set of Boolean values associated to the key, or null if the key is not present in
     * the map or last operation is a delete.
     */
    @Name("getMVBoolean")
    fun getMVBoolean(key: String): Set<Boolean?>? {
        return this.mvMap.getBoolean(key)
    }

    /**
     * Gets the set of double values corresponding to a given key.
     * @param key the key that should be looked for.
     * @return the set of double values associated to the key, or null if the key is not present in
     * the map or last operation is a delete.
     */
    @Name("getMVDouble")
    fun getMVDouble(key: String): Set<Double?>? {
        return this.mvMap.getDouble(key)
    }

    /**
     * Gets the set of integer values corresponding to a given key.
     * @param key the key that should be looked for.
     * @return the set of integer values associated to the key, or null if the key is not present in
     * the map or last operation is a delete.
     */
    @Name("getMVInt")
    fun getMVInt(key: String): Set<Int?>? {
        return this.mvMap.getInt(key)
    }

    /**
     * Gets the set of string of values corresponding to a given key.
     * @param key the key that should be looked for.
     * @return the set of string values associated to the key, or null if the key is not present in
     * the map or last operation is a delete.
     */
    @Name("getMVString")
    fun getMVString(key: String): Set<String?>? {
        return this.mvMap.getString(key)
    }

    /**
     * Gets the set of integer values corresponding to a given key.
     * @param key the key that should be looked for.
     * @return the set of integer values associated to the key, or null if the key is not present in
     * the map or last operation is a delete.
     */
    @Name("getCntInt")
    fun getCntInt(key: String): Int? {
        return this.cntMap[key]?.get()
    }

    /**
     * Gets an iterator containing the Boolean value currently stored in the map.
     * @return an iterator over the Boolean value stored in the map.
     */
    @Name("iteratorLWWBoolean")
    fun iteratorLWWBoolean(): Iterator<Pair<String, Boolean>> {
        return this.lwwMap.iteratorBoolean()
    }

    /**
     * Gets an iterator containing the double value currently stored in the map.
     * @return an iterator over the double value stored in the map.
     */
    @Name("iteratorLWWDouble")
    fun iteratorLWWDouble(): Iterator<Pair<String, Double>> {
        return this.lwwMap.iteratorDouble()
    }

    /**
     * Gets an iterator containing the integer value currently stored in the map.
     * @return an iterator over the integer value stored in the map.
     */
    @Name("iteratorLWWInt")
    fun iteratorLWWInt(): Iterator<Pair<String, Int>> {
        return this.lwwMap.iteratorInt()
    }

    /**
     * Gets an iterator containing the string value currently stored in the map.
     * @return an iterator over the string value stored in the map.
     */
    @Name("iteratorLWWString")
    fun iteratorLWWString(): Iterator<Pair<String, String>> {
        return this.lwwMap.iteratorString()
    }

    /**
     * Gets an iterator containing the Boolean values currently stored in the map.
     * @return an iterator over the Boolean values stored in the map.
     */
    @Name("iteratorMVBoolean")
    fun iteratorMVBoolean(): Iterator<Pair<String, Set<Boolean?>>> {
        return this.mvMap.iteratorBoolean()
    }

    /**
     * Gets an iterator containing the double values currently stored in the map.
     * @return an iterator over the double values stored in the map.
     */
    @Name("iteratorMVDouble")
    fun iteratorMVDouble(): Iterator<Pair<String, Set<Double?>>> {
        return this.mvMap.iteratorDouble()
    }

    /**
     * Gets an iterator containing the integer values currently stored in the map.
     * @return an iterator over the integer values stored in the map.
     */
    @Name("iteratorMVInt")
    fun iteratorMVInt(): Iterator<Pair<String, Set<Int?>>> {
        return this.mvMap.iteratorInt()
    }

    /**
     * Gets an iterator containing the string values currently stored in the map.
     * @return an iterator over the string values stored in the map.
     */
    @Name("iteratorMVString")
    fun iteratorMVString(): Iterator<Pair<String, Set<String?>>> {
        return this.mvMap.iteratorString()
    }

    /**
     * Gets an iterator containing the integer values currently stored in the map.
     * @return an iterator over the integer values stored in the map.
     */
    @Name("iteratorCntInt")
    fun iteratorCntInt(): Iterator<Pair<String, Int>> {
        return this.cntMap.asSequence().map { (k, v) -> Pair(k, v.get()) }.iterator()
    }

    /**
     * Puts a key / Boolean value pair into the map.
     * @param key the key that is targeted.
     * @param value the Boolean value that should be assigned to the key.
     * @param ts the timestamp of this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("setLWWBoolean")
    fun putLWW(key: String, value: Boolean?, ts: Timestamp): Map {
        val op = Map()
        op.lwwMap.merge(this.lwwMap.put(key, value, ts))
        return op
    }

    /**
     * Puts a key / double value pair into the map.
     * @param key the key that is targeted.
     * @param value the double value that should be assigned to the key.
     * @param ts the timestamp of this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("setLWWDouble")
    fun putLWW(key: String, value: Double?, ts: Timestamp): Map {
        val op = Map()
        op.lwwMap.merge(this.lwwMap.put(key, value, ts))
        return op
    }

    /**
     * Puts a key / integer value pair into the map.
     * @param key the key that is targeted.
     * @param value the integer value that should be assigned to the key.
     * @param ts the timestamp of this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("setLWWInt")
    fun putLWW(key: String, value: Int?, ts: Timestamp): Map {
        val op = Map()
        op.lwwMap.merge(this.lwwMap.put(key, value, ts))
        return op
    }

    /**
     * Puts a key / string value pair into the map.
     * @param key the key that is targeted.
     * @param value the string value that should be assigned to the key.
     * @param ts the timestamp of this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("setLWWString")
    fun putLWW(key: String, value: String?, ts: Timestamp): Map {
        val op = Map()
        op.lwwMap.merge(this.lwwMap.put(key, value, ts))
        return op
    }

    /**
     * Puts a key / Boolean value pair into the map.
     * @param key the key that is targeted.
     * @param value the Boolean value that should be assigned to the key.
     * @param ts the timestamp of this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("setMVBoolean")
    fun putMV(key: String, value: Boolean?, ts: Timestamp): Map {
        val op = Map()
        op.mvMap.merge(this.mvMap.put(key, value, ts))
        return op
    }

    /**
     * Puts a key / double value pair into the map.
     * @param key the key that is targeted.
     * @param value the double value that should be assigned to the key.
     * @param ts the timestamp of this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("setMVDouble")
    fun putMV(key: String, value: Double?, ts: Timestamp): Map {
        val op = Map()
        op.mvMap.merge(this.mvMap.put(key, value, ts))
        return op
    }

    /**
     * Puts a key / integer value pair into the map.
     * @param key the key that is targeted.
     * @param value the integer value that should be assigned to the key.
     * @param ts the timestamp of this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("setMVInt")
    fun putMV(key: String, value: Int?, ts: Timestamp): Map {
        val op = Map()
        op.mvMap.merge(this.mvMap.put(key, value, ts))
        return op
    }

    /**
     * Puts a key / string value pair into the map.
     * @param key the key that is targeted.
     * @param value the string value that should be assigned to the key.
     * @param ts the timestamp of this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("setMVString")
    fun putMV(key: String, value: String?, ts: Timestamp): Map {
        val op = Map()
        op.mvMap.merge(this.mvMap.put(key, value, ts))
        return op
    }

    fun increment(key: String, inc: Int, ts: Timestamp): Map {
        val op = Map()
        var cnt = this.cntMap[key]
        if (cnt == null) cnt = PNCounter()
        op.cntMap[key] = cnt.increment(inc, ts)
        this.cntMap[key] = cnt
        return op
    }

    fun decrement(key: String, dec: Int, ts: Timestamp): Map {
        val op = Map()
        var cnt = this.cntMap[key]
        if (cnt == null) cnt = PNCounter()
        op.cntMap[key] = cnt.decrement(dec, ts)
        this.cntMap[key] = cnt
        return op
    }

    /**
     * Deletes a given key / Boolean value pair if it is present in the map and has not yet been
     * deleted.
     * @param key the key that should be deleted.
     * @param ts the timestamp linked to this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("deleteLWWBoolean")
    fun deleteLWWBoolean(key: String, ts: Timestamp): Map {
        val op = Map()
        op.lwwMap.merge(this.lwwMap.deleteBoolean(key, ts))
        return op
    }

    /**
     * Deletes a given key / double value pair if it is present in the map and has not yet been
     * deleted.
     * @param key the key that should be deleted.
     * @param ts the timestamp linked to this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("deleteLWWDouble")
    fun deleteLWWDouble(key: String, ts: Timestamp): Map {
        val op = Map()
        op.lwwMap.merge(this.lwwMap.deleteDouble(key, ts))
        return op
    }

    /**
     * Deletes a given key / integer value pair if it is present in the map and has not yet been
     * deleted.
     * @param key the key that should be deleted.
     * @param ts the timestamp linked to this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("deleteLWWInt")
    fun deleteLWWInt(key: String, ts: Timestamp): Map {
        val op = Map()
        op.lwwMap.merge(this.lwwMap.deleteInt(key, ts))
        return op
    }

    /**
     * Deletes a given key / string value pair if it is present in the map and has not yet been
     * deleted.
     * @param key the key that should be deleted.
     * @param ts the timestamp linked to this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("deleteLWWString")
    fun deleteLWWString(key: String, ts: Timestamp): Map {
        val op = Map()
        op.lwwMap.merge(this.lwwMap.deleteString(key, ts))
        return op
    }

    /**
     * Deletes a given key / Boolean value pair if it is present in the map and has not yet been
     * deleted.
     * @param key the key that should be deleted.
     * @param ts the timestamp linked to this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("deleteMVBoolean")
    fun deleteMVBoolean(key: String, ts: Timestamp): Map {
        val op = Map()
        op.mvMap.merge(this.mvMap.deleteBoolean(key, ts))
        return op
    }

    /**
     * Deletes a given key / double value pair if it is present in the map and has not yet been
     * deleted.
     * @param key the key that should be deleted.
     * @param ts the timestamp linked to this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("deleteMVDouble")
    fun deleteMVDouble(key: String, ts: Timestamp): Map {
        val op = Map()
        op.mvMap.merge(this.mvMap.deleteDouble(key, ts))
        return op
    }

    /**
     * Deletes a given key / integer value pair if it is present in the map and has not yet been
     * deleted.
     * @param key the key that should be deleted.
     * @param ts the timestamp linked to this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("deleteMVInt")
    fun deleteMVInt(key: String, ts: Timestamp): Map {
        val op = Map()
        op.mvMap.merge(this.mvMap.deleteInt(key, ts))
        return op
    }

    /**
     * Deletes a given key / string value pair if it is present in the map and has not yet been
     * deleted.
     * @param key the key that should be deleted.
     * @param ts the timestamp linked to this operation.
     * @return the delta corresponding to this operation.
     */
    @Name("deleteMVString")
    fun deleteMVString(key: String, ts: Timestamp): Map {
        val op = Map()
        op.mvMap.merge(this.mvMap.deleteString(key, ts))
        return op
    }

    /**
     * Generates a delta of operations recorded and not already present in a given context.
     * @param vv the context used as starting point to generate the delta.
     * @return the corresponding delta of operations.
     */
    override fun generateDelta(vv: VersionVector): Map {
        val delta = Map()

        delta.lwwMap.merge(this.lwwMap.generateDelta(vv))
        delta.mvMap.merge(this.mvMap.generateDelta(vv))

        for ((key, cnt) in this.cntMap) {
            val deltaCnt = PNCounter()
            deltaCnt.merge(cnt.generateDelta(vv))
            delta.cntMap[key] = deltaCnt
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
        if (delta !is Map) throw IllegalArgumentException("Map unsupported merge argument")

        this.lwwMap.merge(delta.lwwMap)
        this.mvMap.merge(delta.mvMap)

        for ((key, cnt) in delta.cntMap) {
            var localCnt = this.cntMap[key]
            if (localCnt == null) localCnt = PNCounter()
            localCnt.merge(cnt)
            this.cntMap[key] = localCnt
        }
    }

    /**
     * Serializes this crdt map to a json string.
     * @return the resulted json string.
     */
    override fun toJson(): String {
        val jsonSerializer = JsonMapSerializer(serializer())
        return Json.encodeToString(jsonSerializer, this)
    }

    companion object {
        /**
         * Constant value for key fields' separator.
         */
        private const val SEPARATOR = "%"

        /**
         * Constant suffix value for key associated to a last writer wins value.
         */
        const val LWWREGISTER = SEPARATOR + "LWW"

        /**
         * Constant suffix value for key associated to a multi-value.
         */
        const val MVREGISTER = SEPARATOR + "MV"

        /**
         * Constant suffix value for key associated to a counter value.
         */
        const val PNCOUNTER = SEPARATOR + "CNT"

        /**
         * Get the type name for serialization.
         * @return the type as a string.
         */
        @Name("getType")
        fun getType(): String {
            return "Map"
        }

        /**
         * Deserializes a given json string in a crdt map.
         * @param json the given json string.
         * @return the resulted crdt map.
         */
        @Name("fromJson")
        fun fromJson(json: String): Map {
            val jsonSerializer = JsonMapSerializer(serializer())
            return Json.decodeFromString(jsonSerializer, json)
        }
    }
}

/**
* This class is a json transformer for Map, it allows the separation between data and metadata.
*/
class JsonMapSerializer(serializer: KSerializer<Map>) :
    JsonTransformingSerializer<Map>(serializer) {

    override fun transformSerialize(element: JsonElement): JsonElement {
        val values = mutableMapOf<String, JsonElement>()

        val lww = element.jsonObject["lwwMap"]!!.jsonObject
        val lwwEntries = mutableMapOf<String, JsonElement>()
        for ((key, entry) in lww["entries"]!!.jsonObject) {
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
            values[key + Map.LWWREGISTER] = value as JsonElement
            lwwEntries[key] = entry.jsonObject["second"]!!.jsonObject
        }
        val lwwMetadata = JsonObject(mapOf("entries" to JsonObject(lwwEntries.toMap())))

        val mv = element.jsonObject["mvMap"]!!.jsonObject
        val mvEntries = mutableMapOf<String, JsonElement>()
        val causalContext = mv["causalContext"]!!.jsonObject
        for ((key, entry) in mv["entries"]!!.jsonObject) {
            val value = mutableListOf<JsonElement>()
            val meta = mutableListOf<JsonElement>()
            for (tmpPair in entry.jsonArray) {
                when {
                    key.endsWith(MVMap.BOOLEAN) -> {
                        value.add(JsonPrimitive(tmpPair.jsonObject["first"]!!.jsonPrimitive.booleanOrNull) as JsonElement)
                    }
                    key.endsWith(MVMap.DOUBLE) -> {
                        value.add(JsonPrimitive(tmpPair.jsonObject["first"]!!.jsonPrimitive.doubleOrNull) as JsonElement)
                    }
                    key.endsWith(MVMap.INTEGER) -> {
                        value.add(JsonPrimitive(tmpPair.jsonObject["first"]!!.jsonPrimitive.intOrNull) as JsonElement)
                    }
                    else -> {
                        value.add(tmpPair.jsonObject["first"] as JsonElement)
                    }
                }
                meta.add(tmpPair.jsonObject["second"]!!.jsonObject)
            }
            values[key + Map.MVREGISTER] = JsonArray(value)
            mvEntries[key] = JsonArray(meta)
        }
        val mvMetadata = JsonObject(mapOf("entries" to JsonObject(mvEntries.toMap()), "causalContext" to causalContext))

        val cnt = element.jsonObject["cntMap"]!!.jsonObject
        val cntMetadata = mutableMapOf<String, JsonElement>()
        for ((key, meta) in cnt) {
            val incValue = meta.jsonObject["increment"]!!.jsonArray.filter { it.jsonObject.containsKey("first") }.sumBy { it.jsonObject["first"]!!.jsonPrimitive.int }
            val decValue = meta.jsonObject["decrement"]!!.jsonArray.filter { it.jsonObject.containsKey("first") }.sumBy { it.jsonObject["first"]!!.jsonPrimitive.int }
            cntMetadata[key] = meta
            values[key + Map.PNCOUNTER] = JsonPrimitive(incValue - decValue)
        }

        val metadata = JsonObject(mapOf("lwwMap" to lwwMetadata, "mvMap" to mvMetadata, "cntMap" to JsonObject(cntMetadata)))
        return JsonObject(mapOf("type" to JsonPrimitive("Map"), "metadata" to metadata).plus(values))
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        val metadata = element.jsonObject["metadata"]!!.jsonObject

        val lwwMetadata = metadata["lwwMap"]!!.jsonObject
        val lwwEntries = mutableMapOf<String, JsonElement>()
        for ((key, entry) in lwwMetadata["entries"]!!.jsonObject) {
            var value = element.jsonObject[key + Map.LWWREGISTER]!!.jsonPrimitive
            if (value !is JsonNull && !key.endsWith(LWWMap.STRING)) {
                value = JsonPrimitive(value.toString())
            }
            val tmpEntry = JsonObject(mapOf("first" to value as JsonElement, "second" to entry))
            lwwEntries[key] = tmpEntry
        }
        val lww = JsonObject(mapOf("entries" to JsonObject(lwwEntries)))

        val mvMetadata = metadata["mvMap"]!!.jsonObject
        val causalContext = mvMetadata["causalContext"]!!.jsonObject
        val mvEntries = mutableMapOf<String, JsonElement>()
        for ((key, meta) in mvMetadata["entries"]!!.jsonObject) {
            val values = element.jsonObject[key + Map.MVREGISTER]!!.jsonArray
            val tmpEntries = mutableListOf<JsonElement>()
            for ((idx, ts) in meta.jsonArray.withIndex()) {
                var value = values[idx]
                if (value !is JsonNull && !key.endsWith(MVMap.STRING)) {
                    value = JsonPrimitive(value.toString())
                }
                val tmpEntry = JsonObject(mapOf("first" to value, "second" to ts))
                tmpEntries.add(tmpEntry)
            }
            mvEntries[key] = JsonArray(tmpEntries)
        }
        val mv = JsonObject(mapOf("entries" to JsonObject(mvEntries), "causalContext" to causalContext))

        return JsonObject(mapOf("lwwMap" to lww, "mvMap" to mv, "cntMap" to metadata["cntMap"]!!.jsonObject))
    }
}
