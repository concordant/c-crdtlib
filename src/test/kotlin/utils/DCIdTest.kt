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
}
