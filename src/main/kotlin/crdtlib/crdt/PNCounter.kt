package crdtlib.crdt

import crdtlib.utils.DCId
import crdtlib.utils.Timestamp
import crdtlib.utils.UnexpectedTypeException
import crdtlib.utils.VersionVector
import kotlinx.serialization.*
import kotlinx.serialization.json.*

/**
* This class is a delta-based CRDT pn-counter.
*/
@Serializable
class PNCounter : DeltaCRDT<PNCounter> {

    /**
    * A mutable map storing for each datacenter metadata relative to increment operations.
    */
    private val increment: MutableMap<DCId, Pair<Int, Timestamp>> = mutableMapOf<DCId, Pair<Int, Timestamp>>()

    /**
    * A mutable map storing for each datacenter metadata relative to decrement operations.
    */
    private val decrement: MutableMap<DCId, Pair<Int, Timestamp>> = mutableMapOf<DCId, Pair<Int, Timestamp>>()

    /**
    * Gets the value of the counter.
    * @return the value of the counter.
    */
    fun value(): Int {
        return this.increment.values.sumBy{ it.first } - this.decrement.values.sumBy{ it.first }
    }

    /**
    * Increments the counter by the given amount.
    * @param amount the value that should be added to the counter.
    * @return the delta corresponding to this operation.
    */
    fun increment(amount: Int, ts: Timestamp): PNCounter {
        val op = PNCounter()
        if (amount == 0) return op
        if (amount < 0) return this.decrement(-amount, ts)

        val count = this.increment.get(ts.id)?.first ?: 0
        this.increment.put(ts.id, Pair<Int, Timestamp>(count + amount, ts))
        op.increment.put(ts.id, Pair<Int, Timestamp>(count + amount, ts))
        return op
    }

    /**
    * Decrements the counter by the given amount.
    * @param amount the value that should be removed to the counter.
    * @return the delta corresponding to this operation.
    */
    fun decrement(amount: Int, ts: Timestamp): PNCounter {
        val op = PNCounter()
        if (amount == 0) return op
        if (amount < 0) return this.increment(-amount, ts)
      
        val count = this.decrement.get(ts.id)?.first ?: 0
        this.decrement.put(ts.id, Pair<Int, Timestamp>(count + amount, ts))
        op.decrement.put(ts.id, Pair<Int, Timestamp>(count + amount, ts))
        return op
    }

    /**
    * Generates a delta of operations recorded and not already present in a given context.
    * @param vv the context used as starting point to generate the delta.
    * @return the corresponding delta of operations.
    */
    override fun generateDelta(vv: VersionVector): Delta<PNCounter> {
        val delta = PNCounter()
        for ((id, meta) in increment) {
            if (!vv.includesTS(meta.second)) {
                delta.increment.put(id, Pair<Int, Timestamp>(meta.first, meta.second))
            }
        }
        for ((id, meta) in decrement) {
            if (!vv.includesTS(meta.second)) {
                delta.decrement.put(id, Pair<Int, Timestamp>(meta.first, meta.second))
            }
        }
        return delta
    }

    /**
    * Merges information contained in a given delta into the local replica, the merge is unilateral
    * and only the local replica is modified.
    * A foreign information (i.e., increment or decrement values) is applied if the last stored
    * operation w.r.t to a given datacenter is older than the foreign one, or no information is
    * present for this datacenter.
    * @param delta the delta that should be merge with the local replica.
    */
    override fun merge(delta: Delta<PNCounter>) {
        if (delta !is PNCounter) throw UnexpectedTypeException("PNCounter does not support merging with type:" + delta::class)

        for ((id, meta) in delta.increment) {
            val localMeta = this.increment.get(id)
            if (localMeta == null || localMeta.first < meta.first) {
                this.increment.put(id, Pair<Int, Timestamp>(meta.first, meta.second))
            }
        }
        for ((id, meta) in delta.decrement) {
            val localMeta = this.decrement.get(id)
            if (localMeta == null || localMeta.first < meta.first) {
                this.decrement.put(id, Pair<Int, Timestamp>(meta.first, meta.second))
            }
        }
    }

    /**
    * Serializes this crdt counter to a json string.
    * @return the resulted json string.
    */
    fun toJson(): String {
        val JSON = Json(JsonConfiguration.Stable)
        val jsonSerializer = JsonPNCounterSerializer(PNCounter.serializer())
        return JSON.stringify<PNCounter>(jsonSerializer, this)
    }

    companion object {
        /**
        * Deserializes a given json string in a crdt counter.
        * @param json the given json string.
        * @return the resulted crdt counter.
        */
        fun fromJson(json: String): PNCounter {
            val JSON = Json(JsonConfiguration.Stable)
            val jsonSerializer = JsonPNCounterSerializer(PNCounter.serializer())
            return JSON.parse(jsonSerializer, json)
        }
    }
}

/**
* This class is a json transformer for PNCounter, it allows the separation between data and metadata.
*/
class JsonPNCounterSerializer(private val serializer : KSerializer<PNCounter>) :
        JsonTransformingSerializer<PNCounter>(serializer, "JsonPNCounterSerializer") {

    override fun writeTransform(element: JsonElement): JsonElement {
        val incValue = element.jsonObject.getArray("increment").filter { it.jsonObject.containsKey("first") }.sumBy{ it.jsonObject.getPrimitive("first").int }
        val decValue = element.jsonObject.getArray("decrement").filter { it.jsonObject.containsKey("first") }.sumBy{ it.jsonObject.getPrimitive("first").int }
        return JsonObject(mapOf("_type" to JsonPrimitive("PNCounter"), "_metadata" to element, "value" to JsonPrimitive(incValue - decValue)))
    }

    override fun readTransform(element: JsonElement): JsonElement {
        return element.jsonObject.getObject("_metadata")
    }
}
