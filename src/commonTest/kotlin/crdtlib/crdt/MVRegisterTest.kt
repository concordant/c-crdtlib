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
import io.kotest.matchers.collections.*
import io.kotest.matchers.iterator.shouldBeEmpty
import io.kotest.matchers.iterator.shouldHaveNext

/**
* Represents a test suite for MVRegister.
**/
class MVRegisterTest : StringSpec({

    /**
     * This test evaluates the scenario: create empty get/iterator.
     * Call to get should return an empty set.
     * Call to iterator should return an empty iterator.
     */
    "create an empty register and get/iterator" {
        val reg = MVRegister()

        reg.get().shouldBeEmpty()
        reg.iterator().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: create with value get/iterator.
     * Call to get should return a set containing the value.
     * Call to iterator should return an iterator containing the value.
     */
    "create with a value and get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val value = "value"
        val reg = MVRegister(value, ts)

        reg.get().shouldHaveSingleElement(value)

        val it = reg.iterator()
        it.shouldHaveNext()
        it.next().shouldBe(value)
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: create by copy get/iterator.
     * Call to get should return a set containing the assigned value in the first replica.
     * Call to iterator should return an iterator containing the assigned value in the first replica.
     */
    "copy with copy constructor and get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val value = "value"
        val reg1 = MVRegister(value, ts)

        val reg2 = MVRegister(reg1)

        reg2.get().shouldHaveSingleElement(value)

        val it = reg2.iterator()
        it.shouldHaveNext()
        it.next().shouldBe(value)
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: create by copy(concurrent values) get/iterator.
     * Call to get should return a set containing values assigned in first and second replicas.
     * Call to iterator should return an iterator containing values assigned in first and second replicas.
     */
    "copy with copy constructor a register with multi-values and get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val val1 = "value1"
        val val2 = "value2"
        val reg1 = MVRegister(val1, ts1)
        val reg2 = MVRegister(val2, ts2)

        reg2.merge(reg1)
        val reg3 = MVRegister(reg2)

        reg3.get().shouldContainExactlyInAnyOrder(val1, val2)

        val it = reg3.iterator()
        for (value in reg3.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: assign assign get/iterator.
     * Call to get should return last assigned value.
     * Call to iterator should return an iterator containing the last assigned value.
     */
    "create, assign, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val val1 = "value1"
        val val2 = "value2"

        val reg = MVRegister(val1, ts1)
        reg.assign(val2, ts2)

        reg.get().shouldHaveSingleElement(val2)

        val it = reg.iterator()
        it.shouldHaveNext()
        it.next().shouldBe(val2)
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: assign assign(old timestamp) get/iterator.
     * Call to get should return first assigned value.
     * Call to iterator should return an iterator containing the first assigned value.
     */
    "create assign, assign with older timestamp, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val val1 = "value1"
        val val2 = "value2"

        val reg = MVRegister(val1, ts2)
        reg.assign(val2, ts1)

        reg.get().shouldHaveSingleElement(val1)

        val it = reg.iterator()
        it.shouldHaveNext()
        it.next().shouldBe(val1)
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: assign || merge get/iterator.
     * Call to get should return value assigned by the first replica.
     * Call to iterator should return an iterator containing the value assigned by the first replica.
     */
    "R1: create with value; R2: create empty, merge, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val value = "value"

        val reg1 = MVRegister(value, ts)
        val reg2 = MVRegister()
        reg1.merge(reg2)
        reg2.merge(reg1)

        reg1.get().shouldHaveSingleElement(value)
        reg2.get().shouldHaveSingleElement(value)

        val it1 = reg1.iterator()
        it1.shouldHaveNext()
        it1.next().shouldBe(value)
        it1.shouldBeEmpty()
        val it2 = reg2.iterator()
        it2.shouldHaveNext()
        it2.next().shouldBe(value)
        it2.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: assign || merge assign get/iterator.
     * Call to get should return a set containing the value assigned by the second replica.
     * Call to iterator should return an iterator containing the value assigned by the second replica.
     */
    "R1: create with value; R2: create empty, merge, assign, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val val1 = "value1"
        val val2 = "value2"

        val reg1 = MVRegister(val1, ts1)
        val reg2 = MVRegister()
        reg2.merge(reg1)
        reg2.assign(val2, ts2)

        reg2.get().shouldHaveSingleElement(val2)

        val it = reg2.iterator()
        it.shouldHaveNext()
        it.next().shouldBe(val2)
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: assign || assign merge get/iterator.
     * Call to get should return a set containing the two values.
     * Call to iterator should return an iterator containing the two values.
     */
    "R1: create with value; R2: create empty, assign, merge, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val val1 = "value1"
        val val2 = "value2"

        val reg1 = MVRegister(val1, ts1)
        val reg2 = MVRegister()
        reg2.assign(val2, ts2)
        reg2.merge(reg1)

        reg2.get().shouldContainExactlyInAnyOrder(val1, val2)

        val it = reg2.iterator()
        for (value in reg2.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: assign(before merge1) assign(before merge 2) || merge1
     * merge2 get/iterator.
     * Call to get should return a set containing the last value assigned by the first replica.
     * Call to iterator should return an iterator containing the last value assigned by the first replica.
     */
    "R1: create with value, assign; R2: create empty, merge before assign, merge after assign, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val val1 = "value1"
        val val2 = "value2"

        val reg1 = MVRegister(val1, ts1)
        val reg2 = MVRegister(val2, ts2)
        reg2.merge(reg1)
        reg1.assign(val2, ts2)
        reg2.merge(reg1)

        reg2.get().shouldHaveSingleElement(val2)

        val it = reg2.iterator()
        it.shouldHaveNext()
        it.next().shouldBe(val2)
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: assign(before merge1) assign(before merge 2) || assign
     * merge1 merge2 get/iterator.
     * Call to get should return a set containing the last value assigned by the first replica and
     * value assigned by replica two.
     * Call to iterator should return an iterator containing the last value assigned by the first replica and
     * value assigned by replica two.
     */
    "R1: create with value, assign; R2: create with value, merge before assign, merge after assign, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client1.tick()
        val val1 = "value1"
        val val2 = "value2"
        val val3 = "value3"

        val reg1 = MVRegister(val1, ts1)
        val reg2 = MVRegister(val2, ts2)
        reg2.merge(reg1)
        reg1.assign(val3, ts3)
        reg2.merge(reg1)

        reg2.get().shouldContainExactlyInAnyOrder(val2, val3)

        val it = reg2.iterator()
        for (value in reg2.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: assign || assign merge(from 3) || assign merge(from 1)
     * merge(from 2) get/iterator.
     * Call to get should return a set containing the value assigned by the first, second, and third
     * replicas.
     * Call to iterator should return an iterator containing the value assigned by the first, second, and third
     * replicas.
     */
    "R1: create with value; R2: create with value, merge R3; R3: create with value, merge R1, merge R2, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val client3 = SimpleEnvironment(uid3)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client3.tick()
        val val1 = "value1"
        val val2 = "value2"
        val val3 = "value3"
        val reg1 = MVRegister(val1, ts1)
        val reg2 = MVRegister(val2, ts2)
        val reg3 = MVRegister(val3, ts3)

        reg2.merge(reg3)
        reg3.merge(reg1)
        reg3.merge(reg2)

        reg3.get().shouldContainExactlyInAnyOrder(val1, val2, val3)

        val it = reg3.iterator()
        for (value in reg3.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: assign || merge(from 3) assign || assign merge(from 1)
     * merge(from 2) get/iterator.
     * Call to get should return a set containing the value assigned by the first and second
     * replicas, the value assigned by replica three should not be present since it has been
     * overridden by replica two.
     * Call to iterator should return an iterator containing the value assigned by the first and second
     * replicas.
     */
    "R1: create with value; R2: create empty, merge R3, assign; R3: create with value, merge R1, merge R2, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val client3 = SimpleEnvironment(uid3)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val ts3 = client3.tick()
        val val1 = "value1"
        val val2 = "value2"
        val val3 = "value3"
        val reg1 = MVRegister(val1, ts1)
        val reg2 = MVRegister()
        val reg3 = MVRegister(val3, ts3)

        reg2.merge(reg3)
        reg2.assign(val2, ts2)
        reg3.merge(reg1)
        reg3.merge(reg2)

        reg3.get().shouldContainExactlyInAnyOrder(val1, val2)

        val it = reg3.iterator()
        for (value in reg3.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates the use of delta return by call to assign method.
     * Call to get should return a set containing the value assigned by the first replica.
     * Call to iterator should return an iterator containing the value assigned by the first replica.
     */
    "use delta returned by assign" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val value = "value"
        val reg1 = MVRegister()
        val reg2 = MVRegister()

        val assignOp = reg1.assign(value, ts)
        reg1.merge(assignOp)
        reg2.merge(assignOp)

        reg1.get().shouldHaveSingleElement(value)
        reg2.get().shouldHaveSingleElement(value)

        val it1 = reg1.iterator()
        it1.shouldHaveNext()
        it1.next().shouldBe(value)
        it1.shouldBeEmpty()
        val it2 = reg2.iterator()
        it2.shouldHaveNext()
        it2.next().shouldBe(value)
        it2.shouldBeEmpty()
    }

    /**
     * This test evaluates the generation of delta plus its merging into another replica.
     * Call to value should return a set containing values assigned by operations registered in the
     * first and second replicas.
     * Call to iterator should return an iterator containing values assigned by operations registered in the
     * first and second replicas.
     */
    "generate delta then merge" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val vv = client1.getState()
        val val1 = "value1"
        val val2 = "value2"
        val reg1 = MVRegister(val1, ts1)
        val reg2 = MVRegister(val2, ts2)
        val reg3 = MVRegister()

        reg2.merge(reg1)
        val delta = reg2.generateDelta(vv)
        reg3.merge(delta)

        reg3.get().shouldContainExactlyInAnyOrder(val1, val2)

        val it = reg3.iterator()
        for (value in reg3.get()) {
            it.shouldHaveNext()
            it.next().shouldBe(value)
        }
        it.shouldBeEmpty()
    }

    /**
     * This test evaluates JSON serialization of an empty mv register.
     **/
    "empty JSON serialization" {
        val reg = MVRegister()
        val regJson = reg.toJson()

        regJson.shouldBe("""{"type":"MVRegister","metadata":{"entries":[],"causalContext":{"entries":[]}},"value":[]}""")
    }

    /**
     * This test evaluates JSON deserialization of an empty mv register.
     **/
    "empty JSON deserialization" {
        val regJson = MVRegister.fromJson("""{"type":"MVRegister","metadata":{"entries":[],"causalContext":{"entries":[]}},"value":[]}""")

        regJson.get().shouldBeEmpty()
    }

    /**
     * This test evaluates JSON serialization of a mv register.
     **/
    "JSON serialization" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val val1 = "value1"
        val val2 = "value2"

        val reg1 = MVRegister(val1, ts1)
        val reg2 = MVRegister()
        reg2.assign(val2, ts2)
        reg2.merge(reg1)
        val regJson = reg2.toJson()

        regJson.shouldBe("""{"type":"MVRegister","metadata":{"entries":[{"uid":{"name":"clientid2"},"cnt":-2147483647},{"uid":{"name":"clientid1"},"cnt":-2147483647}],"causalContext":{"entries":[{"name":"clientid2"},-2147483647,{"name":"clientid1"},-2147483647]}},"value":["value2","value1"]}""")
    }

    /**
     * This test evaluates JSON deserialization of a mv register.
     **/
    "JSON deserialization" {
        val regJson = MVRegister.fromJson("""{"type":"MVRegister","metadata":{"entries":[{"uid":{"name":"clientid2"},"cnt":-2147483647},{"uid":{"name":"clientid1"},"cnt":-2147483647}],"causalContext":{"entries":[{"name":"clientid2"},-2147483647,{"name":"clientid1"},-2147483647]}},"value":["value2","value1"]}""")

        regJson.get().shouldContainExactlyInAnyOrder("value1", "value2")
    }
})
