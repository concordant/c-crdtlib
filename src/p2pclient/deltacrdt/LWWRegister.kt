package p2pclient.deltacrdt

import p2pclient.utils.Timestamp
import p2pclient.utils.VersionVector
import utils.UnexpectedTypeException
import kotlin.reflect.KClass


class LWWRegister( var value: Any, var valueTs: Timestamp) : DeltaCRDT<LWWRegister> {
    constructor( state: LWWRegister) : this( state.value, state.valueTs)

    fun get(): Any {
         return value
    }

    fun assign( v : Any, ts: Timestamp) {
        if( valueTs.smallerThan(  ts)) {
            valueTs = ts
            value = v
        }
    }

    /**
     * Return this object, typed as the appropriate type.
     */
    override fun getCRDT(): LWWRegister {
        return this
    }

    /**
     * Merge state into this object
     */
    override fun getDelta ( vv : VersionVector) : DeltaDeltaCRDT<LWWRegister> {
        return FullStateDelta<LWWRegister>(this)
    }

    /**
     * Merge state into this object
     */
    override fun mergeState(state0: DeltaCRDT<*>) {
        if( state0 is DeltaCRDT<*>) {
            val state = state0.getCRDT()
            if( state is LWWRegister) {
                if (valueTs.smallerThan(state.valueTs)) {
                    valueTs = state.valueTs
                    value = state.value
                }
            } else
                throw UnexpectedTypeException( "Type not expected on merge of LWWRegister : expected " + value + " was "+ state)
        } else
            throw UnexpectedTypeException( "Type not expected on merge of LWWRegister : " + state0::class)
    }

    /**
     * Merge delta into this object
     */
    override fun mergeDelta(state0: DeltaDeltaCRDT<*>) {
        if( state0 is FullStateDelta<*>) {
            mergeState(state0.value)
        } else
            throw UnexpectedTypeException( "Type not expected on merge of LWWRegister : " + state0::class)
    }
}

