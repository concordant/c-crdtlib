package crdtlib.test

import crdtlib.crdt.MVRegister
import crdtlib.utils.DCId
import crdtlib.utils.SimpleEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
* Represents a test suite for MVRegister.
**/
class MVRegisterTest {

    /**
    * This test evaluates the scenario: create empty get.
    * Call to get should return an empty set.
    */
    @Test
    fun createEmptyGet() {
        val reg = MVRegister<String>()

        assertTrue(reg.get().isEmpty())
    }

    /**
    * This test evaluates the scenario: create with value get.
    * Call to get should return a set containing the value.
    */
    @Test
    fun createValueGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val value = "value"
        val reg = MVRegister<String>(value, ts)

        assertEquals(setOf(value), reg.get())
    }

    /**
    * This test evaluates the scenario: create by copy get.
    * Call to get should return a set containing the assigned in the first replica.
    */
    @Test
    fun createCopyGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val value = "value"
        val reg1 = MVRegister<String>(value, ts)

        val reg2 = MVRegister<String>(reg1)

        assertEquals(setOf(value), reg2.get())
    }

    /**
    * This test evaluates the scenario: create by copy(concurrent values) get.
    * Call to get should return a set containing values assigned in first and second replicas.
    */
    @Test
    fun createCopySetGet() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val val1 = "value1"
        val val2 = "value2"
        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>(val2, ts2)

        reg2.merge(reg1)
        val reg3 = MVRegister<String>(reg2)

        assertEquals(setOf(val1, val2), reg3.get())
    }

    /**
    * This test evaluates the scenario: assign assign get.
    * Call to get should return last assigned value.
    */
    @Test
    fun assignAssignGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val val1 = "value1"
        val val2 = "value2"

        val reg = MVRegister<String>(val1, ts1)
        reg.assign(val2, ts2)

        assertEquals(setOf(val2), reg.get())
    }

    /**
    * This test evaluates the scenario: assign assign(old timestamp) get.
    * Call to get should return first assigned value.
    */
    @Test
    fun assignAssignOldGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val val1 = "value1"
        val val2 = "value2"

        val reg = MVRegister<String>(val1, ts2)
        reg.assign(val2, ts1)

        assertEquals(setOf(val1), reg.get())
    }

    /**
    * This test evaluates the scenario: assign || merge get.
    * Call to get should return value assigned by the first replica.
    */
    @Test
    fun assign_MergeGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val value = "value"

        val reg1 = MVRegister<String>(value, ts)
        val reg2 = MVRegister<String>()
        reg1.merge(reg2)
        reg2.merge(reg1)

        assertEquals(setOf(value), reg1.get())
        assertEquals(setOf(value), reg2.get())
    }

    /**
    * This test evaluates the scenario: assign || merge assign get.
    * Call to get should return a set containing the value assigned by the second replica.
    */
    @Test
    fun assign_MergeAssignGet() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val val1 = "value1"
        val val2 = "value2"

        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>()
        reg2.merge(reg1)
        reg2.assign(val2, ts2)

        assertEquals(setOf(val2), reg2.get())
    }

    /**
    * This test evaluates the scenario: assign || assign merge get.
    * Call to get should return a set containing the two values.
    */
    @Test
    fun assign_AssignMergeGet() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val val1 = "value1"
        val val2 = "value2"

        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>()
        reg2.assign(val2, ts2)
        reg2.merge(reg1)

        assertEquals(setOf(val1, val2), reg2.get())
    }

    /**
    * This test evaluates the scenario: assign(before merge1) assign(before merge 2) || merge1
    * merge2 get.
    * Call to get should return a set containing the last value assigned by the first replica.
    */
    @Test
    fun assignBeforeMerge1AssignBeforeMerge2_Merge1Merge2Get() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val val1 = "value1"
        val val2 = "value2"

        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>(val2, ts2)
        reg2.merge(reg1)
        reg1.assign(val2, ts2)
        reg2.merge(reg1)

        assertEquals(setOf(val2), reg2.get())
    }

    /**
    * This test evaluates the scenario: assign(before merge1) assign(before merge 2) || assign
    * merge1 merge2 get.
    * Call to get should return a set containing the last value assigned by the first replica and
    * value assigned by replica two.
    */
    @Test
    fun assignBeforeMerge1AssignBeforeMerge2_AssignMerge1Merge2Get() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts3 = dc1.getNewTimestamp()
        val val1 = "value1"
        val val2 = "value2"
        val val3 = "value3"

        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>(val2, ts2)
        reg2.merge(reg1)
        reg1.assign(val3, ts3)
        reg2.merge(reg1)

        assertEquals(setOf(val2, val3), reg2.get())
    }

    /**
    * This test evaluates the scenario: assign || assign merge(from 3) || assign merge(from 1)
    * merge(from 2) get.
    * Call to get should return a set containing the value assigned by the first, second, and third
    * replicas.
    */
    @Test
    fun assign_AssignMerge3_AssignMerge1Merge2Get() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val id3 = DCId("dcid3")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val dc3 = SimpleEnvironment(id3)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        val val1 = "value1"
        val val2 = "value2"
        val val3 = "value3"
        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>(val2, ts2)
        val reg3 = MVRegister<String>(val3, ts3)

        reg2.merge(reg3)
        reg3.merge(reg1)
        reg3.merge(reg2)

        assertEquals(setOf(val1, val2, val3), reg3.get())
    }

    /**
    * This test evaluates the scenario: assign || merge(from 3) assign || assign merge(from 1)
    * merge(from 2) get.
    * Call to get should return a set containing the value assigned by the first and second
    * replicas, the value assigned by replica three should not be present since it has been
    * overrided by replica two.
    */
    @Test
    fun assign_Merge3Assign_AssignMerge1Merge2Get() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val id3 = DCId("dcid3")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val dc3 = SimpleEnvironment(id3)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        val val1 = "value1"
        val val2 = "value2"
        val val3 = "value3"
        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>()
        val reg3 = MVRegister<String>(val3, ts3)

        reg2.merge(reg3)
        reg2.assign(val2, ts2)
        reg3.merge(reg1)
        reg3.merge(reg2)

        assertEquals(setOf(val1, val2), reg3.get())
    }

    /**
    * This test evaluates the use of delta return by call to assign method.
    * Call to get should return a set containing the value assigned by the first replica.
    */
    @Test
    fun assignOp() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val value = "value"
        val reg1 = MVRegister<String>()
        val reg2 = MVRegister<String>()

        val assignOp = reg1.assign(value, ts)
        reg1.merge(assignOp)
        reg2.merge(assignOp)

        assertEquals(setOf(value), reg1.get())
        assertEquals(setOf(value), reg2.get())
    }

    /*
    * This test evaluates the generation of delta plus its merging into another replica.
    * Call to value should return a set containing values assigned by operations registered in the
    * first and second replicas.
    */
    @Test
    fun generateDelta() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val vv = dc1.getCurrentState()
        val val1 = "value1"
        val val2 = "value2"
        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>(val2, ts2)
        val reg3 = MVRegister<String>()

        reg2.merge(reg1)
        val delta = reg2.generateDelta(vv)
        reg3.merge(delta)

        assertEquals(setOf(val1, val2), reg3.get())
    }

    /*
    * This test evaluates the generation of an empty delta plus its merging into another replica.
    * Call to value should return an empty set.
    */
    @Test
    fun generateEmptyDelta() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc1.updateStateTS(ts1)
        dc1.updateStateTS(ts2)
        val vv = dc1.getCurrentState()
        val val1 = "value1"
        val val2 = "value2"
        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>(val2, ts2)
        val reg3 = MVRegister<String>()

        reg2.merge(reg1)
        val delta = reg2.generateDelta(vv)
        reg3.merge(delta)

        assertEquals(setOf(), reg3.get())
    }
}
