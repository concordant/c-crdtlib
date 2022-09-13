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

import crdtlib.utils.ClientUId
import crdtlib.utils.Environment
import crdtlib.utils.Json
import crdtlib.utils.Name
import crdtlib.utils.SimpleEnvironment
import crdtlib.utils.Timestamp
import crdtlib.utils.VersionVector
import kotlinx.serialization.*
import kotlinx.serialization.json.*

/**
 * A delta-based CRDT map providing multiple conflict resolution policies.
 *
 * On each key, a [MV entry](MVMap), a [LWW entry](LWWMap)
 * and a [PNCounter] entry can all be used independently.
 *
 * Its JSON serialization respects the following schema:
 * ```json
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
 * ```
 */
@Serializable
class Map : DeltaCRDT {

    /**
     * Proxy environment, for embedded CRDT maps
     *
     * Delegates tick() to env,
     * intercept hooks (onRead(), onWrite() and onMerge())
     * and allow to retrieve intermediate deltas.
     */
    private inner class ProxyEnv : SimpleEnvironment(ClientUId("Map Proxy Env")) {

        /**
         * Store the last delta and timestamp submitted via onMerge()
         */
        private var lastMerge: Pair<DeltaCRDT, Timestamp?>? = null

        /** Pop (return and delete) last (delta, ts) submitted via onMerge
         * Throws NullPointerException if last submitted delta
         * has already been pop()ed.
         * @return the last merge as a Pair(delta)
         */
        fun popMerge(): Pair<DeltaCRDT, Timestamp?> {
            val d = lastMerge
            lastMerge = null
            return d!!
        }

        override fun tick(): Timestamp {
            return env.tick()
        }

        override fun onRead(obj: DeltaCRDT) {
            onRead()
        }

        override fun onWrite(obj: DeltaCRDT, delta: DeltaCRDT) {
            val op = Map()
            when (obj) {
                is LWWMap -> op.lwwMap.merge(delta)
                is MVMap -> op.mvMap.merge(delta)
                is PNCounter -> {
                    val key: String? = cntToKey[obj]
                    if (key != null) {
                        val pn: PNCounter = delta as PNCounter
                        op.cntMap[key] = pn
                        op.cntToKey[pn] = key
                    } else {
                        throw RuntimeException("PNCounter not found.")
                    }
                }
            }
            onWrite(op)
        }

        override fun onMerge(obj: DeltaCRDT, delta: DeltaCRDT, lastTs: Timestamp?) {
            this.lastMerge = Pair(delta, lastTs)
        }
    }

    @Transient
    private val proxyEnv = ProxyEnv()

    /**
     * A LWW map storing key / value pairs that should be merged using LWW.
     */
    private val lwwMap = LWWMap(proxyEnv)

    /**
     * A LWW map storing key / value pairs that should be merged using LWW.
     */
    private val mvMap = MVMap(proxyEnv)

    @Required
    private val cntMap: MutableMap<String, PNCounter> = mutableMapOf()

    private val cntToKey: MutableMap<PNCounter, String> = mutableMapOf()

    /**
     * Default constructor.
     */
    constructor() : super()
    constructor(env: Environment) : super(env)

    override fun copy(): Map {
        val copy = Map(this.env)
        copy.lwwMap.merge(this.lwwMap.copy())
        copy.mvMap.merge(this.mvMap.copy())
        copy.cntMap.putAll(this.cntMap.toMutableMap())
        copy.cntToKey.putAll(this.cntToKey.toMutableMap())
        return copy
    }

    /**
     * Get the LWW Boolean value corresponding to a given [key] ,
     * or null if such a key is not present in the map.
     */
    @Name("getLWWBoolean")
    fun getLWWBoolean(key: String): Boolean? {
        onRead()
        return this.lwwMap.getBoolean(key)
    }

    /**
     * Get the LWW Double value corresponding to a given [key],
     * or null if such a key is not present in the map.
     */
    @Name("getLWWDouble")
    fun getLWWDouble(key: String): Double? {
        onRead()
        return this.lwwMap.getDouble(key)
    }

    /**
     * Get the LWW Int value corresponding to a given [key],
     * or null if such a key is not present in the map.
     */
    @Name("getLWWInt")
    fun getLWWInt(key: String): Int? {
        onRead()
        return this.lwwMap.getInt(key)
    }

    /**
     * Get the LWW String value corresponding to a given [key],
     * or null if such a key is not present in the map.
     */
    @Name("getLWWString")
    fun getLWWString(key: String): String? {
        onRead()
        return this.lwwMap.getString(key)
    }

    /**
     * Get the MV Boolean value corresponding to a given [key],
     * or null if such a key is not present in the map.
     */
    @Name("getMVBoolean")
    fun getMVBoolean(key: String): Set<Boolean?>? {
        onRead()
        return this.mvMap.getBoolean(key)
    }

    /**
     * Get the MV Double value corresponding to a given [key],
     * or null if such a key is not present in the map.
     */
    @Name("getMVDouble")
    fun getMVDouble(key: String): Set<Double?>? {
        onRead()
        return this.mvMap.getDouble(key)
    }

    /**
     * Get the MV Int value corresponding to a given [key],
     * or null if such a key is not present in the map.
     */
    @Name("getMVInt")
    fun getMVInt(key: String): Set<Int?>? {
        onRead()
        return this.mvMap.getInt(key)
    }

    /**
     * Get the MV String value corresponding to a given [key],
     * or null if such a key is not present in the map.
     */
    @Name("getMVString")
    fun getMVString(key: String): Set<String?>? {
        onRead()
        return this.mvMap.getString(key)
    }

    /**
     * Get the PNCounter value corresponding to a given [key],
     * or null if such a key is not present in the map.
     */
    @Name("getCntInt")
    fun getCntInt(key: String): Int? {
        onRead()
        return this.cntMap[key]?.get()
    }

    /**
     * Get an iterator over the LWW Boolean entries in the map.
     */
    @Name("iteratorLWWBoolean")
    fun iteratorLWWBoolean(): Iterator<Pair<String, Boolean>> {
        onRead()
        return this.lwwMap.iteratorBoolean()
    }

    /**
     * Get an iterator over the LWW Double entries in the map.
     */
    @Name("iteratorLWWDouble")
    fun iteratorLWWDouble(): Iterator<Pair<String, Double>> {
        onRead()
        return this.lwwMap.iteratorDouble()
    }

    /**
     * Get an iterator over the LWW Int entries in the map.
     */
    @Name("iteratorLWWInt")
    fun iteratorLWWInt(): Iterator<Pair<String, Int>> {
        onRead()
        return this.lwwMap.iteratorInt()
    }

    /**
     * Get an iterator over the LWW String entries in the map.
     */
    @Name("iteratorLWWString")
    fun iteratorLWWString(): Iterator<Pair<String, String>> {
        onRead()
        return this.lwwMap.iteratorString()
    }

    /**
     * Get an iterator over the MV Boolean entries in the map.
     */
    @Name("iteratorMVBoolean")
    fun iteratorMVBoolean(): Iterator<Pair<String, Set<Boolean?>>> {
        onRead()
        return this.mvMap.iteratorBoolean()
    }

    /**
     * Get an iterator over the MV Double entries in the map.
     */
    @Name("iteratorMVDouble")
    fun iteratorMVDouble(): Iterator<Pair<String, Set<Double?>>> {
        onRead()
        return this.mvMap.iteratorDouble()
    }

    /**
     * Get an iterator over the MV Int entries in the map.
     */
    @Name("iteratorMVInt")
    fun iteratorMVInt(): Iterator<Pair<String, Set<Int?>>> {
        onRead()
        return this.mvMap.iteratorInt()
    }

    /**
     * Get an iterator over the MV String entries in the map.
     */
    @Name("iteratorMVString")
    fun iteratorMVString(): Iterator<Pair<String, Set<String?>>> {
        onRead()
        return this.mvMap.iteratorString()
    }

    /**
     * Get an iterator over the PNCounter entries in the map.
     */
    @Name("iteratorCntInt")
    fun iteratorCntInt(): Iterator<Pair<String, Int>> {
        onRead()
        return this.cntMap.asSequence().map { (k, v) -> Pair(k, v.get()) }.iterator()
    }

    /**
     * Put a [key] / Boolean LWW [value] pair into the map.
     *
     * If [value] is null, the [key] is deleted.
     * @return the delta corresponding to this operation.
     */
    @Name("setLWWBoolean")
    fun putLWW(key: String, value: Boolean?) {
        this.lwwMap.put(key, value)
    }

    /**
     * Put a [key] / Double LWW [value] pair into the map.
     *
     * If [value] is null, the [key] is deleted.
     * @return the delta corresponding to this operation.
     */
    @Name("setLWWDouble")
    fun putLWW(key: String, value: Double?) {
        this.lwwMap.put(key, value)
    }

    /**
     * Put a [key] / Int LWW [value] pair into the map.
     *
     * If [value] is null, the [key] is deleted.
     * @return the delta corresponding to this operation.
     */
    @Name("setLWWInt")
    fun putLWW(key: String, value: Int?) {
        this.lwwMap.put(key, value)
    }

    /**
     * Put a [key] / String LWW [value] pair into the map.
     *
     * If [value] is null, the [key] is deleted.
     * @return the delta corresponding to this operation.
     */
    @Name("setLWWString")
    fun putLWW(key: String, value: String?) {
        this.lwwMap.put(key, value)
    }

    /**
     * Put a [key] / Boolean MV [value] pair into the map.
     *
     * If [value] is null, the [key] is deleted.
     * @return the delta corresponding to this operation.
     */
    @Name("setMVBoolean")
    fun putMV(key: String, value: Boolean?) {
        this.mvMap.put(key, value)
    }

    /**
     * Put a [key] / Double MV [value] pair into the map.
     *
     * If [value] is null, the [key] is deleted.
     * @return the delta corresponding to this operation.
     */
    @Name("setMVDouble")
    fun putMV(key: String, value: Double?) {
        this.mvMap.put(key, value)
    }

    /**
     * Put a [key] / Int MV [value] pair into the map.
     *
     * If [value] is null, the [key] is deleted.
     * @return the delta corresponding to this operation.
     */
    @Name("setMVInt")
    fun putMV(key: String, value: Int?) {
        this.mvMap.put(key, value)
    }

    /**
     * Put a [key] / String MV [value] pair into the map.
     *
     * If [value] is null, the [key] is deleted.
     * @return the delta corresponding to this operation.
     */
    @Name("setMVString")
    fun putMV(key: String, value: String?) {
        this.mvMap.put(key, value)
    }

    /**
     * Increment by [inc] the PNCounter
     * associated with the specified [key] in the map
     *
     * @return the delta corresponding to this operation.
     */
    fun increment(key: String, inc: Int) {
        val cnt = this.cntMap.getOrPut(key) { PNCounter(proxyEnv) }
        this.cntToKey[cnt] = key
        cnt.increment(inc)
    }

    /**
     * Decrement by [dec] the PNCounter
     * associated with the specified [key] in the map
     *
     * @return the delta corresponding to this operation.
     */
    fun decrement(key: String, dec: Int) {
        val cnt = this.cntMap.getOrPut(key) { PNCounter(proxyEnv) }
        this.cntToKey[cnt] = key
        cnt.decrement(dec)
    }

    /**
     * Removes the specified [key] / Boolean LWW value pair from this map.
     *
     * @return the delta corresponding to this operation.
     */
    @Name("deleteLWWBoolean")
    fun deleteLWWBoolean(key: String) {
        this.lwwMap.deleteBoolean(key)
    }

    /**
     * Removes the specified [key] / Double LWW value pair from this map.
     *
     * @return the delta corresponding to this operation.
     */
    @Name("deleteLWWDouble")
    fun deleteLWWDouble(key: String) {
        this.lwwMap.deleteDouble(key)
    }

    /**
     * Removes the specified [key] / Int LWW value pair from this map.
     *
     * @return the delta corresponding to this operation.
     */
    @Name("deleteLWWInt")
    fun deleteLWWInt(key: String) {
        this.lwwMap.deleteInt(key)
    }

    /**
     * Removes the specified [key] / String LWW value pair from this map.
     *
     * @return the delta corresponding to this operation.
     */
    @Name("deleteLWWString")
    fun deleteLWWString(key: String) {
        this.lwwMap.deleteString(key)
    }

    /**
     * Removes the specified [key] / Boolean MV value pair from this map.
     *
     * @return the delta corresponding to this operation.
     */
    @Name("deleteMVBoolean")
    fun deleteMVBoolean(key: String) {
        this.mvMap.deleteBoolean(key)
    }

    /**
     * Removes the specified [key] / Double MV value pair from this map.
     *
     * @return the delta corresponding to this operation.
     */
    @Name("deleteMVDouble")
    fun deleteMVDouble(key: String) {
        this.mvMap.deleteDouble(key)
    }

    /**
     * Removes the specified [key] / Int MV value pair from this map.
     *
     * @return the delta corresponding to this operation.
     */
    @Name("deleteMVInt")
    fun deleteMVInt(key: String) {
        this.mvMap.deleteInt(key)
    }

    /**
     * Removes the specified [key] / String MV value pair from this map.
     *
     * @return the delta corresponding to this operation.
     */
    @Name("deleteMVString")
    fun deleteMVString(key: String) {
        this.mvMap.deleteString(key)
    }

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

    override fun merge(delta: DeltaCRDT) {
        if (delta !is Map) throw IllegalArgumentException("Map unsupported merge argument")

        this.lwwMap.merge(delta.lwwMap)
        // compute max timestamp for onMerge()
        var lastTs = proxyEnv.popMerge().second

        this.mvMap.merge(delta.mvMap)
        val mvMapTs = proxyEnv.popMerge().second
        if (lastTs == null || (mvMapTs?.compareTo(lastTs) ?: 0) > 0) {
            lastTs = mvMapTs
        }

        for ((key, cnt) in delta.cntMap) {
            val localCnt = this.cntMap.getOrPut(key) { PNCounter(proxyEnv) }
            this.cntToKey[localCnt] = key
            localCnt.merge(cnt)
            val localCntTs = proxyEnv.popMerge().second
            if (lastTs == null || (localCntTs?.compareTo(lastTs) ?: 0) > 0) {
                lastTs = localCntTs
            }
            this.cntMap[key] = localCnt
        }
        onMerge(delta, lastTs)
    }

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
        fun fromJson(json: String, env: Environment? = null): Map {
            val jsonSerializer = JsonMapSerializer(serializer())
            val obj = Json.decodeFromString(jsonSerializer, json)
            obj.lwwMap.setEnv(obj.proxyEnv)
            obj.mvMap.setEnv(obj.proxyEnv)
            for ((_, cnt) in obj.cntMap) {
                cnt.setEnv(obj.proxyEnv)
            }
            if (env != null) {
                obj.env = env
            }
            return obj
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
