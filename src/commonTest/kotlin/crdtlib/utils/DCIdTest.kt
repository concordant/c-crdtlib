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

/**
* Represents a test suite for DCUId.
**/
class DCUIdTest : StringSpec({

    /**
    * This test evaluates the comparison of a smaller DCUId and a greater one.
    * Call to compareTo should return negative value.
    */
    "negative compareTo" {
        val dc1 = DCUId("dcid1")
        val dc2 = DCUId("dcid2")

        val cmp = dc1.compareTo(dc2)

        cmp.shouldBeLessThan(0)
    }

    /**
    * This test evaluates the comparison of a greater DCUId and a smaller one.
    * Call to compareTo should return positive value.
    */
    "positive compareTo" {
        val dc1 = DCUId("dcid1")
        val dc2 = DCUId("dcid2")

        val cmp = dc2.compareTo(dc1)

        cmp.shouldBeGreaterThan(0)
    }

    /**
    * This test evaluates the comparison of two equal DCUIds.
    * Call to compareTo should return 0.
    */
    "zero compareTo" {
        val dc1 = DCUId("dcid1")
        val dc2 = DCUId("dcid1")

        val cmp = dc1.compareTo(dc2)

        cmp.shouldBe(0)
    }

    /**
    * This test evaluates the comparison (using operators) of a smaller DCUId and a greater one.
    */
    "negative compareTo using operators" {
        val dc1 = DCUId("dcid1")
        val dc2 = DCUId("dcid2")

        (dc1 < dc2).shouldBeTrue()
        (dc1 <= dc2).shouldBeTrue()
        (dc1 > dc2).shouldBeFalse()
        (dc1 >= dc2).shouldBeFalse()
        (dc1 == dc2).shouldBeFalse()
    }

    /**
    * This test evaluates the comparison (using operators) of a greater DCUId and a smaller one.
    */
    "positive compareTo using operators" {
        val dc1 = DCUId("dcid1")
        val dc2 = DCUId("dcid2")

        (dc2 < dc1).shouldBeFalse()
        (dc2 <= dc1).shouldBeFalse()
        (dc2 > dc1).shouldBeTrue()
        (dc2 >= dc1).shouldBeTrue()
        (dc2 == dc1).shouldBeFalse()
    }

    /**
    * This test evaluates the comparison (using operators) of two equal DCUIds.
    */
    "zero compareTo using operators" {
        val dc1 = DCUId("dcid1")
        val dc2 = DCUId("dcid1")

        (dc1 < dc2).shouldBeFalse()
        (dc1 <= dc2).shouldBeTrue()
        (dc1 > dc2).shouldBeFalse()
        (dc1 >= dc2).shouldBeTrue()
        (dc1 == dc2).shouldBeTrue()
    }

    /**
    * This test evaluates JSON serialization.
    **/
    "JSON serialization" {
        val dc = DCUId("dcid1")

        val dcJson = dc.toJson()

        dcJson.shouldBe("""{"name":"dcid1"}""")
    }

    /**
    * This test evaluates JSON deserialization.
    **/
    "JSON deserialization" {
        val dc = DCUId("dcid1")

        val dcJson = DCUId.fromJson("""{"name":"dcid1"}""")

        dcJson.shouldBe(dc)
    }
})
