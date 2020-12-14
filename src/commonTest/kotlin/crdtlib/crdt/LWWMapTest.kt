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
import io.kotest.matchers.iterator.shouldBeEmpty
import io.kotest.matchers.iterator.shouldHaveNext
import io.kotest.matchers.nulls.*

/**
 * Represents a suite test for LWWMap.
 */
class LWWMapTest : StringSpec({

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
     *
     */
    "create and get/iterator" {
        val map = LWWMap()

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
        val map = LWWMap(client1)

        map.put(key1, valBoolean1)
        map.put(key1, valDouble1)
        map.put(key1, valInt1)
        map.put(key1, valString1)

        map.getBoolean(key1).shouldBe(valBoolean1)
        map.getDouble(key1).shouldBe(valDouble1)
        map.getInt(key1).shouldBe(valInt1)
        map.getString(key1).shouldBe(valString1)

        val iteratorBoolean = map.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, valBoolean1))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, valDouble1))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, valInt1))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, valString1))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "put, delete, get/iterator" {
        val map = LWWMap(client1)

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
        val map = LWWMap(client1)

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
        val map = LWWMap(client1)

        map.put(key1, valBoolean1)
        map.put(key1, valDouble1)
        map.put(key1, valInt1)
        map.put(key1, valString1)
        map.put(key1, valBoolean2)
        map.put(key1, valDouble2)
        map.put(key1, valInt2)
        map.put(key1, valString2)

        map.getBoolean(key1).shouldBe(valBoolean2)
        map.getDouble(key1).shouldBe(valDouble2)
        map.getInt(key1).shouldBe(valInt2)
        map.getString(key1).shouldBe(valString2)

        val iteratorBoolean = map.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put put del get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "put, put, del, get/iterator" {
        val map = LWWMap(client1)

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
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client1)

        map1.put(key1, valBoolean1)
        map1.put(key1, valDouble1)
        map1.put(key1, valInt1)
        map1.put(key1, valString1)
        map1.merge(map2)
        map2.merge(map1)

        map1.getBoolean(key1).shouldBe(valBoolean1)
        map1.getDouble(key1).shouldBe(valDouble1)
        map1.getInt(key1).shouldBe(valInt1)
        map1.getString(key1).shouldBe(valString1)
        map2.getBoolean(key1).shouldBe(valBoolean1)
        map2.getDouble(key1).shouldBe(valDouble1)
        map2.getInt(key1).shouldBe(valInt1)
        map2.getString(key1).shouldBe(valString1)

        val iteratorBoolean1 = map1.iteratorBoolean()
        iteratorBoolean1.shouldHaveNext()
        iteratorBoolean1.next().shouldBe(Pair(key1, valBoolean1))
        iteratorBoolean1.shouldBeEmpty()

        val iteratorDouble1 = map1.iteratorDouble()
        iteratorDouble1.shouldHaveNext()
        iteratorDouble1.next().shouldBe(Pair(key1, valDouble1))
        iteratorDouble1.shouldBeEmpty()

        val iteratorInt1 = map1.iteratorInt()
        iteratorInt1.shouldHaveNext()
        iteratorInt1.next().shouldBe(Pair(key1, valInt1))
        iteratorInt1.shouldBeEmpty()

        val iteratorString1 = map1.iteratorString()
        iteratorString1.shouldHaveNext()
        iteratorString1.next().shouldBe(Pair(key1, valString1))
        iteratorString1.shouldBeEmpty()

        val iteratorBoolean2 = map2.iteratorBoolean()
        iteratorBoolean2.shouldHaveNext()
        iteratorBoolean2.next().shouldBe(Pair(key1, valBoolean1))
        iteratorBoolean2.shouldBeEmpty()

        val iteratorDouble2 = map2.iteratorDouble()
        iteratorDouble2.shouldHaveNext()
        iteratorDouble2.next().shouldBe(Pair(key1, valDouble1))
        iteratorDouble2.shouldBeEmpty()

        val iteratorInt2 = map2.iteratorInt()
        iteratorInt2.shouldHaveNext()
        iteratorInt2.next().shouldBe(Pair(key1, valInt1))
        iteratorInt2.shouldBeEmpty()

        val iteratorString2 = map2.iteratorString()
        iteratorString2.shouldHaveNext()
        iteratorString2.next().shouldBe(Pair(key1, valString1))
        iteratorString2.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || merge putLWW get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by the put registered in the second replica.
     */
    "R1: put; R2: merge, put LWW, get/iterator" {
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)

        map1.put(key1, valBoolean1)
        map1.put(key1, valDouble1)
        map1.put(key1, valInt1)
        map1.put(key1, valString1)
        map2.merge(map1)
        map2.put(key1, valBoolean2)
        map2.put(key1, valDouble2)
        map2.put(key1, valInt2)
        map2.put(key1, valString2)

        map2.getBoolean(key1).shouldBe(valBoolean2)
        map2.getDouble(key1).shouldBe(valDouble2)
        map2.getInt(key1).shouldBe(valInt2)
        map2.getString(key1).shouldBe(valString2)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || putLWW merge get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by put registered in the second replica.
     */
    "R1: put | R2: put, merge 1->2, get/iterator" {
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)

        map1.put(key1, valBoolean1)
        map1.put(key1, valDouble1)
        map1.put(key1, valInt1)
        map1.put(key1, valString1)
        map2.put(key1, valBoolean2)
        map2.put(key1, valDouble2)
        map2.put(key1, valInt2)
        map2.put(key1, valString2)
        map2.merge(map1)

        map2.getBoolean(key1).shouldBe(valBoolean2)
        map2.getDouble(key1).shouldBe(valDouble2)
        map2.getInt(key1).shouldBe(valInt2)
        map2.getString(key1).shouldBe(valString2)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: putLWW || put merge get/iterator.
     * Call to get should return the value set by put registered in the first replica.
     * Call to iterator should return an iterator containing the value set by the put registered in the first replica.
     */
    "R1: put | R2: put, merge R2->R1, get/iterator" {
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)

        map1.put(key1, valBoolean1)
        map1.put(key1, valDouble1)
        map1.put(key1, valInt1)
        map1.put(key1, valString1)
        map2.put(key1, valBoolean2)
        map2.put(key1, valDouble2)
        map2.put(key1, valInt2)
        map2.put(key1, valString2)
        map1.merge(map2)

        map1.getBoolean(key1).shouldBe(valBoolean2)
        map1.getDouble(key1).shouldBe(valDouble2)
        map1.getInt(key1).shouldBe(valInt2)
        map1.getString(key1).shouldBe(valString2)

        val iteratorBoolean2 = map1.iteratorBoolean()
        iteratorBoolean2.shouldHaveNext()
        iteratorBoolean2.next().shouldBe(Pair(key1, valBoolean2))
        iteratorBoolean2.shouldBeEmpty()

        val iteratorDouble2 = map1.iteratorDouble()
        iteratorDouble2.shouldHaveNext()
        iteratorDouble2.next().shouldBe(Pair(key1, valDouble2))
        iteratorDouble2.shouldBeEmpty()

        val iteratorInt2 = map1.iteratorInt()
        iteratorInt2.shouldHaveNext()
        iteratorInt2.next().shouldBe(Pair(key1, valInt2))
        iteratorInt2.shouldBeEmpty()

        val iteratorString2 = map1.iteratorString()
        iteratorString2.shouldHaveNext()
        iteratorString2.next().shouldBe(Pair(key1, valString2))
        iteratorString2.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put delLWW || put merge get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "R1: put, delete LWW; R2: put, merge, get/iterator" {
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)

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

        map2.getBoolean(key1).shouldBeNull()
        map2.getDouble(key1).shouldBeNull()
        map2.getInt(key1).shouldBeNull()
        map2.getString(key1).shouldBeNull()

        map2.iteratorBoolean().shouldBeEmpty()
        map2.iteratorDouble().shouldBeEmpty()
        map2.iteratorInt().shouldBeEmpty()
        map2.iteratorString().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put delLWW || put merge(before del) merge(after del) get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "R1: put, delete LWW; R2: put, merge before delete, merge after delete, get/iterator" {
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)

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

        map2.getBoolean(key1).shouldBeNull()
        map2.getDouble(key1).shouldBeNull()
        map2.getInt(key1).shouldBeNull()
        map2.getString(key1).shouldBeNull()

        map2.iteratorBoolean().shouldBeEmpty()
        map2.iteratorDouble().shouldBeEmpty()
        map2.iteratorInt().shouldBeEmpty()
        map2.iteratorString().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del || putLWW merge get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by the put registered in the second replica.
     */
    "R1: put, delete; R2: put LWW, merge, get/iterator" {
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)

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

        map2.getBoolean(key1).shouldBe(valBoolean2)
        map2.getDouble(key1).shouldBe(valDouble2)
        map2.getInt(key1).shouldBe(valInt2)
        map2.getString(key1).shouldBe(valString2)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del || putLWW merge(before del) merge(after del) get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by the put registered in the second replica.
     */
    "R1: put, delete; R2: put LWW, merge before delete, merge after delete, get/iterator" {
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)

        map1.put(key1, valBoolean1)
        map1.put(key1, valDouble1)
        map1.put(key1, valInt1)
        map1.put(key1, valString1)
        client2.tick()
        client2.tick()
        client2.tick()
        client2.tick()
        map2.put(key1, valBoolean2)
        map2.put(key1, valDouble2)
        map2.put(key1, valInt2)
        map2.put(key1, valString2)
        map2.merge(map1)
        map1.deleteBoolean(key1)
        map1.deleteDouble(key1)
        map1.deleteInt(key1)
        map1.deleteString(key1)
        map2.merge(map1)

        map2.getBoolean(key1).shouldBe(valBoolean2)
        map2.getDouble(key1).shouldBe(valDouble2)
        map2.getInt(key1).shouldBe(valInt2)
        map2.getString(key1).shouldBe(valString2)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || put || merge1 delLWW merge2 get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "R1: put | R2: put; merge R1->R3, R3: delete, merge R2->R3" {
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)
        val map3 = LWWMap(client3)

        map1.put(key1, valBoolean1)
        map1.put(key1, valDouble1)
        map1.put(key1, valInt1)
        map1.put(key1, valString1)
        map2.put(key1, valBoolean2)
        map2.put(key1, valDouble2)
        map2.put(key1, valInt2)
        map2.put(key1, valString2)
        map3.merge(map1)
        map3.deleteBoolean(key1)
        map3.deleteDouble(key1)
        map3.deleteInt(key1)
        map3.deleteString(key1)
        map3.merge(map2)

        map3.getBoolean(key1).shouldBeNull()
        map3.getDouble(key1).shouldBeNull()
        map3.getInt(key1).shouldBeNull()
        map3.getString(key1).shouldBeNull()

        map3.iteratorBoolean().shouldBeEmpty()
        map3.iteratorDouble().shouldBeEmpty()
        map3.iteratorInt().shouldBeEmpty()
        map3.iteratorString().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || putLWW || merge1 del merge2 get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by the put registered in the second replica.
     */
    "R1: put | R3: put; merge R1->R2, R2: delete, merge R3->R2" {
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)
        val map3 = LWWMap(client3)

        map1.put(key1, valBoolean1)
        map1.put(key1, valDouble1)
        map1.put(key1, valInt1)
        map1.put(key1, valString1)
        map3.put(key1, valBoolean2)
        map3.put(key1, valDouble2)
        map3.put(key1, valInt2)
        map3.put(key1, valString2)
        map2.merge(map1)
        // environment not updated by merge: next deletes lose
        map2.deleteBoolean(key1)
        map2.deleteDouble(key1)
        map2.deleteInt(key1)
        map2.deleteString(key1)
        map2.merge(map3)

        map2.getBoolean(key1).shouldBe(valBoolean2)
        map2.getDouble(key1).shouldBe(valDouble2)
        map2.getInt(key1).shouldBe(valInt2)
        map2.getString(key1).shouldBe(valString2)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the use of deltas return by call to put method.
     * Call to get should return the value set by put registered in the first replica.
     * Call to iterator should return an iterator containing the value set by the put registered in the first replica.
     */
    "use deltas returned by put" {
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client1)

        val opBoolean = map1.put(key1, valBoolean1)
        val opDouble = map1.put(key1, valDouble1)
        val opInt = map1.put(key1, valInt1)
        val opString = map1.put(key1, valString1)
        map1.merge(opBoolean)
        map1.merge(opDouble)
        map1.merge(opInt)
        map1.merge(opString)
        map2.merge(opBoolean)
        map2.merge(opDouble)
        map2.merge(opInt)
        map2.merge(opString)

        map1.getBoolean(key1).shouldBe(valBoolean1)
        map1.getDouble(key1).shouldBe(valDouble1)
        map1.getInt(key1).shouldBe(valInt1)
        map1.getString(key1).shouldBe(valString1)
        map2.getBoolean(key1).shouldBe(valBoolean1)
        map2.getDouble(key1).shouldBe(valDouble1)
        map2.getInt(key1).shouldBe(valInt1)
        map2.getString(key1).shouldBe(valString1)

        val iteratorBoolean1 = map1.iteratorBoolean()
        iteratorBoolean1.shouldHaveNext()
        iteratorBoolean1.next().shouldBe(Pair(key1, valBoolean1))
        iteratorBoolean1.shouldBeEmpty()

        val iteratorDouble1 = map1.iteratorDouble()
        iteratorDouble1.shouldHaveNext()
        iteratorDouble1.next().shouldBe(Pair(key1, valDouble1))
        iteratorDouble1.shouldBeEmpty()

        val iteratorInt1 = map1.iteratorInt()
        iteratorInt1.shouldHaveNext()
        iteratorInt1.next().shouldBe(Pair(key1, valInt1))
        iteratorInt1.shouldBeEmpty()

        val iteratorString1 = map1.iteratorString()
        iteratorString1.shouldHaveNext()
        iteratorString1.next().shouldBe(Pair(key1, valString1))
        iteratorString1.shouldBeEmpty()

        val iteratorBoolean2 = map2.iteratorBoolean()
        iteratorBoolean2.shouldHaveNext()
        iteratorBoolean2.next().shouldBe(Pair(key1, valBoolean1))
        iteratorBoolean2.shouldBeEmpty()

        val iteratorDouble2 = map2.iteratorDouble()
        iteratorDouble2.shouldHaveNext()
        iteratorDouble2.next().shouldBe(Pair(key1, valDouble1))
        iteratorDouble2.shouldBeEmpty()

        val iteratorInt2 = map2.iteratorInt()
        iteratorInt2.shouldHaveNext()
        iteratorInt2.next().shouldBe(Pair(key1, valInt1))
        iteratorInt2.shouldBeEmpty()

        val iteratorString2 = map2.iteratorString()
        iteratorString2.shouldHaveNext()
        iteratorString2.next().shouldBe(Pair(key1, valString1))
        iteratorString2.shouldBeEmpty()
    }

    /**
     * This test evaluates the use of deltas return by call to put and delete methods.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "use deltas returned by put and delete" {
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client1)

        val putOpBoolean = map1.put(key1, valBoolean1)
        val putOpDouble = map1.put(key1, valDouble1)
        val putOpInt = map1.put(key1, valInt1)
        val putOpString = map1.put(key1, valString1)
        val delOpBoolean = map1.deleteBoolean(key1)
        val delOpDouble = map1.deleteDouble(key1)
        val delOpInt = map1.deleteInt(key1)
        val delOpString = map1.deleteString(key1)
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
     * Call to iterator should return an iterator containing the values set by the puts registered in the first replica.
     */
    "merge deltas returned by put operations" {
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client1)

        val opBoolean1 = map1.put(key1, valBoolean1)
        val opDouble1 = map1.put(key1, valDouble1)
        val opInt1 = map1.put(key1, valInt1)
        val opString1 = map1.put(key1, valString1)
        val opBoolean2 = map1.put(key1, valBoolean2)
        val opDouble2 = map1.put(key1, valDouble2)
        val opInt2 = map1.put(key1, valInt2)
        val opString2 = map1.put(key1, valString2)
        val opBoolean3 = map1.put(key2, valBoolean1)
        val opDouble3 = map1.put(key2, valDouble1)
        val opInt3 = map1.put(key2, valInt1)
        val opString3 = map1.put(key2, valString1)
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

        map2.getBoolean(key1).shouldBe(valBoolean2)
        map2.getDouble(key1).shouldBe(valDouble2)
        map2.getInt(key1).shouldBe(valInt2)
        map2.getString(key1).shouldBe(valString2)
        map2.getBoolean(key2).shouldBe(valBoolean1)
        map2.getDouble(key2).shouldBe(valDouble1)
        map2.getInt(key2).shouldBe(valInt1)
        map2.getString(key2).shouldBe(valString1)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, valBoolean2))
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key2, valBoolean1))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, valDouble2))
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key2, valDouble1))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, valInt2))
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key2, valInt1))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, valString2))
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key2, valString1))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the merge of deltas return by call to put and delete methods.
     * Call to get should return the value set by put or null if it has been deleted.
     * Call to iterator should return an iterator containing the value set by put with key2.
     */
    "merge deltas returned by put and delete operations" {
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client1)

        val opBoolean1 = map1.put(key1, valBoolean1)
        val opDouble1 = map1.put(key1, valDouble1)
        val opInt1 = map1.put(key1, valInt1)
        val opString1 = map1.put(key1, valString1)
        val opBoolean2 = map1.deleteBoolean(key1)
        val opDouble2 = map1.deleteDouble(key1)
        val opInt2 = map1.deleteInt(key1)
        val opString2 = map1.deleteString(key1)
        val opBoolean3 = map1.put(key2, valBoolean1)
        val opDouble3 = map1.put(key2, valDouble1)
        val opInt3 = map1.put(key2, valInt1)
        val opString3 = map1.put(key2, valString1)
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
        map2.getBoolean(key2).shouldBe(valBoolean1)
        map2.getDouble(key2).shouldBe(valDouble1)
        map2.getInt(key2).shouldBe(valInt1)
        map2.getString(key2).shouldBe(valString1)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key2, valBoolean1))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key2, valDouble1))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key2, valInt1))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key2, valString1))
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
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client1)

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
        map2.getBoolean(key3).shouldBe(valBoolean1)
        map2.getDouble(key3).shouldBe(valDouble1)
        map2.getInt(key3).shouldBe(valInt1)
        map2.getString(key3).shouldBe(valString1)
        map2.getBoolean(key4).shouldBe(valBoolean1)
        map2.getDouble(key4).shouldBe(valDouble1)
        map2.getInt(key4).shouldBe(valInt1)
        map2.getString(key4).shouldBe(valString1)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key3, valBoolean1))
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key4, valBoolean1))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key3, valDouble1))
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key4, valDouble1))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key3, valInt1))
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key4, valInt1))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key3, valString1))
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key4, valString1))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the generation of delta (including delete) plus its merging into another replica.
     * Call to get should return the values set by puts or null set by delete w.r.t the given context.
     * Call to iterator should return an iterator containing the values set by puts w.r.t the given context.
     */
    "generate delta with delete" {
        val vv = VersionVector()
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client1)

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
        map2.getBoolean(key3).shouldBe(valBoolean1)
        map2.getDouble(key3).shouldBe(valDouble1)
        map2.getInt(key3).shouldBe(valInt1)
        map2.getString(key3).shouldBe(valString1)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key3, valBoolean1))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key3, valDouble1))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key3, valInt1))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key3, valString1))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates JSON serialization an empty LWW map.
     */
    "empty JSON serialization" {
        val map = LWWMap(client1)

        val mapJson = map.toJson()

        mapJson.shouldBe("""{"type":"LWWMap","metadata":{"entries":{}}}""")
    }

    /**
     * This test evaluates JSON deserialization of an empty LWW map.
     */
    "empty JSON deserialization" {
        val mapJson = LWWMap.fromJson(
            """{"type":"LWWMap","metadata":{"entries":{}}}""",
            client1
        )
        mapJson.put("key1", "value1")

        mapJson.getString("key1").shouldBe("value1")
        mapJson.getString("key2").shouldBeNull()
        mapJson.getString("key3").shouldBeNull()
    }

    /**
     * This test evaluates JSON serialization of a LWW map.
     */
    "JSON serialization" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val value1 = 1
        val value2 = "value2"
        val value3 = "value3"
        val value4 = true
        val value5 = 3.14159
        val map = LWWMap(client)

        map.put(key1, value1)
        map.put(key2, value2)
        map.deleteString(key2)
        map.put(key3, value3)
        map.put(key4, value4)
        map.put(key5, value5)
        val mapJson = map.toJson()

        mapJson.shouldBe("""{"type":"LWWMap","metadata":{"entries":{"key1%INTEGER":{"uid":{"name":"clientid"},"cnt":-2147483647},"key2%STRING":{"uid":{"name":"clientid"},"cnt":-2147483645},"key3%STRING":{"uid":{"name":"clientid"},"cnt":-2147483644},"key4%BOOLEAN":{"uid":{"name":"clientid"},"cnt":-2147483643},"key5%DOUBLE":{"uid":{"name":"clientid"},"cnt":-2147483642}}},"key1%INTEGER":1,"key2%STRING":null,"key3%STRING":"value3","key4%BOOLEAN":true,"key5%DOUBLE":3.14159}""")
    }

    /**
     * This test evaluates JSON deserialization of a LWW map.
     */
    "JSON deserialization" {
        val mapJson = LWWMap.fromJson("""{"type":"LWWMap","metadata":{"entries":{"key1%INTEGER":{"uid":{"name":"clientid"},"cnt":-2147483648},"key2%STRING":{"uid":{"name":"clientid"},"cnt":-2147483646},"key3%STRING":{"uid":{"name":"clientid"},"cnt":-2147483645},"key4%BOOLEAN":{"uid":{"name":"clientid"},"cnt":-2147483644},"key5%DOUBLE":{"uid":{"name":"clientid"},"cnt":-2147483643}}},"key1%INTEGER":1,"key2%STRING":null,"key3%STRING":"value3","key4%BOOLEAN":true,"key5%DOUBLE":3.14159}""")

        mapJson.getInt("key1").shouldBe(1)
        mapJson.getString("key2").shouldBeNull()
        mapJson.getString("key3").shouldBe("value3")
        mapJson.getBoolean("key4").shouldBe(true)
        mapJson.getDouble("key5").shouldBe(3.14159)
    }
})
