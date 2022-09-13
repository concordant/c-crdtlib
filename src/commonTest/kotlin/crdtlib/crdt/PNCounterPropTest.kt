/*
* MIT License
*
* Copyright © 2022, Concordant and contributors.
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
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.forAll

enum class CounterOpType {
    INCR, DECR
}

val CounterOperationArb = arbitrary { rs ->
    Pair(Arb.enum<CounterOpType>().next(rs), Arb.int(-1000, 1000).next(rs))
}

class PNCounterPropTest : StringSpec({
    val uid1 = ClientUId("clientid1")
    val uid2 = ClientUId("clientid2")
    var client1 = SimpleEnvironment(uid1)
    var client2 = SimpleEnvironment(uid2)

    beforeTest {
        client1 = SimpleEnvironment(uid1)
        client2 = SimpleEnvironment(uid2)
    }

    "multiple increments/decrement" {
        forAll(Arb.list(CounterOperationArb, 0..100)) { ops ->
            var res = 0
            val cnt = PNCounter(client1)

            ops.map { op ->
                when (op.first) {
                    CounterOpType.INCR -> {
                        res += op.second
                        cnt.increment(op.second)
                    }
                    CounterOpType.DECR -> {
                        res -= op.second
                        cnt.decrement(op.second)
                    }
                }
            }
            cnt.get() == res
        }
    }

    "merge multiple increments/decrement" {
        forAll(Arb.list(CounterOperationArb, 0..100)) { ops ->
            var res = 0
            val cnt1 = PNCounter(client1)
            val cnt2 = PNCounter(client2)

            ops.map { op ->
                when (op.first) {
                    CounterOpType.INCR -> {
                        res += op.second
                        cnt1.increment(op.second)
                    }
                    CounterOpType.DECR -> {
                        res -= op.second
                        cnt1.decrement(op.second)
                    }
                }
            }
            cnt2.merge(cnt1)
            cnt1.merge(cnt2)
            cnt1.get() == res && cnt1.get() == cnt2.get()
        }
    }

    "R1: multiple operations; R2: multiple operations, merge" {
        forAll(Arb.list(CounterOperationArb, 0..100)) { ops ->
            var res = 0
            val cnt1 = PNCounter(client1)
            val cnt2 = PNCounter(client2)

            val subListSize = Arb.int(0..ops.size).next()
            val ops1 = ops.subList(0, subListSize)
            val ops2 = ops.subList(subListSize, ops.size)

            ops1.map { op ->
                when (op.first) {
                    CounterOpType.INCR -> {
                        res += op.second
                        cnt1.increment(op.second)
                    }
                    CounterOpType.DECR -> {
                        res -= op.second
                        cnt1.decrement(op.second)
                    }
                }
            }

            ops2.map { op ->
                when (op.first) {
                    CounterOpType.INCR -> {
                        res += op.second
                        cnt2.increment(op.second)
                    }
                    CounterOpType.DECR -> {
                        res -= op.second
                        cnt2.decrement(op.second)
                    }
                }
            }
            cnt2.merge(cnt1)
            cnt2.get() == res
        }
    }

    "R1: multiple operations; R2: merge, multiple operations" {
        forAll(Arb.list(CounterOperationArb, 0..100)) { ops ->
            var res = 0
            val cnt1 = PNCounter(client1)
            val cnt2 = PNCounter(client2)

            val subListSize = Arb.int(0..ops.size).next()
            val ops1 = ops.subList(0, subListSize)
            val ops2 = ops.subList(subListSize, ops.size)

            ops1.map { op ->
                when (op.first) {
                    CounterOpType.INCR -> {
                        res += op.second
                        cnt1.increment(op.second)
                    }
                    CounterOpType.DECR -> {
                        res -= op.second
                        cnt1.decrement(op.second)
                    }
                }
            }
            cnt2.merge(cnt1)

            ops2.map { op ->
                when (op.first) {
                    CounterOpType.INCR -> {
                        res += op.second
                        cnt2.increment(op.second)
                    }
                    CounterOpType.DECR -> {
                        res -= op.second
                        cnt2.decrement(op.second)
                    }
                }
            }
            cnt2.get() == res
        }
    }

    "use delta returned by increment and decrement" {
        forAll(Arb.list(CounterOperationArb, 0..100)) { ops ->
            var res = 0
            val cnt1 = PNCounter(client1)
            val cnt2 = PNCounter(client2)

            ops.map { op ->
                when (op.first) {
                    CounterOpType.INCR -> {
                        res += op.second
                        val returnedIncOp = cnt1.increment(op.second)
                        cnt2.merge(returnedIncOp)
                        cnt1.merge(returnedIncOp)
                    }
                    CounterOpType.DECR -> {
                        res -= op.second
                        val returnedDecOp = cnt1.decrement(op.second)
                        cnt2.merge(returnedDecOp)
                        cnt1.merge(returnedDecOp)
                    }
                }
            }
            cnt2.merge(cnt1)
            cnt1.merge(cnt2)
            cnt1.get() == res && cnt1.get() == cnt2.get()
        }
    }

    "merge deltas" {
        forAll(Arb.list(CounterOperationArb, 0..100)) { ops ->
            var res = 0
            val cnt1 = PNCounter(client1)
            val cnt2 = PNCounter(client2)
            val cnt3 = PNCounter()
            val deltas = PNCounter()

            val subListSize = Arb.int(0..ops.size).next()
            val ops1 = ops.subList(0, subListSize)
            val ops2 = ops.subList(subListSize, ops.size)

            ops1.map { op ->
                when (op.first) {
                    CounterOpType.INCR -> {
                        cnt1.increment(op.second)
                    }
                    CounterOpType.DECR -> {
                        cnt1.decrement(op.second)
                    }
                }
            }
            cnt2.merge(cnt1)

            ops2.map { op ->
                when (op.first) {
                    CounterOpType.INCR -> {
                        res += op.second
                        deltas.merge(cnt2.increment(op.second))
                    }
                    CounterOpType.DECR -> {
                        res -= op.second
                        deltas.merge(cnt2.decrement(op.second))
                    }
                }
            }
            cnt3.merge(deltas)
            cnt3.get() == res
        }
    }

    "generate delta" {
        forAll(Arb.list(Arb.positiveInts(1000), 0..100)) { ops ->
            var res = 0
            val cnt1 = PNCounter(client1)
            val cnt2 = PNCounter(client2)

            val subListSize = Arb.int(0..ops.size).next()
            val ops1 = ops.subList(0, subListSize)
            val ops2 = ops.subList(subListSize, ops.size)

            ops1.map { op ->
                cnt1.increment(op)
            }
            val vv = client1.getState()

            ops2.map { op ->
                res -= op
                cnt1.decrement(op)
            }
            val delta = cnt1.generateDelta(vv)
            cnt2.merge(delta)
            cnt2.get() == res
        }
    }

    "deserialize is inverse to serialize" {
        forAll(Arb.list(CounterOperationArb, 0..100)) { ops ->
            var res = 0
            val cnt = PNCounter(client1)

            ops.map { op ->
                when (op.first) {
                    CounterOpType.INCR -> {
                        res += op.second
                        cnt.increment(op.second)
                    }
                    CounterOpType.DECR -> {
                        res -= op.second
                        cnt.decrement(op.second)
                    }
                }
            }
            cnt.get() == PNCounter.fromJson(cnt.toJson()).get()
        }
    }
})
