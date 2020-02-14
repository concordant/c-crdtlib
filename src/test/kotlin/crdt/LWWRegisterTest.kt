package crdtlib.test

import crdtlib.utils.DCId
import crdtlib.utils.SimpleEnvironment
import crdtlib.crdt.LWWRegister
import kotlin.test.Test
import kotlin.test.assertEquals


class LWWRegisterTest {
    @Test
    fun createGet() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val value = "value"

        val reg = LWWRegister<String>(value, ts)

        assertEquals(value, reg.get())
    }

    @Test
    fun valueUpdate() {
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

    @Test
    fun oldValueUpdate() {
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

    @Test
    fun mergeState() {
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


    @Test
    fun mergeDelta() {
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
