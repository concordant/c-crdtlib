package crdtlib.crdt

import crdtlib.utils.Timestamp
import crdtlib.utils.VersionVector

class LWWRegister<DataT>( var value: DataT, var valueTs: Timestamp) {
    constructor( op: AssignOp<DataT>) : this( op.opValue, op.opTs)

    fun get(): ReadOperation<LWWRegister<DataT>,DataT> {
        return GetOp<DataT>()
    }

    fun doGet(): DataT {
        return value
    }

    fun assign( v : DataT, ts: Timestamp, vv: VersionVector): AssignOp<DataT> {
        return AssignOp( v, ts)
    }

    fun doAssign( v : DataT, ts: Timestamp): Boolean {
        if( valueTs.smallerThan(  ts)) {
            valueTs = ts
            value = v
        }
        return true
    }

    class AssignOp<DataT>( internal val opValue: DataT, internal val opTs:Timestamp): Operation<LWWRegister<DataT>>, Delta<LWWRegister<DataT>> {

        override fun exec( obj: LWWRegister<DataT>): Boolean {
            return obj.doAssign( opValue, opTs)
        }

    }
    class GetOp<DataT>(): ReadOperation<LWWRegister<DataT>,DataT> {
        override fun exec( obj: LWWRegister<DataT>): DataT {
            return obj.doGet()
        }

    }

    fun generateDelta( vv: VersionVector) : Delta<LWWRegister<DataT>> {
        if( vv.includesTS( valueTs))
            return EmptyDelta()
        else {
            return AssignOp( value, valueTs)
        }
    }

}

