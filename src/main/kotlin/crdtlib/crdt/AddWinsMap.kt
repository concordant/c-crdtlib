package crdtlib.crdt

import crdtlib.utils.Timestamp
import crdtlib.utils.UnexpectedTypeException
import crdtlib.utils.VersionVector

/**
* This class is a delta-based CRDT map implementing add-wins to resolve conflicts.
*/
class AddWinsMap : DeltaCRDT<AddWinsMap> {

    /**
    * A mutable map storing metadata relative to each key.
    */
    private val entries: MutableMap<String, Pair<String?, Timestamp>> = mutableMapOf<String, Pair<String?, Timestamp>>()

    /**
    * A causal context summarizing executed operations.
    */
    private val causalContext: VersionVector

    /**
    * Default constructor creating a empty causal context.
    */
    constructor() {
        this.causalContext = VersionVector()
    }

    /**
    * Constructor initializing the causal context.
    */
    constructor(cc: VersionVector) {
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
    * @param ts the timestamp linked to this operation.
    * @return the delta corresponding to this operation.
    **/
    fun put(key: String, value: String, ts: Timestamp): AddWinsMap {
        val putOp = AddWinsMap()
        if (!this.causalContext.includesTS(ts)) {
            this.entries.put(key, Pair<String?, Timestamp>(value, ts))
            this.causalContext.addTS(ts)
            putOp.entries.put(key, Pair<String?, Timestamp>(value, ts))
        }
        return putOp
    }

    /**
    * Deletes a given key if it is present in the map and has not yet been deleted.
    * @param key the key that should be deleted.
    * @param ts the timestamp linked to this operation.
    * @return the delta corresponding to this operation.
    **/
    fun delete(key: String, ts: Timestamp): AddWinsMap {
        val delOp = AddWinsMap(this.causalContext.copy())
        if (this.entries.contains(key) && !this.causalContext.includesTS(ts)) {
            this.entries.put(key, Pair<String?, Timestamp>(null, ts))
            this.causalContext.addTS(ts)
            delOp.entries.put(key, Pair<String?, Timestamp>(null, ts))
        }
        return delOp
    }

    /**
    * Generates a delta of operations recorded and not already present in a given context.
    * @param vv the context used as starting point to generate the delta.
    * @return the corresponding delta of operations.
    */
    override fun generateDelta(vv: VersionVector): Delta<AddWinsMap> {
        var delta = AddWinsMap()
        for ((key, pair) in this.entries) {
            val value = pair.first
            val ts = pair.second
            if (!vv.includesTS(ts)) {
                delta.entries.put(key, Pair<String?, Timestamp>(value, ts))
            }
        }
        return delta
    }

    /**
    * Merges informations contained in a given delta into the local replica, the merge is
    * unilateral and only local replica is modified.
    * A foreign 'PUT' operation is applied if:
    * -the key does not exists in the local replica or;
    * -last operation is a 'PUT' with a smaller timestamp compared to the foreign one or;
    * -last operation is a 'DEL' and the foreign timestamp is not included in the local causal
    * context.
    * A foreign 'DEL' operation is applied iff the local timestamp is included in the foreign
    * context.
    * @param delta the delta that should be merge with the local replica.
    */
    override fun merge(delta: Delta<AddWinsMap>) {
        if (delta !is AddWinsMap)
            throw UnexpectedTypeException("AddWins does not support merging with type: " + delta::class)

        for ((key, meta) in delta.entries) {
            val value = meta.first
            val ts = meta.second

            val localMeta = this.entries.get(key)
            val localValue = localMeta?.first
            val localTs = localMeta?.second

            var updateEntry = false
            if (value != null) {
                if (localTs == null) {
                    updateEntry = true
                } else if (localValue != null && localTs < ts) {
                    updateEntry = true
                } else if (localValue == null && !this.causalContext.includesTS(ts)) {
                    updateEntry = true
                }
            } else {
                if (localTs != null && delta.causalContext.includesTS(localTs)) {
                    updateEntry = true
                }
            }

            if (updateEntry) {
                this.entries.put(key, Pair<String?, Timestamp>(value, ts))
                this.causalContext.addTS(ts)
            }
        }
    }

    /**
    * Creates a string containing the state of the map.
    * @return a string containing the state of the map.
    **/
    override fun toString(): String {
        var str = "AddWinsMap{\n"
        for ((key, pair) in this.entries) {
            str += "key:${key}, value:${pair.first}, ts:${pair.second}\n"
        }
        return str + "}\n"
    }
}
