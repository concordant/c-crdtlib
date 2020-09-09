/*
* Copyright © 2020, Concordant and contributors.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
* associated documentation files (the "Software"), to deal in the Software without restriction,
* including without limitation the rights to use, copy, modify, merge, publish, distribute,
* sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all copies or
* substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
* NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package crdtlib.test

import crdtlib.crdt.LWWRegister
import crdtlib.utils.DCUId
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
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
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
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
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
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
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
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
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
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
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
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
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
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
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

    /**
    * This test evaluates JSON serialization of a lww register.
    **/
    @Test
    fun toJsonSerialization() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val value = "value"

        val reg = LWWRegister<String>(value, ts)

        assertEquals("""{"_type":"LWWRegister","_metadata":{"uid":{"name":"dcid"},"cnt":1},"value":"value"}""", reg.toJson(String::class))
    }

    /**
    * This test evaluates JSON deserialization of a lww register.
    **/
    @Test
    fun fromJsonDeserialization() {
        val regJson = LWWRegister.fromJson(String::class, """{"_type":"LWWRegister","_metadata":{"uid":{"name":"dcid"},"cnt":1},"value":"value"}""")

        assertEquals("value", regJson.get())
    }
}
