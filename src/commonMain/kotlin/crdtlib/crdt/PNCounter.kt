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
import crdtlib.utils.Environment
import crdtlib.utils.Json
import crdtlib.utils.Name
import crdtlib.utils.Timestamp
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
 * A delta-based CRDT PN-counter.
 *
 * Initialized to 0, it can be concurrently incremented and decremented.
 *
 * For each replica, both sum of increments and decrements
 * performed on the replica are retained as grow-only counters.
 * The PN-Counter value is computed as the difference between
 * increments and decrements of all replicas.
 *
 * When merging, the maximum value of every increment and decrement counter
 * is retained (following grow-only semantic).
 *
 * Its JSON serialization respects the following schema:
 * ```json
 * {
 *   "type": "PNCounter",
 *   "metadata": {
 *       "increment": [
 *           (( ClientUId.toJson(), {
 *               "first": $value, // $value is an integer
 *               "second": Timestamp.toJson()
 *           }, )*( ClientUId.toJson(), {
 *               "first": $value, // $value is an integer
 *               "second": Timestamp.toJson()
 *           } ))?
 *       ],
 *       "decrement": [
 *           (( ClientUId.toJson(), {
 *               "first": $value, // $value is an integer
 *               "second": Timestamp.toJson()
 *           }, )*( ClientUId.toJson(), {
 *               "first": $value, // $value is an integer
 *               "second": Timestamp.toJson()
 *           } ))?
 *       ]
 *   },
 *   "value": $value // $value is an integer
 * }
 * ```
 */
@Serializable
class PNCounter : DeltaCRDT {

    /**
     * A mutable map storing for each client metadata relative to increment operations.
     */
    @Required
    private val increment: MutableMap<ClientUId, Pair<Int, Timestamp>> = mutableMapOf()

    /**
     * A mutable map storing for each client metadata relative to decrement operations.
     */
    @Required
    private val decrement: MutableMap<ClientUId, Pair<Int, Timestamp>> = mutableMapOf()

    /**
     * Default constructor.
     */
    constructor()
    constructor(env: Environment) : super(env)

    override fun copy(): PNCounter {
        val copy = PNCounter(this.env)
        copy.increment.putAll(increment.toMutableMap())
        copy.decrement.putAll(decrement.toMutableMap())
        return copy
    }

    /**
     * Gets the value of the counter.
     */
    @Name("get")
    fun get(): Int {
        onRead()
        return this.increment.values.sumBy { it.first } - this.decrement.values.sumBy { it.first }
    }

    /**
     * Increments the counter by the given [amount].
     *
     * @return the delta corresponding to this operation.
     */
    @Name("increment")
    fun increment(amount: Int): PNCounter {
        if (amount < 0) return this.decrement(-amount)
        val op = PNCounter()
        if (amount == 0) {
            onWrite(op)
            return op
        }

        val ts = env.tick()
        val count = this.increment[ts.uid]?.first ?: 0
        if (Int.MAX_VALUE - count < amount - 1) {
            throw RuntimeException("PNCounter has reached Int.MAX_VALUE")
        }
        op.increment[ts.uid] = Pair(count + amount, ts)
        onWrite(op)
        this.increment[ts.uid] = Pair(count + amount, ts)
        return op
    }

    /**
     * Decrements the counter by the given [amount].
     *
     * @return the delta corresponding to this operation.
     */
    @Name("decrement")
    fun decrement(amount: Int): PNCounter {
        if (amount < 0) return this.increment(-amount)
        val op = PNCounter()
        if (amount == 0) {
            onWrite(op)
            return op
        }

        val ts = env.tick()
        val count = this.decrement[ts.uid]?.first ?: 0
        if (Int.MAX_VALUE - count < amount - 1) {
            throw RuntimeException("PNCounter has reached Int.MAX_VALUE")
        }
        op.decrement[ts.uid] = Pair(count + amount, ts)
        onWrite(op)
        this.decrement[ts.uid] = Pair(count + amount, ts)
        return op
    }

    override fun generateDelta(vv: VersionVector): PNCounter {
        val delta = PNCounter()
        for ((uid, meta) in increment) {
            if (!vv.contains(meta.second)) {
                delta.increment[uid] = Pair(meta.first, meta.second)
            }
        }
        for ((uid, meta) in decrement) {
            if (!vv.contains(meta.second)) {
                delta.decrement[uid] = Pair(meta.first, meta.second)
            }
        }
        return delta
    }

    override fun merge(delta: DeltaCRDT) {
        if (delta !is PNCounter) throw IllegalArgumentException("PNCounter unsupported merge argument")

        var lastTs: Timestamp? = null
        for ((uid, meta) in delta.increment) {
            if (lastTs == null || lastTs < meta.second) {
                lastTs = meta.second
            }
            val localMeta = this.increment[uid]
            if (localMeta == null || localMeta.first < meta.first) {
                this.increment[uid] = Pair(meta.first, meta.second)
            }
        }
        for ((uid, meta) in delta.decrement) {
            if (lastTs == null || lastTs < meta.second) {
                lastTs = meta.second
            }
            val localMeta = this.decrement[uid]
            if (localMeta == null || localMeta.first < meta.first) {
                this.decrement[uid] = Pair(meta.first, meta.second)
            }
        }
        onMerge(delta, lastTs)
    }

    override fun toJson(): String {
        val jsonSerializer = JsonPNCounterSerializer(serializer())
        return Json.encodeToString(jsonSerializer, this)
    }

    companion object {
        /**
         * Get the type name for serialization.
         * @return the type as a string.
         */
        @Name("getType")
        fun getType(): String {
            return "PNCounter"
        }

        /**
         * Deserializes a given json string in a crdt counter.
         * @param json the given json string.
         * @return the resulted crdt counter.
         */
        @Name("fromJson")
        fun fromJson(json: String, env: Environment? = null): PNCounter {
            val jsonSerializer = JsonPNCounterSerializer(serializer())
            val obj = Json.decodeFromString(jsonSerializer, json)
            if (env != null) obj.env = env
            return obj
        }
    }
}

/**
* This class is a json transformer for PNCounter, it allows the separation between data and metadata.
*/
class JsonPNCounterSerializer(serializer: KSerializer<PNCounter>) :
    JsonTransformingSerializer<PNCounter>(serializer) {

    override fun transformSerialize(element: JsonElement): JsonElement {
        val incValue = element.jsonObject["increment"]!!.jsonArray.filter {
            it.jsonObject.containsKey("first")
        }.sumBy { it.jsonObject["first"]!!.jsonPrimitive.int }
        val decValue = element.jsonObject["decrement"]!!.jsonArray.filter {
            it.jsonObject.containsKey("first")
        }.sumBy { it.jsonObject["first"]!!.jsonPrimitive.int }
        return JsonObject(
            mapOf(
                "type" to JsonPrimitive("PNCounter"),
                "metadata" to element,
                "value" to JsonPrimitive(incValue - decValue)
            )
        )
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonObject["metadata"]!!.jsonObject
    }
}
