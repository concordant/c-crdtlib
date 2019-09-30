package p2pclient.deltacrdt

import p2pclient.utils.DCId
import p2pclient.utils.SimpleEnvironment
import p2pclient.deltacrdt.*
import kotlin.test.Test
import kotlin.test.assertEquals


class LWWRegisterTest {
    @Test
    fun test1() {
        val idDc1 = DCId("dc1")
        val dc1 = SimpleEnvironment(idDc1)
        val ts0 = dc1.getNewTimestamp()
        val reg1 = LWWRegister("nuno", ts0)
        dc1.updateStateTS(ts0)

        val idDc2 = DCId("dc2")
        val dc2 = SimpleEnvironment(idDc2)
        val reg2 = LWWRegister("nuno", ts0)
        dc2.updateStateTS(ts0)
        assertEquals(reg1.get(), "nuno")
        assertEquals(reg2.get(), "nuno")

        val ts2 = dc2.getNewTimestamp()
        reg2.assign("annette", ts2)
        dc2.updateStateTS(ts2)
        assertEquals(reg1.get(), "nuno")
        assertEquals(reg2.get(), "annette")


        val ts1 = dc1.getNewTimestamp()
        reg1.assign("valter", ts1)
        dc1.updateStateTS(ts1)
        assertEquals(reg1.get(), "valter")
        assertEquals(reg2.get(), "annette")

        reg2.mergeState( reg1)
        dc2.updateStateVV( dc1.getCurrentState())
        assertEquals(reg1.get(), "valter")
        assertEquals(reg2.get(), "annette")

        reg1.mergeState( reg2)
        dc1.updateStateVV( dc2.getCurrentState())
        assertEquals(reg1.get(), "annette")
        assertEquals(reg2.get(), "annette")

        println( "All tests OK !!")
    }
}