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

import crdtlib.crdt.JSMRegister
import crdtlib.utils.VersionVector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
* Represents a suite test for JSMRegister.
**/
class JSMRegisterTest {

    /**
    * This test evaluates the scenario: create string value get.
    * Call to get should return the value set by the constructor.
    */
    @Test
    fun createStringGet() {
        val value = "value"

        val reg = JSMRegister<String>(value)

        assertEquals(value, reg.get())
    }

    /**
    * This test evaluates the scenario: create int value get.
    * Call to get should return the value set by the constructor.
    */
    @Test
    fun createIntGet() {
        val value = 42

        val reg = JSMRegister<Int>(value)

        assertEquals(value, reg.get())
    }

    /**
    * This test evaluates the scenario: create assign (with greater value) get.
    * Call to get should return the value set by assign.
    */
    @Test
    fun createAssignGreaterGet() {
        val val1 = 42
        val val2 = 100

        val reg = JSMRegister<Int>(val1)
        reg.assign(val2)

        assertEquals(val2, reg.get())
    }

    /**
    * This test evaluates the scenario: create assign (with lower value) get.
    * Call to get should return the value set by the constructor.
    */
    @Test
    fun createAssignLowerGet() {
        val val1 = 42
        val val2 = 3

        val reg = JSMRegister<Int>(val1)
        reg.assign(val2)

        assertEquals(val1, reg.get())
    }

    /**
    * This test evaluates the scenario: create || create (with greater value) merge get.
    * Call to get should return the value set by the second replica.
    */
    @Test
    fun create_createGreaterMergeGet() {
        val val1 = 42
        val val2 = 101

        val reg1 = JSMRegister<Int>(val1)
        val reg2 = JSMRegister<Int>(val2)
        reg2.merge(reg1)

        assertEquals(val2, reg2.get())
    }

    /**
    * This test evaluates the scenario: create || create (with lower value) merge get.
    * Call to get should return the value set by the first replica.
    */
    @Test
    fun create_createLowerMergeGet() {
        val val1 = 42
        val val2 = 41

        val reg1 = JSMRegister<Int>(val1)
        val reg2 = JSMRegister<Int>(val2)
        reg2.merge(reg1)

        assertEquals(val1, reg2.get())
    }

    /**
    * This test evaluates the scenario: create (with lower value) assign (with the greatest value)
    * || create (with lower value) merge get.
    * Call to get should return the value set by assign in the first replica.
    */
    fun createAssignGreatest_createMergeGet() {
        val val1 = "BBB"
        val val2 = "CCC"
        val val3 = "AAA"

        val reg1 = JSMRegister<String>(val1)
        reg1.assign(val2)
        val reg2 = JSMRegister<String>(val3)
        reg2.merge(reg1)

        assertEquals(val2, reg2.get())
    }

    /**
    * This test evaluates the scenario: create (with lower value) assign (with a greater value) ||
    * create (with the greatest value) merge get.
    * Call to get should return the value set in the second replica.
    */
    fun createAssign_createGreatestMergeGet() {
        val val1 = "AAA"
        val val2 = "BBB"
        val val3 = "CCC"

        val reg1 = JSMRegister<String>(val1)
        reg1.assign(val2)
        val reg2 = JSMRegister<String>(val3)
        reg2.merge(reg1)

        assertEquals(val3, reg2.get())
    }

    /**
    * This test evaluates the scenario: create (with the greatest value) assign (with lower value)
    * || create (with lower value) merge get.
    * Call to get should return the value set at initialization in the first replica.
    */
    fun createGreatestAssign_createMergeGet() {
        val val1 = "CCC"
        val val2 = "AAA"
        val val3 = "BBB"

        val reg1 = JSMRegister<String>(val1)
        reg1.assign(val2)
        val reg2 = JSMRegister<String>(val3)
        reg2.merge(reg1)

        assertEquals(val1, reg2.get())
    }

    /**
    * This test evaluates the scenario: create (with lower value) merge (before assign in replica 2)
    * || create (with the greatest value) assign (with lower value) merge get.
    * Call to get should return the value set at initialization in the second replica.
    */
    fun createMergeBeforeAssign_createGreatestAssignMerge() {
        val val1 = 4
        val val2 = 5
        val val3 = 2

        val reg1 = JSMRegister<Int>(val1)
        val reg2 = JSMRegister<Int>(val2)
        reg1.merge(reg2)
        reg2.assign(val3)
        reg2.merge(reg1)

        assertEquals(val2, reg2.get())
    }

    /**
    * This test evaluates the use of delta return by call to assign method.
    * Call to get should return value set at initialization in the first replica.
    */
    @Test
    fun assignOp() {
        val val1 = 8
        val val2 = 6
        val val3 = 5

        val reg1 = JSMRegister<Int>(val1)
        val assignOp = reg1.assign(val2)
        val reg2 = JSMRegister<Int>(val3)
        reg2.merge(assignOp)

        assertEquals(val1, reg2.get())
    }

    /*
    * This test evaluates the generation of delta plus its merging into another replica.
    * Call to get should return the value set at initialization in the first replica.
    */
    @Test
    fun generateDelta() {
        val vv = VersionVector()
        val val1 = 8
        val val2 = 6
        val val3 = 5

        val reg1 = JSMRegister<Int>(val1)
        reg1.assign(val2)
        val reg2 = JSMRegister<Int>(val3)
        val delta = reg1.generateDelta(vv)
        reg2.merge(delta)

        assertEquals(val1, reg2.get())
    }

    /**
    * This test evaluates JSON serialization of a JSM register.
    **/
    @Test
    fun toJsonSerialization() {
        val value = "VALUE"

        val reg = JSMRegister<String>(value)

        assertEquals("""{"_type":"JSMRegister","value":"VALUE"}""", reg.toJson())
    }

    /**
    * This test evaluates JSON deserialization of a JSM register.
    **/
    @Test
    fun fromJsonDeserialization() {
        val regJson = JSMRegister.fromJson<String>("""{"_type":"JSMRegister","value":"VALUE"}""")

        assertEquals("VALUE", regJson.get())
    }
}
