package crdtlib.crdt

import crdtlib.utils.Timestamp
import crdtlib.utils.UnexpectedTypeException
import crdtlib.utils.VersionVector

class MVRegister<DataT> : DeltaCRDT<MVRegister<DataT>> {

    private val entries: MutableSet<Pair<DataT, VersionVector>>
    
    constructor() {
        this.entries = mutableSetOf()
    }

    constructor(value: DataT, ts: Timestamp) {
        this.entries = mutableSetOf()
        val vv = VersionVector()
        vv.addTS(ts)
        this.entries.add(Pair<DataT, VersionVector>(value, vv))
    }

    constructor(values: Set<DataT>, ts: Timestamp) {
        this.entries = mutableSetOf()
        val vv = VersionVector()
        vv.addTS(ts)
        for (value in values) {
            this.entries.add(Pair<DataT, VersionVector>(value, vv))
        }
    }

    fun get(): Set<DataT> {
        return this.entries.map { it.first }.toSet()
    }

    fun assign(value: DataT, ts: Timestamp): MVRegister<DataT> {
       return assign(setOf(value), ts)
    }

    fun assign(values: Set<DataT>, ts: Timestamp): MVRegister<DataT> {
        val op = MVRegister<DataT>()
        val newVv = VersionVector()
        for ((_, vv) in this.entries) {
            newVv.pointWiseMax(vv)
        }
        if (!newVv.includesTS(ts)) {
            newVv.addTS(ts)
            this.entries.clear()
            for (v in values) {
                this.entries.add(Pair<DataT, VersionVector>(v, newVv))
                op.entries.add(Pair<DataT, VersionVector>(v, newVv))
            }
        }
        return op
    }

    override fun generateDelta(vv: VersionVector): Delta<MVRegister<DataT>> {
        val delta = MVRegister<DataT>()
        for ((value, localVv) in this.entries) {
            if (!localVv.isSmaller(vv)) {
                delta.entries.add(Pair<DataT, VersionVector>(value, localVv))
            }
        }
        return delta
    }

    override fun merge(delta: Delta<MVRegister<DataT>>) {
        if (delta !is MVRegister) throw UnexpectedTypeException("MVRegister does not support merging with type:" + delta::class)

        val tmpEntries = mutableSetOf<Pair<DataT, VersionVector>>()
        for ((localVal, localVv) in this.entries) {
            for ((_, otherVv) in delta.entries) {
                if (!localVv.isSmaller(otherVv)) {
                    tmpEntries.add(Pair<DataT, VersionVector>(localVal, localVv))
                    println("LOOP1")
                }
            }
        }
        for ((otherVal, otherVv) in delta.entries) {
            for ((_, localVv) in this.entries) {
                if (!otherVv.isSmaller(localVv)) {
                    tmpEntries.add(Pair<DataT, VersionVector>(otherVal, otherVv))
                    println("LOOP2")
                }
            }
        }

        this.entries.clear()
        for ((value, vv) in tmpEntries) {
            this.entries.add(Pair<DataT, VersionVector>(value, vv))
        }
    }
}
