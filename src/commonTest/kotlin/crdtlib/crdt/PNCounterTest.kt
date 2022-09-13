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
import io.kotest.matchers.*

/**
 * Represents a suite test for PNCounter.
 */
class PNCounterTest : StringSpec({

    val uid1 = ClientUId("clientid1")
    val uid2 = ClientUId("clientid2")
    var client1 = SimpleEnvironment(uid1)
    var client2 = SimpleEnvironment(uid2)

    beforeTest {
        client1 = SimpleEnvironment(uid1)
        client2 = SimpleEnvironment(uid2)
    }

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
        val cnt = PNCounter(client1)

        cnt.increment(10)

        cnt.get().shouldBe(10)
    }

    /**
     * This test evaluates the scenario: decrement get.
     * Call to get should return the inverse of value set by decrement.
     */
    "decrement and get" {
        val cnt = PNCounter(client1)

        cnt.decrement(10)

        cnt.get().shouldBe(-10)
    }

    /**
     * This test evaluates the scenario: increment(with a negative value) get.
     * Call to get should return the value set by increment.
     */
    "increment with negative amount and get" {
        val cnt = PNCounter(client1)

        cnt.increment(-10)

        cnt.get().shouldBe(-10)
    }

    /**
     * This test evaluates the scenario: decrement(with a negative value) get.
     * Call to get should return the inverse of value set by decrement.
     */
    "decrement with negative amount and get" {
        val cnt = PNCounter(client1)

        cnt.decrement(-10)

        cnt.get().shouldBe(10)
    }

    /**
     * This test evaluates the scenario: increment(multiple times) get.
     * Call to get should return the sum of values set by calls to increment.
     */
    "multiple increments and get" {
        val cnt = PNCounter(client1)

        cnt.increment(10)
        cnt.increment(1)
        cnt.increment(100)

        cnt.get().shouldBe(111)
    }

    /**
     * This test evaluates the scenario: decrement(multiple times) get.
     * Call to get should return the inverse of the sum of values set by calls to decrement.
     */
    "multiple decrements and get" {
        val cnt = PNCounter(client1)

        cnt.decrement(10)
        cnt.decrement(1)
        cnt.decrement(100)

        cnt.get().shouldBe(-111)
    }

    /**
     * This test evaluates the scenario: multiple increment and decrement get.
     * Call to get should return the sum of increments minus the sum of decrements.
     */
    "increment, decrement, get positive value" {
        val cnt = PNCounter(client1)

        cnt.increment(42)
        cnt.decrement(27)
        cnt.increment(34)
        cnt.decrement(2)

        cnt.get().shouldBe(47)
    }

    /**
     * This test evaluates the scenario: multiple increment and decrement get.
     * Call to get should return the sum of increments minus the sum of decrements.
     */
    "increment, decrement, get negative value" {
        val cnt = PNCounter(client1)

        cnt.increment(42)
        cnt.decrement(77)
        cnt.increment(34)
        cnt.decrement(13)

        cnt.get().shouldBe(-14)
    }

    /**
     * This test evaluates the scenario: increment || merge get.
     * Call to get should return value set by increment in the first replica.
     */
    "R1: increment; R2: merge and get" {
        val cnt1 = PNCounter(client1)
        val cnt2 = PNCounter(client2)

        cnt1.increment(11)
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
        val dec = 11
        val cnt1 = PNCounter(client1)
        val cnt2 = PNCounter(client2)

        cnt1.decrement(dec)
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
        val cnt1 = PNCounter(client1)
        val cnt2 = PNCounter(client2)

        cnt1.increment(10)
        cnt2.increment(1)
        cnt2.merge(cnt1)

        cnt2.get().shouldBe(11)
    }

    /**
     * This test evaluates the scenario: increment || merge increment get.
     * Call to get should return sum of the two increment values.
     */
    "R1: increment; R2: merge, increment, get" {
        val cnt1 = PNCounter(client1)
        val cnt2 = PNCounter(client2)

        cnt1.increment(10)
        cnt2.merge(cnt1)
        cnt2.increment(1)

        cnt2.get().shouldBe(11)
    }

    /**
     * This test evaluates the scenario: decrement || decrement merge get.
     * Call to get should return the inverse of the sum of the two decrement values.
     */
    "R1: decrement; R2: decrement, merge, get" {
        val cnt1 = PNCounter(client1)
        val cnt2 = PNCounter(client2)

        cnt1.decrement(10)
        cnt2.decrement(1)
        cnt2.merge(cnt1)

        cnt2.get().shouldBe(-11)
    }

    /**
     * This test evaluates the scenario: decrement || merge decrement get.
     * Call to get should return the inverse of the sum of the two decrement values.
     */
    "R1: decrement; R2: merge, decrement, get" {
        val cnt1 = PNCounter(client1)
        val cnt2 = PNCounter(client2)

        cnt1.decrement(10)
        cnt2.merge(cnt1)
        cnt2.decrement(1)

        cnt2.get().shouldBe(-11)
    }

    /**
     * This test evaluates the scenario: some operations || some operations merge get.
     * Call to get should return the sum of increment values minus the sum of the decrement values.
     */
    "R1: multiple operations; R2: multiple operations, merge, get" {
        val cnt1 = PNCounter(client1)
        val cnt2 = PNCounter(client2)

        cnt1.decrement(10)
        cnt1.increment(10)
        cnt1.increment(30)
        cnt1.decrement(20)
        cnt2.decrement(30)
        cnt2.increment(50)
        cnt2.increment(70)
        cnt2.decrement(40)
        cnt2.merge(cnt1)

        cnt2.get().shouldBe(60)
    }

    /**
     * This test evaluates the scenario: some operations || merge some operations get.
     * Call to get should return the sum of increment values minus the sum of the decrement values.
     */
    "R1: multiple operations; R2: merge, multiple operations, get" {
        val cnt1 = PNCounter(client1)
        val cnt2 = PNCounter(client2)

        cnt1.decrement(10)
        cnt1.increment(10)
        cnt1.increment(30)
        cnt1.decrement(20)
        cnt2.merge(cnt1)
        cnt2.decrement(30)
        cnt2.increment(50)
        cnt2.increment(70)
        cnt2.decrement(40)

        cnt2.get().shouldBe(60)
    }

    /**
     * This test evaluates the use of delta return by call to increment method.
     * Call to get should return the increment value set in the first replica.
     */
    "use delta returned by increment" {
        val cnt1 = PNCounter(client1)
        val cnt2 = PNCounter(client2)

        val returnedIncOp = cnt1.increment(11)
        val incOp = client1.popWrite().second
        returnedIncOp.shouldBe(incOp)

        cnt2.merge(incOp)
        cnt1.merge(incOp)

        cnt2.get().shouldBe(11)
    }

    /**
     * This test evaluates the use of delta return by call to decrement method.
     * Call to get should return the inverse of the decrement value set in the first replica.
     */
    "use delta returned by decrement" {
        val cnt1 = PNCounter(client1)
        val cnt2 = PNCounter(client2)

        val returnedDecOp = cnt1.decrement(11)
        val decOp = client1.popWrite().second
        returnedDecOp.shouldBe(decOp)

        cnt2.merge(decOp)
        cnt1.merge(decOp)

        cnt1.get().shouldBe(-11)
        cnt2.get().shouldBe(-11)
    }

    /**
     * This test evaluates the use of delta return by call to increment and decrement methods.
     * Call to get should return the sum of increment values minus the sum of decrement values.
     */
    "use delta returned by increment and decrement" {
        val cnt1 = PNCounter(client1)
        val cnt2 = PNCounter(client2)

        val returnedDecOp = cnt1.decrement(11)
        val decOp = client1.popWrite().second
        returnedDecOp.shouldBe(decOp)
        val returnedIncOp = cnt1.increment(22)
        val incOp = client1.popWrite().second
        returnedIncOp.shouldBe(incOp)

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
        val cnt1 = PNCounter(client1)
        val cnt2 = PNCounter(client2)

        cnt1.increment(11)
        cnt1.increment(33)
        val vv = client1.getState()
        cnt1.decrement(10)
        cnt1.decrement(20)
        val delta = cnt1.generateDelta(vv)
        cnt2.merge(delta)

        cnt2.get().shouldBe(-30)
    }

    "Read Only Environment" {
        client2 = ReadOnlyEnvironment(uid2)
        val cnt1 = PNCounter(client1)
        val cnt2 = PNCounter(client2)

        cnt1.increment(20)
        cnt1.decrement(5)
        cnt2.merge(cnt1)

        shouldThrow<RuntimeException> {
            cnt2.increment(10)
        }
        shouldThrow<RuntimeException> {
            cnt2.decrement(20)
        }
        cnt1.get().shouldBe(15)
        cnt2.get().shouldBe(15)
    }

    /**
     * This test evaluates JSON serialization of an empty pncounter.
     */
    "empty JSON serialization" {
        val cnt = PNCounter()

        val cntJson = cnt.toJson()

        cntJson.shouldBe("""{"type":"PNCounter","metadata":{"increment":[],"decrement":[]},"value":0}""")
    }

    /**
     * This test evaluates JSON deserialization of an empty pncounter.
     */
    "empty JSON deserialization" {
        val cntJson = PNCounter.fromJson("""{"type":"PNCounter","metadata":{"increment":[],"decrement":[]},"value":0}""")

        cntJson.get().shouldBe(0)
    }

    /**
     * This test evaluates JSON serialization of a pncounter.
     */
    "JSON serialization" {
        val cnt1 = PNCounter(client1)
        val cnt2 = PNCounter(client2)

        cnt1.decrement(10)
        cnt1.increment(10)
        cnt2.decrement(20)
        cnt2.increment(30)
        cnt2.merge(cnt1)
        val cntJson = cnt2.toJson()

        cntJson.shouldBe("""{"type":"PNCounter","metadata":{"increment":[{"name":"clientid2"},{"first":30,"second":{"uid":{"name":"clientid2"},"cnt":-2147483646}},{"name":"clientid1"},{"first":10,"second":{"uid":{"name":"clientid1"},"cnt":-2147483646}}],"decrement":[{"name":"clientid2"},{"first":20,"second":{"uid":{"name":"clientid2"},"cnt":-2147483647}},{"name":"clientid1"},{"first":10,"second":{"uid":{"name":"clientid1"},"cnt":-2147483647}}]},"value":10}""")
    }

    /**
     * This test evaluates JSON deserialization of a pncounter.
     */
    "JSON deserialization" {
        val cntJson = PNCounter.fromJson("""{"type":"PNCounter","metadata":{"increment":[{"name":"clientid2"},{"first":30,"second":{"uid":{"name":"clientid2"},"cnt":-2147483646}},{"name":"clientid1"},{"first":10,"second":{"uid":{"name":"clientid1"},"cnt":-2147483646}}],"decrement":[{"name":"clientid2"},{"first":20,"second":{"uid":{"name":"clientid2"},"cnt":-2147483647}},{"name":"clientid1"},{"first":10,"second":{"uid":{"name":"clientid1"},"cnt":-2147483647}}]},"value":10}""")

        cntJson.get().shouldBe(10)
    }
})
