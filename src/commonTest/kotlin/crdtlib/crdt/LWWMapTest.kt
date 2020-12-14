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
    /**
     * This test evaluates the scenario: get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     *
     */
    "create and get/iterator" {
        val key = "key"
        val map = LWWMap()

        map.getBoolean(key).shouldBeNull()
        map.getDouble(key).shouldBeNull()
        map.getInt(key).shouldBeNull()
        map.getString(key).shouldBeNull()

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
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map = LWWMap(client)

        map.put(key, valueBoolean)
        map.put(key, valueDouble)
        map.put(key, valueInt)
        map.put(key, valueString)

        map.getBoolean(key).shouldBe(valueBoolean)
        map.getDouble(key).shouldBe(valueDouble)
        map.getInt(key).shouldBe(valueInt)
        map.getString(key).shouldBe(valueString)

        val iteratorBoolean = map.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, valueBoolean))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, valueDouble))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, valueInt))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, valueString))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "put, delete, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map = LWWMap(client)

        map.put(key, valueBoolean)
        map.put(key, valueDouble)
        map.put(key, valueInt)
        map.put(key, valueString)
        map.deleteBoolean(key)
        map.deleteDouble(key)
        map.deleteInt(key)
        map.deleteString(key)

        map.getBoolean(key).shouldBeNull()
        map.getDouble(key).shouldBeNull()
        map.getInt(key).shouldBeNull()
        map.getString(key).shouldBeNull()

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
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val map = LWWMap(client)

        map.deleteBoolean(key)
        map.deleteDouble(key)
        map.deleteInt(key)
        map.deleteString(key)

        map.getBoolean(key).shouldBeNull()
        map.getDouble(key).shouldBeNull()
        map.getInt(key).shouldBeNull()
        map.getString(key).shouldBeNull()

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
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map = LWWMap(client)

        map.put(key, valBoolean1)
        map.put(key, valDouble1)
        map.put(key, valInt1)
        map.put(key, valString1)
        map.put(key, valBoolean2)
        map.put(key, valDouble2)
        map.put(key, valInt2)
        map.put(key, valString2)

        map.getBoolean(key).shouldBe(valBoolean2)
        map.getDouble(key).shouldBe(valDouble2)
        map.getInt(key).shouldBe(valInt2)
        map.getString(key).shouldBe(valString2)

        val iteratorBoolean = map.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put put del get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "put, put, del, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map = LWWMap(client)

        map.put(key, valBoolean1)
        map.put(key, valDouble1)
        map.put(key, valInt1)
        map.put(key, valString1)
        map.put(key, valBoolean2)
        map.put(key, valDouble2)
        map.put(key, valInt2)
        map.put(key, valString2)
        map.deleteBoolean(key)
        map.deleteDouble(key)
        map.deleteInt(key)
        map.deleteString(key)

        map.getBoolean(key).shouldBeNull()
        map.getDouble(key).shouldBeNull()
        map.getInt(key).shouldBeNull()
        map.getString(key).shouldBeNull()

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
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = LWWMap(client)
        val map2 = LWWMap(client)

        map1.put(key, valueBoolean)
        map1.put(key, valueDouble)
        map1.put(key, valueInt)
        map1.put(key, valueString)
        map1.merge(map2)
        map2.merge(map1)

        map1.getBoolean(key).shouldBe(valueBoolean)
        map1.getDouble(key).shouldBe(valueDouble)
        map1.getInt(key).shouldBe(valueInt)
        map1.getString(key).shouldBe(valueString)
        map2.getBoolean(key).shouldBe(valueBoolean)
        map2.getDouble(key).shouldBe(valueDouble)
        map2.getInt(key).shouldBe(valueInt)
        map2.getString(key).shouldBe(valueString)

        val iteratorBoolean1 = map1.iteratorBoolean()
        iteratorBoolean1.shouldHaveNext()
        iteratorBoolean1.next().shouldBe(Pair(key, valueBoolean))
        iteratorBoolean1.shouldBeEmpty()

        val iteratorDouble1 = map1.iteratorDouble()
        iteratorDouble1.shouldHaveNext()
        iteratorDouble1.next().shouldBe(Pair(key, valueDouble))
        iteratorDouble1.shouldBeEmpty()

        val iteratorInt1 = map1.iteratorInt()
        iteratorInt1.shouldHaveNext()
        iteratorInt1.next().shouldBe(Pair(key, valueInt))
        iteratorInt1.shouldBeEmpty()

        val iteratorString1 = map1.iteratorString()
        iteratorString1.shouldHaveNext()
        iteratorString1.next().shouldBe(Pair(key, valueString))
        iteratorString1.shouldBeEmpty()

        val iteratorBoolean2 = map2.iteratorBoolean()
        iteratorBoolean2.shouldHaveNext()
        iteratorBoolean2.next().shouldBe(Pair(key, valueBoolean))
        iteratorBoolean2.shouldBeEmpty()

        val iteratorDouble2 = map2.iteratorDouble()
        iteratorDouble2.shouldHaveNext()
        iteratorDouble2.next().shouldBe(Pair(key, valueDouble))
        iteratorDouble2.shouldBeEmpty()

        val iteratorInt2 = map2.iteratorInt()
        iteratorInt2.shouldHaveNext()
        iteratorInt2.next().shouldBe(Pair(key, valueInt))
        iteratorInt2.shouldBeEmpty()

        val iteratorString2 = map2.iteratorString()
        iteratorString2.shouldHaveNext()
        iteratorString2.next().shouldBe(Pair(key, valueString))
        iteratorString2.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || merge putLWW get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by the put registered in the second replica.
     */
    "R1: put; R2: merge, put LWW, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)

        map1.put(key, valBoolean1)
        map1.put(key, valDouble1)
        map1.put(key, valInt1)
        map1.put(key, valString1)
        map2.merge(map1)
        map2.put(key, valBoolean2)
        map2.put(key, valDouble2)
        map2.put(key, valInt2)
        map2.put(key, valString2)

        map2.getBoolean(key).shouldBe(valBoolean2)
        map2.getDouble(key).shouldBe(valDouble2)
        map2.getInt(key).shouldBe(valInt2)
        map2.getString(key).shouldBe(valString2)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || putLWW merge get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by put registered in the second replica.
     */
    "R1: put | R2: put, merge 1->2, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)

        map1.put(key, valBoolean1)
        map1.put(key, valDouble1)
        map1.put(key, valInt1)
        map1.put(key, valString1)
        map2.put(key, valBoolean2)
        map2.put(key, valDouble2)
        map2.put(key, valInt2)
        map2.put(key, valString2)
        map2.merge(map1)

        map2.getBoolean(key).shouldBe(valBoolean2)
        map2.getDouble(key).shouldBe(valDouble2)
        map2.getInt(key).shouldBe(valInt2)
        map2.getString(key).shouldBe(valString2)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: putLWW || put merge get/iterator.
     * Call to get should return the value set by put registered in the first replica.
     * Call to iterator should return an iterator containing the value set by the put registered in the first replica.
     */
    "R1: put | R2: put, merge R2->R1, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)

        map1.put(key, valBoolean1)
        map1.put(key, valDouble1)
        map1.put(key, valInt1)
        map1.put(key, valString1)
        map2.put(key, valBoolean2)
        map2.put(key, valDouble2)
        map2.put(key, valInt2)
        map2.put(key, valString2)
        map1.merge(map2)

        map1.getBoolean(key).shouldBe(valBoolean2)
        map1.getDouble(key).shouldBe(valDouble2)
        map1.getInt(key).shouldBe(valInt2)
        map1.getString(key).shouldBe(valString2)

        val iteratorBoolean2 = map1.iteratorBoolean()
        iteratorBoolean2.shouldHaveNext()
        iteratorBoolean2.next().shouldBe(Pair(key, valBoolean2))
        iteratorBoolean2.shouldBeEmpty()

        val iteratorDouble2 = map1.iteratorDouble()
        iteratorDouble2.shouldHaveNext()
        iteratorDouble2.next().shouldBe(Pair(key, valDouble2))
        iteratorDouble2.shouldBeEmpty()

        val iteratorInt2 = map1.iteratorInt()
        iteratorInt2.shouldHaveNext()
        iteratorInt2.next().shouldBe(Pair(key, valInt2))
        iteratorInt2.shouldBeEmpty()

        val iteratorString2 = map1.iteratorString()
        iteratorString2.shouldHaveNext()
        iteratorString2.next().shouldBe(Pair(key, valString2))
        iteratorString2.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put delLWW || put merge get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "R1: put, delete LWW; R2: put, merge, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)

        map2.put(key, valBoolean2)
        map2.put(key, valDouble2)
        map2.put(key, valInt2)
        map2.put(key, valString2)
        map1.put(key, valBoolean1)
        map1.put(key, valDouble1)
        map1.put(key, valInt1)
        map1.put(key, valString1)
        map1.deleteBoolean(key)
        map1.deleteDouble(key)
        map1.deleteInt(key)
        map1.deleteString(key)
        map2.merge(map1)

        map2.getBoolean(key).shouldBeNull()
        map2.getDouble(key).shouldBeNull()
        map2.getInt(key).shouldBeNull()
        map2.getString(key).shouldBeNull()

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
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)

        map2.put(key, valBoolean2)
        map2.put(key, valDouble2)
        map2.put(key, valInt2)
        map2.put(key, valString2)
        map1.put(key, valBoolean1)
        map1.put(key, valDouble1)
        map1.put(key, valInt1)
        map1.put(key, valString1)
        map2.merge(map1)
        map1.deleteBoolean(key)
        map1.deleteDouble(key)
        map1.deleteInt(key)
        map1.deleteString(key)
        map2.merge(map1)

        map2.getBoolean(key).shouldBeNull()
        map2.getDouble(key).shouldBeNull()
        map2.getInt(key).shouldBeNull()
        map2.getString(key).shouldBeNull()

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
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)

        map1.put(key, valBoolean1)
        map1.put(key, valDouble1)
        map1.put(key, valInt1)
        map1.put(key, valString1)
        map1.deleteBoolean(key)
        map1.deleteDouble(key)
        map1.deleteInt(key)
        map1.deleteString(key)
        client2.tick()
        client2.tick()
        client2.tick()
        client2.tick()
        map2.put(key, valBoolean2)
        map2.put(key, valDouble2)
        map2.put(key, valInt2)
        map2.put(key, valString2)
        map2.merge(map1)

        map2.getBoolean(key).shouldBe(valBoolean2)
        map2.getDouble(key).shouldBe(valDouble2)
        map2.getInt(key).shouldBe(valInt2)
        map2.getString(key).shouldBe(valString2)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del || putLWW merge(before del) merge(after del) get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by the put registered in the second replica.
     */
    "R1: put, delete; R2: put LWW, merge before delete, merge after delete, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)

        map1.put(key, valBoolean1)
        map1.put(key, valDouble1)
        map1.put(key, valInt1)
        map1.put(key, valString1)
        client2.tick()
        client2.tick()
        client2.tick()
        client2.tick()
        map2.put(key, valBoolean2)
        map2.put(key, valDouble2)
        map2.put(key, valInt2)
        map2.put(key, valString2)
        map2.merge(map1)
        map1.deleteBoolean(key)
        map1.deleteDouble(key)
        map1.deleteInt(key)
        map1.deleteString(key)
        map2.merge(map1)

        map2.getBoolean(key).shouldBe(valBoolean2)
        map2.getDouble(key).shouldBe(valDouble2)
        map2.getInt(key).shouldBe(valInt2)
        map2.getString(key).shouldBe(valString2)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || put || merge1 delLWW merge2 get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "R1: put | R2: put; merge R1->R3, R3: delete, merge R2->R3" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val client3 = SimpleEnvironment(uid3)
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)
        val map3 = LWWMap(client3)

        map1.put(key, valBoolean1)
        map1.put(key, valDouble1)
        map1.put(key, valInt1)
        map1.put(key, valString1)
        map2.put(key, valBoolean2)
        map2.put(key, valDouble2)
        map2.put(key, valInt2)
        map2.put(key, valString2)
        map3.merge(map1)
        map3.deleteBoolean(key)
        map3.deleteDouble(key)
        map3.deleteInt(key)
        map3.deleteString(key)
        map3.merge(map2)

        map3.getBoolean(key).shouldBeNull()
        map3.getDouble(key).shouldBeNull()
        map3.getInt(key).shouldBeNull()
        map3.getString(key).shouldBeNull()

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
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val uid3 = ClientUId("clientid3")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val client3 = SimpleEnvironment(uid3)
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap(client1)
        val map2 = LWWMap(client2)
        val map3 = LWWMap(client3)

        map1.put(key, valBoolean1)
        map1.put(key, valDouble1)
        map1.put(key, valInt1)
        map1.put(key, valString1)
        map3.put(key, valBoolean2)
        map3.put(key, valDouble2)
        map3.put(key, valInt2)
        map3.put(key, valString2)
        map2.merge(map1)
        // environment not updated by merge: next deletes lose
        map2.deleteBoolean(key)
        map2.deleteDouble(key)
        map2.deleteInt(key)
        map2.deleteString(key)
        map2.merge(map3)

        map2.getBoolean(key).shouldBe(valBoolean2)
        map2.getDouble(key).shouldBe(valDouble2)
        map2.getInt(key).shouldBe(valInt2)
        map2.getString(key).shouldBe(valString2)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the use of deltas return by call to put method.
     * Call to get should return the value set by put registered in the first replica.
     * Call to iterator should return an iterator containing the value set by the put registered in the first replica.
     */
    "use deltas returned by put" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = LWWMap(client)
        val map2 = LWWMap(client)

        val opBoolean = map1.put(key, valueBoolean)
        val opDouble = map1.put(key, valueDouble)
        val opInt = map1.put(key, valueInt)
        val opString = map1.put(key, valueString)
        map1.merge(opBoolean)
        map1.merge(opDouble)
        map1.merge(opInt)
        map1.merge(opString)
        map2.merge(opBoolean)
        map2.merge(opDouble)
        map2.merge(opInt)
        map2.merge(opString)

        map1.getBoolean(key).shouldBe(valueBoolean)
        map1.getDouble(key).shouldBe(valueDouble)
        map1.getInt(key).shouldBe(valueInt)
        map1.getString(key).shouldBe(valueString)
        map2.getBoolean(key).shouldBe(valueBoolean)
        map2.getDouble(key).shouldBe(valueDouble)
        map2.getInt(key).shouldBe(valueInt)
        map2.getString(key).shouldBe(valueString)

        val iteratorBoolean1 = map1.iteratorBoolean()
        iteratorBoolean1.shouldHaveNext()
        iteratorBoolean1.next().shouldBe(Pair(key, valueBoolean))
        iteratorBoolean1.shouldBeEmpty()

        val iteratorDouble1 = map1.iteratorDouble()
        iteratorDouble1.shouldHaveNext()
        iteratorDouble1.next().shouldBe(Pair(key, valueDouble))
        iteratorDouble1.shouldBeEmpty()

        val iteratorInt1 = map1.iteratorInt()
        iteratorInt1.shouldHaveNext()
        iteratorInt1.next().shouldBe(Pair(key, valueInt))
        iteratorInt1.shouldBeEmpty()

        val iteratorString1 = map1.iteratorString()
        iteratorString1.shouldHaveNext()
        iteratorString1.next().shouldBe(Pair(key, valueString))
        iteratorString1.shouldBeEmpty()

        val iteratorBoolean2 = map2.iteratorBoolean()
        iteratorBoolean2.shouldHaveNext()
        iteratorBoolean2.next().shouldBe(Pair(key, valueBoolean))
        iteratorBoolean2.shouldBeEmpty()

        val iteratorDouble2 = map2.iteratorDouble()
        iteratorDouble2.shouldHaveNext()
        iteratorDouble2.next().shouldBe(Pair(key, valueDouble))
        iteratorDouble2.shouldBeEmpty()

        val iteratorInt2 = map2.iteratorInt()
        iteratorInt2.shouldHaveNext()
        iteratorInt2.next().shouldBe(Pair(key, valueInt))
        iteratorInt2.shouldBeEmpty()

        val iteratorString2 = map2.iteratorString()
        iteratorString2.shouldHaveNext()
        iteratorString2.next().shouldBe(Pair(key, valueString))
        iteratorString2.shouldBeEmpty()
    }

    /**
     * This test evaluates the use of deltas return by call to put and delete methods.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "use deltas returned by put and delete" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = LWWMap(client)
        val map2 = LWWMap(client)

        val putOpBoolean = map1.put(key, valueBoolean)
        val putOpDouble = map1.put(key, valueDouble)
        val putOpInt = map1.put(key, valueInt)
        val putOpString = map1.put(key, valueString)
        val delOpBoolean = map1.deleteBoolean(key)
        val delOpDouble = map1.deleteDouble(key)
        val delOpInt = map1.deleteInt(key)
        val delOpString = map1.deleteString(key)
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

        map1.getBoolean(key).shouldBeNull()
        map1.getDouble(key).shouldBeNull()
        map1.getInt(key).shouldBeNull()
        map1.getString(key).shouldBeNull()
        map2.getBoolean(key).shouldBeNull()
        map2.getDouble(key).shouldBeNull()
        map2.getInt(key).shouldBeNull()
        map2.getString(key).shouldBeNull()

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
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key1 = "key1"
        val key2 = "key2"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap(client)
        val map2 = LWWMap(client)

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
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key1 = "key1"
        val key2 = "key2"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = LWWMap(client)
        val map2 = LWWMap(client)

        val opBoolean1 = map1.put(key1, valueBoolean)
        val opDouble1 = map1.put(key1, valueDouble)
        val opInt1 = map1.put(key1, valueInt)
        val opString1 = map1.put(key1, valueString)
        val opBoolean2 = map1.deleteBoolean(key1)
        val opDouble2 = map1.deleteDouble(key1)
        val opInt2 = map1.deleteInt(key1)
        val opString2 = map1.deleteString(key1)
        val opBoolean3 = map1.put(key2, valueBoolean)
        val opDouble3 = map1.put(key2, valueDouble)
        val opInt3 = map1.put(key2, valueInt)
        val opString3 = map1.put(key2, valueString)
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
        map2.getBoolean(key2).shouldBe(valueBoolean)
        map2.getDouble(key2).shouldBe(valueDouble)
        map2.getInt(key2).shouldBe(valueInt)
        map2.getString(key2).shouldBe(valueString)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key2, valueBoolean))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key2, valueDouble))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key2, valueInt))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key2, valueString))
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
        val uid = ClientUId("clientid1")
        val client = SimpleEnvironment(uid)
        val vv = VersionVector()
        val key1 = "key1"
        val key2 = "key2"
        val key3 = "key3"
        val key4 = "key4"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = LWWMap(client)
        val map2 = LWWMap(client)

        map1.put(key1, valueBoolean)
        map1.put(key1, valueDouble)
        map1.put(key1, valueInt)
        map1.put(key1, valueString)
        map1.put(key2, valueBoolean)
        map1.put(key2, valueDouble)
        map1.put(key2, valueInt)
        map1.put(key2, valueString)
        vv.update(client.tick())
        map1.put(key3, valueBoolean)
        map1.put(key3, valueDouble)
        map1.put(key3, valueInt)
        map1.put(key3, valueString)
        map1.put(key4, valueBoolean)
        map1.put(key4, valueDouble)
        map1.put(key4, valueInt)
        map1.put(key4, valueString)
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
        map2.getBoolean(key3).shouldBe(valueBoolean)
        map2.getDouble(key3).shouldBe(valueDouble)
        map2.getInt(key3).shouldBe(valueInt)
        map2.getString(key3).shouldBe(valueString)
        map2.getBoolean(key4).shouldBe(valueBoolean)
        map2.getDouble(key4).shouldBe(valueDouble)
        map2.getInt(key4).shouldBe(valueInt)
        map2.getString(key4).shouldBe(valueString)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key3, valueBoolean))
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key4, valueBoolean))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key3, valueDouble))
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key4, valueDouble))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key3, valueInt))
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key4, valueInt))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key3, valueString))
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key4, valueString))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the generation of delta (including delete) plus its merging into another replica.
     * Call to get should return the values set by puts or null set by delete w.r.t the given context.
     * Call to iterator should return an iterator containing the values set by puts w.r.t the given context.
     */
    "generate delta with delete" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val vv = VersionVector()
        val key1 = "key1"
        val key2 = "key2"
        val key3 = "key3"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = LWWMap(client)
        val map2 = LWWMap(client)

        map1.put(key1, valueBoolean)
        map1.put(key1, valueDouble)
        map1.put(key1, valueInt)
        map1.put(key1, valueString)
        vv.update(client.tick())
        map1.put(key2, valueBoolean)
        map1.put(key2, valueDouble)
        map1.put(key2, valueInt)
        map1.put(key2, valueString)
        map1.deleteBoolean(key2)
        map1.deleteDouble(key2)
        map1.deleteInt(key2)
        map1.deleteString(key2)
        map1.put(key3, valueBoolean)
        map1.put(key3, valueDouble)
        map1.put(key3, valueInt)
        map1.put(key3, valueString)
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
        map2.getBoolean(key3).shouldBe(valueBoolean)
        map2.getDouble(key3).shouldBe(valueDouble)
        map2.getInt(key3).shouldBe(valueInt)
        map2.getString(key3).shouldBe(valueString)

        val iteratorBoolean = map2.iteratorBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key3, valueBoolean))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key3, valueDouble))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key3, valueInt))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key3, valueString))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates JSON serialization an empty LWW map.
     */
    "empty JSON serialization" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val map = LWWMap(client)

        val mapJson = map.toJson()

        mapJson.shouldBe("""{"type":"LWWMap","metadata":{"entries":{}}}""")
    }

    /**
     * This test evaluates JSON deserialization of an empty LWW map.
     */
    "empty JSON deserialization" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)

        val mapJson = LWWMap.fromJson(
            """{"type":"LWWMap","metadata":{"entries":{}}}""",
            client
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
        val key1 = "key1"
        val key2 = "key2"
        val key3 = "key3"
        val key4 = "key4"
        val key5 = "key5"
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
