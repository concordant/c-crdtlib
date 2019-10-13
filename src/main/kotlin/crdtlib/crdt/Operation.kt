package crdtlib.crdt


// TODO: define Operation as abstract and store Timestamp and Dependencies
interface Operation<DataT> {
    fun exec( obj: DataT): Boolean
}

interface ReadOperation<DataT, ResultT> {
    fun exec( obj: DataT): ResultT
}

interface Delta<DataT> {
    fun exec( obj: DataT): Boolean
}

class EmptyDelta<DataT> : Delta<DataT> {
    override fun exec( obj: DataT): Boolean {
        return true
    }

}