package crdtlib.crdt

import crdtlib.utils.VersionVector

/**
 * Interface for delta based CRDT
 */
interface DeltaCRDT<CrdtT> : Delta<CrdtT> {
    /**
     * Returns diff from vv
     */
    fun generateDelta(vv: VersionVector): Delta<CrdtT>

    /**
     * Merge state into this object
     */
    fun merge(delta: Delta<CrdtT>)
}
