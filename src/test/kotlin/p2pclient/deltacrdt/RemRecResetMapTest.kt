package deltacrdt

import crdtlib.utils.DCId
import crdtlib.utils.SimpleEnvironment
import crdtlib.deltacrdt.*
import kotlin.test.Test
import kotlin.test.assertEquals


class RemRecResetMapTest {
    @Test
    fun test1() {
        val idDc1 = DCId("dc1")
        val dc1 = SimpleEnvironment(idDc1)
        val map1 = RemRecResetMap()

        val ts1 = dc1.getNewTimestamp()
        val reg1 = LWWRegister("nuno", ts1)
        map1.put( "key1", reg1, ts1, dc1.getCurrentState())
        dc1.updateStateTS(ts1)

        val idDc2 = DCId("dc2")
        val dc2 = SimpleEnvironment(idDc2)
        val map2 = RemRecResetMap()

        val ts2 = dc2.getNewTimestamp()
        val reg2 = LWWRegister("valter", ts2)
        map2.put( "key2", reg2, ts2, dc2.getCurrentState())
        dc2.updateStateTS(ts2)


        map2.mergeDelta( map1.getDelta( dc2.getCurrentState()))
        println( "map tests")
        println( map2)
        var v1 = map2.get( "key1", LWWRegister::class)
        if ( v1 is LWWRegister)
            println( v1.get())
        v1 = map2.get( "key2", LWWRegister::class)
        if ( v1 is LWWRegister)
            println( v1.get())

    }
}