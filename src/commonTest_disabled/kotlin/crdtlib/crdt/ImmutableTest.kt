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
