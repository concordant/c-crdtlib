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
import kotlin.reflect.KClass
import kotlinx.serialization.*
import kotlinx.serialization.builtins.*
import kotlinx.serialization.json.*

/**
* This class is a delta-based CRDT multi-value register.
* It is serializable to JSON and respect the following schema:
* {
    "_type": "MVRegister",
    "_metadata": VersionVector.toJson(),
    "value": [
        (( T.toJson(), )*( T.toJson() ))?
    ]
* }
*/
@Serializable(with = MVRegisterSerializer::class)
class MVRegister<T : Any> : DeltaCRDT<MVRegister<T>> {

    /**
    * A mutable set storing the different values with their associated timestamp.
    */
    var entries: MutableSet<Pair<T, Timestamp>> = mutableSetOf();

    /**
    * A version vector summarizing the entries seen by all values.
    */
    val causalContext: VersionVector = VersionVector();

    /**
    * Default constructor creating a empty register.
    */
    constructor() {
    }

    /**
    * Constructor creating a register initialized with a given value.
    * @param value the value to be put in the register.
    * @param ts the associated timestamp.
    */
    constructor(value: T, ts: Timestamp) {
        this.entries = mutableSetOf(Pair<T, Timestamp>(value, ts))
        this.causalContext.addTS(ts)
    }

    /**
    * Constructor creating a copy of a given register.
    * @param other the register that should be copy.
    */
    constructor(other: MVRegister<T>) {
        this.entries = other.entries.toMutableSet()
        this.causalContext.pointWiseMax(other.causalContext)
    }

    constructor(entries: Set<Pair<T, Timestamp>>, causalContext: VersionVector) {
        this.entries = entries.toMutableSet()
        this.causalContext.pointWiseMax(causalContext)
    }

    /**
    * Gets the set of values currently stored in the register.
    * @return the set of values stored.
    */
    @Name("get")
    fun get(): Set<T> {
        return this.entries.map { it.first }.toSet()
    }

    /**
    * Assigns a given value to the register.
    * This value overload all others and the causal context is updated with the given timestamp.
    * Assign is not effective if the associated timestamp is already included in the causal context.
    * @param value the value that should be assigned.
    * @param ts the timestamp associated to the operation.
    * @return the delta corresponding to this operation.
    */
    @Name("set")
    fun assign(value: T, ts: Timestamp): Delta<MVRegister<T>> {
        if (this.causalContext.includesTS(ts)) return EmptyDelta<MVRegister<T>>()

        this.entries.clear()
        this.entries.add(Pair<T, Timestamp>(value, ts))
        this.causalContext.addTS(ts)

        return MVRegister(this)
    }

    /**
    * Generates a delta of operations recorded and not already present in a given context.
    * @param vv the context used as starting point to generate the delta.
    * @return the corresponding delta of operations.
    */
    override fun generateDeltaProtected(vv: VersionVector): Delta<MVRegister<T>> {
        if (this.causalContext.isSmallerOrEquals(vv)) return EmptyDelta<MVRegister<T>>()
        return MVRegister(this)
    }

    /**
    * Merges information contained in a given delta into the local replica, the merge is unilateral
    * and only the local replica is modified.
    * A foreign (local) value is kept iff it is contained in the local (foreign) replica or its
    * associated timestamp is not included in the local (foreign) causal context.
    * @param delta the delta that should be merge with the local replica.
    */
    override fun mergeProtected(delta: Delta<MVRegister<T>>) {
        if (delta is EmptyDelta<MVRegister<T>>) return
        if (delta !is MVRegister) throw UnexpectedTypeException("MVRegister does not support merging with type: " + delta::class)

        val keptEntries = mutableSetOf<Pair<T, Timestamp>>()
        for ((value, ts) in this.entries) {
            if (!delta.causalContext.includesTS(ts) || delta.entries.any { it.second == ts }) {
                keptEntries.add(Pair(value, ts))
            }
        }
        for ((value, ts) in delta.entries) {
            if (!this.causalContext.includesTS(ts) || this.entries.any { it.second == ts }) {
                keptEntries.add(Pair(value, ts))
            }
        }

        this.entries = keptEntries
        this.causalContext.pointWiseMax(delta.causalContext)
    }

    /**
    * Serializes this crdt MV register to a json string.
    * @return the resulted json string.
    */
    @OptIn(ImplicitReflectionSerializer::class)
    @Name("toJson")
    fun toJson(kclass: KClass<T>): String {
        val jsonSerializer = JsonMVRegisterSerializer(MVRegister.serializer(kclass.serializer()))
        return Json.stringify<MVRegister<T>>(jsonSerializer, this)
    }

    companion object {
        /**
        * Deserializes a given json string in a crdt MV register.
        * @param json the given json string.
        * @return the resulted MV register.
        */
        @OptIn(ImplicitReflectionSerializer::class)
        @Name("fromJson")
        fun <T : Any> fromJson(kclass: KClass<T>, json: String): MVRegister<T> {
            val jsonSerializer = JsonMVRegisterSerializer(MVRegister.serializer(kclass.serializer()))
            return Json.parse(jsonSerializer, json)
        }
    }
}

/**
* This class is a serializer for generic MVRegister.
*/
@Serializer(forClass = MVRegister::class)
class MVRegisterSerializer<T : Any>(private val dataSerializer: KSerializer<T>) :
        KSerializer<MVRegister<T>> {

    override val descriptor: SerialDescriptor = SerialDescriptor("MVRegisterSerializer") {
        element("entries", SetSerializer(PairSerializer(dataSerializer, Timestamp.serializer())).descriptor)
        element("causalContext", VersionVector.serializer().descriptor)
    }

    override fun serialize(encoder: Encoder, value: MVRegister<T>) {
        val output = encoder.beginStructure(descriptor)
        output.encodeSerializableElement(descriptor, 0, SetSerializer(PairSerializer(dataSerializer, Timestamp.serializer())), value.entries)
        output.encodeSerializableElement(descriptor, 1, VersionVector.serializer(), value.causalContext)
        output.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): MVRegister<T> {
        val input = decoder.beginStructure(descriptor)
        lateinit var entries: Set<Pair<T, Timestamp>>
        lateinit var causalContext: VersionVector
        loop@ while (true) {
            when (val idx = input.decodeElementIndex(descriptor)) {
                CompositeDecoder.READ_DONE -> break@loop
                0 -> entries = input.decodeSerializableElement(descriptor, idx, SetSerializer(PairSerializer(dataSerializer, Timestamp.serializer())))
                1 -> causalContext = input.decodeSerializableElement(descriptor, idx, VersionVector.serializer())
                else -> throw SerializationException("Unknown index $idx")
            }
        }
        input.endStructure(descriptor)
        return MVRegister<T>(entries, causalContext)
    }
}

/**
* This class is a json transformer for MVRegister, it allows the separation between data and metadata.
*/
class JsonMVRegisterSerializer<T : Any>(private val serializer: KSerializer<MVRegister<T>>) :
        JsonTransformingSerializer<MVRegister<T>>(serializer, "JsonMVRegisterSerializer") {

    override fun writeTransform(element: JsonElement): JsonElement {
        val entries = mutableListOf<JsonElement>()
        val value = mutableListOf<JsonElement>()
        for (tmpPair in element.jsonObject.getArray("entries")) {
            value.add(tmpPair.jsonObject.get("first") as JsonElement)
            entries.add(tmpPair.jsonObject.getObject("second"))
        }
        val metadata = JsonObject(mapOf("entries" to JsonArray(entries), "causalContext" to element.jsonObject.getObject("causalContext")))
        return JsonObject(mapOf("_type" to JsonPrimitive("MVRegister"), "_metadata" to metadata, "value" to JsonArray(value)))
    }

    override fun readTransform(element: JsonElement): JsonElement {
        val entries = mutableListOf<JsonElement>()
        val metadata = element.jsonObject.getObject("_metadata")
        val value = element.jsonObject.getArray("value")
        var idxValue = 0
        for (tmpEntry in metadata.getArray("entries")) {
            entries.add(JsonObject(mapOf("first" to value[idxValue], "second" to tmpEntry)))
            idxValue++
        }
        val causalContext = metadata.getObject("causalContext")
        return JsonObject(mapOf("entries" to JsonArray(entries), "causalContext" to causalContext))
    }
}
