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

package crdtlib.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll

class VersionVectorPropTest: StringSpec({
    "deserialize is inverse to serialize" {
        forAll(versionVectorArb) { vv ->
            val vv2 = VersionVector.fromJson(VersionVector(vv).toJson())
            vv == vv2
        }
    }

    "two vvs are either concurrent or comparable" {
        forAll(versionVectorArb, versionVectorArb) { vv1, vv2 ->
            val isComparable = vv1.isSmaller(vv2) || vv1.isGreater(vv2) || vv1 == vv2
            val isConcurrent = vv1.isConcurrent(vv2) && vv2.isConcurrent(vv1)
            isComparable xor isConcurrent
        }
    }

    "copy generates equal version vector" {
        forAll(versionVectorArb) { vv1 ->
            val vv2 = vv1.copy()
            vv1 == vv2
        }
    }

    "merge is idempotent" {
        forAll(versionVectorArb) { vv1 ->
            val vv2 = vv1.copy()
            vv2.pointWiseMax(vv1)
            vv1 == vv2
        }
    }
    "merge is commutative" {
        forAll(versionVectorArb,versionVectorArb) { vv1, vv2 ->
            val vv2copy = vv2.copy()
            vv2.pointWiseMax(vv1)
            vv1.pointWiseMax(vv2copy)
            vv1 == vv2
        }
    }
    "added timestamp should be included" {
        forAll(versionVectorArb, timestampArb) { vv1, ts ->
            vv1.addTS(ts)
            vv1.includesTS(ts)
        }
    }
    "new version vector includes no timestamp" {
        forAll(timestampArb) { ts ->
            !(VersionVector().includesTS(ts))
        }
    }
    "incrementing maxVal" {
        forAll(versionVectorNonMaxArb, dcuidArb, Arb.int(Integer.MIN_VALUE, Int.MAX_VALUE-1)) { vv, dcuid, i ->
            val maxTS = vv.maxVal()
            val incrTS = 1 + (maxTS ?: i)
            vv.addTS(Timestamp(dcuid, incrTS))
            incrTS == vv.maxVal()
        }
    }
})



