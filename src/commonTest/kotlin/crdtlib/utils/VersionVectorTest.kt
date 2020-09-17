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

package crdtlib.test

import crdtlib.utils.DCUId
import crdtlib.utils.Timestamp
import crdtlib.utils.VersionVector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
* Represents a test suite for VersionVector.
**/
class VersionVectorTest {
    
    /**
    * This test evaluates that the maximum value of newly created version vector is equal to 0.
    **/
    @Test
    fun createMaxVal() {
        val vv = VersionVector()

        assertEquals(null, vv.maxVal())
    }

    /**
    * This test evaluates that the value returned by the maxVal method after adding multiple
    * timestamps is correct.
    **/
    @Test
    fun multipleValuesMaxVal() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid3, 1)
        val vv = VersionVector()

        vv.addTS(ts1)
        vv.addTS(ts2)
        vv.addTS(ts3)

        assertEquals(3, vv.maxVal())
    }

    /**
    * This test evaluates the inclusion of timestamps in a newly created version vector.
    * Calls to includeTS should return false.
    **/
    @Test
    fun createIncludeTS() {
        val uid = DCUId("dcid")
        val ts1 = Timestamp(uid, 1)
        val ts2 = Timestamp(uid, 3)
        val vv = VersionVector()

        assertFalse(vv.includesTS(ts1))
        assertFalse(vv.includesTS(ts2))
    }

    /**
    * This test evaluates the inclusion of timestamps having negative counter in a newly created version vector.
    * Calls to includeTS should return false.
    **/
    @Test
    fun createIncludeNegativeTS() {
        val uid = DCUId("dcid")
        val ts1 = Timestamp(uid, Int.MIN_VALUE)
        val ts2 = Timestamp(uid, Timestamp.CNT_MIN_VALUE)
        val ts3 = Timestamp(uid, -8000)
        val vv = VersionVector()

        assertFalse(vv.includesTS(ts1))
        assertFalse(vv.includesTS(ts2))
        assertFalse(vv.includesTS(ts3))
    }

    /**
    * This test evaluates the inclusion of timestamps in a version vector where one timestamp has
    * been added.
    * Calls to includeTS should return true for all timestamps with same datacenter unique id and a
    * count less or equals to the added timestamp, and false otherwise.
    **/
    @Test
    fun addTSIncludeTS() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid1, 2)
        val ts3 = Timestamp(uid1, 3)
        val ts4 = Timestamp(uid2, 1)
        val vv = VersionVector()

        vv.addTS(ts2)

        assertTrue(vv.includesTS(ts1))
        assertTrue(vv.includesTS(ts2))
        assertFalse(vv.includesTS(ts3))
        assertFalse(vv.includesTS(ts4))
    }

    /**
    * This test evaluates the merging of a first none empty version vector into a second empty one.
    * Calls to includeTS at second version vector should return true for all timestamps that were
    * added to the first one.
    **/
    @Test
    fun toEmptyPointWiseMax() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 2)
        val ts2 = Timestamp(uid2, 3)
        val ts3 = Timestamp(uid3, 1)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.addTS(ts1)
        vv1.addTS(ts2)
        vv1.addTS(ts3)
        vv2.pointWiseMax(vv1)

        assertTrue(vv2.includesTS(ts1))
        assertTrue(vv2.includesTS(ts2))
        assertTrue(vv2.includesTS(ts3))
    }

    /**
    * This test evaluates the merging of a first empty version vector into a second none empty one.
    * Calls to includeTS at second version vector should return true for all timestamps that were
    * added to it before merging.
    **/
    @Test
    fun fromEmptyPointWiseMax() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 2)
        val ts2 = Timestamp(uid2, 3)
        val ts3 = Timestamp(uid3, 1)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.addTS(ts1)
        vv1.addTS(ts2)
        vv1.addTS(ts3)
        vv1.pointWiseMax(vv2)

        assertTrue(vv1.includesTS(ts1))
        assertTrue(vv1.includesTS(ts2))
        assertTrue(vv1.includesTS(ts3))
    }

    /**
    * This test evaluates the merging of a first version vector into a second smaller one.
    * Calls to includeTS at second version vector should return true for all timestamps that were
    * added to the first version vector.
    **/
    @Test
    fun toSmallerPointWiseMax() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 2)
        val ts2 = Timestamp(uid2, 3)
        val ts3 = Timestamp(uid3, 1)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.addTS(ts1)
        vv1.addTS(ts2)
        vv1.addTS(ts4)
        vv2.addTS(ts1)
        vv2.addTS(ts3)
        vv2.pointWiseMax(vv1)

        assertTrue(vv2.includesTS(ts1))
        assertTrue(vv2.includesTS(ts2))
        assertTrue(vv2.includesTS(ts3))
        assertTrue(vv2.includesTS(ts4))
    }

    /**
    * This test evaluates the merging of a first smaller version vector into a second one.
    * Calls to includeTS at second version vector should return true for all timestamps that were
    * added to it before merging.
    **/
    @Test
    fun fromSmallerPointWiseMax() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 2)
        val ts2 = Timestamp(uid2, 3)
        val ts3 = Timestamp(uid3, 1)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.addTS(ts1)
        vv1.addTS(ts2)
        vv1.addTS(ts4)
        vv2.addTS(ts1)
        vv2.addTS(ts3)
        vv1.pointWiseMax(vv2)

        assertTrue(vv1.includesTS(ts1))
        assertTrue(vv1.includesTS(ts2))
        assertTrue(vv1.includesTS(ts3))
        assertTrue(vv1.includesTS(ts4))
    }

    /**
    * This test evaluates the merging of two equal version vectors.
    * Calls to includeTS at second version vector should return true for all timestamps that were
    * added to it before merging.
    **/
    @Test
    fun equalPointWiseMax() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 2)
        val ts2 = Timestamp(uid2, 3)
        val ts3 = Timestamp(uid3, 1)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.addTS(ts1)
        vv1.addTS(ts2)
        vv1.addTS(ts3)
        vv2.addTS(ts1)
        vv2.addTS(ts2)
        vv2.addTS(ts3)
        vv1.pointWiseMax(vv2)

        assertTrue(vv1.includesTS(ts1))
        assertTrue(vv1.includesTS(ts2))
        assertTrue(vv1.includesTS(ts3))
    }

    /**
    * This test evaluates the merging of two concurrent version vectors.
    * Calls to includeTS at second version vector should return true for all timestamps that were
    * added to itself and to the first version vector.
    **/
    @Test
    fun concurrentPointWiseMax() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val uid4 = DCUId("dcid4")
        val ts1 = Timestamp(uid1, 2)
        val ts2 = Timestamp(uid2, 3)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 1)
        val ts5 = Timestamp(uid3, 2)
        val ts6 = Timestamp(uid4, 3)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.addTS(ts1)
        vv1.addTS(ts2)
        vv1.addTS(ts5)
        vv2.addTS(ts3)
        vv2.addTS(ts4)
        vv2.addTS(ts6)
        vv2.pointWiseMax(vv1)

        assertTrue(vv2.includesTS(ts1))
        assertTrue(vv2.includesTS(ts2))
        assertTrue(vv2.includesTS(ts3))
        assertTrue(vv2.includesTS(ts4))
        assertTrue(vv2.includesTS(ts5))
        assertTrue(vv2.includesTS(ts6))
    }

    /**
    * This test evaluates smaller or equals comparison between a first smaller version vector and a
    * second one with the same entries.
    * Calls to smallerOrEquals should return true.
    **/
    @Test
    fun sameEntriesSmaller() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.addTS(ts1)
        vv1.addTS(ts2)
        vv1.addTS(ts4)
        vv2.addTS(ts1)
        vv2.addTS(ts3)
        vv2.addTS(ts4)

        assertTrue(vv1.isSmallerOrEquals(vv2))
        assertFalse(vv1.isNotComparable(vv2))
    }

    /**
    * This test evaluates smaller or equals comparison between a first smaller version vector and a
    * second one with more entries.
    * Calls to smallerOrEquals should return true.
    **/
    @Test
    fun lessEntriesSmaller() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.addTS(ts2)
        vv1.addTS(ts4)
        vv2.addTS(ts1)
        vv2.addTS(ts3)
        vv2.addTS(ts4)

        assertTrue(vv1.isSmallerOrEquals(vv2))
        assertFalse(vv1.isNotComparable(vv2))
    }

    /**
    * This test evaluates smaller or equals comparison between two equal version vectors.
    * Calls to smallerOrEquals should return true.
    **/
    @Test
    fun sameEntriesEqual() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 4)
        val ts3 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.addTS(ts1)
        vv1.addTS(ts2)
        vv1.addTS(ts3)
        vv2.addTS(ts1)
        vv2.addTS(ts2)
        vv2.addTS(ts3)

        assertTrue(vv1.isSmallerOrEquals(vv2))
        assertFalse(vv1.isNotComparable(vv2))
    }

    /**
    * This test evaluates smaller or equals comparison between a first greater version vector and a
    * second with the same entries.
    * Calls to smallerOrEquals should return false.
    **/
    @Test
    fun sameEntriesGreater() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.addTS(ts1)
        vv1.addTS(ts3)
        vv1.addTS(ts4)
        vv2.addTS(ts1)
        vv2.addTS(ts2)
        vv2.addTS(ts4)

        assertFalse(vv1.isSmallerOrEquals(vv2))
        assertFalse(vv1.isNotComparable(vv2))
    }

    /**
    * This test evaluates smaller or equals comparison between two concurrent version vectors with
    * the same entries.
    * Calls to smallerOrEquals should return false.
    **/
    @Test
    fun sameEntriesConcurrent() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 2)
        val ts5 = Timestamp(uid3, 3)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.addTS(ts1)
        vv1.addTS(ts2)
        vv1.addTS(ts5)
        vv2.addTS(ts1)
        vv2.addTS(ts3)
        vv2.addTS(ts4)

        assertFalse(vv1.isSmallerOrEquals(vv2))
        assertTrue(vv1.isNotComparable(vv2))
    }

    /**
    * This test evaluates smaller or equals comparison between a first version vector and a
    * concurrent second one with more entries.
    * Calls to smallerOrEquals should return false.
    **/
    @Test
    fun lessEntriesConcurrent() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.addTS(ts1)
        vv1.addTS(ts3)
        vv2.addTS(ts1)
        vv2.addTS(ts2)
        vv2.addTS(ts4)

        assertFalse(vv1.isSmallerOrEquals(vv2))
        assertTrue(vv1.isNotComparable(vv2))
    }

    /**
    * This test evaluates smaller or equals comparison between a first greater version vector and a
    * second one with less entries.
    * Calls to smallerOrEquals should return false.
    **/
    @Test
    fun moreEntriesGreater() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.addTS(ts1)
        vv1.addTS(ts3)
        vv1.addTS(ts4)
        vv2.addTS(ts2)
        vv2.addTS(ts4)

        assertFalse(vv1.isSmallerOrEquals(vv2))
        assertFalse(vv1.isNotComparable(vv2))
    }

    /**
    * This test evaluates smaller or equals comparison between a first version vector and a second
    * concurrent one with less entries.
    * Calls to smallerOrEquals should return false.
    **/
    @Test
    fun moreEntriesConcurrent() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.addTS(ts1)
        vv1.addTS(ts2)
        vv1.addTS(ts4)
        vv2.addTS(ts1)
        vv2.addTS(ts3)

        assertFalse(vv1.isSmallerOrEquals(vv2))
        assertTrue(vv1.isNotComparable(vv2))
    }

    /**
    * This test evaluates smaller or equals comparison between a first version vector and a second
    * concurrent one, where both have an entry not present in the other one.
    * Calls to smallerOrEquals should return false.
    **/
    @Test
    fun moreAndLessEntriesConcurrent() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 2)
        val ts3 = Timestamp(uid2, 4)
        val ts4 = Timestamp(uid3, 2)
        val vv1 = VersionVector()
        val vv2 = VersionVector()

        vv1.addTS(ts2)
        vv1.addTS(ts4)
        vv2.addTS(ts1)
        vv2.addTS(ts3)

        assertFalse(vv1.isSmallerOrEquals(vv2))
        assertTrue(vv1.isNotComparable(vv2))
    }

    /**
    * This test evaluates the use of copy method.
    * Calls to includeTS at second version vector should return true for all timestamps that were
    * added to the first one.
    **/
    @Test
    fun copyMethod() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 4)
        val ts3 = Timestamp(uid3, 2)
        val vv1 = VersionVector()

        vv1.addTS(ts1)
        vv1.addTS(ts2)
        vv1.addTS(ts3)
        val vv2 = vv1.copy()

        assertTrue(vv1.isSmallerOrEquals(vv2))
        assertTrue(vv2.isSmallerOrEquals(vv1))
        assertFalse(vv1.isNotComparable(vv2))
    }

    /**
    * This test evaluates the use of copy constructor.
    * Calls to includeTS at second version vector should return true for all timestamps that were
    * added to the first one.
    **/
    @Test
    fun copyConstructor() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 4)
        val ts3 = Timestamp(uid3, 2)
        val vv1 = VersionVector()

        vv1.addTS(ts1)
        vv1.addTS(ts2)
        vv1.addTS(ts3)
        val vv2 = VersionVector(vv1)

        assertTrue(vv1.isSmallerOrEquals(vv2))
        assertTrue(vv2.isSmallerOrEquals(vv1))
        assertFalse(vv1.isNotComparable(vv2))
    }

    /**
    * This test evaluates JSON serialization of an empty version vector.
    **/
    @Test
    fun emptyToJsonSerialization() {
        val vv = VersionVector()

        val vvJson = vv.toJson()

        assertEquals("""{"entries":[]}""", vvJson)
    }

    /**
    * This test evaluates JSON deserialization of an empty version vector.
    **/
    @Test
    fun emptyFromJsonDeserialization() {
        val vv = VersionVector()

        val vvJson = VersionVector.fromJson("""{"entries":[]}""")

        assertTrue(vv.isSmallerOrEquals(vvJson))
        assertTrue(vvJson.isSmallerOrEquals(vv))
    }

    /**
    * This test evaluates JSON serialization of a version vector.
    **/
    @Test
    fun toJsonSerialization() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 4)
        val ts3 = Timestamp(uid3, 2)
        val vv = VersionVector()

        vv.addTS(ts1)
        vv.addTS(ts2)
        vv.addTS(ts3)
        val vvJson = vv.toJson()

        assertEquals("""{"entries":[{"name":"dcid1"},3,{"name":"dcid2"},4,{"name":"dcid3"},2]}""", vvJson)
    }

    /**
    * This test evaluates JSON deserialization of a version vector.
    **/
    @Test
    fun fromJsonDeserialization() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val ts1 = Timestamp(uid1, 3)
        val ts2 = Timestamp(uid2, 4)
        val ts3 = Timestamp(uid3, 2)
        val vv = VersionVector()

        vv.addTS(ts1)
        vv.addTS(ts2)
        vv.addTS(ts3)
        val vvJson = VersionVector.fromJson("""{"entries":[{"name":"dcid1"},3,{"name":"dcid2"},4,{"name":"dcid3"},2]}""")

        assertTrue(vv.isSmallerOrEquals(vvJson))
        assertTrue(vvJson.isSmallerOrEquals(vv))
    }
}
