package p2pclient.deltacrdt

import p2pclient.utils.Environment
import p2pclient.utils.Timestamp
import p2pclient.utils.VersionVector
import kotlin.reflect.KClass

interface MapDeltaCRDT<T> : DeltaCRDT<T> {
    fun get(key: String, type: KClass<*>): DeltaCRDT<*>?
    fun put(key: String, value: DeltaCRDT<*>, ts: Timestamp, vv: VersionVector)
    fun put(key: String, value: DeltaCRDT<*>, env: Environment)
    fun remove(key: String, type: KClass<*>, ts: Timestamp, vv: VersionVector)
    fun remove(key: String, type: KClass<*>, env: Environment)
}