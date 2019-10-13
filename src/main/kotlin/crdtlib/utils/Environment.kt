package crdtlib.utils

interface Environment {
    /** Method for generating a monotonically increasing timestamp
     */
    fun getNewTimestamp(): Timestamp

    /** Method for return the current state
     */
    fun getCurrentState(): VersionVector

    /** Updates the current state with the given timestamp
     */
    fun updateStateTS(ts : Timestamp)

    /** Updates the current state with the given version vector
     */
    fun updateStateVV( vv : VersionVector)
}
