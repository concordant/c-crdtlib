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
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.forAll

/**
 * Represents a test suite for LWWRegister.
 */
class LWWRegisterPropTest : StringSpec({

    val uid1 = ClientUId("clientid1")
    val uid2 = ClientUId("clientid2")
    var client1 = SimpleEnvironment(uid1)
    var client2 = SimpleEnvironment(uid2)

    beforeTest {
        client1 = SimpleEnvironment(uid1)
        client2 = SimpleEnvironment(uid2)
    }

    "get initial value" {
        forAll(Arb.string()) { s ->
            val reg = LWWRegister(s, client1)
            reg.get() == s
        }
    }

    "assign" {
        forAll(750, Arb.list(Arb.string())) { values ->
            val reg = LWWRegister(client1)
            var res: String? = null

            values.map { value ->
                res = value
                reg.assign(value)
            }
            reg.get() == res
        }
    }

    "merge" {
        forAll(750, Arb.list(Arb.string())) { values ->
            val reg1 = LWWRegister(client1)
            val reg2 = LWWRegister(client2)
            var res: String? = null

            values.map { value ->
                res = value
                reg1.assign(value)
            }
            reg2.merge(reg1)
            reg1.get() == res && reg1.get() == reg2.get()
        }
    }

    "R1: create empty; R2: create with greater timestamp, merge" {
        forAll(Arb.string()) { s ->
            val reg1 = LWWRegister(client1)
            val reg2 = LWWRegister(s, client2)
            reg1.merge(reg2)
            reg2.merge(reg1)

            reg1.get() == s && reg2.get() == s
        }
    }

    "R1: create; R2: create with greater timestamp, merge" {
        forAll(Arb.pair(Arb.string(), Arb.string())) { p ->
            val reg1 = LWWRegister(p.first, client1)
            val reg2 = LWWRegister(p.second, client2)
            reg1.merge(reg2)
            reg2.merge(reg1)

            reg1.get() == p.second && reg2.get() == p.second
        }
    }

    "R1: create; R2: create, merge, assign" {
        forAll(Arb.triple(Arb.string(), Arb.string(), Arb.string())) { t ->
            val reg1 = LWWRegister(t.first, client1)
            val reg2 = LWWRegister(t.second, client2)
            reg2.merge(reg1)
            reg2.assign(t.third)

            reg2.get() == t.third
        }
    }

    "use delta returned by assign" {
        forAll(750, Arb.list(Arb.string())) { values ->
            val reg1 = LWWRegister(client1)
            val reg2 = LWWRegister(client1)
            var res: String? = null

            values.map { value ->
                res = value
                reg2.merge(reg1.assign(value))
            }
            reg1.get() == res && reg1.get() == reg2.get()
        }
    }

    "merge delta returned by assign" {
        forAll(750, Arb.list(Arb.string())) { values ->
            val reg1 = LWWRegister(client1)
            val reg2 = LWWRegister(client1)
            val deltas = LWWRegister()
            var res: String? = null

            values.map { value ->
                res = value
                deltas.merge(reg1.assign(value))
            }
            reg2.merge(deltas)
            reg1.get() == res && reg1.get() == reg2.get() && reg1.get() == deltas.get()
        }
    }

    "generate delta" {
        forAll(750, Arb.list(Arb.string())) { values ->
            val reg1 = LWWRegister(client1)
            val reg2 = LWWRegister(client1)
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
            reg2.get() == res
        }
    }

    "deserialize is inverse to serialize" {
        forAll(750, Arb.list(Arb.string(), 0..50)) { values ->
            val reg1 = LWWRegister(client1)
            val reg2 = LWWRegister(client2)

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

            reg2.get() == LWWRegister.fromJson(reg2.toJson()).get()
        }
    }
})
