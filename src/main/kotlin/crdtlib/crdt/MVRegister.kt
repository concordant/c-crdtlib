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
* This class is a delta-based CRDT multi-value register.
*/
class MVRegister<DataT> : DeltaCRDT<MVRegister<DataT>> {

    /**
    * A mutable set storing the different values with their associated timestamp.
    */
    private var entries: MutableSet<Pair<DataT, Timestamp>>

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
    * Merges information contained in a given delta into the local replica, the merge is unilateral
    * and only the local replica is modified.
    * A foreign (local) value is kept iff it is contained in the local (foreign) replica or its
    * associated timestamp is not included in the local (foreign) causal context.
    * @param delta the delta that should be merge with the local replica.
    */
    override fun merge(delta: Delta<MVRegister<DataT>>) {
        if (delta is EmptyDelta<MVRegister<DataT>>) return
        if (delta !is MVRegister) throw UnexpectedTypeException("MVRegister does not support merging with type: " + delta::class)

        val keptEntries = mutableSetOf<Pair<DataT, Timestamp>>()
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
}
