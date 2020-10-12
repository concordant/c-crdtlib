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
import crdtlib.utils.Timestamp
import crdtlib.utils.SimpleEnvironment
import crdtlib.utils.VersionVector
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*

/**
* Represents a suite test for RGA.
**/
class RGATest : StringSpec({

    /**
    * This test evaluates the scenario: create, get.
    * Call to get should return an empty array.
    */
    "create and get" {
        val rga = RGA<Char>()
        rga.get().shouldBeEmpty()
    }

    /**
    * This test evaluates the scenario: insert at 0, get.
    * Call to get should return an array containing the inserted value.
    */
    "insert at 0 and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts)

        rga.get().shouldHaveSingleElement('A')
    }

    /**
    * This test evaluates the scenario: insert at 0 twice, get.
    * Call to get should return an array containing the two inserted values.
    * Second value should be at index 0 and first value at index 1.
    */
    "insert at 0, insert at 0, get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val rga = RGA<Char>()

        rga.insertAt(0, 'B', ts1)
        rga.insertAt(0, 'A', ts2)

        rga.get().shouldContainExactly('A', 'B')
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1, get.
    * Call to get should return an array containing the two inserted values.
    * First value should be at index 0 and second value at index 1.
    */
    "insert at 0, insert at 1, get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(1, 'B', ts2)

        rga.get().shouldContainExactly('A', 'B')
    }

    /**
    * This test evaluates the scenario: insert at 0, remove at 0, get.
    * Call to get should return an empty array.
    */
    "inser at 0, remove at 0, get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.removeAt(0, ts2)

        rga.get().shouldBeEmpty()
    }

    /**
    * This test evaluates the scenario: insert at 0 twice, remove at 0 twice, get.
    * Call to get should return an empty array.
    */
    "insert at 0, insert at 0, remove at 0, remove at 0, get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val ts4 = client.tick()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(0, 'B', ts2)
        rga.removeAt(0, ts3)
        rga.removeAt(0, ts4)

        rga.get().shouldBeEmpty()
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1, remove at 0, insert at 1, get.
    * Call to get should return an array containing the two last inserted values.
    * Second inserted value should be at index 0 and third inserted value at index 1.
    */
    "insert at 0, insert at 1, remove at 0, insert at 1, get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val ts4 = client.tick()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(1, 'B', ts2)
        rga.removeAt(0, ts3)
        rga.insertAt(1, 'C', ts4)

        rga.get().shouldContainExactly('B', 'C')
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1, remove at 1, insert at 1, get.
    * Call to get should return an array containing the first and third inserted values.
    * First inserted value should be at index 0 and thrid inserted value at index 1.
    */
    "insert at 0, insert at 1, remove at 1, insert at 1, get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val ts4 = client.tick()
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(1, 'B', ts2)
        rga.removeAt(1, ts3)
        rga.insertAt(1, 'C', ts4)

        rga.get().shouldContainExactly('A', 'C')
    }

    /**
    * This test evaluates the scenario: insert at 0 || merge, get.
    * Call to get should return an array containing the value inserted in replica 1.
    */
    "R1: insert at 1; R2: merge, get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'A', ts)
        rga2.merge(rga1)

        rga2.get().shouldHaveSingleElement('A')
    }

    /**
    * This test evaluates the scenario: insert at 0 twice || merge, get.
    * Call to get should return an array containing the two values inserted in replica 1.
    */
    "R1: insert at 0, insert at 0; R2: merge, get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'B', ts1)
        rga1.insertAt(0, 'A', ts2)
        rga2.merge(rga1)

        rga2.get().shouldContainExactly('A', 'B')
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1, insert at 2 || merge, get.
    * Call to get should return an array containing the three values inserted in replica 1.
    */
    "R1: insert at 0, insert at 1, insert at 2; R2: merge, get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val ts3 = client.tick()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'A', ts1)
        rga1.insertAt(1, 'B', ts2)
        rga1.insertAt(2, 'C', ts3)
        rga2.merge(rga1)

        rga2.get().shouldContainExactly('A', 'B', 'C')
    }

    /**
    * This test evaluates the scenario: insert at 0 || insert at 0 (with greater timestamp), merge
    * get.
    * Call to get should return an array containing the two values. Value inserted in replica 2
    * should be at index 0 and the one inserted at replica 1 at index 1.
    */
    "R1: insert at 0; R2: insert at 0 with greater timestamp, merge, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'B', ts1)
        rga2.insertAt(0, 'A', ts2)
        rga2.merge(rga1)

        rga2.get().shouldContainExactly('A', 'B')
    }

    /**
    * This test evaluates the scenario: insert at 0 (with greater timestamp) || insert at 0, merge
    * get.
    * Call to get should return an array containing the two values. Value inserted in replica
    * 1 should be at index 0 and the one inserted at replica 2 at index 1.
    */
    "R1: insert at 0 with greater timestamp; R2: insert at 0, merge, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'A', ts2)
        rga2.insertAt(0, 'B', ts1)
        rga2.merge(rga1)

        rga2.get().shouldContainExactly('A', 'B')
    }
 
    /**
    * This test evaluates the scenario: insert at 0 twice || insert at 0 twice, merge get.
    * Call to get should return an array containing the four values. Values should be ordered
    * according to decreasing order of their associated timestamp.
    */
    "R1: insert at 0, insert at 0; R2: insert at 0, insert at 0, merge, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client1.tick()
        val ts4 = client2.tick()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'D', ts1)
        rga1.insertAt(0, 'B', ts3)
        rga2.insertAt(0, 'C', ts2)
        rga2.insertAt(0, 'A', ts4)
        rga2.merge(rga1)

        rga2.get().shouldContainExactly('A', 'B', 'C', 'D')
    }

    /**
    * This test evaluates the scenario: insert at 0 (with greater timestamp), insert at 1 || insert
    * at 0, insert at 1, merge get.
    * Call to get should return an array containing the four values. Values inserted in replica 1
    * should be before the one inserted in replica 2.
    */
    "insert at 0 with greater timestamp, insert at 1; R2: insert at 0, insert at 1, merge, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client1.tick()
        val ts4 = client2.tick()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'C', ts1)
        rga1.insertAt(1, 'D', ts3)
        rga2.insertAt(0, 'A', ts2)
        rga2.insertAt(1, 'B', ts4)
        rga1.merge(rga2)

        rga1.get().shouldContainExactly('A', 'B', 'C', 'D')
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1 || insert at 0 (with greater
    * timestamp), insert at 1, merge get.
    * Call to get should return an array containing the four values. Values inserted in replica 2
    * should be before the one inserted in replica 1.
    */
    "R1: insert at 0, insert at 1; R2: insert at 0 with greater timestamp, insert at 1, merge, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client1.tick()
        val ts4 = client2.tick()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'C', ts1)
        rga1.insertAt(1, 'D', ts3)
        rga2.insertAt(0, 'A', ts2)
        rga2.insertAt(1, 'B', ts4)
        rga2.merge(rga1)

        rga2.get().shouldContainExactly('A', 'B', 'C', 'D')
    }

    /**
    * This test evaluates the scenario: insert four times, remove at 1 || merge (after adds in
    * replica 1), remove at 2, merge, get.
    * Call to get should return an array containing the two values that have not been remove (the
    * first and the fourth one).
    */
    "R1: insert four times, remove at 1; R2: merge after inserts, remove at 2, merge, get" {
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

        rga2.get().shouldContainExactly('A', 'D')
    }

    /**
    * This test evaluates the scenario: insert at 0 (with greater timestamp) insert at 1 || insert
    * at 0 (with second greater timestamp), insert at 1 || insert at 0, insert at 1, merge from
    * replica 1, merge from replica 2, get.
    * Call to get should return an array containing the six values. Values inserted in replica 1
    * should be before the one inserted in replica 2 which should be before those inserted at
    * replica 3.
    */
    "R1: insert at 0 with greater timestamp, insert at 1; R2: insert at 0 with second timestamp, insert at 1; R3: insert at 0, insert at 1, merge R1 and R2, get" {
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

        rga3.get().shouldContainExactly('A', 'B', 'C', 'D', 'E', 'F')
    }

    /**
    * This test evaluates the scenario: insert at 0 (with greater timestamp) insert at 1 || insert
    * at 0, insert at 1 || insert at 0 (with second greater timestamp), insert at 1, merge from
    * replica 1, merge from replica 2, get.
    * Call to get should return an array containing the six values. Values inserted in replica 1
    * should be before the one inserted in replica 3 which should be before those inserted at
    * replica 2.
    */
    "R1: insert at 0 with greater timestamp, insert at 1; R2: insert at 0, insert at 1; R3: insert at 0 with second timestamp, insert at 1, merge R1 and R2, get" {
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

        rga3.get().shouldContainExactly('A', 'B', 'C', 'D', 'E', 'F')
    }

    /**
    * This test evaluates the scenario: insert at 0 (with second greater timestamp) insert at 1 ||
    * insert at 0 (with greater timestamp), insert at 1 || insert at 0, insert at 1, merge from
    * replica 1, merge from replica 2, get.
    * Call to get should return an array containing the six values. Values inserted in replica 2
    * should be before the one inserted in replica 1 which should be before those inserted at
    * replica 3.
    */
    "R1: insert at 0 with second timestamp, insert at 1; R2: insert at 0 with greater timestamp, insert at 1; R3: insert at 0, insert at 1, merge R1 and R2, get" {
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

        rga3.get().shouldContainExactly('A', 'B', 'C', 'D', 'E', 'F')
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1 || insert at 0 (with greater
    * timestamp), insert at 1 || insert at 0 (with second greater timestamp), insert at 1, merge
    * from replica 1, merge from replica 2, get.
    * Call to get should return an array containing the six values. Values inserted in replica 2
    * should be before the one inserted in replica 3 which should be before those inserted at
    * replica 1.
    */
    "R1: insert at 0, insert at 1; R2: insert at 0 with greater timestamp, insert at 1; R3: insert at 0 with second timestamp, insert at 1, merge R1 and R2, get" {
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

        rga3.get().shouldContainExactly('A', 'B', 'C', 'D', 'E', 'F')
    }

    /**
    * This test evaluates the scenario: insert at 0, insert at 1 || insert at 0 (with second greater
    * timestamp), insert at 1 || insert at 0 (with greater timestamp), insert at 1, merge from
    * replica 1, merge from replica 2, get.
    * Call to get should return an array containing the six values. Values inserted in replica 3
    * should be before the one inserted in replica 2 which should be before those inserted at
    * replica 1.
    */
    "R1: insert at 0, insert at 1; R2: insert at 0 with second timestamp, insert at 1; R3: insert at 0 with greater timestamp, insert at 1, merge R1 and R2, get" {
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

        rga3.get().shouldContainExactly('A', 'B', 'C', 'D', 'E', 'F')
    }

    /**
    * This test evaluates the scenario: insert at 0 (with second greater timestamp) insert at 1 ||
    * insert at 0, insert at 1 || insert at 0 (with greater timestamp), insert at 1, merge from
    * replica 1, merge from replica 2, get.
    * Call to get should return an array containing the six values. Values inserted in replica 3
    * should be before the one inserted in replica 1 which should be before those inserted at
    * replica 2.
    */
    "R1: insert at 0 with second timestamp, insert at 1; R2: insert at 0, insert at 1; R3: insert at 0 with greater timestamp, insert at 1, merge R1 and R2, get" {
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

        rga3.get().shouldContainExactly('A', 'B', 'C', 'D', 'E', 'F')
    }

    /**
    * This test evaluates the use of delta return by call to insertAt method.
    * Call to get should return an array containing the value inserted in replica 1.
    */
    "use delta returned by insert" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        val insertOp = rga1.insertAt(0, 'A', ts)
        rga2.merge(insertOp)
        rga2.merge(insertOp)

        rga1.get().shouldHaveSingleElement('A')
        rga2.get().shouldHaveSingleElement('A')
    }

    /**
    * This test evaluates the use of delta return by call to removeAt method.
    * Call to get should return an empty array.
    */
    "use delta returned by remove" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'A', ts1)
        rga2.merge(rga1)
        val removeOp = rga1.removeAt(0, ts2)
        rga1.merge(removeOp)
        rga2.merge(removeOp)

        rga1.get().shouldBeEmpty()
        rga2.get().shouldBeEmpty()
    }

    /**
    * This test evaluates the use of delta return by call to insertAt and removeAt methods.
    * Call to get should return an empty array.
    */
    "use delta returned by insert and remove" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        val insertOp = rga1.insertAt(0, 'A', ts1)
        val removeOp = rga1.removeAt(0, ts2)
        rga1.merge(insertOp)
        rga1.merge(removeOp)
        rga2.merge(insertOp)
        rga2.merge(removeOp)

        rga1.get().shouldBeEmpty()
        rga2.get().shouldBeEmpty()
    }

    /**
    * This test evaluates the merge of deltas returned by call to insertAt and removeAt methods.
    * Call to get should return an empty array.
    */
    "merge from delta insert to delta remove" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        val op1 = rga1.insertAt(0, 'A', ts1)
        val op2 = rga1.removeAt(0, ts2)
        op1.merge(op2)
        rga1.merge(op1)
        rga2.merge(op1)

        rga1.get().shouldBeEmpty()
        rga2.get().shouldBeEmpty()
    }

    /**
    * This test evaluates the merge of deltas returned by call to removeAt and insertAt methods.
    * Call to get should return an empty array.
    */
    "merge from delta remove to delta import" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        val op1 = rga1.insertAt(0, 'A', ts1)
        val op2 = rga1.removeAt(0, ts2)
        op2.merge(op1)
        rga1.merge(op2)
        rga2.merge(op2)

        rga1.get().shouldBeEmpty()
        rga2.get().shouldBeEmpty()
    }

    /**
    * This test evaluates the generation of delta plus its merging into another replica.
    * Call to get should return an array containing the values set yb insertAt w.r.t the given
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
        val rga1 = RGA<Char>()
        val rga2 = RGA<Char>()

        rga1.insertAt(0, 'A', ts1)
        rga1.insertAt(0, 'B', ts3)
        rga1.insertAt(0, 'C', ts2)
        rga1.insertAt(0, 'D', ts4)
        val delta = rga1.generateDelta(vv)
        rga2.merge(delta)

        rga2.get().shouldContainExactly('D', 'B')
    }

    /**
    * This test evaluates JSON serialization of an empty RGA.
    **/
    "empty JSON serialization" {
        val rga = RGA<Char>()

        val rgaJson = rga.toJson(Char::class)

        rgaJson.shouldBe("""{"_type":"RGA","_metadata":[],"value":[]}""")
    }

    /**
    * This test evaluates JSON deserialization of an empty RGA.
    **/
    "empty JSON deserialization" {
        val rgaJson = RGA.fromJson(Char::class, """{"_type":"RGA","_metadata":[],"value":[]}""")

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
        val rga = RGA<Char>()

        rga.insertAt(0, 'A', ts1)
        rga.insertAt(1, 'B', ts2)
        rga.removeAt(1, ts3)
        rga.insertAt(1, 'C', ts4)
        val rgaJson = rga.toJson(Char::class)

        rgaJson.shouldBe("""{"_type":"RGA","_metadata":[{"anchor":null,"uid":{"uid":{"name":"clientid"},"cnt":-2147483647},"ts":{"uid":{"name":"clientid"},"cnt":-2147483647},"removed":false},{"anchor":{"uid":{"name":"clientid"},"cnt":-2147483647},"uid":{"uid":{"name":"clientid"},"cnt":-2147483644},"ts":{"uid":{"name":"clientid"},"cnt":-2147483644},"removed":false},{"atom":"B","anchor":{"uid":{"name":"clientid"},"cnt":-2147483647},"uid":{"uid":{"name":"clientid"},"cnt":-2147483646},"ts":{"uid":{"name":"clientid"},"cnt":-2147483645},"removed":true}],"value":["A","C"]}""")
    }

    /**
    * This test evaluates JSON deserialization of an RGA.
    **/
    "JSON deserialization" {
        val rgaJson = RGA.fromJson(Char::class, """{"_type":"RGA","_metadata":[{"anchor":null,"uid":{"uid":{"name":"clientid"},"cnt":-2147483647},"ts":{"uid":{"name":"clientid"},"cnt":-2147483647},"removed":false},{"anchor":{"uid":{"name":"clientid"},"cnt":-2147483647},"uid":{"uid":{"name":"clientid"},"cnt":-2147483644},"ts":{"uid":{"name":"clientid"},"cnt":-2147483644},"removed":false},{"atom":"B","anchor":{"uid":{"name":"clientid"},"cnt":-2147483647},"uid":{"uid":{"name":"clientid"},"cnt":-2147483646},"ts":{"uid":{"name":"clientid"},"cnt":-2147483645},"removed":true}],"value":["A","C"]}""")

        rgaJson.get().shouldContainExactly('A', 'C')
    }
})
