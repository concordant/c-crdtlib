package crdtlib.utils

/**
* This abstract class represents a contextual environment.
*/
abstract class Environment {

    /**
    * Protected abstract method generating a monotonically increasing timestamp.
    * @return the generated timestamp.
    */
    protected abstract fun getNewTimestampProtected(): Timestamp

    /**
    * Protected abstract method getting the current state associated with the environment.
    * @return the current state.
    */
    protected abstract fun getCurrentStateProtected(): VersionVector

    /**
    * Protected abstract method updating the current state with the given timestamp.
    * @param ts the given timestamp.
    */
    protected abstract fun updateStateTSProtected(ts: Timestamp)

    /**
    * Protected abstract method updating the current state with the given version vector.
    * @param vv the given version vector.
    */
    protected abstract fun updateStateVVProtected(vv: VersionVector)

    /**
    * Generates a monotonically increasing timestamp.
    * This trick is used to be able to force the method name in the generated javascript.
    * @return the generated timestamp.
    */
    @Name("getNewTimestamp")
    fun getNewTimestamp(): Timestamp {
        return getNewTimestampProtected()
    }

    /**
    * Gets the current state associated with the environment.
    * This trick is used to be able to force the method name in the generated javascript.
    * @return the current state.
    */
    @Name("getCurrentState")
    fun getCurrentState(): VersionVector {
        return getCurrentStateProtected()
    }

    /**
    * Updates the current state with the given timestamp.
    * This trick is used to be able to force the method name in the generated javascript.
    * @param ts the given timestamp.
    */
    @Name("updateStateTS")
    fun updateStateTS(ts: Timestamp) {
        return updateStateTSProtected(ts)
    }

    /**
    * Updates the current state with the given version vector.
    * This trick is used to be able to force the method name in the generated javascript.
    * @param vv the given version vector.
    */
    @Name("updateStateVV")
    fun updateStateVV(vv: VersionVector) {
        return updateStateVVProtected(vv)
    }
}
