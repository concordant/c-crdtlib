package crdtlib.utils

/**
* This class represents a simple environment generating increasing monotonic timestamps.
* @property id the datacenter unique identifier associated with this environment.
*/
class SimpleEnvironment(val id: DCId) : Environment {

    /**
    * A version vector storing this environment causal context.
    */
    private var curState = VersionVector()

    /**
    * The value associated with the last generated timestamp.
    * This value is initialized to 0 meaning that the first generated timestamp has the value 1.
    */
    private var lastTs: Int = 0

    /**
    * Generates a monotonically increasing timestamp.
    * @return the generated timestamp.
    */
    override fun getNewTimestamp(): Timestamp {
        lastTs = curState.maxVal() + 1
        return Timestamp(id, lastTs)
    }

    /**
    * Gets the current state associated with the environment.
    * @return the current state.
    */
    override fun getCurrentState(): VersionVector {
        return curState.copy()
    }

    /**
    * Updates the current state with the given timestamp.
    * @param ts the given timestamp.
    */
    override fun updateStateTS(ts: Timestamp) {
        curState.addTS(ts)
    }

    /**
    * Updates the current state with the given version vector.
    * @param vv the given version vector.
    */
    override fun updateStateVV(vv: VersionVector) {
        curState.pointWiseMax(vv)
    }
}
