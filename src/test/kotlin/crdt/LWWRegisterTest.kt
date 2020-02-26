package crdtlib.test

import crdtlib.crdt.LWWRegister
import crdtlib.utils.DCId
import crdtlib.utils.SimpleEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

/**
* Represents a test suite for LWWRegister.
**/
class LWWRegisterTest {

    /**
    * This test evaluates the scenario: create get.
    * Call to get should return the value assigned by the constructor.
    */
    @Test
    fun createGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val value = "value"

        val reg = LWWRegister<String>(value, ts)

        assertEquals(value, reg.get())
    }

    /**
    * This test evaluates the scenario: create assign get.
    * Call to get should return the value set by the assign method.
    */
    @Test
    fun assignGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val val1 = "value1"
        val val2 = "value2"

        val reg = LWWRegister<String>(val1, ts1)
        reg.assign(val2, ts2)

        assertEquals(val2, reg.get())
    }

    /**
    * This test evaluates the scenario: create assign(older timestamp) get.
    * Call to get should return the value set by the constructor.
    */
    @Test
    fun assignOldGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val val1 = "value1"
        val val2 = "value2"
        
        val reg = LWWRegister<String>(val1, ts2)
        reg.assign(val2, ts1)

        assertEquals(val1, reg.get())
    }

    /**
    * This test evaluates the scenario: assign || assign merge get.
    * Call to get should return the value set in the second replica.
    */
    @Test
    fun assign_assignMergeGet() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val val1 = "value1"
        val val2 = "value2"

        val reg1 = LWWRegister<String>(val1, ts1)
        val reg2 = LWWRegister<String>(val2, ts2)
        reg1.merge(reg2)
        reg2.merge(reg1)

        assertEquals(val2, reg1.get())
        assertEquals(val2, reg2.get())
    }

    /**
    * This test evaluates the scenario: assign || assign merge assign get.
    * Call to get should return the value set by call to assign method in the second replica.
    */
    @Test
    fun assign_mergeAssignGet() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts3 = dc2.getNewTimestamp()
        val val1 = "value1"
        val val2 = "value2"
        val val3 = "value3"

        val reg1 = LWWRegister<String>(val1, ts1)
        val reg2 = LWWRegister<String>(val2, ts2)
        reg2.merge(reg1)
        reg2.assign(val3, ts3)

        assertEquals(val3, reg2.get())
    }

    /**
    * This test evaluates the use of delta return by call to assign method.
    * Call to get should return last value set in the second replica.
    */
    @Test
    fun assignOp() {
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
        val val1 = "value1"
        val val2 = "value2"
        val val3 = "value3"
        val val4 = "value4"

        val reg1 = LWWRegister<String>(val1, ts1)
        val reg2 = LWWRegister<String>(val2, ts2)
        val assignOp1 = reg1.assign(val3, ts3)
        val assignOp2 = reg2.assign(val4, ts4)

        reg1.merge(assignOp2)
        reg2.merge(assignOp1)

        assertEquals(val4, reg1.get())
        assertEquals(val4, reg2.get())
    }

    /*
    * This test evaluates the generation of delta plus its merging into another replica.
    * Call to get should return the values set in the second replica.
    */

    @Test
    fun generateDelta() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val vv1 = dc1.getCurrentState()
        dc2.updateStateTS(ts2)
        dc2.updateStateTS(ts1)
        val vv2 = dc2.getCurrentState()
        val val1 = "value1"
        val val2 = "value2"

        val reg1 = LWWRegister<String>(val1, ts1)
        val reg2 = LWWRegister<String>(val2, ts2)
        val delta2 = reg1.generateDelta(vv2)
        val delta1 = reg2.generateDelta(vv1)

        reg1.merge(delta1)
        reg2.merge(delta2)

        assertEquals(val2, reg1.get())
        assertEquals(val2, reg2.get())
    }
}