package crdtlib.crdt

import crdtlib.utils.DCId
import crdtlib.utils.SimpleEnvironment
import crdtlib.crdt.*
import kotlin.test.Test
import kotlin.test.assertEquals


class LWWRegisterTest {
    @Test
    fun test1() {
        val idDc1 = DCId( "dc1")
        val dc1 = SimpleEnvironment( idDc1)
        val ts0 = dc1.getNewTimestamp()
        val reg1 = LWWRegister<String>( "nuno", ts0)
        dc1.updateStateTS(ts0)

        val idDc2 = DCId( "dc2")
        val dc2 = SimpleEnvironment( idDc2)
        val reg2 = LWWRegister<String>( "nuno", ts0)
        dc2.updateStateTS(ts0)


        val ts2 = dc2.getNewTimestamp()
        val op2 = reg2.assign( "annette", ts2, dc2.getCurrentState())
        op2.exec( reg2)
        dc2.updateStateTS( ts2)
        println( "DC1 : " + reg1.doGet())
        println( "DC2 : " + reg2.doGet())

        val ts1 = dc1.getNewTimestamp()
        val op1 = reg1.assign( "valter", ts1, dc1.getCurrentState())
        op1.exec( reg1)
        dc1.updateStateTS( ts1)
        println( "DC1 : " + reg1.doGet())
        println( "DC2 : " + reg2.doGet())

        op1.exec( reg2)
        dc2.updateStateTS( ts1)
        println( "DC1 : " + reg1.doGet())
        println( "DC2 : " + reg2.doGet())

        op2.exec( reg1)
        dc1.updateStateTS( ts2)
        println( "DC1 : " + reg1.doGet())
        println( "DC2. : " + reg2.doGet())
    }
}