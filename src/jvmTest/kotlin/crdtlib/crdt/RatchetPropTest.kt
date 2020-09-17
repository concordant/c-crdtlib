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

import crdtlib.utils.VersionVector
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.forAll
import java.lang.Integer.max


val RatchetIntArb = arb { rs ->
    val vs = Arb.int().values(rs)
    vs.map { v -> Ratchet(v.value)}
}

enum class OpType {
    ASSIGN, MERGE
}
val OperationArb = arb { rs ->
    val typs = Arb.enum<OpType>().values(rs)

    val vs = Arb.int().values(rs)
    typs.zip(vs).map { (t, v) -> Pair(t.value, v.value)}
}

class RatchetPropTest: StringSpec({

    "deserialize is inverse to serialize" {
        forAll(RatchetIntArb) { r ->
            r.get() == Ratchet.fromJson<Int>(r.toJson()).get()
            //TODO: After fixing equality, this should also work:
            // r == Ratchet.fromJson<Int>(r.toJson())
        }
    }
    "get initial value" {
        forAll(Arb.string()){ s ->
            s == Ratchet(s).get()
        }
    }
    "arbitrary set and merge always yields largest element" {
        forAll(Arb.list(OperationArb)) { ops ->
            val maybeMaximum = ops.maxBy { it.second }
            val maximum = maybeMaximum?.second ?: Int.MIN_VALUE

            val r = Ratchet(Int.MIN_VALUE)
            ops.map { op ->
                when(op.first){
                    OpType.ASSIGN -> r.assign(op.second)
                    OpType.MERGE -> r.merge(Ratchet(op.second))
                }}
            r.get() == maximum
        }
    }

    "merge with deltas"{
        forAll(Arb.list(Arb.int()), Arb.list(Arb.int())) { ops1, ops2 ->
            val m1 = ops1.max() ?: Int.MIN_VALUE
            val m2 = ops2.max() ?: Int.MIN_VALUE

            val r1 = Ratchet(Int.MIN_VALUE)
            val r2 = Ratchet(Int.MIN_VALUE)
            ops1.map { op -> r1.assign(op) }
            ops2.map { op -> r2.assign(op) }

            val d = r1.generateDelta(VersionVector())
            r2.merge(d)
            r2.get() == max(m1, m2)
        }
    }
})
