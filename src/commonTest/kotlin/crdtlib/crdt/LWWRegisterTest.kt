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

/**
* Represents a test suite for LWWRegister.
**/
class LWWRegisterTest : StringSpec({

    /**
     * This test evaluates the scenario: create get.
     * Call to get should return the value assigned by the constructor.
     */
    "create register and get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val reg = LWWRegister("value", client)

        reg.get().shouldBe("value")
    }

    /**
     * This test evaluates the scenario: create assign get.
     * Call to get should return the value set by the assign method.
     */
    "create, assign, get" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)

        val reg = LWWRegister("value1", client)
        reg.assign("value2")

        reg.get().shouldBe("value2")
    }

    /**
     * This test evaluates the scenario: assign || assign merge get.
     * Call to get should return the value set in the second replica.
     */
    "R1: create; R2: create with greater timestamp, merge, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)

        val reg1 = LWWRegister("value1", client1)
        val reg2 = LWWRegister("value2", client2)
        reg1.merge(reg2)
        reg2.merge(reg1)

        reg1.get().shouldBe("value2")
        reg2.get().shouldBe("value2")
    }

    /**
     * This test evaluates the scenario: assign || assign merge assign get.
     * Call to get should return the value set by call to assign method in the second replica.
     */
    "R1: create; R2: create, merge, assign, get" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)

        val reg1 = LWWRegister("value1", client1)
        val reg2 = LWWRegister("value2", client2)
        reg2.merge(reg1)
        reg2.assign("value3")

        reg2.get().shouldBe("value3")
    }

    /**
     * This test evaluates the use of delta return by call to assign method.
     * Call to get should return last value set in the second replica.
     */
    "use delta generated by assign" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)

        val reg1 = LWWRegister("value1", client1)
        val reg2 = LWWRegister("value2", client2)
        val assignOp1 = reg1.assign("value3")
        val assignOp2 = reg2.assign("value4")

        reg1.merge(assignOp2)
        reg2.merge(assignOp1)

        reg1.get().shouldBe("value4")
        reg2.get().shouldBe("value4")
    }

    /*
    * This test evaluates the generation of delta plus its merging into another replica.
    * Call to get should return the values set in the second replica.
    */
    "generate delta then merge" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)

        val reg1 = LWWRegister("value1", client1)
        val reg2 = LWWRegister("value2", client2)
        val vv1 = client1.getState()
        val vv2 = client2.getState()
        val delta2 = reg1.generateDelta(vv2)
        val delta1 = reg2.generateDelta(vv1)

        reg1.merge(delta1)
        reg2.merge(delta2)

        reg1.get().shouldBe("value2")
        reg2.get().shouldBe("value2")
    }

    /**
     * This test evaluates JSON serialization of a lww register.
     **/
    "JSON serialization" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val value = "value"

        val reg = LWWRegister(value, client)
        val regJson = reg.toJson()

        regJson.shouldBe("""{"_type":"LWWRegister","_metadata":{"uid":{"name":"clientid"},"cnt":-2147483647},"value":"value"}""")
    }

    /**
     * This test evaluates JSON deserialization of a lww register.
     **/
    "JSON deserialization" {
        val regJson = LWWRegister.fromJson("""{"_type":"LWWRegister","_metadata":{"uid":{"name":"clientid"},"cnt":-2147483647},"value":"value"}""")

        regJson.get().shouldBe("value")
    }
})
