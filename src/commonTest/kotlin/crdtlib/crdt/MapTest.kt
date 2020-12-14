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
 * Represents a suite test for Map.
 */
class MapTest : StringSpec({
    /**
     * This test evaluates the scenario: get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "create and get/iterator" {
        val key = "key"
        val map = Map()

        map.getLWWBoolean(key).shouldBeNull()
        map.getLWWDouble(key).shouldBeNull()
        map.getLWWInt(key).shouldBeNull()
        map.getLWWString(key).shouldBeNull()
        map.getMVBoolean(key).shouldBeNull()
        map.getMVDouble(key).shouldBeNull()
        map.getMVInt(key).shouldBeNull()
        map.getMVString(key).shouldBeNull()
        map.getCntInt(key).shouldBeNull()

        map.iteratorLWWBoolean().shouldBeEmpty()
        map.iteratorLWWDouble().shouldBeEmpty()
        map.iteratorLWWInt().shouldBeEmpty()
        map.iteratorLWWString().shouldBeEmpty()
        map.iteratorMVBoolean().shouldBeEmpty()
        map.iteratorMVDouble().shouldBeEmpty()
        map.iteratorMVInt().shouldBeEmpty()
        map.iteratorMVString().shouldBeEmpty()
        map.iteratorCntInt().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put get/iterator.
     * Call to get should return the value set by the put.
     * Call to iterator should return an iterator containing the value set by the put.
     */
    "LWW put and get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map = Map(client)

        map.putLWW(key, valueBoolean)
        map.putLWW(key, valueDouble)
        map.putLWW(key, valueInt)
        map.putLWW(key, valueString)

        map.getLWWBoolean(key).shouldBe(valueBoolean)
        map.getLWWDouble(key).shouldBe(valueDouble)
        map.getLWWInt(key).shouldBe(valueInt)
        map.getLWWString(key).shouldBe(valueString)

        val iteratorBoolean = map.iteratorLWWBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, valueBoolean))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map.iteratorLWWDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, valueDouble))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map.iteratorLWWInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, valueInt))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map.iteratorLWWString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, valueString))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "LWW put, delete, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map = Map(client)

        map.putLWW(key, valueBoolean)
        map.putLWW(key, valueDouble)
        map.putLWW(key, valueInt)
        map.putLWW(key, valueString)
        map.deleteLWWBoolean(key)
        map.deleteLWWDouble(key)
        map.deleteLWWInt(key)
        map.deleteLWWString(key)

        map.getLWWBoolean(key).shouldBeNull()
        map.getLWWDouble(key).shouldBeNull()
        map.getLWWInt(key).shouldBeNull()
        map.getLWWString(key).shouldBeNull()

        map.iteratorLWWBoolean().shouldBeEmpty()
        map.iteratorLWWDouble().shouldBeEmpty()
        map.iteratorLWWInt().shouldBeEmpty()
        map.iteratorLWWString().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: del get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "LWW delete and get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val map = Map(client)

        map.deleteLWWBoolean(key)
        map.deleteLWWDouble(key)
        map.deleteLWWInt(key)
        map.deleteLWWString(key)

        map.getLWWBoolean(key).shouldBeNull()
        map.getLWWDouble(key).shouldBeNull()
        map.getLWWInt(key).shouldBeNull()
        map.getLWWString(key).shouldBeNull()

        map.iteratorLWWBoolean().shouldBeEmpty()
        map.iteratorLWWDouble().shouldBeEmpty()
        map.iteratorLWWInt().shouldBeEmpty()
        map.iteratorLWWString().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put put get/iterator.
     * Call to get should return the value set by the second put.
     * Call to iterator should return an iterator containing the value set by the second put.
     */
    "LWW put, put, get/iterator" {
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
        val map = Map(client)

        map.putLWW(key, valBoolean1)
        map.putLWW(key, valDouble1)
        map.putLWW(key, valInt1)
        map.putLWW(key, valString1)
        map.putLWW(key, valBoolean2)
        map.putLWW(key, valDouble2)
        map.putLWW(key, valInt2)
        map.putLWW(key, valString2)

        map.getLWWBoolean(key).shouldBe(valBoolean2)
        map.getLWWDouble(key).shouldBe(valDouble2)
        map.getLWWInt(key).shouldBe(valInt2)
        map.getLWWString(key).shouldBe(valString2)

        val iteratorBoolean = map.iteratorLWWBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map.iteratorLWWDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map.iteratorLWWInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map.iteratorLWWString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put put del get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "LWW put, put, delete, get/iterator" {
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
        val map = Map(client)

        map.putLWW(key, valBoolean1)
        map.putLWW(key, valDouble1)
        map.putLWW(key, valInt1)
        map.putLWW(key, valString1)
        map.putLWW(key, valBoolean2)
        map.putLWW(key, valDouble2)
        map.putLWW(key, valInt2)
        map.putLWW(key, valString2)
        map.deleteLWWBoolean(key)
        map.deleteLWWDouble(key)
        map.deleteLWWInt(key)
        map.deleteLWWString(key)

        map.getLWWBoolean(key).shouldBeNull()
        map.getLWWDouble(key).shouldBeNull()
        map.getLWWInt(key).shouldBeNull()
        map.getLWWString(key).shouldBeNull()

        map.iteratorLWWBoolean().shouldBeEmpty()
        map.iteratorLWWDouble().shouldBeEmpty()
        map.iteratorLWWInt().shouldBeEmpty()
        map.iteratorLWWString().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || merge get/iterator.
     * Call to get should return the value set by the put registered in the first replica.
     * Call to iterator should return an iterator containing the value set by the put registered in the first replica.
     */
    "LWW R1: put; R2: merge, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = Map(client)
        val map2 = Map(client)

        map1.putLWW(key, valueBoolean)
        map1.putLWW(key, valueDouble)
        map1.putLWW(key, valueInt)
        map1.putLWW(key, valueString)
        map1.merge(map2)
        map2.merge(map1)

        map1.getLWWBoolean(key).shouldBe(valueBoolean)
        map1.getLWWDouble(key).shouldBe(valueDouble)
        map1.getLWWInt(key).shouldBe(valueInt)
        map1.getLWWString(key).shouldBe(valueString)
        map2.getLWWBoolean(key).shouldBe(valueBoolean)
        map2.getLWWDouble(key).shouldBe(valueDouble)
        map2.getLWWInt(key).shouldBe(valueInt)
        map2.getLWWString(key).shouldBe(valueString)

        val iteratorBoolean1 = map1.iteratorLWWBoolean()
        iteratorBoolean1.shouldHaveNext()
        iteratorBoolean1.next().shouldBe(Pair(key, valueBoolean))
        iteratorBoolean1.shouldBeEmpty()

        val iteratorDouble1 = map1.iteratorLWWDouble()
        iteratorDouble1.shouldHaveNext()
        iteratorDouble1.next().shouldBe(Pair(key, valueDouble))
        iteratorDouble1.shouldBeEmpty()

        val iteratorInt1 = map1.iteratorLWWInt()
        iteratorInt1.shouldHaveNext()
        iteratorInt1.next().shouldBe(Pair(key, valueInt))
        iteratorInt1.shouldBeEmpty()

        val iteratorString1 = map1.iteratorLWWString()
        iteratorString1.shouldHaveNext()
        iteratorString1.next().shouldBe(Pair(key, valueString))
        iteratorString1.shouldBeEmpty()

        val iteratorBoolean2 = map2.iteratorLWWBoolean()
        iteratorBoolean2.shouldHaveNext()
        iteratorBoolean2.next().shouldBe(Pair(key, valueBoolean))
        iteratorBoolean2.shouldBeEmpty()

        val iteratorDouble2 = map2.iteratorLWWDouble()
        iteratorDouble2.shouldHaveNext()
        iteratorDouble2.next().shouldBe(Pair(key, valueDouble))
        iteratorDouble2.shouldBeEmpty()

        val iteratorInt2 = map2.iteratorLWWInt()
        iteratorInt2.shouldHaveNext()
        iteratorInt2.next().shouldBe(Pair(key, valueInt))
        iteratorInt2.shouldBeEmpty()

        val iteratorString2 = map2.iteratorLWWString()
        iteratorString2.shouldHaveNext()
        iteratorString2.next().shouldBe(Pair(key, valueString))
        iteratorString2.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || merge putLWW get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by the put registered in the second replica.
     */
    "LWW R1: put; R2: merge, put LWW, get/iterator" {
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
        val map1 = Map(client1)
        val map2 = Map(client2)

        map1.putLWW(key, valBoolean1)
        map1.putLWW(key, valDouble1)
        map1.putLWW(key, valInt1)
        map1.putLWW(key, valString1)
        map2.merge(map1)
        map2.putLWW(key, valBoolean2)
        map2.putLWW(key, valDouble2)
        map2.putLWW(key, valInt2)
        map2.putLWW(key, valString2)

        map2.getLWWBoolean(key).shouldBe(valBoolean2)
        map2.getLWWDouble(key).shouldBe(valDouble2)
        map2.getLWWInt(key).shouldBe(valInt2)
        map2.getLWWString(key).shouldBe(valString2)

        val iteratorBoolean = map2.iteratorLWWBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorLWWDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorLWWInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorLWWString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || putLWW merge get
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by the put registered in the second replica.
     */
    "R1: put LWW | R2: put LWW ; merge R1->R2" {
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
        val map1 = Map(client1)
        val map2 = Map(client2)

        map1.putLWW(key, valBoolean1)
        map1.putLWW(key, valDouble1)
        map1.putLWW(key, valInt1)
        map1.putLWW(key, valString1)
        map2.putLWW(key, valBoolean2)
        map2.putLWW(key, valDouble2)
        map2.putLWW(key, valInt2)
        map2.putLWW(key, valString2)
        map2.merge(map1)

        map2.getLWWBoolean(key).shouldBe(valBoolean2)
        map2.getLWWDouble(key).shouldBe(valDouble2)
        map2.getLWWInt(key).shouldBe(valInt2)
        map2.getLWWString(key).shouldBe(valString2)

        val iteratorBoolean = map2.iteratorLWWBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorLWWDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorLWWInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorLWWString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: putLWW || put merge get/iterator.
     * Call to get should return the value set by put registered in the first replica.
     * Call to iterator should return an iterator containing the value set by the put registered in the first replica.
     */
    "R1: put LWW | R2: put LWW ; merge R2->R1" {
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
        val map1 = Map(client1)
        val map2 = Map(client2)

        map1.putLWW(key, valBoolean1)
        map1.putLWW(key, valDouble1)
        map1.putLWW(key, valInt1)
        map1.putLWW(key, valString1)
        map2.putLWW(key, valBoolean2)
        map2.putLWW(key, valDouble2)
        map2.putLWW(key, valInt2)
        map2.putLWW(key, valString2)
        map1.merge(map2)

        map1.getLWWBoolean(key).shouldBe(valBoolean2)
        map1.getLWWDouble(key).shouldBe(valDouble2)
        map1.getLWWInt(key).shouldBe(valInt2)
        map1.getLWWString(key).shouldBe(valString2)

        val iteratorBoolean = map1.iteratorLWWBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map1.iteratorLWWDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map1.iteratorLWWInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map1.iteratorLWWString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put delLWW || put merge get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "LWW R1: put, delete LWW; R2: put, merge, get/iterator" {
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
        val map1 = Map(client1)
        val map2 = Map(client2)

        map2.putLWW(key, valBoolean2)
        map2.putLWW(key, valDouble2)
        map2.putLWW(key, valInt2)
        map2.putLWW(key, valString2)
        map1.putLWW(key, valBoolean1)
        map1.putLWW(key, valDouble1)
        map1.putLWW(key, valInt1)
        map1.putLWW(key, valString1)
        map1.deleteLWWBoolean(key)
        map1.deleteLWWDouble(key)
        map1.deleteLWWInt(key)
        map1.deleteLWWString(key)
        map2.merge(map1)

        map2.getLWWBoolean(key).shouldBeNull()
        map2.getLWWDouble(key).shouldBeNull()
        map2.getLWWInt(key).shouldBeNull()
        map2.getLWWString(key).shouldBeNull()

        map2.iteratorLWWBoolean().shouldBeEmpty()
        map2.iteratorLWWDouble().shouldBeEmpty()
        map2.iteratorLWWInt().shouldBeEmpty()
        map2.iteratorLWWString().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put delLWW || put merge(before del) merge(after del) get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "LWW R1: put, delete LWW; R2: put, merge before delete, merge after delete, get/iterator" {
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
        val map1 = Map(client1)
        val map2 = Map(client2)

        map2.putLWW(key, valBoolean2)
        map2.putLWW(key, valDouble2)
        map2.putLWW(key, valInt2)
        map2.putLWW(key, valString2)
        map1.putLWW(key, valBoolean1)
        map1.putLWW(key, valDouble1)
        map1.putLWW(key, valInt1)
        map1.putLWW(key, valString1)
        map2.merge(map1)
        map1.deleteLWWBoolean(key)
        map1.deleteLWWDouble(key)
        map1.deleteLWWInt(key)
        map1.deleteLWWString(key)
        map2.merge(map1)

        map2.getLWWBoolean(key).shouldBeNull()
        map2.getLWWDouble(key).shouldBeNull()
        map2.getLWWInt(key).shouldBeNull()
        map2.getLWWString(key).shouldBeNull()

        map2.iteratorLWWBoolean().shouldBeEmpty()
        map2.iteratorLWWDouble().shouldBeEmpty()
        map2.iteratorLWWInt().shouldBeEmpty()
        map2.iteratorLWWString().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del || putLWW merge get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by put registered in the second replica.
     */
    "LWW R1: put, delete; R2: put LWW, merge, get/iterator" {
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
        val map1 = Map(client1)
        val map2 = Map(client2)

        map1.putLWW(key, valBoolean1)
        map1.putLWW(key, valDouble1)
        map1.putLWW(key, valInt1)
        map1.putLWW(key, valString1)
        map1.deleteLWWBoolean(key)
        map1.deleteLWWDouble(key)
        map1.deleteLWWInt(key)
        map1.deleteLWWString(key)
        client2.tick()
        client2.tick()
        client2.tick()
        client2.tick()
        map2.putLWW(key, valBoolean2)
        map2.putLWW(key, valDouble2)
        map2.putLWW(key, valInt2)
        map2.putLWW(key, valString2)
        map2.merge(map1)

        map2.getLWWBoolean(key).shouldBe(valBoolean2)
        map2.getLWWDouble(key).shouldBe(valDouble2)
        map2.getLWWInt(key).shouldBe(valInt2)
        map2.getLWWString(key).shouldBe(valString2)

        val iteratorBoolean = map2.iteratorLWWBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorLWWDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorLWWInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorLWWString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del || putLWW merge(before del) merge(after del) get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by put registered in the second replica.
     */
    "LWW R1: put, delete; R2: put LWW, merge before delete, merge after delete, get/iterator" {
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
        val map1 = Map(client1)
        val map2 = Map(client2)

        map1.putLWW(key, valBoolean1)
        map1.putLWW(key, valDouble1)
        map1.putLWW(key, valInt1)
        map1.putLWW(key, valString1)
        client2.tick()
        client2.tick()
        client2.tick()
        client2.tick()
        map2.putLWW(key, valBoolean2)
        map2.putLWW(key, valDouble2)
        map2.putLWW(key, valInt2)
        map2.putLWW(key, valString2)
        map2.merge(map1)
        map1.deleteLWWBoolean(key)
        map1.deleteLWWDouble(key)
        map1.deleteLWWInt(key)
        map1.deleteLWWString(key)
        map2.merge(map1)

        map2.getLWWBoolean(key).shouldBe(valBoolean2)
        map2.getLWWDouble(key).shouldBe(valDouble2)
        map2.getLWWInt(key).shouldBe(valInt2)
        map2.getLWWString(key).shouldBe(valString2)

        val iteratorBoolean = map2.iteratorLWWBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorLWWDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorLWWInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorLWWString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || put || merge1 delLWW merge2 get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "LWW: R1 put ; merge R1->R3 ; R3 delete | R2 put ; merge R2->R3" {
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
        val map1 = Map(client1)
        val map2 = Map(client2)
        val map3 = Map(client3)

        map1.putLWW(key, valBoolean1)
        map1.putLWW(key, valDouble1)
        map1.putLWW(key, valInt1)
        map1.putLWW(key, valString1)
        map3.merge(map1)
        map2.putLWW(key, valBoolean2)
        map2.putLWW(key, valDouble2)
        map2.putLWW(key, valInt2)
        map2.putLWW(key, valString2)
        // client3 has a larger uid: next deletes win
        map3.deleteLWWBoolean(key)
        map3.deleteLWWDouble(key)
        map3.deleteLWWInt(key)
        map3.deleteLWWString(key)
        map3.merge(map2)

        map3.getLWWBoolean(key).shouldBeNull()
        map3.getLWWDouble(key).shouldBeNull()
        map3.getLWWInt(key).shouldBeNull()
        map3.getLWWString(key).shouldBeNull()

        map3.iteratorLWWBoolean().shouldBeEmpty()
        map3.iteratorLWWDouble().shouldBeEmpty()
        map3.iteratorLWWInt().shouldBeEmpty()
        map3.iteratorLWWString().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: putLWW || put || merge1 del merge2 get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by put registered in the second replica.
     */
    "LWW: R1 put ; merge R1->R2 ; R2 delete | R3 put ; merge R3->R2" {
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
        val map1 = Map(client1)
        val map2 = Map(client2)
        val map3 = Map(client3)

        map1.putLWW(key, valBoolean1)
        map1.putLWW(key, valDouble1)
        map1.putLWW(key, valInt1)
        map1.putLWW(key, valString1)
        map2.merge(map1)
        map2.deleteLWWBoolean(key)
        map2.deleteLWWDouble(key)
        map2.deleteLWWInt(key)
        map2.deleteLWWString(key)
        // client3 has a larger uid: next puts win
        map3.putLWW(key, valBoolean2)
        map3.putLWW(key, valDouble2)
        map3.putLWW(key, valInt2)
        map3.putLWW(key, valString2)
        map2.merge(map3)

        map2.getLWWBoolean(key).shouldBe(valBoolean2)
        map2.getLWWDouble(key).shouldBe(valDouble2)
        map2.getLWWInt(key).shouldBe(valInt2)
        map2.getLWWString(key).shouldBe(valString2)

        val iteratorBoolean = map3.iteratorLWWBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, valBoolean2))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map3.iteratorLWWDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, valDouble2))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map3.iteratorLWWInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, valInt2))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map3.iteratorLWWString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, valString2))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the use of deltas return by call to put method.
     * Call to get should return the value set by put registered in the first replica.
     * Call to iterator should return an iterator containing the value set by put registered in the first replica.
     */
    "LWW use deltas returned by put" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = Map(client)
        val map2 = Map(client)

        val opBoolean = map1.putLWW(key, valueBoolean)
        val opDouble = map1.putLWW(key, valueDouble)
        val opInt = map1.putLWW(key, valueInt)
        val opString = map1.putLWW(key, valueString)
        map1.merge(opBoolean)
        map1.merge(opDouble)
        map1.merge(opInt)
        map1.merge(opString)
        map2.merge(opBoolean)
        map2.merge(opDouble)
        map2.merge(opInt)
        map2.merge(opString)

        map1.getLWWBoolean(key).shouldBe(valueBoolean)
        map1.getLWWDouble(key).shouldBe(valueDouble)
        map1.getLWWInt(key).shouldBe(valueInt)
        map1.getLWWString(key).shouldBe(valueString)
        map2.getLWWBoolean(key).shouldBe(valueBoolean)
        map2.getLWWDouble(key).shouldBe(valueDouble)
        map2.getLWWInt(key).shouldBe(valueInt)
        map2.getLWWString(key).shouldBe(valueString)

        val iteratorBoolean1 = map1.iteratorLWWBoolean()
        iteratorBoolean1.shouldHaveNext()
        iteratorBoolean1.next().shouldBe(Pair(key, valueBoolean))
        iteratorBoolean1.shouldBeEmpty()

        val iteratorDouble1 = map1.iteratorLWWDouble()
        iteratorDouble1.shouldHaveNext()
        iteratorDouble1.next().shouldBe(Pair(key, valueDouble))
        iteratorDouble1.shouldBeEmpty()

        val iteratorInt1 = map1.iteratorLWWInt()
        iteratorInt1.shouldHaveNext()
        iteratorInt1.next().shouldBe(Pair(key, valueInt))
        iteratorInt1.shouldBeEmpty()

        val iteratorString1 = map1.iteratorLWWString()
        iteratorString1.shouldHaveNext()
        iteratorString1.next().shouldBe(Pair(key, valueString))
        iteratorString1.shouldBeEmpty()

        val iteratorBoolean2 = map2.iteratorLWWBoolean()
        iteratorBoolean2.shouldHaveNext()
        iteratorBoolean2.next().shouldBe(Pair(key, valueBoolean))
        iteratorBoolean2.shouldBeEmpty()

        val iteratorDouble2 = map2.iteratorLWWDouble()
        iteratorDouble2.shouldHaveNext()
        iteratorDouble2.next().shouldBe(Pair(key, valueDouble))
        iteratorDouble2.shouldBeEmpty()

        val iteratorInt2 = map2.iteratorLWWInt()
        iteratorInt2.shouldHaveNext()
        iteratorInt2.next().shouldBe(Pair(key, valueInt))
        iteratorInt2.shouldBeEmpty()

        val iteratorString2 = map2.iteratorLWWString()
        iteratorString2.shouldHaveNext()
        iteratorString2.next().shouldBe(Pair(key, valueString))
        iteratorString2.shouldBeEmpty()
    }

    /**
     * This test evaluates the use of deltas return by call to put and delete methods.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "LWW use deltas returned by put and delete" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = Map(client)
        val map2 = Map(client)

        val putOpBoolean = map1.putLWW(key, valueBoolean)
        val putOpDouble = map1.putLWW(key, valueDouble)
        val putOpInt = map1.putLWW(key, valueInt)
        val putOpString = map1.putLWW(key, valueString)
        val delOpBoolean = map1.deleteLWWBoolean(key)
        val delOpDouble = map1.deleteLWWDouble(key)
        val delOpInt = map1.deleteLWWInt(key)
        val delOpString = map1.deleteLWWString(key)
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

        map1.getLWWBoolean(key).shouldBeNull()
        map1.getLWWDouble(key).shouldBeNull()
        map1.getLWWInt(key).shouldBeNull()
        map1.getLWWString(key).shouldBeNull()
        map2.getLWWBoolean(key).shouldBeNull()
        map2.getLWWDouble(key).shouldBeNull()
        map2.getLWWInt(key).shouldBeNull()
        map2.getLWWString(key).shouldBeNull()

        map1.iteratorLWWBoolean().shouldBeEmpty()
        map1.iteratorLWWDouble().shouldBeEmpty()
        map1.iteratorLWWInt().shouldBeEmpty()
        map1.iteratorLWWString().shouldBeEmpty()
        map2.iteratorLWWBoolean().shouldBeEmpty()
        map2.iteratorLWWDouble().shouldBeEmpty()
        map2.iteratorLWWInt().shouldBeEmpty()
        map2.iteratorLWWString().shouldBeEmpty()
    }

    /**
     * This test evaluates the merge of deltas return by call to put method.
     * Call to get should return the values set by puts registered in the first replica.
     * Call to iterator should return an empty iterator containing the values set by puts registered in the first replica.
     */
    "LWW merge deltas returned by put operations" {
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
        val map1 = Map(client)
        val map2 = Map(client)

        val opBoolean1 = map1.putLWW(key1, valBoolean1)
        val opDouble1 = map1.putLWW(key1, valDouble1)
        val opInt1 = map1.putLWW(key1, valInt1)
        val opString1 = map1.putLWW(key1, valString1)
        val opBoolean2 = map1.putLWW(key1, valBoolean2)
        val opDouble2 = map1.putLWW(key1, valDouble2)
        val opInt2 = map1.putLWW(key1, valInt2)
        val opString2 = map1.putLWW(key1, valString2)
        val opBoolean3 = map1.putLWW(key2, valBoolean1)
        val opDouble3 = map1.putLWW(key2, valDouble1)
        val opInt3 = map1.putLWW(key2, valInt1)
        val opString3 = map1.putLWW(key2, valString1)
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

        map2.getLWWBoolean(key1).shouldBe(valBoolean2)
        map2.getLWWDouble(key1).shouldBe(valDouble2)
        map2.getLWWInt(key1).shouldBe(valInt2)
        map2.getLWWString(key1).shouldBe(valString2)
        map2.getLWWBoolean(key2).shouldBe(valBoolean1)
        map2.getLWWDouble(key2).shouldBe(valDouble1)
        map2.getLWWInt(key2).shouldBe(valInt1)
        map2.getLWWString(key2).shouldBe(valString1)

        val iteratorBoolean = map2.iteratorLWWBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, valBoolean2))
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key2, valBoolean1))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorLWWDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, valDouble2))
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key2, valDouble1))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorLWWInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, valInt2))
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key2, valInt1))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorLWWString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, valString2))
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key2, valString1))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the merge of deltas return by call to put and delete methods.
     * Call to get should return the value set by put or null if it has been deleted.
     * Call to iterator should return an iterator containing the value set by put if it has been deleted.
     */
    "LWW merge deltas returned by put and delete operations" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key1 = "key1"
        val key2 = "key2"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = Map(client)
        val map2 = Map(client)

        val opBoolean1 = map1.putLWW(key1, valueBoolean)
        val opDouble1 = map1.putLWW(key1, valueDouble)
        val opInt1 = map1.putLWW(key1, valueInt)
        val opString1 = map1.putLWW(key1, valueString)
        val opBoolean2 = map1.deleteLWWBoolean(key1)
        val opDouble2 = map1.deleteLWWDouble(key1)
        val opInt2 = map1.deleteLWWInt(key1)
        val opString2 = map1.deleteLWWString(key1)
        val opBoolean3 = map1.putLWW(key2, valueBoolean)
        val opDouble3 = map1.putLWW(key2, valueDouble)
        val opInt3 = map1.putLWW(key2, valueInt)
        val opString3 = map1.putLWW(key2, valueString)
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

        map2.getLWWBoolean(key1).shouldBeNull()
        map2.getLWWDouble(key1).shouldBeNull()
        map2.getLWWInt(key1).shouldBeNull()
        map2.getLWWString(key1).shouldBeNull()
        map2.getLWWBoolean(key2).shouldBe(valueBoolean)
        map2.getLWWDouble(key2).shouldBe(valueDouble)
        map2.getLWWInt(key2).shouldBe(valueInt)
        map2.getLWWString(key2).shouldBe(valueString)

        val iteratorBoolean = map2.iteratorLWWBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key2, valueBoolean))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorLWWDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key2, valueDouble))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorLWWInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key2, valueInt))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorLWWString()
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
    "LWW generate delta" {
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
        val map1 = Map(client)
        val map2 = Map(client)

        map1.putLWW(key1, valueBoolean)
        map1.putLWW(key1, valueDouble)
        map1.putLWW(key1, valueInt)
        map1.putLWW(key1, valueString)
        map1.putLWW(key2, valueBoolean)
        map1.putLWW(key2, valueDouble)
        map1.putLWW(key2, valueInt)
        map1.putLWW(key2, valueString)
        vv.update(client.tick())
        map1.putLWW(key3, valueBoolean)
        map1.putLWW(key3, valueDouble)
        map1.putLWW(key3, valueInt)
        map1.putLWW(key3, valueString)
        map1.putLWW(key4, valueBoolean)
        map1.putLWW(key4, valueDouble)
        map1.putLWW(key4, valueInt)
        map1.putLWW(key4, valueString)
        val delta = map1.generateDelta(vv)
        map2.merge(delta)

        map2.getLWWBoolean(key1).shouldBeNull()
        map2.getLWWDouble(key1).shouldBeNull()
        map2.getLWWInt(key1).shouldBeNull()
        map2.getLWWString(key1).shouldBeNull()
        map2.getLWWBoolean(key2).shouldBeNull()
        map2.getLWWDouble(key2).shouldBeNull()
        map2.getLWWInt(key2).shouldBeNull()
        map2.getLWWString(key2).shouldBeNull()
        map2.getLWWBoolean(key3).shouldBe(valueBoolean)
        map2.getLWWDouble(key3).shouldBe(valueDouble)
        map2.getLWWInt(key3).shouldBe(valueInt)
        map2.getLWWString(key3).shouldBe(valueString)
        map2.getLWWBoolean(key4).shouldBe(valueBoolean)
        map2.getLWWDouble(key4).shouldBe(valueDouble)
        map2.getLWWInt(key4).shouldBe(valueInt)
        map2.getLWWString(key4).shouldBe(valueString)

        val iteratorBoolean = map2.iteratorLWWBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key3, valueBoolean))
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key4, valueBoolean))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorLWWDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key3, valueDouble))
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key4, valueDouble))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorLWWInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key3, valueInt))
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key4, valueInt))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorLWWString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key3, valueString))
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key4, valueString))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the generation of delta (including delete) plus its merging into another replica.
     * Call to get should return the values set by puts or null set by delete w.r.t the given context.
     * Call to iterator should return an iterator containing the values set by puts or null set by delete
     * w.r.t the given context.
     */
    "LWW generate delta with delete" {
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
        val map1 = Map(client)
        val map2 = Map(client)

        map1.putLWW(key1, valueBoolean)
        map1.putLWW(key1, valueDouble)
        map1.putLWW(key1, valueInt)
        map1.putLWW(key1, valueString)
        vv.update(client.tick())
        map1.putLWW(key2, valueBoolean)
        map1.putLWW(key2, valueDouble)
        map1.putLWW(key2, valueInt)
        map1.putLWW(key2, valueString)
        map1.deleteLWWBoolean(key2)
        map1.deleteLWWDouble(key2)
        map1.deleteLWWInt(key2)
        map1.deleteLWWString(key2)
        map1.putLWW(key3, valueBoolean)
        map1.putLWW(key3, valueDouble)
        map1.putLWW(key3, valueInt)
        map1.putLWW(key3, valueString)
        val delta = map1.generateDelta(vv)
        map2.merge(delta)

        map2.getLWWBoolean(key1).shouldBeNull()
        map2.getLWWDouble(key1).shouldBeNull()
        map2.getLWWInt(key1).shouldBeNull()
        map2.getLWWString(key1).shouldBeNull()
        map2.getLWWBoolean(key2).shouldBeNull()
        map2.getLWWDouble(key2).shouldBeNull()
        map2.getLWWInt(key2).shouldBeNull()
        map2.getLWWString(key2).shouldBeNull()
        map2.getLWWBoolean(key3).shouldBe(valueBoolean)
        map2.getLWWDouble(key3).shouldBe(valueDouble)
        map2.getLWWInt(key3).shouldBe(valueInt)
        map2.getLWWString(key3).shouldBe(valueString)

        val iteratorBoolean = map2.iteratorLWWBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key3, valueBoolean))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorLWWDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key3, valueDouble))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorLWWInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key3, valueInt))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorLWWString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key3, valueString))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put get/iterator.
     * Call to get should return the value set by the put.
     * Call to iterator should return an iterator containing the value set by the put.
     */
    "MV put and get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map = Map(client)

        map.putMV(key, valueBoolean)
        map.putMV(key, valueDouble)
        map.putMV(key, valueInt)
        map.putMV(key, valueString)

        map.getMVBoolean(key)!!.shouldHaveSingleElement(valueBoolean)
        map.getMVDouble(key)!!.shouldHaveSingleElement(valueDouble)
        map.getMVInt(key)!!.shouldHaveSingleElement(valueInt)
        map.getMVString(key)!!.shouldHaveSingleElement(valueString)

        val iteratorBoolean = map.iteratorMVBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, setOf(valueBoolean)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map.iteratorMVDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, setOf(valueDouble)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map.iteratorMVInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, setOf(valueInt)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map.iteratorMVString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, setOf(valueString)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "MV put, delete, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map = Map(client)

        map.putMV(key, valueBoolean)
        map.putMV(key, valueDouble)
        map.putMV(key, valueInt)
        map.putMV(key, valueString)
        map.deleteMVBoolean(key)
        map.deleteMVDouble(key)
        map.deleteMVInt(key)
        map.deleteMVString(key)

        map.getMVBoolean(key).shouldBeNull()
        map.getMVDouble(key).shouldBeNull()
        map.getMVInt(key).shouldBeNull()
        map.getMVString(key).shouldBeNull()

        map.iteratorMVBoolean().shouldBeEmpty()
        map.iteratorMVDouble().shouldBeEmpty()
        map.iteratorMVInt().shouldBeEmpty()
        map.iteratorMVString().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: del get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "MV delete and get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val map = Map(client)

        map.deleteMVBoolean(key)
        map.deleteMVDouble(key)
        map.deleteMVInt(key)
        map.deleteMVString(key)

        map.getMVBoolean(key).shouldBeNull()
        map.getMVDouble(key).shouldBeNull()
        map.getMVInt(key).shouldBeNull()
        map.getMVString(key).shouldBeNull()

        map.iteratorMVBoolean().shouldBeEmpty()
        map.iteratorMVDouble().shouldBeEmpty()
        map.iteratorMVInt().shouldBeEmpty()
        map.iteratorMVString().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put put get/iterator.
     * Call to get should return the value set by the second put.
     * Call to iterator should return an iterator containing the value set by the second put.
     */
    "MV put, put, get/iterator" {
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
        val map = Map(client)

        map.putMV(key, valBoolean1)
        map.putMV(key, valDouble1)
        map.putMV(key, valInt1)
        map.putMV(key, valString1)
        map.putMV(key, valBoolean2)
        map.putMV(key, valDouble2)
        map.putMV(key, valInt2)
        map.putMV(key, valString2)

        map.getMVBoolean(key)!!.shouldHaveSingleElement(valBoolean2)
        map.getMVDouble(key)!!.shouldHaveSingleElement(valDouble2)
        map.getMVInt(key)!!.shouldHaveSingleElement(valInt2)
        map.getMVString(key)!!.shouldHaveSingleElement(valString2)

        val iteratorBoolean = map.iteratorMVBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, setOf(valBoolean2)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map.iteratorMVDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, setOf(valDouble2)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map.iteratorMVInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, setOf(valInt2)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map.iteratorMVString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, setOf(valString2)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put put del get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "MV put, put, delete, get/iterator" {
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
        val map = Map(client)

        map.putMV(key, valBoolean1)
        map.putMV(key, valDouble1)
        map.putMV(key, valInt1)
        map.putMV(key, valString1)
        map.putMV(key, valBoolean2)
        map.putMV(key, valDouble2)
        map.putMV(key, valInt2)
        map.putMV(key, valString2)
        map.deleteMVBoolean(key)
        map.deleteMVDouble(key)
        map.deleteMVInt(key)
        map.deleteMVString(key)

        map.getMVBoolean(key).shouldBeNull()
        map.getMVDouble(key).shouldBeNull()
        map.getMVInt(key).shouldBeNull()
        map.getMVString(key).shouldBeNull()

        map.iteratorMVBoolean().shouldBeEmpty()
        map.iteratorMVDouble().shouldBeEmpty()
        map.iteratorMVInt().shouldBeEmpty()
        map.iteratorMVString().shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || merge get/iterator.
     * Call to get should return the value set by the put registered in the first replica.
     * Call to iterator should return an iterator containing the value set by the put registered in the first replica.
     */
    "MV R1: put; R2: merge, get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = Map(client)
        val map2 = Map(client)

        map1.putMV(key, valueBoolean)
        map1.putMV(key, valueDouble)
        map1.putMV(key, valueInt)
        map1.putMV(key, valueString)
        map1.merge(map2)
        map2.merge(map1)

        map1.getMVBoolean(key)!!.shouldHaveSingleElement(valueBoolean)
        map1.getMVDouble(key)!!.shouldHaveSingleElement(valueDouble)
        map1.getMVInt(key)!!.shouldHaveSingleElement(valueInt)
        map1.getMVString(key)!!.shouldHaveSingleElement(valueString)
        map2.getMVBoolean(key)!!.shouldHaveSingleElement(valueBoolean)
        map2.getMVDouble(key)!!.shouldHaveSingleElement(valueDouble)
        map2.getMVInt(key)!!.shouldHaveSingleElement(valueInt)
        map2.getMVString(key)!!.shouldHaveSingleElement(valueString)

        val iteratorBoolean1 = map1.iteratorMVBoolean()
        iteratorBoolean1.shouldHaveNext()
        iteratorBoolean1.next().shouldBe(Pair(key, setOf(valueBoolean)))
        iteratorBoolean1.shouldBeEmpty()

        val iteratorDouble1 = map1.iteratorMVDouble()
        iteratorDouble1.shouldHaveNext()
        iteratorDouble1.next().shouldBe(Pair(key, setOf(valueDouble)))
        iteratorDouble1.shouldBeEmpty()

        val iteratorInt1 = map1.iteratorMVInt()
        iteratorInt1.shouldHaveNext()
        iteratorInt1.next().shouldBe(Pair(key, setOf(valueInt)))
        iteratorInt1.shouldBeEmpty()

        val iteratorString1 = map1.iteratorMVString()
        iteratorString1.shouldHaveNext()
        iteratorString1.next().shouldBe(Pair(key, setOf(valueString)))
        iteratorString1.shouldBeEmpty()

        val iteratorBoolean2 = map2.iteratorMVBoolean()
        iteratorBoolean2.shouldHaveNext()
        iteratorBoolean2.next().shouldBe(Pair(key, setOf(valueBoolean)))
        iteratorBoolean2.shouldBeEmpty()

        val iteratorDouble2 = map2.iteratorMVDouble()
        iteratorDouble2.shouldHaveNext()
        iteratorDouble2.next().shouldBe(Pair(key, setOf(valueDouble)))
        iteratorDouble2.shouldBeEmpty()

        val iteratorInt2 = map2.iteratorMVInt()
        iteratorInt2.shouldHaveNext()
        iteratorInt2.next().shouldBe(Pair(key, setOf(valueInt)))
        iteratorInt2.shouldBeEmpty()

        val iteratorString2 = map2.iteratorMVString()
        iteratorString2.shouldHaveNext()
        iteratorString2.next().shouldBe(Pair(key, setOf(valueString)))
        iteratorString2.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || merge put get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by put registered in the second replica.
     */
    "MV R1: put; R2: merge, put, get/iterator" {
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
        val map1 = Map(client1)
        val map2 = Map(client2)

        map1.putMV(key, valBoolean1)
        map1.putMV(key, valDouble1)
        map1.putMV(key, valInt1)
        map1.putMV(key, valString1)
        map2.merge(map1)
        map2.putMV(key, valBoolean2)
        map2.putMV(key, valDouble2)
        map2.putMV(key, valInt2)
        map2.putMV(key, valString2)

        map2.getMVBoolean(key)!!.shouldHaveSingleElement(valBoolean2)
        map2.getMVDouble(key)!!.shouldHaveSingleElement(valDouble2)
        map2.getMVInt(key)!!.shouldHaveSingleElement(valInt2)
        map2.getMVString(key)!!.shouldHaveSingleElement(valString2)

        val iteratorBoolean = map2.iteratorMVBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, setOf(valBoolean2)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorMVDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, setOf(valDouble2)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorMVInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, setOf(valInt2)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorMVString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, setOf(valString2)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || put merge get
     * Call to get should return a set containing the two concurrently put values.
     * Call to iterator should return an iterator containing the two concurrently put values.
     */
    "MV R1: put; R2: put, merge, get/iterator" {
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
        val map1 = Map(client1)
        val map2 = Map(client2)

        map1.putMV(key, valBoolean1)
        map1.putMV(key, valDouble1)
        map1.putMV(key, valInt1)
        map1.putMV(key, valString1)
        map2.putMV(key, valBoolean2)
        map2.putMV(key, valDouble2)
        map2.putMV(key, valInt2)
        map2.putMV(key, valString2)
        map2.merge(map1)

        map2.getMVBoolean(key)!!.shouldContainExactlyInAnyOrder(valBoolean1, valBoolean2)
        map2.getMVDouble(key)!!.shouldContainExactlyInAnyOrder(valDouble1, valDouble2)
        map2.getMVInt(key)!!.shouldContainExactlyInAnyOrder(valInt1, valInt2)
        map2.getMVString(key)!!.shouldContainExactlyInAnyOrder(valString1, valString2)

        val iteratorBoolean = map2.iteratorMVBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, setOf(valBoolean1, valBoolean2)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorMVDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, setOf(valDouble1, valDouble2)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorMVInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, setOf(valInt1, valInt2)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorMVString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, setOf(valString1, valString2)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del || put (with older timestamp) merge get/iterator.
     * Call to get should return a set containing the value set in the second replica.
     * Call to iterator should return an iterator containing the value set in the second replica.
     */
    "MV R1: put, delete; R2: put with older timestamp, merge, get/iterator" {
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
        val map1 = Map(client1)
        val map2 = Map(client2)

        map2.putMV(key, valBoolean2)
        map2.putMV(key, valDouble2)
        map2.putMV(key, valInt2)
        map2.putMV(key, valString2)
        map1.putMV(key, valBoolean1)
        map1.putMV(key, valDouble1)
        map1.putMV(key, valInt1)
        map1.putMV(key, valString1)
        map1.deleteMVBoolean(key)
        map1.deleteMVDouble(key)
        map1.deleteMVInt(key)
        map1.deleteMVString(key)
        map2.merge(map1)

        map2.getMVBoolean(key)!!.shouldContainExactlyInAnyOrder(valBoolean2, null)
        map2.getMVDouble(key)!!.shouldContainExactlyInAnyOrder(valDouble2, null)
        map2.getMVInt(key)!!.shouldContainExactlyInAnyOrder(valInt2, null)
        map2.getMVString(key)!!.shouldContainExactlyInAnyOrder(valString2, null)

        val iteratorBoolean = map2.iteratorMVBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, setOf(valBoolean2, null)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorMVDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, setOf(valDouble2, null)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorMVInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, setOf(valInt2, null)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorMVString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, setOf(valString2, null)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del || put(with older timestamp) merge(before del)
     * merge(after del) get/iterator.
     * Call to get should return a set containing the value set in the second replica.
     * Call to iterator should return an iterator containing the value set in the second replica.
     */
    "MV R1: put, delete; R2: put with older timestamp, merge before delete, merge after delete, get/iterator" {
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
        val map1 = Map(client1)
        val map2 = Map(client2)

        map2.putMV(key, valBoolean2)
        map2.putMV(key, valDouble2)
        map2.putMV(key, valInt2)
        map2.putMV(key, valString2)
        map1.putMV(key, valBoolean1)
        map1.putMV(key, valDouble1)
        map1.putMV(key, valInt1)
        map1.putMV(key, valString1)
        map2.merge(map1)
        map1.deleteMVBoolean(key)
        map1.deleteMVDouble(key)
        map1.deleteMVInt(key)
        map1.deleteMVString(key)
        map2.merge(map1)

        map2.getMVBoolean(key)!!.shouldContainExactlyInAnyOrder(valBoolean2, null)
        map2.getMVDouble(key)!!.shouldContainExactlyInAnyOrder(valDouble2, null)
        map2.getMVInt(key)!!.shouldContainExactlyInAnyOrder(valInt2, null)
        map2.getMVString(key)!!.shouldContainExactlyInAnyOrder(valString2, null)

        val iteratorBoolean = map2.iteratorMVBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, setOf(valBoolean2, null)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorMVDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, setOf(valDouble2, null)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorMVInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, setOf(valInt2, null)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorMVString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, setOf(valString2, null)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del || put(with newer timestamp) merge get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by put registered in the second replica.
     */
    "MV R1: put, delete; R2: put with newer timestamp, merge, get/iterator" {
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
        val map1 = Map(client1)
        val map2 = Map(client2)

        map1.putMV(key, valBoolean1)
        map1.putMV(key, valDouble1)
        map1.putMV(key, valInt1)
        map1.putMV(key, valString1)
        map1.deleteMVBoolean(key)
        map1.deleteMVDouble(key)
        map1.deleteMVInt(key)
        map1.deleteMVString(key)
        client2.tick()
        client2.tick()
        client2.tick()
        client2.tick()
        map2.putMV(key, valBoolean2)
        map2.putMV(key, valDouble2)
        map2.putMV(key, valInt2)
        map2.putMV(key, valString2)
        map2.merge(map1)

        map2.getMVBoolean(key)!!.shouldContainExactlyInAnyOrder(valBoolean2, null)
        map2.getMVDouble(key)!!.shouldContainExactlyInAnyOrder(valDouble2, null)
        map2.getMVInt(key)!!.shouldContainExactlyInAnyOrder(valInt2, null)
        map2.getMVString(key)!!.shouldContainExactlyInAnyOrder(valString2, null)

        val iteratorBoolean = map2.iteratorMVBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, setOf(valBoolean2, null)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorMVDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, setOf(valDouble2, null)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorMVInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, setOf(valInt2, null)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorMVString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, setOf(valString2, null)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put del || put(with newer timestamp) merge(before del)
     * merge(after del) get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by put registered in the second replica.
     */
    "MV R1: put, delete; R2: put with newer timestamp, merge before delete, merge after, delete, get/iterator" {
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
        val map1 = Map(client1)
        val map2 = Map(client2)

        map1.putMV(key, valBoolean1)
        map1.putMV(key, valDouble1)
        map1.putMV(key, valInt1)
        map1.putMV(key, valString1)
        client2.tick()
        client2.tick()
        client2.tick()
        client2.tick()
        map2.putMV(key, valBoolean2)
        map2.putMV(key, valDouble2)
        map2.putMV(key, valInt2)
        map2.putMV(key, valString2)
        map2.merge(map1)
        map1.deleteMVBoolean(key)
        map1.deleteMVDouble(key)
        map1.deleteMVInt(key)
        map1.deleteMVString(key)
        map2.merge(map1)

        map2.getMVBoolean(key)!!.shouldContainExactlyInAnyOrder(valBoolean2, null)
        map2.getMVDouble(key)!!.shouldContainExactlyInAnyOrder(valDouble2, null)
        map2.getMVInt(key)!!.shouldContainExactlyInAnyOrder(valInt2, null)
        map2.getMVString(key)!!.shouldContainExactlyInAnyOrder(valString2, null)

        val iteratorBoolean = map2.iteratorMVBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, setOf(valBoolean2, null)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorMVDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, setOf(valDouble2, null)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorMVInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, setOf(valInt2, null)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorMVString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, setOf(valString2, null)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: put || put || merge1 del merge2 get/iterator.
     * Call to get should return the value set by put registered in the second replica.
     * Call to iterator should return an iterator containing the value set by put registered in the second replica.
     */
    "MV R1: put; R2: put; R3: merge R1, delete, merge R2, get/iterator" {
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
        val map1 = Map(client1)
        val map2 = Map(client2)
        val map3 = Map(client3)

        map1.putMV(key, valBoolean1)
        map1.putMV(key, valDouble1)
        map1.putMV(key, valInt1)
        map1.putMV(key, valString1)
        map3.merge(map1)
        map2.putMV(key, valBoolean2)
        map2.putMV(key, valDouble2)
        map2.putMV(key, valInt2)
        map2.putMV(key, valString2)
        map3.deleteMVBoolean(key)
        map3.deleteMVDouble(key)
        map3.deleteMVInt(key)
        map3.deleteMVString(key)
        map3.merge(map2)

        map3.getMVBoolean(key)!!.shouldContainExactlyInAnyOrder(valBoolean2, null)
        map3.getMVDouble(key)!!.shouldContainExactlyInAnyOrder(valDouble2, null)
        map3.getMVInt(key)!!.shouldContainExactlyInAnyOrder(valInt2, null)
        map3.getMVString(key)!!.shouldContainExactlyInAnyOrder(valString2, null)

        val iteratorBoolean = map3.iteratorMVBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key, setOf(valBoolean2, null)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map3.iteratorMVDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key, setOf(valDouble2, null)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map3.iteratorMVInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key, setOf(valInt2, null)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map3.iteratorMVString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key, setOf(valString2, null)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the use of deltas return by call to put method.
     * Call to get should return the value set by put registered in the first replica.
     * Call to iterator should return an iterator containing the value set by put registered in the first replica.
     */
    "MV use deltas returned by put" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = Map(client)
        val map2 = Map(client)

        val opBoolean = map1.putMV(key, valueBoolean)
        val opDouble = map1.putMV(key, valueDouble)
        val opInt = map1.putMV(key, valueInt)
        val opString = map1.putMV(key, valueString)
        map1.merge(opBoolean)
        map1.merge(opDouble)
        map1.merge(opInt)
        map1.merge(opString)
        map2.merge(opBoolean)
        map2.merge(opDouble)
        map2.merge(opInt)
        map2.merge(opString)

        map1.getMVBoolean(key)!!.shouldHaveSingleElement(valueBoolean)
        map1.getMVDouble(key)!!.shouldHaveSingleElement(valueDouble)
        map1.getMVInt(key)!!.shouldHaveSingleElement(valueInt)
        map1.getMVString(key)!!.shouldHaveSingleElement(valueString)
        map2.getMVBoolean(key)!!.shouldHaveSingleElement(valueBoolean)
        map2.getMVDouble(key)!!.shouldHaveSingleElement(valueDouble)
        map2.getMVInt(key)!!.shouldHaveSingleElement(valueInt)
        map2.getMVString(key)!!.shouldHaveSingleElement(valueString)

        val iteratorBoolean1 = map1.iteratorMVBoolean()
        iteratorBoolean1.shouldHaveNext()
        iteratorBoolean1.next().shouldBe(Pair(key, setOf(valueBoolean)))
        iteratorBoolean1.shouldBeEmpty()

        val iteratorDouble1 = map1.iteratorMVDouble()
        iteratorDouble1.shouldHaveNext()
        iteratorDouble1.next().shouldBe(Pair(key, setOf(valueDouble)))
        iteratorDouble1.shouldBeEmpty()

        val iteratorInt1 = map1.iteratorMVInt()
        iteratorInt1.shouldHaveNext()
        iteratorInt1.next().shouldBe(Pair(key, setOf(valueInt)))
        iteratorInt1.shouldBeEmpty()

        val iteratorString1 = map1.iteratorMVString()
        iteratorString1.shouldHaveNext()
        iteratorString1.next().shouldBe(Pair(key, setOf(valueString)))
        iteratorString1.shouldBeEmpty()

        val iteratorBoolean2 = map2.iteratorMVBoolean()
        iteratorBoolean2.shouldHaveNext()
        iteratorBoolean2.next().shouldBe(Pair(key, setOf(valueBoolean)))
        iteratorBoolean2.shouldBeEmpty()

        val iteratorDouble2 = map2.iteratorMVDouble()
        iteratorDouble2.shouldHaveNext()
        iteratorDouble2.next().shouldBe(Pair(key, setOf(valueDouble)))
        iteratorDouble2.shouldBeEmpty()

        val iteratorInt2 = map2.iteratorMVInt()
        iteratorInt2.shouldHaveNext()
        iteratorInt2.next().shouldBe(Pair(key, setOf(valueInt)))
        iteratorInt2.shouldBeEmpty()

        val iteratorString2 = map2.iteratorMVString()
        iteratorString2.shouldHaveNext()
        iteratorString2.next().shouldBe(Pair(key, setOf(valueString)))
        iteratorString2.shouldBeEmpty()
    }

    /**
     * This test evaluates the use of deltas return by call to put and delete methods.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    "MV use deltas returned by put and delete" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = Map(client)
        val map2 = Map(client)

        val putOpBoolean = map1.putMV(key, valueBoolean)
        val putOpDouble = map1.putMV(key, valueDouble)
        val putOpInt = map1.putMV(key, valueInt)
        val putOpString = map1.putMV(key, valueString)
        val delOpBoolean = map1.deleteMVBoolean(key)
        val delOpDouble = map1.deleteMVDouble(key)
        val delOpInt = map1.deleteMVInt(key)
        val delOpString = map1.deleteMVString(key)
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

        map1.getMVBoolean(key).shouldBeNull()
        map1.getMVDouble(key).shouldBeNull()
        map1.getMVInt(key).shouldBeNull()
        map1.getMVString(key).shouldBeNull()
        map2.getMVBoolean(key).shouldBeNull()
        map2.getMVDouble(key).shouldBeNull()
        map2.getMVInt(key).shouldBeNull()
        map2.getMVString(key).shouldBeNull()

        map1.iteratorMVBoolean().shouldBeEmpty()
        map1.iteratorMVDouble().shouldBeEmpty()
        map1.iteratorMVInt().shouldBeEmpty()
        map1.iteratorMVString().shouldBeEmpty()
        map2.iteratorMVBoolean().shouldBeEmpty()
        map2.iteratorMVDouble().shouldBeEmpty()
        map2.iteratorMVInt().shouldBeEmpty()
        map2.iteratorMVString().shouldBeEmpty()
    }

    /**
     * This test evaluates the merge of deltas return by call to put method.
     * Call to get should return the values set by puts registered in the first replica.
     * Call to iterator should return an iterator containing the values set by puts registered in the first replica.
     */
    "MV merge deltas returned by put operations" {
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
        val map1 = Map(client)
        val map2 = Map(client)

        val opBoolean1 = map1.putMV(key1, valBoolean1)
        val opDouble1 = map1.putMV(key1, valDouble1)
        val opInt1 = map1.putMV(key1, valInt1)
        val opString1 = map1.putMV(key1, valString1)
        val opBoolean2 = map1.putMV(key1, valBoolean2)
        val opDouble2 = map1.putMV(key1, valDouble2)
        val opInt2 = map1.putMV(key1, valInt2)
        val opString2 = map1.putMV(key1, valString2)
        val opBoolean3 = map1.putMV(key2, valBoolean1)
        val opDouble3 = map1.putMV(key2, valDouble1)
        val opInt3 = map1.putMV(key2, valInt1)
        val opString3 = map1.putMV(key2, valString1)
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

        map2.getMVBoolean(key1)!!.shouldHaveSingleElement(valBoolean2)
        map2.getMVDouble(key1)!!.shouldHaveSingleElement(valDouble2)
        map2.getMVInt(key1)!!.shouldHaveSingleElement(valInt2)
        map2.getMVString(key1)!!.shouldHaveSingleElement(valString2)
        map2.getMVBoolean(key2)!!.shouldHaveSingleElement(valBoolean1)
        map2.getMVDouble(key2)!!.shouldHaveSingleElement(valDouble1)
        map2.getMVInt(key2)!!.shouldHaveSingleElement(valInt1)
        map2.getMVString(key2)!!.shouldHaveSingleElement(valString1)

        val iteratorBoolean = map2.iteratorMVBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key1, setOf(valBoolean2)))
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key2, setOf(valBoolean1)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorMVDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key1, setOf(valDouble2)))
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key2, setOf(valDouble1)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorMVInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key1, setOf(valInt2)))
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key2, setOf(valInt1)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorMVString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key1, setOf(valString2)))
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key2, setOf(valString1)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the merge of deltas return by call to put and delete methods.
     * Call to get should return the value set by put or null if it has been deleted.
     * Call to iterator should return an iterator containing the value set by put if it has been deleted.
     */
    "MV merge deltas returned by put and delete operations" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val key1 = "key1"
        val key2 = "key2"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = Map(client)
        val map2 = Map(client)

        val opBoolean1 = map1.putMV(key1, valueBoolean)
        val opDouble1 = map1.putMV(key1, valueDouble)
        val opInt1 = map1.putMV(key1, valueInt)
        val opString1 = map1.putMV(key1, valueString)
        val opBoolean2 = map1.deleteMVBoolean(key1)
        val opDouble2 = map1.deleteMVDouble(key1)
        val opInt2 = map1.deleteMVInt(key1)
        val opString2 = map1.deleteMVString(key1)
        val opBoolean3 = map1.putMV(key2, valueBoolean)
        val opDouble3 = map1.putMV(key2, valueDouble)
        val opInt3 = map1.putMV(key2, valueInt)
        val opString3 = map1.putMV(key2, valueString)
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

        map2.getMVBoolean(key1).shouldBeNull()
        map2.getMVDouble(key1).shouldBeNull()
        map2.getMVInt(key1).shouldBeNull()
        map2.getMVString(key1).shouldBeNull()
        map2.getMVBoolean(key2)!!.shouldHaveSingleElement(valueBoolean)
        map2.getMVDouble(key2)!!.shouldHaveSingleElement(valueDouble)
        map2.getMVInt(key2)!!.shouldHaveSingleElement(valueInt)
        map2.getMVString(key2)!!.shouldHaveSingleElement(valueString)

        val iteratorBoolean = map2.iteratorMVBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key2, setOf(valueBoolean)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorMVDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key2, setOf(valueDouble)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorMVInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key2, setOf(valueInt)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorMVString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key2, setOf(valueString)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the generation of delta (only put) plus its merging into another replica.
     * Call to get should return the values set by puts registered in the first replica after w.r.t
     * the given context.
     * Call to iterator should return an iterator containing the values set by puts registered in the first replica
     * after w.r.t the given context.
     */
    "MV generate delta" {
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
        val map1 = Map(client)
        val map2 = Map(client)

        map1.putMV(key1, valueBoolean)
        map1.putMV(key1, valueDouble)
        map1.putMV(key1, valueInt)
        map1.putMV(key1, valueString)
        map1.putMV(key2, valueBoolean)
        map1.putMV(key2, valueDouble)
        map1.putMV(key2, valueInt)
        map1.putMV(key2, valueString)
        vv.update(client.tick())
        map1.putMV(key3, valueBoolean)
        map1.putMV(key3, valueDouble)
        map1.putMV(key3, valueInt)
        map1.putMV(key3, valueString)
        map1.putMV(key4, valueBoolean)
        map1.putMV(key4, valueDouble)
        map1.putMV(key4, valueInt)
        map1.putMV(key4, valueString)
        val delta = map1.generateDelta(vv)
        map2.merge(delta)

        map2.getMVBoolean(key1).shouldBeNull()
        map2.getMVDouble(key1).shouldBeNull()
        map2.getMVInt(key1).shouldBeNull()
        map2.getMVString(key1).shouldBeNull()
        map2.getMVBoolean(key2).shouldBeNull()
        map2.getMVDouble(key2).shouldBeNull()
        map2.getMVInt(key2).shouldBeNull()
        map2.getMVString(key2).shouldBeNull()
        map2.getMVBoolean(key3)!!.shouldHaveSingleElement(valueBoolean)
        map2.getMVDouble(key3)!!.shouldHaveSingleElement(valueDouble)
        map2.getMVInt(key3)!!.shouldHaveSingleElement(valueInt)
        map2.getMVString(key3)!!.shouldHaveSingleElement(valueString)
        map2.getMVBoolean(key4)!!.shouldHaveSingleElement(valueBoolean)
        map2.getMVDouble(key4)!!.shouldHaveSingleElement(valueDouble)
        map2.getMVInt(key4)!!.shouldHaveSingleElement(valueInt)
        map2.getMVString(key4)!!.shouldHaveSingleElement(valueString)

        val iteratorBoolean = map2.iteratorMVBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key3, setOf(valueBoolean)))
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key4, setOf(valueBoolean)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorMVDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key3, setOf(valueDouble)))
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key4, setOf(valueDouble)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorMVInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key3, setOf(valueInt)))
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key4, setOf(valueInt)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorMVString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key3, setOf(valueString)))
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key4, setOf(valueString)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the generation of delta (including delete) plus its merging into another replica.
     * Call to get should return the values set by puts or null set by delete w.r.t the given context.
     * Call to iterator should return an iterator containing the values set by puts or null set by delete
     * w.r.t the given context.
     */
    "MV generate delta with delete" {
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
        val map1 = Map(client)
        val map2 = Map(client)

        map1.putMV(key1, valueBoolean)
        map1.putMV(key1, valueDouble)
        map1.putMV(key1, valueInt)
        map1.putMV(key1, valueString)
        vv.update(client.tick())
        map1.putMV(key2, valueBoolean)
        map1.putMV(key2, valueDouble)
        map1.putMV(key2, valueInt)
        map1.putMV(key2, valueString)
        map1.deleteMVBoolean(key2)
        map1.deleteMVDouble(key2)
        map1.deleteMVInt(key2)
        map1.deleteMVString(key2)
        map1.putMV(key3, valueBoolean)
        map1.putMV(key3, valueDouble)
        map1.putMV(key3, valueInt)
        map1.putMV(key3, valueString)
        val delta = map1.generateDelta(vv)
        map2.merge(delta)

        map2.getMVBoolean(key1).shouldBeNull()
        map2.getMVDouble(key1).shouldBeNull()
        map2.getMVInt(key1).shouldBeNull()
        map2.getMVString(key1).shouldBeNull()
        map2.getMVBoolean(key2).shouldBeNull()
        map2.getMVDouble(key2).shouldBeNull()
        map2.getMVInt(key2).shouldBeNull()
        map2.getMVString(key2).shouldBeNull()
        map2.getMVBoolean(key3)!!.shouldHaveSingleElement(valueBoolean)
        map2.getMVDouble(key3)!!.shouldHaveSingleElement(valueDouble)
        map2.getMVInt(key3)!!.shouldHaveSingleElement(valueInt)
        map2.getMVString(key3)!!.shouldHaveSingleElement(valueString)

        val iteratorBoolean = map2.iteratorMVBoolean()
        iteratorBoolean.shouldHaveNext()
        iteratorBoolean.next().shouldBe(Pair(key3, setOf(valueBoolean)))
        iteratorBoolean.shouldBeEmpty()

        val iteratorDouble = map2.iteratorMVDouble()
        iteratorDouble.shouldHaveNext()
        iteratorDouble.next().shouldBe(Pair(key3, setOf(valueDouble)))
        iteratorDouble.shouldBeEmpty()

        val iteratorInt = map2.iteratorMVInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair(key3, setOf(valueInt)))
        iteratorInt.shouldBeEmpty()

        val iteratorString = map2.iteratorMVString()
        iteratorString.shouldHaveNext()
        iteratorString.next().shouldBe(Pair(key3, setOf(valueString)))
        iteratorString.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: increment get/iterator.
     * Call to get should return the value set by increment.
     * Call to iterator should return an iterator containing the value set by increment.
     */
    "CNT increment and get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val map = Map(client)

        map.increment("key", 10)
        map.getCntInt("key").shouldBe(10)

        val iteratorInt = map.iteratorCntInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair("key", 10))
        iteratorInt.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: decrement get/iterator.
     * Call to get should return the inverse of value set by decrement.
     * Call to iterator should return an iterator containing the inverse of value set by decrement.
     */
    "CNT decrement and get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val map = Map(client)

        map.decrement("key", 10)
        map.getCntInt("key").shouldBe(-10)

        val iteratorInt = map.iteratorCntInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair("key", -10))
        iteratorInt.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: increment(with a negative value) get/iterator.
     * Call to get should return the value set by increment.
     * Call to iterator should return an iterator containing the value set by increment.
     */
    "CNT increment with negative amount and get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val map = Map(client)

        map.increment("key", -10)

        map.getCntInt("key").shouldBe(-10)

        val iteratorInt = map.iteratorCntInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair("key", -10))
        iteratorInt.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: decrement(with a negative value) get/iterator.
     * Call to get should return the inverse of value set by decrement.
     * Call to iterator should return an iterator containing the inverse of value set by decrement.
     */
    "CNT decrement with a negative amount and get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val map = Map(client)

        map.decrement("key", -10)
        map.getCntInt("key").shouldBe(10)

        val iteratorInt = map.iteratorCntInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair("key", 10))
        iteratorInt.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: increment(multiple times) get/iterator.
     * Call to get should return the sum of values set by calls to increment.
     * Call to iterator should return an iterator containing the sum of values set by calls to increment.
     */
    "CNT multiple increments and get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val map = Map(client)

        map.increment("key", 10)
        map.increment("key", 1)
        map.increment("key", 100)

        map.getCntInt("key").shouldBe(111)

        val iteratorInt = map.iteratorCntInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair("key", 111))
        iteratorInt.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: decrement(multiple times) get/iterator.
     * Call to get should return the inverse of the sum of values set by calls to decrement.
     * Call to iterator should return an iterator containing the inverse of the sum of values set by calls to decrement.
     */
    "CNT multiple decrements and get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val map = Map(client)

        map.decrement("key", 10)
        map.decrement("key", 1)
        map.decrement("key", 100)

        map.getCntInt("key").shouldBe(-111)

        val iteratorInt = map.iteratorCntInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair("key", -111))
        iteratorInt.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: multiple increment and decrement get/iterator.
     * Call to get should return the sum of increments minus the sum of decrements.
     * Call to iterator should return an iterator containing the sum of increments minus the sum of decrements.
     */
    "CNT increment, decrement, get positive value" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val map = Map(client)

        map.increment("key", 42)
        map.decrement("key", 27)
        map.increment("key", 34)
        map.decrement("key", 2)

        map.getCntInt("key").shouldBe(47)

        val iteratorInt = map.iteratorCntInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair("key", 47))
        iteratorInt.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: multiple increment and decrement get/iterator.
     * Call to get should return the sum of increments minus the sum of decrements.
     * Call to iterator should return an iterator containing the sum of increments minus the sum of decrements.
     */
    "CNT increment, decrement, get a negative value" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val map = Map(client)

        map.increment("key", 42)
        map.decrement("key", 77)
        map.increment("key", 34)
        map.decrement("key", 13)

        map.getCntInt("key").shouldBe(-14)

        val iteratorInt = map.iteratorCntInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair("key", -14))
        iteratorInt.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: increment || merge get/iterator.
     * Call to get should return the value set by increment in the first replica.
     * Call to iterator should return an iterator containing the value set by increment in the first replica.
     */
    "CNT R1: increment; R2: merge and get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val map1 = Map(client)
        val map2 = Map(client)

        map1.increment("key", 11)
        map2.merge(map1)
        map1.merge(map2)

        map1.getCntInt("key").shouldBe(11)
        map2.getCntInt("key").shouldBe(11)

        val iteratorInt1 = map1.iteratorCntInt()
        iteratorInt1.shouldHaveNext()
        iteratorInt1.next().shouldBe(Pair("key", 11))
        iteratorInt1.shouldBeEmpty()

        val iteratorInt2 = map2.iteratorCntInt()
        iteratorInt2.shouldHaveNext()
        iteratorInt2.next().shouldBe(Pair("key", 11))
        iteratorInt2.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: decrement || merge get/iterator.
     * Call to get should return the inverse value set by decrement in the first replica.
     * Call to iterator should return an iterator containing the inverse value set by decrement in the first replica.
     */
    "CNT R1: decrement; R2: merge and get/iterator" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val map1 = Map(client)
        val map2 = Map(client)

        map1.decrement("key", 11)
        map2.merge(map1)
        map1.merge(map2)

        map1.getCntInt("key").shouldBe(-11)
        map2.getCntInt("key").shouldBe(-11)

        val iteratorInt1 = map1.iteratorCntInt()
        iteratorInt1.shouldHaveNext()
        iteratorInt1.next().shouldBe(Pair("key", -11))
        iteratorInt1.shouldBeEmpty()

        val iteratorInt2 = map2.iteratorCntInt()
        iteratorInt2.shouldHaveNext()
        iteratorInt2.next().shouldBe(Pair("key", -11))
        iteratorInt2.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: increment || increment merge get/iterator.
     * Call to get should return the sum of the two increment values.
     * Call to iterator should return an iterator containing the sum of the two increment values.
     */
    "CNT R1: increment; R2: increment, merge, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val map1 = Map(client1)
        val map2 = Map(client2)

        map1.increment("key", 10)
        map2.increment("key", 1)
        map2.merge(map1)

        map2.getCntInt("key").shouldBe(11)

        val iteratorInt = map2.iteratorCntInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair("key", 11))
        iteratorInt.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: increment || merge increment get/iterator.
     * Call to get should return the sum of the two increment values.
     * Call to iterator should return an iterator containing the sum of the two increment values.
     */
    "CNT R1: increment; R2: merge, increment, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val map1 = Map(client1)
        val map2 = Map(client2)

        map1.increment("key", 10)
        map2.merge(map1)
        map2.increment("key", 1)

        map2.getCntInt("key").shouldBe(11)

        val iteratorInt = map2.iteratorCntInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair("key", 11))
        iteratorInt.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: decrement || decrement merge get/iterator.
     * Call to get should return the inverse of the sum of the two decrement values.
     * Call to iterator should return an iterator containing the inverse of the sum of the two decrement values.
     */
    "CNT R1: decrement; R2: decrement, merge, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val map1 = Map(client1)
        val map2 = Map(client2)

        map1.decrement("key", 10)
        map2.decrement("key", 1)
        map2.merge(map1)

        map2.getCntInt("key").shouldBe(-11)

        val iteratorInt = map2.iteratorCntInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair("key", -11))
        iteratorInt.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: decrement || merge decrement get/iterator.
     * Call to get should return the inverse of the sum of the two decrement values.
     * Call to iterator should return an iterator containing the inverse of the sum of the two decrement values.
     */
    "CNT R1: decrement; R2: merge, decrement, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val map1 = Map(client1)
        val map2 = Map(client2)

        map1.decrement("key", 10)
        map2.merge(map1)
        map2.decrement("key", 1)

        map2.getCntInt("key").shouldBe(-11)

        val iteratorInt = map2.iteratorCntInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair("key", -11))
        iteratorInt.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: some operations || some operations merge get/iterator.
     * Call to get should return the sum of increment values minus the sum of the decrement values.
     * Call to iterator should return an iterator containing the sum of increment values minus the sum of the decrement values.
     */
    "CNT R1: multiple operations; R2: multiple operations, merge, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val map1 = Map(client1)
        val map2 = Map(client2)

        map1.decrement("key", 10)
        map1.increment("key", 10)
        map1.increment("key", 30)
        map1.decrement("key", 20)
        map2.decrement("key", 30)
        map2.increment("key", 50)
        map2.increment("key", 70)
        map2.decrement("key", 40)
        map2.merge(map1)

        map2.getCntInt("key").shouldBe(60)

        val iteratorInt = map2.iteratorCntInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair("key", 60))
        iteratorInt.shouldBeEmpty()
    }

    /**
     * This test evaluates the scenario: some operations || merge some operations get/iterator.
     * Call to get should return the sum of increment values minus the sum of the decrement values.
     * Call to iterator should return an iterator containing the sum of increment values minus the sum of the decrement values.
     */
    "CNT R1: multiple operations; R2: merge, multiple operations, get/iterator" {
        val uid1 = ClientUId("clientid1")
        val uid2 = ClientUId("clientid2")
        val client1 = SimpleEnvironment(uid1)
        val client2 = SimpleEnvironment(uid2)
        val map1 = Map(client1)
        val map2 = Map(client2)

        map1.decrement("key", 10)
        map1.increment("key", 10)
        map1.increment("key", 30)
        map1.decrement("key", 20)
        map2.merge(map1)
        map2.decrement("key", 30)
        map2.increment("key", 50)
        map2.increment("key", 70)
        map2.decrement("key", 40)

        map2.getCntInt("key").shouldBe(60)

        val iteratorInt = map2.iteratorCntInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair("key", 60))
        iteratorInt.shouldBeEmpty()
    }

    /**
     * This test evaluates the use of delta return by call to increment method.
     * Call to get should return the increment value set in the first replica.
     * Call to iterator should return an iterator containing the increment value set in the first replica.
     */
    "CNT use delta return by increment" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val map1 = Map(client)
        val map2 = Map(client)

        val incOp = map1.increment("key", 11)
        map2.merge(incOp)
        map1.merge(incOp)

        map1.getCntInt("key").shouldBe(11)
        map2.getCntInt("key").shouldBe(11)

        val iteratorInt1 = map1.iteratorCntInt()
        iteratorInt1.shouldHaveNext()
        iteratorInt1.next().shouldBe(Pair("key", 11))
        iteratorInt1.shouldBeEmpty()

        val iteratorInt2 = map2.iteratorCntInt()
        iteratorInt2.shouldHaveNext()
        iteratorInt2.next().shouldBe(Pair("key", 11))
        iteratorInt2.shouldBeEmpty()
    }

    /**
     * This test evaluates the use of delta return by call to decrement method.
     * Call to get should return the inverse of the decrement value set in the first replica.
     * Call to iterator should return an iterator containing the inverse of the decrement value set in the first replica.
     */
    "CNT use delta return by decrement" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val map1 = Map(client)
        val map2 = Map(client)

        val decOp = map1.decrement("key", 11)
        map2.merge(decOp)
        map1.merge(decOp)

        map1.getCntInt("key").shouldBe(-11)
        map2.getCntInt("key").shouldBe(-11)

        val iteratorInt1 = map1.iteratorCntInt()
        iteratorInt1.shouldHaveNext()
        iteratorInt1.next().shouldBe(Pair("key", -11))
        iteratorInt1.shouldBeEmpty()

        val iteratorInt2 = map2.iteratorCntInt()
        iteratorInt2.shouldHaveNext()
        iteratorInt2.next().shouldBe(Pair("key", -11))
        iteratorInt2.shouldBeEmpty()
    }

    /**
     * This test evaluates the use of delta return by call to increment and decrement methods.
     * Call to get should return the sum of increment values minus the sum of decrement values.
     * Call to iterator should return an iterator containing the sum of increment values minus the sum of decrement values.
     */
    "CNT use delta return by increment and decrement" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val map1 = Map(client)
        val map2 = Map(client)

        val decOp = map1.decrement("key", 11)
        val incOp = map1.increment("key", 22)
        map2.merge(decOp)
        map2.merge(incOp)
        map1.merge(decOp)
        map1.merge(incOp)

        map1.getCntInt("key").shouldBe(11)
        map2.getCntInt("key").shouldBe(11)

        val iteratorInt1 = map1.iteratorCntInt()
        iteratorInt1.shouldHaveNext()
        iteratorInt1.next().shouldBe(Pair("key", 11))
        iteratorInt1.shouldBeEmpty()

        val iteratorInt2 = map2.iteratorCntInt()
        iteratorInt2.shouldHaveNext()
        iteratorInt2.next().shouldBe(Pair("key", 11))
        iteratorInt2.shouldBeEmpty()
    }

    /**
     * This test evaluates the generation of delta plus its merging into another replica.
     * Call to get should return the values set by operations registered in the first replica after
     * w.r.t the given context (here only the decrements).
     * Call to iterator should return an iterator containing the values set by operations registered
     * in the first replica after w.r.t the given context (here only the decrements).
     */
    "CNT generate delta" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)
        val map1 = Map(client)
        val map2 = Map(client)

        map1.increment("key", 11)
        map1.increment("key", 33)
        val vv = client.getState()
        map1.decrement("key", 10)
        map1.decrement("key", 20)
        val delta = map1.generateDelta(vv)
        map2.merge(delta)

        map2.getCntInt("key").shouldBe(-30)

        val iteratorInt = map2.iteratorCntInt()
        iteratorInt.shouldHaveNext()
        iteratorInt.next().shouldBe(Pair("key", -30))
        iteratorInt.shouldBeEmpty()
    }

    /**
     * This test evaluates JSON serialization an empty map.
     */
    "empty JSON serialization" {
        val map = Map()

        val mapJson = map.toJson()

        mapJson.shouldBe("""{"_type":"Map","_metadata":{"lwwMap":{"entries":{}},"mvMap":{"entries":{},"causalContext":{"entries":[]}},"cntMap":{}}}""")
    }

    /**
     * This test evaluates JSON deserialization of an empty map.
     */
    "empty JSON deserialization" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)

        val mapJson = Map.fromJson(
            """{"_type":"Map","_metadata":{"lwwMap":{"entries":{}},"mvMap":{"entries":{},"causalContext":{"entries":[]}},"cntMap":{}}}""",
            client
        )
        mapJson.putLWW("key1", "value1")
        mapJson.putMV("key1", "value1")
        mapJson.increment("key1", 42)

        mapJson.getLWWString("key1").shouldBe("value1")
        mapJson.getMVString("key1")!!.shouldHaveSingleElement("value1")
        mapJson.getCntInt("key1").shouldBe(42)
        mapJson.getLWWString("key2").shouldBeNull()
        mapJson.getMVString("key2").shouldBeNull()
        mapJson.getCntInt("key2").shouldBeNull()
    }

    /**
     * This test evaluates JSON serialization of a map.
     */
    "JSON serialization" {
        val uid = ClientUId("clientid")
        val client = SimpleEnvironment(uid)

        val map = Map(client)

        map.putLWW("key", true)
        map.putLWW("key", 3.14)
        map.putLWW("key", 42)
        map.putLWW("key", "value")

        map.putMV("key", true)
        map.putMV("key", 3.14)
        map.putMV("key", 42)
        map.putMV("key", "value")
        map.increment("key", 42)
        map.decrement("key", 11)
        val mapJson = map.toJson()

        mapJson.shouldBe("""{"_type":"Map","_metadata":{"lwwMap":{"entries":{"key%BOOLEAN":{"uid":{"name":"clientid"},"cnt":-2147483647},"key%DOUBLE":{"uid":{"name":"clientid"},"cnt":-2147483646},"key%INTEGER":{"uid":{"name":"clientid"},"cnt":-2147483645},"key%STRING":{"uid":{"name":"clientid"},"cnt":-2147483644}}},"mvMap":{"entries":{"key%BOOLEAN":[{"uid":{"name":"clientid"},"cnt":-2147483643}],"key%DOUBLE":[{"uid":{"name":"clientid"},"cnt":-2147483642}],"key%INTEGER":[{"uid":{"name":"clientid"},"cnt":-2147483641}],"key%STRING":[{"uid":{"name":"clientid"},"cnt":-2147483640}]},"causalContext":{"entries":[{"name":"clientid"},-2147483640]}},"cntMap":{"key":{"increment":[{"name":"clientid"},{"first":42,"second":{"uid":{"name":"clientid"},"cnt":-2147483639}}],"decrement":[{"name":"clientid"},{"first":11,"second":{"uid":{"name":"clientid"},"cnt":-2147483638}}]}}},"key%BOOLEAN%LWW":true,"key%DOUBLE%LWW":3.14,"key%INTEGER%LWW":42,"key%STRING%LWW":"value","key%BOOLEAN%MV":[true],"key%DOUBLE%MV":[3.14],"key%INTEGER%MV":[42],"key%STRING%MV":["value"],"key%CNT":31}""")
    }

    /**
     * This test evaluates JSON deserialization of a map.
     */
    "JSON deserialization" {
        val mapJson = Map.fromJson("""{"_type":"Map","_metadata":{"lwwMap":{"entries":{"key%BOOLEAN":{"uid":{"name":"clientid"},"cnt":-2147483647},"key%DOUBLE":{"uid":{"name":"clientid"},"cnt":-2147483646},"key%INTEGER":{"uid":{"name":"clientid"},"cnt":-2147483645},"key%STRING":{"uid":{"name":"clientid"},"cnt":-2147483644}}},"mvMap":{"entries":{"key%BOOLEAN":[{"uid":{"name":"clientid"},"cnt":-2147483643}],"key%DOUBLE":[{"uid":{"name":"clientid"},"cnt":-2147483642}],"key%INTEGER":[{"uid":{"name":"clientid"},"cnt":-2147483641}],"key%STRING":[{"uid":{"name":"clientid"},"cnt":-2147483640}]},"causalContext":{"entries":[{"name":"clientid"},-2147483640]}},"cntMap":{"key":{"increment":[{"name":"clientid"},{"first":42,"second":{"uid":{"name":"clientid"},"cnt":-2147483639}}],"decrement":[{"name":"clientid"},{"first":11,"second":{"uid":{"name":"clientid"},"cnt":-2147483638}}]}}},"key%BOOLEAN%LWW":true,"key%DOUBLE%LWW":3.14,"key%INTEGER%LWW":42,"key%STRING%LWW":"value","key%BOOLEAN%MV":[true],"key%DOUBLE%MV":[3.14],"key%INTEGER%MV":[42],"key%STRING%MV":["value"],"key%CNT":31}""")

        mapJson.getLWWBoolean("key").shouldBe(true)
        mapJson.getLWWDouble("key").shouldBe(3.14)
        mapJson.getLWWInt("key").shouldBe(42)
        mapJson.getLWWString("key").shouldBe("value")
        mapJson.getMVBoolean("key")!!.shouldHaveSingleElement(true)
        mapJson.getMVDouble("key")!!.shouldHaveSingleElement(3.14)
        mapJson.getMVInt("key")!!.shouldHaveSingleElement(42)
        mapJson.getMVString("key")!!.shouldHaveSingleElement("value")
        mapJson.getCntInt("key").shouldBe(31)
    }
})
