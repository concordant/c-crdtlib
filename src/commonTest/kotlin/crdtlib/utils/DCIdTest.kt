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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
* Represents a test suite for DCId.
**/
class DCIdTest {

    /**
    * This test evaluates the comparison of a smaller DCId and a greater one.
    * Call to compareTo should return negative value.
    */
    @Test
    fun negativeCompareTo() {
        val dc1 = DCId("dcid1")
        val dc2 = DCId("dcid2")

        val cmp = dc1.compareTo(dc2)

        assertTrue(cmp < 0)
    }

    /**
    * This test evaluates the comparison of a greater DCId and a smaller one.
    * Call to compareTo should return positive value.
    */
    @Test
    fun positiveCompareTo() {
        val dc1 = DCId("dcid1")
        val dc2 = DCId("dcid2")

        val cmp = dc2.compareTo(dc1)

        assertTrue(cmp > 0)
    }

    /**
    * This test evaluates the comparison of two equal DCIds.
    * Call to compareTo should return 0.
    */
    @Test
    fun zeroCompareTo() {
        val dc1 = DCId("dcid1")
        val dc2 = DCId("dcid1")

        val cmp = dc1.compareTo(dc2)

        assertEquals(0, cmp)
    }

    /**
    * This test evaluates the comparison (using operators) of a smaller DCId and a greater one.
    */
    @Test
    fun negativeCompareToOperator() {
        val dc1 = DCId("dcid1")
        val dc2 = DCId("dcid2")

        assertTrue(dc1 < dc2)
        assertTrue(dc1 <= dc2)
        assertFalse(dc1 > dc2)
        assertFalse(dc1 >= dc2)
        assertFalse(dc1 == dc2)
    }

    /**
    * This test evaluates the comparison (using operators) of a greater DCId and a smaller one.
    */
    @Test
    fun positiveCompareToOperator() {
        val dc1 = DCId("dcid1")
        val dc2 = DCId("dcid2")

        assertFalse(dc2 < dc1)
        assertFalse(dc2 <= dc1)
        assertTrue(dc2 > dc1)
        assertTrue(dc2 >= dc1)
        assertFalse(dc2 == dc1)
    }

    /**
    * This test evaluates the comparison (using operators) of two equal DCIds.
    */
    @Test
    fun zeroCompareToOperator() {
        val dc1 = DCId("dcid1")
        val dc2 = DCId("dcid1")

        assertFalse(dc1 < dc2)
        assertTrue(dc1 <= dc2)
        assertFalse(dc1 > dc2)
        assertTrue(dc1 >= dc2)
        assertTrue(dc1 == dc2)
    }

    /**
    * This test evaluates JSON serialization.
    **/
    @Test
    fun toJsonSerialization() {
        val dc = DCId("dcid1")

        val dcJson = dc.toJson()

        assertEquals("""{"name":"dcid1"}""", dcJson)
    }

    /**
    * This test evaluates JSON deserialization.
    **/
    @Test
    fun fromJsonDeserialization() {
        val dc = DCId("dcid1")

        val dcJson = DCId.fromJson("""{"name":"dcid1"}""")

        assertTrue(dc == dcJson)
    }
}
