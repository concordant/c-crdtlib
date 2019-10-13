package crdtlib.deltacrdt

import crdtlib.utils.VersionVector

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
    fun mergeDelta( state0: DeltaDeltaCRDT<*>)

    /**
     * Merge state into this object
     */
//    fun mergeState( state: DeltaCRDT<T>)
    fun mergeState( state0: DeltaCRDT<*>)
}

interface DeltaDeltaCRDT<T>

data class FullStateDelta<T>( val value : DeltaCRDT<T>) : DeltaDeltaCRDT<T>