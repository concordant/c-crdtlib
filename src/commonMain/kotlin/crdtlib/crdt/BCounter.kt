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
import crdtlib.utils.VersionVector
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.math.absoluteValue

/**
* This class is a delta-based CRDT bounded-counter for invariant "greater or equal" or "lower or equal".
 * The initial value is the bound.
 * It is serializable to JSON and respect the following schema:
 * {
 *   "_type": "BCounter",
 *   "_metadata": {
 *       "type": $type, // $type is GEQ or LEQ
 *       "bound": $value // $value is an integer
 *       "rightsObtained": [
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
 *       "rightsConsumed": [
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
class BCounter(val type: BType, var identifier: Timestamp, var bound: Int, var ts: Timestamp, var initial: Int? = null) : DeltaCRDT<BCounter>() {

    init {
        if (initial != null) {
            if (type == BType.GEQ && initial!! > bound) {
                this.increment(initial!! - bound, ts)
            } else if (type == BType.LEQ && initial!! < bound) {
                this.decrement(bound - initial!!, ts)
            } else {
                throw IllegalArgumentException("The initial value break the invariant.")
            }
        }
    }

    /**
     * A mutable map of mutable map storing for each client metadata relative to rights obtained.
     * rightsObtained[i][i] represent the rights increments by replica i.
     * rightsObtained[i][j] with i!=j represent the rights transferred from replica i to replica j.
     * For a "greater or equal" bounded counter, this map is storing each client metadata relative to increment operations and transferred rights.
     * For a "lower or equal" bounded counter, this map is storing each client metadata relative to decrement operations and transferred rights.
     */
    @Required
    private val rightsObtained: MutableMap<ClientUId, MutableMap<ClientUId, Pair<Int, Timestamp>>> = mutableMapOf()

    /**
     * A mutable map storing for each client metadata relative to rights consumed.
     * rightsConsumed[i] represent the rights consumed by replica i.
     * For a "greater or equal" bounded counter, this map is storing each client metadata relative to decrement operations.
     * For a "lower or equal" bounded counter, this map is storing each client metadata relative to increment operations.
     */
    @Required
    private val rightsConsumed: MutableMap<ClientUId, Pair<Int, Timestamp>> = mutableMapOf()

    /**
     * Gets the sum of increments values minus the sum of decrements values.
     * @return the sum of values from increment minus the sum of values from decrement.
     */
    private fun getValue(): Int {
        var sum = 0
        for ((k, v) in this.rightsObtained) {
            sum += v[k]?.first ?: 0
        }
        return sum - this.rightsConsumed.values.sumBy { it.first }
    }

    /**
     * Gets the value of the counter.
     * @return the value of the counter.
     */
    @Name("get")
    fun get(): Int {
        return if (this.type == BType.GEQ) this.initial ?: this.bound + this.getValue() else this.initial ?: this.bound - this.getValue()
    }

    /**
     * Gets the local rights of the counter.
     * @return the local rights of the counter.
     */
    @Name("localRights")
    fun localRights(uid: ClientUId): Int {
        var rights = (this.initial ?: this.bound - this.bound).absoluteValue
        rights += this.rightsObtained[uid]?.get(uid)?.first ?: 0
        for (v in this.rightsObtained.values) {
            rights += v[uid]?.first ?: 0
        }
        rights -= (this.rightsObtained[uid]?.values?.sumBy { it.first } ?: 0)
        rights -= this.rightsConsumed[uid]?.first ?: 0
        return rights
    }

    /**
     * Increments the variable rightsObtained by the given amount.
     * @param amount the value that should be added to the variable rightsObtained.
     * @return the delta corresponding to this operation.
     */
    private fun incrementRights(amount: Int, ts: Timestamp): BCounter {
        val op = BCounter(this.type, this.identifier, this.bound, this.ts)
        if (amount == 0) return op
        if (amount < 0) return this.decrementRights(-amount, ts)

        val count = this.rightsObtained[ts.uid]?.get(ts.uid)?.first ?: 0
        if (Int.MAX_VALUE - count < amount - 1) {
            throw RuntimeException("BCounter has reached Int.MAX_VALUE")
        }

        if (this.rightsObtained[ts.uid] == null) {
            this.rightsObtained[ts.uid] = mutableMapOf(ts.uid to Pair(count + amount, ts))
        } else {
            this.rightsObtained[ts.uid]?.put(ts.uid, Pair(count + amount, ts))
        }

        op.rightsObtained[ts.uid] = mutableMapOf(ts.uid to Pair(count + amount, ts))
        return op
    }

    /**
     * Increments the counter by the given amount.
     * @param amount the value that should be added to the counter.
     * @return the delta corresponding to this operation.
     */
    @Name("increment")
    fun increment(amount: Int, ts: Timestamp): BCounter {
        return if (this.type == BType.GEQ) incrementRights(amount, ts) else decrementRights(amount, ts)
    }

    /**
     * Increments the variable rightsConsumed by the given amount.
     * @param amount the value that should be added to the variable rightsConsumed.
     * @return the delta corresponding to this operation.
     */
    private fun decrementRights(amount: Int, ts: Timestamp): BCounter {
        val op = BCounter(this.type, this.identifier, this.bound, this.ts)
        if (amount == 0) return op
        if (amount < 0) return this.incrementRights(-amount, ts)

        if (amount > this.localRights(ts.uid)) {
            throw IllegalArgumentException("BCounter has not enough amount")
        }

        val count = this.rightsConsumed[ts.uid]?.first ?: 0
        if (Int.MAX_VALUE - count < amount - 1) {
            throw RuntimeException("BCounter has reached Int.MAX_VALUE")
        }
        this.rightsConsumed[ts.uid] = Pair(count + amount, ts)
        op.rightsConsumed[ts.uid] = Pair(count + amount, ts)
        return op
    }

    /**
     * Decrements the counter by the given amount.
     * @param amount the value that should be removed to the counter.
     * @return the delta corresponding to this operation.
     */
    @Name("decrement")
    fun decrement(amount: Int, ts: Timestamp): BCounter {
        return if (this.type == BType.GEQ) decrementRights(amount, ts) else incrementRights(amount, ts)
    }

    /**
     * Transfers rights from the local replica to some other replica to.
     * @param amount the rights that should be transferred from the local replica to replica to.
     * @return true if the local replica have enough rights to transfers.
     */
    @Name("transfer")
    fun transfer(amount: Int, to: ClientUId, ts: Timestamp): Boolean {
        if (localRights(ts.uid) < amount) {
            return false
        }
        this.rightsObtained[ts.uid]?.put(to, Pair((this.rightsObtained[ts.uid]?.get(to)?.first ?: 0) + amount, ts))
        return true
    }

    /**
     * Generates a delta of operations recorded and not already present in a given context.
     * @param vv the context used as starting point to generate the delta.
     * @return the corresponding delta of operations.
     */
    override fun generateDelta(vv: VersionVector): BCounter {
        val delta = BCounter(this.type, this.identifier, this.bound, this.ts)
        for ((uid1, meta2) in this.rightsObtained) {
            for ((uid2, meta) in meta2) {
                if (!vv.contains(meta.second)) {
                    if (delta.rightsObtained[uid1] == null) {
                        delta.rightsObtained[uid1] = mutableMapOf(uid2 to Pair(meta.first, meta.second))
                    } else {
                        delta.rightsObtained[uid1]?.put(uid2, Pair(meta.first, meta.second))
                    }
                }
            }
        }
        for ((uid, meta) in this.rightsConsumed) {
            if (!vv.contains(meta.second)) {
                delta.rightsConsumed[uid] = Pair(meta.first, meta.second)
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
    override fun merge(delta: BCounter) {
        if (delta.type != this.type) throw IllegalArgumentException("Trying to merge BCounter with different type of invariant")
        if (this.identifier != delta.identifier) {
            if (this.identifier <delta.identifier) {
                this.identifier = delta.identifier
                this.bound = delta.bound
                this.ts = delta.ts
                this.initial = delta.initial
                this.rightsObtained.clear()
                this.rightsConsumed.clear()
                this.rightsObtained.putAll(delta.rightsObtained)
                this.rightsConsumed.putAll(delta.rightsConsumed)
            }
            return
        }

        for ((uid, meta2) in delta.rightsObtained) {
            for ((uid2, meta) in meta2) {
                if (this.rightsObtained[uid] == null) {
                    this.rightsObtained[uid] = mutableMapOf(uid2 to Pair(meta.first, meta.second))
                }
                val localMeta = this.rightsObtained[uid]?.get(uid2)
                if (localMeta == null || localMeta.first < meta.first) {
                    this.rightsObtained[uid]?.put(uid2, Pair(meta.first, meta.second))
                }
            }
        }
        for ((uid, meta) in delta.rightsConsumed) {
            val localMeta = this.rightsConsumed[uid]
            if (localMeta == null || localMeta.first < meta.first) {
                this.rightsConsumed[uid] = Pair(meta.first, meta.second)
            }
        }
    }

    /**
     * Serializes this crdt counter to a json string.
     * @return the resulted json string.
     */
    override fun toJson(): String {
        val jsonSerializer = JsonBCounterSerializer(BCounter.serializer())
        return Json.encodeToString(jsonSerializer, this)
    }

    /**
     * GEQ : Greater or equal -> lower bound
     * LEQ : Lower or equal -> upper bound
     */
    enum class BType {
        GEQ, LEQ
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
        val bound = element.jsonObject["bound"]!!.jsonPrimitive.int

        var incValue = 0
        val keys = element.jsonObject["rightsObtained"]!!.jsonArray
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
        val decValue = element.jsonObject["rightsConsumed"]!!.jsonArray.filter {
            it.jsonObject.containsKey("first")
        }.sumBy { it.jsonObject["first"]!!.jsonPrimitive.int }

        val value = if (element.jsonObject["type"]!!.jsonPrimitive.toString() == """"GEQ"""") bound + incValue - decValue else bound - incValue + decValue

        return JsonObject(
            mapOf(
                "_type" to JsonPrimitive("BCounter"),
                "_metadata" to element,
                "value" to JsonPrimitive(value)
            )
        )
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonObject["_metadata"]!!.jsonObject
    }
}
