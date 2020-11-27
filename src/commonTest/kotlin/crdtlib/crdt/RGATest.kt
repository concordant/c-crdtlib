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
import crdtlib.utils.VersionVector
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.iterator.shouldBeEmpty
import io.kotest.matchers.iterator.shouldHaveNext

/**
* Represents a suite test for RGA.
**/
class RGATest : StringSpec({

    /**
     * This test evaluates the scenario: create, get/iterator.
     * Call to get should return an empty array.
     * Call to get at 0 should raise a IndexOutOfBoundsException.
     * Call to iterator should return an empty iterator.
     */
    "create and get/iterator" {
        val rga = RGA()
        rga.get().shouldBeEmpty()
        shouldThrow<IndexOutOfBoundsException> {
            rga.get(0)
        }
        rga.iterator().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0, get/iterator.
     * Call to get should return an array containing the inserted value.
     * Call to get at 0 should return the inserted value.
     * Call to iterator should return an iterator containing the inserted value.
     */
    "insert at 0 and get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val rga = RGA()

        rga.insertAt(0, "A", ts)

        rga.get().shouldHaveSingleElement("A")
        rga.get(0).shouldBe("A")

        val it = rga.iterator()
        it.shouldHaveNext()
        it.next().shouldBe("A")
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0 twice, get/iterator.
     * Call to get should return an array containing the two inserted values.
     * Second value should be at index 0 and first value at index 1.
     * Call to get at 0 should return the second inserted value.
     * Call to get at 1 should return the first inserted value.
     * Call to iterator should return an iterator containing the two inserted value.
     */
    "insert at 0, insert at 0, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val rga = RGA()

        rga.insertAt(0, "B", ts1)
        rga.insertAt(0, "A", ts2)

        rga.get().shouldContainExactly("A", "B")
        rga.get(0).shouldBe("A")
        rga.get(1).shouldBe("B")

        val it = rga.iterator()
        for (value in rga.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0, insert at 1, get/iterator.
     * Call to get should return an array containing the two inserted values.
     * First value should be at index 0 and second value at index 1.
     * Call to get at 0 should return the first inserted value.
     * Call to get at 1 should return the second inserted value.
     * Call to iterator should return an iterator containing the two inserted value.
     */
    "insert at 0, insert at 1, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val rga = RGA()

        rga.insertAt(0, "A", ts1)
        rga.insertAt(1, "B", ts2)

        rga.get().shouldContainExactly("A", "B")
        rga.get(0).shouldBe("A")
        rga.get(1).shouldBe("B")

        val it = rga.iterator()
        for (value in rga.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0, remove at 0, get/iterator.
     * Call to get should return an empty array.
     * Call to get at 0 should raise a IndexOutOfBoundsException.
     * Call to iterator should return an empty iterator.
     */
    "insert at 0, remove at 0, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val rga = RGA()

        rga.insertAt(0, "A", ts1)
        rga.removeAt(0, ts2)

        rga.get().shouldBeEmpty()
        shouldThrow<IndexOutOfBoundsException> {
            rga.get(0)
        }
        rga.iterator().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0 twice, remove at 0 twice, get/iterator.
     * Call to get should return an empty array.
     * Call to get at 0 should raise a IndexOutOfBoundsException.
     * Call to iterator should return an empty iterator.
     */
    "insert at 0, insert at 0, remove at 0, remove at 0, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val ts4 = client.tick()
        val rga = RGA()

        rga.insertAt(0, "A", ts1)
        rga.insertAt(0, "B", ts2)
        rga.removeAt(0, ts3)
        rga.removeAt(0, ts4)

        rga.get().shouldBeEmpty()
        shouldThrow<IndexOutOfBoundsException> {
            rga.get(0)
        }
        rga.iterator().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0, insert at 1, remove at 0, insert at 1, get/iterator.
     * Call to get should return an array containing the two last inserted values.
     * Second inserted value should be at index 0 and third inserted value at index 1.
     * Call to get at 1 should return the second inserted value.
     * Call to get at 2 should return the third inserted value.
     * Call to iterator should return an iterator containing the two last inserted values.
     */
    "insert at 0, insert at 1, remove at 0, insert at 1, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val ts4 = client.tick()
        val rga = RGA()

        rga.insertAt(0, "A", ts1)
        rga.insertAt(1, "B", ts2)
        rga.removeAt(0, ts3)
        rga.insertAt(1, "C", ts4)

        rga.get().shouldContainExactly("B", "C")
        rga.get(0).shouldBe("B")
        rga.get(1).shouldBe("C")

        val it = rga.iterator()
        for (value in rga.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0, insert at 1, remove at 1, insert at 1, get/iterator.
     * Call to get should return an array containing the first and third inserted values.
     * First inserted value should be at index 0 and third inserted value at index 1.
     * Call to get at 0 should return the first inserted value.
     * Call to get at 1 should return the third inserted value.
     * Call to iterator should return an iterator containing the first and third inserted values.
     */
    "insert at 0, insert at 1, remove at 1, insert at 1, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val ts4 = client.tick()
        val rga = RGA()

        rga.insertAt(0, "A", ts1)
        rga.insertAt(1, "B", ts2)
        rga.removeAt(1, ts3)
        rga.insertAt(1, "C", ts4)

        rga.get().shouldContainExactly("A", "C")
        rga.get(0).shouldBe("A")
        rga.get(1).shouldBe("C")

        val it = rga.iterator()
        for (value in rga.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0 || merge, get/iterator.
     * Call to get should return an array containing the value inserted in replica 1.
     * Call to get at 0 should return the value inserted in replica 1.
     * Call to iterator should return an iterator containing the value inserted in replica 1.
     */
    "R1: insert at 1; R2: merge, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, "A", ts)
        rga2.merge(rga1)

        rga2.get().shouldHaveSingleElement("A")
        rga2.get(0).shouldBe("A")

        val it = rga2.iterator()
        it.shouldHaveNext()
        it.next().shouldBe("A")
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0 twice || merge, get/iterator.
     * Call to get should return an array containing the two values inserted in replica 1.
     * Call to get at 0 should return the second value inserted in replica 1.
     * Call to get at 1 should return the first value inserted in replica 1.
     * Call to iterator should return an iterator containing the two values inserted in replica 1.
     */
    "R1: insert at 0, insert at 0; R2: merge, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, "B", ts1)
        rga1.insertAt(0, "A", ts2)
        rga2.merge(rga1)

        rga2.get().shouldContainExactly("A", "B")
        rga2.get(0).shouldBe("A")
        rga2.get(1).shouldBe("B")

        val it = rga2.iterator()
        for (value in rga2.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0, insert at 1, insert at 2 || merge, get/iterator.
     * Call to get should return an array containing the three values inserted in replica 1.
     * Call to get at 0 should return the first value inserted in replica 1.
     * Call to get at 1 should return the second value inserted in replica 1.
     * Call to get at 2 should return the third value inserted in replica 1.
     * Call to iterator should return an iterator containing the three values inserted in replica 1.
     */
    "R1: insert at 0, insert at 1, insert at 2; R2: merge, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, "A", ts1)
        rga1.insertAt(1, "B", ts2)
        rga1.insertAt(2, "C", ts3)
        rga2.merge(rga1)

        rga2.get().shouldContainExactly("A", "B", "C")
        rga2.get(0).shouldBe("A")
        rga2.get(1).shouldBe("B")
        rga2.get(2).shouldBe("C")

        val it = rga2.iterator()
        for (value in rga2.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0 || insert at 0 (with greater timestamp), merge,
     * get/iterator.
     * Call to get should return an array containing the two values. Value inserted in replica 2
     * should be at index 0 and the one inserted at replica 1 at index 1.
     * Call to get at 0 should return the value inserted in replica 2.
     * Call to get at 1 should return the value inserted in replica 1.
     * Call to iterator should return an iterator containing the two values.
     */
    "R1: insert at 0; R2: insert at 0 with greater timestamp, merge, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, "B", ts1)
        rga2.insertAt(0, "A", ts2)
        rga2.merge(rga1)

        rga2.get().shouldContainExactly("A", "B")
        rga2.get(0).shouldBe("A")
        rga2.get(1).shouldBe("B")

        val it = rga2.iterator()
        for (value in rga2.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0 (with greater timestamp) || insert at 0, merge,
     * get/iterator.
     * Call to get should return an array containing the two values. Value inserted in replica
     * 1 should be at index 0 and the one inserted at replica 2 at index 1.
     * Call to get at 0 should return the value inserted in replica 1.
     * Call to get at 1 should return the value inserted in replica 2.
     * Call to iterator should return an iterator containing the two values.
     */
    "R1: insert at 0 with greater timestamp; R2: insert at 0, merge, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, "A", ts2)
        rga2.insertAt(0, "B", ts1)
        rga2.merge(rga1)

        rga2.get().shouldContainExactly("A", "B")
        rga2.get(0).shouldBe("A")
        rga2.get(1).shouldBe("B")

        val it = rga2.iterator()
        for (value in rga2.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0 twice || insert at 0 twice, merge, get/iterator.
     * Call to get should return an array containing the four values. Values should be ordered
     * according to decreasing order of their associated timestamp.
     * Call to get at 0 should return the second value inserted in replica 2.
     * Call to get at 1 should return the second value inserted in replica 1.
     * Call to get at 2 should return the first value inserted in replica 2.
     * Call to get at 3 should return the first value inserted in replica 1.
     * Call to iterator should return an iterator containing the four values.
     */
    "R1: insert at 0, insert at 0; R2: insert at 0, insert at 0, merge, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client1.tick()
        val ts4 = client2.tick()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, "D", ts1)
        rga1.insertAt(0, "B", ts3)
        rga2.insertAt(0, "C", ts2)
        rga2.insertAt(0, "A", ts4)
        rga2.merge(rga1)

        rga2.get().shouldContainExactly("A", "B", "C", "D")
        rga2.get(0).shouldBe("A")
        rga2.get(1).shouldBe("B")
        rga2.get(2).shouldBe("C")
        rga2.get(3).shouldBe("D")

        val it = rga2.iterator()
        for (value in rga2.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0 (with greater timestamp), insert at 1 || insert
     * at 0, insert at 1, merge, get/iterator.
     * Call to get should return an array containing the four values. Values inserted in replica 1
     * should be before the one inserted in replica 2.
     * Call to get at 0 should return the first value inserted in replica 1.
     * Call to get at 1 should return the second value inserted in replica 1.
     * Call to get at 2 should return the first value inserted in replica 2.
     * Call to get at 3 should return the second value inserted in replica 2.
     * Call to iterator should return an iterator containing the four values.
     */
    "insert at 0 with greater timestamp, insert at 1; R2: insert at 0, insert at 1, merge, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client1.tick()
        val ts4 = client2.tick()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, "A", ts2)
        rga1.insertAt(1, "B", ts4)
        rga2.insertAt(0, "C", ts1)
        rga2.insertAt(1, "D", ts3)
        rga1.merge(rga2)

        rga1.get().shouldContainExactly("A", "B", "C", "D")
        rga1.get(0).shouldBe("A")
        rga1.get(1).shouldBe("B")
        rga1.get(2).shouldBe("C")
        rga1.get(3).shouldBe("D")

        val it = rga1.iterator()
        for (value in rga1.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0, insert at 1 || insert at 0 (with greater
     * timestamp), insert at 1, merge, get/iterator.
     * Call to get should return an array containing the four values. Values inserted in replica 2
     * should be before the one inserted in replica 1.
     * Call to get at 0 should return the first value inserted in replica 2.
     * Call to get at 1 should return the second value inserted in replica 2.
     * Call to get at 2 should return the first value inserted in replica 1.
     * Call to get at 3 should return the second value inserted in replica 1.
     * Call to iterator should return an iterator containing the four values.
     */
    "R1: insert at 0, insert at 1; R2: insert at 0 with greater timestamp, insert at 1, merge, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client1.tick()
        val ts4 = client2.tick()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, "C", ts1)
        rga1.insertAt(1, "D", ts3)
        rga2.insertAt(0, "A", ts2)
        rga2.insertAt(1, "B", ts4)
        rga2.merge(rga1)

        rga2.get().shouldContainExactly("A", "B", "C", "D")
        rga2.get(0).shouldBe("A")
        rga2.get(1).shouldBe("B")
        rga2.get(2).shouldBe("C")
        rga2.get(3).shouldBe("D")

        val it = rga2.iterator()
        for (value in rga2.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert four times, remove at 1 || merge (after adds in
     * replica 1), remove at 2, merge, get/iterator.
     * Call to get should return an array containing the two values that have not been remove (the
     * first and the fourth one).
     * Call to get at 0 should return the first inserted value.
     * Call to get at 1 should return the fourth inserted value.
     * Call to iterator should return an iterator containing the two values that have not been remove (the
     * first and the fourth one).
     */
    "R1: insert four times, remove at 1; R2: merge after inserts, remove at 2, merge, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client1.tick()
        val ts3 = client1.tick()
        val ts4 = client1.tick()
        val ts5 = client1.tick()
        val ts6 = client2.tick()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, "A", ts1)
        rga1.insertAt(1, "B", ts2)
        rga1.insertAt(2, "C", ts3)
        rga1.insertAt(3, "D", ts4)
        rga2.merge(rga1)
        rga1.removeAt(1, ts5)
        rga2.removeAt(2, ts6)
        rga2.merge(rga1)

        rga2.get().shouldContainExactly("A", "D")
        rga2.get(0).shouldBe("A")
        rga2.get(1).shouldBe("D")

        val it = rga2.iterator()
        for (value in rga2.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0 (with greater timestamp) insert at 1 || insert
     * at 0 (with second greater timestamp), insert at 1 || insert at 0, insert at 1, merge from
     * replica 1, merge from replica 2, get/iterator.
     * Call to get should return an array containing the six values. Values inserted in replica 1
     * should be before the one inserted in replica 2 which should be before those inserted at
     * replica 3.
     * Call to get at 0 should return the first value inserted in replica 1.
     * Call to get at 1 should return the second inserted value in replica 1.
     * Call to get at 2 should return the first inserted value in replica 2.
     * Call to get at 3 should return the second inserted value in replica 2.
     * Call to get at 4 should return the first inserted value in replica 3.
     * Call to get at 5 should return the second inserted value in replica 3.
     * Call to iterator should return an iterator containing the six values.
     */
    "R1: insert at 0 with greater timestamp, insert at 1; R2: insert at 0 with second timestamp, insert at 1; R3: insert at 0, insert at 1, merge R1 and R2, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val client3 = SimpleEnvironment(uid3)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client3.tick()
        val ts4 = client1.tick()
        val ts5 = client2.tick()
        val ts6 = client3.tick()
        val rga1 = RGA()
        val rga2 = RGA()
        val rga3 = RGA()

        rga1.insertAt(0, "A", ts3)
        rga1.insertAt(1, "B", ts6)
        rga2.insertAt(0, "C", ts2)
        rga2.insertAt(1, "D", ts5)
        rga3.insertAt(0, "E", ts1)
        rga3.insertAt(1, "F", ts4)
        rga3.merge(rga1)
        rga3.merge(rga2)

        rga3.get().shouldContainExactly("A", "B", "C", "D", "E", "F")
        rga3.get(0).shouldBe("A")
        rga3.get(1).shouldBe("B")
        rga3.get(2).shouldBe("C")
        rga3.get(3).shouldBe("D")
        rga3.get(4).shouldBe("E")
        rga3.get(5).shouldBe("F")

        val it = rga3.iterator()
        for (value in rga3.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0 (with greater timestamp) insert at 1 || insert
     * at 0, insert at 1 || insert at 0 (with second greater timestamp), insert at 1, merge from
     * replica 1, merge from replica 2, get/iterator.
     * Call to get should return an array containing the six values. Values inserted in replica 1
     * should be before the one inserted in replica 3 which should be before those inserted at
     * replica 2.
     * Call to get at 0 should return the first value inserted in replica 1.
     * Call to get at 1 should return the second inserted value in replica 1.
     * Call to get at 2 should return the first inserted value in replica 3.
     * Call to get at 3 should return the second inserted value in replica 3.
     * Call to get at 4 should return the first inserted value in replica 2.
     * Call to get at 5 should return the second inserted value in replica 2.
     * Call to iterator should return an iterator containing the six values.
     */
    "R1: insert at 0 with greater timestamp, insert at 1; R2: insert at 0, insert at 1; R3: insert at 0 with second timestamp, insert at 1, merge R1 and R2, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val client3 = SimpleEnvironment(uid3)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client3.tick()
        val ts4 = client1.tick()
        val ts5 = client2.tick()
        val ts6 = client3.tick()
        val rga1 = RGA()
        val rga2 = RGA()
        val rga3 = RGA()

        rga1.insertAt(0, "A", ts3)
        rga1.insertAt(1, "B", ts6)
        rga2.insertAt(0, "E", ts1)
        rga2.insertAt(1, "F", ts4)
        rga3.insertAt(0, "C", ts2)
        rga3.insertAt(1, "D", ts5)
        rga3.merge(rga1)
        rga3.merge(rga2)

        rga3.get().shouldContainExactly("A", "B", "C", "D", "E", "F")
        rga3.get(0).shouldBe("A")
        rga3.get(1).shouldBe("B")
        rga3.get(2).shouldBe("C")
        rga3.get(3).shouldBe("D")
        rga3.get(4).shouldBe("E")
        rga3.get(5).shouldBe("F")

        val it = rga3.iterator()
        for (value in rga3.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0 (with second greater timestamp) insert at 1 ||
     * insert at 0 (with greater timestamp), insert at 1 || insert at 0, insert at 1, merge from
     * replica 1, merge from replica 2, get/iterator.
     * Call to get should return an array containing the six values. Values inserted in replica 2
     * should be before the one inserted in replica 1 which should be before those inserted at
     * replica 3.
     * Call to get at 0 should return the first value inserted in replica 2.
     * Call to get at 1 should return the second inserted value in replica 2.
     * Call to get at 2 should return the first inserted value in replica 1.
     * Call to get at 3 should return the second inserted value in replica 1.
     * Call to get at 4 should return the first inserted value in replica 3.
     * Call to get at 5 should return the second inserted value in replica 3.
     * Call to iterator should return an iterator containing the six values.
     */
    "R1: insert at 0 with second timestamp, insert at 1; R2: insert at 0 with greater timestamp, insert at 1; R3: insert at 0, insert at 1, merge R1 and R2, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val client3 = SimpleEnvironment(uid3)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client3.tick()
        val ts4 = client1.tick()
        val ts5 = client2.tick()
        val ts6 = client3.tick()
        val rga1 = RGA()
        val rga2 = RGA()
        val rga3 = RGA()

        rga1.insertAt(0, "C", ts2)
        rga1.insertAt(1, "D", ts5)
        rga2.insertAt(0, "A", ts3)
        rga2.insertAt(1, "B", ts6)
        rga3.insertAt(0, "E", ts1)
        rga3.insertAt(1, "F", ts4)
        rga3.merge(rga1)
        rga3.merge(rga2)

        rga3.get().shouldContainExactly("A", "B", "C", "D", "E", "F")
        rga3.get(0).shouldBe("A")
        rga3.get(1).shouldBe("B")
        rga3.get(2).shouldBe("C")
        rga3.get(3).shouldBe("D")
        rga3.get(4).shouldBe("E")
        rga3.get(5).shouldBe("F")

        val it = rga3.iterator()
        for (value in rga3.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0, insert at 1 || insert at 0 (with greater
     * timestamp), insert at 1 || insert at 0 (with second greater timestamp), insert at 1, merge
     * from replica 1, merge from replica 2, get/iterator.
     * Call to get should return an array containing the six values. Values inserted in replica 2
     * should be before the one inserted in replica 3 which should be before those inserted at
     * replica 1.
     * Call to get at 0 should return the first value inserted in replica 2.
     * Call to get at 1 should return the second inserted value in replica 2.
     * Call to get at 2 should return the first inserted value in replica 3.
     * Call to get at 3 should return the second inserted value in replica 3.
     * Call to get at 4 should return the first inserted value in replica 1.
     * Call to get at 5 should return the second inserted value in replica 1.
     * Call to iterator should return an iterator containing the six values.
     */
    "R1: insert at 0, insert at 1; R2: insert at 0 with greater timestamp, insert at 1; R3: insert at 0 with second timestamp, insert at 1, merge R1 and R2, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val client3 = SimpleEnvironment(uid3)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client3.tick()
        val ts4 = client1.tick()
        val ts5 = client2.tick()
        val ts6 = client3.tick()
        val rga1 = RGA()
        val rga2 = RGA()
        val rga3 = RGA()

        rga1.insertAt(0, "E", ts1)
        rga1.insertAt(1, "F", ts4)
        rga2.insertAt(0, "A", ts3)
        rga2.insertAt(1, "B", ts6)
        rga3.insertAt(0, "C", ts2)
        rga3.insertAt(1, "D", ts5)
        rga3.merge(rga1)
        rga3.merge(rga2)

        rga3.get().shouldContainExactly("A", "B", "C", "D", "E", "F")
        rga3.get(0).shouldBe("A")
        rga3.get(1).shouldBe("B")
        rga3.get(2).shouldBe("C")
        rga3.get(3).shouldBe("D")
        rga3.get(4).shouldBe("E")
        rga3.get(5).shouldBe("F")

        val it = rga3.iterator()
        for (value in rga3.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0, insert at 1 || insert at 0 (with second greater
     * timestamp), insert at 1 || insert at 0 (with greater timestamp), insert at 1, merge from
     * replica 1, merge from replica 2, get/iterator.
     * Call to get should return an array containing the six values. Values inserted in replica 3
     * should be before the one inserted in replica 2 which should be before those inserted at
     * replica 1.
     * Call to get at 0 should return the first value inserted in replica 3.
     * Call to get at 1 should return the second inserted value in replica 3.
     * Call to get at 2 should return the first inserted value in replica 2.
     * Call to get at 3 should return the second inserted value in replica 2.
     * Call to get at 4 should return the first inserted value in replica 1.
     * Call to get at 5 should return the second inserted value in replica 1.
     * Call to iterator should return an iterator containing the six values.
     */
    "R1: insert at 0, insert at 1; R2: insert at 0 with second timestamp, insert at 1; R3: insert at 0 with greater timestamp, insert at 1, merge R1 and R2, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val client3 = SimpleEnvironment(uid3)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client3.tick()
        val ts4 = client1.tick()
        val ts5 = client2.tick()
        val ts6 = client3.tick()
        val rga1 = RGA()
        val rga2 = RGA()
        val rga3 = RGA()

        rga1.insertAt(0, "E", ts1)
        rga1.insertAt(1, "F", ts4)
        rga2.insertAt(0, "C", ts2)
        rga2.insertAt(1, "D", ts5)
        rga3.insertAt(0, "A", ts3)
        rga3.insertAt(1, "B", ts6)
        rga3.merge(rga1)
        rga3.merge(rga2)

        rga3.get().shouldContainExactly("A", "B", "C", "D", "E", "F")
        rga3.get(0).shouldBe("A")
        rga3.get(1).shouldBe("B")
        rga3.get(2).shouldBe("C")
        rga3.get(3).shouldBe("D")
        rga3.get(4).shouldBe("E")
        rga3.get(5).shouldBe("F")

        val it = rga3.iterator()
        for (value in rga3.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: insert at 0 (with second greater timestamp) insert at 1 ||
     * insert at 0, insert at 1 || insert at 0 (with greater timestamp), insert at 1, merge from
     * replica 1, merge from replica 2, get/iterator.
     * Call to get should return an array containing the six values. Values inserted in replica 3
     * should be before the one inserted in replica 1 which should be before those inserted at
     * replica 2.
     * Call to get at 0 should return the first value inserted in replica 3.
     * Call to get at 1 should return the second inserted value in replica 3.
     * Call to get at 2 should return the first inserted value in replica 1.
     * Call to get at 3 should return the second inserted value in replica 1.
     * Call to get at 4 should return the first inserted value in replica 2.
     * Call to get at 5 should return the second inserted value in replica 2.
     * Call to iterator should return an iterator containing the six values.
     */
    "R1: insert at 0 with second timestamp, insert at 1; R2: insert at 0, insert at 1; R3: insert at 0 with greater timestamp, insert at 1, merge R1 and R2, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val client3 = SimpleEnvironment(uid3)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client3.tick()
        val ts4 = client1.tick()
        val ts5 = client2.tick()
        val ts6 = client3.tick()
        val rga1 = RGA()
        val rga2 = RGA()
        val rga3 = RGA()

        rga1.insertAt(0, "C", ts2)
        rga1.insertAt(1, "D", ts5)
        rga2.insertAt(0, "E", ts1)
        rga2.insertAt(1, "F", ts4)
        rga3.insertAt(0, "A", ts3)
        rga3.insertAt(1, "B", ts6)
        rga3.merge(rga1)
        rga3.merge(rga2)

        rga3.get().shouldContainExactly("A", "B", "C", "D", "E", "F")
        rga3.get(0).shouldBe("A")
        rga3.get(1).shouldBe("B")
        rga3.get(2).shouldBe("C")
        rga3.get(3).shouldBe("D")
        rga3.get(4).shouldBe("E")
        rga3.get(5).shouldBe("F")

        val it = rga3.iterator()
        for (value in rga3.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the use of delta return by call to insertAt method.
     * Call to get should return an array containing the value inserted in replica 1.
     * Call to get at 0 should return the value inserted in replica 1.
     * Call to iterator should return an iterator containing the value inserted in replica 1.
     */
    "use delta returned by insert" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val rga1 = RGA()
        val rga2 = RGA()

        val insertOp = rga1.insertAt(0, "A", ts)
        rga2.merge(insertOp)
        rga2.merge(insertOp)

        rga1.get().shouldHaveSingleElement("A")
        rga2.get().shouldHaveSingleElement("A")
        rga1.get(0).shouldBe("A")
        rga2.get(0).shouldBe("A")

        val it1 = rga1.iterator()
        it1.shouldHaveNext()
        it1.next().shouldBe("A")
        it1.shouldBeEmpty()
        val it2 = rga2.iterator()
        it2.shouldHaveNext()
        it2.next().shouldBe("A")
        it2.shouldBeEmpty()
    }

    /**
     * This test evaluates the use of delta return by call to removeAt method.
     * Call to get should return an empty array.
     * Call to get at 0 should raise a IndexOutOfBoundsException.
     * Call to iterator should return an empty iterator.
     */
    "use delta returned by remove" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, "A", ts1)
        rga2.merge(rga1)
        val removeOp = rga1.removeAt(0, ts2)
        rga1.merge(removeOp)
        rga2.merge(removeOp)

        rga1.get().shouldBeEmpty()
        rga2.get().shouldBeEmpty()
        shouldThrow<IndexOutOfBoundsException> {
            rga1.get(0)
        }
        shouldThrow<IndexOutOfBoundsException> {
            rga2.get(0)
        }
        rga1.iterator().shouldBeEmpty()
        rga2.iterator().shouldBeEmpty()
    }

    /**
     * This test evaluates the use of delta return by call to insertAt and removeAt methods.
     * Call to get should return an empty array.
     * Call to get at 0 should raise a IndexOutOfBoundsException.
     * Call to iterator should return an empty iterator.
     */
    "use delta returned by insert and remove" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val rga1 = RGA()
        val rga2 = RGA()

        val insertOp = rga1.insertAt(0, "A", ts1)
        val removeOp = rga1.removeAt(0, ts2)
        rga1.merge(insertOp)
        rga1.merge(removeOp)
        rga2.merge(insertOp)
        rga2.merge(removeOp)

        rga1.get().shouldBeEmpty()
        rga2.get().shouldBeEmpty()
        shouldThrow<IndexOutOfBoundsException> {
            rga1.get(0)
        }
        shouldThrow<IndexOutOfBoundsException> {
            rga2.get(0)
        }
        rga1.iterator().shouldBeEmpty()
        rga2.iterator().shouldBeEmpty()
    }

    /**
     * This test evaluates the merge of deltas returned by call to insertAt and removeAt methods.
     * Call to get should return an empty array.
     * Call to get at 0 should raise a IndexOutOfBoundsException.
     * Call to iterator should return an empty iterator.
     */
    "merge from delta insert to delta remove" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val rga1 = RGA()
        val rga2 = RGA()

        val op1 = rga1.insertAt(0, "A", ts1)
        val op2 = rga1.removeAt(0, ts2)
        op1.merge(op2)
        rga1.merge(op1)
        rga2.merge(op1)

        rga1.get().shouldBeEmpty()
        rga2.get().shouldBeEmpty()
        shouldThrow<IndexOutOfBoundsException> {
            rga1.get(0)
        }
        shouldThrow<IndexOutOfBoundsException> {
            rga2.get(0)
        }
        rga1.iterator().shouldBeEmpty()
        rga2.iterator().shouldBeEmpty()
    }

    /**
     * This test evaluates the merge of deltas returned by call to removeAt and insertAt methods.
     * Call to get should return an empty array.
     * Call to get at 0 should raise a IndexOutOfBoundsException.
     * Call to iterator should return an empty iterator.
     */
    "merge from delta remove to delta import" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val rga1 = RGA()
        val rga2 = RGA()

        val op1 = rga1.insertAt(0, "A", ts1)
        val op2 = rga1.removeAt(0, ts2)
        op2.merge(op1)
        rga1.merge(op2)
        rga2.merge(op2)

        rga1.get().shouldBeEmpty()
        rga2.get().shouldBeEmpty()
        shouldThrow<IndexOutOfBoundsException> {
            rga1.get(0)
        }
        shouldThrow<IndexOutOfBoundsException> {
            rga2.get(0)
        }
        rga1.iterator().shouldBeEmpty()
        rga2.iterator().shouldBeEmpty()
    }

    /**
     * This test evaluates the generation of delta plus its merging into another replica.
     * Call to get should return an array containing the values set by insertAt w.r.t the given
     * context.
     * Call to get at 0 should return the fourth value set by insertAt.
     * Call to get at 1 should return the second value set by insertAt.
     * Call to iterator should return an iterator containing the values set by insertAt w.r.t the given
     * context.
     */
    "generate delta" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val ts4 = client.tick()
        val vv = VersionVector()
        vv.update(ts2)
        val rga1 = RGA()
        val rga2 = RGA()

        rga1.insertAt(0, "A", ts1)
        rga1.insertAt(0, "B", ts3)
        rga1.insertAt(0, "C", ts2)
        rga1.insertAt(0, "D", ts4)
        val delta = rga1.generateDelta(vv)
        rga2.merge(delta)

        rga2.get().shouldContainExactly("D", "B")
        rga2.get(0).shouldBe("D")
        rga2.get(1).shouldBe("B")

        val it = rga2.iterator()
        for (value in rga2.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates JSON serialization of an empty RGA.
     **/
    "empty JSON serialization" {
        val rga = RGA()

        val rgaJson = rga.toJson()

        rgaJson.shouldBe("""{"_type":"RGA","_metadata":[],"value":[]}""")
    }

    /**
     * This test evaluates JSON deserialization of an empty RGA.
     **/
    "empty JSON deserialization" {
        val rgaJson = RGA.fromJson("""{"_type":"RGA","_metadata":[],"value":[]}""")

        rgaJson.get().shouldBeEmpty()
    }

    /**
     * This test evaluates JSON serialization of an RGA.
     **/
    "JSON serialization" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val ts4 = client.tick()
        val rga = RGA()

        rga.insertAt(0, "A", ts1)
        rga.insertAt(1, "B", ts2)
        rga.removeAt(1, ts3)
        rga.insertAt(1, "C", ts4)
        val rgaJson = rga.toJson()

        rgaJson.shouldBe("""{"_type":"RGA","_metadata":[{"anchor":null,"uid":{"uid":{"name":"clientid"},"cnt":-2147483647},"ts":{"uid":{"name":"clientid"},"cnt":-2147483647},"removed":false},{"anchor":{"uid":{"name":"clientid"},"cnt":-2147483647},"uid":{"uid":{"name":"clientid"},"cnt":-2147483644},"ts":{"uid":{"name":"clientid"},"cnt":-2147483644},"removed":false},{"atom":"B","anchor":{"uid":{"name":"clientid"},"cnt":-2147483647},"uid":{"uid":{"name":"clientid"},"cnt":-2147483646},"ts":{"uid":{"name":"clientid"},"cnt":-2147483645},"removed":true}],"value":["A","C"]}""")
    }

    /**
     * This test evaluates JSON deserialization of an RGA.
     **/
    "JSON deserialization" {
        val rgaJson = RGA.fromJson("""{"_type":"RGA","_metadata":[{"anchor":null,"uid":{"uid":{"name":"clientid"},"cnt":-2147483647},"ts":{"uid":{"name":"clientid"},"cnt":-2147483647},"removed":false},{"anchor":{"uid":{"name":"clientid"},"cnt":-2147483647},"uid":{"uid":{"name":"clientid"},"cnt":-2147483644},"ts":{"uid":{"name":"clientid"},"cnt":-2147483644},"removed":false},{"atom":"B","anchor":{"uid":{"name":"clientid"},"cnt":-2147483647},"uid":{"uid":{"name":"clientid"},"cnt":-2147483646},"ts":{"uid":{"name":"clientid"},"cnt":-2147483645},"removed":true}],"value":["A","C"]}""")

        rgaJson.get().shouldContainExactly("A", "C")
    }
})
