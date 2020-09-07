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

import crdtlib.crdt.PNCounter
import crdtlib.utils.DCUId
import crdtlib.utils.SimpleEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
* Represents a suite test for PNCounter.
**/
class PNCounterTest {

    /**
    * This test evaluates the scenario: get.
    * Call to get should return 0.
    */
    @Test
    fun createVal() {
        val cnt = PNCounter()

        assertEquals(0, cnt.get())
    }

    /**
    * This test evaluates the scenario: increment get.
    * Call to get should return the value set by increment.
    */
    @Test
    fun increment() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val inc = 10
        val cnt = PNCounter()

        cnt.increment(inc, ts)

        assertEquals(inc, cnt.get())
    }


    /**
    * This test evaluates the scenario: decrement get.
    * Call to get should return the inverse of value set by decrement.
    */
    @Test
    fun decrement() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val dec = 10
        val cnt = PNCounter()

        cnt.decrement(dec, ts)

        assertEquals(-dec, cnt.get())
    }

    /**
    * This test evaluates the scenario where increment overflows.
    */
    @Test
    fun incrementOverflow() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        val inc = 42
        val cnt = PNCounter()

        cnt.increment(Int.MAX_VALUE - inc, ts1)
        cnt.increment(inc, ts2)

        assertFailsWith(RuntimeException::class) {
            cnt.increment(inc, ts3)
        }
    }


    /**
    * This test evaluates the scenario where decrement overflows.
    */
    @Test
    fun decrementOverflow() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        val dec = 10
        val cnt = PNCounter()

        cnt.decrement(Int.MAX_VALUE - dec, ts1)
        cnt.decrement(dec, ts2)

        assertFailsWith(RuntimeException::class) {
            cnt.decrement(dec, ts3)
        }
    }

    /**
    * This test evaluates the scenario: increment(with a negative value) get.
    * Call to get should return the value set by increment.
    */
    @Test
    fun incrementNegativeAmount() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val inc = -10
        val cnt = PNCounter()

        cnt.increment(inc, ts)

        assertEquals(inc, cnt.get())
    }

    /**
    * This test evaluates the scenario: decrement(with a negative value) get.
    * Call to get should return the inverse of value set by decrement.
    */
    @Test
    fun decrementNegativeAmount() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val dec = -10
        val cnt = PNCounter()

        cnt.decrement(dec, ts)

        assertEquals(-dec, cnt.get())
    }

    /**
    * This test evaluates the scenario: incremement(multiple times) get.
    * Call to get should return the sum of values set by calls to increment.
    */
    @Test
    fun multiIncrement() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        val inc1 = 10
        val inc2 = 1
        val inc3 = 100
        val cnt = PNCounter()

        cnt.increment(inc1, ts1)
        cnt.increment(inc2, ts2)
        cnt.increment(inc3, ts3)

        assertEquals(111, cnt.get())
    }

    /**
    * This test evaluates the scenario: decremement(multiple times) get.
    * Call to get should return the inverse of the sum of values set by calls to decrement.
    */
    @Test
    fun multiDecrement() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        val dec1 = 10
        val dec2 = 1
        val dec3 = 100
        val cnt = PNCounter()

        cnt.decrement(dec1, ts1)
        cnt.decrement(dec2, ts2)
        cnt.decrement(dec3, ts3)

        assertEquals(-111, cnt.get())
    }

    /**
    * This test evaluates the scenario: multiple increment and decrement get.
    * Call to get should return the sum of increments minus the sum of decrements.
    */
    @Test
    fun incrementDecrementPositive() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        val inc1 = 42
        val inc2 = 34
        val dec1 = 27
        val dec2 = 2
        val cnt = PNCounter()

        cnt.increment(inc1, ts1)
        cnt.decrement(dec1, ts2)
        cnt.increment(inc2, ts3)
        cnt.decrement(dec2, ts4)

        assertEquals(47, cnt.get())
    }

    /**
    * This test evaluates the scenario: multiple increment and decrement get.
    * Call to get should return the sum of increments minus the sum of decrements.
    */
    @Test
    fun incrementDecrementNegative() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        val inc1 = 42
        val inc2 = 34
        val dec1 = 77
        val dec2 = 13
        val cnt = PNCounter()

        cnt.increment(inc1, ts1)
        cnt.decrement(dec1, ts2)
        cnt.increment(inc2, ts3)
        cnt.decrement(dec2, ts4)

        assertEquals(-14, cnt.get())
    }

    /**
    * This test evaluates the scenario: increment || merge get.
    * Call to get should return value set by increment in the first replica.
    */
    @Test
    fun increment_MergeValue() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val inc = 11 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.increment(inc, ts)
        cnt2.merge(cnt1)
        cnt1.merge(cnt2)

        assertEquals(11, cnt1.get())
        assertEquals(11, cnt2.get())
    }

    /**
    * This test evaluates the scenario: decrement || merge get.
    * Call to get should return the inverse value set by decrement in the first replica.
    */
    @Test
    fun decrement_MergeValue() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val dec = 11 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.decrement(dec, ts)
        cnt2.merge(cnt1)
        cnt1.merge(cnt2)

        assertEquals(-11, cnt1.get())
        assertEquals(-11, cnt2.get())
    }

    /**
    * This test evaluates the scenario: increment || increment merge get.
    * Call to get should return sum of the two increment values.
    */
    @Test
    fun increment_incrementMergeValue() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val inc1 = 10 
        val inc2 = 1 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.increment(inc1, ts1)
        cnt2.increment(inc2, ts2)
        cnt2.merge(cnt1)

        assertEquals(11, cnt2.get())
    }

    /**
    * This test evaluates the scenario: increment || merge increment get.
    * Call to get should return sum of the two increment values.
    */
    @Test
    fun increment_mergeIncrementValue() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val inc1 = 10 
        val inc2 = 1 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.increment(inc1, ts1)
        cnt2.merge(cnt1)
        cnt2.increment(inc2, ts2)

        assertEquals(11, cnt2.get())
    }

    /**
    * This test evaluates the scenario: decrement || decrement merge get.
    * Call to get should return the inverse of the sum of the two decrement values.
    */
    @Test
    fun decrement_decrementMergeValue() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val dec1 = 10 
        val dec2 = 1 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.decrement(dec1, ts1)
        cnt2.decrement(dec2, ts2)
        cnt2.merge(cnt1)

        assertEquals(-11, cnt2.get())
    }

    /**
    * This test evaluates the scenario: decrement || merge decrement get.
    * Call to get should return the inverse of the sum of the two decrement values.
    */
    @Test
    fun decrement_mergeDecrementValue() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val dec1 = 10 
        val dec2 = 1 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.decrement(dec1, ts1)
        cnt2.merge(cnt1)
        cnt2.decrement(dec2, ts2)

        assertEquals(-11, cnt2.get())
    }

    /**
    * This test evaluates the scenario: some operations || some operations merge get.
    * Call to get should return the sum of increment values minus the sum of the decrement values.
    */
    @Test
    fun multipleOperations_multipleOperationMergeValue() {
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
        dc1.updateStateTS(ts3)
        val ts5 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts4)
        val ts6 = dc2.getNewTimestamp()
        dc1.updateStateTS(ts5)
        val ts7 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts6)
        val ts8 = dc2.getNewTimestamp()
        val dec1 = 10
        val dec2 = 20
        val dec3 = 30
        val dec4 = 40
        val inc1 = 10
        val inc2 = 30
        val inc3 = 50
        val inc4 = 70
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.decrement(dec1, ts1)
        cnt1.increment(inc1, ts3)
        cnt1.increment(inc2, ts5)
        cnt1.decrement(dec2, ts7)
        cnt2.decrement(dec3, ts2)
        cnt2.increment(inc3, ts4)
        cnt2.increment(inc4, ts6)
        cnt2.decrement(dec4, ts8)
        cnt2.merge(cnt1)

        assertEquals(60, cnt2.get())
    }

    /**
    * This test evaluates the scenario: some operations || merge some operations get.
    * Call to get should return the sum of increment values minus the sum of the decrement values.
    */
    @Test
    fun multipleOperations_mergeMultipleOperationsValue() {
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
        dc1.updateStateTS(ts3)
        val ts5 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts4)
        val ts6 = dc2.getNewTimestamp()
        dc1.updateStateTS(ts5)
        val ts7 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts6)
        val ts8 = dc2.getNewTimestamp()
        val dec1 = 10
        val dec2 = 20
        val dec3 = 30
        val dec4 = 40
        val inc1 = 10
        val inc2 = 30
        val inc3 = 50
        val inc4 = 70
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.decrement(dec1, ts1)
        cnt1.increment(inc1, ts3)
        cnt1.increment(inc2, ts5)
        cnt1.decrement(dec2, ts7)
        cnt2.merge(cnt1)
        cnt2.decrement(dec3, ts2)
        cnt2.increment(inc3, ts4)
        cnt2.increment(inc4, ts6)
        cnt2.decrement(dec4, ts8)

        assertEquals(60, cnt2.get())
    }

    /**
    * This test evaluates the use of delta return by call to increment method.
    * Call to get should return the increment value set in the first replica.
    */
    @Test
    fun incrementOp() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val inc = 11 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        val incOp = cnt1.increment(inc, ts)
        cnt2.merge(incOp)
        cnt1.merge(incOp)

        assertEquals(11, cnt1.get())
        assertEquals(11, cnt2.get())
    }

    /**
    * This test evaluates the use of delta return by call to decrement method.
    * Call to get should return the inverse of the decrement value set in the first replica.
    */
    @Test
    fun decrementOp() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val dec = 11 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        val decOp = cnt1.decrement(dec, ts)
        cnt2.merge(decOp)
        cnt1.merge(decOp)

        assertEquals(-11, cnt1.get())
        assertEquals(-11, cnt2.get())
    }

    /**
    * This test evaluates the use of delta return by call to incremetn and decrement methods.
    * Call to get should return the sum of increment values minus the sum of decrement values.
    */
    @Test
    fun multipleOp() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val dec = 11 
        val inc = 22 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        val decOp = cnt1.decrement(dec, ts1)
        val incOp = cnt1.increment(inc, ts2)
        cnt2.merge(decOp)
        cnt2.merge(incOp)
        cnt1.merge(decOp)
        cnt1.merge(incOp)

        assertEquals(11, cnt1.get())
        assertEquals(11, cnt2.get())
    }

    /*
    * This test evaluates the generation of delta plus its merging into another replica.
    * Call to get should return the values set by operations registered in the first replica after
    * w.r.t the given context (here only the decrements).
    */
    @Test
    fun generateDelta() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val vv = dc.getCurrentState()
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        val inc1 = 11
        val inc2 = 33
        val dec1 = 10
        val dec2 = 20
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.increment(inc1, ts1)
        cnt1.increment(inc2, ts2)
        cnt1.decrement(dec1, ts3)
        cnt1.decrement(dec2, ts4)
        val delta = cnt1.generateDelta(vv)
        cnt2.merge(delta)

        assertEquals(-30, cnt2.get())
    }

    /**
    * This test evaluates JSON serialization of an empty pncounter.
    **/
    @Test
    fun emptyToJsonSerialization() {
        val cnt = PNCounter()

        val cntJson = cnt.toJson();

        assertEquals("""{"_type":"PNCounter","_metadata":{"increment":[],"decrement":[]},"value":0}""", cntJson)
    }

    /**
    * This test evaluates JSON deserialization of an empty pncounter.
    **/
    @Test
    fun emptyFromJsonDeserialization() {
        val cntJson = PNCounter.fromJson("""{"_type":"PNCounter","_metadata":{"increment":[],"decrement":[]},"value":0}""")

        assertEquals(0, cntJson.get())
    }

    /**
    * This test evaluates JSON serialization of a pncounter.
    **/
    @Test
    fun toJsonSerialization() {
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
        val dec1 = 10
        val dec2 = 20
        val inc1 = 10
        val inc2 = 30
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.decrement(dec1, ts1)
        cnt1.increment(inc1, ts3)
        cnt2.decrement(dec2, ts2)
        cnt2.increment(inc2, ts4)
        cnt2.merge(cnt1)

        assertEquals("""{"_type":"PNCounter","_metadata":{"increment":[{"name":"dcid2"},{"first":30,"second":{"uid":{"name":"dcid2"},"cnt":2}},{"name":"dcid1"},{"first":10,"second":{"uid":{"name":"dcid1"},"cnt":2}}],"decrement":[{"name":"dcid2"},{"first":20,"second":{"uid":{"name":"dcid2"},"cnt":1}},{"name":"dcid1"},{"first":10,"second":{"uid":{"name":"dcid1"},"cnt":1}}]},"value":10}""", cnt2.toJson())
    }

    /**
    * This test evaluates JSON deserialization of a pncounter.
    **/
    @Test
    fun fromJsonDeserialization() {
        val cntJson = PNCounter.fromJson("""{"_type":"PNCounter","_metadata":{"increment":[{"name":"dcid2"},{"first":30,"second":{"uid":{"name":"dcid2"},"cnt":2}},{"name":"dcid1"},{"first":10,"second":{"uid":{"name":"dcid1"},"cnt":2}}],"decrement":[{"name":"dcid2"},{"first":20,"second":{"uid":{"name":"dcid2"},"cnt":1}},{"name":"dcid1"},{"first":10,"second":{"uid":{"name":"dcid1"},"cnt":1}}]},"value":10}""")

        assertEquals(10, cntJson.get())
    }
}
