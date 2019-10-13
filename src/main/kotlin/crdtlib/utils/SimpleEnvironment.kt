package crdtlib.utils

class SimpleEnvironment( val id: DCId) : Environment {
    private var curState = VersionVector()
    private var lastTs : Int = 0

    override fun getNewTimestamp(): Timestamp {
        lastTs = curState.maxVal() + 1
        return Timestamp( id, lastTs)
    }

    override fun getCurrentState(): VersionVector {
        return curState.copy()
    }

    override fun updateStateTS(ts: Timestamp) {
        curState.addTS( ts)
    }

    override fun updateStateVV(vv: VersionVector) {
        curState.pointWiseMax( vv)
    }

}