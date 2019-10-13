package p2pclient.crdt

import crdtlib.crdt.LWWMap
import crdtlib.utils.DCId
import crdtlib.utils.SimpleEnvironment
import kotlin.test.Test

class LWWMapTest {
    @Test
    fun test1() {
        val idDc1 = DCId( "dc1")
        val dc1 = SimpleEnvironment( idDc1)
        //val ts0 = dc1.getNewTimestamp()
        val reg1 = LWWMap<String,String>()

        val idDc2 = DCId( "dc2")
        val dc2 = SimpleEnvironment( idDc2)
        val reg2 = LWWMap<String,String>()

        val key = "A1"

        val ts2 = dc2.getNewTimestamp()
        val op2 = reg2.put( key, "nuno", ts2, dc2.getCurrentState())
        op2.exec( reg2)
        dc2.updateStateTS( ts2)
        println( "DC1 : " + reg1.doGet( key))
        println( "DC2 : " + reg2.doGet( key))

        val ts1 = dc1.getNewTimestamp()
        val op1 = reg1.put( key, "valter", ts1, dc1.getCurrentState())
        op1.exec( reg1)
        dc1.updateStateTS( ts1)
        println( "DC1 : " + reg1.doGet( key))
        println( "DC2 : " + reg2.doGet( key))

        op1.exec( reg2)
        dc2.updateStateTS( ts1)
        println( "DC1 : " + reg1.doGet( key))
        println( "DC2 : " + reg2.doGet( key))

        op2.exec( reg1)
        dc1.updateStateTS( ts2)
        println( "DC1 : " + reg1.doGet( key))
        println( "DC2 : " + reg2.doGet( key))

        val ts3 = dc2.getNewTimestamp()
        val op3 = reg1.delete(key, ts3, dc2.getCurrentState())
        op3.exec( reg2)
        dc2.updateStateTS( ts3)
        println( "DC1 : " + reg1.doGet( key))
        println( "DC2 : " + reg2.doGet( key))    }
}