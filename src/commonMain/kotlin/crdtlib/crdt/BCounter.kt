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

import crdtlib.utils.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

/**
 * A delta-based CRDT bounded-counter (non-negative, initially 0).
 *
 * Following design from V. Balegas et al., "Extending
 * Eventually Consistent Cloud Databases for Enforcing Numeric Invariants,"
 * 2015 IEEE 34th Symposium on Reliable Distributed Systems (SRDS),
 * Montreal, QC, 2015, pp. 31-36, doi: 10.1109/SRDS.2015.32.
 *
 * Its JSON serialization respects the following schema:
 * ```json
 * {
 *   "type": "BCounter",
 *   "metadata": {
 *       "increment": [
 *          ({ClientUId.toJson()}, [
 *              (( ClientUId.toJson(), {
 *                  "first": $value, // $value is an integer
 *                  "second": Timestamp.toJson()
 *              }, )*( ClientUId.toJson(), {
 *                      "first": $value, // $value is an integer
 *                      "second": Timestamp.toJson()
 *              } ))?
 *          ])?
 *       ],
 *       "decrement": [
 *           ({ ClientUId.toJson(), {
 *               "first": $value, // $value is an integer
 *               "second": Timestamp.toJson()
 *           }, )*( ClientUId.toJson(), {
 *               "first": $value, // $value is an integer
 *               "second": Timestamp.toJson()
 *           } })?
 *       ]
 *   },
 *   "value": $value // $value is an integer
 * }
 * ```
 */
@Serializable
class BCounter : DeltaCRDT {

    /**
     * A two-level mutable map storing each client metadata
     * relative to increment operations:
     * - increment[i][i] represents the increments by replica i.
     * - increment[i][j] with i!=j represents the rights transferred
     *                   from replica i to replica j.
     */
    @Required
    private val increment: MutableMap<ClientUId, MutableMap<ClientUId, Pair<Int, Timestamp>>> = mutableMapOf()

    /**
     * A mutable map storing each client metadata
     * relative to decrement operations:
     * decrement[i] represent the rights consumed by replica i.
     * Invariant:
     * dec[i] ≤ inc[i][i] + 𝚺_{i≠j} inc[j][i] - 𝚺_{i≠j} inc[i][j]
     *  ( local increment + rights given to i  - rights given by i )
     */
    @Required
    private val decrement: MutableMap<ClientUId, Pair<Int, Timestamp>> = mutableMapOf()

    /**
     * Default constructor.
     */
    constructor() : super()
    constructor(env: Environment) : super(env)

    override fun copy(): BCounter {
        val copy = BCounter(this.env)
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
        return checkedSum(
            this.increment.asSequence().map {
                (k, v) ->
                v[k]?.first ?: 0
            },
            this.decrement.asSequence().map {
                (_, v) ->
                v.first
            }
        )
    }

    /**
     * Gets the local rights of the counter.
     */
    @Name("localRights")
    fun localRights(uid: ClientUId): Int {
        onRead()
        return checkedSum(
            this.increment.asSequence().map {
                (_, v) ->
                v[uid]?.first ?: 0
            } +
                (this.increment[uid]?.get(uid)?.first ?: 0),
            (
                this.increment[uid]?.asSequence()?.map {
                    (_, v) ->
                    v.first
                } ?: sequenceOf()
                ) +
                (this.decrement[uid]?.first ?: 0)
        )
    }

    /**
     * Increments the counter by the given [amount].
     *
     * Throw IllegalArgumentException if localRights is not sufficient
     * (with negative amount only; see [decrement]).
     * @return the delta corresponding to this operation.
     */
    @Name("increment")
    fun increment(amount: Int): BCounter {
        if (amount < 0) return this.decrement(-amount)
        val op = BCounter()
        if (amount == 0) {
            onWrite(op)
            return op
        }

        val ts = env.tick()
        val thisLine = this.increment.getOrPut(ts.uid) { mutableMapOf() }
        val count = checkedSum(thisLine[ts.uid]?.first ?: 0, amount)
        thisLine[ts.uid] = Pair(count, ts)

        op.increment[ts.uid] = mutableMapOf(ts.uid to Pair(count, ts))
        onWrite(op)
        return op
    }

    /**
     * Decrements the counter by the given [amount].
     *
     * A replica can not decrement by more than its [localRights].
     * Throw IllegalArgumentException if [localRights] are not sufficient.
     * @return the delta corresponding to this operation.
     */
    @Name("decrement")
    fun decrement(amount: Int): BCounter {
        if (amount < 0) return this.increment(-amount)
        val op = BCounter()
        if (amount == 0) {
            onWrite(op)
            return op
        }

        try {
            if (amount > this.localRights(env.uid)) {
                throw IllegalArgumentException("BCounter has not enough rights")
            }
        } catch (e: ArithmeticException) {
            // localRights overflowed, so it is larger than amount
        }
        val count = checkedSum(this.decrement[env.uid]?.first ?: 0, amount)
        val ts = env.tick()
        this.decrement[ts.uid] = Pair(count, ts)
        op.decrement[ts.uid] = Pair(count, ts)
        onWrite(op)
        return op
    }

    /**
     * Transfers a given [amount] of rights from the local replica
     * to some [other](to)
     *
     * Throw IllegalArgumentException if [localRights] are not sufficient.
     * @return the delta corresponding to this operation.
     */
    @Name("transfer")
    fun transfer(amount: Int, to: ClientUId): BCounter {
        try {
            if (amount > this.localRights(env.uid)) {
                throw IllegalArgumentException("BCounter has not enough rights")
            }
        } catch (e: ArithmeticException) {
            // localRights overflowed, so it is larger than amount
        }

        val op = BCounter()
        if (amount == 0) return op
        val thisLine = this.increment.getOrPut(env.uid) { mutableMapOf() }
        val rights = checkedSum(thisLine[to]?.first ?: 0, amount)

        val ts = env.tick()
        thisLine[to] = Pair(rights, ts)
        op.increment[env.uid] = mutableMapOf()
        op.increment[env.uid]?.put(to, Pair(rights, ts))
        onWrite(op)
        return op
    }

    override fun generateDelta(vv: VersionVector): BCounter {
        val delta = BCounter()
        for ((uid1, meta2) in this.increment) {
            for ((uid2, meta) in meta2) {
                if (!vv.contains(meta.second)) {
                    if (delta.increment[uid1] == null) {
                        delta.increment[uid1] = mutableMapOf(uid2 to Pair(meta.first, meta.second))
                    } else {
                        delta.increment[uid1]?.put(uid2, Pair(meta.first, meta.second))
                    }
                }
            }
        }
        for ((uid, meta) in this.decrement) {
            if (!vv.contains(meta.second)) {
                delta.decrement[uid] = Pair(meta.first, meta.second)
            }
        }
        return delta
    }

    override fun merge(delta: DeltaCRDT) {
        if (delta !is BCounter) throw IllegalArgumentException("BCounter unsupported merge argument")

        var lastTs: Timestamp? = null
        for ((uid, meta2) in delta.increment) {
            for ((uid2, meta) in meta2) {
                if (lastTs == null || lastTs < meta.second) {
                    lastTs = meta.second
                }
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
        val jsonSerializer = JsonBCounterSerializer(serializer())
        return Json.encodeToString(jsonSerializer, this)
    }

    companion object {
        /**
         * Get the type name for serialization.
         * @return the type as a string.
         */
        @Name("getType")
        fun getType(): String {
            return "BCounter"
        }

        /**
         * Deserializes a given json string in a crdt counter.
         * @param json the given json string.
         * @return the resulted crdt counter.
         */
        @Name("fromJson")
        fun fromJson(json: String, env: Environment? = null): BCounter {
            val jsonSerializer = JsonBCounterSerializer(serializer())
            val obj = Json.decodeFromString(jsonSerializer, json)
            if (env != null) obj.env = env
            return obj
        }
    }
}

/**
 * This class is a json transformer for BCounter, it allows the separation between data and metadata.
 */
class JsonBCounterSerializer(serializer: KSerializer<BCounter>) :
    JsonTransformingSerializer<BCounter>(serializer) {

    override fun transformSerialize(element: JsonElement): JsonElement {
        var incValue = 0
        val keys = element.jsonObject["increment"]!!.jsonArray
        for (i in 0 until keys.size / 2) {
            val id = keys[2 * i]
            val sec = keys[2 * i + 1].jsonArray

            for (j in 0 until sec.size / 2) {
                if (sec[2 * j] == id) {
                    incValue += sec[2 * j + 1].jsonObject["first"]?.jsonPrimitive?.int ?: 0
                    break
                }
            }
        }
        val decValue = element.jsonObject["decrement"]!!.jsonArray.filter {
            it.jsonObject.containsKey("first")
        }.sumBy { it.jsonObject["first"]!!.jsonPrimitive.int }

        return JsonObject(
            mapOf(
                "type" to JsonPrimitive("BCounter"),
                "metadata" to element,
                "value" to JsonPrimitive(incValue - decValue)
            )
        )
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonObject["metadata"]!!.jsonObject
    }
}
