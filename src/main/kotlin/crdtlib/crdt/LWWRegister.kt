package crdtlib.crdt

import crdtlib.utils.Timestamp
import crdtlib.utils.UnexpectedTypeException
import crdtlib.utils.VersionVector
import kotlin.reflect.KClass
import kotlinx.serialization.*
import kotlinx.serialization.json.*

/**
* This class is a delta-based CRDT last writer wins (LWW) register.
*/
@Serializable(with = LWWRegisterSerializer::class)
class LWWRegister<T : Any> : DeltaCRDT<LWWRegister<T>> {

    /**
    * The value stored in the register.
    */
    var value: T

    /**
    * The timestamp associated to the value.
    */
    var ts: Timestamp

    /**
    * Constructor creating a register initialized with a given value.
    * @param value the value to be put in the registered.
    * @param ts the timestamp associated with the value.
    */
    constructor(value: T, ts: Timestamp) {
        this.value = value
        this.ts = ts
    }

    /**
    * Constructor creating a copy of a given register.
    * @param other the register that should be copy.
    */
    constructor(other: LWWRegister<T>) {
        this.value = other.value
        this.ts = other.ts
    }

    /**
    * Gets the value currently stored in the register.
    * @return value stored in the register.
    */
    fun get(): T {
        return value
    }

    /**
    * Assigns a given value to the register.
    * Assign is not effective if the associated timestamp is smaller (older) than the current one.
    * @param value the value that should be assigned.
    * @param ts the timestamp associated to the operation.
    * @return the delta corresponding to this operation.
    */
    fun assign(v: T, ts: Timestamp): Delta<LWWRegister<T>> {
        if (this.ts >= ts) return EmptyDelta<LWWRegister<T>>()
        this.ts = ts
        this.value = v
        return LWWRegister<T>(this)
    }

    /**
    * Generates a delta of operations recorded and not already present in a given context.
    * @param vv the context used as starting point to generate the delta.
    * @return the corresponding delta of operations.
    */
    override fun generateDelta(vv: VersionVector): Delta<LWWRegister<T>> {
        if (vv.includesTS(ts)) return EmptyDelta<LWWRegister<T>>()
        return LWWRegister<T>(this)
    }

    /**
    * Merges information contained in a given delta into the local replica, the merge is unilateral
    * and only the local replica is modified.
    * The foreign value wins iff its associated timestamp is greater than the current one.
    * @param delta the delta that should be merge with the local replica.
    */
    override fun merge(delta: Delta<LWWRegister<T>>) {
        if (delta is EmptyDelta<LWWRegister<T>>) return
        if (delta !is LWWRegister<T>) throw UnexpectedTypeException("LWWRegister does not support merging with type: " + delta::class)

        if (this.ts < delta.ts) {
            this.value = delta.value
            this.ts = delta.ts
        }
    }

    /**
    * Serializes this crdt LWW register to a json string.
    * @return the resulted json string.
    */
    @OptIn(ImplicitReflectionSerializer::class)
    fun toJson(kclass: KClass<T>): String {
        val JSON = Json(JsonConfiguration.Stable)
        val jsonSerializer = JsonLWWRegisterSerializer(LWWRegister.serializer(kclass.serializer()))
        return JSON.stringify<LWWRegister<T>>(jsonSerializer, this)
    }

    companion object {
        /**
        * Deserializes a given json string in a crdt LWW register.
        * @param json the given json string.
        * @return the resulted LWW register.
        */
        @OptIn(ImplicitReflectionSerializer::class)
        fun <T : Any> fromJson(kclass: KClass<T>, json: String): LWWRegister<T> {
            val JSON = Json(JsonConfiguration.Stable)
            val jsonSerializer = JsonLWWRegisterSerializer(LWWRegister.serializer(kclass.serializer()))
            return JSON.parse(jsonSerializer, json)
        }
    }
}

/**
* This class is a serializer for generic LWWRegister.
*/
@Serializer(forClass = LWWRegister::class)
class LWWRegisterSerializer<T : Any>(private val dataSerializer: KSerializer<T>) :
        KSerializer<LWWRegister<T>> {

    override val descriptor: SerialDescriptor = SerialDescriptor("LWWRegisterSerializer") {
        element("value", dataSerializer.descriptor)
        element("ts", Timestamp.serializer().descriptor)
    }

    override fun serialize(encoder: Encoder, value: LWWRegister<T>) {
        val output = encoder.beginStructure(descriptor)
        output.encodeSerializableElement(descriptor, 0, dataSerializer, value.value)
        output.encodeSerializableElement(descriptor, 1, Timestamp.serializer(), value.ts)
        output.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): LWWRegister<T> {
        val input = decoder.beginStructure(descriptor)
        lateinit var value: T
        lateinit var ts: Timestamp
        loop@ while (true) {
            when (val idx = input.decodeElementIndex(descriptor)) {
                CompositeDecoder.READ_DONE -> break@loop
                0 -> value = input.decodeSerializableElement(descriptor, idx, dataSerializer)
                1 -> ts = input.decodeSerializableElement(descriptor, idx, Timestamp.serializer())
                else -> throw SerializationException("Unknown index $idx")
            }
        }
        input.endStructure(descriptor)
        return LWWRegister<T>(value, ts)
    }
}

/**
* This class is a json transformer for LWWRegister, it allows the separation between data and metadata.
*/
class JsonLWWRegisterSerializer<T : Any>(private val serializer: KSerializer<LWWRegister<T>>) :
        JsonTransformingSerializer<LWWRegister<T>>(serializer, "JsonLWWRegisterSerializer") {

    override fun writeTransform(element: JsonElement): JsonElement {
        val value = element.jsonObject.get("value") as JsonElement
        val metadata = element.jsonObject.getObject("ts")
        return JsonObject(mapOf("_type" to JsonPrimitive("LWWRegister"), "_metadata" to metadata, "value" to value))
    }

    override fun readTransform(element: JsonElement): JsonElement {
        val value = element.jsonObject.get("value") as JsonElement
        val ts = element.jsonObject.getObject("_metadata")
        return JsonObject(mapOf("value" to value, "ts" to ts))
    }
}
