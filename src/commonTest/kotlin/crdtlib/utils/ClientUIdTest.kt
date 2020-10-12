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
* Represents a test suite for ClientUId.
**/
class ClientUIdTest : StringSpec({

    /**
    * This test evaluates the comparison of a smaller ClientUId and a greater one.
    * Call to compareTo should return negative value.
    */
    "negative compareTo" {
        val client1 = ClientUId("clientid1")
        val client2 = ClientUId("clientid2")

        val cmp = client1.compareTo(client2)

        cmp.shouldBeLessThan(0)
    }

    /**
    * This test evaluates the comparison of a greater ClientUId and a smaller one.
    * Call to compareTo should return positive value.
    */
    "positive compareTo" {
        val client1 = ClientUId("clientid1")
        val client2 = ClientUId("clientid2")

        val cmp = client2.compareTo(client1)

        cmp.shouldBeGreaterThan(0)
    }

    /**
    * This test evaluates the comparison of two equal ClientUIds.
    * Call to compareTo should return 0.
    */
    "zero compareTo" {
        val client1 = ClientUId("clientid1")
        val client2 = ClientUId("clientid1")

        val cmp = client1.compareTo(client2)

        cmp.shouldBe(0)
    }

    /**
    * This test evaluates the comparison (using operators) of a smaller ClientUId and a greater one.
    */
    "negative compareTo using operators" {
        val client1 = ClientUId("clientid1")
        val client2 = ClientUId("clientid2")

        (client1 < client2).shouldBeTrue()
        (client1 <= client2).shouldBeTrue()
        (client1 > client2).shouldBeFalse()
        (client1 >= client2).shouldBeFalse()
        (client1 == client2).shouldBeFalse()
    }

    /**
    * This test evaluates the comparison (using operators) of a greater ClientUId and a smaller one.
    */
    "positive compareTo using operators" {
        val client1 = ClientUId("clientid1")
        val client2 = ClientUId("clientid2")

        (client2 < client1).shouldBeFalse()
        (client2 <= client1).shouldBeFalse()
        (client2 > client1).shouldBeTrue()
        (client2 >= client1).shouldBeTrue()
        (client2 == client1).shouldBeFalse()
    }

    /**
    * This test evaluates the comparison (using operators) of two equal ClientUIds.
    */
    "zero compareTo using operators" {
        val client1 = ClientUId("clientid1")
        val client2 = ClientUId("clientid1")

        (client1 < client2).shouldBeFalse()
        (client1 <= client2).shouldBeTrue()
        (client1 > client2).shouldBeFalse()
        (client1 >= client2).shouldBeTrue()
        (client1 == client2).shouldBeTrue()
    }

    /**
    * This test evaluates JSON serialization.
    **/
    "JSON serialization" {
        val client = ClientUId("clientid1")

        val clientJson = client.toJson()

        clientJson.shouldBe("""{"name":"clientid1"}""")
    }

    /**
    * This test evaluates JSON deserialization.
    **/
    "JSON deserialization" {
        val client = ClientUId("clientid1")

        val clientJson = ClientUId.fromJson("""{"name":"clientid1"}""")

        clientJson.shouldBe(client)
    }
})
