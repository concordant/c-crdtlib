package crdtlib.utils

import kotlin.math.absoluteValue

class VersionVector {

    private val entries: MutableMap<DCId, Int> = mutableMapOf<DCId, Int>()

    constructor() {
    }

    constructor(vv: VersionVector) {
        entries.putAll(vv.entries)
    }

    fun maxVal(): Int {
        return entries.values.maxBy { it.absoluteValue } ?: 0
    }

    fun addTS(ts: Timestamp) {
        val curCnt = entries.getOrElse(ts.id, { -1 })
        if(curCnt < ts.cnt)
            entries[ts.id] = ts.cnt
    }

    fun includesTS(ts: Timestamp): Boolean {
        val cnt = entries.getOrElse(ts.id, { -1 })
        return cnt >= ts.cnt
    }

    fun pointWiseMax(vv: VersionVector) {
        for((k, v) in vv.entries)
            if(entries.getOrElse(k, { 0 }) < v)
                entries.put(k, v)
    }

    fun copy(): VersionVector {
        return VersionVector(this)
    }
}
