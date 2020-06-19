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

import crdtlib.utils.Timestamp
import crdtlib.utils.UnexpectedTypeException
import crdtlib.utils.VersionVector

/**
* This class is a delta-based CRDT map implementing last writer wins (LWW) to resolve conflicts.
*/
class LWWMap : DeltaCRDT<LWWMap> {

    /**
    * A mutable map storing metadata relative to each key.
    */
    private val entries: MutableMap<String, Pair<String?, Timestamp>>

    /**
    * A causal context summarizing executed operations.
    */
    private val causalContext: VersionVector

    /**
    * Default constructor creating a empty causal context.
    */
    constructor() {
        this.entries = mutableMapOf<String, Pair<String?, Timestamp>>()
        this.causalContext = VersionVector()
    }

    /**
    * Constructor initializing the causal context.
    */
    constructor(cc: VersionVector) {
        this.entries = mutableMapOf<String, Pair<String?, Timestamp>>()
        this.causalContext = cc
    }

    /**
    * Gets the value corresponding to a given key.
    * @param key the key that should be looked for.
    * @return the value associated to the key, or null if the key is not present in the map or last
    * operation is a delete.
    **/
    fun get(key: String): String? {
        return this.entries.get(key)?.first
    }

    /**
    * Puts a key value pair into the map.
    * @param key the key that is targeted.
    * @param value the value that should be assigned to the key.
    * @param ts the timestamp of this operation.
    * @return the delta corresponding to this operation.
    **/
    fun put(key: String, value: String?, ts: Timestamp): LWWMap {
        val op = LWWMap()
        val currentTs = this.entries.get(key)?.second
        if (currentTs == null || currentTs < ts) {
            this.entries.put(key, Pair<String?, Timestamp>(value, ts))
            op.entries.put(key, Pair<String?, Timestamp>(value, ts))
        }
        this.causalContext.addTS(ts)
        return op
    }

    /**
    * Deletes a given key if it is present in the map and has not yet been deleted.
    * @param key the key that should be deleted.
    * @param ts the timestamp linked to this operation.
    * @return the delta corresponding to this operation.
    **/
    fun delete(key: String, ts: Timestamp): LWWMap {
        return put(key, null, ts)
    }

    /**
    * Generates a delta of operations recorded and not already present in a given context.
    * @param vv the context used as starting point to generate the delta.
    * @return the corresponding delta of operations.
    */
    override fun generateDelta(vv: VersionVector): Delta<LWWMap> {
        var delta = LWWMap()
        for ((key, meta) in this.entries) {
            val value = meta.first
            val ts = meta.second
            if (!vv.includesTS(ts)) {
                delta.entries.put(key, Pair<String?, Timestamp>(value, ts))
            }
        }
        return delta
    }

    /**
    * Merges information contained in a given delta into the local replica, the merge is unilateral
    * and only the local replica is modified.
    * A foreign operation (i.e., put or delete) is applied iff last locally stored operation has a
    * smaller timestamp compared to the foreign one, or there is no local operation recorded.
    * @param delta the delta that should be merged with the local replica.
    */
    override fun merge(delta: Delta<LWWMap>) {
        if (delta !is LWWMap)
            throw UnexpectedTypeException("LWWMap does not support merging with type: " + delta::class)

        for ((key, meta) in delta.entries) {
            val value = meta.first
            val ts = meta.second
            val localTs = this.entries.get(key)?.second
            if (localTs == null || localTs < ts) {
                this.entries.put(key, Pair<String?, Timestamp>(value, ts))
            }
            this.causalContext.addTS(ts)
        }
    }

    /**
    * Creates a string containing the state of the map.
    * @return a string containing the state of the map.
    **/
    override fun toString(): String {
        var str = "LWWMap{\n"
        for ((key, pair) in this.entries) {
            str += "key:${key}, value:${pair.first}, ts:${pair.second}\n"
        }
        return str + "}\n"
    }
}
