package crdtlib.crdt

import crdtlib.utils.Timestamp
import crdtlib.utils.VersionVector

/**
 * Simple map that should be enough for a non-fancy spreadsheet
 */
class LWWMap<KeyT,DataT> {
    private val entries: MutableMap<KeyT,LWWRegister<DataT?>> = mutableMapOf<KeyT,LWWRegister<DataT?>>()

    // Methods to be called by applications
    fun put( key: KeyT, value: DataT?, ts: Timestamp, vv: VersionVector): PutOp<KeyT,DataT> {
        val reg = entries.getOrElse( key, {LWWRegister<DataT?>( value, ts)})
        return PutOp( key, reg.assign(value, ts, vv))
    }

    fun delete( key: KeyT, ts: Timestamp, vv: VersionVector): PutOp<KeyT,DataT> {
        return put( key, null, ts, vv)
    }

    fun get( key: KeyT): ReadOperation<LWWMap<KeyT,DataT>,DataT?> {
        return GetOp<KeyT,DataT>( key)
    }

    // Methods to be called by the system
    fun doPut( key: KeyT, op: LWWRegister.AssignOp<DataT?>): Boolean {
        val reg = entries.getOrPut( key, {LWWRegister<DataT?>(op)})
        return op.exec(reg)
    }


    fun doGet(key: KeyT): DataT? {
        val reg = entries.get( key)
        if( reg == null)
            return null
        else {
            return reg.doGet()
        }
    }


    class PutOp<KeyT,DataT>( internal val opKey: KeyT, internal val op: LWWRegister.AssignOp<DataT?>): Operation<LWWMap<KeyT,DataT>> {
        override fun exec(obj: LWWMap<KeyT, DataT>): Boolean {
            return obj.doPut( opKey, op)
        }

    }

    class GetOp<KeyT,DataT>( internal val key:KeyT): ReadOperation<LWWMap<KeyT,DataT>,DataT?> {
        override fun exec( obj: LWWMap<KeyT,DataT>): DataT? {
            return obj.doGet(key)
        }

    }

}