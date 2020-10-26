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
import io.kotest.matchers.*
import io.kotest.matchers.booleans.*
import io.kotest.matchers.comparables.*
import io.kotest.matchers.nulls.*

/**
* Represents a test suite for VersionVector.
**/
class VersionVectorTest : StringSpec({

    /**
     * This test evaluates that the maximum value of newly created version vector is null.
     **/
    "empty version vector get max" {
        val vv = VersionVector()

        vv.max().shouldBe(Timestamp.CNT_MIN_VALUE)
    }

    /**
     * This test evaluates that the value returned by the max method after adding multiple
     * timestamps is correct.
     **/
    "multiple values get max" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid3, 1)
        val vv = VersionVector()

        vv.update(ts1)
        vv.update(ts2)
        vv.update(ts3)

        vv.max().shouldBe(3)
    }

    /**
     * This test evaluates the inclusion of timestamps in a newly created version vector.
     * Calls to contains should return false.
     **/
    "empty version vector contains no timestamp" {
        val uid = ClientUId("clientid")
        val ts1 = Timestamp(uid, 1)
        val ts2 = Timestamp(uid, 3)
        val vv = VersionVector()

        vv.contains(ts1).shouldBeFalse()
        vv.contains(ts2).shouldBeFalse()
    }

    /**
     * This test evaluates the inclusion of timestamps having negative counter in a newly created
     * version vector.
     * Calls to contains should return false.
     **/
    "empty version vector contains no negative timestamp except Timestamp CNT_MIN_VALUE" {
        val uid = ClientUId("clientid")
        val ts1 = Timestamp(uid, Timestamp.CNT_MIN_VALUE)
        val ts2 = Timestamp(uid, Timestamp.CNT_MIN_VALUE + 1)
        val ts3 = Timestamp(uid, -8000)
        val vv = VersionVector()

        vv.contains(ts1).shouldBeTrue()
        vv.contains(ts2).shouldBeFalse()
        vv.contains(ts3).shouldBeFalse()
    }

    /**
     * This test evaluates the inclusion of timestamps in a version vector where one timestamp has
     * been added.
     * Calls to contains should return true for all timestamps with same client unique id and a
     * count less or equals to the added timestamp, and false otherwise.
     **/
    "add timstamp then check inclusion" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid1, 2)
        val ts3 = Timestamp(uid1, 3)
        val ts4 = Timestamp(uid2, 1)
        val vv = VersionVector()

        vv.update(ts2)

        vv.contains(ts1).shouldBeTrue()
        vv.contains(ts2).shouldBeTrue()
        vv.contains(ts3).shouldBeFalse()
        vv.contains(ts4).shouldBeFalse()
    }

    /**
     * This test evaluates the merging of a first none empty version vector into a second empty one.
     * Calls to contains at second version vector should return true for all timestamps that were
     * added to the first one.
     **/
    "update to an empty version vector" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 2)
        val ts2 = Timestamp(uid2, 3)
        val ts3 = Timestamp(uid3, 1)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.update(ts1)
        vv1.update(ts2)
        vv1.update(ts3)
        vv2.update(vv1)

        vv2.contains(ts1).shouldBeTrue()
        vv2.contains(ts2).shouldBeTrue()
        vv2.contains(ts3).shouldBeTrue()
    }

    /**
     * This test evaluates the merging of a first empty version vector into a second none empty one.
     * Calls to contains at second version vector should return true for all timestamps that were
     * added to it before merging.
     **/
    "update from an empty version vector" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 2)
        val ts2 = Timestamp(uid2, 3)
        val ts3 = Timestamp(uid3, 1)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.update(ts1)
        vv1.update(ts2)
        vv1.update(ts3)
        vv1.update(vv2)

        vv1.contains(ts1).shouldBeTrue()
        vv1.contains(ts2).shouldBeTrue()
        vv1.contains(ts3).shouldBeTrue()
    }

    /**
     * This test evaluates the merging of a first version vector into a second smaller one.
     * Calls to contains at second version vector should return true for all timestamps that were
     * added to the first version vector.
     **/
    "update to a smaller version vector" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 2)
        val ts2 = Timestamp(uid2, 3)
        val ts3 = Timestamp(uid3, 1)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.update(ts1)
        vv1.update(ts2)
        vv1.update(ts4)
        vv2.update(ts1)
        vv2.update(ts3)
        vv2.update(vv1)

        vv2.contains(ts1).shouldBeTrue()
        vv2.contains(ts2).shouldBeTrue()
        vv2.contains(ts3).shouldBeTrue()
        vv2.contains(ts4).shouldBeTrue()
    }

    /**
     * This test evaluates the merging of a first smaller version vector into a second one.
     * Calls to contains at second version vector should return true for all timestamps that were
     * added to it before merging.
     **/
    "update from a smaller version vector" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 2)
        val ts2 = Timestamp(uid2, 3)
        val ts3 = Timestamp(uid3, 1)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.update(ts1)
        vv1.update(ts2)
        vv1.update(ts4)
        vv2.update(ts1)
        vv2.update(ts3)
        vv1.update(vv2)

        vv1.contains(ts1).shouldBeTrue()
        vv1.contains(ts2).shouldBeTrue()
        vv1.contains(ts3).shouldBeTrue()
        vv1.contains(ts4).shouldBeTrue()
    }

    /**
     * This test evaluates the merging of two equal version vectors.
     * Calls to contains at second version vector should return true for all timestamps that were
     * added to it before merging.
     **/
    "update between two equaled version vectors" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 2)
        val ts2 = Timestamp(uid2, 3)
        val ts3 = Timestamp(uid3, 1)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.update(ts1)
        vv1.update(ts2)
        vv1.update(ts3)
        vv2.update(ts1)
        vv2.update(ts2)
        vv2.update(ts3)
        vv1.update(vv2)

        vv1.contains(ts1).shouldBeTrue()
        vv1.contains(ts2).shouldBeTrue()
        vv1.contains(ts3).shouldBeTrue()
    }

    /**
     * This test evaluates the merging of two concurrent version vectors.
     * Calls to contains at second version vector should return true for all timestamps that were
     * added to itself and to the first version vector.
     **/
    "update between two concurrent version vectors" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val uid4 = ClientUId("clientid4")
        val ts1 = Timestamp(uid1, 2)
        val ts2 = Timestamp(uid2, 3)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 1)
        val ts5 = Timestamp(uid3, 2)
        val ts6 = Timestamp(uid4, 3)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.update(ts1)
        vv1.update(ts2)
        vv1.update(ts5)
        vv2.update(ts3)
        vv2.update(ts4)
        vv2.update(ts6)
        vv2.update(vv1)

        vv2.contains(ts1).shouldBeTrue()
        vv2.contains(ts2).shouldBeTrue()
        vv2.contains(ts3).shouldBeTrue()
        vv2.contains(ts4).shouldBeTrue()
        vv2.contains(ts5).shouldBeTrue()
        vv2.contains(ts6).shouldBeTrue()
    }

    /**
     * This test evaluates smaller or equals comparison between a first smaller version vector and a
     * second one with the same entries.
     * Calls to smallerOrEquals should return true.
     **/
    "is smaller or equals with a smaller version vector with same entries" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.update(ts1)
        vv1.update(ts2)
        vv1.update(ts4)
        vv2.update(ts1)
        vv2.update(ts3)
        vv2.update(ts4)

        vv1.isSmallerOrEquals(vv2).shouldBeTrue()
        vv1.isSmaller(vv2).shouldBeTrue()
        vv1.isGreaterOrEquals(vv2).shouldBeFalse()
        vv1.isGreater(vv2).shouldBeFalse()
        vv1.equals(vv2).shouldBeFalse()
        vv1.isNotComparable(vv2).shouldBeFalse()
    }

    /**
     * This test evaluates smaller or equals comparison between a first smaller version vector and a
     * second one with more entries.
     * Calls to smallerOrEquals should return true.
     **/
    "is smaller or equals with a smaller version vector with less entries" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.update(ts2)
        vv1.update(ts4)
        vv2.update(ts1)
        vv2.update(ts3)
        vv2.update(ts4)

        vv1.isSmallerOrEquals(vv2).shouldBeTrue()
        vv1.isSmaller(vv2).shouldBeTrue()
        vv1.isGreaterOrEquals(vv2).shouldBeFalse()
        vv1.isGreater(vv2).shouldBeFalse()
        vv1.equals(vv2).shouldBeFalse()
        vv1.isNotComparable(vv2).shouldBeFalse()
    }

    /**
     * This test evaluates smaller or equals comparison between two equal version vectors.
     * Calls to smallerOrEquals should return true.
     **/
    "is smaller or equals with an equaled version vector" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 4)
        val ts3 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.update(ts1)
        vv1.update(ts2)
        vv1.update(ts3)
        vv2.update(ts1)
        vv2.update(ts2)
        vv2.update(ts3)

        vv1.isSmallerOrEquals(vv2).shouldBeTrue()
        vv1.isSmaller(vv2).shouldBeFalse()
        vv1.isGreaterOrEquals(vv2).shouldBeTrue()
        vv1.isGreater(vv2).shouldBeFalse()
        vv1.equals(vv2).shouldBeTrue()
        vv1.isNotComparable(vv2).shouldBeFalse()
    }

    /**
     * This test evaluates smaller or equals comparison between a first greater version vector and a
     * second with the same entries.
     * Calls to smallerOrEquals should return false.
     **/
    "is smaller or equals with a greater version vector with same entries" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.update(ts1)
        vv1.update(ts3)
        vv1.update(ts4)
        vv2.update(ts1)
        vv2.update(ts2)
        vv2.update(ts4)

        vv1.isSmallerOrEquals(vv2).shouldBeFalse()
        vv1.isSmaller(vv2).shouldBeFalse()
        vv1.isGreaterOrEquals(vv2).shouldBeTrue()
        vv1.isGreater(vv2).shouldBeTrue()
        vv1.equals(vv2).shouldBeFalse()
        vv1.isNotComparable(vv2).shouldBeFalse()
    }

    /**
     * This test evaluates smaller or equals comparison between two concurrent version vectors with
     * the same entries.
     * Calls to smallerOrEquals should return false.
     **/
    "is smaller or equals with a concurrent version vector with same entries" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 2)
        val ts5 = Timestamp(uid3, 3)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.update(ts1)
        vv1.update(ts2)
        vv1.update(ts5)
        vv2.update(ts1)
        vv2.update(ts3)
        vv2.update(ts4)

        vv1.isSmallerOrEquals(vv2).shouldBeFalse()
        vv1.isSmaller(vv2).shouldBeFalse()
        vv1.isGreaterOrEquals(vv2).shouldBeFalse()
        vv1.isGreater(vv2).shouldBeFalse()
        vv1.equals(vv2).shouldBeFalse()
        vv1.isNotComparable(vv2).shouldBeTrue()
    }

    /**
     * This test evaluates smaller or equals comparison between a first version vector and a
     * concurrent second one with more entries.
     * Calls to smallerOrEquals should return false.
     **/
    "is smaller or equals with a concurrent version vector with less entries" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.update(ts1)
        vv1.update(ts3)
        vv2.update(ts1)
        vv2.update(ts2)
        vv2.update(ts4)

        vv1.isSmallerOrEquals(vv2).shouldBeFalse()
        vv1.isSmaller(vv2).shouldBeFalse()
        vv1.isGreaterOrEquals(vv2).shouldBeFalse()
        vv1.isGreater(vv2).shouldBeFalse()
        vv1.equals(vv2).shouldBeFalse()
        vv1.isNotComparable(vv2).shouldBeTrue()
    }

    /**
     * This test evaluates smaller or equals comparison between a first greater version vector and a
     * second one with less entries.
     * Calls to smallerOrEquals should return false.
     **/
    "is smaller or equals with a greater version vector with more entries" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.update(ts1)
        vv1.update(ts3)
        vv1.update(ts4)
        vv2.update(ts2)
        vv2.update(ts4)

        vv1.isSmallerOrEquals(vv2).shouldBeFalse()
        vv1.isSmaller(vv2).shouldBeFalse()
        vv1.isGreaterOrEquals(vv2).shouldBeTrue()
        vv1.isGreater(vv2).shouldBeTrue()
        vv1.equals(vv2).shouldBeFalse()
        vv1.isNotComparable(vv2).shouldBeFalse()
    }

    /**
     * This test evaluates smaller or equals comparison between a first version vector and a second
     * concurrent one with less entries.
     * Calls to smallerOrEquals should return false.
     **/
    "is smaller or equals with a concurrent version vector with more entries" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.update(ts1)
        vv1.update(ts2)
        vv1.update(ts4)
        vv2.update(ts1)
        vv2.update(ts3)

        vv1.isSmallerOrEquals(vv2).shouldBeFalse()
        vv1.isSmaller(vv2).shouldBeFalse()
        vv1.isGreaterOrEquals(vv2).shouldBeFalse()
        vv1.isGreater(vv2).shouldBeFalse()
        vv1.equals(vv2).shouldBeFalse()
        vv1.isNotComparable(vv2).shouldBeTrue()
    }

    /**
     * This test evaluates smaller or equals comparison between a first version vector and a second
     * concurrent one, where both have an entry not present in the other one.
     * Calls to smallerOrEquals should return false.
     **/
    "is smaller or equals with a concurrent version vector having different entries" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.update(ts2)
        vv1.update(ts4)
        vv2.update(ts1)
        vv2.update(ts3)

        vv1.isSmallerOrEquals(vv2).shouldBeFalse()
        vv1.isSmaller(vv2).shouldBeFalse()
        vv1.isGreaterOrEquals(vv2).shouldBeFalse()
        vv1.isGreater(vv2).shouldBeFalse()
        vv1.equals(vv2).shouldBeFalse()
        vv1.isNotComparable(vv2).shouldBeTrue()
    }

    /**
     * This test evaluates the use of copy method.
     * Calls to contains at second version vector should return true for all timestamps that were
     * added to the first one.
     **/
    "copy with copy method" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 4)
        val ts3 = Timestamp(uid3, 2)
        val vv1 = VersionVector()

        vv1.update(ts1)
        vv1.update(ts2)
        vv1.update(ts3)
        val vv2 = vv1.copy()

        vv1.isSmallerOrEquals(vv2).shouldBeTrue()
        vv2.isSmallerOrEquals(vv1).shouldBeTrue()
        vv1.isNotComparable(vv2).shouldBeFalse()
    }

    /**
     * This test evaluates the use of copy constructor.
     * Calls to contains at second version vector should return true for all timestamps that were
     * added to the first one.
     **/
    "copy with copy constructor" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 4)
        val ts3 = Timestamp(uid3, 2)
        val vv1 = VersionVector()

        vv1.update(ts1)
        vv1.update(ts2)
        vv1.update(ts3)
        val vv2 = VersionVector(vv1)

        vv1.isSmallerOrEquals(vv2).shouldBeTrue()
        vv2.isSmallerOrEquals(vv1).shouldBeTrue()
        vv1.isNotComparable(vv2).shouldBeFalse()
    }

    /**
     * This test evaluates JSON serialization of an empty version vector.
     **/
    "empty JSON serialization" {
        val vv = VersionVector()

        val vvJson = vv.toJson()

        vvJson.shouldBe("""{"entries":[]}""")
    }

    /**
     * This test evaluates JSON deserialization of an empty version vector.
     **/
    "empty JSON deserialization" {
        val vv = VersionVector()

        val vvJson = VersionVector.fromJson("""{"entries":[]}""")

        vv.isSmallerOrEquals(vvJson).shouldBeTrue()
        vvJson.isSmallerOrEquals(vv).shouldBeTrue()
    }

    /**
     * This test evaluates JSON serialization of a version vector.
     **/
    "JSON serialization" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 4)
        val ts3 = Timestamp(uid3, 2)
        val vv = VersionVector()

        vv.update(ts1)
        vv.update(ts2)
        vv.update(ts3)
        val vvJson = vv.toJson()

        vvJson.shouldBe("""{"entries":[{"name":"clientid1"},3,{"name":"clientid2"},4,{"name":"clientid3"},2]}""")
    }

    /**
     * This test evaluates JSON deserialization of a version vector.
     **/
    "JSON deserialization" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 4)
        val ts3 = Timestamp(uid3, 2)
        val vv = VersionVector()

        vv.update(ts1)
        vv.update(ts2)
        vv.update(ts3)
        val vvJson = VersionVector.fromJson("""{"entries":[{"name":"clientid1"},3,{"name":"clientid2"},4,{"name":"clientid3"},2]}""")

        vv.isSmallerOrEquals(vvJson).shouldBeTrue()
        vvJson.isSmallerOrEquals(vv).shouldBeTrue()
    }
})
