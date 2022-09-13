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
import crdtlib.utils.VersionVector
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.iterator.shouldBeEmpty
import io.kotest.matchers.iterator.shouldHaveNext

/**
 * Represents a suite test for RGA.
 */
class RGATest : StringSpec({

    val uid1 = ClientUId("clientid1")
    val uid2 = ClientUId("clientid2")
    val uid3 = ClientUId("clientid3")
    var client1 = SimpleEnvironment(uid1)
    var client2 = SimpleEnvironment(uid2)
    var client3 = SimpleEnvironment(uid3)

    beforeTest {
        client1 = SimpleEnvironment(uid1)
        client2 = SimpleEnvironment(uid2)
        client3 = SimpleEnvironment(uid3)
    }

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
        val rga = RGA(client1)

        rga.insertAt(0, "A")

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
        val rga = RGA(client1)

        rga.insertAt(0, "B")
        rga.insertAt(0, "A")

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
        val rga = RGA(client1)

        rga.insertAt(0, "A")
        rga.insertAt(1, "B")

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
        val rga = RGA(client1)

        rga.insertAt(0, "A")
        rga.removeAt(0)

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
        val rga = RGA(client1)

        rga.insertAt(0, "A")
        rga.insertAt(0, "B")
        rga.removeAt(0)
        rga.removeAt(0)

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
        val rga = RGA(client1)

        rga.insertAt(0, "A")
        rga.insertAt(1, "B")
        rga.removeAt(0)
        rga.insertAt(1, "C")

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
        val rga = RGA(client1)

        rga.insertAt(0, "A")
        rga.insertAt(1, "B")
        rga.removeAt(1)
        rga.insertAt(1, "C")

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
    "R1: insert at 0; R2: merge, get/iterator" {
        val rga1 = RGA(client1)
        val rga2 = RGA(client1)

        rga1.insertAt(0, "A")
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
        val rga1 = RGA(client1)
        val rga2 = RGA(client1)

        rga1.insertAt(0, "B")
        rga1.insertAt(0, "A")
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
        val rga1 = RGA(client1)
        val rga2 = RGA(client1)

        rga1.insertAt(0, "A")
        rga1.insertAt(1, "B")
        rga1.insertAt(2, "C")
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

    "R1: insert at 0; R2: insert at 0, merge, get/iterator" {
        val rga1 = RGA(client1)
        val rga2 = RGA(client2)

        rga1.insertAt(0, "B")
        rga2.insertAt(0, "A")
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
        val rga1 = RGA(client1)
        val rga2 = RGA(client2)

        rga1.insertAt(0, "D")
        rga1.insertAt(0, "B")
        rga2.insertAt(0, "C")
        rga2.insertAt(0, "A")
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

    "R1: insert at 0, 1; R2: insert at 0, 1, merge, get/iterator" {
        val rga1 = RGA(client1)
        val rga2 = RGA(client2)

        rga1.insertAt(0, "C")
        rga1.insertAt(1, "D")
        rga2.insertAt(0, "A")
        rga2.insertAt(1, "B")
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

        rga2.merge(rga1)
        rga2.get(0).shouldBe("A")
        rga2.get(1).shouldBe("B")
        rga2.get(2).shouldBe("C")
        rga2.get(3).shouldBe("D")
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
        val rga1 = RGA(client1)
        val rga2 = RGA(client2)

        rga1.insertAt(0, "A")
        rga1.insertAt(1, "B")
        rga1.insertAt(2, "C")
        rga1.insertAt(3, "D")
        rga2.merge(rga1)
        rga1.removeAt(1)
        rga2.removeAt(2)
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
     * This test evaluates the scenario: insert at 0 and 1 in all replicas,
     * then merge first and second replica into the third one.
     * Call to get in the third replica should return an array containing the
     * six values correctly ordered: third replica's values first, then second
     * replica's values, and finally first replica's values.
     * Call to iterator should return an iterator containing the six values
     * correctly ordered.
     */
    "R1, R2, R3: insert 0, 1 ; merge R1, R2 -> R3" {
        val rga1 = RGA(client1)
        val rga2 = RGA(client2)
        val rga3 = RGA(client3)

        // client3 has the largest uid → will be first in merged RGA
        rga3.insertAt(0, "A")
        rga3.insertAt(1, "B")
        rga2.insertAt(0, "C")
        rga2.insertAt(1, "D")
        rga1.insertAt(0, "E")
        rga1.insertAt(1, "F")
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
     * This test evaluates the scenario: insert at 0 and 1 in all replicas,
     * then merge second and third replica into the first one.
     * Call to get in the first replica should return an array containing the
     * six values correctly ordered: third replica's values first, then second
     * replica's values, and finally first replica's values.
     * Call to iterator should return an iterator containing the six values
     * correctly ordered.
     */
    "R1, R2, R3: insert 0, 1 ; merge R3, R2 -> R1" {
        val rga1 = RGA(client1)
        val rga2 = RGA(client2)
        val rga3 = RGA(client3)

        // client3 has the largest uid → will be first in merged RGA
        rga3.insertAt(0, "A")
        rga3.insertAt(1, "B")
        rga2.insertAt(0, "C")
        rga2.insertAt(1, "D")
        rga1.insertAt(0, "E")
        rga1.insertAt(1, "F")
        rga1.merge(rga3)
        rga1.merge(rga2)

        rga1.get().shouldContainExactly("A", "B", "C", "D", "E", "F")
        rga1.get(0).shouldBe("A")
        rga1.get(1).shouldBe("B")
        rga1.get(2).shouldBe("C")
        rga1.get(3).shouldBe("D")
        rga1.get(4).shouldBe("E")
        rga1.get(5).shouldBe("F")

        val it = rga1.iterator()
        for (value in rga1.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario of issue #35
     *
     * 1362 13452 134562
     *   1       1       1
     *  / \     / \     / \
     * 3   2   3   2   3   2
     * |      /       / \
     * 6     4       4   6
     *       |       |
     *       5       5
     *
     * (assuming element 6 has a smaller timestamp than element 4)
     * - 1, 2 and 3 are added and synchronized
     * - 6 is added on R1, 4 and 5 on R2 (concurrently)
     * - R1 to R2 are merged
     */
    "merge R1: 1362 and R2: 13452" {
        val rga1 = RGA(client1)
        val rga2 = RGA(client2)

        // add 132 to R1
        rga1.insertAt(0, "1")
        rga1.insertAt(1, "2")
        rga1.insertAt(1, "3")

        // merge R1 → R2
        rga2.merge(rga1)

        // add 6 to R1 (1362)
        rga1.insertAt(2, "6")

        rga1.get().shouldContainExactly("1", "3", "6", "2")
        rga1.get(0).shouldBe("1")
        rga1.get(1).shouldBe("3")
        rga1.get(2).shouldBe("6")
        rga1.get(3).shouldBe("2")
        var it1 = rga1.iterator()
        for (value in rga1.get()) {
            it1.shouldHaveNext()
            it1.next().shouldBe(value)
        }
        it1.shouldBeEmpty()

        // add 4,5 to R2 (13452)
        rga2.insertAt(2, "4")
        rga2.insertAt(3, "5")

        rga2.get().shouldContainExactly("1", "3", "4", "5", "2")
        rga2.get(0).shouldBe("1")
        rga2.get(1).shouldBe("3")
        rga2.get(2).shouldBe("4")
        rga2.get(3).shouldBe("5")
        rga2.get(4).shouldBe("2")
        var it2 = rga2.iterator()
        for (value in rga2.get()) {
            it2.shouldHaveNext()
            it2.next().shouldBe(value)
        }
        it2.shouldBeEmpty()

        // merge R1 and R2
        rga2.merge(rga1)
        rga1.merge(rga2)

        rga1.get().shouldContainExactly("1", "3", "4", "5", "6", "2")
        rga1.get(0).shouldBe("1")
        rga1.get(1).shouldBe("3")
        rga1.get(2).shouldBe("4")
        rga1.get(3).shouldBe("5")
        rga1.get(4).shouldBe("6")
        rga1.get(5).shouldBe("2")
        it1 = rga1.iterator()
        for (value in rga1.get()) {
            it1.shouldHaveNext()
            it1.next().shouldBe(value)
        }
        it1.shouldBeEmpty()
        rga2.get().shouldContainExactly("1", "3", "4", "5", "6", "2")
        rga2.get(0).shouldBe("1")
        rga2.get(1).shouldBe("3")
        rga2.get(2).shouldBe("4")
        rga2.get(3).shouldBe("5")
        rga2.get(4).shouldBe("6")
        rga2.get(5).shouldBe("2")
        it2 = rga2.iterator()
        for (value in rga2.get()) {
            it2.shouldHaveNext()
            it2.next().shouldBe(value)
        }
        it2.shouldBeEmpty()
    }

    /**
     * A similar scenario with a deeper tree
     *
     * 1346572 13465982 134659872
     *       1         1         1
     *      / \       / \       / \
     *     3   2     3   2     3   2
     *    /         /         /
     *   4         4         4
     *  / \       / \       / \
     * 6   5     6   5     6   5
     *     |        / \       /|\
     *     7       9   8     9 8 7
     *
     * (assuming element 7 has a smaller timestamp than element 8)
     * - 1 to 5 are added and synchronized
     * - 7 is added on R1, 8 and 9 on R2 (concurrently)
     * - R1 to R2 are merged
     */
    "merge R1: 1346572 and R@: 13465982" {
        val rga1 = RGA(client1)
        val rga2 = RGA(client2)

        // add 134652 to R1
        rga1.insertAt(0, "1")
        rga1.insertAt(1, "2")
        rga1.insertAt(1, "3")
        rga1.insertAt(2, "4")
        rga1.insertAt(3, "5")
        rga1.insertAt(3, "6")

        // merge R1 → R2
        rga2.merge(rga1)

        // add 7 to R1 (1346572)
        rga1.insertAt(5, "7")

        rga1.get().shouldContainExactly("1", "3", "4", "6", "5", "7", "2")
        rga1.get(0).shouldBe("1")
        rga1.get(1).shouldBe("3")
        rga1.get(2).shouldBe("4")
        rga1.get(3).shouldBe("6")
        rga1.get(4).shouldBe("5")
        rga1.get(5).shouldBe("7")
        rga1.get(6).shouldBe("2")
        var it1 = rga1.iterator()
        for (value in rga1.get()) {
            it1.shouldHaveNext()
            it1.next().shouldBe(value)
        }
        it1.shouldBeEmpty()

        // add 8,9 to R2 (13465982)
        rga2.insertAt(5, "8")
        rga2.insertAt(5, "9")

        rga2.get().shouldContainExactly("1", "3", "4", "6", "5", "9", "8", "2")
        rga2.get(0).shouldBe("1")
        rga2.get(1).shouldBe("3")
        rga2.get(2).shouldBe("4")
        rga2.get(3).shouldBe("6")
        rga2.get(4).shouldBe("5")
        rga2.get(5).shouldBe("9")
        rga2.get(6).shouldBe("8")
        rga2.get(7).shouldBe("2")
        var it2 = rga2.iterator()
        for (value in rga2.get()) {
            it2.shouldHaveNext()
            it2.next().shouldBe(value)
        }
        it2.shouldBeEmpty()

        // merge R1 and R2
        rga2.merge(rga1)
        rga1.merge(rga2)

        rga1.get().shouldContainExactly("1", "3", "4", "6", "5", "9", "8", "7", "2")
        rga1.get(0).shouldBe("1")
        rga1.get(1).shouldBe("3")
        rga1.get(2).shouldBe("4")
        rga1.get(3).shouldBe("6")
        rga1.get(4).shouldBe("5")
        rga1.get(5).shouldBe("9")
        rga1.get(6).shouldBe("8")
        rga1.get(7).shouldBe("7")
        rga1.get(8).shouldBe("2")
        it1 = rga1.iterator()
        for (value in rga1.get()) {
            it1.shouldHaveNext()
            it1.next().shouldBe(value)
        }
        it1.shouldBeEmpty()
        rga2.get().shouldContainExactly("1", "3", "4", "6", "5", "9", "8", "7", "2")
        rga2.get(0).shouldBe("1")
        rga2.get(1).shouldBe("3")
        rga2.get(2).shouldBe("4")
        rga2.get(3).shouldBe("6")
        rga2.get(4).shouldBe("5")
        rga2.get(5).shouldBe("9")
        rga2.get(6).shouldBe("8")
        rga2.get(7).shouldBe("7")
        rga2.get(8).shouldBe("2")
        it2 = rga2.iterator()
        for (value in rga2.get()) {
            it2.shouldHaveNext()
            it2.next().shouldBe(value)
        }
        it2.shouldBeEmpty()
    }

    /**
     * Same scenario without the 2 (up to the root)
     *
     * 134657 1346598 13465987
     *       1       1       1
     *      /       /       /
     *     3       3       3
     *    /       /       /
     *   4       4       4
     *  / \     / \     / \
     * 6   5   6   5   6   5
     *     |      / \     /|\
     *     7     9   8   9 8 7
     *
     * (assuming element 7 has a smaller timestamp than element 8)
     * - 1 to 5 are added and synchronized
     * - 7 is added on R1, 8 and 9 on R2 (concurrently)
     * - R1 to R2 are merged
     */
    "merge R1: 134657 and R2: 1346598" {
        val rga1 = RGA(client1)
        val rga2 = RGA(client2)

        // add 13465 to R1
        rga1.insertAt(0, "1")
        rga1.insertAt(1, "3")
        rga1.insertAt(2, "4")
        rga1.insertAt(3, "5")
        rga1.insertAt(3, "6")

        // merge R1 → R2
        rga2.merge(rga1)

        // add 7 to R1 (134657)
        rga1.insertAt(5, "7")

        rga1.get().shouldContainExactly("1", "3", "4", "6", "5", "7")
        rga1.get(0).shouldBe("1")
        rga1.get(1).shouldBe("3")
        rga1.get(2).shouldBe("4")
        rga1.get(3).shouldBe("6")
        rga1.get(4).shouldBe("5")
        rga1.get(5).shouldBe("7")
        var it1 = rga1.iterator()
        for (value in rga1.get()) {
            it1.shouldHaveNext()
            it1.next().shouldBe(value)
        }
        it1.shouldBeEmpty()

        // add 8,9 to R2 (1346598)
        rga2.insertAt(5, "8")
        rga2.insertAt(5, "9")

        rga2.get().shouldContainExactly("1", "3", "4", "6", "5", "9", "8")
        rga2.get(0).shouldBe("1")
        rga2.get(1).shouldBe("3")
        rga2.get(2).shouldBe("4")
        rga2.get(3).shouldBe("6")
        rga2.get(4).shouldBe("5")
        rga2.get(5).shouldBe("9")
        rga2.get(6).shouldBe("8")
        var it2 = rga2.iterator()
        for (value in rga2.get()) {
            it2.shouldHaveNext()
            it2.next().shouldBe(value)
        }
        it2.shouldBeEmpty()

        // merge R1 and R2
        rga2.merge(rga1)
        rga1.merge(rga2)

        rga1.get().shouldContainExactly("1", "3", "4", "6", "5", "9", "8", "7")
        rga1.get(0).shouldBe("1")
        rga1.get(1).shouldBe("3")
        rga1.get(2).shouldBe("4")
        rga1.get(3).shouldBe("6")
        rga1.get(4).shouldBe("5")
        rga1.get(5).shouldBe("9")
        rga1.get(6).shouldBe("8")
        rga1.get(7).shouldBe("7")
        it1 = rga1.iterator()
        for (value in rga1.get()) {
            it1.shouldHaveNext()
            it1.next().shouldBe(value)
        }
        it1.shouldBeEmpty()
        rga2.get().shouldContainExactly("1", "3", "4", "6", "5", "9", "8", "7")
        rga2.get(0).shouldBe("1")
        rga2.get(1).shouldBe("3")
        rga2.get(2).shouldBe("4")
        rga2.get(3).shouldBe("6")
        rga2.get(4).shouldBe("5")
        rga2.get(5).shouldBe("9")
        rga2.get(6).shouldBe("8")
        rga2.get(7).shouldBe("7")
        it2 = rga2.iterator()
        for (value in rga2.get()) {
            it2.shouldHaveNext()
            it2.next().shouldBe(value)
        }
        it2.shouldBeEmpty()
    }

    /**
     * This test evaluates the use of delta return by call to insertAt method.
     * Call to get should return an array containing the value inserted in replica 1.
     * Call to get at 0 should return the value inserted in replica 1.
     * Call to iterator should return an iterator containing the value inserted in replica 1.
     */
    "use delta returned by insert" {
        val rga1 = RGA(client1)
        val rga2 = RGA(client1)

        val returnedInsertOp = rga1.insertAt(0, "A")
        val insertOp = client1.popWrite().second
        returnedInsertOp.shouldBe(insertOp)

        rga1.merge(insertOp)
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
        val rga1 = RGA(client1)
        val rga2 = RGA(client1)

        rga1.insertAt(0, "A")
        rga2.merge(rga1)
        val returnedRemoveOp = rga1.removeAt(0)
        val removeOp = client1.popWrite().second
        returnedRemoveOp.shouldBe(removeOp)

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
        val rga1 = RGA(client1)
        val rga2 = RGA(client1)

        val returnedInsertOp = rga1.insertAt(0, "A")
        val insertOp = client1.popWrite().second
        returnedInsertOp.shouldBe(insertOp)
        val returnedRemoveOp = rga1.removeAt(0)
        val removeOp = client1.popWrite().second
        returnedRemoveOp.shouldBe(removeOp)

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
        val rga1 = RGA(client1)
        val rga2 = RGA(client1)

        val returnedOp1 = rga1.insertAt(0, "A")
        val op1 = client1.popWrite().second
        returnedOp1.shouldBe(op1)
        val returnedOp2 = rga1.removeAt(0)
        val op2 = client1.popWrite().second
        returnedOp2.shouldBe(op2)

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
        val rga1 = RGA(client1)
        val rga2 = RGA(client1)

        val returnedOp1 = rga1.insertAt(0, "A")
        val op1 = client1.popWrite().second
        returnedOp1.shouldBe(op1)
        val returnedOp2 = rga1.removeAt(0)
        val op2 = client1.popWrite().second
        returnedOp2.shouldBe(op2)

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
        val vv = VersionVector()
        val rga1 = RGA(client1)
        val rga2 = RGA(client1)

        rga1.insertAt(0, "A")
        rga1.insertAt(0, "B")
        vv.update(client1.tick())
        rga1.insertAt(0, "C")
        rga1.insertAt(0, "D")
        val delta = rga1.generateDelta(vv)
        rga2.merge(delta)

        rga2.get().shouldContainExactly("D", "C")
        rga2.get(0).shouldBe("D")
        rga2.get(1).shouldBe("C")

        val it = rga2.iterator()
        for (value in rga2.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    "Read Only Environment" {
        client2 = ReadOnlyEnvironment(uid2)
        val rga1 = RGA(client1)
        val rga2 = RGA(client2)

        rga1.insertAt(0, "A")
        rga1.insertAt(1, "B")
        rga1.removeAt(1)
        rga2.merge(rga1)

        shouldThrow<RuntimeException> {
            rga2.insertAt(1, "C")
        }
        shouldThrow<RuntimeException> {
            rga2.removeAt(0)
        }
        rga1.get().shouldHaveSingleElement("A")
        rga2.get().shouldHaveSingleElement("A")
    }

    /**
     * This test evaluates JSON serialization of an empty RGA.
     */
    "empty JSON serialization" {
        val rga = RGA()

        val rgaJson = rga.toJson()

        rgaJson.shouldBe("""{"type":"RGA","metadata":[],"value":[]}""")
    }

    /**
     * This test evaluates JSON deserialization of an empty RGA.
     */
    "empty JSON deserialization" {
        val rgaJson = RGA.fromJson("""{"type":"RGA","metadata":[],"value":[]}""")

        rgaJson.get().shouldBeEmpty()
    }

    /**
     * This test evaluates JSON serialization of an RGA.
     */
    "JSON serialization" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val rga = RGA(client)

        rga.insertAt(0, "A")
        rga.insertAt(1, "B")
        rga.removeAt(1)
        rga.insertAt(1, "C")
        val rgaJson = rga.toJson()

        rgaJson.shouldBe("""{"type":"RGA","metadata":[{"anchor":null,"uid":{"uid":{"name":"clientid"},"cnt":-2147483647},"ts":{"uid":{"name":"clientid"},"cnt":-2147483647},"removed":false},{"anchor":{"uid":{"name":"clientid"},"cnt":-2147483647},"uid":{"uid":{"name":"clientid"},"cnt":-2147483644},"ts":{"uid":{"name":"clientid"},"cnt":-2147483644},"removed":false},{"atom":"B","anchor":{"uid":{"name":"clientid"},"cnt":-2147483647},"uid":{"uid":{"name":"clientid"},"cnt":-2147483646},"ts":{"uid":{"name":"clientid"},"cnt":-2147483645},"removed":true}],"value":["A","C"]}""")
    }

    /**
     * This test evaluates JSON deserialization of an RGA.
     */
    "JSON deserialization" {
        val rgaJson = RGA.fromJson("""{"type":"RGA","metadata":[{"anchor":null,"uid":{"uid":{"name":"clientid"},"cnt":-2147483647},"ts":{"uid":{"name":"clientid"},"cnt":-2147483647},"removed":false},{"anchor":{"uid":{"name":"clientid"},"cnt":-2147483647},"uid":{"uid":{"name":"clientid"},"cnt":-2147483644},"ts":{"uid":{"name":"clientid"},"cnt":-2147483644},"removed":false},{"atom":"B","anchor":{"uid":{"name":"clientid"},"cnt":-2147483647},"uid":{"uid":{"name":"clientid"},"cnt":-2147483646},"ts":{"uid":{"name":"clientid"},"cnt":-2147483645},"removed":true}],"value":["A","C"]}""")

        rgaJson.get().shouldContainExactly("A", "C")
    }
})
