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
* This class is a delta-based CRDT bounded-counter for invariant greater or equal to 0.
 * The initial value is 0.
 *
 * Following design from  V. Balegas et al., "Extending
 * Eventually Consistent Cloud Databases for Enforcing Numeric Invariants,"
 * 2015 IEEE 34th Symposium on Reliable Distributed Systems (SRDS),
 * Montreal, QC, 2015, pp. 31-36, doi: 10.1109/SRDS.2015.32.
 *
 * It is serializable to JSON and respect the following schema:
 * {
 *   "_type": "BCounter",
 *   "_metadata": {
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
 */
@Serializable
class BCounter : DeltaCRDT() {
    /**
     * A mutable map of mutable map storing each client metadata relative to increment operations.
     * increment[i][i] represent the increments by replica i.
     * increment[i][j] with i!=j represent the rights transferred from replica i to replica j.
     */
    @Required
    private val increment: MutableMap<ClientUId, MutableMap<ClientUId, Pair<Int, Timestamp>>> = mutableMapOf()

    /**
     * A mutable map storing each client metadata relative to decrement operations.
     * decrement[i] represent the rights consumed by replica i.
     * Invariant:
     * dec[i] ≤ inc[i][i] + 𝚺_{i≠j} inc[j][i] - 𝚺_{i≠j} inc[i][j]
     *  ( local increment + rights given to i  - rights given by i )
     */
    @Required
    private val decrement: MutableMap<ClientUId, Pair<Int, Timestamp>> = mutableMapOf()

    /**
     * Gets the value of the counter.
     * @return the value of the counter.
     */
    @Name("get")
    fun get(): Int {
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
     * @return the local rights of the counter.
     */
    @Name("localRights")
    fun localRights(uid: ClientUId): Int {
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
     * Increments the counter by the given amount.
     * @param amount the value that should be added to the counter.
     * @return the delta corresponding to this operation.
     */
    @Name("increment")
    fun increment(amount: Int, ts: Timestamp): BCounter {
        val op = BCounter()
        if (amount == 0) return op
        if (amount < 0) return this.decrement(-amount, ts)

        val thisLine = this.increment.getOrPut(ts.uid, { mutableMapOf() })
        val count = checkedSum(thisLine[ts.uid]?.first ?: 0, amount)
        thisLine[ts.uid] = Pair(count, ts)

        op.increment[ts.uid] = mutableMapOf(ts.uid to Pair(count, ts))
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

        try {
            if (amount > this.localRights(ts.uid)) {
                throw IllegalArgumentException("BCounter has not enough rights")
            }
        } catch (e: ArithmeticException) {
            // localRights overflowed, so it is larger than amount
        }
        val count = checkedSum(this.decrement[ts.uid]?.first ?: 0, amount)

        this.decrement[ts.uid] = Pair(count, ts)
        op.decrement[ts.uid] = Pair(count, ts)
        return op
    }

    /**
     * Transfers rights from the local replica to some other replica to.
     * @param amount the rights that should be transferred from the local replica to replica to.
     * @return the delta corresponding to this operation.
     */
    @Name("transfer")
    fun transfer(amount: Int, to: ClientUId, ts: Timestamp): BCounter {
        try {
            if (amount > this.localRights(ts.uid)) {
                throw IllegalArgumentException("BCounter has not enough rights")
            }
        } catch (e: ArithmeticException) {
            // localRights overflowed, so it is larger than amount
        }

        val op = BCounter()
        if (amount == 0) return op
        val rights = checkedSum(
            this.increment[ts.uid]?.get(to)?.first ?: 0,
            amount
        )

        this.increment[ts.uid]?.put(to, Pair(rights, ts))
        op.increment[ts.uid]?.put(to, Pair(rights, ts))
        return op
    }

    /**
     * Generates a delta of operations recorded and not already present in a given context.
     * @param vv the context used as starting point to generate the delta.
     * @return the corresponding delta of operations.
     */
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

    /**
     * Merges information contained in a given delta into the local replica, the merge is unilateral
     * and only the local replica is modified.
     * A foreign information (i.e., increment or decrement values) is applied if the last stored
     * operation w.r.t to a given client is older than the foreign one, or no information is
     * present for this client.
     * @param delta the delta that should be merge with the local replica.
     */
    override fun merge(delta: DeltaCRDT) {
        if (delta !is BCounter) throw IllegalArgumentException("BCounter unsupported merge argument")

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
    override fun toJson(): String {
        val jsonSerializer = JsonBCounterSerializer(serializer())
        return Json.encodeToString(jsonSerializer, this)
    }

    companion object {
        /**
         * Deserializes a given json string in a crdt counter.
         * @param json the given json string.
         * @return the resulted crdt counter.
         */
        @Name("fromJson")
        fun fromJson(json: String): BCounter {
            val jsonSerializer = JsonBCounterSerializer(serializer())
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
                "_type" to JsonPrimitive("BCounter"),
                "_metadata" to element,
                "value" to JsonPrimitive(incValue - decValue)
            )
        )
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonObject["_metadata"]!!.jsonObject
    }
}
