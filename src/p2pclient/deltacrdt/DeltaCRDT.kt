package p2pclient.deltacrdt

import p2pclient.utils.VersionVector

/**
 * Interface for state based CRDT
 */
interface DeltaCRDT<T> {
    /**
     * Return this object, typed as the appropriate type.
     */
    fun getCRDT() : T

    /**
     * Returns diff from vv
     */
    fun getDelta( vv: VersionVector) : DeltaDeltaCRDT<T>

    /**
     * Merge state into this object
     */
    fun mergeDelta( state: DeltaDeltaCRDT<*>)

    /**
     * Merge state into this object
     */
//    fun mergeState( state: DeltaCRDT<T>)
    fun mergeState( state: DeltaCRDT<*>)
}

interface DeltaDeltaCRDT<T>

data class FullStateDelta<T>( val value : DeltaCRDT<T>) : DeltaDeltaCRDT<T>