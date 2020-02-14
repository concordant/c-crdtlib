package crdtlib.crdt

import crdtlib.utils.Timestamp
import crdtlib.utils.VersionVector

class LWWRegister<DataT>(var value: DataT, var valueTs: Timestamp) : DeltaCRDT<LWWRegister<DataT>> {

    constructor(op: AssignOp<DataT>) : this(op.opValue, op.opTs)

    fun get(): DataT {
        return value
    }

    fun assign(v: DataT, ts: Timestamp): AssignOp<DataT> {
        doAssign(v, ts)
        return AssignOp(v, ts)
    }

    fun doAssign(v: DataT, ts: Timestamp): Boolean {
        if(this.valueTs.smallerThan(ts)) {
            this.valueTs = ts
            this.value = v
        }
        return true
    }

    class AssignOp<DataT>(internal val opValue: DataT, internal val opTs: Timestamp) : UpdateOperation<LWWRegister<DataT>> {
        override fun exec(obj: LWWRegister<DataT>): Boolean {
            return obj.doAssign(opValue, opTs)
        }
    }

    override fun generateDelta(vv: VersionVector): Delta<LWWRegister<DataT>> {
        if (vv.includesTS(valueTs))
            return EmptyDelta<LWWRegister<DataT>>()
        else
            return AssignOp(value, valueTs)
    }

    override fun merge(delta: Delta<LWWRegister<DataT>>) {
        if (delta is AssignOp<DataT>)
            delta.exec(this)
        else if (delta is LWWRegister<DataT>)
            this.doAssign(delta.value, delta.valueTs)
    }
}
