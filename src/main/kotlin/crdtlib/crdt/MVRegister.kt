package crdtlib.crdt

import crdtlib.utils.Timestamp
import crdtlib.utils.UnexpectedTypeException
import crdtlib.utils.VersionVector

/**
* This class is a delta-based CRDT multi-value register.
*/
class MVRegister<DataT> : DeltaCRDT<MVRegister<DataT>> {

    /**
    * A mutable set storing pairs of value and associated version vector.
    */
    private val entries: MutableSet<Pair<DataT, VersionVector>>

    /**
    * Default constructor creating a empty register.
    */
    constructor() {
        this.entries = mutableSetOf()
    }

    /**
    * Constructor creating a register initialized with a given value.
    * @param value the value to be put in the register.
    * @param ts the associated timestamp.
    */
    constructor(value: DataT, ts: Timestamp) {
        this.entries = mutableSetOf()
        val vv = VersionVector()
        vv.addTS(ts)
        this.entries.add(Pair<DataT, VersionVector>(value, vv))
    }

    /**
    * Constructor creating a copy of a given register.
    * @param other the register that should be copy.
    */
    constructor(other: MVRegister<DataT>) {
        this.entries = mutableSetOf()
        for ((value, vv) in other.entries) {
            this.entries.add(Pair<DataT, VersionVector>(value, vv))
        }
    }

    /**
    * Gets the set of values currently stored in the register.
    * @return the set of values stored.
    **/
    fun get(): Set<DataT> {
        return this.entries.map { it.first }.toSet()
    }

    /**
    * Assigns a given value to the register.
    * This value overload all others and the associated version vector is the point wise max of all
    * version vector. Assign is not effective if the associated timestamp is smaller (older) than
    * the one of an already registered local assign.
    * @param value the value that should be assigned.
    * @param ts the timestamp associated to the operation.
    * @return the delta corresponding to this operation.
    */
    fun assign(value: DataT, ts: Timestamp): MVRegister<DataT> {
        val op = MVRegister<DataT>()
        val newVv = VersionVector()
        for ((_, vv) in this.entries) {
            newVv.pointWiseMax(vv)
        }
        if (!newVv.includesTS(ts)) {
            newVv.addTS(ts)
            this.entries.clear()
            this.entries.add(Pair<DataT, VersionVector>(value, newVv))
            op.entries.add(Pair<DataT, VersionVector>(value, newVv))
        }
        return op
    }

    /**
    * Generates a delta of operations recorded and not already present in a given context.
    * @param vv the context used as starting point to generate the delta.
    * @return the corresponding delta of operations.
    */
    override fun generateDelta(vv: VersionVector): Delta<MVRegister<DataT>> {
        val delta = MVRegister<DataT>()
        for ((value, localVv) in this.entries) {
            if (!localVv.isSmaller(vv)) {
                delta.entries.add(Pair<DataT, VersionVector>(value, localVv))
            }
        }
        return delta
    }

    /**
    * Merges informations contained in a given delta into the local replica, the merge is unilateral
    * and only local replica is modified.
    * Values foreign (local) value is kept if its associated version vector is not smaller than any
    * local (foreign) stored version vector.
    * @param delta the delta that should be merge with the local replica.
    */
    override fun merge(delta: Delta<MVRegister<DataT>>) {
        if (delta !is MVRegister) throw UnexpectedTypeException("MVRegister does not support merging with type:" + delta::class)

        val tmpEntries = mutableSetOf<Pair<DataT, VersionVector>>()
        for ((localVal, localVv) in this.entries) {
            if (!delta.entries.map { it.second }.any { localVv.isSmaller(it) }) {
                tmpEntries.add(Pair<DataT, VersionVector>(localVal, localVv))
            }
        }
        for ((otherVal, otherVv) in delta.entries) {
            if (!this.entries.map { it.second }.any { otherVv.isSmaller(it) }) {
                tmpEntries.add(Pair<DataT, VersionVector>(otherVal, otherVv))
            }
        }

        this.entries.clear()
        for ((value, vv) in tmpEntries) {
            this.entries.add(Pair<DataT, VersionVector>(value, vv))
        }
    }
}
