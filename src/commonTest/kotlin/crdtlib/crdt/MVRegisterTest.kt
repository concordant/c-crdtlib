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

/**
 * Represents a test suite for MVRegister.
 */
class MVRegisterTest : StringSpec({

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

    fun MVRegister.shouldContainExactlyInAnyOrder(vararg strings: String) {
        this.get().shouldContainExactlyInAnyOrder(*strings)
        // Compare using iterator()
        this.toList().shouldContainExactlyInAnyOrder(*strings)
    }

    /**
     * This test evaluates the scenario: create empty get/iterator.
     * Call to get should return an empty set.
     * Call to iterator should return an empty iterator.
     */
    "create an empty register and get/iterator" {
        val reg = MVRegister()

        reg.shouldContainExactlyInAnyOrder()
    }

    /**
     * This test evaluates the scenario: create with value get/iterator.
     * Call to get should return a set containing the value.
     * Call to iterator should return an iterator containing the value.
     */
    "create with a value and get/iterator" {
        val reg1 = MVRegister("value1", client1)

        reg1.shouldContainExactlyInAnyOrder("value1")
    }

    /**
     * This test evaluates the scenario: create by copy get/iterator.
     * Call to get should return a set containing the assigned value in the first replica.
     * Call to iterator should return an iterator containing the assigned value in the first replica.
     */
    "copy with copy method and get/iterator" {
        val reg1 = MVRegister("value1", client1)

        val reg2 = reg1.copy()

        reg2.shouldContainExactlyInAnyOrder("value1")
    }

    /**
     * This test evaluates the scenario: create by copy(concurrent values) get/iterator.
     * Call to get should return a set containing values assigned in first and second replicas.
     * Call to iterator should return an iterator containing values assigned in first and second replicas.
     */
    "copy a register with multi-values with copy method and get/iterator" {
        val reg1 = MVRegister("value1", client1)
        val reg2 = MVRegister("value2", client2)

        reg2.merge(reg1)
        val reg3 = reg2.copy()

        reg3.shouldContainExactlyInAnyOrder("value1", "value2")
    }

    /**
     * This test evaluates the scenario: assign assign get/iterator.
     * Call to get should return last assigned value.
     * Call to iterator should return an iterator containing the last assigned value.
     */
    "create, assign, get/iterator" {
        val reg = MVRegister("value1", client1)
        reg.assign("value2")

        reg.shouldContainExactlyInAnyOrder("value2")
    }

    /**
     * This test evaluates the scenario: assign || merge get/iterator.
     * Call to get should return value assigned by the first replica.
     * Call to iterator should return an iterator containing the value assigned by the first replica.
     */
    "R1: create with value; R2: create empty, merge, get/iterator" {
        val reg1 = MVRegister("value", client1)
        val reg2 = MVRegister()
        reg1.merge(reg2)
        reg2.merge(reg1)

        reg1.shouldContainExactlyInAnyOrder("value")
        reg2.shouldContainExactlyInAnyOrder("value")
    }

    /**
     * This test evaluates the scenario: assign || merge assign get/iterator.
     * Call to get should return a set containing the value assigned by the second replica.
     * Call to iterator should return an iterator containing the value assigned by the second replica.
     */
    "R1: create with value; R2: create empty, merge, assign, get/iterator" {
        val reg1 = MVRegister("value1", client1)
        val reg2 = MVRegister(client2)
        reg2.merge(reg1)
        reg2.assign("value2")

        reg2.shouldContainExactlyInAnyOrder("value2")
    }

    /**
     * This test evaluates the scenario: assign || assign merge get/iterator.
     * Call to get should return a set containing the two values.
     * Call to iterator should return an iterator containing the two values.
     */
    "R1: create with value; R2: create empty, assign, merge, get/iterator" {
        val reg1 = MVRegister("value1", client1)
        val reg2 = MVRegister(client2)
        reg2.assign("value2")
        reg2.merge(reg1)

        reg2.shouldContainExactlyInAnyOrder("value1", "value2")
    }

    /**
     * This test evaluates the scenario: assign(before merge1) assign(before merge 2) || merge1
     * merge2 get/iterator.
     * Call to get should return a set containing the last value assigned by the first replica.
     * Call to iterator should return an iterator containing the last value assigned by the first replica.
     */
    "R1: create with value, assign; R2: create empty, merge before assign, merge after assign, get/iterator" {
        val reg1 = MVRegister("value1", client1)
        val reg2 = MVRegister("value2", client1)
        reg2.merge(reg1)
        reg1.assign("value2")
        reg2.merge(reg1)

        reg2.shouldContainExactlyInAnyOrder("value2")
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
        val reg1 = MVRegister("value1", client1)
        val reg2 = MVRegister("value2", client2)
        reg2.merge(reg1)
        reg1.assign("value3")
        reg2.merge(reg1)

        reg2.shouldContainExactlyInAnyOrder("value2", "value3")
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
        val reg1 = MVRegister("value1", client1)
        val reg2 = MVRegister("value2", client2)
        val reg3 = MVRegister("value3", client3)

        reg2.merge(reg3)
        reg3.merge(reg1)
        reg3.merge(reg2)

        reg3.shouldContainExactlyInAnyOrder("value1", "value2", "value3")
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
        val reg1 = MVRegister("value1", client1)
        val reg2 = MVRegister(client2)
        val reg3 = MVRegister("value3", client3)

        reg2.merge(reg3)
        reg2.assign("value2")
        reg3.merge(reg1)
        reg3.merge(reg2)

        reg3.shouldContainExactlyInAnyOrder("value1", "value2")
    }

    /**
     * This test evaluates the use of delta return by call to assign method.
     * Call to get should return a set containing the value assigned by the first replica.
     * Call to iterator should return an iterator containing the value assigned by the first replica.
     */
    "use delta returned by assign" {
        val reg1 = MVRegister(client1)
        val reg2 = MVRegister(client1)

        val returnedAssignOp = reg1.assign("value")
        val assignOp = client1.popWrite().second
        returnedAssignOp.shouldBe(assignOp)

        reg1.merge(assignOp)
        reg2.merge(assignOp)

        reg1.shouldContainExactlyInAnyOrder("value")
        reg2.shouldContainExactlyInAnyOrder("value")
    }

    /**
     * This test evaluates the generation of delta plus its merging into another replica.
     * Call to value should return a set containing values assigned by operations registered in the
     * first and second replicas.
     * Call to iterator should return an iterator containing values assigned by operations registered in the
     * first and second replicas.
     */
    "generate delta then merge" {
        val reg1 = MVRegister("value1", client1)
        val reg2 = MVRegister("value2", client2)
        val reg3 = MVRegister()

        val vv = client2.getState()
        reg2.merge(reg1)
        val delta = reg2.generateDelta(vv)
        reg3.merge(delta)

        reg3.shouldContainExactlyInAnyOrder("value1", "value2")
    }

    /**
     * This test evaluates JSON serialization of an empty mv register.
     */
    "empty JSON serialization" {
        val reg = MVRegister()
        val regJson = reg.toJson()

        regJson.shouldBe("""{"type":"MVRegister","metadata":{"entries":[],"causalContext":{"entries":[]}},"value":[]}""")
    }

    /**
     * This test evaluates JSON deserialization of an empty mv register.
     */
    "empty JSON deserialization" {
        val regJson = MVRegister.fromJson("""{"type":"MVRegister","metadata":{"entries":[],"causalContext":{"entries":[]}},"value":[]}""")

        regJson.shouldContainExactlyInAnyOrder()
    }

    /**
     * This test evaluates JSON serialization of a mv register.
     */
    "JSON serialization" {
        val reg1 = MVRegister("value1", client1)
        val reg2 = MVRegister(client2)
        reg2.assign("value2")
        reg2.merge(reg1)
        val regJson = reg2.toJson()

        regJson.shouldBe("""{"type":"MVRegister","metadata":{"entries":[{"uid":{"name":"clientid2"},"cnt":-2147483647},{"uid":{"name":"clientid1"},"cnt":-2147483647}],"causalContext":{"entries":[{"name":"clientid2"},-2147483647,{"name":"clientid1"},-2147483647]}},"value":["value2","value1"]}""")
    }

    /**
     * This test evaluates JSON deserialization of a mv register.
     */
    "JSON deserialization" {
        val regJson = MVRegister.fromJson(
            """{"type":"MVRegister","metadata":{"entries":[{"uid":{"name":"clientid2"},"cnt":-2147483647},{"uid":{"name":"clientid1"},"cnt":-2147483647}],"causalContext":{"entries":[{"name":"clientid2"},-2147483647,{"name":"clientid1"},-2147483647]}},"value":["value2","value1"]}""",
            client1
        )

        regJson.shouldContainExactlyInAnyOrder("value1", "value2")
    }
})
