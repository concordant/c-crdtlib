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

import crdtlib.utils.ClientUId
import crdtlib.utils.Json
import crdtlib.utils.Name
import crdtlib.utils.Timestamp
import crdtlib.utils.UnexpectedTypeException
import crdtlib.utils.VersionVector
import kotlinx.serialization.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
* This class is a delta-based CRDT bounded-counter greater or equal 0.
* It is serializable to JSON and respect the following schema:
* {
    "_type": "BCounter",
    "_metadata": {
        "increment": [
            (( ClientUId.toJson(), {
                "first": $value, // $value is an integer
                "second": Timestamp.toJson()
            }, )*( ClientUId.toJson(), {
                "first": $value, // $value is an integer
                "second": Timestamp.toJson()
            } ))?
        ],
        "decrement": [
            (( ClientUId.toJson(), {
                "first": $value, // $value is an integer
                "second": Timestamp.toJson()
            }, )*( ClientUId.toJson(), {
                "first": $value, // $value is an integer
                "second": Timestamp.toJson()
            } ))?
        ]
    },
    "value": $value // $value is an integer
* }
*/
@Serializable
class BCounter() : DeltaCRDT<BCounter>() {

    /**
    * A mutable map storing for each client metadata relative to increment operations.
    */
    @Required
    private val increment: MutableMap<ClientUId, MutableMap<ClientUId, Pair<Int, Timestamp>>> = mutableMapOf()

    /**
    * A mutable map storing for each client metadata relative to decrement operations.
    */
    @Required
    private val decrement: MutableMap<ClientUId, Pair<Int, Timestamp>> = mutableMapOf()

    /**
    * Gets the value of the counter.
    * @return the value of the counter.
    */
    @Name("get")
    fun get(): Int {
        var sum = 0
        for ((k, v) in this.increment) {
            sum += v[k]?.first ?: 0
        }
        return sum - this.decrement.values.sumBy { it.first }
    }

    @Name("localRights")
    fun localRights(uid: ClientUId): Int {
        var rights = this.increment[uid]?.get(uid)?.first ?: 0
        for ((k, v) in this.increment) {
            rights += v[uid]?.first ?: 0
        }
        rights -= (this.increment[uid]?.values?.sumBy { it.first } ?: 0)
        rights -= this.decrement[uid]?.first ?: 0
        return rights
    }

    /**
    * Increments the counter by the given amount.
    * @param amount the value that should be added to the counter.
    * @return the delta corresponding to this operation.
    */
    @Name("increment")
    fun increment(amount: Int, ts: Timestamp): BCounter {
        val op = BCounter()
        if (amount == 0) return op
        if (amount < 0) return this.decrement(-amount, ts)

        val count = this.increment[ts.uid]?.get(ts.uid)?.first ?: 0
        if (Int.MAX_VALUE - count < amount - 1) {
            throw RuntimeException("BCounter has reached Int.MAX_VALUE")
        }

        if (this.increment[ts.uid] == null) {
            this.increment[ts.uid] = mutableMapOf(ts.uid to Pair(count + amount, ts))
        } else {
            this.increment[ts.uid]?.put(ts.uid, Pair(count + amount, ts))
        }

        op.increment[ts.uid] = mutableMapOf(ts.uid to Pair(count + amount, ts))
        return op
    }

    /**
    * Decrements the counter by the given amount.
    * @param amount the value that should be removed to the counter.
    * @return the delta corresponding to this operation.
    */
    @Name("decrement")
    fun decrement(amount: Int, ts: Timestamp): BCounter {
        val op = BCounter()
        if (amount == 0) return op
        if (amount < 0) return this.increment(-amount, ts)

        if (amount > this.localRights(ts.uid)) {
            throw IllegalArgumentException("BCounter has not enough amount")
        }

        val count = this.decrement[ts.uid]?.first ?: 0
        if (Int.MAX_VALUE - count < amount - 1) {
            throw RuntimeException("BCounter has reached Int.MAX_VALUE")
        }
        this.decrement[ts.uid] = Pair(count + amount, ts)
        op.decrement[ts.uid] = Pair(count + amount, ts)
        return op
    }

    @Name("transfer")
    fun transfer(amount: Int, to: ClientUId, ts: Timestamp): Boolean {
        var b = localRights(ts.uid) >= amount
        this.increment[ts.uid]?.put(to, Pair((this.increment[ts.uid]?.get(to)?.first ?: 0) + amount, ts))
        return b
    }

    /**
    * Generates a delta of operations recorded and not already present in a given context.
    * @param vv the context used as starting point to generate the delta.
    * @return the corresponding delta of operations.
    */
    override fun generateDeltaProtected(vv: VersionVector): DeltaCRDT<BCounter> {
        val delta = BCounter()
        for ((uid, meta2) in increment) {
            for ((uid2, meta) in meta2) {
                if (!vv.contains(meta.second)) {
                    if (delta.increment[uid] == null) {
                        delta.increment[uid] = mutableMapOf(uid2 to Pair(meta.first, meta.second))
                    } else {
                        delta.increment[uid]?.put(uid2, Pair(meta.first, meta.second))
                    }
                }
            }
        }
        for ((uid, meta) in decrement) {
            if (!vv.contains(meta.second)) {
                delta.decrement[uid] = Pair(meta.first, meta.second)
            }
        }
        return delta
    }

    /**
    * Merges information contained in a given delta into the local replica, the merge is unilateral
    * and only the local replica is modified.
    * A foreign information (i.e., increment or decrement values) is applied if the last stored
    * operation w.r.t to a given client is older than the foreign one, or no information is
    * present for this client.
    * @param delta the delta that should be merge with the local replica.
    */
    override fun mergeProtected(delta: DeltaCRDT<BCounter>) {
        if (delta !is BCounter) throw UnexpectedTypeException("BCounter does not support merging with type:" + delta::class)

        for ((uid, meta2) in delta.increment) {
            for ((uid2, meta) in meta2) {
                if (this.increment[uid] == null) {
                    this.increment[uid] = mutableMapOf(uid2 to Pair(meta.first, meta.second))
                }
                val localMeta = this.increment[uid]?.get(uid2)
                if (localMeta == null || localMeta.first < meta.first) {
                    this.increment[uid]?.put(uid2, Pair(meta.first, meta.second))
                }
            }
        }
        for ((uid, meta) in delta.decrement) {
            val localMeta = this.decrement[uid]
            if (localMeta == null || localMeta.first < meta.first) {
                this.decrement[uid] = Pair(meta.first, meta.second)
            }
        }
    }

    /**
    * Serializes this crdt counter to a json string.
    * @return the resulted json string.
    */
    @Name("toJson")
    fun toJson(): String {
        val jsonSerializer = JsonBCounterSerializer(BCounter.serializer())
        return Json.encodeToString<BCounter>(jsonSerializer, this)
    }

    companion object {
        /**
        * Deserializes a given json string in a crdt counter.
        * @param json the given json string.
        * @return the resulted crdt counter.
        */
        @Name("fromJson")
        fun fromJson(json: String): BCounter {
            val jsonSerializer = JsonBCounterSerializer(BCounter.serializer())
            return Json.decodeFromString(jsonSerializer, json)
        }
    }
}

/**
* This class is a json transformer for BCounter, it allows the separation between data and metadata.
*/
class JsonBCounterSerializer(private val serializer: KSerializer<BCounter>) :
        JsonTransformingSerializer<BCounter>(serializer) {

    override fun transformSerialize(element: JsonElement): JsonElement {
        val incValue = element.jsonObject["increment"]!!.jsonArray.filter {
            it.jsonObject.containsKey("first")
        }.sumBy { it.jsonObject["first"]!!.jsonPrimitive.int }
        val decValue = element.jsonObject["decrement"]!!.jsonArray.filter {
            it.jsonObject.containsKey("first")
        }.sumBy { it.jsonObject["first"]!!.jsonPrimitive.int }
        return JsonObject(mapOf(
            "_type" to JsonPrimitive("BCounter"),
            "_metadata" to element,
            "value" to JsonPrimitive(incValue - decValue)))
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonObject["_metadata"]!!.jsonObject
    }
}
