package crdtlib.crdt

interface UpdateOperation<CrdtT> : Delta<CrdtT> {
    fun exec(obj: CrdtT): Boolean
}

class EmptyDelta<CrdtT> : UpdateOperation<CrdtT> {
    override fun exec(obj: CrdtT): Boolean {
        return true
    }
}
