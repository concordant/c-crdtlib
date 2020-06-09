package crdtlib.crdt

import crdtlib.utils.Name
import crdtlib.utils.VersionVector

/**
 * Interface for delta based CRDT
 */
abstract class DeltaCRDT<CrdtT> : Delta<CrdtT> {

    /**
     * Protected abstract method generating the delta from a given version vector.
     * @param vv the given version vector.
     * @return the delta from the version vector.
     */
    protected abstract fun generateDeltaProtected(vv: VersionVector): Delta<CrdtT>

    /**
     * Protected abstract methods merging a given delta into this CRDT.
     * @param delta the delta to be merge.
     */
    protected abstract fun mergeProtected(delta: Delta<CrdtT>)

    /**
     * Generates the delta from a given version vector by calling the protected abstract method.
     * This trick is used to be able to force the method name in the generated javascript.
     * @param vv the given version vector.
     * @return the delta from the version vector.
     */
    @Name("generateDelta")
    fun generateDelta(vv: VersionVector): Delta<CrdtT> {
        return generateDeltaProtected(vv)
    }

    /**
     * Merges a given delta into this CRDT by calling the protected method.
     * This trick is used to be able to force the method name in the generated javascript.
     * @param delta the delta to be merge.
     */
    @Name("merge")
    fun merge(delta: Delta<CrdtT>) {
        mergeProtected(delta)
    }
}
