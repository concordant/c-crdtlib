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
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.forAll

val RatchetArb = arbitrary { rs ->
    Ratchet(Arb.string().next(rs))
}

enum class OpType {
    ASSIGN, MERGE
}
val OperationArb = arbitrary { rs ->
    Pair(Arb.enum<OpType>().next(rs), Arb.string().next(rs))
}

class RatchetPropTest : StringSpec({

    val uid1 = ClientUId("clientid1")
    var client1 = SimpleEnvironment(uid1)

    beforeTest {
        client1 = SimpleEnvironment(uid1)
    }

    "deserialize is inverse to serialize" {
        forAll(RatchetArb) { r ->
            r.get() == Ratchet.fromJson(r.toJson()).get()
            // TODO: After fixing equality, this should also work:
            // r == Ratchet.fromJson<String>(r.toJson())
        }
    }

    "get initial value" {
        forAll(Arb.string()) { s ->
            s == Ratchet(s).get()
        }
    }

    "arbitrary set and merge always yields largest element" {
        forAll(500, Arb.list(OperationArb)) { ops ->
            val maybeMaximum = ops.maxByOrNull { it.second }
            val maximum = maybeMaximum?.second ?: ""

            val r = Ratchet("", client1)
            ops.map { op ->
                when (op.first) {
                    OpType.ASSIGN -> r.assign(op.second)
                    OpType.MERGE -> r.merge(Ratchet(op.second))
                }
            }
            r.get() == maximum
        }
    }

    "merge with deltas" {
        forAll(750, Arb.list(Arb.string(), 0..25), Arb.list(Arb.string(), 0..25)) { ops1, ops2 ->
            val m1 = ops1.maxOrNull() ?: ""
            val m2 = ops2.maxOrNull() ?: ""

            val r1 = Ratchet("", client1)
            val r2 = Ratchet("", client1)
            ops1.map { op -> r1.assign(op) }
            ops2.map { op -> r2.assign(op) }

            val d = r1.generateDelta(VersionVector())
            r2.merge(d)
            r2.get() == maxOf(m1, m2)
        }
    }
})
