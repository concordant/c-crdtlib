package crdtlib.utils

/**
* This interface represents a contextual environment.
**/
interface Environment {

    /**
    * Generates a monotonically increasing timestamp.
    **/
    fun getNewTimestamp(): Timestamp

    /**
    * Gets the current state associated with the environment.
    **/
    fun getCurrentState(): VersionVector

    /**
    * Updates the current state with the given timestamp.
    **/
    fun updateStateTS(ts: Timestamp)

    /**
    * Updates the current state with the given version vector.
    **/
    fun updateStateVV(vv: VersionVector)
}
