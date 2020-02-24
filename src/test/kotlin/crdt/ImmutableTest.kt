package crdtlib.test

import crdtlib.crdt.Immutable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
* Represents a suite test for Immutable.
**/
class ImmutableTest {

    /**
    * This test evaluates the scenario: create string value get.
    * Call to value should return the value set by the constructor.
    */
    @Test
    fun createStringGet() {
        val value = "value"

        val imm = Immutable<String>(value)

        assertEquals(value, imm.get())
    }

    /**
    * This test evaluates the scenario: create int value get.
    * Call to value should return the value set by the constructor.
    */
    @Test
    fun createIntGet() {
        val value = 42

        val imm = Immutable<Int>(value)

        assertEquals(value, imm.get())
    }
}
