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
import crdtlib.utils.UnexpectedTypeException
import crdtlib.utils.VersionVector
import kotlinx.serialization.*
import kotlinx.serialization.json.*

/**
* This class is a delta-based CRDT map implementing last writer wins (LWW) to resolve conflicts.
* It is serializable to JSON and respect the following schema:
* {
    "_type": "Map",
    "_metadata": {
        "entries": {
            // $key is a string
            (( "$key": Timestamp.toJson(), )*( "$key": Timestamp.toJson() ))?
        }
    }
    // $key is a string and $value can be Boolean, double, integer or string
    ( , "$key": "$value" )*
* }
*/
@Serializable
class Map : DeltaCRDT<Map> {

    /**
    * A LWW map storing key / value pairs that should be merged using LWW.
    */
    private val lwwMap: LWWMap = LWWMap()

    /**
    * A LWW map storing key / value pairs that should be merged using LWW.
    */
    private val mvMap: MVMap = MVMap()

    private val cntMap: MutableMap<String, PNCounter> = mutableMapOf() 

    /**
    * Default constructor.
    */
    constructor() {
    }

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
        return this.cntMap.get(key)?.get()
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
        var cnt = this.cntMap.get(key)
        if (cnt == null) cnt = PNCounter()
        op.cntMap.put(key, cnt.increment(inc, ts))
        this.cntMap.put(key, cnt)
        return op
    }

    fun decrement(key: String, dec: Int, ts: Timestamp): Map {
        val op = Map()
        var cnt = this.cntMap.get(key)
        if (cnt == null) cnt = PNCounter()
        op.cntMap.put(key, cnt.decrement(dec, ts))
        this.cntMap.put(key, cnt)
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
    override fun generateDeltaProtected(vv: VersionVector): Delta<Map> {
        var delta = Map()
        delta.lwwMap.merge(this.lwwMap.generateDelta(vv))
        delta.mvMap.merge(this.mvMap.generateDelta(vv))
        return delta
    }

    /**
    * Merges information contained in a given delta into the local replica, the merge is unilateral
    * and only the local replica is modified.
    * A foreign operation (i.e., put or delete) is applied iff last locally stored operation has a
    * smaller timestamp compared to the foreign one, or there is no local operation recorded.
    * @param delta the delta that should be merged with the local replica.
    */
    override fun mergeProtected(delta: Delta<Map>) {
        if (delta !is Map)
            throw UnexpectedTypeException("Map does not support merging with type: " + delta::class)

        this.lwwMap.merge(delta.lwwMap)
        this.mvMap.merge(delta.mvMap)
    }

    /**
    * Serializes this crdt map to a json string.
    * @return the resulted json string.
    */
    @Name("toJson")
    fun toJson(): String {
        val jsonSerializer = JsonMapSerializer(Map.serializer())
        return Json.stringify<Map>(jsonSerializer, this)
    }

    companion object {
        /**
        * Deserializes a given json string in a crdt map.
        * @param json the given json string.
        * @return the resulted crdt map.
        */
        @Name("fromJson")
        fun fromJson(json: String): Map {
            val jsonSerializer = JsonMapSerializer(Map.serializer())
            return Json.parse(jsonSerializer, json)
        }
    }
}

/**
* This class is a json transformer for Map, it allows the separation between data and metadata.
*/
class JsonMapSerializer(private val serializer: KSerializer<Map>) :
        JsonTransformingSerializer<Map>(serializer, "JsonMapSerializer") {

    override fun writeTransform(element: JsonElement): JsonElement {
        return element
    }

    override fun readTransform(element: JsonElement): JsonElement {
        return element
    }
}
