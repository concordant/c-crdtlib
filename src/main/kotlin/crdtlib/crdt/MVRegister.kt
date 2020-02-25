package crdtlib.crdt

import crdtlib.utils.Timestamp
import crdtlib.utils.UnexpectedTypeException
import crdtlib.utils.VersionVector

/**
* This class is a delta-based CRDT multi-value register.
*/
class MVRegister<DataT> : DeltaCRDT<MVRegister<DataT>> {

    /**
    * A mutable set storing the different values with their associated timestamp.
    */
    private val entries: MutableSet<Pair<DataT, Timestamp>>

    /**
    * A version vector summarizing the entries seen by all values.
    */
    private val causalContext: VersionVector

    /**
    * Default constructor creating a empty register.
    */
    constructor() {
        this.entries = mutableSetOf()
        this.causalContext = VersionVector()
    }

    /**
    * Constructor creating a register initialized with a given value.
    * @param value the value to be put in the register.
    * @param ts the associated timestamp.
    */
    constructor(value: DataT, ts: Timestamp) {
        this.entries = mutableSetOf(Pair<DataT, Timestamp>(value, ts))
        this.causalContext = VersionVector()
        this.causalContext.addTS(ts)
    }

    /**
    * Constructor creating a copy of a given register.
    * @param other the register that should be copy.
    */
    constructor(other: MVRegister<DataT>) {
        this.entries = mutableSetOf()
        for ((value, ts) in other.entries) {
            this.entries.add(Pair<DataT, Timestamp>(value, ts))
        }
        this.causalContext = VersionVector()
        this.causalContext.pointWiseMax(other.causalContext)
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
    * This value overload all others and the causal context is updated with the given timestamp.
    * Assign is not effective if the associated timestamp is already included in the causal context.
    * @param value the value that should be assigned.
    * @param ts the timestamp associated to the operation.
    * @return the delta corresponding to this operation.
    */
    fun assign(value: DataT, ts: Timestamp): Delta<MVRegister<DataT>> {
        if (this.causalContext.includesTS(ts)) return EmptyDelta<MVRegister<DataT>>()

        this.entries.clear()
        this.entries.add(Pair<DataT, Timestamp>(value, ts))
        this.causalContext.addTS(ts)

        return MVRegister(this)
    }

    /**
    * Generates a delta of operations recorded and not already present in a given context.
    * @param vv the context used as starting point to generate the delta.
    * @return the corresponding delta of operations.
    */
    override fun generateDelta(vv: VersionVector): Delta<MVRegister<DataT>> {
        if (this.causalContext.isSmallerOrEquals(vv)) return EmptyDelta<MVRegister<DataT>>()
        return MVRegister(this)
    }

    /**
    * Merges informations contained in a given delta into the local replica, the merge is unilateral
    * and only local replica is modified.
    * Only foreign value(s) are kept if delta's causal context is not smaller than the local one.
    * Foreign plus local values are kept if delta's causal context is concurrent to the local one.
    * @param delta the delta that should be merge with the local replica.
    */
    override fun merge(delta: Delta<MVRegister<DataT>>) {
        if (delta is EmptyDelta<MVRegister<DataT>>) return
        if (delta !is MVRegister) throw UnexpectedTypeException("MVRegister does not support merging with type:" + delta::class)
        if (delta.causalContext.isSmaller(this.causalContext)) return

        if (this.causalContext.isSmaller(delta.causalContext)) {
            this.entries.clear()
        }
        for ((value, ts) in delta.entries) {
            val sameDcValues = this.entries.filter { it.second.id == ts.id }
            if (sameDcValues.all { it.second < ts }) {
                sameDcValues.forEach { this.entries.remove(it) }
                this.entries.add(Pair<DataT, Timestamp>(value, ts))
            }
        }
        this.causalContext.pointWiseMax(delta.causalContext)
    }
}
