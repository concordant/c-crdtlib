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
import kotlinx.serialization.json.*

/**
* This class is a generic delta-based CRDT last writer wins (LWW) register.
* A LWW register can only embed primitive types (i.e,, string, numbers, and boolean).
* It is serializable to JSON and respects the following schema:
* {
    "_type": "LWWRegister",
    "_metadata": Timestamp.toJson(),
    "value": T.toJson()
* }
* @property value the value stored in the register.
* @property value the timestamp associated to the value.
*/
@Serializable(with = LWWRegisterSerializer::class)
class LWWRegister<T : Any>(var value: T, var ts: Timestamp) : DeltaCRDT<LWWRegister<T>>() {

    init {
        if (this.value !is String && this.value !is Boolean && this.value !is Number) throw UnexpectedTypeException("LWWRegister does not support type: " + this.value::class)
    }

    /**
    * Constructor creating a copy of a given register.
    * @param other the register that should be copy.
    */
    constructor(other: LWWRegister<T>): this(other.value, other.ts) {
    }

    /**
    * Gets the value currently stored in the register.
    * @return value stored in the register.
    */
    @Name("get")
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
    @Name("set")
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
    override fun generateDeltaProtected(vv: VersionVector): Delta<LWWRegister<T>> {
        if (vv.includesTS(ts)) return EmptyDelta<LWWRegister<T>>()
        return LWWRegister<T>(this)
    }

    /**
    * Merges information contained in a given delta into the local replica, the merge is unilateral
    * and only the local replica is modified.
    * The foreign value wins iff its associated timestamp is greater than the current one.
    * @param delta the delta that should be merge with the local replica.
    */
    override fun mergeProtected(delta: Delta<LWWRegister<T>>) {
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
    @Name("toJson")
    fun toJson(): String {
        val jsonSerializer = JsonLWWRegisterSerializer(LWWRegister.serializer(this.value::class.serializer() as KSerializer<T>))
        return Json.stringify<LWWRegister<T>>(jsonSerializer, this)
    }

    companion object {
        /**
        * Deserializes a given json string in a crdt LWW register.
        * @param json the given json string.
        * @return the resulted LWW register.
        */
        @OptIn(ImplicitReflectionSerializer::class)
        @Name("fromJson")
        inline fun <reified T : Any> fromJson(json: String): LWWRegister<T> {
            val jsonSerializer = JsonLWWRegisterSerializer(LWWRegister.serializer(T::class.serializer()))
            return Json.parse(jsonSerializer, json)
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
