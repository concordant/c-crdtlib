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

import crdtlib.crdt.RGA
import crdtlib.utils.DCUId
import crdtlib.utils.Timestamp
import crdtlib.utils.SimpleEnvironment
import crdtlib.utils.VersionVector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
* Represents a suite test for RGA.
**/
class RGATest {

    /**
    * This test evaluates the scenario: create, get.
    * Call to get should return an empty array.
    */
    @Test
    fun createGet() {
        val rga = RGA<Char>()
        assertEquals(listOf(), rga.get())
    }

    /**
    * This test evaluates the scenario: insert at 0, get.
    * Call to get should return an array containing the inserted value.
    */
    @Test
    fun add0Get() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts)

        assertEquals(listOf('A'), rga.get())
    }

    /**
    * This test evaluates the scenario: insert at 0 twice, get.
    * Call to get should return an array containing the two inserted values.
    * Second value should be at index 0 and first value at index 1.
    */
    @Test
    fun add0Add0Get() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga = RGA<Char>()

        rga.insertAt(0, 'B', ts1)
        rga.insertAt(0, 'A', ts2)

        assertEquals(listOf('A', 'B'), rga.get())
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1, get.
    * Call to get should return an array containing the two inserted values.
    * First value should be at index 0 and second value at index 1.
    */
    @Test
    fun add0Add1Get() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(1, 'B', ts2)

        assertEquals(listOf('A', 'B'), rga.get())
    }

    /**
    * This test evaluates the scenario: insert at 0, remove at 0, get.
    * Call to get should return an empty array.
    */
    @Test
    fun add0Remove0Get() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.removeAt(0, ts2)

        assertEquals(listOf(), rga.get())
    }

    /**
    * This test evaluates the scenario: insert at 0 twice, remove at 0 twice, get.
    * Call to get should return an empty array.
    */
    @Test
    fun add0Add0Remove0Remove0Get() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(0, 'B', ts2)
        rga.removeAt(0, ts3)
        rga.removeAt(0, ts4)

        assertEquals(listOf(), rga.get())
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1, remove at 0, insert at 1, get.
    * Call to get should return an array containing the two last inserted values.
    * Second inserted value should be at index 0 and third inserted value at index 1.
    */
    @Test
    fun add0Add1Remove0Add1Get() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(1, 'B', ts2)
        rga.removeAt(0, ts3)
        rga.insertAt(1, 'C', ts4)

        assertEquals(listOf('B', 'C'), rga.get())
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1, remove at 1, insert at 1, get.
    * Call to get should return an array containing the first and third inserted values.
    * First inserted value should be at index 0 and thrid inserted value at index 1.
    */
    @Test
    fun add0Add1Remove1Add1Get() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(1, 'B', ts2)
        rga.removeAt(1, ts3)
        rga.insertAt(1, 'C', ts4)

        assertEquals(listOf('A', 'C'), rga.get())
    }

    /**
    * This test evaluates the scenario: insert at 0 || merge, get.
    * Call to get should return an array containing the value inserted in replica 1.
    */
    @Test
    fun add0_MergeGet() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'A', ts)
        rga2.merge(rga1)

        assertEquals(listOf('A'), rga2.get())
    }

    /**
    * This test evaluates the scenario: insert at 0 twice || merge, get.
    * Call to get should return an array containing the two values inserted in replica 1.
    */
    @Test
    fun add0Add0_MergeGet() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'B', ts1)
        rga1.insertAt(0, 'A', ts2)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B'), rga2.get())
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1, insert at 2 || merge, get.
    * Call to get should return an array containing the three values inserted in replica 1.
    */
    @Test
    fun add0Add1Add2_MergeGet() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'A', ts1)
        rga1.insertAt(1, 'B', ts2)
        rga1.insertAt(2, 'C', ts3)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B', 'C'), rga2.get())
    }

    /**
    * This test evaluates the scenario: insert at 0 || insert at 0 (with greater timestamp), merge
    * get.
    * Call to get should return an array containing the two values. Value inserted in replica 2
    * should be at index 0 and the one inserted at replica 1 at index 1.
    */
    @Test
    fun add0_AddWin0MergeGet() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'B', ts1)
        rga2.insertAt(0, 'A', ts2)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B'), rga2.get())
    }

    /**
    * This test evaluates the scenario: insert at 0 (with greater timestamp) || insert at 0, merge
    * get.
    * Call to get should return an array containing the two values. Value inserted in replica
    * 1 should be at index 0 and the one inserted at replica 2 at index 1.
    */
    @Test
    fun addWin0_Add0MergeGet() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'A', ts2)
        rga2.insertAt(0, 'B', ts1)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B'), rga2.get())
    }
 
    /**
    * This test evaluates the scenario: insert at 0 twice || insert at 0 twice, merge get.
    * Call to get should return an array containing the four values. Values should be ordered
    * according to decreasing order of their associated timestamp.
    */
    @Test
    fun add0Add0_Add0Add0MergeGet() {
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
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'D', ts1)
        rga1.insertAt(0, 'B', ts3)
        rga2.insertAt(0, 'C', ts2)
        rga2.insertAt(0, 'A', ts4)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B', 'C', 'D'), rga2.get())
    }

    /**
    * This test evaluates the scenario: insert at 0 (with greater timestamp), insert at 1 || insert
    * at 0, insert at 1, merge get.
    * Call to get should return an array containing the four values. Values inserted in replica 1
    * should be before the one inserted in replica 2.
    */
    @Test
    fun addWin0Add1_Add0Add1MergeGet() {
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
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'C', ts1)
        rga1.insertAt(1, 'D', ts3)
        rga2.insertAt(0, 'A', ts2)
        rga2.insertAt(1, 'B', ts4)
        rga1.merge(rga2)

        assertEquals(listOf('A', 'B', 'C', 'D'), rga1.get())
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1 || insert at 0 (with greater
    * timestamp), insert at 1, merge get.
    * Call to get should return an array containing the four values. Values inserted in replica 2
    * should be before the one inserted in replica 1.
    */
    @Test
    fun add0Add1_AddWin0Add1MergeGet() {
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
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'C', ts1)
        rga1.insertAt(1, 'D', ts3)
        rga2.insertAt(0, 'A', ts2)
        rga2.insertAt(1, 'B', ts4)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'B', 'C', 'D'), rga2.get())
    }

    /**
    * This test evaluates the scenario: insert four gets, remove at 1 || merge (after adds in
    * replica 1), remove at 2, merge, get.
    * Call to get should return an array containing the two values that have not been remove (the
    * first and the fourth one).
    */
    @Test
    fun add0Add1Add2Add3Remove1_MergeAfterAddsRemove2MergeGet() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts2 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts2)
        val ts3 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts3)
        val ts4 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts4)
        val ts5 = dc1.getNewTimestamp()
        val ts6 = dc2.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'A', ts1)
        rga1.insertAt(1, 'B', ts2)
        rga1.insertAt(2, 'C', ts3)
        rga1.insertAt(3, 'D', ts4)
        rga2.merge(rga1)
        rga1.removeAt(1, ts5)
        rga2.removeAt(2, ts6)
        rga2.merge(rga1)

        assertEquals(listOf('A', 'D'), rga2.get())
    }

    /**
     * This test evaluates the scenario of issue #35
     *                                  Expected      Faulty ordering
     * Replica A        Replica B       (ok on A)       on B of #35
     *  1362             13452           134562           134526
     *     1                 1               1               1
     *    / \               / \             / \             / \
     *   3   2             3   2           3   2           3   2
     *    \               /               / \             / \___
     *     6             4               4   6           4   6
     *                   |               |               |
     *                   5               5               5
     *
     * (assuming element 6 has a smaller timestamp than element 4)
     * - 1, 2 and 3 are added and synchronized
     * - 6 is added on A, 4 and 5 on B (concurrently)
     * - A to B are merged
     */
    @Test
    fun mergeA1362_B13452() {
	val uidA = DCUId("dcidA")
	val uidB = DCUId("dcidB")
	val envA = SimpleEnvironment(uidA)
	val envB = SimpleEnvironment(uidB)
	val rgaA = RGA<Int>()
	val rgaB = RGA<Int>()
	var tsA: Timestamp
	var tsB: Timestamp

	// add 132 to A
	tsA = envA.getNewTimestamp()
	envA.updateStateTS(tsA)
	rgaA.insertAt(0, 1, tsA)

	tsA = envA.getNewTimestamp()
	envA.updateStateTS(tsA)
	rgaA.insertAt(1, 2, tsA)

	tsA = envA.getNewTimestamp()
	envA.updateStateTS(tsA)
	rgaA.insertAt(1, 3, tsA)

	// merge A → B
	rgaB.merge(rgaA)
	envB.updateStateVV(envA.getCurrentState())

	// add 6 to A (1362)
	tsA = envA.getNewTimestamp()
	envA.updateStateTS(tsA)
	rgaA.insertAt(2, 6, tsA)

	assertEquals(listOf(1, 3, 6, 2), rgaA.get())

	// add 4,5 to B (13452)
	tsB = envB.getNewTimestamp()
	envB.updateStateTS(tsB)
	rgaB.insertAt(2, 4, tsB)

	tsB = envB.getNewTimestamp()
	envB.updateStateTS(tsB)
	rgaB.insertAt(3, 5, tsB)

	assertEquals(listOf(1, 3, 4, 5, 2), rgaB.get())

	// merge A and B
	rgaB.merge(rgaA)
	envB.updateStateVV(envA.getCurrentState())
	rgaA.merge(rgaB)
	envA.updateStateVV(envB.getCurrentState())

	assertEquals(rgaA.get(), rgaB.get())
    }

    /**
     * A similar scenario with a deeper tree
     *
     * Replica A        Replica B       Expected
     *  1346572         13465982        134659872
     *        1               1               1
     *       / \             / \             / \
     *      3   2           3   2           3   2
     *     /               /               /
     *    4               4               4
     *   / \             / \             / \
     *  6   5           6   5           6   5
     *      |              / \             /|\
     *      7             9   8           9 8 7
     *
     * (assuming element 7 has a smaller timestamp than element 8)
     * - 1 to 5 are added and synchronized
     * - 7 is added on A, 8 and 9 on B (concurrently)
     * - A to B are merged
     */
    @Test
    fun mergeA1346572_B13465982() {
	val uidA = DCUId("dcidA")
	val uidB = DCUId("dcidB")
	val envA = SimpleEnvironment(uidA)
	val envB = SimpleEnvironment(uidB)
	val rgaA = RGA<Char>()
	val rgaB = RGA<Char>()
	var tsA: Timestamp
	var tsB: Timestamp

	// add 134652 to A
	tsA = envA.getNewTimestamp()
	envA.updateStateTS(tsA)
	rgaA.insertAt(0, '1', tsA)

	tsA = envA.getNewTimestamp()
	envA.updateStateTS(tsA)
	rgaA.insertAt(1, '2', tsA)

	tsA = envA.getNewTimestamp()
	envA.updateStateTS(tsA)
	rgaA.insertAt(1, '3', tsA)

	tsA = envA.getNewTimestamp()
	envA.updateStateTS(tsA)
	rgaA.insertAt(2, '4', tsA)

	tsA = envA.getNewTimestamp()
	envA.updateStateTS(tsA)
	rgaA.insertAt(3, '5', tsA)

	tsA = envA.getNewTimestamp()
	envA.updateStateTS(tsA)
	rgaA.insertAt(3, '6', tsA)

	// merge A → B
	rgaB.merge(rgaA)
	envB.updateStateVV(envA.getCurrentState())

	// add 7 to A (1346572)
	tsA = envA.getNewTimestamp()
	envA.updateStateTS(tsA)
	rgaA.insertAt(5, '7', tsA)

	assertEquals(listOf('1', '3', '4', '6', '5', '7', '2'), rgaA.get())

	// add 8,9 to B (13465982)
	tsB = envB.getNewTimestamp()
	envB.updateStateTS(tsB)
	rgaB.insertAt(5, '8', tsB)

	tsB = envB.getNewTimestamp()
	envB.updateStateTS(tsB)
	rgaB.insertAt(5, '9', tsB)

	assertEquals(listOf('1', '3', '4', '6', '5', '9', '8', '2'),
		     rgaB.get())

	// merge A and B
	rgaB.merge(rgaA)
	envB.updateStateVV(envA.getCurrentState())
	rgaA.merge(rgaB)
	envA.updateStateVV(envB.getCurrentState())

	assertEquals(rgaA.get(), rgaB.get())
    }

    /**
     * Same scenario without the 2 (up to the root)
     *
     * Replica A        Replica B       Expected
     *  134657          1346598         13465987
     *        1               1               1
     *       /               /               /
     *      3               3               3
     *     /               /               /
     *    4               4               4
     *   / \             / \             / \
     *  6   5           6   5           6   5
     *      |              / \             /|\
     *      7             9   8           9 8 7
     *
     * (assuming element 7 has a smaller timestamp than element 8)
     * - 1 to 5 are added and synchronized
     * - 7 is added on A, 8 and 9 on B (concurrently)
     * - A to B are merged
     */

    @Test
    fun mergeA134657_B1346598() {
	val uidA = DCUId("dcidA")
	val uidB = DCUId("dcidB")
	val envA = SimpleEnvironment(uidA)
	val envB = SimpleEnvironment(uidB)
	val rgaA = RGA<Char>()
	val rgaB = RGA<Char>()
	var tsA: Timestamp
	var tsB: Timestamp

	// add 13465 to A
	tsA = envA.getNewTimestamp()
	envA.updateStateTS(tsA)
	rgaA.insertAt(0, '1', tsA)

	tsA = envA.getNewTimestamp()
	envA.updateStateTS(tsA)
	rgaA.insertAt(1, '3', tsA)

	tsA = envA.getNewTimestamp()
	envA.updateStateTS(tsA)
	rgaA.insertAt(2, '4', tsA)

	tsA = envA.getNewTimestamp()
	envA.updateStateTS(tsA)
	rgaA.insertAt(3, '5', tsA)

	tsA = envA.getNewTimestamp()
	envA.updateStateTS(tsA)
	rgaA.insertAt(3, '6', tsA)

	// merge A → B
	rgaB.merge(rgaA)
	envB.updateStateVV(envA.getCurrentState())

	// add 7 to A (134657)
	tsA = envA.getNewTimestamp()
	envA.updateStateTS(tsA)
	rgaA.insertAt(5, '7', tsA)

	assertEquals(listOf('1', '3', '4', '6', '5', '7'), rgaA.get())

	// add 8,9 to B (1346598)
	tsB = envB.getNewTimestamp()
	envB.updateStateTS(tsB)
	rgaB.insertAt(5, '8', tsB)

	tsB = envB.getNewTimestamp()
	envB.updateStateTS(tsB)
	rgaB.insertAt(5, '9', tsB)

	assertEquals(listOf('1', '3', '4', '6', '5', '9', '8'),
		     rgaB.get())

	// merge A and B
	rgaB.merge(rgaA)
	envB.updateStateVV(envA.getCurrentState())
	rgaA.merge(rgaB)
	envA.updateStateVV(envB.getCurrentState())

	assertEquals(rgaA.get(), rgaB.get())
    }

    /**
    * This test evaluates the scenario: insert at 0 (with greater timestamp) insert at 1 || insert
    * at 0 (with second greater timestamp), insert at 1 || insert at 0, insert at 1, merge from
    * replica 1, merge from replica 2, get.
    * Call to get should return an array containing the six values. Values inserted in replica 1
    * should be before the one inserted in replica 2 which should be before those inserted at
    * replica 3.
    */
    @Test
    fun addWin0Add1_AddSecond0Add1_Add0Add1Merge1Merge2Get() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val dc3 = SimpleEnvironment(uid3)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts4 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts5 = dc2.getNewTimestamp()
        dc3.updateStateTS(ts3)
        val ts6 = dc3.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()
        val rga3 = RGA<Char>()

        rga1.insertAt(0, 'A', ts3)
        rga1.insertAt(1, 'B', ts6)
        rga2.insertAt(0, 'C', ts2)
        rga2.insertAt(1, 'D', ts5)
        rga3.insertAt(0, 'E', ts1)
        rga3.insertAt(1, 'F', ts4)
        rga3.merge(rga1)
        rga3.merge(rga2)

        assertEquals(listOf('A', 'B', 'C', 'D', 'E', 'F'), rga3.get())
    }

    /**
    * This test evaluates the scenario: insert at 0 (with greater timestamp) insert at 1 || insert
    * at 0, insert at 1 || insert at 0 (with second greater timestamp), insert at 1, merge from
    * replica 1, merge from replica 2, get.
    * Call to get should return an array containing the six values. Values inserted in replica 1
    * should be before the one inserted in replica 3 which should be before those inserted at
    * replica 2.
    */
    @Test
    fun addWin0Add1_Add0Add1_AddSecond0Add1Merge1Merge2Get() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val dc3 = SimpleEnvironment(uid3)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts4 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts5 = dc2.getNewTimestamp()
        dc3.updateStateTS(ts3)
        val ts6 = dc3.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()
        val rga3 = RGA<Char>()

        rga1.insertAt(0, 'A', ts3)
        rga1.insertAt(1, 'B', ts6)
        rga2.insertAt(0, 'E', ts1)
        rga2.insertAt(1, 'F', ts4)
        rga3.insertAt(0, 'C', ts2)
        rga3.insertAt(1, 'D', ts5)
        rga3.merge(rga1)
        rga3.merge(rga2)

        assertEquals(listOf('A', 'B', 'C', 'D', 'E', 'F'), rga3.get())
    }

    /**
    * This test evaluates the scenario: insert at 0 (with second greater timestamp) insert at 1 ||
    * insert at 0 (with greater timestamp), insert at 1 || insert at 0, insert at 1, merge from
    * replica 1, merge from replica 2, get.
    * Call to get should return an array containing the six values. Values inserted in replica 2
    * should be before the one inserted in replica 1 which should be before those inserted at
    * replica 3.
    */
    @Test
    fun addSecond0Add1_AddWin0Add1_Add0Add1Merge1Merge2Get() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val dc3 = SimpleEnvironment(uid3)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts4 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts5 = dc2.getNewTimestamp()
        dc3.updateStateTS(ts3)
        val ts6 = dc3.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()
        val rga3 = RGA<Char>()

        rga1.insertAt(0, 'C', ts2)
        rga1.insertAt(1, 'D', ts5)
        rga2.insertAt(0, 'A', ts3)
        rga2.insertAt(1, 'B', ts6)
        rga3.insertAt(0, 'E', ts1)
        rga3.insertAt(1, 'F', ts4)
        rga3.merge(rga1)
        rga3.merge(rga2)

        assertEquals(listOf('A', 'B', 'C', 'D', 'E', 'F'), rga3.get())
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1 || insert at 0 (with greater
    * timestamp), insert at 1 || insert at 0 (with second greater timestamp), insert at 1, merge
    * from replica 1, merge from replica 2, get.
    * Call to get should return an array containing the six values. Values inserted in replica 2
    * should be before the one inserted in replica 3 which should be before those inserted at
    * replica 1.
    */
    @Test
    fun add0Add1_AddWin0Add1_AddSecond0Add1Merge1Merge2Get() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val dc3 = SimpleEnvironment(uid3)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts4 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts5 = dc2.getNewTimestamp()
        dc3.updateStateTS(ts3)
        val ts6 = dc3.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()
        val rga3 = RGA<Char>()

        rga1.insertAt(0, 'E', ts1)
        rga1.insertAt(1, 'F', ts4)
        rga2.insertAt(0, 'A', ts3)
        rga2.insertAt(1, 'B', ts6)
        rga3.insertAt(0, 'C', ts2)
        rga3.insertAt(1, 'D', ts5)
        rga3.merge(rga1)
        rga3.merge(rga2)

        assertEquals(listOf('A', 'B', 'C', 'D', 'E', 'F'), rga3.get())
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1 || insert at 0 (with second greater
    * timestamp), insert at 1 || insert at 0 (with greater timestamp), insert at 1, merge from
    * replica 1, merge from replica 2, get.
    * Call to get should return an array containing the six values. Values inserted in replica 3
    * should be before the one inserted in replica 2 which should be before those inserted at
    * replica 1.
    */
    @Test
    fun add0Add1_AddSecond0Add1_AddWin0Add1Merge1Merge2Get() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val dc3 = SimpleEnvironment(uid3)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts4 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts5 = dc2.getNewTimestamp()
        dc3.updateStateTS(ts3)
        val ts6 = dc3.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()
        val rga3 = RGA<Char>()

        rga1.insertAt(0, 'E', ts1)
        rga1.insertAt(1, 'F', ts4)
        rga2.insertAt(0, 'C', ts2)
        rga2.insertAt(1, 'D', ts5)
        rga3.insertAt(0, 'A', ts3)
        rga3.insertAt(1, 'B', ts6)
        rga3.merge(rga1)
        rga3.merge(rga2)

        assertEquals(listOf('A', 'B', 'C', 'D', 'E', 'F'), rga3.get())
    }

    /**
    * This test evaluates the scenario: insert at 0 (with second greater timestamp) insert at 1 ||
    * insert at 0, insert at 1 || insert at 0 (with greater timestamp), insert at 1, merge from
    * replica 1, merge from replica 2, get.
    * Call to get should return an array containing the six values. Values inserted in replica 3
    * should be before the one inserted in replica 1 which should be before those inserted at
    * replica 2.
    */
    @Test
    fun addSecond0Add1_Add0Add1_AddWin0Add1Merge1Merge2Get() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val dc3 = SimpleEnvironment(uid3)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts4 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts5 = dc2.getNewTimestamp()
        dc3.updateStateTS(ts3)
        val ts6 = dc3.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()
        val rga3 = RGA<Char>()

        rga1.insertAt(0, 'C', ts2)
        rga1.insertAt(1, 'D', ts5)
        rga2.insertAt(0, 'E', ts1)
        rga2.insertAt(1, 'F', ts4)
        rga3.insertAt(0, 'A', ts3)
        rga3.insertAt(1, 'B', ts6)
        rga3.merge(rga1)
        rga3.merge(rga2)

        assertEquals(listOf('A', 'B', 'C', 'D', 'E', 'F'), rga3.get())
    }

    /**
    * This test evaluates the use of delta return by call to insertAt method.
    * Call to get should return an array containing the value inserted in replica 1.
    */
    @Test
    fun insertOp() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        val insertOp = rga1.insertAt(0, 'A', ts)
        rga2.merge(insertOp)
        rga2.merge(insertOp)

        assertEquals(listOf('A'), rga1.get())
        assertEquals(listOf('A'), rga2.get())
    }

    /**
    * This test evaluates the use of delta return by call to removeAt method.
    * Call to get should return an empty array.
    */
    @Test
    fun removeOp() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'A', ts1)
        rga2.merge(rga1)
        val removeOp = rga1.removeAt(0, ts2)
        rga1.merge(removeOp)
        rga2.merge(removeOp)

        assertEquals(listOf(), rga1.get())
        assertEquals(listOf(), rga2.get())
    }

    /**
    * This test evaluates the use of delta return by call to insertAt and removeAt methods.
    * Call to get should return an empty array.
    */
    @Test
    fun insertRemoveOp() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        val insertOp = rga1.insertAt(0, 'A', ts1)
        val removeOp = rga1.removeAt(0, ts2)
        rga1.merge(insertOp)
        rga1.merge(removeOp)
        rga2.merge(insertOp)
        rga2.merge(removeOp)

        assertEquals(listOf(), rga1.get())
        assertEquals(listOf(), rga2.get())
    }

    /**
    * This test evaluates the merge of deltas returned by call to insertAt and removeAt methods.
    * Call to get should return an empty array.
    */
    @Test
    fun insertRemoveOpFusion() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        val op1 = rga1.insertAt(0, 'A', ts1)
        val op2 = rga1.removeAt(0, ts2)
        op1.merge(op2)
        rga1.merge(op1)
        rga2.merge(op1)

        assertEquals(listOf(), rga1.get())
        assertEquals(listOf(), rga2.get())
    }

    /**
    * This test evaluates the merge of deltas returned by call to removeAt and insertAt methods.
    * Call to get should return an empty array.
    */
    @Test
    fun removeInsertOpFusion() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        val op1 = rga1.insertAt(0, 'A', ts1)
        val op2 = rga1.removeAt(0, ts2)
        op2.merge(op1)
        rga1.merge(op2)
        rga2.merge(op2)

        assertEquals(listOf(), rga1.get())
        assertEquals(listOf(), rga2.get())
    }

    /**
    * This test evaluates the generation of delta plus its merging into another replica.
    * Call to get should return an array containing the values set yb insertAt w.r.t the given
    * context.
    */
    @Test
    fun generateDelta() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        val vv = VersionVector()
        vv.addTS(ts2)
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'A', ts1)
        rga1.insertAt(0, 'B', ts3)
        rga1.insertAt(0, 'C', ts2)
        rga1.insertAt(0, 'D', ts4)
        val delta = rga1.generateDelta(vv)
        rga2.merge(delta)

        assertEquals(listOf('D', 'B'), rga2.get())
    }

    /**
    * This test evaluates JSON serialization of an empty RGA.
    **/
    @Test
    fun emptyToJsonSerialization() {
        val rga = RGA<Char>()

        val rgaJson = rga.toJson(Char::class)

        assertEquals("""{"_type":"RGA","_metadata":[],"value":[]}""", rgaJson)
    }

    /**
    * This test evaluates JSON deserialization of an empty RGA.
    **/
    @Test
    fun emptyFromJsonDeserialization() {
        val rgaJson = RGA.fromJson(Char::class, """{"_type":"RGA","_metadata":[],"value":[]}""")

        assertEquals(listOf(), rgaJson.get())
    }

    /**
    * This test evaluates JSON serialization of an RGA.
    **/
    @Test
    fun toJsonSerialization() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(1, 'B', ts2)
        rga.removeAt(1, ts3)
        rga.insertAt(1, 'C', ts4)
        val rgaJson = rga.toJson(Char::class)

        assertEquals("""{"_type":"RGA","_metadata":[{"anchor":null,"uid":{"uid":{"name":"dcid"},"cnt":1},"ts":{"uid":{"name":"dcid"},"cnt":1},"removed":false},{"anchor":{"uid":{"name":"dcid"},"cnt":1},"uid":{"uid":{"name":"dcid"},"cnt":4},"ts":{"uid":{"name":"dcid"},"cnt":4},"removed":false},{"atom":"B","anchor":{"uid":{"name":"dcid"},"cnt":1},"uid":{"uid":{"name":"dcid"},"cnt":2},"ts":{"uid":{"name":"dcid"},"cnt":3},"removed":true}],"value":["A","C"]}""", rgaJson)
    }

    /**
    * This test evaluates JSON deserialization of an RGA.
    **/
    @Test
    fun fromJsonDeserialization() {
        val rgaJson = RGA.fromJson(Char::class, """{"_type":"RGA","_metadata":[{"anchor":null,"uid":{"uid":{"name":"dcid"},"cnt":1},"ts":{"uid":{"name":"dcid"},"cnt":1},"removed":false},{"anchor":{"uid":{"name":"dcid"},"cnt":1},"uid":{"uid":{"name":"dcid"},"cnt":4},"ts":{"uid":{"name":"dcid"},"cnt":4},"removed":false},{"atom":"B","anchor":{"uid":{"name":"dcid"},"cnt":1},"uid":{"uid":{"name":"dcid"},"cnt":2},"ts":{"uid":{"name":"dcid"},"cnt":3},"removed":true}],"value":["A","C"]}""")

        assertEquals(listOf('A', 'C'), rgaJson.get())
    }
}
