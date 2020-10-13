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

import io.kotest.assertions.throwables.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll

/**
* Represents a test suite for SimpleEnvironment.
**/
class SimpleEnvironmentPropTest: StringSpec( {

    // AB: This seems rather special for this particular environment
    "initial environment has counter smaller than any other timestamp" {
        // Int.MIN_VALUE is a Kotest edge case (always generated):
        // use it to be sure we test Int.MIN_VALUE + 1.
        forAll(clientuidArb, Arb.int(Int.MIN_VALUE, Int.MAX_VALUE - 1)) { uid, i ->
            val se = SimpleEnvironment(uid)
            se.tick().compareTo(Timestamp(uid, i + 1)) <= 0
        }
    }
    "empty environment has empty version vector" {
        forAll(simpleEnvironmentArb){ se ->
            val vv = se.getState()
            vv.isSmallerOrEquals(VersionVector()) && VersionVector().isSmallerOrEquals(vv)
        }
    }
    "update of environment increments counter" {
        forAll(clientuidArb, Arb.int(Int.MIN_VALUE, Int.MAX_VALUE -1)){ uid, cnt ->
            val se = SimpleEnvironment(uid)
            se.update(Timestamp(uid, cnt))
            val ts = se.tick()
            ts.cnt > cnt
        }
    }
    "timestamps are monotonically increasing" {
        forAll(clientuidArb, clientuidArb, Arb.int(Int.MIN_VALUE, Int.MAX_VALUE -1), Arb.int(Int.MIN_VALUE, Int.MAX_VALUE -1)){ uid1, uid2, cnt1, cnt2 ->
            val se = SimpleEnvironment(uid1)
            se.update(Timestamp(uid1, cnt1))
            se.update(Timestamp(uid2, cnt2))
            val ts = se.tick()

            ts.cnt > cnt1 && ts.cnt > cnt2
        }
    }

    "exception when arbitrary entry reaches max val" {
        forAll(clientuidArb, clientuidArb){ uid1, uid2 ->
            val se = SimpleEnvironment(uid1)
            se.update(Timestamp(uid2, Int.MAX_VALUE))
            shouldThrow<RuntimeException> {
                se.tick()
            }
            true
        }
    }


    "new timestamps are monotonic" {
        forAll(clientuidArb, clientuidArb, Arb.int(Int.MIN_VALUE, Int.MAX_VALUE -1), Arb.int(Int.MIN_VALUE, Int.MAX_VALUE -1)){ uid1, uid2, cnt1, cnt2->
            val se = SimpleEnvironment(uid1)
            se.update(Timestamp(uid1, cnt1))
            se.update(Timestamp(uid2, cnt2))
            val ts1 = se.tick()
            val ts2 = se.tick()

            ts1.cnt < ts2.cnt
        }
    }

    "added timestamp is included in state version vector" {
        forAll(simpleEnvironmentArb, timestampArb){ se, ts ->
            se.update(ts)
            val vv = se.getState()
            vv.contains(ts)
        }
    }

    "updated state includes added version vector" {
        forAll(simpleEnvironmentArb, versionVectorArb){ se, vv ->
            se.update(vv)
            val vv2 = se.getState()
            vv.isSmallerOrEquals(vv2)
        }
    }

})
