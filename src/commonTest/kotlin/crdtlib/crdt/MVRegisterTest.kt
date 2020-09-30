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
**/
class MVRegisterTest : StringSpec({

    /**
    * This test evaluates the scenario: create empty get.
    * Call to get should return an empty set.
    */
    "create an empty register and get" {
        val reg = MVRegister<String>()

        reg.get().shouldBeEmpty()
    }

    /**
    * This test evaluates the scenario: create with value get.
    * Call to get should return a set containing the value.
    */
    "create with a value and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val value = "value"
        val reg = MVRegister<String>(value, ts)

        reg.get().shouldHaveSingleElement(value)
    }

    /**
    * This test evaluates the scenario: create by copy get.
    * Call to get should return a set containing the assigned in the first replica.
    */
    "copy with copy constructor and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val value = "value"
        val reg1 = MVRegister<String>(value, ts)

        val reg2 = MVRegister<String>(reg1)

        reg2.get().shouldHaveSingleElement(value)
    }

    /**
    * This test evaluates the scenario: create by copy(concurrent values) get.
    * Call to get should return a set containing values assigned in first and second replicas.
    */
    "copy with copy constructor a register with multi-values and get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val val1 = "value1"
        val val2 = "value2"
        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>(val2, ts2)

        reg2.merge(reg1)
        val reg3 = MVRegister<String>(reg2)

        reg3.get().shouldContainExactlyInAnyOrder(val1, val2)
    }

    /**
    * This test evaluates the scenario: assign assign get.
    * Call to get should return last assigned value.
    */
    "create, assign, get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val val1 = "value1"
        val val2 = "value2"

        val reg = MVRegister<String>(val1, ts1)
        reg.assign(val2, ts2)

        reg.get().shouldHaveSingleElement(val2)
    }

    /**
    * This test evaluates the scenario: assign assign(old timestamp) get.
    * Call to get should return first assigned value.
    */
    "create assign, assign with older timestamp, get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val val1 = "value1"
        val val2 = "value2"

        val reg = MVRegister<String>(val1, ts2)
        reg.assign(val2, ts1)

        reg.get().shouldHaveSingleElement(val1)
    }

    /**
    * This test evaluates the scenario: assign || merge get.
    * Call to get should return value assigned by the first replica.
    */
    "R1: create with value; R2: create empty, merge, get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val value = "value"

        val reg1 = MVRegister<String>(value, ts)
        val reg2 = MVRegister<String>()
        reg1.merge(reg2)
        reg2.merge(reg1)

        reg1.get().shouldHaveSingleElement(value)
        reg2.get().shouldHaveSingleElement(value)
    }

    /**
    * This test evaluates the scenario: assign || merge assign get.
    * Call to get should return a set containing the value assigned by the second replica.
    */
    "R1: create with value; R2: create empty, merge, assign, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val val1 = "value1"
        val val2 = "value2"

        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>()
        reg2.merge(reg1)
        reg2.assign(val2, ts2)

        reg2.get().shouldHaveSingleElement(val2)
    }

    /**
    * This test evaluates the scenario: assign || assign merge get.
    * Call to get should return a set containing the two values.
    */
    "R1: create with value; R2: create empty, assign, merge, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val ts1 = client1.tick()
        val ts2 = client2.tick()
        val val1 = "value1"
        val val2 = "value2"

        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>()
        reg2.assign(val2, ts2)
        reg2.merge(reg1)

        reg2.get().shouldContainExactlyInAnyOrder(val1, val2)
    }

    /**
    * This test evaluates the scenario: assign(before merge1) assign(before merge 2) || merge1
    * merge2 get.
    * Call to get should return a set containing the last value assigned by the first replica.
    */
    "R1: create with value, assign; R2: craete empty, merge before assign, merge after assign, get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts1 = client.tick()
        val ts2 = client.tick()
        val val1 = "value1"
        val val2 = "value2"

        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>(val2, ts2)
        reg2.merge(reg1)
        reg1.assign(val2, ts2)
        reg2.merge(reg1)

        reg2.get().shouldHaveSingleElement(val2)
    }

    /**
    * This test evaluates the scenario: assign(before merge1) assign(before merge 2) || assign
    * merge1 merge2 get.
    * Call to get should return a set containing the last value assigned by the first replica and
    * value assigned by replica two.
    */
    "R1: create with value, assign; R2: create with value, merge before assign, merge after assign, get" {
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

        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>(val2, ts2)
        reg2.merge(reg1)
        reg1.assign(val3, ts3)
        reg2.merge(reg1)

        reg2.get().shouldContainExactlyInAnyOrder(val2, val3)
    }

    /**
    * This test evaluates the scenario: assign || assign merge(from 3) || assign merge(from 1)
    * merge(from 2) get.
    * Call to get should return a set containing the value assigned by the first, second, and third
    * replicas.
    */
    "R1: create with value; R2: create with value, merge R3; R3: create with value, merge R1, merge R2, get" {
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
        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>(val2, ts2)
        val reg3 = MVRegister<String>(val3, ts3)

        reg2.merge(reg3)
        reg3.merge(reg1)
        reg3.merge(reg2)

        reg3.get().shouldContainExactlyInAnyOrder(val1, val2, val3)
    }

    /**
    * This test evaluates the scenario: assign || merge(from 3) assign || assign merge(from 1)
    * merge(from 2) get.
    * Call to get should return a set containing the value assigned by the first and second
    * replicas, the value assigned by replica three should not be present since it has been
    * overrided by replica two.
    */
    "R1: create with value; R2: create empty, merge R3, assign; R3: create with value, merge R1, merge R2, get" {
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
        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>()
        val reg3 = MVRegister<String>(val3, ts3)

        reg2.merge(reg3)
        reg2.assign(val2, ts2)
        reg3.merge(reg1)
        reg3.merge(reg2)

        reg3.get().shouldContainExactlyInAnyOrder(val1, val2)
    }

    /**
    * This test evaluates the use of delta return by call to assign method.
    * Call to get should return a set containing the value assigned by the first replica.
    */
    "use delta returned by assign" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val ts = client.tick()
        val value = "value"
        val reg1 = MVRegister<String>()
        val reg2 = MVRegister<String>()

        val assignOp = reg1.assign(value, ts)
        reg1.merge(assignOp)
        reg2.merge(assignOp)

        reg1.get().shouldHaveSingleElement(value)
        reg2.get().shouldHaveSingleElement(value)
    }

    /*
    * This test evaluates the generation of delta plus its merging into another replica.
    * Call to value should return a set containing values assigned by operations registered in the
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
        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>(val2, ts2)
        val reg3 = MVRegister<String>()

        reg2.merge(reg1)
        val delta = reg2.generateDelta(vv)
        reg3.merge(delta)

        reg3.get().shouldContainExactlyInAnyOrder(val1, val2)
    }

    /**
    * This test evaluates JSON serialization of an empty mv register.
    **/
    "empty JSON serialization" {
        val reg = MVRegister<String>()
        val regJson = reg.toJson(String::class)

        regJson.shouldBe("""{"_type":"MVRegister","_metadata":{"entries":[],"causalContext":{"entries":[]}},"value":[]}""")
    }

    /**
    * This test evaluates JSON deserialization of an empty mv register.
    **/
    "empty JSON deserialization" {
        val regJson = MVRegister.fromJson(String::class, """{"_type":"MVRegister","_metadata":{"entries":[],"causalContext":{"entries":[]}},"value":[]}""")

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

        val reg1 = MVRegister<String>(val1, ts1)
        val reg2 = MVRegister<String>()
        reg2.assign(val2, ts2)
        reg2.merge(reg1)
        val regJson = reg2.toJson(String::class)

        regJson.shouldBe("""{"_type":"MVRegister","_metadata":{"entries":[{"uid":{"name":"clientid2"},"cnt":-2147483648},{"uid":{"name":"clientid1"},"cnt":-2147483648}],"causalContext":{"entries":[{"name":"clientid2"},-2147483648,{"name":"clientid1"},-2147483648]}},"value":["value2","value1"]}""")
    }

    /**
    * This test evaluates JSON deserialization of a mv register.
    **/
    "JSON deserialization" {
        val regJson = MVRegister.fromJson(String::class, """{"_type":"MVRegister","_metadata":{"entries":[{"uid":{"name":"clientid2"},"cnt":-2147483648},{"uid":{"name":"clientid1"},"cnt":-2147483648}],"causalContext":{"entries":[{"name":"clientid2"},-2147483648,{"name":"clientid1"},-2147483648]}},"value":["value2","value1"]}""")

        regJson.get().shouldContainExactlyInAnyOrder("value1", "value2")
    }
})
