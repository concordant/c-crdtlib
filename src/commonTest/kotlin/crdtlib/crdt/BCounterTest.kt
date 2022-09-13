/*
* MIT License
*
* Copyright © 2022, Concordant and contributors.
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
import crdtlib.utils.ReadOnlyEnvironment
import crdtlib.utils.SimpleEnvironment
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.shouldBe

/**
 * Represents a suite test for BCounter.
 */
class BCounterTest : StringSpec({

    val uid1 = ClientUId("clientid1")
    val uid2 = ClientUId("clientid2")
    var client1 = SimpleEnvironment(uid1)
    var client2 = SimpleEnvironment(uid2)

    beforeTest {
        client1 = SimpleEnvironment(uid1)
        client2 = SimpleEnvironment(uid2)
    }

    "create and get value" {
        val cnt = BCounter()

        cnt.get().shouldBeZero()
    }

    "create and localRights value" {
        val cnt = BCounter()

        cnt.localRights(uid1).shouldBeZero()
    }

    "increment and get/localRights" {
        val inc = 10
        val cnt = BCounter(client1)

        cnt.increment(inc)

        cnt.get().shouldBe(10)
        cnt.localRights(uid1).shouldBe(10)
    }

    "failing decrement and get/localRights" {
        val cnt = BCounter(client1)

        shouldThrow<IllegalArgumentException> {
            cnt.decrement(10)
        }

        cnt.get().shouldBeZero()
        cnt.localRights(uid1).shouldBeZero()
    }

    "increment decrement and get/localRights" {
        val cnt = BCounter(client1)

        cnt.increment(15)
        cnt.decrement(6)

        cnt.get().shouldBe(9)
        cnt.localRights(uid1).shouldBe(9)
    }

    "failing negative increment and get" {
        val cnt = BCounter(client1)

        shouldThrow<IllegalArgumentException> {
            cnt.increment(-15)
        }

        cnt.get().shouldBeZero()
        cnt.localRights(uid1).shouldBeZero()
    }

    "positive increment, negative increment and get" {
        val cnt = BCounter(client1)

        cnt.increment(15)
        cnt.increment(-8)

        cnt.get().shouldBe(7)
        cnt.localRights(uid1).shouldBe(7)
    }

    "negative decrement and get" {
        val cnt = BCounter(client1)

        cnt.decrement(-15)

        cnt.get().shouldBe(15)
        cnt.localRights(uid1).shouldBe(15)
    }

    "multiple increments and get" {
        val cnt = BCounter(client1)

        cnt.increment(10)
        cnt.increment(1)
        cnt.increment(100)

        cnt.get().shouldBe(111)
        cnt.localRights(uid1).shouldBe(111)
    }

    "multiple failing decrements and get" {
        val cnt = BCounter(client1)

        shouldThrow<IllegalArgumentException> {
            cnt.decrement(10)
        }
        shouldThrow<IllegalArgumentException> {
            cnt.decrement(1)
        }
        shouldThrow<IllegalArgumentException> {
            cnt.decrement(100)
        }

        cnt.get().shouldBeZero()
        cnt.localRights(uid1).shouldBeZero()
    }

    "one increment, multiple decrements and get" {
        val cnt = BCounter(client1)

        cnt.increment(150)
        cnt.decrement(10)
        cnt.decrement(1)
        cnt.decrement(100)

        cnt.get().shouldBe(39)
        cnt.localRights(uid1).shouldBe(39)
    }

    "multiple (increment, decrement)" {
        val cnt = BCounter(client1)

        cnt.increment(15)
        cnt.get().shouldBe(15)
        cnt.localRights(uid1).shouldBe(15)

        cnt.decrement(5)
        cnt.get().shouldBe(10)
        cnt.localRights(uid1).shouldBe(10)

        cnt.increment(40)
        cnt.get().shouldBe(50)
        cnt.localRights(uid1).shouldBe(50)

        cnt.decrement(2)
        cnt.get().shouldBe(48)
        cnt.localRights(uid1).shouldBe(48)

        shouldThrow<IllegalArgumentException> {
            cnt.decrement(50)
        }
    }

    /* Merging */

    "R1: increment; R2: merge and get" {
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client1)

        cnt1.increment(11)
        cnt2.merge(cnt1)
        cnt1.merge(cnt2)

        cnt1.get().shouldBe(11)
        cnt2.get().shouldBe(11)
        cnt1.localRights(uid1).shouldBe(11)
        cnt2.localRights(uid1).shouldBe(11)
    }

    "R1: increment; R2: increment, merge, get" {
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client2)

        cnt1.increment(10)
        cnt2.increment(1)
        cnt2.merge(cnt1)

        cnt2.get().shouldBe(11)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(1)
    }

    "R1: increment; R2: merge, increment, get" {
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client2)

        cnt1.increment(10)
        cnt2.merge(cnt1)
        cnt2.increment(1)

        cnt2.get().shouldBe(11)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(1)
    }

    "R1: multiple operations; R2: multiple operations, merge, get" {
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client2)

        cnt1.increment(10)
        cnt1.decrement(10)
        cnt1.increment(30)
        cnt1.decrement(20)
        cnt2.increment(50)
        cnt2.decrement(30)
        cnt2.increment(70)
        cnt2.decrement(40)
        cnt2.merge(cnt1)

        cnt2.get().shouldBe(60)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(50)

        shouldThrow<IllegalArgumentException> {
            cnt1.decrement(20)
        }
        shouldThrow<IllegalArgumentException> {
            cnt2.decrement(60)
        }
    }

    "R1: multiple operations; R2: merge, multiple operations, get" {
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client2)

        cnt1.increment(10)
        cnt1.decrement(10)
        cnt1.increment(30)
        cnt1.decrement(20)
        cnt2.merge(cnt1)
        cnt2.increment(50)
        cnt2.decrement(30)
        cnt2.increment(70)
        cnt2.decrement(40)

        cnt2.get().shouldBe(60)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(50)
    }

    /* Deltas */

    "use delta returned by increment" {
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client1)

        val returnedIncOp = cnt1.increment(11)
        val incOp = client1.popWrite().second
        returnedIncOp.shouldBe(incOp)

        cnt2.merge(incOp)
        cnt1.merge(incOp)

        cnt2.get().shouldBe(11)
        cnt1.localRights(uid1).shouldBe(11)
        cnt2.localRights(uid1).shouldBe(11)
    }

    "use delta returned by increment and decrement" {
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client1)

        val returnedIncOp = cnt1.increment(15)
        val incOp = client1.popWrite().second
        returnedIncOp.shouldBe(incOp)
        val returnedDecOp = cnt1.decrement(11)
        val decOp = client1.popWrite().second
        returnedDecOp.shouldBe(decOp)

        cnt2.merge(incOp)
        cnt1.merge(incOp)
        cnt2.merge(decOp)
        cnt1.merge(decOp)

        cnt1.get().shouldBe(4)
        cnt2.get().shouldBe(4)
        cnt1.localRights(uid1).shouldBe(4)
        cnt2.localRights(uid1).shouldBe(4)
    }

    "use delta returned by transfer" {
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client2)

        cnt1.increment(20)
        cnt2.merge(cnt1)
        val delta = cnt1.transfer(5, uid2)
        cnt2.merge(delta)
        cnt1.get().shouldBe(20)
        cnt2.get().shouldBe(20)
        cnt1.localRights(uid1).shouldBe(15)
        cnt1.localRights(uid2).shouldBe(5)
        cnt2.localRights(uid1).shouldBe(15)
        cnt2.localRights(uid2).shouldBe(5)
    }

    "generate delta" {
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client1)

        cnt1.increment(11)
        cnt1.increment(33)
        val vv = client1.getState()
        cnt2.merge(cnt1)
        cnt1.decrement(10)
        cnt1.decrement(20)
        val delta = cnt1.generateDelta(vv)
        cnt2.merge(delta)

        cnt2.get().shouldBe(14)
        cnt2.localRights(uid1).shouldBe(14)
    }

    /* Rights transfer */

    "rights transfer, one way" {
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client2)

        cnt1.increment(20)
        cnt1.transfer(5, uid2)
        cnt2.merge(cnt1)
        cnt1.get().shouldBe(20)
        cnt2.get().shouldBe(20)
        cnt1.localRights(uid1).shouldBe(15)
        cnt2.localRights(uid2).shouldBe(5)

        shouldThrow<IllegalArgumentException> {
            cnt2.decrement(10)
        }
        cnt1.transfer(5, uid2)
        cnt2.merge(cnt1)
        cnt2.decrement(10)
        cnt1.merge(cnt2)

        cnt1.get().shouldBe(10)
        cnt2.get().shouldBe(10)
        cnt1.localRights(uid1).shouldBe(10)
        cnt1.localRights(uid2).shouldBe(0)
        cnt2.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(0)
    }

    "two rights transfers, one way" {
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client1)

        cnt1.increment(10)
        cnt1.transfer(6, uid2)
        cnt2.merge(cnt1)
        cnt1.get().shouldBe(10)
        cnt2.get().shouldBe(10)
        cnt1.localRights(uid1).shouldBe(4)
        cnt2.localRights(uid2).shouldBe(6)

        cnt1.transfer(4, uid2)
        cnt2.merge(cnt1)
        cnt1.get().shouldBe(10)
        cnt2.get().shouldBe(10)
        cnt1.localRights(uid1).shouldBe(0)
        cnt2.localRights(uid2).shouldBe(10)
    }

    "two rights transfers (one failing), one way" {
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client2)

        cnt1.increment(10)
        cnt2.increment(30)
        cnt2.merge(cnt1)
        cnt1.merge(cnt2)

        cnt1.get().shouldBe(40)
        cnt2.get().shouldBe(40)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(30)

        cnt1.transfer(5, uid2)
        cnt2.merge(cnt1)
        cnt1.get().shouldBe(40)
        cnt2.get().shouldBe(40)
        cnt1.localRights(uid1).shouldBe(5)
        cnt2.localRights(uid2).shouldBe(35)

        shouldThrow<IllegalArgumentException> {
            cnt1.transfer(10, uid2)
        }
        cnt1.get().shouldBe(40)
        cnt2.get().shouldBe(40)
        cnt1.localRights(uid1).shouldBe(5)
    }

    "two way transfer" {
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client2)

        cnt1.increment(10)
        cnt2.increment(30)
        cnt2.merge(cnt1)
        cnt1.merge(cnt2)

        cnt1.get().shouldBe(40)
        cnt2.get().shouldBe(40)
        cnt1.localRights(uid1).shouldBe(10)
        cnt2.localRights(uid2).shouldBe(30)

        cnt2.transfer(20, uid1)
        cnt1.merge(cnt2)
        cnt1.get().shouldBe(40)
        cnt2.get().shouldBe(40)
        cnt1.localRights(uid1).shouldBe(30)
        cnt2.localRights(uid2).shouldBe(10)

        cnt1.transfer(15, uid2)
        cnt2.merge(cnt1)
        cnt1.get().shouldBe(40)
        cnt2.get().shouldBe(40)
        cnt1.localRights(uid1).shouldBe(15)
        cnt2.localRights(uid2).shouldBe(25)
    }

    /* Overflow errors */

    "overflowing increment" {
        val cnt = BCounter(client1)

        cnt.increment(Int.MAX_VALUE)

        shouldThrow<ArithmeticException> {
            cnt.increment(1)
        }

        cnt.get().shouldBe(Int.MAX_VALUE)
    }

    "R1: increment; R2: increment, merge, overflowing get" {
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client2)

        cnt1.increment(Int.MAX_VALUE)
        cnt2.increment(1)
        cnt2.merge(cnt1)

        shouldThrow<ArithmeticException> {
            cnt2.get()
        }
    }

    "overflowing rights transfer" {
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client2)
        cnt1.increment(Int.MAX_VALUE)
        cnt2.increment(1)

        cnt2.transfer(1, uid1)
        cnt1.merge(cnt2)
        shouldThrow<ArithmeticException> {
            cnt1.localRights(uid1)
        }
        cnt1.transfer(1, uid2)

        cnt1.merge(cnt2)
        cnt1.localRights(uid1).shouldBe(Int.MAX_VALUE)
        cnt1.localRights(uid2).shouldBe(1)
    }

    "rights transfer, overflowing decrement" {
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client2)
        cnt1.increment(Int.MAX_VALUE)
        cnt2.increment(1)
        cnt2.transfer(1, uid1)
        cnt1.merge(cnt2)

        cnt1.decrement(1)
        shouldThrow<ArithmeticException> {
            cnt1.decrement(Int.MAX_VALUE)
        }
        cnt1.get().shouldBe(Int.MAX_VALUE)
    }

    "Read Only Environment" {
        val inc = 10
        client2 = ReadOnlyEnvironment(uid2)
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client2)

        cnt1.increment(inc)
        cnt1.transfer(inc, uid2)
        cnt2.merge(cnt1)

        shouldThrow<RuntimeException> {
            cnt2.increment(20)
        }
        shouldThrow<RuntimeException> {
            cnt2.decrement(5)
        }
        shouldThrow<RuntimeException> {
            cnt2.transfer(5, uid1)
        }

        cnt2.get().shouldBe(10)
        cnt2.localRights(uid1).shouldBe(0)
        cnt2.localRights(uid2).shouldBe(10)
    }

    /* Serialization */

    "empty JSON serialization" {
        val cnt = BCounter(client1)

        val cntJson = cnt.toJson()
        cntJson.shouldBe("""{"type":"BCounter","metadata":{"increment":[],"decrement":[]},"value":0}""")
    }

    "empty JSON deserialization" {
        val cntJson = BCounter.fromJson("""{"type":"BCounter","metadata":{"increment":[],"decrement":[]},"value":0}""")

        cntJson.get().shouldBe(0)
    }

    "JSON serialization" {
        val cnt1 = BCounter(client1)
        val cnt2 = BCounter(client2)

        cnt1.increment(10)
        cnt1.decrement(5)
        cnt2.increment(30)
        cnt2.decrement(20)
        cnt2.transfer(2, uid1)
        cnt1.merge(cnt2)
        val cntJson = cnt1.toJson()

        cntJson.shouldBe("""{"type":"BCounter","metadata":{"increment":[{"name":"clientid1"},[{"name":"clientid1"},{"first":10,"second":{"uid":{"name":"clientid1"},"cnt":-2147483647}}],{"name":"clientid2"},[{"name":"clientid2"},{"first":30,"second":{"uid":{"name":"clientid2"},"cnt":-2147483647}},{"name":"clientid1"},{"first":2,"second":{"uid":{"name":"clientid2"},"cnt":-2147483645}}]],"decrement":[{"name":"clientid1"},{"first":5,"second":{"uid":{"name":"clientid1"},"cnt":-2147483646}},{"name":"clientid2"},{"first":20,"second":{"uid":{"name":"clientid2"},"cnt":-2147483646}}]},"value":15}""")
    }

    "JSON deserialization" {
        val cntJson = BCounter.fromJson(
            """{"type":"BCounter","metadata":{"increment":[{"name":"clientid1"},[{"name":"clientid1"},{"first":10,"second":{"uid":{"name":"clientid1"},"cnt":-2147483647}}],{"name":"clientid2"},[{"name":"clientid2"},{"first":30,"second":{"uid":{"name":"clientid2"},"cnt":-2147483647}},{"name":"clientid1"},{"first":2,"second":{"uid":{"name":"clientid2"},"cnt":-2147483645}}]],"decrement":[{"name":"clientid1"},{"first":5,"second":{"uid":{"name":"clientid1"},"cnt":-2147483646}},{"name":"clientid2"},{"first":20,"second":{"uid":{"name":"clientid2"},"cnt":-2147483646}}]},"value":15}""",
            client1
        )

        cntJson.get().shouldBe(15)
        cntJson.localRights(uid1).shouldBe(7)
        cntJson.localRights(uid2).shouldBe(8)
    }
})
