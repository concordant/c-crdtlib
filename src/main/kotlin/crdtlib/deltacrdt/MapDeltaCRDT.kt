package crdtlib.deltacrdt

import crdtlib.utils.Environment
import crdtlib.utils.Timestamp
import crdtlib.utils.VersionVector
import kotlin.reflect.KClass

interface MapDeltaCRDT<T> : DeltaCRDT<T> {
    fun containsKey(key: String, type: KClass<*>): Boolean
    fun get(key: String, type: KClass<*>): DeltaCRDT<*>?
    fun put(key: String, value: DeltaCRDT<*>, ts: Timestamp, vv: VersionVector)
    fun put(key: String, value: DeltaCRDT<*>, env: Environment)
    fun remove(key: String, type: KClass<*>, ts: Timestamp, vv: VersionVector)
    fun remove(key: String, type: KClass<*>, env: Environment)
}