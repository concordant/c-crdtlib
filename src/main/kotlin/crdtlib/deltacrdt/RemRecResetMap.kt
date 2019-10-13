package crdtlib.deltacrdt

import crdtlib.utils.Environment
import crdtlib.utils.Timestamp
import crdtlib.utils.VersionVector
import crdtlib.utils.UnexpectedTypeException
import kotlin.reflect.KClass


class RemRecResetMap : MapDeltaCRDT<RemRecResetMap> {
    private val entries: MutableMap<String,DeltaCRDT<*>> = mutableMapOf<String,DeltaCRDT<*>>()


    /**
     * Returns  true if the  given key exists
     */
    override fun containsKey(key: String, type: KClass<*>): Boolean {
        val pos = key.indexOf( '.')
        if( pos == -1) {
            val pos2 = key.indexOf( ':')
            if( pos2 > -1) {
                return entries.containsKey( key)
            } else {
                val fkey = key + ":" + type.simpleName
                return entries.containsKey( fkey)
            }
        } else {
            var fkey = key.substring( 0, pos)
            val pos2 = fkey.indexOf( ':')
            if( pos2 == -1)
                fkey = fkey + ":" + this::class.simpleName
            val el = entries[fkey] ?: return false
            if( el is MapDeltaCRDT)
                return el.containsKey( key.substring( pos + 1), type)
            else
                throw UnexpectedTypeException( "Expecting map CRDT in key of : " + el::class)
        }

    }

    /**
     * Returns the object associated with the given key.
     * If the object does not exist, return null
     */
    override fun get( key: String, type: KClass<*>) : DeltaCRDT<*>? {
        val pos = key.indexOf( '.')
        if( pos == -1) {
            val pos2 = key.indexOf( ':')
            if( pos2 > -1) {
                //TODO: check that the type matches the encoded key
                return entries[key]
            } else {
                val fkey = key + ":" + type.simpleName
                return entries[fkey]
            }
        } else {
            var fkey = key.substring( 0, pos)
            val pos2 = fkey.indexOf( ':')
            if( pos2 == -1)
               fkey = fkey + ":" + this::class.simpleName
            val el = entries[fkey] ?: return null
            if( el is MapDeltaCRDT)
                return el.get( key.substring( pos + 1), type)
            else
                throw UnexpectedTypeException( "Expecting map CRDT in key of : " + el::class)
        }
    }

    override fun put( key: String, value: DeltaCRDT<*>, ts: Timestamp, vv: VersionVector) {
        val pos = key.indexOf( '.')
        if( pos == -1) {
            val pos2 = key.indexOf( ':')
            if( pos2  < pos) {
                //TODO: check that the type matches the encoded key
                entries.put(key, value)
            } else {
                val fkey = key + ":" + value::class.simpleName
                entries.put(fkey, value)
            }
        } else {
            var fkey = key.substring( 0, pos)
            val pos2 = fkey.indexOf( ':')
            if( pos2 == -1)
                fkey = fkey + ":" + this::class.simpleName
            var el = entries.getOrPut(fkey) { RemRecResetMap()}
            if( el !is MapDeltaCRDT)
                throw UnexpectedTypeException( "Expecting map CRDT in key of : " + el::class)
            else
                el.put( key, value, ts, vv)
       }
    }


    // Methods to be called by applications
    override fun put( key: String, value: DeltaCRDT<*>, env: Environment) {
        val vv = env.getCurrentState()
        val ts = env.getNewTimestamp()
        put( key, value, ts, vv)
        env.updateStateTS( ts)
    }

    override fun remove( key: String, type: KClass<*>, ts: Timestamp, vv: VersionVector) {
        //   TO BE COMPLETED
    }


    // Methods to be called by applications
    override fun remove( key: String, type: KClass<*>, env: Environment) {
        val vv = env.getCurrentState()
        val ts = env.getNewTimestamp()
        remove( key, type, ts, vv)
        env.updateStateTS( ts)
    }



    /**
     * Return this object, typed as the appropriate type.
     */
    override fun getCRDT(): RemRecResetMap {
        return this
    }

    /**
     * Merge state into this object
     */
    override fun getDelta ( vv : VersionVector) : DeltaDeltaCRDT<RemRecResetMap> {
        return FullStateDelta<RemRecResetMap>(this)
    }

    /**
     * Merge state into this object
     */
    override fun mergeState(state0: DeltaCRDT<*>) {
        val state = state0.getCRDT()
        if( state is RemRecResetMap) {
            for (entR in state.entries) {
                val elL = entries[entR.key]
                if (elL == null)
                    entries[entR.key] = entR.value
                else {
                    elL.mergeState(entR.value)
/*                    throw UnexpectedTypeException( "Type not expected on merge of key  : " + entR.key +
                            " has local type " + elL::class +
                            " merging with remote type " + entR.value::class)
*/
                }
            }
        } else if( state == null)
            throw UnexpectedTypeException( "Type not expected on merge of RemRecResetMap : null ")
        else
            throw UnexpectedTypeException( "Type not expected on merge of RemRecResetMap : " + state::class)
    }

    /**
     * Merge state into this object
     */
    override fun mergeDelta(state0: DeltaDeltaCRDT<*>) {
        if( state0 is FullStateDelta<*>) {
            mergeState( state0.value)
        } else
            throw UnexpectedTypeException( "Type not expected on merge of RemRecResetMap : " + state0::class)
    }



}

