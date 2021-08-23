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
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll

class MVMapPropTest : StringSpec({

    val uid1 = ClientUId("clientid1")
    val uid2 = ClientUId("clientid2")
    val uid3 = ClientUId("clientid3")
    var client1 = SimpleEnvironment(uid1)
    var client2 = SimpleEnvironment(uid2)
    var client3 = SimpleEnvironment(uid3)

    beforeTest {
        client1 = SimpleEnvironment(uid1)
        client2 = SimpleEnvironment(uid2)
        client3 = SimpleEnvironment(uid3)
    }

    "multiple put" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map = MVMap(client1)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map.put(key, valBoolean)
                map.put(key, valDouble)
                map.put(key, valInt)
                map.put(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }

            val iteratorBoolean = map.iteratorBoolean()
            for ((k, v) in mapBoolean) {
                map.getBoolean(k)!!.shouldHaveSingleElement(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorBoolean.shouldBeEmpty()

            val iteratorDouble = map.iteratorDouble()
            for ((k, v) in mapDouble) {
                map.getDouble(k)!!.shouldHaveSingleElement(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorDouble.shouldBeEmpty()

            val iteratorInt = map.iteratorInt()
            for ((k, v) in mapInt) {
                map.getInt(k)!!.shouldHaveSingleElement(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorInt.shouldBeEmpty()

            val iteratorString = map.iteratorString()
            for ((k, v) in mapString) {
                map.getString(k)!!.shouldHaveSingleElement(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "multiple put on same key Boolean, delete" {
        checkAll(Arb.list(Arb.bool())) { values ->
            val map = MVMap(client1)
            val key = Arb.string().next()

            values.map { value ->
                map.put(key, value)
            }

            map.getBoolean(key)?.shouldHaveSingleElement(values.lastOrNull())
            val iteratorBoolean = map.iteratorBoolean()
            if (values.lastOrNull() != null) {
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(key, setOf(values.last())))
            }
            iteratorBoolean.shouldBeEmpty()

            map.deleteBoolean(key)
            map.getBoolean(key).shouldBeNull()
            map.iteratorBoolean().shouldBeEmpty()
        }
    }

    "multiple put on same key Double, delete" {
        checkAll(Arb.list(Arb.numericDoubles())) { values ->
            val map = MVMap(client1)
            val key = Arb.string().next()

            values.map { double ->
                map.put(key, double)
            }

            map.getDouble(key)?.shouldHaveSingleElement(values.lastOrNull())
            val iteratorDouble = map.iteratorDouble()
            if (values.lastOrNull() != null) {
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(key, setOf(values.last())))
            }
            iteratorDouble.shouldBeEmpty()

            map.deleteDouble(key)
            map.getDouble(key).shouldBeNull()
            map.iteratorDouble().shouldBeEmpty()
        }
    }

    "multiple put on same key Int, delete" {
        checkAll(Arb.list(Arb.int())) { values ->
            val map = MVMap(client1)
            val key = Arb.string().next()

            values.map { value ->
                map.put(key, value)
            }

            map.getInt(key)?.shouldHaveSingleElement(values.lastOrNull())
            val iteratorInt = map.iteratorInt()
            if (values.lastOrNull() != null) {
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(key, setOf(values.last())))
            }
            iteratorInt.shouldBeEmpty()

            map.deleteInt(key)
            map.getInt(key).shouldBeNull()
            map.iteratorInt().shouldBeEmpty()
        }
    }

    "multiple put on same key String, delete" {
        checkAll(Arb.list(Arb.string(0..1), 0..25)) { values ->
            val map = MVMap(client1)
            val key = Arb.string().next()

            values.map { value ->
                map.put(key, value)
            }

            map.getString(key)?.shouldHaveSingleElement(values.lastOrNull())
            val iteratorString = map.iteratorString()
            if (values.lastOrNull() != null) {
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(key, setOf(values.last())))
            }
            iteratorString.shouldBeEmpty()

            map.deleteString(key)
            map.getString(key).shouldBeNull()
            map.iteratorString().shouldBeEmpty()
        }
    }

    "R1: put; R2: merge" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = MVMap(client1)
            val map2 = MVMap(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map1.put(key, valBoolean)
                map1.put(key, valDouble)
                map1.put(key, valInt)
                map1.put(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            map2.merge(map1)

            val iteratorBoolean = map2.iteratorBoolean()
            for ((k, v) in mapBoolean) {
                map1.getBoolean(k)!!.shouldHaveSingleElement(v)
                map2.getBoolean(k)!!.shouldHaveSingleElement(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map1.getDouble(k)!!.shouldHaveSingleElement(v)
                map2.getDouble(k)!!.shouldHaveSingleElement(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map1.getInt(k)!!.shouldHaveSingleElement(v)
                map2.getInt(k)!!.shouldHaveSingleElement(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorString()
            for ((k, v) in mapString) {
                map1.getString(k)!!.shouldHaveSingleElement(v)
                map2.getString(k)!!.shouldHaveSingleElement(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "R1: put; R2: merge, put" {
        checkAll(50, Arb.list(Arb.string(0..1), 0..10)) { keys ->
            val map1 = MVMap(client1)
            val map2 = MVMap(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map1.put(key, arbBoolean.next())
                map1.put(key, arbDouble.next())
                map1.put(key, arbInt.next())
                map1.put(key, arbString.next())
            }
            map2.merge(map1)
            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map2.put(key, valBoolean)
                map2.put(key, valDouble)
                map2.put(key, valInt)
                map2.put(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }

            val iteratorBoolean = map2.iteratorBoolean()
            for ((k, v) in mapBoolean) {
                map2.getBoolean(k)!!.shouldHaveSingleElement(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map2.getDouble(k)!!.shouldHaveSingleElement(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map2.getInt(k)!!.shouldHaveSingleElement(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorString()
            for ((k, v) in mapString) {
                map2.getString(k)!!.shouldHaveSingleElement(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "R1: put; R2: put, merge" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = MVMap(client1)
            val map2 = MVMap(client2)

            val mapBoolean1 = mutableMapOf<String, Boolean>()
            val mapDouble1 = mutableMapOf<String, Double>()
            val mapInt1 = mutableMapOf<String, Int>()
            val mapString1 = mutableMapOf<String, String>()
            val mapBoolean2 = mutableMapOf<String, Boolean>()
            val mapDouble2 = mutableMapOf<String, Double>()
            val mapInt2 = mutableMapOf<String, Int>()
            val mapString2 = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map1.put(key, valBoolean)
                map1.put(key, valDouble)
                map1.put(key, valInt)
                map1.put(key, valString)
                mapBoolean1[key] = valBoolean
                mapDouble1[key] = valDouble
                mapInt1[key] = valInt
                mapString1[key] = valString
            }
            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map2.put(key, valBoolean)
                map2.put(key, valDouble)
                map2.put(key, valInt)
                map2.put(key, valString)
                mapBoolean2[key] = valBoolean
                mapDouble2[key] = valDouble
                mapInt2[key] = valInt
                mapString2[key] = valString
            }
            map2.merge(map1)

            val iteratorBoolean = map2.iteratorBoolean()
            for ((k, v) in mapBoolean2) {
                map2.getBoolean(k)!!.shouldContainExactlyInAnyOrder(setOf(v, mapBoolean1[k]))
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v, mapBoolean1[k])))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in mapDouble2) {
                map2.getDouble(k)!!.shouldContainExactlyInAnyOrder(setOf(v, mapDouble1[k]))
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v, mapDouble1[k])))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in mapInt2) {
                map2.getInt(k)!!.shouldContainExactlyInAnyOrder(setOf(v, mapInt1[k]))
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v, mapInt1[k])))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorString()
            for ((k, v) in mapString2) {
                map2.getString(k)!!.shouldContainExactlyInAnyOrder(setOf(v, mapString1[k]))
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v, mapString1[k])))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "R1: put, delete; R2: put with older timestamp, merge" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = MVMap(client1)
            val map2 = MVMap(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map2.put(key, valBoolean)
                map2.put(key, valDouble)
                map2.put(key, valInt)
                map2.put(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            keys.map { key ->
                map1.put(key, arbBoolean.next())
                map1.put(key, arbDouble.next())
                map1.put(key, arbInt.next())
                map1.put(key, arbString.next())
            }
            keys.map { key ->
                map1.deleteBoolean(key)
                map1.deleteDouble(key)
                map1.deleteInt(key)
                map1.deleteString(key)
            }
            map2.merge(map1)

            val iteratorBoolean = map2.iteratorBoolean()
            for ((k, v) in mapBoolean) {
                map2.getBoolean(k)!!.shouldContainExactlyInAnyOrder(mapBoolean[k], null)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map2.getDouble(k)!!.shouldContainExactlyInAnyOrder(mapDouble[k], null)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map2.getInt(k)!!.shouldContainExactlyInAnyOrder(mapInt[k], null)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorString()
            for ((k, v) in mapString) {
                map2.getString(k)!!.shouldContainExactlyInAnyOrder(mapString[k], null)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "R1: put, delete; R2: put with older timestamp, merge before delete, merge after delete" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = MVMap(client1)
            val map2 = MVMap(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map2.put(key, valBoolean)
                map2.put(key, valDouble)
                map2.put(key, valInt)
                map2.put(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            keys.map { key ->
                map1.put(key, arbBoolean.next())
                map1.put(key, arbDouble.next())
                map1.put(key, arbInt.next())
                map1.put(key, arbString.next())
            }
            map2.merge(map1)
            keys.map { key ->
                map1.deleteBoolean(key)
                map1.deleteDouble(key)
                map1.deleteInt(key)
                map1.deleteString(key)
            }
            map2.merge(map1)

            val iteratorBoolean = map2.iteratorBoolean()
            for ((k, v) in mapBoolean) {
                map2.getBoolean(k)!!.shouldContainExactlyInAnyOrder(mapBoolean[k], null)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map2.getDouble(k)!!.shouldContainExactlyInAnyOrder(mapDouble[k], null)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map2.getInt(k)!!.shouldContainExactlyInAnyOrder(mapInt[k], null)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorString()
            for ((k, v) in mapString) {
                map2.getString(k)!!.shouldContainExactlyInAnyOrder(mapString[k], null)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "R1: put, delete; R2: put with newer timestamp, merge" {
        checkAll(200, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = MVMap(client1)
            val map2 = MVMap(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map1.put(key, arbBoolean.next())
                map1.put(key, arbDouble.next())
                map1.put(key, arbInt.next())
                map1.put(key, arbString.next())
                client2.tick()
                client2.tick()
                client2.tick()
                client2.tick()
            }
            keys.map { key ->
                map1.deleteBoolean(key)
                map1.deleteDouble(key)
                map1.deleteInt(key)
                map1.deleteString(key)
            }

            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map2.put(key, valBoolean)
                map2.put(key, valDouble)
                map2.put(key, valInt)
                map2.put(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            map2.merge(map1)

            val iteratorBoolean = map2.iteratorBoolean()
            for ((k, v) in mapBoolean) {
                map2.getBoolean(k)!!.shouldContainExactlyInAnyOrder(mapBoolean[k], null)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map2.getDouble(k)!!.shouldContainExactlyInAnyOrder(mapDouble[k], null)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map2.getInt(k)!!.shouldContainExactlyInAnyOrder(mapInt[k], null)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorString()
            for ((k, v) in mapString) {
                map2.getString(k)!!.shouldContainExactlyInAnyOrder(mapString[k], null)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "R1: put, delete; R2: put with newer timestamp, merge before delete, merge after delete" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = MVMap(client1)
            val map2 = MVMap(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map1.put(key, arbBoolean.next())
                map1.put(key, arbDouble.next())
                map1.put(key, arbInt.next())
                map1.put(key, arbString.next())
            }
            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map2.put(key, valBoolean)
                map2.put(key, valDouble)
                map2.put(key, valInt)
                map2.put(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            map2.merge(map1)
            keys.map { key ->
                map1.deleteBoolean(key)
                map1.deleteDouble(key)
                map1.deleteInt(key)
                map1.deleteString(key)
            }
            map2.merge(map1)

            val iteratorBoolean = map2.iteratorBoolean()
            for ((k, v) in mapBoolean) {
                map2.getBoolean(k)!!.shouldContainExactlyInAnyOrder(mapBoolean[k], null)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map2.getDouble(k)!!.shouldContainExactlyInAnyOrder(mapDouble[k], null)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map2.getInt(k)!!.shouldContainExactlyInAnyOrder(mapInt[k], null)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorString()
            for ((k, v) in mapString) {
                map2.getString(k)!!.shouldContainExactlyInAnyOrder(mapString[k], null)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "R1: put; R2: put; R3: merge R1, delete, merge R2" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = MVMap(client1)
            val map2 = MVMap(client2)
            val map3 = MVMap(client3)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map1.put(key, arbBoolean.next())
                map1.put(key, arbDouble.next())
                map1.put(key, arbInt.next())
                map1.put(key, arbString.next())
            }
            map3.merge(map1)
            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map2.put(key, valBoolean)
                map2.put(key, valDouble)
                map2.put(key, valInt)
                map2.put(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            keys.map { key ->
                map3.deleteBoolean(key)
                map3.deleteDouble(key)
                map3.deleteInt(key)
                map3.deleteString(key)
            }
            map3.merge(map2)

            val iteratorBoolean = map3.iteratorBoolean()
            for ((k, v) in mapBoolean) {
                map3.getBoolean(k)!!.shouldContainExactlyInAnyOrder(mapBoolean[k], null)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map3.iteratorDouble()
            for ((k, v) in mapDouble) {
                map3.getDouble(k)!!.shouldContainExactlyInAnyOrder(mapDouble[k], null)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map3.iteratorInt()
            for ((k, v) in mapInt) {
                map3.getInt(k)!!.shouldContainExactlyInAnyOrder(mapInt[k], null)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map3.iteratorString()
            for ((k, v) in mapString) {
                map3.getString(k)!!.shouldContainExactlyInAnyOrder(mapString[k], null)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "use deltas returned by put" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = MVMap(client1)
            val map2 = MVMap(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map2.merge(map1.put(key, valBoolean))
                map2.merge(map1.put(key, valDouble))
                map2.merge(map1.put(key, valInt))
                map2.merge(map1.put(key, valString))
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }

            val iteratorBoolean = map2.iteratorBoolean()
            for ((k, v) in mapBoolean) {
                map1.getBoolean(k)!!.shouldHaveSingleElement(v)
                map2.getBoolean(k)!!.shouldHaveSingleElement(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map1.getDouble(k)!!.shouldHaveSingleElement(v)
                map2.getDouble(k)!!.shouldHaveSingleElement(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map1.getInt(k)!!.shouldHaveSingleElement(v)
                map2.getInt(k)!!.shouldHaveSingleElement(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorString()
            for ((k, v) in mapString) {
                map1.getString(k)!!.shouldHaveSingleElement(v)
                map2.getString(k)!!.shouldHaveSingleElement(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "use deltas returned by put and delete" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = MVMap(client1)
            val map2 = MVMap(client2)

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map2.merge(map1.put(key, arbBoolean.next()))
                map2.merge(map1.put(key, arbDouble.next()))
                map2.merge(map1.put(key, arbInt.next()))
                map2.merge(map1.put(key, arbString.next()))
            }
            keys.map { key ->
                map2.merge(map1.deleteBoolean(key))
                map2.merge(map1.deleteDouble(key))
                map2.merge(map1.deleteInt(key))
                map2.merge(map1.deleteString(key))
            }

            keys.map { key ->
                map1.getBoolean(key).shouldBeNull()
                map1.getDouble(key).shouldBeNull()
                map1.getInt(key).shouldBeNull()
                map1.getString(key).shouldBeNull()
                map2.getBoolean(key).shouldBeNull()
                map2.getDouble(key).shouldBeNull()
                map2.getInt(key).shouldBeNull()
                map2.getString(key).shouldBeNull()
            }
            map1.iteratorBoolean().shouldBeEmpty()
            map1.iteratorDouble().shouldBeEmpty()
            map1.iteratorInt().shouldBeEmpty()
            map1.iteratorString().shouldBeEmpty()
            map2.iteratorBoolean().shouldBeEmpty()
            map2.iteratorDouble().shouldBeEmpty()
            map2.iteratorInt().shouldBeEmpty()
            map2.iteratorString().shouldBeEmpty()
        }
    }

    "merge deltas returned by put operations" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..15), Arb.list(Arb.string(0..1), 0..15)) { keys1, keys2 ->
            val map1 = MVMap(client1)
            val map2 = MVMap(client2)
            val deltas = MVMap()

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys1.map { key ->
                deltas.merge(map1.put(key, arbBoolean.next()))
                deltas.merge(map1.put(key, arbDouble.next()))
                deltas.merge(map1.put(key, arbInt.next()))
                deltas.merge(map1.put(key, arbString.next()))
            }
            keys1.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                deltas.merge(map1.put(key, valBoolean))
                deltas.merge(map1.put(key, valDouble))
                deltas.merge(map1.put(key, valInt))
                deltas.merge(map1.put(key, valString))
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            keys2.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                deltas.merge(map1.put(key, valBoolean))
                deltas.merge(map1.put(key, valDouble))
                deltas.merge(map1.put(key, valInt))
                deltas.merge(map1.put(key, valString))
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            map2.merge(deltas)

            val iteratorBoolean1 = map1.iteratorBoolean()
            val iteratorBoolean2 = map2.iteratorBoolean()
            for ((k, v) in mapBoolean) {
                map1.getBoolean(k)!!.shouldHaveSingleElement(v)
                map2.getBoolean(k)!!.shouldHaveSingleElement(v)
                iteratorBoolean1.shouldHaveNext()
                iteratorBoolean1.next().shouldBe(Pair(k, setOf(v)))
                iteratorBoolean2.shouldHaveNext()
                iteratorBoolean2.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorBoolean1.shouldBeEmpty()
            iteratorBoolean2.shouldBeEmpty()
            val iteratorDouble1 = map1.iteratorDouble()
            val iteratorDouble2 = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map1.getDouble(k)!!.shouldHaveSingleElement(v)
                map2.getDouble(k)!!.shouldHaveSingleElement(v)
                iteratorDouble1.shouldHaveNext()
                iteratorDouble1.next().shouldBe(Pair(k, setOf(v)))
                iteratorDouble2.shouldHaveNext()
                iteratorDouble2.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorDouble1.shouldBeEmpty()
            iteratorDouble2.shouldBeEmpty()
            val iteratorInt1 = map1.iteratorInt()
            val iteratorInt2 = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map1.getInt(k)!!.shouldHaveSingleElement(v)
                map2.getInt(k)!!.shouldHaveSingleElement(v)
                iteratorInt1.shouldHaveNext()
                iteratorInt1.next().shouldBe(Pair(k, setOf(v)))
                iteratorInt2.shouldHaveNext()
                iteratorInt2.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorInt1.shouldBeEmpty()
            iteratorInt2.shouldBeEmpty()
            val iteratorString1 = map1.iteratorString()
            val iteratorString2 = map2.iteratorString()
            for ((k, v) in mapString) {
                map1.getString(k)!!.shouldHaveSingleElement(v)
                map2.getString(k)!!.shouldHaveSingleElement(v)
                iteratorString1.shouldHaveNext()
                iteratorString1.next().shouldBe(Pair(k, setOf(v)))
                iteratorString2.shouldHaveNext()
                iteratorString2.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorString1.shouldBeEmpty()
            iteratorString2.shouldBeEmpty()
        }
    }

    "merge deltas returned by put and delete operations" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..15), Arb.list(Arb.string(0..1), 0..15)) { keys1, keys2 ->
            val map1 = MVMap(client1)
            val map2 = MVMap(client2)
            val deltas = MVMap()

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys1.map { key ->
                deltas.merge(map1.put(key, arbBoolean.next()))
                deltas.merge(map1.put(key, arbDouble.next()))
                deltas.merge(map1.put(key, arbInt.next()))
                deltas.merge(map1.put(key, arbString.next()))
            }
            keys1.map { key ->
                deltas.merge(map1.deleteBoolean(key))
                deltas.merge(map1.deleteDouble(key))
                deltas.merge(map1.deleteInt(key))
                deltas.merge(map1.deleteString(key))
            }
            keys2.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                deltas.merge(map1.put(key, valBoolean))
                deltas.merge(map1.put(key, valDouble))
                deltas.merge(map1.put(key, valInt))
                deltas.merge(map1.put(key, valString))
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            map2.merge(deltas)

            for ((k, v) in mapBoolean) {
                map1.getBoolean(k)!!.shouldHaveSingleElement(v)
                map2.getBoolean(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapDouble) {
                map1.getDouble(k)!!.shouldHaveSingleElement(v)
                map2.getDouble(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapInt) {
                map1.getInt(k)!!.shouldHaveSingleElement(v)
                map2.getInt(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapString) {
                map1.getString(k)!!.shouldHaveSingleElement(v)
                map2.getString(k)!!.shouldHaveSingleElement(v)
            }

            val iteratorBoolean = map2.iteratorBoolean()
            for ((k, v) in iteratorBoolean) {
                map1.getBoolean(k).shouldBe(v)
                map2.getBoolean(k).shouldBe(v)
                v.shouldHaveSingleElement(mapBoolean[k])
            }
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in iteratorDouble) {
                map1.getDouble(k).shouldBe(v)
                map2.getDouble(k).shouldBe(v)
                v.shouldHaveSingleElement(mapDouble[k])
            }
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in iteratorInt) {
                map1.getInt(k).shouldBe(v)
                map2.getInt(k).shouldBe(v)
                v.shouldHaveSingleElement(mapInt[k])
            }
            val iteratorString = map2.iteratorString()
            for ((k, v) in iteratorString) {
                map1.getString(k).shouldBe(v)
                map2.getString(k).shouldBe(v)
                v.shouldHaveSingleElement(mapString[k])
            }
        }
    }

    "generate delta" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..15), Arb.list(Arb.string(0..1), 0..15)) { keys1, keys2 ->
            val vv = VersionVector()
            val map1 = MVMap(client1)
            val map2 = MVMap(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys1.map { key ->
                map1.put(key, arbBoolean.next())
                map1.put(key, arbDouble.next())
                map1.put(key, arbInt.next())
                map1.put(key, arbString.next())
            }
            vv.update(client1.tick())
            keys2.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map1.put(key, valBoolean)
                map1.put(key, valDouble)
                map1.put(key, valInt)
                map1.put(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            val delta = map1.generateDelta(vv)
            map2.merge(delta)

            for ((k, v) in mapBoolean) {
                map1.getBoolean(k)!!.shouldHaveSingleElement(v)
                map2.getBoolean(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapDouble) {
                map1.getDouble(k)!!.shouldHaveSingleElement(v)
                map2.getDouble(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapInt) {
                map1.getInt(k)!!.shouldHaveSingleElement(v)
                map2.getInt(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapString) {
                map1.getString(k)!!.shouldHaveSingleElement(v)
                map2.getString(k)!!.shouldHaveSingleElement(v)
            }

            val iteratorBoolean = map2.iteratorBoolean()
            for ((k, v) in iteratorBoolean) {
                map1.getBoolean(k).shouldBe(v)
                map2.getBoolean(k).shouldBe(v)
                v.shouldHaveSingleElement(mapBoolean[k])
            }
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in iteratorDouble) {
                map1.getDouble(k).shouldBe(v)
                map2.getDouble(k).shouldBe(v)
                v.shouldHaveSingleElement(mapDouble[k])
            }
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in iteratorInt) {
                map1.getInt(k).shouldBe(v)
                map2.getInt(k).shouldBe(v)
                v.shouldHaveSingleElement(mapInt[k])
            }
            val iteratorString = map2.iteratorString()
            for ((k, v) in iteratorString) {
                map1.getString(k).shouldBe(v)
                map2.getString(k).shouldBe(v)
                v.shouldHaveSingleElement(mapString[k])
            }
        }
    }

    "generate delta with delete" {
        checkAll(
            250, Arb.list(Arb.string(0..1), 0..15), Arb.list(Arb.string(0..1), 0..15),
            Arb.list(Arb.string(0..1), 0..15)
        ) { keys1, keys2, keys3 ->
            val vv = VersionVector()
            val map1 = MVMap(client1)
            val map2 = MVMap(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys1.map { key ->
                map1.put(key, arbBoolean.next())
                map1.put(key, arbDouble.next())
                map1.put(key, arbInt.next())
                map1.put(key, arbString.next())
            }
            vv.update(client1.tick())
            keys2.map { key ->
                map1.put(key, arbBoolean.next())
                map1.put(key, arbDouble.next())
                map1.put(key, arbInt.next())
                map1.put(key, arbString.next())
            }
            keys2.map { key ->
                map1.deleteBoolean(key)
                map1.deleteDouble(key)
                map1.deleteInt(key)
                map1.deleteString(key)
            }
            keys3.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map1.put(key, valBoolean)
                map1.put(key, valDouble)
                map1.put(key, valInt)
                map1.put(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            val delta = map1.generateDelta(vv)
            map2.merge(delta)

            for ((k, v) in mapBoolean) {
                map1.getBoolean(k)!!.shouldHaveSingleElement(v)
                map2.getBoolean(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapDouble) {
                map1.getDouble(k)!!.shouldHaveSingleElement(v)
                map2.getDouble(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapInt) {
                map1.getInt(k)!!.shouldHaveSingleElement(v)
                map2.getInt(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapString) {
                map1.getString(k)!!.shouldHaveSingleElement(v)
                map2.getString(k)!!.shouldHaveSingleElement(v)
            }

            val iteratorBoolean = map2.iteratorBoolean()
            for ((k, v) in iteratorBoolean) {
                map1.getBoolean(k).shouldBe(v)
                map2.getBoolean(k).shouldBe(v)
                v.shouldHaveSingleElement(mapBoolean[k])
            }
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in iteratorDouble) {
                map1.getDouble(k).shouldBe(v)
                map2.getDouble(k).shouldBe(v)
                v.shouldHaveSingleElement(mapDouble[k])
            }
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in iteratorInt) {
                map1.getInt(k).shouldBe(v)
                map2.getInt(k).shouldBe(v)
                v.shouldHaveSingleElement(mapInt[k])
            }
            val iteratorString = map2.iteratorString()
            for ((k, v) in iteratorString) {
                map1.getString(k).shouldBe(v)
                map2.getString(k).shouldBe(v)
                v.shouldHaveSingleElement(mapString[k])
            }
        }
    }

    "deserialize is inverse to serialize" {
        checkAll<String, String, String, String, String>(500) { key1, key2, key3, key4, key5 ->
            val map1 = MVMap(client1)
            val map2 = MVMap(client2)

            val value1 = Arb.int().next()
            val value2 = Arb.string().next()
            val value3 = Arb.string().next()
            val value4 = Arb.bool().next()
            val value5 = Arb.double().next()

            map1.put(key1, value1)
            map1.put(key2, value2)
            map1.deleteString(key2)
            map1.put(key3, value3)
            map1.put(key4, value4)
            map1.put(key5, value5)
            map2.put(key3, value2)
            map1.merge(map2)
            val mapJson = map1.toJson()

            mapJson.shouldBe(MVMap.fromJson(mapJson).toJson())
        }
    }
})
