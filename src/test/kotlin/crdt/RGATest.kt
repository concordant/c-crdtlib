package crdtlib.test

import crdtlib.crdt.RGA
import crdtlib.utils.DCId
import crdtlib.utils.Timestamp
import crdtlib.utils.SimpleEnvironment
import crdtlib.utils.VersionVector
import kotlin.test.Test
import kotlin.test.assertEquals

/**
* Represents a suite test for RGA.
**/
class RGATest {

    /**
    * This test evaluates the scenario: create, value.
    * Call to get should return an empty array.
    */
    @Test
    fun createValue() {
        val rga = RGA<Char>()
        assertEquals(listOf(), rga.value())
    }

    /**
    * This test evaluates the scenario: insert at 0, value.
    * Call to value should return an array containing the inserted value.
    */
    @Test
    fun add0Value() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts)

        assertEquals(listOf('A'), rga.value())
    }

    /**
    * This test evaluates the scenario: insert at 0 twice, value.
    * Call to value should return an array containing the two inserted values.
    * Second value should be at index 0 and first value at index 1.
    */
    @Test
    fun add0Add0Value() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga = RGA<Char>()

        rga.insertAt(0, 'B', ts1)
        rga.insertAt(0, 'A', ts2)

        assertEquals(listOf('A', 'B'), rga.value())
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1, value.
    * Call to value should return an array containing the two inserted values.
    * First value should be at index 0 and second value at index 1.
    */
    @Test
    fun add0Add1Value() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(1, 'B', ts2)

        assertEquals(listOf('A', 'B'), rga.value())
    }

    /**
    * This test evaluates the scenario: insert at 0, remove at 0, value.
    * Call to value should return an empty array.
    */
    @Test
    fun add0Remove0Value() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.removeAt(0, ts2)

        assertEquals(listOf(), rga.value())
    }

    /**
    * This test evaluates the scenario: insert at 0 twice, remove at 0 twice, value.
    * Call to value should return an empty array.
    */
    @Test
    fun add0Add0Remove0Remove0Value() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(0, 'B', ts2)
        rga.removeAt(0, ts3)
        rga.removeAt(0, ts4)

        assertEquals(listOf(), rga.value())
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1, remove at 0, insert at 1, value.
    * Call to value should return an array containing the two last inserted values.
    * Second inserted value should be at index 0 and third inserted value at index 1.
    */
    @Test
    fun add0Add1Remove0Add1Value() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(1, 'B', ts2)
        rga.removeAt(0, ts3)
        rga.insertAt(1, 'C', ts4)

        assertEquals(listOf('B', 'C'), rga.value())
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1, remove at 1, insert at 1, value.
    * Call to value should return an array containing the first and third inserted values.
    * First inserted value should be at index 0 and thrid inserted value at index 1.
    */
    @Test
    fun add0Add1Remove1Add1Value() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(1, 'B', ts2)
        rga.removeAt(1, ts3)
        rga.insertAt(1, 'C', ts4)

        assertEquals(listOf('A', 'C'), rga.value())
    }

    /**
    * This test evaluates the scenario: insert at 0 || merge, value.
    * Call to value should return an array containing the value inserted in replica 1.
    */
    @Test
    fun add0_MergeValue() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'A', ts)
        rga2.merge(rga1)

        assertEquals(listOf('A'), rga2.value())
    }

    /**
    * This test evaluates the scenario: insert at 0 twice || merge, value.
    * Call to value should return an array containing the two values inserted in replica 1.
    */
    @Test
    fun add0Add0_MergeValue() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'B', ts1)
        rga1.insertAt(0, 'A', ts2)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B'), rga2.value())
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1, insert at 2 || merge, value.
    * Call to value should return an array containing the three values inserted in replica 1.
    */
    @Test
    fun add0Add1Add2_MergeValue() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'A', ts1)
        rga1.insertAt(1, 'B', ts2)
        rga1.insertAt(2, 'C', ts3)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B', 'C'), rga2.value())
    }

    /**
    * This test evaluates the scenario: insert at 0 || insert at 0 (with greater timestamp), merge
    * value.
    * Call to value should return an array containing the two values. Value inserted in replica 2
    * should be at index 0 and the one inserted at replica 1 at index 1.
    */
    @Test
    fun add0_AddWin0MergeValue() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'B', ts1)
        rga2.insertAt(0, 'A', ts2)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B'), rga2.value())
    }

    /**
    * This test evaluates the scenario: insert at 0 (with greater timestamp) || insert at 0, merge
    * value.
    * Call to value should return an array containing the two values. Value inserted in replica
    * 1 should be at index 0 and the one inserted at replica 2 at index 1.
    */
    @Test
    fun addWin0_Add0MergeValue() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'A', ts2)
        rga2.insertAt(0, 'B', ts1)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B'), rga2.value())
    }
 
    /**
    * This test evaluates the scenario: insert at 0 twice || insert at 0 twice, merge value.
    * Call to value should return an array containing the four values. Values should be ordered
    * according to decreasing order of their associated timestamp.
    */
    @Test
    fun add0Add0_Add0Add0MergeValue() {
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
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'D', ts1)
        rga1.insertAt(0, 'B', ts3)
        rga2.insertAt(0, 'C', ts2)
        rga2.insertAt(0, 'A', ts4)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B', 'C', 'D'), rga2.value())
    }

    /**
    * This test evaluates the scenario: insert at 0 (with greater timestamp), insert at 1 || insert
    * at 0, insert at 1, merge value.
    * Call to value should return an array containing the four values. Values inserted in replica 1
    * should be before the one inserted in replica 2.
    */
    @Test
    fun addWin0Add1_Add0Add1MergeValue() {
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
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'C', ts1)
        rga1.insertAt(1, 'D', ts3)
        rga2.insertAt(0, 'A', ts2)
        rga2.insertAt(1, 'B', ts4)
        rga1.merge(rga2)

        assertEquals(listOf('A', 'B', 'C', 'D'), rga1.value())
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1 || insert at 0 (with greater
    * timestamp), insert at 1, merge value.
    * Call to value should return an array containing the four values. Values inserted in replica 2
    * should be before the one inserted in replica 1.
    */
    @Test
    fun add0Add1_AddWin0Add1MergeValue() {
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
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'C', ts1)
        rga1.insertAt(1, 'D', ts3)
        rga2.insertAt(0, 'A', ts2)
        rga2.insertAt(1, 'B', ts4)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B', 'C', 'D'), rga2.value())
    }

    /**
    * This test evaluates the scenario: insert four values, remove at 1 || merge (after adds in
    * replica 1), remove at 2, merge, value.
    * Call to value should return an array containing the two values that have not been remove (the
    * first and the fourth one).
    */
    @Test
    fun add0Add1Add2Add3Remove1_MergeAfterAddsRemove2MergeValue() {
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
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

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

    /**
    * This test evaluates the scenario: insert at 0 (with greater timestamp) insert at 1 || insert
    * at 0 (with second greater timestamp), insert at 1 || insert at 0, insert at 1, merge from
    * replica 1, merge from replica 2, value.
    * Call to value should return an array containing the six values. Values inserted in replica 1
    * should be before the one inserted in replica 2 which should be before those inserted at
    * replica 3.
    */
    @Test
    fun addWin0Add1_AddSecond0Add1_Add0Add1Merge1Merge2Value() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val id3 = DCId("dcid3")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val dc3 = SimpleEnvironment(id3)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts4 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts5 = dc2.getNewTimestamp()
        dc3.updateStateTS(ts3)
        val ts6 = dc3.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()
        val rga3 = RGA<Char>()

        rga1.insertAt(0, 'A', ts3)
        rga1.insertAt(1, 'B', ts6)
        rga2.insertAt(0, 'C', ts2)
        rga2.insertAt(1, 'D', ts5)
        rga3.insertAt(0, 'E', ts1)
        rga3.insertAt(1, 'F', ts4)
        rga3.merge(rga1)
        rga3.merge(rga2)

        assertEquals(listOf('A', 'B', 'C', 'D', 'E', 'F'), rga3.value())
    }

    /**
    * This test evaluates the scenario: insert at 0 (with greater timestamp) insert at 1 || insert
    * at 0, insert at 1 || insert at 0 (with second greater timestamp), insert at 1, merge from
    * replica 1, merge from replica 2, value.
    * Call to value should return an array containing the six values. Values inserted in replica 1
    * should be before the one inserted in replica 3 which should be before those inserted at
    * replica 2.
    */
    @Test
    fun addWin0Add1_Add0Add1_AddSecond0Add1Merge1Merge2Value() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val id3 = DCId("dcid3")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val dc3 = SimpleEnvironment(id3)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts4 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts5 = dc2.getNewTimestamp()
        dc3.updateStateTS(ts3)
        val ts6 = dc3.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()
        val rga3 = RGA<Char>()

        rga1.insertAt(0, 'A', ts3)
        rga1.insertAt(1, 'B', ts6)
        rga2.insertAt(0, 'E', ts1)
        rga2.insertAt(1, 'F', ts4)
        rga3.insertAt(0, 'C', ts2)
        rga3.insertAt(1, 'D', ts5)
        rga3.merge(rga1)
        rga3.merge(rga2)

        assertEquals(listOf('A', 'B', 'C', 'D', 'E', 'F'), rga3.value())
    }

    /**
    * This test evaluates the scenario: insert at 0 (with second greater timestamp) insert at 1 ||
    * insert at 0 (with greater timestamp), insert at 1 || insert at 0, insert at 1, merge from
    * replica 1, merge from replica 2, value.
    * Call to value should return an array containing the six values. Values inserted in replica 2
    * should be before the one inserted in replica 1 which should be before those inserted at
    * replica 3.
    */
    @Test
    fun addSecond0Add1_AddWin0Add1_Add0Add1Merge1Merge2Value() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val id3 = DCId("dcid3")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val dc3 = SimpleEnvironment(id3)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts4 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts5 = dc2.getNewTimestamp()
        dc3.updateStateTS(ts3)
        val ts6 = dc3.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()
        val rga3 = RGA<Char>()

        rga1.insertAt(0, 'C', ts2)
        rga1.insertAt(1, 'D', ts5)
        rga2.insertAt(0, 'A', ts3)
        rga2.insertAt(1, 'B', ts6)
        rga3.insertAt(0, 'E', ts1)
        rga3.insertAt(1, 'F', ts4)
        rga3.merge(rga1)
        rga3.merge(rga2)

        assertEquals(listOf('A', 'B', 'C', 'D', 'E', 'F'), rga3.value())
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1 || insert at 0 (with greater
    * timestamp), insert at 1 || insert at 0 (with second greater timestamp), insert at 1, merge
    * from replica 1, merge from replica 2, value.
    * Call to value should return an array containing the six values. Values inserted in replica 2
    * should be before the one inserted in replica 3 which should be before those inserted at
    * replica 1.
    */
    @Test
    fun add0Add1_AddWin0Add1_AddSecond0Add1Merge1Merge2Value() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val id3 = DCId("dcid3")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val dc3 = SimpleEnvironment(id3)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts4 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts5 = dc2.getNewTimestamp()
        dc3.updateStateTS(ts3)
        val ts6 = dc3.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()
        val rga3 = RGA<Char>()

        rga1.insertAt(0, 'E', ts1)
        rga1.insertAt(1, 'F', ts4)
        rga2.insertAt(0, 'A', ts3)
        rga2.insertAt(1, 'B', ts6)
        rga3.insertAt(0, 'C', ts2)
        rga3.insertAt(1, 'D', ts5)
        rga3.merge(rga1)
        rga3.merge(rga2)

        assertEquals(listOf('A', 'B', 'C', 'D', 'E', 'F'), rga3.value())
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1 || insert at 0 (with second greater
    * timestamp), insert at 1 || insert at 0 (with greater timestamp), insert at 1, merge from
    * replica 1, merge from replica 2, value.
    * Call to value should return an array containing the six values. Values inserted in replica 3
    * should be before the one inserted in replica 2 which should be before those inserted at
    * replica 1.
    */
    @Test
    fun add0Add1_AddSecond0Add1_AddWin0Add1Merge1Merge2Value() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val id3 = DCId("dcid3")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val dc3 = SimpleEnvironment(id3)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts4 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts5 = dc2.getNewTimestamp()
        dc3.updateStateTS(ts3)
        val ts6 = dc3.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()
        val rga3 = RGA<Char>()

        rga1.insertAt(0, 'E', ts1)
        rga1.insertAt(1, 'F', ts4)
        rga2.insertAt(0, 'C', ts2)
        rga2.insertAt(1, 'D', ts5)
        rga3.insertAt(0, 'A', ts3)
        rga3.insertAt(1, 'B', ts6)
        rga3.merge(rga1)
        rga3.merge(rga2)

        assertEquals(listOf('A', 'B', 'C', 'D', 'E', 'F'), rga3.value())
    }

    /**
    * This test evaluates the scenario: insert at 0 (with second greater timestamp) insert at 1 ||
    * insert at 0, insert at 1 || insert at 0 (with greater timestamp), insert at 1, merge from
    * replica 1, merge from replica 2, value.
    * Call to value should return an array containing the six values. Values inserted in replica 3
    * should be before the one inserted in replica 1 which should be before those inserted at
    * replica 2.
    */
    @Test
    fun addSecond0Add1_Add0Add1_AddWin0Add1Merge1Merge2Value() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val id3 = DCId("dcid3")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val dc3 = SimpleEnvironment(id3)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts4 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts5 = dc2.getNewTimestamp()
        dc3.updateStateTS(ts3)
        val ts6 = dc3.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()
        val rga3 = RGA<Char>()

        rga1.insertAt(0, 'C', ts2)
        rga1.insertAt(1, 'D', ts5)
        rga2.insertAt(0, 'E', ts1)
        rga2.insertAt(1, 'F', ts4)
        rga3.insertAt(0, 'A', ts3)
        rga3.insertAt(1, 'B', ts6)
        rga3.merge(rga1)
        rga3.merge(rga2)

        assertEquals(listOf('A', 'B', 'C', 'D', 'E', 'F'), rga3.value())
    }

    /**
    * This test evaluates the use of delta return by call to insertAt method.
    * Call to value should return an array containing the value inserted in replica 1.
    */
    @Test
    fun insertOp() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        val insertOp = rga1.insertAt(0, 'A', ts)
        rga2.merge(insertOp)
        rga2.merge(insertOp)

        assertEquals(listOf('A'), rga1.value())
        assertEquals(listOf('A'), rga2.value())
    }

    /**
    * This test evaluates the use of delta return by call to removeAt method.
    * Call to value should return an empty array.
    */
    @Test
    fun removeOp() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'A', ts1)
        rga2.merge(rga1)
        val removeOp = rga1.removeAt(0, ts2)
        rga1.merge(removeOp)
        rga2.merge(removeOp)

        assertEquals(listOf(), rga1.value())
        assertEquals(listOf(), rga2.value())
    }

    /**
    * This test evaluates the use of delta return by call to insertAt and removeAt methods.
    * Call to value should return an empty array.
    */
    @Test
    fun insertRemoveOp() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        val insertOp = rga1.insertAt(0, 'A', ts1)
        val removeOp = rga1.removeAt(0, ts2)
        rga1.merge(insertOp)
        rga1.merge(removeOp)
        rga2.merge(insertOp)
        rga2.merge(removeOp)

        assertEquals(listOf(), rga1.value())
        assertEquals(listOf(), rga2.value())
    }

    /**
    * This test evaluates the merge of deltas returned by call to insertAt and removeAt methods.
    * Call to value should return an empty array.
    */
    @Test
    fun insertRemoveOpFusion() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        val op1 = rga1.insertAt(0, 'A', ts1)
        val op2 = rga1.removeAt(0, ts2)
        op1.merge(op2)
        rga1.merge(op1)
        rga2.merge(op1)

        assertEquals(listOf(), rga1.value())
        assertEquals(listOf(), rga2.value())
    }

    /**
    * This test evaluates the merge of deltas returned by call to removeAt and insertAt methods.
    * Call to value should return an empty array.
    */
    @Test
    fun removeInsertOpFusion() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        val op1 = rga1.insertAt(0, 'A', ts1)
        val op2 = rga1.removeAt(0, ts2)
        op2.merge(op1)
        rga1.merge(op2)
        rga2.merge(op2)

        assertEquals(listOf(), rga1.value())
        assertEquals(listOf(), rga2.value())
    }

    /**
    * This test evaluates the generation of delta plus its merging into another replica.
    * Call to value should return an array containing the values set yb insertAt w.r.t the given
    * context.
    */
    @Test
    fun generateDelta() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        val vv = VersionVector()
        vv.addTS(ts2)
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'A', ts1)
        rga1.insertAt(0, 'B', ts3)
        rga1.insertAt(0, 'C', ts2)
        rga1.insertAt(0, 'D', ts4)
        val delta = rga1.generateDelta(vv)
        rga2.merge(delta)

        assertEquals(listOf('D', 'B'), rga2.value())
    }

    /**
    * This test evaluates JSON serialization.
    **/
    @Test
    fun toJsonSerialization() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(1, 'B', ts2)
        rga.removeAt(1, ts3)
        rga.insertAt(1, 'C', ts4)
        val rgaJson = rga.toJson(Char::class)

        assertEquals("""{"_metadata":[{"anchor":null,"uid":{"id":{"name":"dcid"},"cnt":1},"ts":{"id":{"name":"dcid"},"cnt":1},"removed":false},{"anchor":{"id":{"name":"dcid"},"cnt":1},"uid":{"id":{"name":"dcid"},"cnt":4},"ts":{"id":{"name":"dcid"},"cnt":4},"removed":false},{"atom":"B","anchor":{"id":{"name":"dcid"},"cnt":1},"uid":{"id":{"name":"dcid"},"cnt":2},"ts":{"id":{"name":"dcid"},"cnt":3},"removed":true}],"value":["A","C"]}""", rgaJson)
    }

    /**
    * This test evaluates JSON deserialization.
    **/
    @Test
    fun fromJsonDeserialization() {
        val rgaJson = RGA.fromJson(Char::class, """{"_metadata":[{"anchor":null,"uid":{"id":{"name":"dcid"},"cnt":1},"ts":{"id":{"name":"dcid"},"cnt":1},"removed":false},{"anchor":{"id":{"name":"dcid"},"cnt":1},"uid":{"id":{"name":"dcid"},"cnt":4},"ts":{"id":{"name":"dcid"},"cnt":4},"removed":false},{"atom":"B","anchor":{"id":{"name":"dcid"},"cnt":1},"uid":{"id":{"name":"dcid"},"cnt":2},"ts":{"id":{"name":"dcid"},"cnt":3},"removed":true}],"value":["A","C"]}""")

        assertEquals(listOf('A', 'C'), rgaJson.value())
    }
}
