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

package crdtlib.crdt

import crdtlib.utils.ClientUId
import crdtlib.utils.SimpleEnvironment
import crdtlib.utils.VersionVector
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.iterator.shouldBeEmpty
import io.kotest.matchers.iterator.shouldHaveNext
import io.kotest.matchers.nulls.*

/**
 * Represents a suite test for MVMap.
 */
class MVMapTest : StringSpec({

    val uid1 = ClientUId("clientid1")
    val uid2 = ClientUId("clientid2")
    val uid3 = ClientUId("clientid3")
    var client1 = SimpleEnvironment(uid1)
    var client2 = SimpleEnvironment(uid2)
    var client3 = SimpleEnvironment(uid3)

    val valBoolean1 = true
    val valBoolean2 = false
    val valDouble1 = 12.3456789
    val valDouble2 = 3.14159
    val valInt1 = 42
    val valInt2 = -100
    val valString1 = "value1"
    val valString2 = "value2"

    val key1 = "key1"
    val key2 = "key2"
    val key3 = "key3"
    val key4 = "key4"
    val key5 = "key5"

    beforeTest {
        client1 = SimpleEnvironment(uid1)
        client2 = SimpleEnvironment(uid2)
        client3 = SimpleEnvironment(uid3)
    }

    /**
     * This test evaluates the scenario: get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "create and get/iterator" {
        val map = MVMap()

        map.getBoolean(key1).shouldBeNull()
        map.getDouble(key1).shouldBeNull()
        map.getInt(key1).shouldBeNull()
        map.getString(key1).shouldBeNull()

        map.iteratorBoolean().shouldBeEmpty()
        map.iteratorDouble().shouldBeEmpty()
        map.iteratorInt().shouldBeEmpty()
        map.iteratorString().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put get/iterator.
     * Call to get should return the value set by the put.
     * Call to iterator should return an iterator containing the value set by the put.
     */
    "put and get/iterator" {
        val map = MVMap(client1)

        map.put(key1, valBoolean1)
        map.put(key1, valDouble1)
        map.put(key1, valInt1)
        map.put(key1, valString1)

        map.getBoolean(key1)!!.shouldHaveSingleElement(valBoolean1)
        map.getDouble(key1)!!.shouldHaveSingleElement(valDouble1)
        map.getInt(key1)!!.shouldHaveSingleElement(valInt1)
        map.getString(key1)!!.shouldHaveSingleElement(valString1)

        val iteratorBoolean = map.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, setOf(valBoolean1)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, setOf(valDouble1)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, setOf(valInt1)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, setOf(valString1)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "put, delete, get/iterator" {
        val map = MVMap(client1)

        map.put(key1, valBoolean1)
        map.put(key1, valDouble1)
        map.put(key1, valInt1)
        map.put(key1, valString1)
        map.deleteBoolean(key1)
        map.deleteDouble(key1)
        map.deleteInt(key1)
        map.deleteString(key1)

        map.getBoolean(key1).shouldBeNull()
        map.getDouble(key1).shouldBeNull()
        map.getInt(key1).shouldBeNull()
        map.getString(key1).shouldBeNull()

        map.iteratorBoolean().shouldBeEmpty()
        map.iteratorDouble().shouldBeEmpty()
        map.iteratorInt().shouldBeEmpty()
        map.iteratorString().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: del get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "delete and get/iterator" {
        val map = MVMap(client1)

        map.deleteBoolean(key1)
        map.deleteDouble(key1)
        map.deleteInt(key1)
        map.deleteString(key1)

        map.getBoolean(key1).shouldBeNull()
        map.getDouble(key1).shouldBeNull()
        map.getInt(key1).shouldBeNull()
        map.getString(key1).shouldBeNull()

        map.iteratorBoolean().shouldBeEmpty()
        map.iteratorDouble().shouldBeEmpty()
        map.iteratorInt().shouldBeEmpty()
        map.iteratorString().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put put get/iterator.
     * Call to get should return the value set by the second put.
     * Call to iterator should return an iterator containing the value set by the second put.
     */
    "put, put, get/iterator" {
        val map = MVMap(client1)

        map.put(key1, valBoolean1)
        map.put(key1, valDouble1)
        map.put(key1, valInt1)
        map.put(key1, valString1)
        map.put(key1, valBoolean2)
        map.put(key1, valDouble2)
        map.put(key1, valInt2)
        map.put(key1, valString2)

        map.getBoolean(key1)!!.shouldHaveSingleElement(valBoolean2)
        map.getDouble(key1)!!.shouldHaveSingleElement(valDouble2)
        map.getInt(key1)!!.shouldHaveSingleElement(valInt2)
        map.getString(key1)!!.shouldHaveSingleElement(valString2)

        val iteratorBoolean = map.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, setOf(valBoolean2)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, setOf(valDouble2)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, setOf(valInt2)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, setOf(valString2)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put put del get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "put, put, del, get/iterator" {
        val map = MVMap(client1)

        map.put(key1, valBoolean1)
        map.put(key1, valDouble1)
        map.put(key1, valInt1)
        map.put(key1, valString1)
        map.put(key1, valBoolean2)
        map.put(key1, valDouble2)
        map.put(key1, valInt2)
        map.put(key1, valString2)
        map.deleteBoolean(key1)
        map.deleteDouble(key1)
        map.deleteInt(key1)
        map.deleteString(key1)

        map.getBoolean(key1).shouldBeNull()
        map.getDouble(key1).shouldBeNull()
        map.getInt(key1).shouldBeNull()
        map.getString(key1).shouldBeNull()

        map.iteratorBoolean().shouldBeEmpty()
        map.iteratorDouble().shouldBeEmpty()
        map.iteratorInt().shouldBeEmpty()
        map.iteratorString().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || merge get/iterator.
     * Call to get should return the value set by the put registered in the first replica.
     * Call to iterator should return an iterator containing the value set by the put registered in the first replica.
     */
    "R1: put; R2: merge and get/iterator" {
        val map1 = MVMap(client1)
        val map2 = MVMap(client1)

        map1.put(key1, valBoolean1)
        map1.put(key1, valDouble1)
        map1.put(key1, valInt1)
        map1.put(key1, valString1)
        map1.merge(map2)
        map2.merge(map1)

        map1.getBoolean(key1)!!.shouldHaveSingleElement(valBoolean1)
        map1.getDouble(key1)!!.shouldHaveSingleElement(valDouble1)
        map1.getInt(key1)!!.shouldHaveSingleElement(valInt1)
        map1.getString(key1)!!.shouldHaveSingleElement(valString1)
        map2.getBoolean(key1)!!.shouldHaveSingleElement(valBoolean1)
        map2.getDouble(key1)!!.shouldHaveSingleElement(valDouble1)
        map2.getInt(key1)!!.shouldHaveSingleElement(valInt1)
        map2.getString(key1)!!.shouldHaveSingleElement(valString1)

        val iteratorBoolean1 = map1.iteratorBoolean()
        iteratorBoolean1.shouldHaveNext()
        iteratorBoolean1.next().shouldBe(Pair(key1, setOf(valBoolean1)))
        iteratorBoolean1.shouldBeEmpty()

        val iteratorDouble1 = map1.iteratorDouble()
        iteratorDouble1.shouldHaveNext()
        iteratorDouble1.next().shouldBe(Pair(key1, setOf(valDouble1)))
        iteratorDouble1.shouldBeEmpty()

        val iteratorInt1 = map1.iteratorInt()
        iteratorInt1.shouldHaveNext()
        iteratorInt1.next().shouldBe(Pair(key1, setOf(valInt1)))
        iteratorInt1.shouldBeEmpty()

        val iteratorString1 = map1.iteratorString()
        iteratorString1.shouldHaveNext()
        iteratorString1.next().shouldBe(Pair(key1, setOf(valString1)))
        iteratorString1.shouldBeEmpty()

        val iteratorBoolean2 = map2.iteratorBoolean()
        iteratorBoolean2.shouldHaveNext()
        iteratorBoolean2.next().shouldBe(Pair(key1, setOf(valBoolean1)))
        iteratorBoolean2.shouldBeEmpty()

        val iteratorDouble2 = map2.iteratorDouble()
        iteratorDouble2.shouldHaveNext()
        iteratorDouble2.next().shouldBe(Pair(key1, setOf(valDouble1)))
        iteratorDouble2.shouldBeEmpty()

        val iteratorInt2 = map2.iteratorInt()
        iteratorInt2.shouldHaveNext()
        iteratorInt2.next().shouldBe(Pair(key1, setOf(valInt1)))
        iteratorInt2.shouldBeEmpty()

        val iteratorString2 = map2.iteratorString()
        iteratorString2.shouldHaveNext()
        iteratorString2.next().shouldBe(Pair(key1, setOf(valString1)))
        iteratorString2.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || merge put get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by the put registered in the second replica.
     */
    "R1: put; R2: merge, put, get/iterator" {
        val map1 = MVMap(client1)
        val map2 = MVMap(client2)

        map1.put(key1, valBoolean1)
        map1.put(key1, valDouble1)
        map1.put(key1, valInt1)
        map1.put(key1, valString1)
        map2.merge(map1)
        map2.put(key1, valBoolean2)
        map2.put(key1, valDouble2)
        map2.put(key1, valInt2)
        map2.put(key1, valString2)

        map2.getBoolean(key1)!!.shouldHaveSingleElement(valBoolean2)
        map2.getDouble(key1)!!.shouldHaveSingleElement(valDouble2)
        map2.getInt(key1)!!.shouldHaveSingleElement(valInt2)
        map2.getString(key1)!!.shouldHaveSingleElement(valString2)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, setOf(valBoolean2)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, setOf(valDouble2)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, setOf(valInt2)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, setOf(valString2)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || put merge get
     * Call to get should return a set containing the two concurrently put values.
     * Call to iterator should return an iterator containing the two concurrently put values.
     */
    "R1: put; R2: put, merge, get/iterator" {
        val map1 = MVMap(client1)
        val map2 = MVMap(client2)

        map1.put(key1, valBoolean1)
        map1.put(key1, valDouble1)
        map1.put(key1, valInt1)
        map1.put(key1, valString1)
        map2.put(key1, valBoolean2)
        map2.put(key1, valDouble2)
        map2.put(key1, valInt2)
        map2.put(key1, valString2)
        map2.merge(map1)

        map2.getBoolean(key1)!!.shouldContainExactlyInAnyOrder(valBoolean1, valBoolean2)
        map2.getDouble(key1)!!.shouldContainExactlyInAnyOrder(valDouble1, valDouble2)
        map2.getInt(key1)!!.shouldContainExactlyInAnyOrder(valInt1, valInt2)
        map2.getString(key1)!!.shouldContainExactlyInAnyOrder(valString1, valString2)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, setOf(valBoolean1, valBoolean2)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, setOf(valDouble1, valDouble2)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, setOf(valInt1, valInt2)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, setOf(valString1, valString2)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del || put(with older timestamp) merge get/iterator.
     * Call to get should return a set containing the value set in the second replica and null.
     * Call to iterator should return an iterator containing the value set in the second replica.
     */
    "R1: put, delete; R2: put with older timestamp, merge, get/iterator" {
        val map1 = MVMap(client1)
        val map2 = MVMap(client2)

        map2.put(key1, valBoolean2)
        map2.put(key1, valDouble2)
        map2.put(key1, valInt2)
        map2.put(key1, valString2)
        map1.put(key1, valBoolean1)
        map1.put(key1, valDouble1)
        map1.put(key1, valInt1)
        map1.put(key1, valString1)
        map1.deleteBoolean(key1)
        map1.deleteDouble(key1)
        map1.deleteInt(key1)
        map1.deleteString(key1)
        map2.merge(map1)

        map2.getBoolean(key1)!!.shouldContainExactlyInAnyOrder(valBoolean2, null)
        map2.getDouble(key1)!!.shouldContainExactlyInAnyOrder(valDouble2, null)
        map2.getInt(key1)!!.shouldContainExactlyInAnyOrder(valInt2, null)
        map2.getString(key1)!!.shouldContainExactlyInAnyOrder(valString2, null)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, setOf(valBoolean2, null)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, setOf(valDouble2, null)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, setOf(valInt2, null)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, setOf(valString2, null)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del || put(with older timestamp) merge(before del)
     * merge(after del) get/iterator.
     * Call to get should return a set containing the value set in the second replica and null.
     * Call to iterator should return an iterator containing the value set in the second replica.
     */
    "R1: put, delete; R2: put with older timestamp, merge before delete, merge after delete, get/iterator" {
        val map1 = MVMap(client1)
        val map2 = MVMap(client2)

        map2.put(key1, valBoolean2)
        map2.put(key1, valDouble2)
        map2.put(key1, valInt2)
        map2.put(key1, valString2)
        map1.put(key1, valBoolean1)
        map1.put(key1, valDouble1)
        map1.put(key1, valInt1)
        map1.put(key1, valString1)
        map2.merge(map1)
        map1.deleteBoolean(key1)
        map1.deleteDouble(key1)
        map1.deleteInt(key1)
        map1.deleteString(key1)
        map2.merge(map1)

        map2.getBoolean(key1)!!.shouldContainExactlyInAnyOrder(valBoolean2, null)
        map2.getDouble(key1)!!.shouldContainExactlyInAnyOrder(valDouble2, null)
        map2.getInt(key1)!!.shouldContainExactlyInAnyOrder(valInt2, null)
        map2.getString(key1)!!.shouldContainExactlyInAnyOrder(valString2, null)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, setOf(valBoolean2, null)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, setOf(valDouble2, null)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, setOf(valInt2, null)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, setOf(valString2, null)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del || put(with newer timestamp) merge get/iterator.
     * Call to get should return the value set by put registered in the second replica and null.
     * Call to iterator should return an iterator containing the value set by put registered in the second replica.
     */
    "R1: put, delete; R2: put with newer timestamp, merge, get/iterator" {
        val map1 = MVMap(client1)
        val map2 = MVMap(client2)

        map1.put(key1, valBoolean1)
        map1.put(key1, valDouble1)
        map1.put(key1, valInt1)
        map1.put(key1, valString1)
        map1.deleteBoolean(key1)
        map1.deleteDouble(key1)
        map1.deleteInt(key1)
        map1.deleteString(key1)
        client2.tick()
        client2.tick()
        client2.tick()
        client2.tick()
        map2.put(key1, valBoolean2)
        map2.put(key1, valDouble2)
        map2.put(key1, valInt2)
        map2.put(key1, valString2)
        map2.merge(map1)

        map2.getBoolean(key1)!!.shouldContainExactlyInAnyOrder(valBoolean2, null)
        map2.getDouble(key1)!!.shouldContainExactlyInAnyOrder(valDouble2, null)
        map2.getInt(key1)!!.shouldContainExactlyInAnyOrder(valInt2, null)
        map2.getString(key1)!!.shouldContainExactlyInAnyOrder(valString2, null)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, setOf(valBoolean2, null)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, setOf(valDouble2, null)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, setOf(valInt2, null)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, setOf(valString2, null)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del || put(with newer timestamp) merge(before del)
     * merge(after del) get/iterator.
     * Call to get should return the value set by put registered in the second replica and null.
     * Call to iterator should return an iterator containing the value set by put registered in the second replica.
     */
    "R1: put, delete; R2: put with newer timestamp, merge before delete, merge after delete, get/iterator" {
        val map1 = MVMap(client1)
        val map2 = MVMap(client2)

        map1.put(key1, valBoolean1)
        map1.put(key1, valDouble1)
        map1.put(key1, valInt1)
        map1.put(key1, valString1)
        map2.put(key1, valBoolean2)
        map2.put(key1, valDouble2)
        map2.put(key1, valInt2)
        map2.put(key1, valString2)
        map2.merge(map1)
        client2.tick()
        client2.tick()
        client2.tick()
        client2.tick()
        map1.deleteBoolean(key1)
        map1.deleteDouble(key1)
        map1.deleteInt(key1)
        map1.deleteString(key1)
        map2.merge(map1)

        map2.getBoolean(key1)!!.shouldContainExactlyInAnyOrder(valBoolean2, null)
        map2.getDouble(key1)!!.shouldContainExactlyInAnyOrder(valDouble2, null)
        map2.getInt(key1)!!.shouldContainExactlyInAnyOrder(valInt2, null)
        map2.getString(key1)!!.shouldContainExactlyInAnyOrder(valString2, null)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, setOf(valBoolean2, null)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, setOf(valDouble2, null)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, setOf(valInt2, null)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, setOf(valString2, null)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || put || merge1 del merge2 get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by put registered in the second replica.
     */
    "R1: put; R2: put; R3: merge R1, delete, merge R2, get/iterator" {
        val map1 = MVMap(client1)
        val map2 = MVMap(client2)
        val map3 = MVMap(client3)

        map1.put(key1, valBoolean1)
        map1.put(key1, valDouble1)
        map1.put(key1, valInt1)
        map1.put(key1, valString1)
        map3.merge(map1)
        map2.put(key1, valBoolean2)
        map2.put(key1, valDouble2)
        map2.put(key1, valInt2)
        map2.put(key1, valString2)
        map3.deleteBoolean(key1)
        map3.deleteDouble(key1)
        map3.deleteInt(key1)
        map3.deleteString(key1)
        map3.merge(map2)

        map3.getDouble(key1)!!.shouldContainExactlyInAnyOrder(valDouble2, null)
        map3.getBoolean(key1)!!.shouldContainExactlyInAnyOrder(valBoolean2, null)
        map3.getInt(key1)!!.shouldContainExactlyInAnyOrder(valInt2, null)
        map3.getString(key1)!!.shouldContainExactlyInAnyOrder(valString2, null)

        val iteratorBoolean = map3.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, setOf(valBoolean2, null)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map3.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, setOf(valDouble2, null)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map3.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, setOf(valInt2, null)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map3.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, setOf(valString2, null)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the use of deltas return by call to put method.
     * Call to get should return the value set by put registered in the first replica.
     * Call to iterator should return an iterator containing the value set by put registered in the first replica.
     */
    "use deltas returned by put" {
        val map1 = MVMap(client1)
        val map2 = MVMap(client1)

        val returnedOpBoolean = map1.put(key1, valBoolean1)
        val opBoolean = client1.popWrite().second
        returnedOpBoolean.shouldBe(opBoolean)
        val returnedOpDouble = map1.put(key1, valDouble1)
        val opDouble = client1.popWrite().second
        returnedOpDouble.shouldBe(opDouble)
        val returnedOpInt = map1.put(key1, valInt1)
        val opInt = client1.popWrite().second
        returnedOpInt.shouldBe(opInt)
        val returnedOpString = map1.put(key1, valString1)
        val opString = client1.popWrite().second
        returnedOpString.shouldBe(opString)

        map1.merge(opBoolean)
        map1.merge(opDouble)
        map1.merge(opInt)
        map1.merge(opString)
        map2.merge(opBoolean)
        map2.merge(opDouble)
        map2.merge(opInt)
        map2.merge(opString)

        map1.getBoolean(key1)!!.shouldHaveSingleElement(valBoolean1)
        map1.getDouble(key1)!!.shouldHaveSingleElement(valDouble1)
        map1.getInt(key1)!!.shouldHaveSingleElement(valInt1)
        map1.getString(key1)!!.shouldHaveSingleElement(valString1)
        map2.getBoolean(key1)!!.shouldHaveSingleElement(valBoolean1)
        map2.getDouble(key1)!!.shouldHaveSingleElement(valDouble1)
        map2.getInt(key1)!!.shouldHaveSingleElement(valInt1)
        map2.getString(key1)!!.shouldHaveSingleElement(valString1)

        val iteratorBoolean1 = map1.iteratorBoolean()
        iteratorBoolean1.shouldHaveNext()
        iteratorBoolean1.next().shouldBe(Pair(key1, setOf(valBoolean1)))
        iteratorBoolean1.shouldBeEmpty()

        val iteratorDouble1 = map1.iteratorDouble()
        iteratorDouble1.shouldHaveNext()
        iteratorDouble1.next().shouldBe(Pair(key1, setOf(valDouble1)))
        iteratorDouble1.shouldBeEmpty()

        val iteratorInt1 = map1.iteratorInt()
        iteratorInt1.shouldHaveNext()
        iteratorInt1.next().shouldBe(Pair(key1, setOf(valInt1)))
        iteratorInt1.shouldBeEmpty()

        val iteratorString1 = map1.iteratorString()
        iteratorString1.shouldHaveNext()
        iteratorString1.next().shouldBe(Pair(key1, setOf(valString1)))
        iteratorString1.shouldBeEmpty()

        val iteratorBoolean2 = map2.iteratorBoolean()
        iteratorBoolean2.shouldHaveNext()
        iteratorBoolean2.next().shouldBe(Pair(key1, setOf(valBoolean1)))
        iteratorBoolean2.shouldBeEmpty()

        val iteratorDouble2 = map2.iteratorDouble()
        iteratorDouble2.shouldHaveNext()
        iteratorDouble2.next().shouldBe(Pair(key1, setOf(valDouble1)))
        iteratorDouble2.shouldBeEmpty()

        val iteratorInt2 = map2.iteratorInt()
        iteratorInt2.shouldHaveNext()
        iteratorInt2.next().shouldBe(Pair(key1, setOf(valInt1)))
        iteratorInt2.shouldBeEmpty()

        val iteratorString2 = map2.iteratorString()
        iteratorString2.shouldHaveNext()
        iteratorString2.next().shouldBe(Pair(key1, setOf(valString1)))
        iteratorString2.shouldBeEmpty()
    }

    /**
     * This test evaluates the use of deltas return by call to put and delete methods.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "use deltas returned by put and delete" {
        val map1 = MVMap(client1)
        val map2 = MVMap(client1)

        val returnedPutOpBoolean = map1.put(key1, valBoolean1)
        val putOpBoolean = client1.popWrite().second
        returnedPutOpBoolean.shouldBe(putOpBoolean)
        val returnedPutOpDouble = map1.put(key1, valDouble1)
        val putOpDouble = client1.popWrite().second
        returnedPutOpDouble.shouldBe(putOpDouble)
        val returnedPutOpInt = map1.put(key1, valInt1)
        val putOpInt = client1.popWrite().second
        returnedPutOpInt.shouldBe(putOpInt)
        val returnedPutOpString = map1.put(key1, valString1)
        val putOpString = client1.popWrite().second
        returnedPutOpString.shouldBe(putOpString)
        val returnedDelOpBoolean = map1.deleteBoolean(key1)
        val delOpBoolean = client1.popWrite().second
        returnedDelOpBoolean.shouldBe(delOpBoolean)
        val returnedDelOpDouble = map1.deleteDouble(key1)
        val delOpDouble = client1.popWrite().second
        returnedDelOpDouble.shouldBe(delOpDouble)
        val returnedDelOpInt = map1.deleteInt(key1)
        val delOpInt = client1.popWrite().second
        returnedDelOpInt.shouldBe(delOpInt)
        val returnedDelOpString = map1.deleteString(key1)
        val delOpString = client1.popWrite().second
        returnedDelOpString.shouldBe(delOpString)

        map1.merge(putOpBoolean)
        map1.merge(putOpDouble)
        map1.merge(putOpInt)
        map1.merge(putOpString)
        map1.merge(delOpBoolean)
        map1.merge(delOpDouble)
        map1.merge(delOpInt)
        map1.merge(delOpString)
        map2.merge(putOpBoolean)
        map2.merge(putOpDouble)
        map2.merge(putOpInt)
        map2.merge(putOpString)
        map2.merge(delOpBoolean)
        map2.merge(delOpDouble)
        map2.merge(delOpInt)
        map2.merge(delOpString)

        map1.getBoolean(key1).shouldBeNull()
        map1.getDouble(key1).shouldBeNull()
        map1.getInt(key1).shouldBeNull()
        map1.getString(key1).shouldBeNull()
        map2.getBoolean(key1).shouldBeNull()
        map2.getDouble(key1).shouldBeNull()
        map2.getInt(key1).shouldBeNull()
        map2.getString(key1).shouldBeNull()

        map1.iteratorBoolean().shouldBeEmpty()
        map1.iteratorDouble().shouldBeEmpty()
        map1.iteratorInt().shouldBeEmpty()
        map1.iteratorString().shouldBeEmpty()
        map2.iteratorBoolean().shouldBeEmpty()
        map2.iteratorDouble().shouldBeEmpty()
        map2.iteratorInt().shouldBeEmpty()
        map2.iteratorString().shouldBeEmpty()
    }

    /**
     * This test evaluates the merge of deltas return by call to put method.
     * Call to get should return the values set by puts registered in the first replica.
     * Call to iterator should return an iterator containing the values set by puts registered in the first replica.
     */
    "merge deltas returned by put operations" {
        val map1 = MVMap(client1)
        val map2 = MVMap(client1)

        val returnedOpBoolean1 = map1.put(key1, valBoolean1)
        val opBoolean1 = client1.popWrite().second
        returnedOpBoolean1.shouldBe(opBoolean1)
        val returnedOpDouble1 = map1.put(key1, valDouble1)
        val opDouble1 = client1.popWrite().second
        returnedOpDouble1.shouldBe(opDouble1)
        val returnedOpInt1 = map1.put(key1, valInt1)
        val opInt1 = client1.popWrite().second
        returnedOpInt1.shouldBe(opInt1)
        val returnedOpString1 = map1.put(key1, valString1)
        val opString1 = client1.popWrite().second
        returnedOpString1.shouldBe(opString1)
        val returnedOpBoolean2 = map1.put(key1, valBoolean2)
        val opBoolean2 = client1.popWrite().second
        returnedOpBoolean2.shouldBe(opBoolean2)
        val returnedOpDouble2 = map1.put(key1, valDouble2)
        val opDouble2 = client1.popWrite().second
        returnedOpDouble2.shouldBe(opDouble2)
        val returnedOpInt2 = map1.put(key1, valInt2)
        val opInt2 = client1.popWrite().second
        returnedOpInt2.shouldBe(opInt2)
        val returnedOpString2 = map1.put(key1, valString2)
        val opString2 = client1.popWrite().second
        returnedOpString2.shouldBe(opString2)
        val returnedOpBoolean3 = map1.put(key2, valBoolean1)
        val opBoolean3 = client1.popWrite().second
        returnedOpBoolean3.shouldBe(opBoolean3)
        val returnedOpDouble3 = map1.put(key2, valDouble1)
        val opDouble3 = client1.popWrite().second
        returnedOpDouble3.shouldBe(opDouble3)
        val returnedOpInt3 = map1.put(key2, valInt1)
        val opInt3 = client1.popWrite().second
        returnedOpInt3.shouldBe(opInt3)
        val returnedOpString3 = map1.put(key2, valString1)
        val opString3 = client1.popWrite().second
        returnedOpString3.shouldBe(opString3)

        opDouble3.merge(opString3)
        opBoolean3.merge(opDouble3)
        opInt3.merge(opBoolean3)
        opString3.merge(opInt3)
        opDouble2.merge(opString3)
        opBoolean2.merge(opDouble2)
        opInt2.merge(opBoolean2)
        opString2.merge(opInt2)
        opDouble1.merge(opString2)
        opBoolean1.merge(opDouble1)
        opInt1.merge(opBoolean1)
        opString1.merge(opInt1)
        map2.merge(opString1)

        map2.getBoolean(key1)!!.shouldHaveSingleElement(valBoolean2)
        map2.getDouble(key1)!!.shouldHaveSingleElement(valDouble2)
        map2.getInt(key1)!!.shouldHaveSingleElement(valInt2)
        map2.getString(key1)!!.shouldHaveSingleElement(valString2)
        map2.getBoolean(key2)!!.shouldHaveSingleElement(valBoolean1)
        map2.getDouble(key2)!!.shouldHaveSingleElement(valDouble1)
        map2.getInt(key2)!!.shouldHaveSingleElement(valInt1)
        map2.getString(key2)!!.shouldHaveSingleElement(valString1)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, setOf(valBoolean2)))
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key2, setOf(valBoolean1)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, setOf(valDouble2)))
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key2, setOf(valDouble1)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, setOf(valInt2)))
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key2, setOf(valInt1)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, setOf(valString2)))
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key2, setOf(valString1)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the merge of deltas return by call to put and delete methods.
     * Call to get should return the value set by put or null if it has been deleted.
     * Call to iterator should return an iterator the value set by put if it has not been deleted.
     */
    "merge deltas returned by put and delete operations" {
        val map1 = MVMap(client1)
        val map2 = MVMap(client1)

        val returnedOpBoolean1 = map1.put(key1, valBoolean1)
        val opBoolean1 = client1.popWrite().second
        returnedOpBoolean1.shouldBe(opBoolean1)
        val returnedOpDouble1 = map1.put(key1, valDouble1)
        val opDouble1 = client1.popWrite().second
        returnedOpDouble1.shouldBe(opDouble1)
        val returnedOpInt1 = map1.put(key1, valInt1)
        val opInt1 = client1.popWrite().second
        returnedOpInt1.shouldBe(opInt1)
        val returnedOpString1 = map1.put(key1, valString1)
        val opString1 = client1.popWrite().second
        returnedOpString1.shouldBe(opString1)
        val returnedOpBoolean2 = map1.deleteBoolean(key1)
        val opBoolean2 = client1.popWrite().second
        returnedOpBoolean2.shouldBe(opBoolean2)
        val returnedOpDouble2 = map1.deleteDouble(key1)
        val opDouble2 = client1.popWrite().second
        returnedOpDouble2.shouldBe(opDouble2)
        val returnedOpInt2 = map1.deleteInt(key1)
        val opInt2 = client1.popWrite().second
        returnedOpInt2.shouldBe(opInt2)
        val returnedOpString2 = map1.deleteString(key1)
        val opString2 = client1.popWrite().second
        returnedOpString2.shouldBe(opString2)
        val returnedOpBoolean3 = map1.put(key2, valBoolean1)
        val opBoolean3 = client1.popWrite().second
        returnedOpBoolean3.shouldBe(opBoolean3)
        val returnedOpDouble3 = map1.put(key2, valDouble1)
        val opDouble3 = client1.popWrite().second
        returnedOpDouble3.shouldBe(opDouble3)
        val returnedOpInt3 = map1.put(key2, valInt1)
        val opInt3 = client1.popWrite().second
        returnedOpInt3.shouldBe(opInt3)
        val returnedOpString3 = map1.put(key2, valString1)
        val opString3 = client1.popWrite().second
        returnedOpString3.shouldBe(opString3)

        opDouble3.merge(opString3)
        opBoolean3.merge(opDouble3)
        opInt3.merge(opBoolean3)
        opString3.merge(opInt3)
        opDouble2.merge(opString3)
        opBoolean2.merge(opDouble2)
        opInt2.merge(opBoolean2)
        opString2.merge(opInt2)
        opDouble1.merge(opString2)
        opBoolean1.merge(opDouble1)
        opInt1.merge(opBoolean1)
        opString1.merge(opInt1)
        map2.merge(opString1)

        map2.getBoolean(key1).shouldBeNull()
        map2.getDouble(key1).shouldBeNull()
        map2.getInt(key1).shouldBeNull()
        map2.getString(key1).shouldBeNull()
        map2.getBoolean(key2)!!.shouldHaveSingleElement(valBoolean1)
        map2.getDouble(key2)!!.shouldHaveSingleElement(valDouble1)
        map2.getInt(key2)!!.shouldHaveSingleElement(valInt1)
        map2.getString(key2)!!.shouldHaveSingleElement(valString1)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key2, setOf(valBoolean1)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key2, setOf(valDouble1)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key2, setOf(valInt1)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key2, setOf(valString1)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the generation of delta (only put) plus its merging into another replica.
     * Call to get should return the values set by puts registered in the first replica after w.r.t
     * the given context.
     * Call to iterator should return an iterator containing the values set by puts registered in the first replica
     * after w.r.t the given context.
     */
    "generate delta" {
        val vv = VersionVector()
        val map1 = MVMap(client1)
        val map2 = MVMap(client1)

        map1.put(key1, valBoolean1)
        map1.put(key1, valDouble1)
        map1.put(key1, valInt1)
        map1.put(key1, valString1)
        map1.put(key2, valBoolean1)
        map1.put(key2, valDouble1)
        map1.put(key2, valInt1)
        map1.put(key2, valString1)
        vv.update(client1.tick())
        map1.put(key3, valBoolean1)
        map1.put(key3, valDouble1)
        map1.put(key3, valInt1)
        map1.put(key3, valString1)
        map1.put(key4, valBoolean1)
        map1.put(key4, valDouble1)
        map1.put(key4, valInt1)
        map1.put(key4, valString1)
        val delta = map1.generateDelta(vv)
        map2.merge(delta)

        map2.getBoolean(key1).shouldBeNull()
        map2.getDouble(key1).shouldBeNull()
        map2.getInt(key1).shouldBeNull()
        map2.getString(key1).shouldBeNull()
        map2.getBoolean(key2).shouldBeNull()
        map2.getDouble(key2).shouldBeNull()
        map2.getInt(key2).shouldBeNull()
        map2.getString(key2).shouldBeNull()
        map2.getBoolean(key3)!!.shouldHaveSingleElement(valBoolean1)
        map2.getDouble(key3)!!.shouldHaveSingleElement(valDouble1)
        map2.getInt(key3)!!.shouldHaveSingleElement(valInt1)
        map2.getString(key3)!!.shouldHaveSingleElement(valString1)
        map2.getBoolean(key4)!!.shouldHaveSingleElement(valBoolean1)
        map2.getDouble(key4)!!.shouldHaveSingleElement(valDouble1)
        map2.getInt(key4)!!.shouldHaveSingleElement(valInt1)
        map2.getString(key4)!!.shouldHaveSingleElement(valString1)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key3, setOf(valBoolean1)))
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key4, setOf(valBoolean1)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key3, setOf(valDouble1)))
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key4, setOf(valDouble1)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key3, setOf(valInt1)))
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key4, setOf(valInt1)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key3, setOf(valString1)))
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key4, setOf(valString1)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the generation of delta (including delete) plus its merging into another replica.
     * Call to get should return the values set by puts or null set by delete w.r.t the given context.
     * Call to iterator should return an iterator containing the values set by puts if it has not been deleted
     * w.r.t the given context.
     */
    "generate delta with delete" {
        val vv = VersionVector()
        val map1 = MVMap(client1)
        val map2 = MVMap(client1)

        map1.put(key1, valBoolean1)
        map1.put(key1, valDouble1)
        map1.put(key1, valInt1)
        map1.put(key1, valString1)
        vv.update(client1.tick())
        map1.put(key2, valBoolean1)
        map1.put(key2, valDouble1)
        map1.put(key2, valInt1)
        map1.put(key2, valString1)
        map1.deleteBoolean(key2)
        map1.deleteDouble(key2)
        map1.deleteInt(key2)
        map1.deleteString(key2)
        map1.put(key3, valBoolean1)
        map1.put(key3, valDouble1)
        map1.put(key3, valInt1)
        map1.put(key3, valString1)
        val delta = map1.generateDelta(vv)
        map2.merge(delta)

        map2.getBoolean(key1).shouldBeNull()
        map2.getDouble(key1).shouldBeNull()
        map2.getInt(key1).shouldBeNull()
        map2.getString(key1).shouldBeNull()
        map2.getBoolean(key2).shouldBeNull()
        map2.getDouble(key2).shouldBeNull()
        map2.getInt(key2).shouldBeNull()
        map2.getString(key2).shouldBeNull()
        map2.getBoolean(key3)!!.shouldHaveSingleElement(valBoolean1)
        map2.getDouble(key3)!!.shouldHaveSingleElement(valDouble1)
        map2.getInt(key3)!!.shouldHaveSingleElement(valInt1)
        map2.getString(key3)!!.shouldHaveSingleElement(valString1)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key3, setOf(valBoolean1)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key3, setOf(valDouble1)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key3, setOf(valInt1)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key3, setOf(valString1)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates JSON serialization an empty MV map.
     */
    "empty JSON serialization" {
        val map = MVMap()

        val mapJson = map.toJson()

        mapJson.shouldBe("""{"type":"MVMap","metadata":{"entries":{},"causalContext":{"entries":[]}}}""")
    }

    /**
     * This test evaluates JSON deserialization of an empty MV map.
     */
    "empty JSON deserialization" {
        val mapJson = MVMap.fromJson(
            """{"type":"MVMap","metadata":{"entries":{},"causalContext":{"entries":[]}}}""",
            client1
        )
        val putDelta = mapJson.put("key1", "value1")
        putDelta.shouldBe(client1.popWrite().second)

        mapJson.getString("key1")!!.shouldHaveSingleElement("value1")
        mapJson.getString("key2").shouldBeNull()
        mapJson.getString("key3").shouldBeNull()
    }

    /**
     * This test evaluates JSON serialization of a MV map.
     */
    "JSON serialization" {
        val value1 = 1
        val value2 = "value2"
        val value3 = "value3"
        val value4 = true
        val value5 = 3.14159
        val map1 = MVMap(client1)
        val map2 = MVMap(client2)

        map1.put(key1, value1)
        map1.put(key2, value2)
        map1.deleteString(key2)
        map1.put(key3, value3)
        map1.put(key4, value4)
        map1.put(key5, value5)
        map2.put(key3, value2)
        map1.merge(map2)
        val mapJson = map1.toJson()

        mapJson.shouldBe("""{"type":"MVMap","metadata":{"entries":{"key1%INTEGER":[{"uid":{"name":"clientid1"},"cnt":-2147483647}],"key2%STRING":[{"uid":{"name":"clientid1"},"cnt":-2147483645}],"key3%STRING":[{"uid":{"name":"clientid1"},"cnt":-2147483644},{"uid":{"name":"clientid2"},"cnt":-2147483647}],"key4%BOOLEAN":[{"uid":{"name":"clientid1"},"cnt":-2147483643}],"key5%DOUBLE":[{"uid":{"name":"clientid1"},"cnt":-2147483642}]},"causalContext":{"entries":[{"name":"clientid1"},-2147483642,{"name":"clientid2"},-2147483647]}},"key1%INTEGER":[1],"key2%STRING":[null],"key3%STRING":["value3","value2"],"key4%BOOLEAN":[true],"key5%DOUBLE":[3.14159]}""")
    }

    /**
     * This test evaluates JSON deserialization of a MV map.
     */
    "JSON deserialization" {
        val mapJson = MVMap.fromJson("""{"type":"MVMap","metadata":{"entries":{"key1%INTEGER":[{"uid":{"name":"clientid1"},"cnt":-2147483647}],"key2%STRING":[{"uid":{"name":"clientid1"},"cnt":-2147483645}],"key3%STRING":[{"uid":{"name":"clientid1"},"cnt":-2147483644},{"uid":{"name":"clientid2"},"cnt":-2147483647}],"key4%BOOLEAN":[{"uid":{"name":"clientid1"},"cnt":-2147483643}],"key5%DOUBLE":[{"uid":{"name":"clientid1"},"cnt":-2147483642}]},"causalContext":{"entries":[{"name":"clientid1"},-2147483642,{"name":"clientid2"},-2147483647]}},"key1%INTEGER":[1],"key2%STRING":[null],"key3%STRING":["value3","value2"],"key4%BOOLEAN":[true],"key5%DOUBLE":[3.14159]}""")

        mapJson.getInt("key1")!!.shouldHaveSingleElement(1)
        mapJson.getString("key2").shouldBeNull()
        mapJson.getString("key3")!!.shouldContainExactlyInAnyOrder("value2", "value3")
        mapJson.getBoolean("key4")!!.shouldHaveSingleElement(true)
        mapJson.getDouble("key5")!!.shouldHaveSingleElement(3.14159)
    }
})
