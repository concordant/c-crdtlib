package crdtlib.utils

import kotlin.math.absoluteValue

/**
* This class represents a version vector.
**/
class VersionVector {

    /**
    * A mutable map storing for each datacenter the greatest timestamp value seen until now.
    */
    private val entries: MutableMap<DCId, Int>

    /**
    * Default constructor.
    **/
    constructor() {
        this.entries = mutableMapOf<DCId, Int>()
    }

    /**
    * Copy constructor.
    **/
    constructor(vv: VersionVector) {
        this.entries = mutableMapOf<DCId, Int>()
        entries.putAll(vv.entries)
    }

    /**
    * Gets the maximal value stored in this version vector.
    * @return the maximal value.
    **/
    fun maxVal(): Int {
        return entries.values.maxBy { it.absoluteValue } ?: 0
    }

    /**
    * Adds a given timestamp to this version vector.
    * @param ts the given timestamp.
    **/
    fun addTS(ts: Timestamp) {
        val curCnt = entries.getOrElse(ts.id, { 0 })
        if(curCnt < ts.cnt)
            entries[ts.id] = ts.cnt
    }

    /**
    * Returns if a given timestamp is included in the version vector.
    * @param ts the given timestamp.
    * @return true if the timestamp is included in the version vector, false otherwise.
    **/
    fun includesTS(ts: Timestamp): Boolean {
        val cnt = entries.getOrElse(ts.id, { 0 })
        return cnt >= ts.cnt
    }

    /**
    * Updates this version vector with a given version vector by taking the maximum value for each
    * entry.
    * @param vv the given version vector used for update.
    **/
    fun pointWiseMax(vv: VersionVector) {
        for((k, v) in vv.entries)
            if(entries.getOrElse(k, { 0 }) < v)
                entries.put(k, v)
    }

    /**
    * Checks that this version vector is smaller or equals than a given version vector.
    * @param vv the given version vector used for comparison.
    * @return true if this version vector is smaller or equals than the other one, false otherwise.
    **/
    fun isSmallerOrEquals(vv: VersionVector): Boolean {
        for((k, v) in vv.entries) {
            val localV = entries.getOrElse(k, { 0 })
            if(localV > v) return false
        }

        for ((k, localV) in this.entries) {
            val v = vv.entries.getOrElse(k, { 0 })
            if(localV > v) return false
        }

        return true
    }

    /**
    * Copies this version vector.
    * @return a copy of this version vector.
    */
    fun copy(): VersionVector {
        return VersionVector(this)
    }
}
