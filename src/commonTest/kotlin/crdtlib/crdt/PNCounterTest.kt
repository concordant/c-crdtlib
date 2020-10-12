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

package crdtlib.crdt

import crdtlib.utils.ClientUId
import crdtlib.utils.SimpleEnvironment
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.*

/**
* Represents a suite test for PNCounter.
**/
class PNCounterTest : StringSpec({

    /**
    * This test evaluates the scenario: get.
    * Call to get should return 0.
    */
    "create and get value" {
        val cnt = PNCounter()

        cnt.get().shouldBe(0)
    }

    /**
    * This test evaluates the scenario: increment get.
    * Call to get should return the value set by increment.
    */
    "increment and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val inc = 10
        val cnt = PNCounter()

        cnt.increment(inc, ts)

        cnt.get().shouldBe(inc)
    }


    /**
    * This test evaluates the scenario: decrement get.
    * Call to get should return the inverse of value set by decrement.
    */
    "decrement and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val dec = 10
        val cnt = PNCounter()

        cnt.decrement(dec, ts)

        cnt.get().shouldBe(-dec)
    }

    /**
    * This test evaluates the scenario: increment(with a negative value) get.
    * Call to get should return the value set by increment.
    */
    "increment with negative amount and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val inc = -10
        val cnt = PNCounter()

        cnt.increment(inc, ts)

        cnt.get().shouldBe(inc)
    }

    /**
    * This test evaluates the scenario: decrement(with a negative value) get.
    * Call to get should return the inverse of value set by decrement.
    */
    "decrement with negative amount and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val dec = -10
        val cnt = PNCounter()

        cnt.decrement(dec, ts)

        cnt.get().shouldBe(-dec)
    }

    /**
    * This test evaluates the scenario: incremement(multiple times) get.
    * Call to get should return the sum of values set by calls to increment.
    */
    "multiple increments and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val inc1 = 10
        val inc2 = 1
        val inc3 = 100
        val cnt = PNCounter()

        cnt.increment(inc1, ts1)
        cnt.increment(inc2, ts2)
        cnt.increment(inc3, ts3)

        cnt.get().shouldBe(111)
    }

    /**
    * This test evaluates the scenario: decremement(multiple times) get.
    * Call to get should return the inverse of the sum of values set by calls to decrement.
    */
    "multiple decrements and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val dec1 = 10
        val dec2 = 1
        val dec3 = 100
        val cnt = PNCounter()

        cnt.decrement(dec1, ts1)
        cnt.decrement(dec2, ts2)
        cnt.decrement(dec3, ts3)

        cnt.get().shouldBe(-111)
    }

    /**
    * This test evaluates the scenario: multiple increment and decrement get.
    * Call to get should return the sum of increments minus the sum of decrements.
    */
    "increment, decrement, get positive value" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val ts4 = client.tick()
        val inc1 = 42
        val inc2 = 34
        val dec1 = 27
        val dec2 = 2
        val cnt = PNCounter()

        cnt.increment(inc1, ts1)
        cnt.decrement(dec1, ts2)
        cnt.increment(inc2, ts3)
        cnt.decrement(dec2, ts4)

        cnt.get().shouldBe(47)
    }

    /**
    * This test evaluates the scenario: multiple increment and decrement get.
    * Call to get should return the sum of increments minus the sum of decrements.
    */
    "increment, decrement, get negative value" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val ts4 = client.tick()
        val inc1 = 42
        val inc2 = 34
        val dec1 = 77
        val dec2 = 13
        val cnt = PNCounter()

        cnt.increment(inc1, ts1)
        cnt.decrement(dec1, ts2)
        cnt.increment(inc2, ts3)
        cnt.decrement(dec2, ts4)

        cnt.get().shouldBe(-14)
    }

    /**
    * This test evaluates the scenario: increment || merge get.
    * Call to get should return value set by increment in the first replica.
    */
    "R1: increment; R2: merge and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val inc = 11 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.increment(inc, ts)
        cnt2.merge(cnt1)
        cnt1.merge(cnt2)

        cnt1.get().shouldBe(11)
        cnt2.get().shouldBe(11)
    }

    /**
    * This test evaluates the scenario: decrement || merge get.
    * Call to get should return the inverse value set by decrement in the first replica.
    */
    "R1: decrement; R2: merge and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val dec = 11 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.decrement(dec, ts)
        cnt2.merge(cnt1)
        cnt1.merge(cnt2)

        cnt1.get().shouldBe(-11)
        cnt2.get().shouldBe(-11)
    }

    /**
    * This test evaluates the scenario: increment || increment merge get.
    * Call to get should return sum of the two increment values.
    */
    "R1: increment; R2: increment, merge, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val inc1 = 10 
        val inc2 = 1 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.increment(inc1, ts1)
        cnt2.increment(inc2, ts2)
        cnt2.merge(cnt1)

        cnt2.get().shouldBe(11)
    }

    /**
    * This test evaluates the scenario: increment || merge increment get.
    * Call to get should return sum of the two increment values.
    */
    "R1: increment; R2: merge, increment, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val inc1 = 10 
        val inc2 = 1 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.increment(inc1, ts1)
        cnt2.merge(cnt1)
        cnt2.increment(inc2, ts2)

        cnt2.get().shouldBe(11)
    }

    /**
    * This test evaluates the scenario: decrement || decrement merge get.
    * Call to get should return the inverse of the sum of the two decrement values.
    */
    "R1: decrement; R2: decrement, merge, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val dec1 = 10 
        val dec2 = 1 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.decrement(dec1, ts1)
        cnt2.decrement(dec2, ts2)
        cnt2.merge(cnt1)

        cnt2.get().shouldBe(-11)
    }

    /**
    * This test evaluates the scenario: decrement || merge decrement get.
    * Call to get should return the inverse of the sum of the two decrement values.
    */
    "R1: decrement; R2: merge, decrement, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val dec1 = 10 
        val dec2 = 1 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        cnt1.decrement(dec1, ts1)
        cnt2.merge(cnt1)
        cnt2.decrement(dec2, ts2)

        cnt2.get().shouldBe(-11)
    }

    /**
    * This test evaluates the scenario: some operations || some operations merge get.
    * Call to get should return the sum of increment values minus the sum of the decrement values.
    */
    "R1: multiple operations; R2: multiple operations, merge, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client1.tick()
        val ts4 = client2.tick()
        val ts5 = client1.tick()
        val ts6 = client2.tick()
        val ts7 = client1.tick()
        val ts8 = client2.tick()
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

        cnt2.get().shouldBe(60)
    }

    /**
    * This test evaluates the scenario: some operations || merge some operations get.
    * Call to get should return the sum of increment values minus the sum of the decrement values.
    */
    "R1: multiple operations; R2: merge, multiple operations, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client1.tick()
        val ts4 = client2.tick()
        val ts5 = client1.tick()
        val ts6 = client2.tick()
        val ts7 = client1.tick()
        val ts8 = client2.tick()
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

        cnt2.get().shouldBe(60)
    }

    /**
    * This test evaluates the use of delta return by call to increment method.
    * Call to get should return the increment value set in the first replica.
    */
    "use delta returned by increment" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val inc = 11 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        val incOp = cnt1.increment(inc, ts)
        cnt2.merge(incOp)
        cnt1.merge(incOp)

        cnt2.get().shouldBe(11) 
    }

    /**
    * This test evaluates the use of delta return by call to decrement method.
    * Call to get should return the inverse of the decrement value set in the first replica.
    */
    "use delta returned by decrement" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val dec = 11 
        val cnt1 = PNCounter()
        val cnt2 = PNCounter()

        val decOp = cnt1.decrement(dec, ts)
        cnt2.merge(decOp)
        cnt1.merge(decOp)

        cnt1.get().shouldBe(-11)
        cnt2.get().shouldBe(-11)
    }

    /**
    * This test evaluates the use of delta return by call to incremetn and decrement methods.
    * Call to get should return the sum of increment values minus the sum of decrement values.
    */
    "use delta returned by increment and decrement" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
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

        cnt1.get().shouldBe(11)
        cnt2.get().shouldBe(11)
    }

    /*
    * This test evaluates the generation of delta plus its merging into another replica.
    * Call to get should return the values set by operations registered in the first replica after
    * w.r.t the given context (here only the decrements).
    */
    "generate delta" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val vv = client.getState()
        val ts3 = client.tick()
        val ts4 = client.tick()
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

        cnt2.get().shouldBe(-30)
    }

    /**
    * This test evaluates JSON serialization of an empty pncounter.
    **/
    "empty JSON serialization" {
        val cnt = PNCounter()

        val cntJson = cnt.toJson();

        cntJson.shouldBe("""{"_type":"PNCounter","_metadata":{"increment":[],"decrement":[]},"value":0}""")
    }

    /**
    * This test evaluates JSON deserialization of an empty pncounter.
    **/
    "empty JSON deserialization" {
        val cntJson = PNCounter.fromJson("""{"_type":"PNCounter","_metadata":{"increment":[],"decrement":[]},"value":0}""")

        cntJson.get().shouldBe(0)
    }

    /**
    * This test evaluates JSON serialization of a pncounter.
    **/
    "JSON serialization" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client1.tick()
        val ts4 = client2.tick()
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
        val cntJson = cnt2.toJson()

        cntJson.shouldBe("""{"_type":"PNCounter","_metadata":{"increment":[{"name":"clientid2"},{"first":30,"second":{"uid":{"name":"clientid2"},"cnt":-2147483646}},{"name":"clientid1"},{"first":10,"second":{"uid":{"name":"clientid1"},"cnt":-2147483646}}],"decrement":[{"name":"clientid2"},{"first":20,"second":{"uid":{"name":"clientid2"},"cnt":-2147483647}},{"name":"clientid1"},{"first":10,"second":{"uid":{"name":"clientid1"},"cnt":-2147483647}}]},"value":10}""")
    }

    /**
    * This test evaluates JSON deserialization of a pncounter.
    **/
    "JSON deserialization" {
        val cntJson = PNCounter.fromJson("""{"_type":"PNCounter","_metadata":{"increment":[{"name":"clientid2"},{"first":30,"second":{"uid":{"name":"clientid2"},"cnt":-2147483646}},{"name":"clientid1"},{"first":10,"second":{"uid":{"name":"clientid1"},"cnt":-2147483646}}],"decrement":[{"name":"clientid2"},{"first":20,"second":{"uid":{"name":"clientid2"},"cnt":-2147483647}},{"name":"clientid1"},{"first":10,"second":{"uid":{"name":"clientid1"},"cnt":-2147483647}}]},"value":10}""")

        cntJson.get().shouldBe(10)
    }
})
