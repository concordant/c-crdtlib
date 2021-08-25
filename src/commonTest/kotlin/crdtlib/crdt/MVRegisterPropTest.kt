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
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.forAll

/**
 * Represents a test suite for MVRegister.
 */
class MVRegisterPropTest : StringSpec({

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

    "get initial value" {
        forAll(Arb.string()) { s ->
            val reg1 = MVRegister(s, client1)
            reg1.get() == setOf(s)
        }
    }

    "copy with copy constructor" {
        forAll(Arb.string()) { s ->
            val reg1 = MVRegister(s, client1)
            val reg2 = MVRegister(reg1)
            reg2.get() == setOf(s)
        }
    }

    "copy with copy constructor a register with multi-values" {
        forAll(Arb.pair(Arb.string(), Arb.string())) { p ->
            val reg1 = MVRegister(p.first, client1)
            val reg2 = MVRegister(p.second, client2)

            reg2.merge(reg1)
            val reg3 = MVRegister(reg2)

            reg3.get() == setOf(p.first, p.second)
        }
    }

    "assign" {
        forAll(500, Arb.list(Arb.string(), 0..50)) { values ->
            val reg = MVRegister(client1)
            var res: String? = null

            values.map { value ->
                res = value
                reg.assign(value)
            }
            reg.get() == setOfNotNull(res)
        }
    }

    "merge" {
        forAll(500, Arb.list(Arb.string(), 0..50)) { values ->
            val reg1 = MVRegister(client1)
            val reg2 = MVRegister(client2)
            var res: String? = null

            values.map { value ->
                res = value
                reg1.assign(value)
            }
            reg2.merge(reg1)
            reg1.get() == setOfNotNull(res) && reg1.get() == reg2.get()
        }
    }

    "R1: create with value; R2: create empty, merge" {
        forAll(Arb.string()) { s ->
            val reg1 = MVRegister(s, client1)
            val reg2 = MVRegister()
            reg1.merge(reg2)
            reg2.merge(reg1)

            reg1.get() == setOf(s) && reg2.get() == setOf(s)
        }
    }

    "R1: create with value; R2: create empty, merge, assign" {
        forAll(Arb.pair(Arb.string(), Arb.string())) { p ->
            val reg1 = MVRegister(p.first, client1)
            val reg2 = MVRegister(client2)
            reg2.merge(reg1)
            reg2.assign(p.second)

            reg2.get() == setOf(p.second)
        }
    }

    "R1: create with value; R2: create empty, assign, merge" {
        forAll(Arb.pair(Arb.string(), Arb.string())) { p ->
            val reg1 = MVRegister(p.first, client1)
            val reg2 = MVRegister(client2)
            reg2.assign(p.second)
            reg2.merge(reg1)

            reg2.get() == setOf(p.first, p.second)
        }
    }

    "R1: create with value, assign; R2: create empty, merge before assign, merge after assign" {
        forAll(Arb.pair(Arb.string(), Arb.string())) { p ->
            val reg1 = MVRegister(p.first, client1)
            val reg2 = MVRegister(p.second, client1)
            reg2.merge(reg1)
            reg1.assign(p.second)
            reg2.merge(reg1)

            reg2.get() == setOf(p.second)
        }
    }

    "R1: create with value, assign; R2: create with value, merge before assign, merge after assign" {
        forAll(Arb.triple(Arb.string(), Arb.string(), Arb.string())) { t ->
            val reg1 = MVRegister(t.first, client1)
            val reg2 = MVRegister(t.second, client2)
            reg2.merge(reg1)
            reg1.assign(t.third)
            reg2.merge(reg1)

            reg2.get() == setOf(t.second, t.third)
        }
    }

    "R1: create with value; R2: create with value, merge R3; R3: create with value, merge R1, merge R2" {
        forAll(Arb.triple(Arb.string(), Arb.string(), Arb.string())) { t ->
            val reg1 = MVRegister(t.first, client1)
            val reg2 = MVRegister(t.second, client2)
            val reg3 = MVRegister(t.third, client3)

            reg2.merge(reg3)
            reg3.merge(reg1)
            reg3.merge(reg2)

            reg3.get() == setOf(t.first, t.second, t.third)
        }
    }

    "R1: create with value; R2: create empty, merge R3, assign; R3: create with value, merge R1, merge R2" {
        forAll(Arb.triple(Arb.string(), Arb.string(), Arb.string())) { t ->
            val reg1 = MVRegister(t.first, client1)
            val reg2 = MVRegister(client2)
            val reg3 = MVRegister(t.third, client3)

            reg2.merge(reg3)
            reg2.assign(t.second)
            reg3.merge(reg1)
            reg3.merge(reg2)

            reg3.get() == setOf(t.first, t.second)
        }
    }

    "use delta returned by assign" {
        forAll(750, Arb.list(Arb.string(), 0..50)) { values ->
            val reg1 = MVRegister(client1)
            val reg2 = MVRegister(client1)
            var res: String? = null

            values.map { value ->
                res = value
                reg2.merge(reg1.assign(value))
            }
            reg1.get() == setOfNotNull(res) && reg1.get() == reg2.get()
        }
    }

    "merge delta returned by assign" {
        forAll(500, Arb.list(Arb.string(), 0..50)) { values ->
            val reg1 = MVRegister(client1)
            val reg2 = MVRegister(client1)
            val deltas = MVRegister()
            var res: String? = null

            values.map { value ->
                res = value
                deltas.merge(reg1.assign(value))
            }
            reg2.merge(deltas)
            reg1.get() == setOfNotNull(res) && reg1.get() == reg2.get() && reg1.get() == deltas.get()
        }
    }

    "generate delta" {
        forAll(750, Arb.list(Arb.string(), 0..50)) { values ->
            val reg1 = MVRegister(client1)
            val reg2 = MVRegister(client1)
            var res: String? = null

            val subListSize = Arb.int(0..values.size).next()
            val values1 = values.subList(0, subListSize)
            val values2 = values.subList(subListSize, values.size)

            values1.map { value ->
                reg1.assign(value)
            }
            val vv = client1.getState()

            values2.map { value ->
                res = value
                reg1.assign(value)
            }
            val delta = reg1.generateDelta(vv)
            reg2.merge(delta)
            reg2.get() == setOfNotNull(res)
        }
    }

    "deserialize is inverse to serialize" {
        forAll(500, Arb.list(Arb.string(), 0..50)) { values ->
            val reg1 = MVRegister(client1)
            val reg2 = MVRegister(client2)

            val subListSize = Arb.int(0..values.size).next()
            val values1 = values.subList(0, subListSize)
            val values2 = values.subList(subListSize, values.size)

            values1.map { value ->
                reg1.assign(value)
            }
            values2.map { value ->
                reg2.assign(value)
            }
            reg2.merge(reg1)

            reg2.get() == MVRegister.fromJson(reg2.toJson()).get()
        }
    }
})
