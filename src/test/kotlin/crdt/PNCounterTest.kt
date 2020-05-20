package crdtlib.test

import crdtlib.crdt.PNCounter
import crdtlib.utils.DCId
import crdtlib.utils.SimpleEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

/**
* Represents a suite test for PNCounter.
**/
class PNCounterTest {

    /**
    * This test evaluates the scenario: value.
    * Call to value should return 0.
    */
    @Test
    fun createVal() {
        val cnt = PNCounter()

        assertEquals(0, cnt.value())
    }

    /**
    * This test evaluates the scenario: increment value.
    * Call to value should return the value set by increment.
    */
    @Test
    fun increment() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val inc = 10
        val cnt = PNCounter()

        cnt.increment(inc, ts)

        assertEquals(inc, cnt.value())
    }


    /**
    * This test evaluates the scenario: decrement value.
    * Call to value should return the inverse of value set by decrement.
    */
    @Test
    fun decrement() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val dec = 10
        val cnt = PNCounter()

        cnt.decrement(dec, ts)

        assertEquals(-dec, cnt.value())
    }

    /**
    * This test evaluates the scenario: increment(with a negative value) value.
    * Call to value should return the value set by increment.
    */
    @Test
    fun incrementNegativeAmount() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val inc = -10
        val cnt = PNCounter()

        cnt.increment(inc, ts)

        assertEquals(inc, cnt.value())
    }

    /**
    * This test evaluates the scenario: decrement(with a negative value) value.
    * Call to value should return the inverse of value set by decrement.
    */
    @Test
    fun decrementNegativeAmount() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val dec = -10
        val cnt = PNCounter()

        cnt.decrement(dec, ts)

        assertEquals(-dec, cnt.value())
    }

    /**
    * This test evaluates the scenario: incremement(multiple times) value.
    * Call to value should return the sum of values set by calls to increment.
    */
    @Test
    fun multiIncrement() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
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

        assertEquals(111, cnt.value())
    }

    /**
    * This test evaluates the scenario: decremement(multiple times) value.
    * Call to value should return the inverse of the sum of values set by calls to decrement.
    */
    @Test
    fun multiDecrement() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
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

        assertEquals(-111, cnt.value())
    }

    /**
    * This test evaluates the scenario: multiple increment and decrement value.
    * Call to value should return the sum of increments minus the sum of decrements.
    */
    @Test
    fun incrementDecrementPositive() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
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

        assertEquals(47, cnt.value())
    }

    /**
    * This test evaluates the scenario: multiple increment and decrement value.
    * Call to value should return the sum of increments minus the sum of decrements.
    */
    @Test
    fun incrementDecrementNegative() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
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

        assertEquals(-14, cnt.value())
    }

    /**
    * This test evaluates the scenario: increment || merge value.
    * Call to value should return value set by increment in the first replica.
    */
    @Test
    fun increment_MergeValue() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val inc = 11 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.increment(inc, ts)
        cnt2.merge(cnt1)
        cnt1.merge(cnt2)

        assertEquals(11, cnt1.value())
        assertEquals(11, cnt2.value())
    }

    /**
    * This test evaluates the scenario: decrement || merge value.
    * Call to value should return the inverse value set by decrement in the first replica.
    */
    @Test
    fun decrement_MergeValue() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val dec = 11 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.decrement(dec, ts)
        cnt2.merge(cnt1)
        cnt1.merge(cnt2)

        assertEquals(-11, cnt1.value())
        assertEquals(-11, cnt2.value())
    }

    /**
    * This test evaluates the scenario: increment || increment merge value.
    * Call to value should return sum of the two increment values.
    */
    @Test
    fun increment_incrementMergeValue() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val inc1 = 10 
        val inc2 = 1 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.increment(inc1, ts1)
        cnt2.increment(inc2, ts2)
        cnt2.merge(cnt1)

        assertEquals(11, cnt2.value())
    }

    /**
    * This test evaluates the scenario: increment || merge increment value.
    * Call to value should return sum of the two increment values.
    */
    @Test
    fun increment_mergeIncrementValue() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val inc1 = 10 
        val inc2 = 1 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.increment(inc1, ts1)
        cnt2.merge(cnt1)
        cnt2.increment(inc2, ts2)

        assertEquals(11, cnt2.value())
    }

    /**
    * This test evaluates the scenario: decrement || decrement merge value.
    * Call to value should return the inverse of the sum of the two decrement values.
    */
    @Test
    fun decrement_decrementMergeValue() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val dec1 = 10 
        val dec2 = 1 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.decrement(dec1, ts1)
        cnt2.decrement(dec2, ts2)
        cnt2.merge(cnt1)

        assertEquals(-11, cnt2.value())
    }

    /**
    * This test evaluates the scenario: decrement || merge decrement value.
    * Call to value should return the inverse of the sum of the two decrement values.
    */
    @Test
    fun decrement_mergeDecrementValue() {
        val id1 = DCId("dcid1")
        val id2 = DCId("dcid2")
        val dc1 = SimpleEnvironment(id1)
        val dc2 = SimpleEnvironment(id2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val dec1 = 10 
        val dec2 = 1 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.decrement(dec1, ts1)
        cnt2.merge(cnt1)
        cnt2.decrement(dec2, ts2)

        assertEquals(-11, cnt2.value())
    }

    /**
    * This test evaluates the scenario: some operations || some operations merge value.
    * Call to value should return the sum of increment values minus the sum of the decrement values.
    */
    @Test
    fun multipleOperations_multipleOperationMergeValue() {
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

        assertEquals(60, cnt2.value())
    }

    /**
    * This test evaluates the scenario: some operations || merge some operations value.
    * Call to value should return the sum of increment values minus the sum of the decrement values.
    */
    @Test
    fun multipleOperations_mergeMultipleOperationsValue() {
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

        assertEquals(60, cnt2.value())
    }

    /**
    * This test evaluates the use of delta return by call to increment method.
    * Call to value should return the increment value set in the first replica.
    */
    @Test
    fun incrementOp() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val inc = 11 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        val incOp = cnt1.increment(inc, ts)
        cnt2.merge(incOp)
        cnt1.merge(incOp)

        assertEquals(11, cnt1.value())
        assertEquals(11, cnt2.value())
    }

    /**
    * This test evaluates the use of delta return by call to decrement method.
    * Call to value should return the inverse of the decrement value set in the first replica.
    */
    @Test
    fun decrementOp() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
        val ts = dc.getNewTimestamp()
        val dec = 11 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        val decOp = cnt1.decrement(dec, ts)
        cnt2.merge(decOp)
        cnt1.merge(decOp)

        assertEquals(-11, cnt1.value())
        assertEquals(-11, cnt2.value())
    }

    /**
    * This test evaluates the use of delta return by call to incremetn and decrement methods.
    * Call to value should return the sum of increment values minus the sum of decrement values.
    */
    @Test
    fun multipleOp() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
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

        assertEquals(11, cnt1.value())
        assertEquals(11, cnt2.value())
    }

    /*
    * This test evaluates the generation of delta plus its merging into another replica.
    * Call to value should return the values set by operations registered in the first replica after
    * w.r.t the given context (here only the decrements).
    */
    @Test
    fun generateDelta() {
        val id = DCId("dcid")
        val dc = SimpleEnvironment(id)
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

        assertEquals(-30, cnt2.value())
    }

    /**
    * This test evaluates JSON serialization.
    **/
    @Test
    fun toJsonSerialization() {
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

        assertEquals("""{"_metadata":{"increment":[{"name":"dcid2"},{"first":30,"second":{"id":{"name":"dcid2"},"cnt":2}},{"name":"dcid1"},{"first":10,"second":{"id":{"name":"dcid1"},"cnt":2}}],"decrement":[{"name":"dcid2"},{"first":20,"second":{"id":{"name":"dcid2"},"cnt":1}},{"name":"dcid1"},{"first":10,"second":{"id":{"name":"dcid1"},"cnt":1}}]},"value":10}""", cnt2.toJson())
    }

    /**
    * This test evaluates JSON deserialization.
    **/
    @Test
    fun fromJsonDeserialization() {
        val cntJson = PNCounter.fromJson("""{"_metadata":{"increment":[{"name":"dcid2"},{"first":30,"second":{"id":{"name":"dcid2"},"cnt":2}},{"name":"dcid1"},{"first":10,"second":{"id":{"name":"dcid1"},"cnt":2}}],"decrement":[{"name":"dcid2"},{"first":20,"second":{"id":{"name":"dcid2"},"cnt":1}},{"name":"dcid1"},{"first":10,"second":{"id":{"name":"dcid1"},"cnt":1}}]},"value":10}""")

        assertEquals(10, cntJson.value())
    }
}
