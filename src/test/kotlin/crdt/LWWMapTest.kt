package crdtlib.test

import crdtlib.crdt.Delta
import crdtlib.crdt.LWWMap
import crdtlib.utils.DCId
import crdtlib.utils.SimpleEnvironment
import crdtlib.utils.VersionVector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LWWMapTest {
    @Test
    fun emptyGet() {
        val key = "key"
        val map = LWWMap()

        assertNull(map.get(key))
    }
    
    @Test
    fun putGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val key = "key"
        val value = "value"
        val map = LWWMap()

        map.put(key, value, ts)

        assertEquals(value, map.get(key))
    }
    
    @Test
    fun putDelGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val key = "key"
        val value = "value"
        val map = LWWMap()

        map.put(key, value, ts1)
        map.delete(key, ts2)

        assertNull(map.get(key))
    }
 
    @Test
    fun delGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        val key = "key"
        val map = LWWMap()

        map.delete(key, ts1)

        assertNull(map.get(key))
    }

    @Test
    fun putPutGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val key = "key"
        val val1 = "value1"
        val val2 = "value2"
        val map = LWWMap()

        map.put(key, val1, ts1)
        map.put(key, val2, ts2)

        assertEquals(val2, map.get(key))
    }

    @Test
    fun putPutDelGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        val key = "key"
        val val1 = "value1"
        val val2 = "value2"
        val map = LWWMap()

        map.put(key, val1, ts1)
        map.put(key, val2, ts2)
        map.delete(key, ts3)

        assertNull(map.get(key))
    }

    @Test
    fun put_MergeGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val key = "key"
        val value = "value"
        val map1 = LWWMap()
        val map2 = LWWMap()

        map1.put(key, value, ts)
        map1.merge(map2)
        map2.merge(map1)

        assertEquals(value, map1.get(key))
        assertEquals(value, map2.get(key))
    }

    @Test
    fun put_MergePutLWWGet() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val key = "key"
        val val1 = "value1"
        val val2 = "value2"
        val map1 = LWWMap()
        val map2 = LWWMap()

        map1.put(key, val1, ts1)
        map2.merge(map1)
        map2.put(key, val2, ts2)

        assertEquals(val2, map2.get(key))
    }

    @Test
    fun put_PutLWWMergeGet() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val key = "key"
        val val1 = "value1"
        val val2 = "value2"
        val map1 = LWWMap()
        val map2 = LWWMap()

        map1.put(key, val1, ts1)
        map2.put(key, val2, ts2)
        map2.merge(map1)

        assertEquals(val2, map2.get(key))
    }
    
    @Test
    fun putLWW_PutMergeGet() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val key = "key"
        val val1 = "value1"
        val val2 = "value2"
        val map1 = LWWMap()
        val map2 = LWWMap()

        map2.put(key, val2, ts1)
        map1.put(key, val1, ts2)
        map2.merge(map1)

        assertEquals(val1, map2.get(key))
    }

    @Test
    fun putLWWDel_PutMergeGet() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts3 = dc2.getNewTimestamp()
        val key = "key"
        val val1 = "value1"
        val val2 = "value2"
        val map1 = LWWMap()
        val map2 = LWWMap()

        map2.put(key, val2, ts1)
        map1.put(key, val1, ts2)
        map1.delete(key, ts3)

        map2.merge(map1)

        assertNull(map2.get(key))
    }

    @Test
    fun putDel_PutLWWMergeGet() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts3 = dc1.getNewTimestamp()
        val key = "key"
        val val1 = "value1"
        val val2 = "value2"
        val map1 = LWWMap()
        val map2 = LWWMap()

        map1.put(key, val1, ts1)
        map2.put(key, val2, ts2)
        map1.delete(key, ts3)
        map2.merge(map1)

        assertEquals(val2, map2.get(key))
    }

    @Test
    fun put_PutLWW_Merge1DelMerge2Get() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val id3 = DCId("dcid3")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val dc3 = SimpleEnvironment(id3)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        val key = "key"
        val val1 = "value1"
        val val2 = "value2"
        val map1 = LWWMap()
        val map2 = LWWMap()
        val map3 = LWWMap()

        map1.put(key, val1, ts1)
        map3.merge(map1)
        map2.put(key, val2, ts2)
        map3.delete(key, ts3)
        map3.merge(map2)

        assertEquals(val2, map3.get(key))
    }

    @Test
    fun putLWW_Put_Merge1DelMerge2Get() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val id3 = DCId("dcid3")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val dc3 = SimpleEnvironment(id3)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        val key = "key"
        val val1 = "value1"
        val val2 = "value2"
        val map1 = LWWMap()
        val map2 = LWWMap()
        val map3 = LWWMap()

        map2.put(key, val1, ts1)
        map1.put(key, val2, ts2)
        map3.merge(map1)
        map3.delete(key, ts3)
        map3.merge(map2)

        assertNull(map3.get(key))
    }

    @Test
    fun putOp() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val key = "key"
        val value = "value"
        val map1 = LWWMap()
        val map2 = LWWMap()

        val op = map1.put(key, value, ts)
        map1.merge(op)
        map2.merge(op)

        assertEquals(value, map1.get(key))
        assertEquals(value, map2.get(key))
    }

    @Test
    fun putDelOp() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val key = "key"
        val value = "value"
        val map1 = LWWMap()
        val map2 = LWWMap()

        val putOp = map1.put(key, value, ts1)
        val delOp = map1.delete(key, ts2)
        map1.merge(putOp)
        map1.merge(delOp)
        map2.merge(putOp)
        map2.merge(delOp)

        assertNull(map1.get(key))
        assertNull(map2.get(key))
    }

    @Test
    fun generateDelta() {
        val id1 = DCId("dcid1")
        val dc1 = SimpleEnvironment(id1)
        val ts1 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts2 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts2)
        val ts3 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts3)
        val ts4 = dc1.getNewTimestamp()
        val vv = VersionVector()
        vv.addTS(ts2)
        val key1 = "key1"
        val key2 = "key2"
        val key3 = "key3"
        val key4 = "key4"
        val value = "value"
        val map1 = LWWMap()
        val map2 = LWWMap()

        map1.put(key1, value, ts1)
        map1.put(key2, value, ts2)
        map1.put(key3, value, ts3)
        map1.put(key4, value, ts4)
        val delta = map1.generateDelta(vv)
        map2.merge(delta)

        assertNull(map2.get(key1))
        assertNull(map2.get(key2))
        assertEquals(value, map2.get(key3))
        assertEquals(value, map2.get(key4))
    }

    //TODO: make this test passing
    //@Test
    //fun generateDeltaWithDel() {
    //    val id1 = DCId("dcid1")
    //    val id2 = DCId("dcid2")
    //    val dc1 = SimpleEnvironment(id1)
    //    val dc2 = SimpleEnvironment(id2)
    //    val ts1 = dc1.getNewTimestamp()
    //    val ts2 = dc2.getNewTimestamp()
    //    dc2.updateStateTS(ts2)
    //    val ts3 = dc2.getNewTimestamp()
    //    dc2.updateStateTS(ts3)
    //    val ts4 = dc2.getNewTimestamp()
    //    dc2.updateStateTS(ts4)
    //    val ts5 = dc2.getNewTimestamp()
    //    val vv = VersionVector()
    //    vv.addTS(ts3)
    //    val key1 = "key1"
    //    val key2 = "key2"
    //    val key3 = "key3"
    //    val value = "value"
    //    val map1 = LWWMap()
    //    val map2 = LWWMap()

    //    map2.put(key1, value, ts1)
    //    map1.put(key1, value, ts2)
    //    map1.put(key2, value, ts3)
    //    map1.delete(key2, ts4)
    //    map1.put(key3, value, ts5)
    //    val delta = map1.generateDelta(vv)
    //    map2.merge(delta)

    //    assertNull(map2.get(key1))
    //    assertNull(map2.get(key2))
    //    assertEquals(value, map2.get(key3))
    //}
}
