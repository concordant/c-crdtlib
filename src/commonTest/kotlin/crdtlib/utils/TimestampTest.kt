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
* Represents a test suite for Timestamp.
**/
class TimestampTest : StringSpec({

    /**
    * This test evaluates the comparison of a smaller timestamp and a greater one, with same
    * client unique id and different counts.
    * Call to compareTo should return negative value.
    */
    "negative compareTo same ClientUId and different count" {
        val uid = ClientUId("clientid")
        val ts1 = Timestamp(uid, 1)
        val ts2 = Timestamp(uid, 2)

        val cmp = ts1.compareTo(ts2)

        cmp.shouldBeLessThan(0)
    }

    /**
    * This test evaluates the comparison of a smaller timestamp and a greater one, with different
    * client unique ids and different counts.
    * Call to compareTo should return negative value.
    */
    "negative compareTo different ClientUId and different count" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid2, 2)

        val cmp = ts1.compareTo(ts2)

        cmp.shouldBeLessThan(0)
    }

    /**
    * This test evaluates the comparison of a smaller timestamp and a greater one, with different
    * client unique ids and same count.
    * Call to compareTo should return negative value.
    */
    "negative compareTo different ClientUId and same count" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid2, 1)

        val cmp = ts1.compareTo(ts2)

        cmp.shouldBeLessThan(0)
    }

    /**
    * This test evaluates the comparison of a greater timestamp and a smaller one, with same
    * client unique id and different counts.
    * Call to compareTo should return positive value.
    */
    "positive compareTo same ClientUId and different count" {
        val uid = ClientUId("clientid")
        val ts1 = Timestamp(uid, 1)
        val ts2 = Timestamp(uid, 2)

        val cmp = ts2.compareTo(ts1)

        cmp.shouldBeGreaterThan(0)
    }

    /**
    * This test evaluates the comparison of a greater timestamp and a smaller one, with different
    * client unique ids and different counts.
    * Call to compareTo should return positive value.
    */
    "positive compareTo different ClientUId and different count" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid2, 2)

        val cmp = ts2.compareTo(ts1)

        cmp.shouldBeGreaterThan(0)
    }

    /**
    * This test evaluates the comparison of a greater timestamp and a smaller one, with different
    * client unique ids and same count.
    * Call to compareTo should return positive value.
    */
    "positive compareTo different ClientUId and same count" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid2, 1)

        val cmp = ts2.compareTo(ts1)

        cmp.shouldBeGreaterThan(0)
    }

    /**
    * This test evaluates the comparison of two equal timestamps.
    * Call to compareTo should return zero.
    */
    "zero compareTo" {
        val uid = ClientUId("clientid")
        val ts1 = Timestamp(uid, 1)
        val ts2 = Timestamp(uid, 1)

        val cmp = ts1.compareTo(ts2)

        cmp.shouldBe(0)
    }

    /**
    * This test evaluates the comparison (using operators) of a smaller timestamp and a greater one,
    * with same client unique id and different counts.
    */
    "negative compareTo with operators same ClientUId and different count" {
        val uid = ClientUId("clientid")
        val ts1 = Timestamp(uid, 1)
        val ts2 = Timestamp(uid, 2)

        (ts1 < ts2).shouldBeTrue()
        (ts1 <= ts2).shouldBeTrue()
        (ts1 > ts2).shouldBeFalse()
        (ts1 >= ts2).shouldBeFalse()
        (ts1 == ts2).shouldBeFalse()
    }

    /**
    * This test evaluates the comparison (using operators) of a smaller timestamp and a greater one,
    * with different client unique ids and different counts.
    */
    "negative compareTo with operators different ClientUId and different count" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid2, 2)

        (ts1 < ts2).shouldBeTrue()
        (ts1 <= ts2).shouldBeTrue()
        (ts1 > ts2).shouldBeFalse()
        (ts1 >= ts2).shouldBeFalse()
        (ts1 == ts2).shouldBeFalse()
    }

    /**
    * This test evaluates the comparison (using operators) of a smaller timestamp and a greater one,
    * with different client unique ids and same count.
    */
    "negative compareTo with operators different ClientUId and same count" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid2, 1)

        (ts1 < ts2).shouldBeTrue()
        (ts1 <= ts2).shouldBeTrue()
        (ts1 > ts2).shouldBeFalse()
        (ts1 >= ts2).shouldBeFalse()
        (ts1 == ts2).shouldBeFalse()
    }

    /**
    * This test evaluates the comparison (using operators) of a greater timestamp and a smaller one,
    * with same client unique id and different counts.
    */
    "positive compareTo with operators same ClientUId and different count" {
        val uid = ClientUId("clientid")
        val ts1 = Timestamp(uid, 1)
        val ts2 = Timestamp(uid, 2)

        (ts2 < ts1).shouldBeFalse()
        (ts2 <= ts1).shouldBeFalse()
        (ts2 > ts1).shouldBeTrue()
        (ts2 >= ts1).shouldBeTrue()
        (ts2 == ts1).shouldBeFalse()
    }

    /**
    * This test evaluates the comparison (using operators) of a greater timestamp and a smaller one,
    * with different client unique  ids and different counts.
    */
    "positive compareTo with operators different ClientUId and different count" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid2, 2)

        (ts2 < ts1).shouldBeFalse()
        (ts2 <= ts1).shouldBeFalse()
        (ts2 > ts1).shouldBeTrue()
        (ts2 >= ts1).shouldBeTrue()
        (ts2 == ts1).shouldBeFalse()
    }

    /**
    * This test evaluates the comparison (using operators) of a greater timestamp and a smaller one,
    * with different client unique ids and same count.
    */
    "positive compareTo with operators different ClientUId and same count" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val ts1 = Timestamp(uid1, 1)
        val ts2 = Timestamp(uid2, 1)

        (ts2 < ts1).shouldBeFalse()
        (ts2 <= ts1).shouldBeFalse()
        (ts2 > ts1).shouldBeTrue()
        (ts2 >= ts1).shouldBeTrue()
        (ts2 == ts1).shouldBeFalse()
    }

    /**
    * This test evaluates the comparison (using operators) of two equal timestamps.
    */
    "zero compareTo with operators" {
        val uid = ClientUId("clientid")
        val ts1 = Timestamp(uid, 1)
        val ts2 = Timestamp(uid, 1)

        (ts1 < ts2).shouldBeFalse()
        (ts1 <= ts2).shouldBeTrue()
        (ts1 > ts2).shouldBeFalse()
        (ts1 >= ts2).shouldBeTrue()
        (ts1 == ts2).shouldBeTrue()
    }

    /**
    * This test evaluates JSON serialization.
    **/
    "JSON serialization" {
        val ts = Timestamp(ClientUId("clientid1"), 3)

        val tsJson = ts.toJson()

        tsJson.shouldBe("""{"uid":{"name":"clientid1"},"cnt":3}""")
    }

    /**
    * This test evaluates JSON deserialization.
    **/
    "JSON deserialization" {
        val ts = Timestamp(ClientUId("clientid1"), 3)

        val tsJson = Timestamp.fromJson("""{"uid":{"name":"clientid1"},"cnt":3}""")

        tsJson.shouldBe(ts)
    }
})
