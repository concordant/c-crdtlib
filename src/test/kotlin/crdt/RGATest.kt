package crdtlib.test

import crdtlib.crdt.RGA
import crdtlib.utils.DCId
import crdtlib.utils.SimpleEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

/**
* Represents a suite test for RGA.
* TODO: comment code
**/
class RGATest {

    @Test
    fun createVal() {
        val rga = RGA()
        assertEquals(listOf(), rga.value())
    }

    @Test
    fun add0Add0Val() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga = RGA()

        rga.insertAt(0, 'B', ts1)
        rga.insertAt(0, 'A', ts2)

        assertEquals(listOf('A', 'B'), rga.value())
    }
    
    @Test
    fun add0Add1Val() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga = RGA()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(1, 'B', ts2)

        assertEquals(listOf('A', 'B'), rga.value())
    }

    @Test
    fun add0Rem0Val() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga = RGA()

        rga.insertAt(0, 'A', ts1)
        rga.removeAt(0, ts2)

        assertEquals(listOf(), rga.value())
    }

    @Test
    fun add0Add0Rem0Rem0Val() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        val rga = RGA()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(0, 'B', ts2)
        rga.removeAt(0, ts3)
        rga.removeAt(0, ts4)

        assertEquals(listOf(), rga.value())
    }

    @Test
    fun add0Add1Rem0Add1Val() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        val rga = RGA()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(1, 'B', ts2)
        rga.removeAt(0, ts3)
        rga.insertAt(1, 'C', ts4)

        assertEquals(listOf('B', 'C'), rga.value())
    }

    @Test
    fun add0Add1Rem1Add1Val() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        val rga = RGA()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(1, 'B', ts2)
        rga.removeAt(1, ts3)
        rga.insertAt(1, 'C', ts4)

        assertEquals(listOf('A', 'C'), rga.value())
    }

    @Test
    fun add0Add0_MergeVal() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, 'B', ts1)
        rga1.insertAt(0, 'A', ts2)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B'), rga2.value())
    }

    @Test
    fun add0Add1Add2_MergeVal() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, 'A', ts1)
        rga1.insertAt(1, 'B', ts2)
        rga1.insertAt(2, 'C', ts3)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B', 'C'), rga2.value())
    }

    @Test
    fun add0_AddWin0MergeVal() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, 'B', ts1)
        rga2.insertAt(0, 'A', ts2)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B'), rga2.value())
    }

    @Test
    fun addWin0_Add0MergeVal() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, 'A', ts2)
        rga2.insertAt(0, 'B', ts1)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B'), rga2.value())
    }
 
    @Test
    fun concurrentAdd0MergeVal() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts3 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts4 = dc2.getNewTimestamp()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, 'D', ts1)
        rga1.insertAt(0, 'B', ts3)
        rga2.insertAt(0, 'C', ts2)
        rga2.insertAt(0, 'A', ts4)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B', 'C', 'D'), rga2.value())
    }

    @Test
    fun concurrentAddMerge1Val() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts3 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts4 = dc2.getNewTimestamp()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, 'C', ts1)
        rga1.insertAt(1, 'D', ts3)
        rga2.insertAt(0, 'A', ts2)
        rga2.insertAt(1, 'B', ts4)
        rga1.merge(rga2)

        assertEquals(listOf('A', 'B', 'C', 'D'), rga1.value())
    }

    @Test
    fun concurrentAddMerge2Val() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts3 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts4 = dc2.getNewTimestamp()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, 'C', ts1)
        rga1.insertAt(1, 'D', ts3)
        rga2.insertAt(0, 'A', ts2)
        rga2.insertAt(1, 'B', ts4)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B', 'C', 'D'), rga2.value())
    }

    @Test
    fun concurrentRemMergeVal() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts2 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts2)
        val ts3 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts3)
        val ts4 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts4)
        val ts5 = dc1.getNewTimestamp()
        val ts6 = dc2.getNewTimestamp()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, 'A', ts1)
        rga1.insertAt(1, 'B', ts2)
        rga1.insertAt(2, 'C', ts3)
        rga1.insertAt(3, 'D', ts4)
        rga2.merge(rga1)
        rga1.removeAt(1, ts5)
        rga2.removeAt(2, ts6)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'D'), rga2.value())
    }

    //TODO: delta tests
    //TODO: op-based
}
