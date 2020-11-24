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
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.shouldBe

/**
* Represents a suite test for BCounter.
**/
class BCounterTest : StringSpec({
    "create and get value" {
        val cnt = BCounter()

        cnt.get().shouldBeZero()
    }

    "create and localRights value" {
        val uid = ClientUId("clientid")

        val cnt = BCounter()

        cnt.localRights(uid).shouldBeZero()
    }

    "increment and get/localRights" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val inc = 10
        val cnt = BCounter()

        cnt.increment(inc, ts)

        cnt.get().shouldBe(10)
        cnt.localRights(uid).shouldBe(10)
    }

    "failing decrement and get/localRights" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val dec = 10
        val cnt = BCounter()

        shouldThrow<IllegalArgumentException> {
            cnt.decrement(dec, ts)
        }

        cnt.get().shouldBeZero()
        cnt.localRights(uid).shouldBeZero()
    }

    "increment decrement and get/localRights" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val inc = 15
        val dec = 6
        val cnt = BCounter()

        cnt.increment(inc, ts1)
        cnt.decrement(dec, ts2)

        cnt.get().shouldBe(9)
        cnt.localRights(uid).shouldBe(9)
    }

    "failing negative increment and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val inc = -15
        val cnt = BCounter()

        shouldThrow<IllegalArgumentException> {
            cnt.increment(inc, ts)
        }

        cnt.get().shouldBeZero()
        cnt.localRights(uid).shouldBeZero()
    }

    "positive increment, negative increment and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val inc1 = 15
        val inc2 = -8
        val cnt = BCounter()

        cnt.increment(inc1, ts1)
        cnt.increment(inc2, ts2)

        cnt.get().shouldBe(7)
        cnt.localRights(uid).shouldBe(7)
    }

    "negative decrement and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val dec = -15
        val cnt = BCounter()

        cnt.decrement(dec, ts)

        cnt.get().shouldBe(15)
        cnt.localRights(uid).shouldBe(15)
    }

    "multiple increments and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val inc1 = 10
        val inc2 = 1
        val inc3 = 100
        val cnt = BCounter()

        cnt.increment(inc1, ts1)
        cnt.increment(inc2, ts2)
        cnt.increment(inc3, ts3)

        cnt.get().shouldBe(111)
        cnt.localRights(uid).shouldBe(111)
    }

    "multiple failing decrements and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val dec1 = 10
        val dec2 = 1
        val dec3 = 100
        val cnt = BCounter()

        shouldThrow<IllegalArgumentException> {
            cnt.decrement(dec1, ts1)
        }
        shouldThrow<IllegalArgumentException> {
            cnt.decrement(dec2, ts2)
        }
        shouldThrow<IllegalArgumentException> {
            cnt.decrement(dec3, ts3)
        }

        cnt.get().shouldBeZero()
        cnt.localRights(uid).shouldBeZero()
    }

    "one increment, multiple decrements and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val ts4 = client.tick()
        val inc = 150
        val dec1 = 10
        val dec2 = 1
        val dec3 = 100
        val cnt = BCounter()

        cnt.increment(inc, ts1)
        cnt.decrement(dec1, ts2)
        cnt.decrement(dec2, ts3)
        cnt.decrement(dec3, ts4)

        cnt.get().shouldBe(39)
        cnt.localRights(uid).shouldBe(39)
    }

    "multiple (increment, decrement)" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val ts4 = client.tick()
        val ts5 = client.tick()
        val inc1 = 15
        val inc2 = 40
        val dec1 = 5
        val dec2 = 2
        val dec3 = 50
        val cnt = BCounter()

        cnt.increment(inc1, ts1)
        cnt.get().shouldBe(15)
        cnt.localRights(uid).shouldBe(15)

        cnt.decrement(dec1, ts2)
        cnt.get().shouldBe(10)
        cnt.localRights(uid).shouldBe(10)

        cnt.increment(inc2, ts3)
        cnt.get().shouldBe(50)
        cnt.localRights(uid).shouldBe(50)

        cnt.decrement(dec2, ts4)
        cnt.get().shouldBe(48)
        cnt.localRights(uid).shouldBe(48)

        shouldThrow<IllegalArgumentException> {
            cnt.decrement(dec3, ts5)
        }
    }

    /* Merging */

    "R1: increment; R2: merge and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val inc = 11
        val cnt1 = BCounter()
        val cnt2 = BCounter()

        cnt1.increment(inc, ts)
        cnt2.merge(cnt1)
        cnt1.merge(cnt2)

        cnt1.get().shouldBe(11)
        cnt2.get().shouldBe(11)
        cnt1.localRights(uid).shouldBe(11)
        cnt2.localRights(uid).shouldBe(11)
    }

    "R1: increment; R2: increment, merge, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val inc1 = 10
        val inc2 = 1
        val cnt1 = BCounter()
        val cnt2 = BCounter()

        cnt1.increment(inc1, ts1)
        cnt2.increment(inc2, ts2)
        cnt2.merge(cnt1)

        cnt2.get().shouldBe(11)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(1)
    }

    "R1: increment; R2: merge, increment, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val inc1 = 10
        val inc2 = 1
        val cnt1 = BCounter()
        val cnt2 = BCounter()

        cnt1.increment(inc1, ts1)
        cnt2.merge(cnt1)
        cnt2.increment(inc2, ts2)

        cnt2.get().shouldBe(11)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(1)
    }

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
        val ts9 = client1.tick()
        val ts10 = client2.tick()
        val dec1 = 10
        val dec2 = 20
        val dec3 = 30
        val dec4 = 40
        val dec5 = 20
        val dec6 = 60
        val inc1 = 10
        val inc2 = 30
        val inc3 = 50
        val inc4 = 70
        val cnt1 = BCounter()
        val cnt2 = BCounter()

        cnt1.increment(inc1, ts1)
        cnt1.decrement(dec1, ts3)
        cnt1.increment(inc2, ts5)
        cnt1.decrement(dec2, ts7)
        cnt2.increment(inc3, ts2)
        cnt2.decrement(dec3, ts4)
        cnt2.increment(inc4, ts6)
        cnt2.decrement(dec4, ts8)
        cnt2.merge(cnt1)

        cnt2.get().shouldBe(60)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(50)

        shouldThrow<IllegalArgumentException> {
            cnt1.decrement(dec5, ts9)
        }
        shouldThrow<IllegalArgumentException> {
            cnt2.decrement(dec6, ts10)
        }
    }

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
        val cnt1 = BCounter()
        val cnt2 = BCounter()

        cnt1.increment(inc1, ts1)
        cnt1.decrement(dec1, ts3)
        cnt1.increment(inc2, ts5)
        cnt1.decrement(dec2, ts7)
        cnt2.merge(cnt1)
        cnt2.increment(inc3, ts2)
        cnt2.decrement(dec3, ts4)
        cnt2.increment(inc4, ts6)
        cnt2.decrement(dec4, ts8)

        cnt2.get().shouldBe(60)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(50)
    }

    /* Deltas */

    "use delta returned by increment" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val inc = 11
        val cnt1 = BCounter()
        val cnt2 = BCounter()

        val incOp = cnt1.increment(inc, ts)
        cnt2.merge(incOp)
        cnt1.merge(incOp)

        cnt2.get().shouldBe(11)
        cnt1.localRights(uid).shouldBe(11)
        cnt2.localRights(uid).shouldBe(11)
    }

    "use delta returned by increment and decrement" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val inc = 15
        val dec = 11
        val cnt1 = BCounter()
        val cnt2 = BCounter()

        val incOp = cnt1.increment(inc, ts1)
        val decOp = cnt1.decrement(dec, ts2)
        cnt2.merge(incOp)
        cnt1.merge(incOp)
        cnt2.merge(decOp)
        cnt1.merge(decOp)

        cnt1.get().shouldBe(4)
        cnt2.get().shouldBe(4)
        cnt1.localRights(uid).shouldBe(4)
        cnt2.localRights(uid).shouldBe(4)
    }

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
        val cnt1 = BCounter()
        val cnt2 = BCounter()

        cnt1.increment(inc1, ts1)
        cnt1.increment(inc2, ts2)
        cnt2.merge(cnt1)
        cnt1.decrement(dec1, ts3)
        cnt1.decrement(dec2, ts4)
        val delta = cnt1.generateDelta(vv)
        cnt2.merge(delta)

        cnt2.get().shouldBe(14)
        cnt2.localRights(uid).shouldBe(14)
    }

    /* Rights transfer */

    "rights transfer, one way" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val ts1 = client1.tick()
        val ts2 = client1.tick()

        val inc = 20
        val trans1 = 5

        val cnt1 = BCounter()
        val cnt2 = BCounter()

        cnt1.increment(inc, ts1)
        cnt1.transfer(trans1, uid2, ts2)
        cnt2.merge(cnt1)
        cnt1.get().shouldBe(20)
        cnt2.get().shouldBe(20)
        cnt1.localRights(uid1).shouldBe(15)
        cnt2.localRights(uid2).shouldBe(5)
    }

    "two rights transfers, one way" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val ts1 = client1.tick()
        val ts2 = client1.tick()
        val ts3 = client1.tick()

        val inc = 10
        val trans1 = 6
        val trans2 = 4

        val cnt1 = BCounter()
        val cnt2 = BCounter()

        cnt1.increment(inc, ts1)
        cnt1.transfer(trans1, uid2, ts2)
        cnt2.merge(cnt1)
        cnt1.get().shouldBe(10)
        cnt2.get().shouldBe(10)
        cnt1.localRights(uid1).shouldBe(inc - trans1)
        cnt2.localRights(uid2).shouldBe(trans1)

        cnt1.transfer(trans2, uid2, ts3)
        cnt2.merge(cnt1)
        cnt1.get().shouldBe(10)
        cnt2.get().shouldBe(10)
        cnt1.localRights(uid1).shouldBe(inc - trans1 - trans2)
        cnt2.localRights(uid2).shouldBe(trans1 + trans2)
    }

    "two rights transfers (one failing), one way" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client1.tick()
        val ts4 = client1.tick()

        val inc1 = 10
        val inc2 = 30

        val cnt1 = BCounter()
        val cnt2 = BCounter()

        cnt1.increment(inc1, ts1)
        cnt2.increment(inc2, ts2)
        cnt2.merge(cnt1)
        cnt1.merge(cnt2)

        cnt1.get().shouldBe(40)
        cnt2.get().shouldBe(40)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(30)

        cnt1.transfer(5, uid2, ts3)
        cnt2.merge(cnt1)
        cnt1.get().shouldBe(40)
        cnt2.get().shouldBe(40)
        cnt1.localRights(uid1).shouldBe(5)
        cnt2.localRights(uid2).shouldBe(35)

        shouldThrow<IllegalArgumentException> {
            cnt1.transfer(10, uid2, ts4)
        }
        cnt1.get().shouldBe(40)
        cnt2.get().shouldBe(40)
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

        val inc1 = 10
        val inc2 = 30

        val cnt1 = BCounter()
        val cnt2 = BCounter()

        cnt1.increment(inc1, ts1)
        cnt2.increment(inc2, ts2)
        cnt2.merge(cnt1)
        cnt1.merge(cnt2)

        cnt1.get().shouldBe(40)
        cnt2.get().shouldBe(40)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(30)

        cnt1.transfer(5, uid2, ts3)
        cnt2.merge(cnt1)
        cnt1.get().shouldBe(40)
        cnt2.get().shouldBe(40)
        cnt1.localRights(uid1).shouldBe(5)
        cnt2.localRights(uid2).shouldBe(35)

        cnt2.transfer(20, uid1, ts4)
        cnt1.merge(cnt2)
        cnt1.get().shouldBe(40)
        cnt2.get().shouldBe(40)
        cnt1.localRights(uid1).shouldBe(25)
        cnt2.localRights(uid2).shouldBe(15)
    }

    /* Overflow errors */

    "overflowing increment" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)

        val cnt = BCounter()

        cnt.increment(Int.MAX_VALUE, client.tick())

        shouldThrow<ArithmeticException> {
            cnt.increment(1, client.tick())
        }

        cnt.get().shouldBe(Int.MAX_VALUE)
    }

    "R1: increment; R2: increment, merge, overflowing get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)

        val cnt1 = BCounter()
        val cnt2 = BCounter()

        cnt1.increment(Int.MAX_VALUE, client1.tick())
        cnt2.increment(1, client2.tick())
        cnt2.merge(cnt1)

        shouldThrow<ArithmeticException> {
            cnt2.get()
        }
    }

    "overflowing rights transfer" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)

        val cnt1 = BCounter()
        val cnt2 = BCounter()
        cnt1.increment(Int.MAX_VALUE, client1.tick())
        cnt2.increment(1, client2.tick())

        cnt2.transfer(1, uid1, client2.tick())
        cnt1.merge(cnt2)
        shouldThrow<ArithmeticException> {
            cnt1.localRights(uid1)
        }
        cnt1.transfer(1, uid2, client1.tick())

        cnt1.merge(cnt2)
        cnt1.localRights(uid1).shouldBe(Int.MAX_VALUE)
        cnt1.localRights(uid2).shouldBe(1)
    }

    "rights transfer, overflowing decrement" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)

        val cnt1 = BCounter()
        val cnt2 = BCounter()
        cnt1.increment(Int.MAX_VALUE, client1.tick())
        cnt2.increment(1, client2.tick())
        cnt2.transfer(1, uid1, client2.tick())
        cnt1.merge(cnt2)

        cnt1.decrement(1, client1.tick())
        shouldThrow<ArithmeticException> {
            cnt1.decrement(Int.MAX_VALUE, client1.tick())
        }
        cnt1.get().shouldBe(Int.MAX_VALUE)
    }

    /* Serialization */

    "empty JSON serialization" {
        val cnt = BCounter()

        val cntJson = cnt.toJson()
        cntJson.shouldBe("""{"_type":"BCounter","_metadata":{"increment":[],"decrement":[]},"value":0}""")
    }

    "empty JSON deserialization" {
        val cntJson = BCounter.fromJson("""{"_type":"BCounter","_metadata":{"increment":[],"decrement":[]},"value":0}""")

        cntJson.get().shouldBe(0)
    }

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
        val inc1 = 10
        val inc2 = 30
        val dec1 = 5
        val dec2 = 20
        val cnt1 = BCounter()
        val cnt2 = BCounter()

        cnt1.increment(inc1, ts1)
        cnt1.decrement(dec1, ts3)
        cnt2.increment(inc2, ts2)
        cnt2.decrement(dec2, ts4)
        cnt2.transfer(2, uid1, ts5)
        cnt1.merge(cnt2)
        val cntJson = cnt1.toJson()

        cntJson.shouldBe("""{"_type":"BCounter","_metadata":{"increment":[{"name":"clientid1"},[{"name":"clientid1"},{"first":10,"second":{"uid":{"name":"clientid1"},"cnt":-2147483647}}],{"name":"clientid2"},[{"name":"clientid2"},{"first":30,"second":{"uid":{"name":"clientid2"},"cnt":-2147483647}},{"name":"clientid1"},{"first":2,"second":{"uid":{"name":"clientid2"},"cnt":-2147483645}}]],"decrement":[{"name":"clientid1"},{"first":5,"second":{"uid":{"name":"clientid1"},"cnt":-2147483646}},{"name":"clientid2"},{"first":20,"second":{"uid":{"name":"clientid2"},"cnt":-2147483646}}]},"value":15}""")
    }

    "JSON deserialization" {
        val cntJson = BCounter.fromJson("""{"_type":"BCounter","_metadata":{"increment":[{"name":"clientid1"},[{"name":"clientid1"},{"first":10,"second":{"uid":{"name":"clientid1"},"cnt":-2147483647}}],{"name":"clientid2"},[{"name":"clientid2"},{"first":30,"second":{"uid":{"name":"clientid2"},"cnt":-2147483647}},{"name":"clientid1"},{"first":2,"second":{"uid":{"name":"clientid2"},"cnt":-2147483645}}]],"decrement":[{"name":"clientid1"},{"first":5,"second":{"uid":{"name":"clientid1"},"cnt":-2147483646}},{"name":"clientid2"},{"first":20,"second":{"uid":{"name":"clientid2"},"cnt":-2147483646}}]},"value":15}""")

        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        cntJson.get().shouldBe(15)
        cntJson.localRights(uid1).shouldBe(7)
        cntJson.localRights(uid2).shouldBe(8)
    }
})
