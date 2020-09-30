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
import io.kotest.matchers.*
import io.kotest.matchers.booleans.*
import io.kotest.matchers.comparables.*


/**
* Represents a test suite for SimpleEnvironment.
**/
class SimpleEnvironmentTest : StringSpec({

    /**
    * This test evaluates that the first timestamp generated by an empty environment has the good
    * datacenter unique id (the one associated with the environment) and a count equals to 1.
    **/
    "empty dc gets new timestamp" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)

        val ts = dc.getNewTimestamp()

        ts.shouldBe(Timestamp(uid, Timestamp.CNT_MIN_VALUE))
    }

    /**
    * This test evaluates that the current state associated with an empty environment is an empty
    * version vector.
    **/
    "empty dc gets current state" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val emptyVV = VersionVector()

        val vv = dc.getCurrentState()

        vv.isSmallerOrEquals(emptyVV).shouldBeTrue()
        emptyVV.isSmallerOrEquals(vv).shouldBeTrue()
    }

    /**
    * This test evaluates that after updating the environment with a local timestamp, the next
    * generated timestamp has a datacenter unique id equals to the one associated with the
    * environment and a count equals to the update timestamp count plus 1.
    **/
    "update with local timstamp then generate new timstamp" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)

        dc.updateStateTS(Timestamp(uid, 7))
        val ts = dc.getNewTimestamp()

        ts.shouldBe(Timestamp(uid, 8))
    }

    /**
    * This test evaluates that after updating the environment with a foreign timestamp, the next
    * generated timestamp has a datacenter unique id equals to the one associated with the
    * environment and a count equals to the update timestamp count plus 1.
    **/
    "update with foriegn timstamp then generate new timstamp" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc = SimpleEnvironment(uid1)

        dc.updateStateTS(Timestamp(uid2, 4))
        val ts = dc.getNewTimestamp()

        ts.shouldBe(Timestamp(uid1, 5))
    }

    /**
    * This test evaluates that after updating the environment with a local timestamp having a
    * MAX_VALUE counter, the next generation of timestamp throws an exception.
    **/
    "update with local timstamp then generate new timstamp and overflow" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)

        dc.updateStateTS(Timestamp(uid, Int.MAX_VALUE))

        shouldThrow<RuntimeException> {
            dc.getNewTimestamp()
        }
    }

    /**
    * This test evaluates that after updating the environment with a foreign timestamp having a
    * MAX_VALUE counter, the next generation of timestamp throws an exception.
    **/
    "update with foreign timstamp then generate new timstamp and overflow" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc = SimpleEnvironment(uid1)

        dc.updateStateTS(Timestamp(uid2, Int.MAX_VALUE))

        shouldThrow<RuntimeException> {
            dc.getNewTimestamp()
        }
    }

    /**
    * This test evaluates that after updating the environment with a foreign and a local timestamp,
    * the next generated timestamp has a datacenter unique id equals to the one associated with the
    * environment and a count equals to the max of update timestamp counts plus 1.
    **/
    "update with local and foreign timstamp then generate timstamp" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc = SimpleEnvironment(uid1)

        dc.updateStateTS(Timestamp(uid2, 6))
        dc.updateStateTS(Timestamp(uid1, 5))
        val ts = dc.getNewTimestamp()

        ts.shouldBe(Timestamp(uid1, 7))
    }

    /**
    * This test evaluates that after updating the environment with a local timestamp, the current
    * state associated with the environment is a version vector containing the update timestamp.
    **/
    "update with local timstamp then get current state" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = Timestamp(uid, 7)
        val cmpVV = VersionVector()
        cmpVV.addTS(ts)

        dc.updateStateTS(ts)
        val vv = dc.getCurrentState()

        vv.isSmallerOrEquals(cmpVV).shouldBeTrue()
        cmpVV.isSmallerOrEquals(vv).shouldBeTrue()
    }

    /**
    * This test evaluates that after updating the environment with a foreign timestamp, the current
    * state associated with the environment is a version vector containing the update timestamp.
    **/
    "update with foreign timstamp then get current state" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc = SimpleEnvironment(uid1)
        val ts = Timestamp(uid2, 5)
        val cmpVV = VersionVector()
        cmpVV.addTS(ts)

        dc.updateStateTS(ts)
        val vv = dc.getCurrentState()

        vv.isSmallerOrEquals(cmpVV).shouldBeTrue()
        cmpVV.isSmallerOrEquals(vv).shouldBeTrue()
    }

    /**
    * This test evaluates that after updating the environment with a foreign and a local timestamp,
    * the current state associated with the environment is a version vector containing the update
    * timestamps.
    **/
    "update with local and foreign timstamp then get current state" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc = SimpleEnvironment(uid1)
        val ts1 = Timestamp(uid1, 7)
        val ts2 = Timestamp(uid2, 6)
        val cmpVV = VersionVector()
        cmpVV.addTS(ts1)
        cmpVV.addTS(ts2)

        dc.updateStateTS(ts1)
        dc.updateStateTS(ts2)
        val vv = dc.getCurrentState()

        vv.isSmallerOrEquals(cmpVV).shouldBeTrue()
        cmpVV.isSmallerOrEquals(vv).shouldBeTrue()
    }

    /**
    * This test evaluates that after updating the environment with a version vector, the next
    * generated timestamp has a datacenter unique id equals to the one associated with the
    * environment and a count equals to the max value in the update version vector plus 1.
    **/
    "update with version vector then generate timstamp" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc = SimpleEnvironment(uid1)
        val ts1 = Timestamp(uid1, 6)
        val ts2 = Timestamp(uid2, 7)
        val vv = VersionVector()
        vv.addTS(ts1)
        vv.addTS(ts2)

        dc.updateStateVV(vv)
        val ts3 = dc.getNewTimestamp()

        ts3.shouldBe(Timestamp(uid1, 8))
    }

    /**
    * This test evaluates that after updating the environment with a version vector, the current
    * state associated to the environment equals to the update version vector.
    **/
    "update with version vector then get current state" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc = SimpleEnvironment(uid1)
        val ts1 = Timestamp(uid1, 6)
        val ts2 = Timestamp(uid2, 5)
        val vv1 = VersionVector()
        vv1.addTS(ts1)
        vv1.addTS(ts2)

        dc.updateStateVV(vv1)
        val vv2 = dc.getCurrentState()

        vv1.isSmallerOrEquals(vv2).shouldBeTrue()
        vv2.isSmallerOrEquals(vv1).shouldBeTrue()
    }
})