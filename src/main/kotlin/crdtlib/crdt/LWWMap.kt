package crdtlib.crdt

import crdtlib.utils.Timestamp
import crdtlib.utils.VersionVector

class LWWMap : DeltaCRDT<LWWMap> {

    private val entries: MutableMap<String, Pair<String?, Timestamp>> = mutableMapOf<String, Pair<String?, Timestamp>>()

    fun get(key: String): String? {
        return this.entries.get(key)?.first
    }

    fun put(key: String, value: String, ts: Timestamp): PutOp {
        doPut(key, value, ts)
        return PutOp(key, value, ts)
    }

    fun delete(key: String, ts: Timestamp): DelOp {
        doDelete(key, ts)
        return DelOp(key, ts)
    }

    fun doPut(key: String, value: String, ts: Timestamp): Boolean {
        var entry = this.entries.get(key)
        if (entry == null || entry.second < ts) {
            this.entries.put(key, Pair<String?, Timestamp>(value, ts))
        }
        return true
    }

    fun doDelete(key: String, ts: Timestamp): Boolean {
        var entry = this.entries.get(key)
        if (entry != null && entry.second <= ts) {
            this.entries.put(key, Pair<String?, Timestamp>(null, entry.second))
        }
        return true
    }

    override fun generateDelta(vv: VersionVector): Delta<LWWMap> {
        var delta = LWWMap()
        for ((key, pair) in this.entries) {
            var value = pair.first
            var ts = pair.second
            if (!vv.includesTS(ts)) {
                delta.entries.put(key, Pair<String?, Timestamp>(value, ts))
            }
        }
        return delta
    }

    override fun merge(delta: Delta<LWWMap>) {
        if (delta is UpdateOperation<LWWMap>)
            delta.exec(this)
        else if (delta is LWWMap) {
            for ((key, value) in delta.entries) {
                val tmpVal = value.first
                if (tmpVal != null) {
                    doPut(key, tmpVal, value.second)
                }
                else
                    doDelete(key, value.second)
            }
        }
    }

    override fun toString(): String {
        var str = "LWWMap{\n"
        for ((key, pair) in this.entries) {
            str += "key:${key}, value:${pair.first}, ts:${pair.second}\n"
        }
        return str + "}\n"
    }

    class PutOp(val opKey: String, val opVal: String, val opTs: Timestamp) : UpdateOperation<LWWMap> {
        override fun exec(obj: LWWMap): Boolean {
            return obj.doPut(opKey, opVal, opTs)
        }
    }

    class DelOp(val opKey: String, val opTs: Timestamp) : UpdateOperation<LWWMap> {
        override fun exec(obj: LWWMap): Boolean {
            return obj.doDelete(opKey, opTs)
        }
    }
}
