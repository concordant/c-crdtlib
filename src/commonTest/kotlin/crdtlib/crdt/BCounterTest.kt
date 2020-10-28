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
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.shouldBe

/**
* Represents a suite test for BCounter.
**/
class BCounterTest : StringSpec({
    /**
     * This test evaluates the scenario: get.
     * Call to get should return the lower bound 10.
     */
    "create and get value" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val bound = 10

        val cnt = BCounter(bound)

        cnt.get().shouldBe(bound)
    }

    /**
     * This test evaluates the scenario: localRights.
     * Call to localRights should return the lower bound 10.
     */
    "create and localRights value" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val bound = 10

        val cnt = BCounter(bound)

        cnt.localRights(uid).shouldBeZero()
    }

    /**
     * This test evaluates the scenario: increment get.
     * Call to get should return the lower bound + the value set by increment.
     */
    "increment and get/localRights" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val bound = 10
        val inc = 10

        val cnt = BCounter(bound)

        cnt.increment(inc, ts)

        cnt.get().shouldBe(bound + inc)
        cnt.localRights(uid).shouldBe(inc)
    }

    /**
     * This test evaluates the scenario: increment get.
     * Call to get should return the lower bound + the value set by increment.
     */
    "increment, decrement OK and get/localRights" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val bound = 10
        val inc = 10
        val dec = 5

        val cnt = BCounter(bound)

        cnt.increment(inc, ts1)
        cnt.decrement(dec, ts2)

        cnt.get().shouldBe(bound + inc - dec)
        cnt.localRights(uid).shouldBe(inc - dec)
    }

    "increment, decrement KO and get/localRights" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val bound = 10
        val inc = 10
        val dec = 15

        val cnt = BCounter(bound)

        cnt.increment(inc, ts1)
        shouldThrow<IllegalArgumentException> {
            cnt.decrement(dec, ts2)
        }
    }

    /**
     * This test evaluates the scenario: increment(with a negative value) get.
     * Call to get should return the value set by increment.
     */
    "increment with positif amount, increment with negative amount and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val bound = 10
        val inc1 = 10
        val inc2 = -5
        val cnt = BCounter(bound)

        cnt.increment(inc1, ts1)
        cnt.increment(inc2, ts2)

        cnt.get().shouldBe(bound + inc1 + inc2)
        cnt.localRights(uid).shouldBe(inc1 + inc2)
    }

    /**
     * This test evaluates the scenario: decrement(with a negative value) get.
     * Call to get should return the inverse of value set by decrement.
     */
    "decrement with negative amount and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val bound = 10
        val dec = -5
        val cnt = BCounter(bound)

        cnt.decrement(dec, ts)

        cnt.get().shouldBe(bound - dec)
        cnt.localRights(uid).shouldBe(-dec)
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
        val bound = 10
        val inc1 = 10
        val inc2 = 1
        val inc3 = 100
        val cnt = BCounter(bound)

        cnt.increment(inc1, ts1)
        cnt.increment(inc2, ts2)
        cnt.increment(inc3, ts3)

        cnt.get().shouldBe(bound + 111)
        cnt.localRights(uid).shouldBe(111)
    }

    /**
     * This test evaluates the scenario: decremement(multiple times) get.
     * Call to get should return the inverse of the sum of values set by calls to decrement.
     */
    "one increment, multiple decrements and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts0 = client.tick()
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val bound = 10
        val inc = 10
        val dec1 = 5
        val dec2 = 1
        val dec3 = 2
        val cnt = BCounter(bound)

        cnt.increment(inc, ts0)
        cnt.decrement(dec1, ts1)
        cnt.decrement(dec2, ts2)
        cnt.decrement(dec3, ts3)

        cnt.get().shouldBe(bound + 2)
        cnt.localRights(uid).shouldBe(2)
    }

    "one increment, multiple decrements and get KO" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts0 = client.tick()
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val bound = 10
        val inc = 10
        val dec1 = 5
        val dec2 = 1
        val dec3 = 20
        val cnt = BCounter(bound)

        cnt.increment(inc, ts0)
        cnt.decrement(dec1, ts1)
        cnt.decrement(dec2, ts2)
        shouldThrow<IllegalArgumentException> {
            cnt.decrement(dec3, ts3)
        }
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
        val bound = 10
        val inc1 = 42
        val inc2 = 34
        val dec1 = 27
        val dec2 = 2
        val cnt = BCounter(bound)

        cnt.increment(inc1, ts1)
        cnt.decrement(dec1, ts2)
        cnt.increment(inc2, ts3)
        cnt.decrement(dec2, ts4)

        cnt.get().shouldBe(bound + 47)
        cnt.localRights(uid).shouldBe(47)
    }

    /**
     * This test evaluates the scenario: multiple increment and decrement get.
     * Call to get should return the sum of increments minus the sum of decrements.
     */
    "increment, decrement, get exception" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val ts4 = client.tick()
        val bound = 10
        val inc1 = 42
        val inc2 = 34
        val dec1 = 13
        val dec2 = 77
        val cnt = BCounter(bound)

        cnt.increment(inc1, ts1)
        cnt.decrement(dec1, ts2)
        cnt.increment(inc2, ts3)
        shouldThrow<IllegalArgumentException> {
            cnt.decrement(dec2, ts4)
        }
    }

    /**
     * This test evaluates the scenario: increment || merge get.
     * Call to get should return value set by increment in the first replica.
     */
    "R1: increment; R2: merge and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val bound = 10
        val inc = 11
        val cnt1 = BCounter(bound)
        val cnt2 = BCounter(bound)

        cnt1.increment(inc, ts)
        cnt2.merge(cnt1)
        cnt1.merge(cnt2)

        cnt1.get().shouldBe(bound + 11)
        cnt2.get().shouldBe(bound + 11)
        cnt1.localRights(uid).shouldBe(11)
        cnt2.localRights(uid).shouldBe(11)
    }

    /**
     * This test evaluates the scenario: increment || merge from different BCounter.
     * Call to get should raise an exception.
     */
    "R1: increment; R2: merge from different BCounter" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val bound = 10
        val inc = 11
        val cnt1 = BCounter(bound)
        val cnt2 = BCounter(2 * bound)

        cnt1.increment(inc, ts)
        shouldThrow<IllegalArgumentException> {
            cnt2.merge(cnt1)
        }
    }

    /**
     * This test evaluates the scenario: decrement || merge get.
     * Call to get should return the inverse value set by decrement in the first replica.
     */
    "R1: increment, decrement; R2: merge and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val bound = 10
        val inc = 20
        val dec = 11
        val cnt1 = BCounter(bound)
        val cnt2 = BCounter(bound)

        cnt1.increment(inc, ts1)
        cnt1.decrement(dec, ts2)
        cnt2.merge(cnt1)
        cnt1.merge(cnt2)

        cnt1.get().shouldBe(bound + 9)
        cnt2.get().shouldBe(bound + 9)
        cnt1.localRights(uid).shouldBe(9)
        cnt2.localRights(uid).shouldBe(9)
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
        val bound = 10
        val inc1 = 10
        val inc2 = 1
        val cnt1 = BCounter(bound)
        val cnt2 = BCounter(bound)

        cnt1.increment(inc1, ts1)
        cnt2.increment(inc2, ts2)
        cnt2.merge(cnt1)

        cnt2.get().shouldBe(bound + 11)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(1)
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
        val bound = 10
        val inc1 = 10
        val inc2 = 1
        val cnt1 = BCounter(bound)
        val cnt2 = BCounter(bound)

        cnt1.increment(inc1, ts1)
        cnt2.merge(cnt1)
        cnt2.increment(inc2, ts2)

        cnt2.get().shouldBe(bound + 11)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(1)
    }

    /**
     * This test evaluates the scenario: decrement || decrement merge get.
     * Call to get should return the inverse of the sum of the two decrement values.
     */
    "R1: increment, decrement; R2: increment, decrement, merge, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client1.tick()
        val ts4 = client2.tick()
        val bound = 10
        val inc1 = 15
        val inc2 = 5
        val dec1 = 10
        val dec2 = 1
        val cnt1 = BCounter(bound)
        val cnt2 = BCounter(bound)

        cnt1.increment(inc1, ts1)
        cnt2.increment(inc2, ts2)
        cnt1.decrement(dec1, ts3)
        cnt2.decrement(dec2, ts4)
        cnt2.merge(cnt1)

        cnt2.get().shouldBe(bound + 9)
        cnt1.localRights(uid1).shouldBe(5)
        cnt2.localRights(uid2).shouldBe(4)
    }

    /**
     * This test evaluates the scenario: decrement || merge decrement get.
     * Call to get should return the inverse of the sum of the two decrement values.
     */
    "R1: increment, decrement; R2: merge, decrement, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client1.tick()
        val ts3 = client2.tick()
        val bound = 10
        val inc = 15
        val dec1 = 10
        val dec2 = 1
        val cnt1 = BCounter(bound)
        val cnt2 = BCounter(bound)

        cnt1.increment(inc, ts1)
        cnt1.decrement(dec1, ts2)
        cnt2.merge(cnt1)
        shouldThrow<IllegalArgumentException> {
            cnt2.decrement(dec2, ts3)
        }
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
        val bound = 10
        val dec1 = 10
        val dec2 = 20
        val dec3 = 30
        val dec4 = 40
        val inc1 = 10
        val inc2 = 30
        val inc3 = 50
        val inc4 = 70
        val cnt1 = BCounter(bound)
        val cnt2 = BCounter(bound)

        cnt1.increment(inc1, ts1)
        cnt1.decrement(dec1, ts3)
        cnt1.increment(inc2, ts5)
        cnt1.decrement(dec2, ts7)
        cnt2.increment(inc3, ts2)
        cnt2.decrement(dec3, ts4)
        cnt2.increment(inc4, ts6)
        cnt2.decrement(dec4, ts8)
        cnt2.merge(cnt1)

        cnt2.get().shouldBe(bound + 60)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(50)
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
        val bound = 10
        val dec1 = 10
        val dec2 = 20
        val dec3 = 30
        val dec4 = 40
        val inc1 = 10
        val inc2 = 30
        val inc3 = 50
        val inc4 = 70
        val cnt1 = BCounter(bound)
        val cnt2 = BCounter(bound)

        cnt1.increment(inc1, ts1)
        cnt1.decrement(dec1, ts3)
        cnt1.increment(inc2, ts5)
        cnt1.decrement(dec2, ts7)
        cnt2.merge(cnt1)
        cnt2.increment(inc3, ts2)
        cnt2.decrement(dec3, ts4)
        cnt2.increment(inc4, ts6)
        cnt2.decrement(dec4, ts8)

        cnt2.get().shouldBe(bound + 60)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(50)
    }

    /**
     * This test evaluates the use of delta return by call to increment method.
     * Call to get should return the increment value set in the first replica.
     */
    "use delta returned by increment" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val bound = 10
        val inc = 11
        val cnt1 = BCounter(bound)
        val cnt2 = BCounter(bound)

        val incOp = cnt1.increment(inc, ts)
        cnt2.merge(incOp)
        cnt1.merge(incOp)

        cnt2.get().shouldBe(bound + 11)
        cnt1.localRights(uid).shouldBe(11)
        cnt2.localRights(uid).shouldBe(11)
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
        val bound = 10
        val inc = 15
        val dec = 11
        val cnt1 = BCounter(bound)
        val cnt2 = BCounter(bound)

        val incOp = cnt1.increment(inc, ts1)
        val decOp = cnt1.decrement(dec, ts2)
        cnt2.merge(incOp)
        cnt1.merge(incOp)
        cnt2.merge(decOp)
        cnt1.merge(decOp)

        cnt1.get().shouldBe(bound + 4)
        cnt2.get().shouldBe(bound + 4)
        cnt1.localRights(uid).shouldBe(4)
        cnt2.localRights(uid).shouldBe(4)
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
        val bound = 10
        val inc1 = 11
        val inc2 = 33
        val dec1 = 10
        val dec2 = 20
        val cnt1 = BCounter(bound)
        val cnt2 = BCounter(bound)

        cnt1.increment(inc1, ts1)
        cnt1.increment(inc2, ts2)
        cnt2.merge(cnt1)
        cnt1.decrement(dec1, ts3)
        cnt1.decrement(dec2, ts4)
        val delta = cnt1.generateDelta(vv)
        cnt2.merge(delta)

        cnt2.get().shouldBe(bound + 14)
        cnt2.localRights(uid).shouldBe(14)
    }

    "one transfer, one way" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client1.tick()

        val bound = 10
        val inc = 10
        val trans1 = 5

        val cnt1 = BCounter(bound)
        val cnt2 = BCounter(bound)

        cnt1.increment(inc, ts1)
        cnt1.transfer(trans1, uid2, ts2).shouldBeTrue()
        cnt2.merge(cnt1)
        cnt1.get().shouldBe(bound + 10)
        cnt2.get().shouldBe(bound + 10)
        cnt1.localRights(uid1).shouldBe(inc - trans1)
        cnt2.localRights(uid2).shouldBe(trans1)
    }

    "two transfer, one way" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client1.tick()
        val ts3 = client1.tick()

        val bound = 10
        val inc = 10
        val trans1 = 6
        val trans2 = 4

        val cnt1 = BCounter(bound)
        val cnt2 = BCounter(bound)

        cnt1.increment(inc, ts1)
        cnt1.transfer(trans1, uid2, ts2).shouldBeTrue()
        cnt2.merge(cnt1)
        cnt1.get().shouldBe(bound + 10)
        cnt2.get().shouldBe(bound + 10)
        cnt1.localRights(uid1).shouldBe(inc - trans1)
        cnt2.localRights(uid2).shouldBe(trans1)

        cnt1.transfer(trans2, uid2, ts3).shouldBeTrue()
        cnt2.merge(cnt1)
        cnt1.get().shouldBe(bound + 10)
        cnt2.get().shouldBe(bound + 10)
        cnt1.localRights(uid1).shouldBe(inc - trans1 - trans2)
        cnt2.localRights(uid2).shouldBe(trans1 + trans2)
    }

    "two transfer, one way, not enough rights" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client1.tick()
        val ts4 = client1.tick()

        val bound = 10
        val inc1 = 10
        val inc2 = 30

        val cnt1 = BCounter(bound)
        val cnt2 = BCounter(bound)

        cnt1.increment(inc1, ts1)
        cnt2.increment(inc2, ts2)
        cnt2.merge(cnt1)
        cnt1.merge(cnt2)

        cnt1.get().shouldBe(bound + 40)
        cnt2.get().shouldBe(bound + 40)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(30)

        cnt1.transfer(5, uid2, ts3).shouldBeTrue()
        cnt2.merge(cnt1)
        cnt1.get().shouldBe(bound + 40)
        cnt2.get().shouldBe(bound + 40)
        cnt1.localRights(uid1).shouldBe(5)
        cnt2.localRights(uid2).shouldBe(35)

        cnt1.transfer(10, uid2, ts4).shouldBeFalse()
        cnt1.get().shouldBe(bound + 40)
        cnt2.get().shouldBe(bound + 40)
        cnt1.localRights(uid1).shouldBe(5)
    }

    "two way transfer" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client1.tick()
        val ts4 = client2.tick()

        val bound = 10
        val inc1 = 10
        val inc2 = 30

        val cnt1 = BCounter(bound)
        val cnt2 = BCounter(bound)

        cnt1.increment(inc1, ts1)
        cnt2.increment(inc2, ts2)
        cnt2.merge(cnt1)
        cnt1.merge(cnt2)

        cnt1.get().shouldBe(bound + 40)
        cnt2.get().shouldBe(bound + 40)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(30)

        cnt1.transfer(5, uid2, ts3).shouldBeTrue()
        cnt2.merge(cnt1)
        cnt1.get().shouldBe(bound + 40)
        cnt2.get().shouldBe(bound + 40)
        cnt1.localRights(uid1).shouldBe(5)
        cnt2.localRights(uid2).shouldBe(35)

        cnt2.transfer(20, uid1, ts4).shouldBeTrue()
        cnt1.merge(cnt2)
        cnt1.get().shouldBe(bound + 40)
        cnt2.get().shouldBe(bound + 40)
        cnt1.localRights(uid1).shouldBe(25)
        cnt2.localRights(uid2).shouldBe(15)
    }

//    /**
//     * This test evaluates JSON serialization of an empty bcounter.
//     **/
//    "empty JSON serialization" {
//        val bound = 10
//        val cnt = BCounter(bound)
//
//        val cntJson = cnt.toJson()
//        cntJson.shouldBe("""{"_type":"BCounter","_metadata":{"bound":10,"increment":[],"decrement":[]},"value":10}""")
//    }
//
//    /**
//     * This test evaluates JSON deserialization of an empty bcounter.
//     **/
//    "empty JSON deserialization" {
//        val cntJson = BCounter.fromJson("""{"_type":"BCounter","_metadata":{"bound":10,"increment":[],"decrement":[]},"value":10}""")
//
//        cntJson.get().shouldBe(10)
//    }

    /**
     * This test evaluates JSON serialization of a bcounter.
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
        val ts5 = client2.tick()
        val bound = 10
        val inc1 = 10
        val inc2 = 30
        val dec1 = 5
        val dec2 = 20
        val cnt1 = BCounter(bound)
        val cnt2 = BCounter(bound)

        cnt1.increment(inc1, ts1)
        cnt1.decrement(dec1, ts3)
        cnt2.increment(inc2, ts2)
        cnt2.decrement(dec2, ts4)
        cnt2.transfer(2, uid1, ts5)
        cnt1.merge(cnt2)
        val cntJson = cnt1.toJson()

        cntJson.shouldBe("""{"_type":"BCounter","_metadata":{"bound":10,"increment":[{"name":"clientid1"},[{"name":"clientid1"},{"first":10,"second":{"uid":{"name":"clientid1"},"cnt":-2147483647}}],{"name":"clientid2"},[{"name":"clientid2"},{"first":30,"second":{"uid":{"name":"clientid2"},"cnt":-2147483647}},{"name":"clientid1"},{"first":2,"second":{"uid":{"name":"clientid2"},"cnt":-2147483645}}]],"decrement":[{"name":"clientid1"},{"first":5,"second":{"uid":{"name":"clientid1"},"cnt":-2147483646}},{"name":"clientid2"},{"first":20,"second":{"uid":{"name":"clientid2"},"cnt":-2147483646}}]},"value":25}""")
    }

    /**
     * This test evaluates JSON deserialization of a bcounter.
     **/
    "JSON deserialization" {
        val cntJson = BCounter.fromJson("""{"_type":"BCounter","_metadata":{"bound":10,"increment":[{"name":"clientid1"},[{"name":"clientid1"},{"first":10,"second":{"uid":{"name":"clientid1"},"cnt":-2147483647}}],{"name":"clientid2"},[{"name":"clientid2"},{"first":30,"second":{"uid":{"name":"clientid2"},"cnt":-2147483647}},{"name":"clientid1"},{"first":2,"second":{"uid":{"name":"clientid2"},"cnt":-2147483645}}]],"decrement":[{"name":"clientid1"},{"first":5,"second":{"uid":{"name":"clientid1"},"cnt":-2147483646}},{"name":"clientid2"},{"first":20,"second":{"uid":{"name":"clientid2"},"cnt":-2147483646}}]},"value":25}""")

        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        cntJson.get().shouldBe(25)
        cntJson.localRights(uid1).shouldBe(7)
        cntJson.localRights(uid2).shouldBe(8)
    }
})
