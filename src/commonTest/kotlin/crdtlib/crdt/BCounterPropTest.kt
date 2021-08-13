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
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.kotest.property.forAll

/**
 * Represents a suite test for BCounter.
 */
class BCounterPropTest : StringSpec({

    val uid1 = ClientUId("clientid1")
    val uid2 = ClientUId("clientid2")
    var client1 = SimpleEnvironment(uid1)
    var client2 = SimpleEnvironment(uid2)

    beforeTest {
        client1 = SimpleEnvironment(uid1)
        client2 = SimpleEnvironment(uid2)
    }

    "multiple increments/decrement" {
        checkAll(Arb.list(Arb.enum<CounterOpType>(), 0..100)) { ops ->
            var res = 0
            val cnt1 = BCounter(client1)

            ops.map { op ->
                when (op) {
                    CounterOpType.INCR -> {
                        val value = Arb.int(-res, 1000).next()
                        res += value
                        cnt1.increment(value)
                    }
                    CounterOpType.DECR -> {
                        val value = Arb.int(-1000, res).next()
                        res -= value
                        cnt1.decrement(value)
                    }
                }
            }
            cnt1.get().shouldBe(res)
            cnt1.localRights(uid1).shouldBe(res)
        }
    }

    "merge multiple increments/decrement" {
        checkAll(Arb.list(Arb.enum<CounterOpType>(), 0..100)) { ops ->
            var res = 0
            val cnt1 = BCounter(client1)
            val cnt2 = BCounter(client1)

            ops.map { op ->
                when (op) {
                    CounterOpType.INCR -> {
                        val value = Arb.int(-res, 1000).next()
                        res += value
                        cnt1.increment(value)
                    }
                    CounterOpType.DECR -> {
                        val value = Arb.int(-1000, res).next()
                        res -= value
                        cnt1.decrement(value)
                    }
                }
            }
            cnt2.merge(cnt1)
            cnt2.get().shouldBe(res)
            cnt2.localRights(uid1).shouldBe(res)
            cnt2.localRights(uid2).shouldBe(0)
        }
    }

    "R1: multiple operations; R2: multiple operations, merge" {
        checkAll(Arb.list(Arb.enum<CounterOpType>(), 0..100)) { ops ->
            var rights1 = 0
            var rights2 = 0
            val cnt1 = BCounter(client1)
            val cnt2 = BCounter(client2)

            val subListSize = Arb.int(0..ops.size).next()
            val ops1 = ops.subList(0, subListSize)
            val ops2 = ops.subList(subListSize, ops.size)

            ops1.map { op ->
                when (op) {
                    CounterOpType.INCR -> {
                        val value = Arb.int(-rights1, 1000).next()
                        rights1 += value
                        cnt1.increment(value)
                    }
                    CounterOpType.DECR -> {
                        val value = Arb.int(-1000, rights1).next()
                        rights1 -= value
                        cnt1.decrement(value)
                    }
                }
            }

            ops2.map { op ->
                when (op) {
                    CounterOpType.INCR -> {
                        val value = Arb.int(-rights2, 1000).next()
                        rights2 += value
                        cnt2.increment(value)
                    }
                    CounterOpType.DECR -> {
                        val value = Arb.int(-1000, rights2).next()
                        rights2 -= value
                        cnt2.decrement(value)
                    }
                }
            }
            cnt2.merge(cnt1)
            cnt2.get().shouldBe(rights1 + rights2)
            cnt2.localRights(uid1).shouldBe(rights1)
            cnt2.localRights(uid2).shouldBe(rights2)
        }
    }

    "R1: multiple operations; R2: merge, multiple operations" {
        checkAll(Arb.list(Arb.enum<CounterOpType>(), 0..100)) { ops ->
            var rights1 = 0
            var rights2 = 0
            val cnt1 = BCounter(client1)
            val cnt2 = BCounter(client2)

            val subListSize = Arb.int(0..ops.size).next()
            val ops1 = ops.subList(0, subListSize)
            val ops2 = ops.subList(subListSize, ops.size)

            ops1.map { op ->
                when (op) {
                    CounterOpType.INCR -> {
                        val value = Arb.int(-rights1, 1000).next()
                        rights1 += value
                        cnt1.increment(value)
                    }
                    CounterOpType.DECR -> {
                        val value = Arb.int(-1000, rights1).next()
                        rights1 -= value
                        cnt1.decrement(value)
                    }
                }
            }
            cnt2.merge(cnt1)

            ops2.map { op ->
                when (op) {
                    CounterOpType.INCR -> {
                        val value = Arb.int(-rights2, 1000).next()
                        rights2 += value
                        cnt2.increment(value)
                    }
                    CounterOpType.DECR -> {
                        val value = Arb.int(-1000, rights2).next()
                        rights2 -= value
                        cnt2.decrement(value)
                    }
                }
            }
            cnt2.get().shouldBe(rights1 + rights2)
            cnt2.localRights(uid1).shouldBe(rights1)
            cnt2.localRights(uid2).shouldBe(rights2)
        }
    }

    "use delta returned by increment and decrement" {
        checkAll(Arb.list(Arb.enum<CounterOpType>(), 0..100)) { ops ->
            var res = 0
            val cnt1 = BCounter(client1)
            val cnt2 = BCounter(client1)

            ops.map { op ->
                when (op) {
                    CounterOpType.INCR -> {
                        val value = Arb.int(-res, 1000).next()
                        res += value
                        cnt2.merge(cnt1.increment(value))
                    }
                    CounterOpType.DECR -> {
                        val value = Arb.int(-1000, res).next()
                        res -= value
                        cnt2.merge(cnt1.decrement(value))
                    }
                }
            }
            cnt1.get().shouldBe(res)
            cnt2.get().shouldBe(res)
            cnt1.localRights(uid1).shouldBe(res)
            cnt1.localRights(uid2).shouldBe(0)
            cnt2.localRights(uid1).shouldBe(res)
            cnt2.localRights(uid2).shouldBe(0)
        }
    }

    "merge deltas" {
        checkAll(Arb.list(Arb.enum<CounterOpType>(), 0..100)) { ops ->
            var res = 0
            val cnt1 = BCounter(client1)
            val cnt2 = BCounter(client2)
            val cnt3 = BCounter()
            val deltas = BCounter()

            val subListSize = Arb.int(0..ops.size).next()
            val ops1 = ops.subList(0, subListSize)
            val ops2 = ops.subList(subListSize, ops.size)

            ops1.map { op ->
                when (op) {
                    CounterOpType.INCR -> {
                        val value = Arb.int(-res, 1000).next()
                        res += value
                        cnt1.increment(value)
                    }
                    CounterOpType.DECR -> {
                        val value = Arb.int(-1000, res).next()
                        res -= value
                        cnt1.decrement(value)
                    }
                }
            }
            cnt2.merge(cnt1)
            res = 0

            ops2.map { op ->
                when (op) {
                    CounterOpType.INCR -> {
                        val value = Arb.int(-res, 1000).next()
                        res += value
                        deltas.merge(cnt2.increment(value))
                    }
                    CounterOpType.DECR -> {
                        val value = Arb.int(-1000, res).next()
                        res -= value
                        deltas.merge(cnt2.decrement(value))
                    }
                }
            }
            cnt3.merge(deltas)
            cnt3.get().shouldBe(res)
            cnt3.localRights(uid1).shouldBe(0)
            cnt3.localRights(uid2).shouldBe(res)
        }
    }

    "generate delta" {
        checkAll(Arb.list(Arb.enum<CounterOpType>(), 0..100)) { ops ->
            var res = 0
            val cnt1 = BCounter(client1)
            val cnt2 = BCounter(client1)

            val subListSize = Arb.int(0..ops.size).next()
            val ops1 = ops.subList(0, subListSize)
            val ops2 = ops.subList(subListSize, ops.size)

            ops1.map { op ->
                when (op) {
                    CounterOpType.INCR -> {
                        val value = Arb.int(-res, 1000).next()
                        res += value
                        cnt1.increment(value)
                    }
                    CounterOpType.DECR -> {
                        val value = Arb.int(-1000, res).next()
                        res -= value
                        cnt1.decrement(value)
                    }
                }
            }

            val vv = client1.getState()
            cnt2.merge(cnt1)

            ops2.map { op ->
                when (op) {
                    CounterOpType.INCR -> {
                        val value = Arb.int(-res, 1000).next()
                        res += value
                        cnt1.increment(value)
                    }
                    CounterOpType.DECR -> {
                        val value = Arb.int(-1000, res).next()
                        res -= value
                        cnt1.decrement(value)
                    }
                }
            }

            val delta = cnt1.generateDelta(vv)
            cnt2.merge(delta)

            cnt1.get().shouldBe(res)
            cnt1.localRights(uid1).shouldBe(res)
            cnt2.get().shouldBe(res)
            cnt2.localRights(uid1).shouldBe(res)
        }
    }

    "rights transfer, one way" {
        checkAll(Arb.list(Arb.enum<CounterOpType>(), 0..100)) { ops ->
            var rights1 = 0
            val cnt1 = BCounter(client1)
            val cnt2 = BCounter(client2)

            val subListSize = Arb.int(0..ops.size).next()
            val ops1 = ops.subList(0, subListSize)
            val ops2 = ops.subList(subListSize, ops.size)

            ops1.map { op ->
                when (op) {
                    CounterOpType.INCR -> {
                        val value = Arb.int(-rights1, 1000).next()
                        rights1 += value
                        cnt1.increment(value)
                    }
                    CounterOpType.DECR -> {
                        val value = Arb.int(-1000, rights1).next()
                        rights1 -= value
                        cnt1.decrement(value)
                    }
                }
            }
            cnt1.transfer(rights1 / 2, uid2)
            cnt2.merge(cnt1)
            var rights2 = rights1 / 2
            rights1 -= rights1 / 2

            ops2.map { op ->
                when (op) {
                    CounterOpType.INCR -> {
                        val value = Arb.int(-rights2, 1000).next()
                        rights2 += value
                        cnt2.increment(value)
                    }
                    CounterOpType.DECR -> {
                        val value = Arb.int(-1000, rights2).next()
                        rights2 -= value
                        cnt2.decrement(value)
                    }
                }
            }
            cnt1.merge(cnt2)
            cnt1.get().shouldBe(rights1 + rights2)
            cnt2.get().shouldBe(rights1 + rights2)
            cnt1.localRights(uid1).shouldBe(rights1)
            cnt1.localRights(uid2).shouldBe(rights2)
            cnt2.localRights(uid1).shouldBe(rights1)
            cnt2.localRights(uid2).shouldBe(rights2)
        }
    }

    "two way transfer" {
        checkAll(Arb.list(Arb.enum<CounterOpType>(), 0..100)) { ops ->
            var rights1 = 0
            val cnt1 = BCounter(client1)
            val cnt2 = BCounter(client2)

            val subListSize = Arb.int(0..ops.size).next()
            val ops1 = ops.subList(0, subListSize)
            val ops2 = ops.subList(subListSize, ops.size)

            ops1.map { op ->
                when (op) {
                    CounterOpType.INCR -> {
                        val value = Arb.int(-rights1, 1000).next()
                        rights1 += value
                        cnt1.increment(value)
                    }
                    CounterOpType.DECR -> {
                        val value = Arb.int(-1000, rights1).next()
                        rights1 -= value
                        cnt1.decrement(value)
                    }
                }
            }
            cnt1.transfer(rights1 / 2, uid2)
            cnt2.merge(cnt1)
            var rights2 = rights1 / 2
            rights1 -= rights1 / 2

            ops2.map { op ->
                when (op) {
                    CounterOpType.INCR -> {
                        val value = Arb.int(-rights2, 1000).next()
                        rights2 += value
                        cnt2.increment(value)
                    }
                    CounterOpType.DECR -> {
                        val value = Arb.int(-1000, rights2).next()
                        rights2 -= value
                        cnt2.decrement(value)
                    }
                }
            }
            cnt2.transfer(rights2 / 2, uid1)
            cnt1.merge(cnt2)
            rights1 += rights2 / 2
            rights2 -= rights2 / 2
            cnt1.get().shouldBe(rights1 + rights2)
            cnt2.get().shouldBe(rights1 + rights2)
            cnt1.localRights(uid1).shouldBe(rights1)
            cnt1.localRights(uid2).shouldBe(rights2)
            cnt2.localRights(uid1).shouldBe(rights1)
            cnt2.localRights(uid2).shouldBe(rights2)
        }
    }

    "deserialize is inverse to serialize" {
        forAll(750, Arb.list(Arb.enum<CounterOpType>(), 0..75)) { ops ->
            var rights1 = 0
            val cnt1 = BCounter(client1)
            val cnt2 = BCounter(client2)

            val subListSize = Arb.int(0..ops.size).next()
            val ops1 = ops.subList(0, subListSize)
            val ops2 = ops.subList(subListSize, ops.size)

            ops1.map { op ->
                when (op) {
                    CounterOpType.INCR -> {
                        val value = Arb.int(-rights1, 1000).next()
                        rights1 += value
                        cnt1.increment(value)
                    }
                    CounterOpType.DECR -> {
                        val value = Arb.int(-1000, rights1).next()
                        rights1 -= value
                        cnt1.decrement(value)
                    }
                }
            }
            cnt1.transfer(rights1 / 2, uid2)
            cnt2.merge(cnt1)
            var rights2 = rights1 / 2
            rights1 -= rights1 / 2

            ops2.map { op ->
                when (op) {
                    CounterOpType.INCR -> {
                        val value = Arb.int(-rights2, 1000).next()
                        rights2 += value
                        cnt2.increment(value)
                    }
                    CounterOpType.DECR -> {
                        val value = Arb.int(-1000, rights2).next()
                        rights2 -= value
                        cnt2.decrement(value)
                    }
                }
            }
            cnt2.get() == BCounter.fromJson(cnt2.toJson()).get()
        }
    }
})
