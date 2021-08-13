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
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.forAll

enum class RGAOpType {
    INSERT, REMOVE
}

class RGAPropTest : StringSpec({
    val uid1 = ClientUId("clientid1")
    val uid2 = ClientUId("clientid2")
    var client1 = SimpleEnvironment(uid1)
    var client2 = SimpleEnvironment(uid2)

    val arbValue: Arb<String> = Arb.string(1..1)

    beforeTest {
        client1 = SimpleEnvironment(uid1)
        client2 = SimpleEnvironment(uid2)
    }

    "multiple insert/remove" {
        forAll(Arb.list(Arb.enum<RGAOpType>(), 0..100)) { ops ->
            val res = StringBuilder()
            val rga = RGA(client1)

            ops.map { op ->
                when (op) {
                    RGAOpType.INSERT -> {
                        val index = Arb.int(0..res.length).next()
                        val value = arbValue.next()
                        res.insert(index, value)
                        rga.insertAt(index, value)
                    }
                    RGAOpType.REMOVE -> {
                        if (res.isNotEmpty()) {
                            val index = Arb.int(res.indices).next()
                            res.deleteAt(index)
                            rga.removeAt(index)
                        } else {
                            // None
                        }
                    }
                }
            }
            rga.get().joinToString(separator = "") == res.toString()
        }
    }

    "merge multiple insert/remove" {
        forAll(Arb.list(Arb.enum<RGAOpType>(), 0..100)) { ops ->
            val res = StringBuilder()
            val rga1 = RGA(client1)
            val rga2 = RGA(client1)

            ops.map { op ->
                when (op) {
                    RGAOpType.INSERT -> {
                        val index = Arb.int(0..res.length).next()
                        val value = arbValue.next()
                        res.insert(index, value)
                        rga1.insertAt(index, value)
                    }
                    RGAOpType.REMOVE -> {
                        if (res.isNotEmpty()) {
                            val index = Arb.int(res.indices).next()
                            res.deleteAt(index)
                            rga1.removeAt(index)
                        } else {
                            // None
                        }
                    }
                }
            }
            rga2.merge(rga1)
            rga2.get().joinToString(separator = "") == res.toString()
        }
    }

    "R1: multiple insert/remove; R2: merge, multiple insert/remove" {
        forAll(Arb.list(Arb.enum<RGAOpType>(), 0..100)) { ops ->
            val res = StringBuilder()
            val rga1 = RGA(client1)
            val rga2 = RGA(client2)

            val subListSize = Arb.int(0..ops.size).next()
            val ops1 = ops.subList(0, subListSize)
            val ops2 = ops.subList(subListSize, ops.size)

            ops1.map { op ->
                when (op) {
                    RGAOpType.INSERT -> {
                        val index = Arb.int(0..res.length).next()
                        val value = arbValue.next()
                        res.insert(index, value)
                        rga1.insertAt(index, value)
                    }
                    RGAOpType.REMOVE -> {
                        if (res.isNotEmpty()) {
                            val index = Arb.int(res.indices).next()
                            res.deleteAt(index)
                            rga1.removeAt(index)
                        } else {
                            // None
                        }
                    }
                }
            }
            rga2.merge(rga1)

            ops2.map { op ->
                when (op) {
                    RGAOpType.INSERT -> {
                        val index = Arb.int(0..res.length).next()
                        val value = arbValue.next()
                        res.insert(index, value)
                        rga2.insertAt(index, value)
                    }
                    RGAOpType.REMOVE -> {
                        if (res.isNotEmpty()) {
                            val index = Arb.int(res.indices).next()
                            res.deleteAt(index)
                            rga2.removeAt(index)
                        } else {
                            // None
                        }
                    }
                }
            }
            rga2.get().joinToString(separator = "") == res.toString()
        }
    }

    "use delta returned by insert and remove" {
        forAll(Arb.list(Arb.enum<RGAOpType>(), 0..100)) { ops ->
            val res = StringBuilder()
            val rga1 = RGA(client1)
            val rga2 = RGA(client1)

            ops.map { op ->
                when (op) {
                    RGAOpType.INSERT -> {
                        val index = Arb.int(0..res.length).next()
                        val value = arbValue.next()
                        res.insert(index, value)
                        rga2.merge(rga1.insertAt(index, value))
                    }
                    RGAOpType.REMOVE -> {
                        if (res.isNotEmpty()) {
                            val index = Arb.int(res.indices).next()
                            res.deleteAt(index)
                            rga2.merge(rga1.removeAt(index))
                        } else {
                            // None
                        }
                    }
                }
            }
            rga1.get() == rga2.get() && rga1.get().joinToString(separator = "") == res.toString()
        }
    }

    "merge deltas" {
        forAll(Arb.list(Arb.enum<RGAOpType>(), 0..100)) { ops ->
            val res = StringBuilder()
            val rga1 = RGA(client1)
            val rga2 = RGA(client1)
            val deltas = RGA()

            ops.map { op ->
                when (op) {
                    RGAOpType.INSERT -> {
                        val index = Arb.int(0..res.length).next()
                        val value = arbValue.next()
                        res.insert(index, value)
                        deltas.merge(rga1.insertAt(index, value))
                    }
                    RGAOpType.REMOVE -> {
                        if (res.isNotEmpty()) {
                            val index = Arb.int(res.indices).next()
                            res.deleteAt(index)
                            deltas.merge(rga1.removeAt(index))
                        } else {
                            // None
                        }
                    }
                }
            }
            rga2.merge(deltas)
            rga1.get() == rga2.get() && rga1.get() == deltas.get() && rga1.get().joinToString(separator = "") == res.toString()
        }
    }

    "generate delta" {
        forAll(Arb.list(Arb.enum<RGAOpType>(), 0..100)) { ops ->
            val res = StringBuilder()
            val vv = VersionVector()
            val rga1 = RGA(client1)
            val rga2 = RGA(client2)

            val subListSize = Arb.int(0..ops.size).next()
            val ops1 = ops.subList(0, subListSize)
            val ops2 = ops.subList(subListSize, ops.size)

            ops1.map { op ->
                when (op) {
                    RGAOpType.INSERT -> {
                        val index = Arb.int(0..res.length).next()
                        val value = arbValue.next()
                        res.insert(index, value)
                        rga1.insertAt(index, value)
                    }
                    RGAOpType.REMOVE -> {
                        if (res.isNotEmpty()) {
                            val index = Arb.int(res.indices).next()
                            res.deleteAt(index)
                            rga1.removeAt(index)
                        } else {
                            // None
                        }
                    }
                }
            }
            vv.update(client1.tick())
            rga2.merge(rga1)

            ops2.map { op ->
                when (op) {
                    RGAOpType.INSERT -> {
                        val index = Arb.int(0..res.length).next()
                        val value = arbValue.next()
                        res.insert(index, value)
                        rga1.insertAt(index, value)
                    }
                    RGAOpType.REMOVE -> {
                        if (res.isNotEmpty()) {
                            val index = Arb.int(res.indices).next()
                            res.deleteAt(index)
                            rga1.removeAt(index)
                        } else {
                            // None
                        }
                    }
                }
            }
            val delta = rga1.generateDelta(vv)
            rga2.merge(delta)
            rga2.get() == rga1.get()
        }
    }

    "deserialize is inverse to serialize" {
        forAll(750, Arb.list(Arb.enum<RGAOpType>(), 0..50)) { ops ->
            val res = StringBuilder()
            val rga = RGA(client1)

            ops.map { op ->
                when (op) {
                    RGAOpType.INSERT -> {
                        val index = Arb.int(0..res.length).next()
                        val value = arbValue.next()
                        res.insert(index, value)
                        rga.insertAt(index, value)
                    }
                    RGAOpType.REMOVE -> {
                        if (res.isNotEmpty()) {
                            val index = Arb.int(res.indices).next()
                            res.deleteAt(index)
                            rga.removeAt(index)
                        } else {
                            // None
                        }
                    }
                }
            }
            rga.get() == RGA.fromJson(rga.toJson()).get()
        }
    }
})
