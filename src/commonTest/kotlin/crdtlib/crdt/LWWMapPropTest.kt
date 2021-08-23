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
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll

class LWWMapPropTest : StringSpec({

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
        checkAll(500, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map = LWWMap(client1)

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
                map.getBoolean(k).shouldBe(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean.shouldBeEmpty()

            val iteratorDouble = map.iteratorDouble()
            for ((k, v) in mapDouble) {
                map.getDouble(k).shouldBe(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, v))
            }
            iteratorDouble.shouldBeEmpty()

            val iteratorInt = map.iteratorInt()
            for ((k, v) in mapInt) {
                map.getInt(k).shouldBe(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, v))
            }
            iteratorInt.shouldBeEmpty()

            val iteratorString = map.iteratorString()
            for ((k, v) in mapString) {
                map.getString(k).shouldBe(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, v))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "multiple put on same key Boolean, delete" {
        checkAll(Arb.list(Arb.bool())) { values ->
            val map = LWWMap(client1)
            val key = Arb.string().next()

            values.map { value ->
                map.put(key, value)
            }

            map.getBoolean(key).shouldBe(values.lastOrNull())
            val iteratorBoolean = map.iteratorBoolean()
            if (values.lastOrNull() != null) {
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(key, values.last()))
            }
            iteratorBoolean.shouldBeEmpty()

            map.deleteBoolean(key)
            map.getBoolean(key).shouldBeNull()
            map.iteratorBoolean().shouldBeEmpty()
        }
    }

    "multiple put on same key Double, delete" {
        checkAll(Arb.list(Arb.numericDoubles())) { values ->
            val map = LWWMap(client1)
            val key = Arb.string().next()

            values.map { double ->
                map.put(key, double)
            }

            map.getDouble(key).shouldBe(values.lastOrNull())
            val iteratorDouble = map.iteratorDouble()
            if (values.lastOrNull() != null) {
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(key, values.last()))
            }
            iteratorDouble.shouldBeEmpty()

            map.deleteDouble(key)
            map.getDouble(key).shouldBeNull()
            map.iteratorDouble().shouldBeEmpty()
        }
    }

    "multiple put on same key Int, delete" {
        checkAll(Arb.list(Arb.int())) { values ->
            val map = LWWMap(client1)
            val key = Arb.string().next()

            values.map { value ->
                map.put(key, value)
            }

            map.getInt(key).shouldBe(values.lastOrNull())
            val iteratorInt = map.iteratorInt()
            if (values.lastOrNull() != null) {
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(key, values.last()))
            }
            iteratorInt.shouldBeEmpty()

            map.deleteInt(key)
            map.getInt(key).shouldBeNull()
            map.iteratorInt().shouldBeEmpty()
        }
    }

    "multiple put on same key String, delete" {
        checkAll(Arb.list(Arb.string(0..1), 0..25)) { values ->
            val map = LWWMap(client1)
            val key = Arb.string().next()

            values.map { value ->
                map.put(key, value)
            }

            map.getString(key).shouldBe(values.lastOrNull())
            val iteratorString = map.iteratorString()
            if (values.lastOrNull() != null) {
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(key, values.last()))
            }
            iteratorString.shouldBeEmpty()

            map.deleteString(key)
            map.getString(key).shouldBeNull()
            map.iteratorString().shouldBeEmpty()
        }
    }

    "R1: put; R2: merge" {
        checkAll(750, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = LWWMap(client1)
            val map2 = LWWMap(client2)

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
                map1.getBoolean(k).shouldBe(v)
                map2.getBoolean(k).shouldBe(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map1.getDouble(k).shouldBe(v)
                map2.getDouble(k).shouldBe(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, v))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map1.getInt(k).shouldBe(v)
                map2.getInt(k).shouldBe(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, v))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorString()
            for ((k, v) in mapString) {
                map1.getString(k).shouldBe(v)
                map2.getString(k).shouldBe(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, v))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "R1: put; R2: merge, put" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = LWWMap(client1)
            val map2 = LWWMap(client2)

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
                map2.getBoolean(k).shouldBe(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map2.getDouble(k).shouldBe(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, v))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map2.getInt(k).shouldBe(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, v))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorString()
            for ((k, v) in mapString) {
                map2.getString(k).shouldBe(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, v))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "R1: put | R2: put, merge 1->2" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = LWWMap(client1)
            val map2 = LWWMap(client2)

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
                map2.getBoolean(k).shouldBe(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map2.getDouble(k).shouldBe(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, v))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map2.getInt(k).shouldBe(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, v))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorString()
            for ((k, v) in mapString) {
                map2.getString(k).shouldBe(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, v))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "R1: put | R2: put, merge R2->R1" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = LWWMap(client1)
            val map2 = LWWMap(client2)

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
            map1.merge(map2)

            val iteratorBoolean = map1.iteratorBoolean()
            for ((k, v) in mapBoolean) {
                map1.getBoolean(k).shouldBe(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map1.iteratorDouble()
            for ((k, v) in mapDouble) {
                map1.getDouble(k).shouldBe(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, v))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map1.iteratorInt()
            for ((k, v) in mapInt) {
                map1.getInt(k).shouldBe(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, v))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map1.iteratorString()
            for ((k, v) in mapString) {
                map1.getString(k).shouldBe(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, v))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "R1: put, delete LWW; R2: put, merge" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = LWWMap(client1)
            val map2 = LWWMap(client2)

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map2.put(key, arbBoolean.next())
                map2.put(key, arbDouble.next())
                map2.put(key, arbInt.next())
                map2.put(key, arbString.next())
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

            keys.map { key ->
                map2.getBoolean(key).shouldBeNull()
                map2.getDouble(key).shouldBeNull()
                map2.getInt(key).shouldBeNull()
                map2.getString(key).shouldBeNull()
            }
            map2.iteratorBoolean().shouldBeEmpty()
            map2.iteratorDouble().shouldBeEmpty()
            map2.iteratorInt().shouldBeEmpty()
            map2.iteratorString().shouldBeEmpty()
        }
    }

    "R1: put, delete LWW; R2: put, merge before delete, merge after delete" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = LWWMap(client1)
            val map2 = LWWMap(client2)

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map2.put(key, arbBoolean.next())
                map2.put(key, arbDouble.next())
                map2.put(key, arbInt.next())
                map2.put(key, arbString.next())
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

            keys.map { key ->
                map2.getBoolean(key).shouldBeNull()
                map2.getDouble(key).shouldBeNull()
                map2.getInt(key).shouldBeNull()
                map2.getString(key).shouldBeNull()
            }
            map2.iteratorBoolean().shouldBeEmpty()
            map2.iteratorDouble().shouldBeEmpty()
            map2.iteratorInt().shouldBeEmpty()
            map2.iteratorString().shouldBeEmpty()
        }
    }

    "R1: put, delete; R2: put LWW, merge" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = LWWMap(client1)
            val map2 = LWWMap(client2)

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
                map2.getBoolean(k).shouldBe(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map2.getDouble(k).shouldBe(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, v))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map2.getInt(k).shouldBe(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, v))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorString()
            for ((k, v) in mapString) {
                map2.getString(k).shouldBe(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, v))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "R1: put, delete; R2: put LWW, merge before delete, merge after delete" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = LWWMap(client1)
            val map2 = LWWMap(client2)

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
                map2.getBoolean(k).shouldBe(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map2.getDouble(k).shouldBe(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, v))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map2.getInt(k).shouldBe(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, v))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorString()
            for ((k, v) in mapString) {
                map2.getString(k).shouldBe(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, v))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "R1: put | R2: put; merge R1->R3, R3: delete, merge R2->R3" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = LWWMap(client1)
            val map2 = LWWMap(client2)
            val map3 = LWWMap(client3)

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
                map2.put(key, arbBoolean.next())
                map2.put(key, arbDouble.next())
                map2.put(key, arbInt.next())
                map2.put(key, arbString.next())
            }
            map3.merge(map1)
            keys.map { key ->
                map3.deleteBoolean(key)
                map3.deleteDouble(key)
                map3.deleteInt(key)
                map3.deleteString(key)
            }
            map3.merge(map2)

            keys.map { key ->
                map3.getBoolean(key).shouldBeNull()
                map3.getDouble(key).shouldBeNull()
                map3.getInt(key).shouldBeNull()
                map3.getString(key).shouldBeNull()
            }
            map3.iteratorBoolean().shouldBeEmpty()
            map3.iteratorDouble().shouldBeEmpty()
            map3.iteratorInt().shouldBeEmpty()
            map3.iteratorString().shouldBeEmpty()
        }
    }

    "R1: put | R3: put; merge R1->R2, R2: delete, merge R3->R2" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = LWWMap(client1)
            val map2 = LWWMap(client2)
            val map3 = LWWMap(client3)

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
                map3.put(key, arbBoolean.next())
                map3.put(key, arbDouble.next())
                map3.put(key, arbInt.next())
                map3.put(key, arbString.next())
            }
            map2.merge(map1)
            keys.map { key ->
                map2.deleteBoolean(key)
                map2.deleteDouble(key)
                map2.deleteInt(key)
                map2.deleteString(key)
            }
            map2.merge(map3)

            keys.map { key ->
                map2.getBoolean(key).shouldBeNull()
                map2.getDouble(key).shouldBeNull()
                map2.getInt(key).shouldBeNull()
                map2.getString(key).shouldBeNull()
            }
            map2.iteratorBoolean().shouldBeEmpty()
            map2.iteratorDouble().shouldBeEmpty()
            map2.iteratorInt().shouldBeEmpty()
            map2.iteratorString().shouldBeEmpty()
        }
    }

    "use deltas returned by put" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = LWWMap(client1)
            val map2 = LWWMap(client2)

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

            val iteratorBoolean1 = map1.iteratorBoolean()
            val iteratorBoolean2 = map2.iteratorBoolean()
            for ((k, v) in mapBoolean) {
                map1.getBoolean(k).shouldBe(v)
                map2.getBoolean(k).shouldBe(v)
                iteratorBoolean1.shouldHaveNext()
                iteratorBoolean1.next().shouldBe(Pair(k, v))
                iteratorBoolean2.shouldHaveNext()
                iteratorBoolean2.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean1.shouldBeEmpty()
            iteratorBoolean2.shouldBeEmpty()
            val iteratorDouble1 = map1.iteratorDouble()
            val iteratorDouble2 = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map1.getDouble(k).shouldBe(v)
                map2.getDouble(k).shouldBe(v)
                iteratorDouble1.shouldHaveNext()
                iteratorDouble1.next().shouldBe(Pair(k, v))
                iteratorDouble2.shouldHaveNext()
                iteratorDouble2.next().shouldBe(Pair(k, v))
            }
            iteratorDouble1.shouldBeEmpty()
            iteratorDouble2.shouldBeEmpty()
            val iteratorInt1 = map1.iteratorInt()
            val iteratorInt2 = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map1.getInt(k).shouldBe(v)
                map2.getInt(k).shouldBe(v)
                iteratorInt1.shouldHaveNext()
                iteratorInt1.next().shouldBe(Pair(k, v))
                iteratorInt2.shouldHaveNext()
                iteratorInt2.next().shouldBe(Pair(k, v))
            }
            iteratorInt1.shouldBeEmpty()
            iteratorInt2.shouldBeEmpty()
            val iteratorString1 = map1.iteratorString()
            val iteratorString2 = map2.iteratorString()
            for ((k, v) in mapString) {
                map1.getString(k).shouldBe(v)
                map2.getString(k).shouldBe(v)
                iteratorString1.shouldHaveNext()
                iteratorString1.next().shouldBe(Pair(k, v))
                iteratorString2.shouldHaveNext()
                iteratorString2.next().shouldBe(Pair(k, v))
            }
            iteratorString1.shouldBeEmpty()
            iteratorString2.shouldBeEmpty()
        }
    }

    "use deltas returned by put and delete" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = LWWMap(client1)
            val map2 = LWWMap(client2)

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
        checkAll(500, Arb.list(Arb.string(0..1), 0..15), Arb.list(Arb.string(0..1), 0..15)) { keys1, keys2 ->
            val map1 = LWWMap(client1)
            val map2 = LWWMap(client2)
            val deltas = LWWMap()

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
                map1.getBoolean(k).shouldBe(v)
                map2.getBoolean(k).shouldBe(v)
                iteratorBoolean1.shouldHaveNext()
                iteratorBoolean1.next().shouldBe(Pair(k, v))
                iteratorBoolean2.shouldHaveNext()
                iteratorBoolean2.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean1.shouldBeEmpty()
            iteratorBoolean2.shouldBeEmpty()
            val iteratorDouble1 = map1.iteratorDouble()
            val iteratorDouble2 = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map1.getDouble(k).shouldBe(v)
                map2.getDouble(k).shouldBe(v)
                iteratorDouble1.shouldHaveNext()
                iteratorDouble1.next().shouldBe(Pair(k, v))
                iteratorDouble2.shouldHaveNext()
                iteratorDouble2.next().shouldBe(Pair(k, v))
            }
            iteratorDouble1.shouldBeEmpty()
            iteratorDouble2.shouldBeEmpty()
            val iteratorInt1 = map1.iteratorInt()
            val iteratorInt2 = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map1.getInt(k).shouldBe(v)
                map2.getInt(k).shouldBe(v)
                iteratorInt1.shouldHaveNext()
                iteratorInt1.next().shouldBe(Pair(k, v))
                iteratorInt2.shouldHaveNext()
                iteratorInt2.next().shouldBe(Pair(k, v))
            }
            iteratorInt1.shouldBeEmpty()
            iteratorInt2.shouldBeEmpty()
            val iteratorString1 = map1.iteratorString()
            val iteratorString2 = map2.iteratorString()
            for ((k, v) in mapString) {
                map1.getString(k).shouldBe(v)
                map2.getString(k).shouldBe(v)
                iteratorString1.shouldHaveNext()
                iteratorString1.next().shouldBe(Pair(k, v))
                iteratorString2.shouldHaveNext()
                iteratorString2.next().shouldBe(Pair(k, v))
            }
            iteratorString1.shouldBeEmpty()
            iteratorString2.shouldBeEmpty()
        }
    }

    "merge deltas returned by put and delete operations" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..15), Arb.list(Arb.string(0..1), 0..15)) { keys1, keys2 ->
            val map1 = LWWMap(client1)
            val map2 = LWWMap(client2)
            val deltas = LWWMap()

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

            val iteratorBoolean1 = map1.iteratorBoolean()
            val iteratorBoolean2 = map2.iteratorBoolean()
            for ((k, v) in mapBoolean) {
                map1.getBoolean(k).shouldBe(v)
                map2.getBoolean(k).shouldBe(v)
                iteratorBoolean1.shouldHaveNext()
                iteratorBoolean1.next().shouldBe(Pair(k, v))
                iteratorBoolean2.shouldHaveNext()
                iteratorBoolean2.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean1.shouldBeEmpty()
            iteratorBoolean2.shouldBeEmpty()
            val iteratorDouble1 = map1.iteratorDouble()
            val iteratorDouble2 = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map1.getDouble(k).shouldBe(v)
                map2.getDouble(k).shouldBe(v)
                iteratorDouble1.shouldHaveNext()
                iteratorDouble1.next().shouldBe(Pair(k, v))
                iteratorDouble2.shouldHaveNext()
                iteratorDouble2.next().shouldBe(Pair(k, v))
            }
            iteratorDouble1.shouldBeEmpty()
            iteratorDouble2.shouldBeEmpty()
            val iteratorInt1 = map1.iteratorInt()
            val iteratorInt2 = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map1.getInt(k).shouldBe(v)
                map2.getInt(k).shouldBe(v)
                iteratorInt1.shouldHaveNext()
                iteratorInt1.next().shouldBe(Pair(k, v))
                iteratorInt2.shouldHaveNext()
                iteratorInt2.next().shouldBe(Pair(k, v))
            }
            iteratorInt1.shouldBeEmpty()
            iteratorInt2.shouldBeEmpty()
            val iteratorString1 = map1.iteratorString()
            val iteratorString2 = map2.iteratorString()
            for ((k, v) in mapString) {
                map1.getString(k).shouldBe(v)
                map2.getString(k).shouldBe(v)
                iteratorString1.shouldHaveNext()
                iteratorString1.next().shouldBe(Pair(k, v))
                iteratorString2.shouldHaveNext()
                iteratorString2.next().shouldBe(Pair(k, v))
            }
            iteratorString1.shouldBeEmpty()
            iteratorString2.shouldBeEmpty()
        }
    }

    "generate delta" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..15), Arb.list(Arb.string(0..1), 0..15)) { keys1, keys2 ->
            val vv = VersionVector()
            val map1 = LWWMap(client1)
            val map2 = LWWMap(client2)

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

            val iteratorBoolean = map2.iteratorBoolean()
            for ((k, v) in mapBoolean) {
                map1.getBoolean(k).shouldBe(v)
                map2.getBoolean(k).shouldBe(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map1.getDouble(k).shouldBe(v)
                map2.getDouble(k).shouldBe(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, v))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map1.getInt(k).shouldBe(v)
                map2.getInt(k).shouldBe(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, v))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorString()
            for ((k, v) in mapString) {
                map1.getString(k).shouldBe(v)
                map2.getString(k).shouldBe(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, v))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "generate delta with delete" {
        checkAll(
            500, Arb.list(Arb.string(0..1), 0..15), Arb.list(Arb.string(0..1), 0..15),
            Arb.list(Arb.string(0..1), 0..15)
        ) { keys1, keys2, keys3 ->
            val vv = VersionVector()
            val map1 = LWWMap(client1)
            val map2 = LWWMap(client2)

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

            val iteratorBoolean = map2.iteratorBoolean()
            for ((k, v) in mapBoolean) {
                map1.getBoolean(k).shouldBe(v)
                map2.getBoolean(k).shouldBe(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorDouble()
            for ((k, v) in mapDouble) {
                map1.getDouble(k).shouldBe(v)
                map2.getDouble(k).shouldBe(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, v))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorInt()
            for ((k, v) in mapInt) {
                map1.getInt(k).shouldBe(v)
                map2.getInt(k).shouldBe(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, v))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorString()
            for ((k, v) in mapString) {
                map1.getString(k).shouldBe(v)
                map2.getString(k).shouldBe(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, v))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "deserialize is inverse to serialize" {
        checkAll<String, String, String, String, String>(750) { key1, key2, key3, key4, key5 ->
            val map = LWWMap(client1)

            val value1 = Arb.int().next()
            val value2 = Arb.string().next()
            val value3 = Arb.string().next()
            val value4 = Arb.bool().next()
            val value5 = Arb.double().next()

            map.put(key1, value1)
            map.put(key2, value2)
            map.deleteString(key2)
            map.put(key3, value3)
            map.put(key4, value4)
            map.put(key5, value5)

            val mapJson = map.toJson()

            mapJson.shouldBe(LWWMap.fromJson(mapJson).toJson())
        }
    }
})
