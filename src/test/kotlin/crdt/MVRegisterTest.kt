package crdtlib.test

import crdtlib.utils.DCId
import crdtlib.utils.SimpleEnvironment
import crdtlib.crdt.MVRegister
import kotlin.test.Test
import kotlin.test.assertEquals


class MVRegisterTest {
    @Test
    fun createEmptyGet() {
        val reg = MVRegister<String>()

        assert(reg.get().isEmpty())
    }

    @Test
    fun createValueGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val value = "value"
        val reg = MVRegister<String>(value, ts)

        assertEquals(setOf(value), reg.get())
    }

    @Test
    fun createSetGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val value = setOf("value1", "value2")
        val reg = MVRegister<String>(value, ts)

        assertEquals(value, reg.get())
    }

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

    @Test
    fun assignAssignSetGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val val1 = "value1"
        val val2 = setOf("value2", "value3")

        val reg = MVRegister<String>(val1, ts1)
        reg.assign(val2, ts2)

        assertEquals(val2, reg.get())
    }

    @Test
    fun assignOldAssignGet() {
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

    @Test
    fun assignSetOldAssignSetGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val val1 = setOf("value1", "value2")
        val val2 = setOf("value3", "value4")

        val reg = MVRegister<String>(val1, ts2)
        reg.assign(val2, ts1)

        assertEquals(val1, reg.get())
    }

    @Test
    fun assign_assignMerge() {
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
        reg1.merge(reg2)
        reg2.merge(reg1)

        assertEquals(setOf(val1, val2), reg1.get())
        assertEquals(setOf(val1, val2), reg2.get())
    }

    @Test
    fun assign_mergeAssign() {
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

    @Test
    fun assignSet_assignSetMerge() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val val1 = setOf("value1", "value2")
        val val2 = setOf("value3", "value4")

        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>(val2, ts2)
        reg1.merge(reg2)
        reg2.merge(reg1)

        assertEquals(val1.union(val2), reg1.get())
        assertEquals(val1.union(val2), reg2.get())
    }

    @Test
    fun assignSet_mergeAssignSet() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val val1 = setOf("value1", "value2")
        val val2 = setOf("value3", "value4")

        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>()
        reg2.merge(reg1)
        reg2.assign(val2, ts2)

        assertEquals(val2, reg2.get())
    }

    // more difficult merge scenarios
    // op-based
    // merge delta
}
