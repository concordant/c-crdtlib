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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
* Represents a test suite for Timestamp.
**/
class TimestampTest {

    /**
    * This test evaluates the comparison of a smaller timestamp and a greater one, with same
    * datacenter unique id and different counts.
    * Call to compareTo should return negative value.
    */
    @Test
    fun negativeCompareToSameDCUIdDifferentCount() {
        val uid = DCUId("dcid")
        val ts1 = Timestamp(uid, 1)
        val ts2 = Timestamp(uid, 2)

        val cmp = ts1.compareTo(ts2)

        assertTrue(cmp < 0)
    }

    /**
    * This test evaluates the comparison of a smaller timestamp and a greater one, with different
    * datacenter unique ids and different counts.
    * Call to compareTo should return negative value.
    */
    @Test
    fun negativeCompareToDifferentDCUIdDifferentCount() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid2, 2)

        val cmp = ts1.compareTo(ts2)

        assertTrue(cmp < 0)
    }

    /**
    * This test evaluates the comparison of a smaller timestamp and a greater one, with different
    * datacenter unique ids and same count.
    * Call to compareTo should return negative value.
    */
    @Test
    fun negativeCompareToDifferentDCUIdSameCount() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid2, 1)

        val cmp = ts2.compareTo(ts1)

        assertTrue(cmp > 0)
    }

    /**
    * This test evaluates the comparison of a greater timestamp and a smaller one, with same
    * datacenter unique id and different counts.
    * Call to compareTo should return positive value.
    */
    @Test
    fun positiveCompareToSameDCUIdDifferentCount() {
        val uid = DCUId("dcid")
        val ts1 = Timestamp(uid, 1)
        val ts2 = Timestamp(uid, 2)

        val cmp = ts2.compareTo(ts1)

        assertTrue(cmp > 0)
    }

    /**
    * This test evaluates the comparison of a greater timestamp and a smaller one, with different
    * datacenter unique ids and different counts.
    * Call to compareTo should return positive value.
    */
    @Test
    fun positiveCompareToDifferentDCUIdDifferentCount() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid2, 2)

        val cmp = ts2.compareTo(ts1)

        assertTrue(cmp > 0)
    }

    /**
    * This test evaluates the comparison of a greater timestamp and a smaller one, with different
    * datacenter unique ids and same count.
    * Call to compareTo should return positive value.
    */
    @Test
    fun positiveCompareToDifferentDCUIdSameCount() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid2, 1)

        val cmp = ts1.compareTo(ts2)

        assertTrue(cmp < 0)
    }

    /**
    * This test evaluates the comparison of two equal timestamps.
    * Call to compareTo should return zero.
    */
    @Test
    fun zeroCompareTo() {
        val uid = DCUId("dcid")
        val ts1 = Timestamp(uid, 1)
        val ts2 = Timestamp(uid, 1)

        val cmp = ts1.compareTo(ts2)

        assertEquals(0, cmp)
    }

    /**
    * This test evaluates the comparison (using operators) of a smaller timestamp and a greater one,
    * with same datacenter unique id and different counts.
    */
    @Test
    fun negativeCompareToOperatorSameDCUIdDifferentCount() {
        val uid = DCUId("dcid")
        val ts1 = Timestamp(uid, 1)
        val ts2 = Timestamp(uid, 2)

        assertTrue(ts1 < ts2)
        assertTrue(ts1 <= ts2)
        assertFalse(ts1 > ts2)
        assertFalse(ts1 >= ts2)
        assertFalse(ts1 == ts2)
    }

    /**
    * This test evaluates the comparison (using operators) of a smaller timestamp and a greater one,
    * with different datacenter unique ids and different counts.
    */
    @Test
    fun negativeCompareToOperatorDifferentDCUIdDifferentCount() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid2, 2)

        assertTrue(ts1 < ts2)
        assertTrue(ts1 <= ts2)
        assertFalse(ts1 > ts2)
        assertFalse(ts1 >= ts2)
        assertFalse(ts1 == ts2)
    }

    /**
    * This test evaluates the comparison (using operators) of a smaller timestamp and a greater one,
    * with different datacenter unique ids and same count.
    */
    @Test
    fun negativeCompareToOperatorDifferentDCUIdSameCount() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid2, 1)

        assertTrue(ts1 < ts2)
        assertTrue(ts1 <= ts2)
        assertFalse(ts1 > ts2)
        assertFalse(ts1 >= ts2)
        assertFalse(ts1 == ts2)
    }

    /**
    * This test evaluates the comparison (using operators) of a greater timestamp and a smaller one,
    * with same datacenter unique id and different counts.
    */
    @Test
    fun positiveCompareToOperatorSameDCUIdDifferentCount() {
        val uid = DCUId("dcid")
        val ts1 = Timestamp(uid, 1)
        val ts2 = Timestamp(uid, 2)

        assertFalse(ts2 < ts1)
        assertFalse(ts2 <= ts1)
        assertTrue(ts2 > ts1)
        assertTrue(ts2 >= ts1)
        assertFalse(ts2 == ts1)
    }

    /**
    * This test evaluates the comparison (using operators) of a greater timestamp and a smaller one,
    * with different datacenter unique  ids and different counts.
    */
    @Test
    fun positiveCompareToOperatorDifferentDCUIdDifferentCount() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid2, 2)

        assertFalse(ts2 < ts1)
        assertFalse(ts2 <= ts1)
        assertTrue(ts2 > ts1)
        assertTrue(ts2 >= ts1)
        assertFalse(ts2 == ts1)
    }

    /**
    * This test evaluates the comparison (using operators) of a greater timestamp and a smaller one,
    * with different datacenter unique ids and same count.
    */
    @Test
    fun positiveCompareToOperatorDifferentDCUIdSameCount() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid2, 1)

        assertFalse(ts2 < ts1)
        assertFalse(ts2 <= ts1)
        assertTrue(ts2 > ts1)
        assertTrue(ts2 >= ts1)
        assertFalse(ts2 == ts1)
    }

    /**
    * This test evaluates the comparison (using operators) of two equal timestamps.
    */
    @Test
    fun zeroCompareToOperator() {
        val uid = DCUId("dcid")
        val ts1 = Timestamp(uid, 1)
        val ts2 = Timestamp(uid, 1)

        assertFalse(ts1 < ts2)
        assertTrue(ts1 <= ts2)
        assertFalse(ts1 > ts2)
        assertTrue(ts1 >= ts2)
        assertTrue(ts1 == ts2)
    }

    /**
    * This test evaluates JSON serialization.
    **/
    @Test
    fun toJsonSerialization() {
        val ts = Timestamp(DCUId("dcid1"), 3)

        val tsJson = ts.toJson()

        assertEquals("""{"uid":{"name":"dcid1"},"cnt":3}""", tsJson)
    }

    /**
    * This test evaluates JSON deserialization.
    **/
    @Test
    fun fromJsonDeserialization() {
        val ts = Timestamp(DCUId("dcid1"), 3)

        val tsJson = Timestamp.fromJson("""{"uid":{"name":"dcid1"},"cnt":3}""")

        assertTrue(ts == tsJson)
    }
}
