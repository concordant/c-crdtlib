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

import crdtlib.utils.DCId
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
    * This test evaluates the comparison of a smaller timestamp and a greater one, with same dc id
    * and different counts.
    * Call to compareTo should return negative value.
    */
    @Test
    fun negativeCompareToSameDCIdDifferentCount() {
        val dc = DCId("dcid")
        val ts1 = Timestamp(dc, 1)
        val ts2 = Timestamp(dc, 2)

        val cmp = ts1.compareTo(ts2)

        assertTrue(cmp < 0)
    }

    /**
    * This test evaluates the comparison of a smaller timestamp and a greater one, with different dc
    * ids and different counts.
    * Call to compareTo should return negative value.
    */
    @Test
    fun negativeCompareToDifferentDCIdDifferentCount() {
        val dc1 = DCId("dcid1")
        val dc2 = DCId("dcid2")
        val ts1 = Timestamp(dc1, 1)
        val ts2 = Timestamp(dc2, 2)

        val cmp = ts1.compareTo(ts2)

        assertTrue(cmp < 0)
    }

    /**
    * This test evaluates the comparison of a smaller timestamp and a greater one, with different dc
    * ids and same count.
    * Call to compareTo should return negative value.
    */
    @Test
    fun negativeCompareToDifferentDCIdSameCount() {
        val dc1 = DCId("dcid1")
        val dc2 = DCId("dcid2")
        val ts1 = Timestamp(dc1, 1)
        val ts2 = Timestamp(dc2, 1)

        val cmp = ts2.compareTo(ts1)

        assertTrue(cmp > 0)
    }

    /**
    * This test evaluates the comparison of a greater timestamp and a smaller one, with same dc id
    * and different counts.
    * Call to compareTo should return positive value.
    */
    @Test
    fun positiveCompareToSameDCIdDifferentCount() {
        val dc = DCId("dcid")
        val ts1 = Timestamp(dc, 1)
        val ts2 = Timestamp(dc, 2)

        val cmp = ts2.compareTo(ts1)

        assertTrue(cmp > 0)
    }

    /**
    * This test evaluates the comparison of a greater timestamp and a smaller one, with different dc
    * ids and different counts.
    * Call to compareTo should return positive value.
    */
    @Test
    fun positiveCompareToDifferentDCIdDifferentCount() {
        val dc1 = DCId("dcid1")
        val dc2 = DCId("dcid2")
        val ts1 = Timestamp(dc1, 1)
        val ts2 = Timestamp(dc2, 2)

        val cmp = ts2.compareTo(ts1)

        assertTrue(cmp > 0)
    }

    /**
    * This test evaluates the comparison of a greater timestamp and a smaller one, with different dc
    * ids and same count.
    * Call to compareTo should return positive value.
    */
    @Test
    fun positiveCompareToDifferentDCIdSameCount() {
        val dc1 = DCId("dcid1")
        val dc2 = DCId("dcid2")
        val ts1 = Timestamp(dc1, 1)
        val ts2 = Timestamp(dc2, 1)

        val cmp = ts1.compareTo(ts2)

        assertTrue(cmp < 0)
    }

    /**
    * This test evaluates the comparison of two equal timestamps.
    * Call to compareTo should return zero.
    */
    @Test
    fun zeroCompareTo() {
        val dc = DCId("dcid")
        val ts1 = Timestamp(dc, 1)
        val ts2 = Timestamp(dc, 1)

        val cmp = ts1.compareTo(ts2)

        assertEquals(0, cmp)
    }

    /**
    * This test evaluates the comparison (using operators) of a smaller timestamp and a greater one,
    * with same dc id and different counts.
    */
    @Test
    fun negativeCompareToOperatorSameDCIdDifferentCount() {
        val dc = DCId("dcid")
        val ts1 = Timestamp(dc, 1)
        val ts2 = Timestamp(dc, 2)

        assertTrue(ts1 < ts2)
        assertTrue(ts1 <= ts2)
        assertFalse(ts1 > ts2)
        assertFalse(ts1 >= ts2)
        assertFalse(ts1 == ts2)
    }

    /**
    * This test evaluates the comparison (using operators) of a smaller timestamp and a greater one,
    * with different dc ids and different counts.
    */
    @Test
    fun negativeCompareToOperatorDifferentDCIdDifferentCount() {
        val dc1 = DCId("dcid1")
        val dc2 = DCId("dcid2")
        val ts1 = Timestamp(dc1, 1)
        val ts2 = Timestamp(dc2, 2)

        assertTrue(ts1 < ts2)
        assertTrue(ts1 <= ts2)
        assertFalse(ts1 > ts2)
        assertFalse(ts1 >= ts2)
        assertFalse(ts1 == ts2)
    }

    /**
    * This test evaluates the comparison (using operators) of a smaller timestamp and a greater one,
    * with different dc ids and same count.
    */
    @Test
    fun negativeCompareToOperatorDifferentDCIdSameCount() {
        val dc1 = DCId("dcid1")
        val dc2 = DCId("dcid2")
        val ts1 = Timestamp(dc1, 1)
        val ts2 = Timestamp(dc2, 1)

        assertTrue(ts1 < ts2)
        assertTrue(ts1 <= ts2)
        assertFalse(ts1 > ts2)
        assertFalse(ts1 >= ts2)
        assertFalse(ts1 == ts2)
    }

    /**
    * This test evaluates the comparison (using operators) of a greater timestamp and a smaller one,
    * with same dc id and different counts.
    */
    @Test
    fun positiveCompareToOperatorSameDCIdDifferentCount() {
        val dc = DCId("dcid")
        val ts1 = Timestamp(dc, 1)
        val ts2 = Timestamp(dc, 2)

        assertFalse(ts2 < ts1)
        assertFalse(ts2 <= ts1)
        assertTrue(ts2 > ts1)
        assertTrue(ts2 >= ts1)
        assertFalse(ts2 == ts1)
    }

    /**
    * This test evaluates the comparison (using operators) of a greater timestamp and a smaller one,
    * with different dc ids and different counts.
    */
    @Test
    fun positiveCompareToOperatorDifferentDCIdDifferentCount() {
        val dc1 = DCId("dcid1")
        val dc2 = DCId("dcid2")
        val ts1 = Timestamp(dc1, 1)
        val ts2 = Timestamp(dc2, 2)

        assertFalse(ts2 < ts1)
        assertFalse(ts2 <= ts1)
        assertTrue(ts2 > ts1)
        assertTrue(ts2 >= ts1)
        assertFalse(ts2 == ts1)
    }

    /**
    * This test evaluates the comparison (using operators) of a greater timestamp and a smaller one,
    * with different dc ids and same count.
    */
    @Test
    fun positiveCompareToOperatorDifferentDCIdSameCount() {
        val dc1 = DCId("dcid1")
        val dc2 = DCId("dcid2")
        val ts1 = Timestamp(dc1, 1)
        val ts2 = Timestamp(dc2, 1)

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
        val dc = DCId("dcid")
        val ts1 = Timestamp(dc, 1)
        val ts2 = Timestamp(dc, 1)

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
        val ts = Timestamp(DCId("dcid1"), 3)

        val tsJson = ts.toJson()

        assertEquals("""{"id":{"name":"dcid1"},"cnt":3}""", tsJson)
    }

    /**
    * This test evaluates JSON deserialization.
    **/
    @Test
    fun fromJsonDeserialization() {
        val ts = Timestamp(DCId("dcid1"), 3)

        val tsJson = Timestamp.fromJson("""{"id":{"name":"dcid1"},"cnt":3}""")

        assertTrue(ts == tsJson)
    }
}
