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

import crdtlib.utils.Environment
import crdtlib.utils.Name
import crdtlib.utils.Timestamp
import crdtlib.utils.VersionVector

/**
* Interface for delta based CRDT
*/
abstract class DeltaCRDT {
    /**
     * The environment linked to this crdt.
     */
    protected lateinit var env: Environment

    /**
     * Setter for environment.
     *
     * Associate an environment to this; used for late initialization.
     * Should be removed in the future ; still needed for deserialization.
     * @param env the new environment reference
     */
    internal fun setEnv(env: Environment) {
        this.env = env
    }

    /**
     * Default delta constructor
     *
     * Create an empty CRDT.
     * A CRDT without associated environment should be considered
     * as a delta only:
     * It won't allow any updating operation
     * and may not support reading operations.
     */
    constructor()

    /**
     * Default CRDT constructor
     *
     * Create an empty CRDT, associated with the given environment.
     * @param env the current environment.
     * Passing null will create a CRDT with uninitialized environment
     * (aka delta) :
     * It won't allow any updating operation
     * and may not support reading operations.
     */
    constructor(env: Environment?) {
        if (env != null) this.env = env
    }

    /**
     * Convenience method to notify a read to current environment.
     *
     * Must be called on every read operation on this.
     * Call current environment onRead method.
     * Do nothing if environment is not initialized.
     */
    protected fun onRead() {
        if (this::env.isInitialized) this.env.onRead(this)
    }

    /**
     * Convenience method to notify a read to current environment.
     *
     * Must be called on every write operation on this.
     * Call current environment onWrite method.
     * Environment must be initialized.
     * @param delta the delta from this modification.
     */
    protected fun onWrite(delta: DeltaCRDT) {
        if (this::env.isInitialized) this.env.onWrite(this, delta)
    }

    /**
     * Convenience method to notify a merge to current environment.
     *
     * Must be called on every merge operation on this.
     * Call current environment [onMerge](Environment.onMerge) method.
     * Environment must be initialized.
     * @param delta the delta to be merged.
     * @param lastTs the foreign timestamp with greater value.
     */
    protected fun onMerge(delta: DeltaCRDT, lastTs: Timestamp?) {
        if (this::env.isInitialized) this.env.onMerge(this, delta, lastTs)
    }

    /**
     * Generates the delta from a given version vector by calling the protected abstract method.
     * @param vv the given version vector.
     * @return the delta from the version vector.
     */
    @Name("generateDelta")
    abstract fun generateDelta(vv: VersionVector): DeltaCRDT

    /**
     * Merges a given delta into this CRDT
     *
     * @param delta the delta to be merge.
     */
    @Name("merge")
    abstract fun merge(delta: DeltaCRDT)

    /**
     * Serializes this delta crdt to a json string.
     *
     * @return the resulted json string.
     */
    @Name("toJson")
    abstract fun toJson(): String

    companion object {
        /**
         * Get the type name for serialization.
         *
         * @return the type as a string.
         */
        @Name("getType")
        fun getType(): String {
            throw NotImplementedError("getType not implemented")
        }

        /**
         * Deserializes a given json string in the corresponding crdt type.
         *
         * @param json the given json string.
         * @return the resulted delta crdt.
         */
        @Name("fromJson")
        fun fromJson(json: String, env: Environment? = null): DeltaCRDT {
            val properJson = json.replace('\'', '"').replace("""\"""", """"""")
            val regex = """"type"\s*:\s*"(\w+)",""".toRegex()
            val matchResult = regex.find(properJson)
            val crdtType = matchResult?.groups?.get(1)?.value
            when (crdtType) {
                "PNCounter" -> {
                    return PNCounter.fromJson(properJson, env)
                }
                "BCounter" -> {
                    return BCounter.fromJson(properJson, env)
                }
                "LWWRegister" -> {
                    return LWWRegister.fromJson(properJson, env)
                }
                "MVRegister" -> {
                    return MVRegister.fromJson(properJson, env)
                }
                "Ratchet" -> {
                    return Ratchet.fromJson(properJson, env)
                }
                "RGA" -> {
                    return RGA.fromJson(properJson, env)
                }
                "LWWMap" -> {
                    return LWWMap.fromJson(properJson, env)
                }
                "MVMap" -> {
                    return MVMap.fromJson(properJson, env)
                }
                "Map" -> {
                    return Map.fromJson(properJson, env)
                }
                else -> {
                    throw IllegalArgumentException("DeltaCRDT cannot deserialize type: $crdtType")
                }
            }
        }
    }
}
