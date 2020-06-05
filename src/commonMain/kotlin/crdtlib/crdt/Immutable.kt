package crdtlib.crdt

/**
* This class store an immutable value
*/
class Immutable<DataT> {

    /**
    * The stored value.
    */
    private val value: DataT;

    /**
    * Constructor creating the immutable with a given value.
    * @param value the desired value.
    */
    constructor(value: DataT) {
        this.value = value
    }

    /**
    * Gets the value stored in the immutable.
    * @return the value stored in the immutable.
    */
    fun get(): DataT {
        return this.value
    }
}
