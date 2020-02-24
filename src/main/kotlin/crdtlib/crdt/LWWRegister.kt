package crdtlib.crdt

import crdtlib.utils.Timestamp
import crdtlib.utils.UnexpectedTypeException
import crdtlib.utils.VersionVector

/**
* This class is a delta-based CRDT last writer wins (LWW) register.
*/
class LWWRegister<DataT> : DeltaCRDT<LWWRegister<DataT>> {

    /**
    * The value stored in the register.
    */
    private var value: DataT

    /**
    * The timestamp associated to the value.
    */
    private var ts: Timestamp

    /**
    * Constructor creating a register initialized with a given value.
    * @param value the value to be put in the registered.
    * @param ts the timestamp associated with the value.
    */
    constructor(value: DataT, ts: Timestamp) {
        this.value = value
        this.ts = ts
    }

    /**
    * Constructor creating a copy of a given register.
    * @param other the register that should be copy.
    */
    constructor(other: LWWRegister<DataT>) {
        this.value = other.value
        this.ts = other.ts
    }

    /**
    * Gets the value currently stored in the register.
    * @return value stored in the register.
    **/
    fun get(): DataT {
        return value
    }

    /**
    * Assigns a given value to the register.
    * Assign is not effective if the associated timestamp is smaller (older) than the current one.
    * @param value the value that should be assigned.
    * @param ts the timestamp associated to the operation.
    * @return the delta corresponding to this operation.
    */
    fun assign(v: DataT, ts: Timestamp): Delta<LWWRegister<DataT>> {
        if (this.ts >= ts) return EmptyDelta<LWWRegister<DataT>>()
        this.ts = ts
        this.value = v
        return LWWRegister<DataT>(this)
    }

    /**
    * Generates a delta of operations recorded and not already present in a given context.
    * @param vv the context used as starting point to generate the delta.
    * @return the corresponding delta of operations.
    */
    override fun generateDelta(vv: VersionVector): Delta<LWWRegister<DataT>> {
        if (vv.includesTS(ts)) return EmptyDelta<LWWRegister<DataT>>()
        return LWWRegister<DataT>(this)
    }

    /**
    * Merges informations contained in a given delta into the local replica, the merge is unilateral
    * and only local replica is modified.
    * The foreign value is kept iff its associated timestamp is greater than the current one.
    * @param delta the delta that should be merge with the local replica.
    */
    override fun merge(delta: Delta<LWWRegister<DataT>>) {
        if (delta is EmptyDelta<LWWRegister<DataT>>) return
        if (delta !is LWWRegister<DataT>) throw UnexpectedTypeException("LWWRegister does not support merging with type: " + delta::class)

        if (this.ts < delta.ts) {
            this.value = delta.value
            this.ts = delta.ts
        }
    }
}
