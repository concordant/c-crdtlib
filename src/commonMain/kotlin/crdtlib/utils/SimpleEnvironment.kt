package crdtlib.utils

/**
* This class represents a simple environment generating increasing monotonic timestamps.
*/
class SimpleEnvironment : Environment {

    /**
    * The datacenter unique identifier associated with this environment.
    */
    private val id: DCId

    /**
    * A version vector storing this environment causal context.
    */
    private var curState: VersionVector

    /**
    * The value associated with the last generated timestamp.
    * This value is initialized to 0 meaning that the first generated timestamp has the value 1.
    */
    private var lastTs: Int

    /**
    * Default constructor.
    */
    constructor(id: DCId) {
        this.id = id
        this.curState = VersionVector()
        this.lastTs = 0
    }

    /**
    * Generates a monotonically increasing timestamp.
    * @return the generated timestamp.
    */
    override fun getNewTimestampProtected(): Timestamp {
        lastTs = curState.maxVal() + 1
        return Timestamp(id, lastTs)
    }

    /**
    * Gets the current state associated with the environment.
    * @return the current state.
    */
    override fun getCurrentStateProtected(): VersionVector {
        return curState.copy()
    }

    /**
    * Updates the current state with the given timestamp.
    * @param ts the given timestamp.
    */
    override fun updateStateTSProtected(ts: Timestamp) {
        curState.addTS(ts)
    }

    /**
    * Updates the current state with the given version vector.
    * @param vv the given version vector.
    */
    override fun updateStateVVProtected(vv: VersionVector) {
        curState.pointWiseMax(vv)
    }
}
