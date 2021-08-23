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

class MapPropTest : StringSpec({

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

    "LWW multiple put" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map = Map(client1)

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
                map.putLWW(key, valBoolean)
                map.putLWW(key, valDouble)
                map.putLWW(key, valInt)
                map.putLWW(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }

            val iteratorBoolean = map.iteratorLWWBoolean()
            for ((k, v) in mapBoolean) {
                map.getLWWBoolean(k).shouldBe(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean.shouldBeEmpty()

            val iteratorDouble = map.iteratorLWWDouble()
            for ((k, v) in mapDouble) {
                map.getLWWDouble(k).shouldBe(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, v))
            }
            iteratorDouble.shouldBeEmpty()

            val iteratorInt = map.iteratorLWWInt()
            for ((k, v) in mapInt) {
                map.getLWWInt(k).shouldBe(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, v))
            }
            iteratorInt.shouldBeEmpty()

            val iteratorString = map.iteratorLWWString()
            for ((k, v) in mapString) {
                map.getLWWString(k).shouldBe(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, v))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "LWW multiple put on same key Boolean, delete" {
        checkAll(Arb.list(Arb.bool())) { values ->
            val map = Map(client1)
            val key = Arb.string().next()

            values.map { value ->
                map.putLWW(key, value)
            }

            map.getLWWBoolean(key).shouldBe(values.lastOrNull())
            val iteratorBoolean = map.iteratorLWWBoolean()
            if (values.lastOrNull() != null) {
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(key, values.last()))
            }
            iteratorBoolean.shouldBeEmpty()

            map.deleteLWWBoolean(key)
            map.getLWWBoolean(key).shouldBeNull()
            map.iteratorLWWBoolean().shouldBeEmpty()
        }
    }

    "LWW multiple put on same key Double, delete" {
        checkAll(Arb.list(Arb.numericDoubles())) { values ->
            val map = Map(client1)
            val key = Arb.string().next()

            values.map { double ->
                map.putLWW(key, double)
            }

            map.getLWWDouble(key).shouldBe(values.lastOrNull())
            val iteratorDouble = map.iteratorLWWDouble()
            if (values.lastOrNull() != null) {
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(key, values.last()))
            }
            iteratorDouble.shouldBeEmpty()

            map.deleteLWWDouble(key)
            map.getLWWDouble(key).shouldBeNull()
            map.iteratorLWWDouble().shouldBeEmpty()
        }
    }

    "LWW multiple put on same key Int, delete" {
        checkAll(Arb.list(Arb.int())) { values ->
            val map = Map(client1)
            val key = Arb.string().next()

            values.map { value ->
                map.putLWW(key, value)
            }

            map.getLWWInt(key).shouldBe(values.lastOrNull())
            val iteratorInt = map.iteratorLWWInt()
            if (values.lastOrNull() != null) {
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(key, values.last()))
            }
            iteratorInt.shouldBeEmpty()

            map.deleteLWWInt(key)
            map.getLWWInt(key).shouldBeNull()
            map.iteratorLWWInt().shouldBeEmpty()
        }
    }

    "LWW multiple put on same key String, delete" {
        checkAll(Arb.list(Arb.string(0..1), 0..25)) { values ->
            val map = Map(client1)
            val key = Arb.string().next()

            values.map { value ->
                map.putLWW(key, value)
            }

            map.getLWWString(key).shouldBe(values.lastOrNull())
            val iteratorString = map.iteratorLWWString()
            if (values.lastOrNull() != null) {
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(key, values.last()))
            }
            iteratorString.shouldBeEmpty()

            map.deleteLWWString(key)
            map.getLWWString(key).shouldBeNull()
            map.iteratorLWWString().shouldBeEmpty()
        }
    }

    "LWW R1: put; R2: merge" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

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
                map1.putLWW(key, valBoolean)
                map1.putLWW(key, valDouble)
                map1.putLWW(key, valInt)
                map1.putLWW(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            map2.merge(map1)

            val iteratorBoolean = map2.iteratorLWWBoolean()
            for ((k, v) in mapBoolean) {
                map1.getLWWBoolean(k).shouldBe(v)
                map2.getLWWBoolean(k).shouldBe(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorLWWDouble()
            for ((k, v) in mapDouble) {
                map1.getLWWDouble(k).shouldBe(v)
                map2.getLWWDouble(k).shouldBe(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, v))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorLWWInt()
            for ((k, v) in mapInt) {
                map1.getLWWInt(k).shouldBe(v)
                map2.getLWWInt(k).shouldBe(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, v))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorLWWString()
            for ((k, v) in mapString) {
                map1.getLWWString(k).shouldBe(v)
                map2.getLWWString(k).shouldBe(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, v))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "LWW R1: put; R2: merge, put" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map1.putLWW(key, arbBoolean.next())
                map1.putLWW(key, arbDouble.next())
                map1.putLWW(key, arbInt.next())
                map1.putLWW(key, arbString.next())
            }
            map2.merge(map1)
            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map2.putLWW(key, valBoolean)
                map2.putLWW(key, valDouble)
                map2.putLWW(key, valInt)
                map2.putLWW(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }

            val iteratorBoolean = map2.iteratorLWWBoolean()
            for ((k, v) in mapBoolean) {
                map2.getLWWBoolean(k).shouldBe(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorLWWDouble()
            for ((k, v) in mapDouble) {
                map2.getLWWDouble(k).shouldBe(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, v))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorLWWInt()
            for ((k, v) in mapInt) {
                map2.getLWWInt(k).shouldBe(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, v))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorLWWString()
            for ((k, v) in mapString) {
                map2.getLWWString(k).shouldBe(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, v))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "LWW R1: put | R2: put, merge 1->2" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

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
                map1.putLWW(key, valBoolean)
                map1.putLWW(key, valDouble)
                map1.putLWW(key, valInt)
                map1.putLWW(key, valString)
            }
            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map2.putLWW(key, valBoolean)
                map2.putLWW(key, valDouble)
                map2.putLWW(key, valInt)
                map2.putLWW(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            map2.merge(map1)

            val iteratorBoolean = map2.iteratorLWWBoolean()
            for ((k, v) in mapBoolean) {
                map2.getLWWBoolean(k).shouldBe(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorLWWDouble()
            for ((k, v) in mapDouble) {
                map2.getLWWDouble(k).shouldBe(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, v))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorLWWInt()
            for ((k, v) in mapInt) {
                map2.getLWWInt(k).shouldBe(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, v))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorLWWString()
            for ((k, v) in mapString) {
                map2.getLWWString(k).shouldBe(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, v))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "LWW R1: put | R2: put, merge R2->R1" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map1.putLWW(key, arbBoolean.next())
                map1.putLWW(key, arbDouble.next())
                map1.putLWW(key, arbInt.next())
                map1.putLWW(key, arbString.next())
            }
            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map2.putLWW(key, valBoolean)
                map2.putLWW(key, valDouble)
                map2.putLWW(key, valInt)
                map2.putLWW(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            map1.merge(map2)

            val iteratorBoolean = map1.iteratorLWWBoolean()
            for ((k, v) in mapBoolean) {
                map1.getLWWBoolean(k).shouldBe(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map1.iteratorLWWDouble()
            for ((k, v) in mapDouble) {
                map1.getLWWDouble(k).shouldBe(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, v))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map1.iteratorLWWInt()
            for ((k, v) in mapInt) {
                map1.getLWWInt(k).shouldBe(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, v))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map1.iteratorLWWString()
            for ((k, v) in mapString) {
                map1.getLWWString(k).shouldBe(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, v))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "LWW R1: put, delete LWW; R2: put, merge" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map2.putLWW(key, arbBoolean.next())
                map2.putLWW(key, arbDouble.next())
                map2.putLWW(key, arbInt.next())
                map2.putLWW(key, arbString.next())
            }
            keys.map { key ->
                map1.putLWW(key, arbBoolean.next())
                map1.putLWW(key, arbDouble.next())
                map1.putLWW(key, arbInt.next())
                map1.putLWW(key, arbString.next())
            }
            keys.map { key ->
                map1.deleteLWWBoolean(key)
                map1.deleteLWWDouble(key)
                map1.deleteLWWInt(key)
                map1.deleteLWWString(key)
            }
            map2.merge(map1)

            keys.map { key ->
                map2.getLWWBoolean(key).shouldBeNull()
                map2.getLWWDouble(key).shouldBeNull()
                map2.getLWWInt(key).shouldBeNull()
                map2.getLWWString(key).shouldBeNull()
            }
            map2.iteratorLWWBoolean().shouldBeEmpty()
            map2.iteratorLWWDouble().shouldBeEmpty()
            map2.iteratorLWWInt().shouldBeEmpty()
            map2.iteratorLWWString().shouldBeEmpty()
        }
    }

    "LWW R1: put, delete LWW; R2: put, merge before delete, merge after delete" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map2.putLWW(key, arbBoolean.next())
                map2.putLWW(key, arbDouble.next())
                map2.putLWW(key, arbInt.next())
                map2.putLWW(key, arbString.next())
            }
            keys.map { key ->
                map1.putLWW(key, arbBoolean.next())
                map1.putLWW(key, arbDouble.next())
                map1.putLWW(key, arbInt.next())
                map1.putLWW(key, arbString.next())
            }
            map2.merge(map1)
            keys.map { key ->
                map1.deleteLWWBoolean(key)
                map1.deleteLWWDouble(key)
                map1.deleteLWWInt(key)
                map1.deleteLWWString(key)
            }
            map2.merge(map1)

            keys.map { key ->
                map2.getLWWBoolean(key).shouldBeNull()
                map2.getLWWDouble(key).shouldBeNull()
                map2.getLWWInt(key).shouldBeNull()
                map2.getLWWString(key).shouldBeNull()
            }
            map2.iteratorLWWBoolean().shouldBeEmpty()
            map2.iteratorLWWDouble().shouldBeEmpty()
            map2.iteratorLWWInt().shouldBeEmpty()
            map2.iteratorLWWString().shouldBeEmpty()
        }
    }

    "LWW R1: put, delete; R2: put LWW, merge" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map1.putLWW(key, arbBoolean.next())
                map1.putLWW(key, arbDouble.next())
                map1.putLWW(key, arbInt.next())
                map1.putLWW(key, arbString.next())
                client2.tick()
                client2.tick()
                client2.tick()
                client2.tick()
            }
            keys.map { key ->
                map1.deleteLWWBoolean(key)
                map1.deleteLWWDouble(key)
                map1.deleteLWWInt(key)
                map1.deleteLWWString(key)
            }
            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map2.putLWW(key, valBoolean)
                map2.putLWW(key, valDouble)
                map2.putLWW(key, valInt)
                map2.putLWW(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            map2.merge(map1)

            val iteratorBoolean = map2.iteratorLWWBoolean()
            for ((k, v) in mapBoolean) {
                map2.getLWWBoolean(k).shouldBe(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorLWWDouble()
            for ((k, v) in mapDouble) {
                map2.getLWWDouble(k).shouldBe(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, v))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorLWWInt()
            for ((k, v) in mapInt) {
                map2.getLWWInt(k).shouldBe(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, v))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorLWWString()
            for ((k, v) in mapString) {
                map2.getLWWString(k).shouldBe(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, v))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "LWW R1: put, delete; R2: put LWW, merge before delete, merge after delete" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map1.putLWW(key, arbBoolean.next())
                map1.putLWW(key, arbDouble.next())
                map1.putLWW(key, arbInt.next())
                map1.putLWW(key, arbString.next())
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
                map2.putLWW(key, valBoolean)
                map2.putLWW(key, valDouble)
                map2.putLWW(key, valInt)
                map2.putLWW(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            map2.merge(map1)
            keys.map { key ->
                map1.deleteLWWBoolean(key)
                map1.deleteLWWDouble(key)
                map1.deleteLWWInt(key)
                map1.deleteLWWString(key)
            }
            map2.merge(map1)

            val iteratorBoolean = map2.iteratorLWWBoolean()
            for ((k, v) in mapBoolean) {
                map2.getLWWBoolean(k).shouldBe(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorLWWDouble()
            for ((k, v) in mapDouble) {
                map2.getLWWDouble(k).shouldBe(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, v))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorLWWInt()
            for ((k, v) in mapInt) {
                map2.getLWWInt(k).shouldBe(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, v))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorLWWString()
            for ((k, v) in mapString) {
                map2.getLWWString(k).shouldBe(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, v))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "LWW R1: put | R2: put; merge R1->R3, R3: delete, merge R2->R3" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)
            val map3 = Map(client3)

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map1.putLWW(key, arbBoolean.next())
                map1.putLWW(key, arbDouble.next())
                map1.putLWW(key, arbInt.next())
                map1.putLWW(key, arbString.next())
            }
            keys.map { key ->
                map2.putLWW(key, arbBoolean.next())
                map2.putLWW(key, arbDouble.next())
                map2.putLWW(key, arbInt.next())
                map2.putLWW(key, arbString.next())
            }
            map3.merge(map1)
            keys.map { key ->
                map3.deleteLWWBoolean(key)
                map3.deleteLWWDouble(key)
                map3.deleteLWWInt(key)
                map3.deleteLWWString(key)
            }
            map3.merge(map2)

            keys.map { key ->
                map3.getLWWBoolean(key).shouldBeNull()
                map3.getLWWDouble(key).shouldBeNull()
                map3.getLWWInt(key).shouldBeNull()
                map3.getLWWString(key).shouldBeNull()
            }
            map3.iteratorLWWBoolean().shouldBeEmpty()
            map3.iteratorLWWDouble().shouldBeEmpty()
            map3.iteratorLWWInt().shouldBeEmpty()
            map3.iteratorLWWString().shouldBeEmpty()
        }
    }

    "LWW R1: put | R3: put; merge R1->R2, R2: delete, merge R3->R2" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)
            val map3 = Map(client3)

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map1.putLWW(key, arbBoolean.next())
                map1.putLWW(key, arbDouble.next())
                map1.putLWW(key, arbInt.next())
                map1.putLWW(key, arbString.next())
            }
            keys.map { key ->
                map3.putLWW(key, arbBoolean.next())
                map3.putLWW(key, arbDouble.next())
                map3.putLWW(key, arbInt.next())
                map3.putLWW(key, arbString.next())
            }
            map2.merge(map1)
            keys.map { key ->
                map2.deleteLWWBoolean(key)
                map2.deleteLWWDouble(key)
                map2.deleteLWWInt(key)
                map2.deleteLWWString(key)
            }
            map2.merge(map3)

            keys.map { key ->
                map2.getLWWBoolean(key).shouldBeNull()
                map2.getLWWDouble(key).shouldBeNull()
                map2.getLWWInt(key).shouldBeNull()
                map2.getLWWString(key).shouldBeNull()
            }
            map2.iteratorLWWBoolean().shouldBeEmpty()
            map2.iteratorLWWDouble().shouldBeEmpty()
            map2.iteratorLWWInt().shouldBeEmpty()
            map2.iteratorLWWString().shouldBeEmpty()
        }
    }

    "LWW use deltas returned by put" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

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
                map2.merge(map1.putLWW(key, valBoolean))
                map2.merge(map1.putLWW(key, valDouble))
                map2.merge(map1.putLWW(key, valInt))
                map2.merge(map1.putLWW(key, valString))
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }

            val iteratorBoolean1 = map1.iteratorLWWBoolean()
            val iteratorBoolean2 = map2.iteratorLWWBoolean()
            for ((k, v) in mapBoolean) {
                map1.getLWWBoolean(k).shouldBe(v)
                map2.getLWWBoolean(k).shouldBe(v)
                iteratorBoolean1.shouldHaveNext()
                iteratorBoolean1.next().shouldBe(Pair(k, v))
                iteratorBoolean2.shouldHaveNext()
                iteratorBoolean2.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean1.shouldBeEmpty()
            iteratorBoolean2.shouldBeEmpty()
            val iteratorDouble1 = map1.iteratorLWWDouble()
            val iteratorDouble2 = map2.iteratorLWWDouble()
            for ((k, v) in mapDouble) {
                map1.getLWWDouble(k).shouldBe(v)
                map2.getLWWDouble(k).shouldBe(v)
                iteratorDouble1.shouldHaveNext()
                iteratorDouble1.next().shouldBe(Pair(k, v))
                iteratorDouble2.shouldHaveNext()
                iteratorDouble2.next().shouldBe(Pair(k, v))
            }
            iteratorDouble1.shouldBeEmpty()
            iteratorDouble2.shouldBeEmpty()
            val iteratorInt1 = map1.iteratorLWWInt()
            val iteratorInt2 = map2.iteratorLWWInt()
            for ((k, v) in mapInt) {
                map1.getLWWInt(k).shouldBe(v)
                map2.getLWWInt(k).shouldBe(v)
                iteratorInt1.shouldHaveNext()
                iteratorInt1.next().shouldBe(Pair(k, v))
                iteratorInt2.shouldHaveNext()
                iteratorInt2.next().shouldBe(Pair(k, v))
            }
            iteratorInt1.shouldBeEmpty()
            iteratorInt2.shouldBeEmpty()
            val iteratorString1 = map1.iteratorLWWString()
            val iteratorString2 = map2.iteratorLWWString()
            for ((k, v) in mapString) {
                map1.getLWWString(k).shouldBe(v)
                map2.getLWWString(k).shouldBe(v)
                iteratorString1.shouldHaveNext()
                iteratorString1.next().shouldBe(Pair(k, v))
                iteratorString2.shouldHaveNext()
                iteratorString2.next().shouldBe(Pair(k, v))
            }
            iteratorString1.shouldBeEmpty()
            iteratorString2.shouldBeEmpty()
        }
    }

    "LWW use deltas returned by put and delete" {
        checkAll(500, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map2.merge(map1.putLWW(key, arbBoolean.next()))
                map2.merge(map1.putLWW(key, arbDouble.next()))
                map2.merge(map1.putLWW(key, arbInt.next()))
                map2.merge(map1.putLWW(key, arbString.next()))
            }
            keys.map { key ->
                map2.merge(map1.deleteLWWBoolean(key))
                map2.merge(map1.deleteLWWDouble(key))
                map2.merge(map1.deleteLWWInt(key))
                map2.merge(map1.deleteLWWString(key))
            }

            keys.map { key ->
                map1.getLWWBoolean(key).shouldBeNull()
                map1.getLWWDouble(key).shouldBeNull()
                map1.getLWWInt(key).shouldBeNull()
                map1.getLWWString(key).shouldBeNull()
                map2.getLWWBoolean(key).shouldBeNull()
                map2.getLWWDouble(key).shouldBeNull()
                map2.getLWWInt(key).shouldBeNull()
                map2.getLWWString(key).shouldBeNull()
            }
            map1.iteratorLWWBoolean().shouldBeEmpty()
            map1.iteratorLWWDouble().shouldBeEmpty()
            map1.iteratorLWWInt().shouldBeEmpty()
            map1.iteratorLWWString().shouldBeEmpty()
            map2.iteratorLWWBoolean().shouldBeEmpty()
            map2.iteratorLWWDouble().shouldBeEmpty()
            map2.iteratorLWWInt().shouldBeEmpty()
            map2.iteratorLWWString().shouldBeEmpty()
        }
    }

    "LWW merge deltas returned by put operations" {
        checkAll(
            250, Arb.list(Arb.string(0..1), 0..25),
            Arb.list(Arb.string(0..1), 0..25)
        ) { keys1, keys2 ->
            val map1 = Map(client1)
            val map2 = Map(client2)
            val deltas = Map()

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys1.map { key ->
                deltas.merge(map1.putLWW(key, arbBoolean.next()))
                deltas.merge(map1.putLWW(key, arbDouble.next()))
                deltas.merge(map1.putLWW(key, arbInt.next()))
                deltas.merge(map1.putLWW(key, arbString.next()))
            }
            keys1.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                deltas.merge(map1.putLWW(key, valBoolean))
                deltas.merge(map1.putLWW(key, valDouble))
                deltas.merge(map1.putLWW(key, valInt))
                deltas.merge(map1.putLWW(key, valString))
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
                deltas.merge(map1.putLWW(key, valBoolean))
                deltas.merge(map1.putLWW(key, valDouble))
                deltas.merge(map1.putLWW(key, valInt))
                deltas.merge(map1.putLWW(key, valString))
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            map2.merge(deltas)

            val iteratorBoolean1 = map1.iteratorLWWBoolean()
            val iteratorBoolean2 = map2.iteratorLWWBoolean()
            for ((k, v) in mapBoolean) {
                map1.getLWWBoolean(k).shouldBe(v)
                map2.getLWWBoolean(k).shouldBe(v)
                iteratorBoolean1.shouldHaveNext()
                iteratorBoolean1.next().shouldBe(Pair(k, v))
                iteratorBoolean2.shouldHaveNext()
                iteratorBoolean2.next().shouldBe(Pair(k, v))
            }
            iteratorBoolean1.shouldBeEmpty()
            iteratorBoolean2.shouldBeEmpty()
            val iteratorDouble1 = map1.iteratorLWWDouble()
            val iteratorDouble2 = map2.iteratorLWWDouble()
            for ((k, v) in mapDouble) {
                map1.getLWWDouble(k).shouldBe(v)
                map2.getLWWDouble(k).shouldBe(v)
                iteratorDouble1.shouldHaveNext()
                iteratorDouble1.next().shouldBe(Pair(k, v))
                iteratorDouble2.shouldHaveNext()
                iteratorDouble2.next().shouldBe(Pair(k, v))
            }
            iteratorDouble1.shouldBeEmpty()
            iteratorDouble2.shouldBeEmpty()
            val iteratorInt1 = map1.iteratorLWWInt()
            val iteratorInt2 = map2.iteratorLWWInt()
            for ((k, v) in mapInt) {
                map1.getLWWInt(k).shouldBe(v)
                map2.getLWWInt(k).shouldBe(v)
                iteratorInt1.shouldHaveNext()
                iteratorInt1.next().shouldBe(Pair(k, v))
                iteratorInt2.shouldHaveNext()
                iteratorInt2.next().shouldBe(Pair(k, v))
            }
            iteratorInt1.shouldBeEmpty()
            iteratorInt2.shouldBeEmpty()
            val iteratorString1 = map1.iteratorLWWString()
            val iteratorString2 = map2.iteratorLWWString()
            for ((k, v) in mapString) {
                map1.getLWWString(k).shouldBe(v)
                map2.getLWWString(k).shouldBe(v)
                iteratorString1.shouldHaveNext()
                iteratorString1.next().shouldBe(Pair(k, v))
                iteratorString2.shouldHaveNext()
                iteratorString2.next().shouldBe(Pair(k, v))
            }
            iteratorString1.shouldBeEmpty()
            iteratorString2.shouldBeEmpty()
        }
    }

    "LWW merge deltas returned by put and delete operations" {
        checkAll(
            250, Arb.list(Arb.string(0..1), 0..25),
            Arb.list(Arb.string(0..1), 0..25)
        ) { keys1, keys2 ->
            val map1 = Map(client1)
            val map2 = Map(client2)
            val deltas = Map()

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys1.map { key ->
                deltas.merge(map1.putLWW(key, arbBoolean.next()))
                deltas.merge(map1.putLWW(key, arbDouble.next()))
                deltas.merge(map1.putLWW(key, arbInt.next()))
                deltas.merge(map1.putLWW(key, arbString.next()))
            }
            keys1.map { key ->
                deltas.merge(map1.deleteLWWBoolean(key))
                deltas.merge(map1.deleteLWWDouble(key))
                deltas.merge(map1.deleteLWWInt(key))
                deltas.merge(map1.deleteLWWString(key))
            }
            keys2.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                deltas.merge(map1.putLWW(key, valBoolean))
                deltas.merge(map1.putLWW(key, valDouble))
                deltas.merge(map1.putLWW(key, valInt))
                deltas.merge(map1.putLWW(key, valString))
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            map2.merge(deltas)

            for ((k, v) in mapBoolean) {
                map1.getLWWBoolean(k).shouldBe(v)
                map2.getLWWBoolean(k).shouldBe(v)
            }
            for ((k, v) in mapDouble) {
                map1.getLWWDouble(k).shouldBe(v)
                map2.getLWWDouble(k).shouldBe(v)
            }
            for ((k, v) in mapInt) {
                map1.getLWWInt(k).shouldBe(v)
                map2.getLWWInt(k).shouldBe(v)
            }
            for ((k, v) in mapString) {
                map1.getLWWString(k).shouldBe(v)
                map2.getLWWString(k).shouldBe(v)
            }

            val iteratorBoolean = map2.iteratorLWWBoolean()
            for ((k, v) in iteratorBoolean) {
                map1.getLWWBoolean(k).shouldBe(v)
                map2.getLWWBoolean(k).shouldBe(v)
                mapBoolean[k].shouldBe(v)
            }
            val iteratorDouble = map2.iteratorLWWDouble()
            for ((k, v) in iteratorDouble) {
                map1.getLWWDouble(k).shouldBe(v)
                map2.getLWWDouble(k).shouldBe(v)
                mapDouble[k].shouldBe(v)
            }
            val iteratorInt = map2.iteratorLWWInt()
            for ((k, v) in iteratorInt) {
                map1.getLWWInt(k).shouldBe(v)
                map2.getLWWInt(k).shouldBe(v)
                mapInt[k].shouldBe(v)
            }
            val iteratorString = map2.iteratorLWWString()
            for ((k, v) in iteratorString) {
                map1.getLWWString(k).shouldBe(v)
                map2.getLWWString(k).shouldBe(v)
                mapString[k].shouldBe(v)
            }
        }
    }

    "LWW generate delta" {
        checkAll(
            250, Arb.list(Arb.string(0..1), 0..25),
            Arb.list(Arb.string(0..1), 0..25)
        ) { keys1, keys2 ->
            val vv = VersionVector()
            val map1 = Map(client1)
            val map2 = Map(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys1.map { key ->
                map1.putLWW(key, arbBoolean.next())
                map1.putLWW(key, arbDouble.next())
                map1.putLWW(key, arbInt.next())
                map1.putLWW(key, arbString.next())
            }
            vv.update(client1.tick())
            keys2.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map1.putLWW(key, valBoolean)
                map1.putLWW(key, valDouble)
                map1.putLWW(key, valInt)
                map1.putLWW(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            val delta = map1.generateDelta(vv)
            map2.merge(delta)

            for ((k, v) in mapBoolean) {
                map1.getLWWBoolean(k).shouldBe(v)
                map2.getLWWBoolean(k).shouldBe(v)
            }
            for ((k, v) in mapDouble) {
                map1.getLWWDouble(k).shouldBe(v)
                map2.getLWWDouble(k).shouldBe(v)
            }
            for ((k, v) in mapInt) {
                map1.getLWWInt(k).shouldBe(v)
                map2.getLWWInt(k).shouldBe(v)
            }
            for ((k, v) in mapString) {
                map1.getLWWString(k).shouldBe(v)
                map2.getLWWString(k).shouldBe(v)
            }

            val iteratorBoolean = map2.iteratorLWWBoolean()
            for ((k, v) in iteratorBoolean) {
                map1.getLWWBoolean(k).shouldBe(v)
                map2.getLWWBoolean(k).shouldBe(v)
                mapBoolean[k].shouldBe(v)
            }
            val iteratorDouble = map2.iteratorLWWDouble()
            for ((k, v) in iteratorDouble) {
                map1.getLWWDouble(k).shouldBe(v)
                map2.getLWWDouble(k).shouldBe(v)
                mapDouble[k].shouldBe(v)
            }
            val iteratorInt = map2.iteratorLWWInt()
            for ((k, v) in iteratorInt) {
                map1.getLWWInt(k).shouldBe(v)
                map2.getLWWInt(k).shouldBe(v)
                mapInt[k].shouldBe(v)
            }
            val iteratorString = map2.iteratorLWWString()
            for ((k, v) in iteratorString) {
                map1.getLWWString(k).shouldBe(v)
                map2.getLWWString(k).shouldBe(v)
                mapString[k].shouldBe(v)
            }
        }
    }

    "LWW generate delta with delete" {
        checkAll(
            250, Arb.list(Arb.string(0..1), 0..15), Arb.list(Arb.string(0..1), 0..15),
            Arb.list(Arb.string(0..1), 0..15)
        ) { keys1, keys2, keys3 ->
            val vv = VersionVector()
            val map1 = Map(client1)
            val map2 = Map(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys1.map { key ->
                map1.putLWW(key, arbBoolean.next())
                map1.putLWW(key, arbDouble.next())
                map1.putLWW(key, arbInt.next())
                map1.putLWW(key, arbString.next())
            }
            vv.update(client1.tick())
            keys2.map { key ->
                map1.putLWW(key, arbBoolean.next())
                map1.putLWW(key, arbDouble.next())
                map1.putLWW(key, arbInt.next())
                map1.putLWW(key, arbString.next())
            }
            keys2.map { key ->
                map1.deleteLWWBoolean(key)
                map1.deleteLWWDouble(key)
                map1.deleteLWWInt(key)
                map1.deleteLWWString(key)
            }
            keys3.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map1.putLWW(key, valBoolean)
                map1.putLWW(key, valDouble)
                map1.putLWW(key, valInt)
                map1.putLWW(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            val delta = map1.generateDelta(vv)
            map2.merge(delta)

            for ((k, v) in mapBoolean) {
                map1.getLWWBoolean(k).shouldBe(v)
                map2.getLWWBoolean(k).shouldBe(v)
            }
            for ((k, v) in mapDouble) {
                map1.getLWWDouble(k).shouldBe(v)
                map2.getLWWDouble(k).shouldBe(v)
            }
            for ((k, v) in mapInt) {
                map1.getLWWInt(k).shouldBe(v)
                map2.getLWWInt(k).shouldBe(v)
            }
            for ((k, v) in mapString) {
                map1.getLWWString(k).shouldBe(v)
                map2.getLWWString(k).shouldBe(v)
            }

            val iteratorBoolean = map2.iteratorLWWBoolean()
            for ((k, v) in iteratorBoolean) {
                map1.getLWWBoolean(k).shouldBe(v)
                map2.getLWWBoolean(k).shouldBe(v)
                mapBoolean[k].shouldBe(v)
            }
            val iteratorDouble = map2.iteratorLWWDouble()
            for ((k, v) in iteratorDouble) {
                map1.getLWWDouble(k).shouldBe(v)
                map2.getLWWDouble(k).shouldBe(v)
                mapDouble[k].shouldBe(v)
            }
            val iteratorInt = map2.iteratorLWWInt()
            for ((k, v) in iteratorInt) {
                map1.getLWWInt(k).shouldBe(v)
                map2.getLWWInt(k).shouldBe(v)
                mapInt[k].shouldBe(v)
            }
            val iteratorString = map2.iteratorLWWString()
            for ((k, v) in iteratorString) {
                map1.getLWWString(k).shouldBe(v)
                map2.getLWWString(k).shouldBe(v)
                mapString[k].shouldBe(v)
            }
        }
    }

    "MV multiple put" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map = Map(client1)

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
                map.putMV(key, valBoolean)
                map.putMV(key, valDouble)
                map.putMV(key, valInt)
                map.putMV(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }

            val iteratorBoolean = map.iteratorMVBoolean()
            for ((k, v) in mapBoolean) {
                map.getMVBoolean(k)!!.shouldHaveSingleElement(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorBoolean.shouldBeEmpty()

            val iteratorDouble = map.iteratorMVDouble()
            for ((k, v) in mapDouble) {
                map.getMVDouble(k)!!.shouldHaveSingleElement(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorDouble.shouldBeEmpty()

            val iteratorInt = map.iteratorMVInt()
            for ((k, v) in mapInt) {
                map.getMVInt(k)!!.shouldHaveSingleElement(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorInt.shouldBeEmpty()

            val iteratorString = map.iteratorMVString()
            for ((k, v) in mapString) {
                map.getMVString(k)!!.shouldHaveSingleElement(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "MV multiple put on same key Boolean, delete" {
        checkAll(Arb.list(Arb.bool())) { values ->
            val map = Map(client1)
            val key = Arb.string().next()

            values.map { value ->
                map.putMV(key, value)
            }

            map.getMVBoolean(key)?.shouldHaveSingleElement(values.lastOrNull())
            val iteratorBoolean = map.iteratorMVBoolean()
            if (values.lastOrNull() != null) {
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(key, setOf(values.last())))
            }
            iteratorBoolean.shouldBeEmpty()

            map.deleteMVBoolean(key)
            map.getMVBoolean(key).shouldBeNull()
            map.iteratorMVBoolean().shouldBeEmpty()
        }
    }

    "MV multiple put on same key Double, delete" {
        checkAll(Arb.list(Arb.numericDoubles())) { values ->
            val map = Map(client1)
            val key = Arb.string().next()

            values.map { double ->
                map.putMV(key, double)
            }

            map.getMVDouble(key)?.shouldHaveSingleElement(values.lastOrNull())
            val iteratorDouble = map.iteratorMVDouble()
            if (values.lastOrNull() != null) {
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(key, setOf(values.last())))
            }
            iteratorDouble.shouldBeEmpty()

            map.deleteMVDouble(key)
            map.getMVDouble(key).shouldBeNull()
            map.iteratorMVDouble().shouldBeEmpty()
        }
    }

    "MV multiple put on same key Int, delete" {
        checkAll(Arb.list(Arb.int())) { values ->
            val map = Map(client1)
            val key = Arb.string().next()

            values.map { value ->
                map.putMV(key, value)
            }

            map.getMVInt(key)?.shouldHaveSingleElement(values.lastOrNull())
            val iteratorInt = map.iteratorMVInt()
            if (values.lastOrNull() != null) {
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(key, setOf(values.last())))
            }
            iteratorInt.shouldBeEmpty()

            map.deleteMVInt(key)
            map.getMVInt(key).shouldBeNull()
            map.iteratorMVInt().shouldBeEmpty()
        }
    }

    "MV multiple put on same key String, delete" {
        checkAll(Arb.list(Arb.string(0..1), 0..25)) { values ->
            val map = Map(client1)
            val key = Arb.string().next()

            values.map { value ->
                map.putMV(key, value)
            }

            map.getMVString(key)?.shouldHaveSingleElement(values.lastOrNull())
            val iteratorString = map.iteratorMVString()
            if (values.lastOrNull() != null) {
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(key, setOf(values.last())))
            }
            iteratorString.shouldBeEmpty()

            map.deleteMVString(key)
            map.getMVString(key).shouldBeNull()
            map.iteratorMVString().shouldBeEmpty()
        }
    }

    "MV R1: put; R2: merge" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

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
                map1.putMV(key, valBoolean)
                map1.putMV(key, valDouble)
                map1.putMV(key, valInt)
                map1.putMV(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            map2.merge(map1)

            val iteratorBoolean = map2.iteratorMVBoolean()
            for ((k, v) in mapBoolean) {
                map1.getMVBoolean(k)!!.shouldHaveSingleElement(v)
                map2.getMVBoolean(k)!!.shouldHaveSingleElement(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorMVDouble()
            for ((k, v) in mapDouble) {
                map1.getMVDouble(k)!!.shouldHaveSingleElement(v)
                map2.getMVDouble(k)!!.shouldHaveSingleElement(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorMVInt()
            for ((k, v) in mapInt) {
                map1.getMVInt(k)!!.shouldHaveSingleElement(v)
                map2.getMVInt(k)!!.shouldHaveSingleElement(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorMVString()
            for ((k, v) in mapString) {
                map1.getMVString(k)!!.shouldHaveSingleElement(v)
                map2.getMVString(k)!!.shouldHaveSingleElement(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "MV R1: put; R2: merge, put" {
        checkAll(50, Arb.list(Arb.string(0..1), 0..10)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map1.putMV(key, arbBoolean.next())
                map1.putMV(key, arbDouble.next())
                map1.putMV(key, arbInt.next())
                map1.putMV(key, arbString.next())
            }
            map2.merge(map1)
            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map2.putMV(key, valBoolean)
                map2.putMV(key, valDouble)
                map2.putMV(key, valInt)
                map2.putMV(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }

            val iteratorBoolean = map2.iteratorMVBoolean()
            for ((k, v) in mapBoolean) {
                map2.getMVBoolean(k)!!.shouldHaveSingleElement(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorMVDouble()
            for ((k, v) in mapDouble) {
                map2.getMVDouble(k)!!.shouldHaveSingleElement(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorMVInt()
            for ((k, v) in mapInt) {
                map2.getMVInt(k)!!.shouldHaveSingleElement(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorMVString()
            for ((k, v) in mapString) {
                map2.getMVString(k)!!.shouldHaveSingleElement(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "MV R1: put; R2: put, merge" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

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
                map1.putMV(key, valBoolean)
                map1.putMV(key, valDouble)
                map1.putMV(key, valInt)
                map1.putMV(key, valString)
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
                map2.putMV(key, valBoolean)
                map2.putMV(key, valDouble)
                map2.putMV(key, valInt)
                map2.putMV(key, valString)
                mapBoolean2[key] = valBoolean
                mapDouble2[key] = valDouble
                mapInt2[key] = valInt
                mapString2[key] = valString
            }
            map2.merge(map1)

            val iteratorBoolean = map2.iteratorMVBoolean()
            for ((k, v) in mapBoolean2) {
                map2.getMVBoolean(k)!!.shouldContainExactlyInAnyOrder(setOf(v, mapBoolean1[k]))
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v, mapBoolean1[k])))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorMVDouble()
            for ((k, v) in mapDouble2) {
                map2.getMVDouble(k)!!.shouldContainExactlyInAnyOrder(setOf(v, mapDouble1[k]))
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v, mapDouble1[k])))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorMVInt()
            for ((k, v) in mapInt2) {
                map2.getMVInt(k)!!.shouldContainExactlyInAnyOrder(setOf(v, mapInt1[k]))
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v, mapInt1[k])))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorMVString()
            for ((k, v) in mapString2) {
                map2.getMVString(k)!!.shouldContainExactlyInAnyOrder(setOf(v, mapString1[k]))
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v, mapString1[k])))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "MV R1: put, delete; R2: put with older timestamp, merge" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

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
                map2.putMV(key, valBoolean)
                map2.putMV(key, valDouble)
                map2.putMV(key, valInt)
                map2.putMV(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            keys.map { key ->
                map1.putMV(key, arbBoolean.next())
                map1.putMV(key, arbDouble.next())
                map1.putMV(key, arbInt.next())
                map1.putMV(key, arbString.next())
            }
            keys.map { key ->
                map1.deleteMVBoolean(key)
                map1.deleteMVDouble(key)
                map1.deleteMVInt(key)
                map1.deleteMVString(key)
            }
            map2.merge(map1)

            val iteratorBoolean = map2.iteratorMVBoolean()
            for ((k, v) in mapBoolean) {
                map2.getMVBoolean(k)!!.shouldContainExactlyInAnyOrder(mapBoolean[k], null)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorMVDouble()
            for ((k, v) in mapDouble) {
                map2.getMVDouble(k)!!.shouldContainExactlyInAnyOrder(mapDouble[k], null)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorMVInt()
            for ((k, v) in mapInt) {
                map2.getMVInt(k)!!.shouldContainExactlyInAnyOrder(mapInt[k], null)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorMVString()
            for ((k, v) in mapString) {
                map2.getMVString(k)!!.shouldContainExactlyInAnyOrder(mapString[k], null)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "MV R1: put, delete; R2: put with older timestamp, merge before delete, merge after delete" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

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
                map2.putMV(key, valBoolean)
                map2.putMV(key, valDouble)
                map2.putMV(key, valInt)
                map2.putMV(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            keys.map { key ->
                map1.putMV(key, arbBoolean.next())
                map1.putMV(key, arbDouble.next())
                map1.putMV(key, arbInt.next())
                map1.putMV(key, arbString.next())
            }
            map2.merge(map1)
            keys.map { key ->
                map1.deleteMVBoolean(key)
                map1.deleteMVDouble(key)
                map1.deleteMVInt(key)
                map1.deleteMVString(key)
            }
            map2.merge(map1)

            val iteratorBoolean = map2.iteratorMVBoolean()
            for ((k, v) in mapBoolean) {
                map2.getMVBoolean(k)!!.shouldContainExactlyInAnyOrder(mapBoolean[k], null)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorMVDouble()
            for ((k, v) in mapDouble) {
                map2.getMVDouble(k)!!.shouldContainExactlyInAnyOrder(mapDouble[k], null)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorMVInt()
            for ((k, v) in mapInt) {
                map2.getMVInt(k)!!.shouldContainExactlyInAnyOrder(mapInt[k], null)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorMVString()
            for ((k, v) in mapString) {
                map2.getMVString(k)!!.shouldContainExactlyInAnyOrder(mapString[k], null)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "MV R1: put, delete; R2: put with newer timestamp, merge" {
        checkAll(200, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map1.putMV(key, arbBoolean.next())
                map1.putMV(key, arbDouble.next())
                map1.putMV(key, arbInt.next())
                map1.putMV(key, arbString.next())
                client2.tick()
                client2.tick()
                client2.tick()
                client2.tick()
            }
            keys.map { key ->
                map1.deleteMVBoolean(key)
                map1.deleteMVDouble(key)
                map1.deleteMVInt(key)
                map1.deleteMVString(key)
            }

            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map2.putMV(key, valBoolean)
                map2.putMV(key, valDouble)
                map2.putMV(key, valInt)
                map2.putMV(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            map2.merge(map1)

            val iteratorBoolean = map2.iteratorMVBoolean()
            for ((k, v) in mapBoolean) {
                map2.getMVBoolean(k)!!.shouldContainExactlyInAnyOrder(mapBoolean[k], null)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorMVDouble()
            for ((k, v) in mapDouble) {
                map2.getMVDouble(k)!!.shouldContainExactlyInAnyOrder(mapDouble[k], null)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorMVInt()
            for ((k, v) in mapInt) {
                map2.getMVInt(k)!!.shouldContainExactlyInAnyOrder(mapInt[k], null)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorMVString()
            for ((k, v) in mapString) {
                map2.getMVString(k)!!.shouldContainExactlyInAnyOrder(mapString[k], null)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "MV R1: put, delete; R2: put with newer timestamp, merge before delete, merge after delete" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map1.putMV(key, arbBoolean.next())
                map1.putMV(key, arbDouble.next())
                map1.putMV(key, arbInt.next())
                map1.putMV(key, arbString.next())
            }
            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map2.putMV(key, valBoolean)
                map2.putMV(key, valDouble)
                map2.putMV(key, valInt)
                map2.putMV(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            map2.merge(map1)
            keys.map { key ->
                map1.deleteMVBoolean(key)
                map1.deleteMVDouble(key)
                map1.deleteMVInt(key)
                map1.deleteMVString(key)
            }
            map2.merge(map1)

            val iteratorBoolean = map2.iteratorMVBoolean()
            for ((k, v) in mapBoolean) {
                map2.getMVBoolean(k)!!.shouldContainExactlyInAnyOrder(mapBoolean[k], null)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorMVDouble()
            for ((k, v) in mapDouble) {
                map2.getMVDouble(k)!!.shouldContainExactlyInAnyOrder(mapDouble[k], null)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorMVInt()
            for ((k, v) in mapInt) {
                map2.getMVInt(k)!!.shouldContainExactlyInAnyOrder(mapInt[k], null)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorMVString()
            for ((k, v) in mapString) {
                map2.getMVString(k)!!.shouldContainExactlyInAnyOrder(mapString[k], null)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "MV R1: put; R2: put; R3: merge R1, delete, merge R2" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..15)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)
            val map3 = Map(client3)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map1.putMV(key, arbBoolean.next())
                map1.putMV(key, arbDouble.next())
                map1.putMV(key, arbInt.next())
                map1.putMV(key, arbString.next())
            }
            map3.merge(map1)
            keys.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map2.putMV(key, valBoolean)
                map2.putMV(key, valDouble)
                map2.putMV(key, valInt)
                map2.putMV(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            keys.map { key ->
                map3.deleteMVBoolean(key)
                map3.deleteMVDouble(key)
                map3.deleteMVInt(key)
                map3.deleteMVString(key)
            }
            map3.merge(map2)

            val iteratorBoolean = map3.iteratorMVBoolean()
            for ((k, v) in mapBoolean) {
                map3.getMVBoolean(k)!!.shouldContainExactlyInAnyOrder(mapBoolean[k], null)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map3.iteratorMVDouble()
            for ((k, v) in mapDouble) {
                map3.getMVDouble(k)!!.shouldContainExactlyInAnyOrder(mapDouble[k], null)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map3.iteratorMVInt()
            for ((k, v) in mapInt) {
                map3.getMVInt(k)!!.shouldContainExactlyInAnyOrder(mapInt[k], null)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map3.iteratorMVString()
            for ((k, v) in mapString) {
                map3.getMVString(k)!!.shouldContainExactlyInAnyOrder(mapString[k], null)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v, null)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "MV use deltas returned by put" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

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
                map2.merge(map1.putMV(key, valBoolean))
                map2.merge(map1.putMV(key, valDouble))
                map2.merge(map1.putMV(key, valInt))
                map2.merge(map1.putMV(key, valString))
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }

            val iteratorBoolean = map2.iteratorMVBoolean()
            for ((k, v) in mapBoolean) {
                map1.getMVBoolean(k)!!.shouldHaveSingleElement(v)
                map2.getMVBoolean(k)!!.shouldHaveSingleElement(v)
                iteratorBoolean.shouldHaveNext()
                iteratorBoolean.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorBoolean.shouldBeEmpty()
            val iteratorDouble = map2.iteratorMVDouble()
            for ((k, v) in mapDouble) {
                map1.getMVDouble(k)!!.shouldHaveSingleElement(v)
                map2.getMVDouble(k)!!.shouldHaveSingleElement(v)
                iteratorDouble.shouldHaveNext()
                iteratorDouble.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorDouble.shouldBeEmpty()
            val iteratorInt = map2.iteratorMVInt()
            for ((k, v) in mapInt) {
                map1.getMVInt(k)!!.shouldHaveSingleElement(v)
                map2.getMVInt(k)!!.shouldHaveSingleElement(v)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorInt.shouldBeEmpty()
            val iteratorString = map2.iteratorMVString()
            for ((k, v) in mapString) {
                map1.getMVString(k)!!.shouldHaveSingleElement(v)
                map2.getMVString(k)!!.shouldHaveSingleElement(v)
                iteratorString.shouldHaveNext()
                iteratorString.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorString.shouldBeEmpty()
        }
    }

    "MV use deltas returned by put and delete" {
        checkAll(250, Arb.list(Arb.string(0..1), 0..25)) { keys ->
            val map1 = Map(client1)
            val map2 = Map(client2)

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys.map { key ->
                map2.merge(map1.putMV(key, arbBoolean.next()))
                map2.merge(map1.putMV(key, arbDouble.next()))
                map2.merge(map1.putMV(key, arbInt.next()))
                map2.merge(map1.putMV(key, arbString.next()))
            }
            keys.map { key ->
                map2.merge(map1.deleteMVBoolean(key))
                map2.merge(map1.deleteMVDouble(key))
                map2.merge(map1.deleteMVInt(key))
                map2.merge(map1.deleteMVString(key))
            }

            keys.map { key ->
                map1.getMVBoolean(key).shouldBeNull()
                map1.getMVDouble(key).shouldBeNull()
                map1.getMVInt(key).shouldBeNull()
                map1.getMVString(key).shouldBeNull()
                map2.getMVBoolean(key).shouldBeNull()
                map2.getMVDouble(key).shouldBeNull()
                map2.getMVInt(key).shouldBeNull()
                map2.getMVString(key).shouldBeNull()
            }
            map1.iteratorMVBoolean().shouldBeEmpty()
            map1.iteratorMVDouble().shouldBeEmpty()
            map1.iteratorMVInt().shouldBeEmpty()
            map1.iteratorMVString().shouldBeEmpty()
            map2.iteratorMVBoolean().shouldBeEmpty()
            map2.iteratorMVDouble().shouldBeEmpty()
            map2.iteratorMVInt().shouldBeEmpty()
            map2.iteratorMVString().shouldBeEmpty()
        }
    }

    "MV merge deltas returned by put operations" {
        checkAll(
            250, Arb.list(Arb.string(0..1), 0..15),
            Arb.list(Arb.string(0..1), 0..15)
        ) { keys1, keys2 ->
            val map1 = Map(client1)
            val map2 = Map(client2)
            val deltas = Map()

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys1.map { key ->
                deltas.merge(map1.putMV(key, arbBoolean.next()))
                deltas.merge(map1.putMV(key, arbDouble.next()))
                deltas.merge(map1.putMV(key, arbInt.next()))
                deltas.merge(map1.putMV(key, arbString.next()))
            }
            keys1.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                deltas.merge(map1.putMV(key, valBoolean))
                deltas.merge(map1.putMV(key, valDouble))
                deltas.merge(map1.putMV(key, valInt))
                deltas.merge(map1.putMV(key, valString))
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
                deltas.merge(map1.putMV(key, valBoolean))
                deltas.merge(map1.putMV(key, valDouble))
                deltas.merge(map1.putMV(key, valInt))
                deltas.merge(map1.putMV(key, valString))
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            map2.merge(deltas)

            val iteratorBoolean1 = map1.iteratorMVBoolean()
            val iteratorBoolean2 = map2.iteratorMVBoolean()
            for ((k, v) in mapBoolean) {
                map1.getMVBoolean(k)!!.shouldHaveSingleElement(v)
                map2.getMVBoolean(k)!!.shouldHaveSingleElement(v)
                iteratorBoolean1.shouldHaveNext()
                iteratorBoolean1.next().shouldBe(Pair(k, setOf(v)))
                iteratorBoolean2.shouldHaveNext()
                iteratorBoolean2.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorBoolean1.shouldBeEmpty()
            iteratorBoolean2.shouldBeEmpty()
            val iteratorDouble1 = map1.iteratorMVDouble()
            val iteratorDouble2 = map2.iteratorMVDouble()
            for ((k, v) in mapDouble) {
                map1.getMVDouble(k)!!.shouldHaveSingleElement(v)
                map2.getMVDouble(k)!!.shouldHaveSingleElement(v)
                iteratorDouble1.shouldHaveNext()
                iteratorDouble1.next().shouldBe(Pair(k, setOf(v)))
                iteratorDouble2.shouldHaveNext()
                iteratorDouble2.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorDouble1.shouldBeEmpty()
            iteratorDouble2.shouldBeEmpty()
            val iteratorInt1 = map1.iteratorMVInt()
            val iteratorInt2 = map2.iteratorMVInt()
            for ((k, v) in mapInt) {
                map1.getMVInt(k)!!.shouldHaveSingleElement(v)
                map2.getMVInt(k)!!.shouldHaveSingleElement(v)
                iteratorInt1.shouldHaveNext()
                iteratorInt1.next().shouldBe(Pair(k, setOf(v)))
                iteratorInt2.shouldHaveNext()
                iteratorInt2.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorInt1.shouldBeEmpty()
            iteratorInt2.shouldBeEmpty()
            val iteratorString1 = map1.iteratorMVString()
            val iteratorString2 = map2.iteratorMVString()
            for ((k, v) in mapString) {
                map1.getMVString(k)!!.shouldHaveSingleElement(v)
                map2.getMVString(k)!!.shouldHaveSingleElement(v)
                iteratorString1.shouldHaveNext()
                iteratorString1.next().shouldBe(Pair(k, setOf(v)))
                iteratorString2.shouldHaveNext()
                iteratorString2.next().shouldBe(Pair(k, setOf(v)))
            }
            iteratorString1.shouldBeEmpty()
            iteratorString2.shouldBeEmpty()
        }
    }

    "MV merge deltas returned by put and delete operations" {
        checkAll(200, Arb.list(Arb.string(0..1), 0..15), Arb.list(Arb.string(0..1), 0..15)) { keys1, keys2 ->
            val map1 = Map(client1)
            val map2 = Map(client2)
            val deltas = Map()

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys1.map { key ->
                deltas.merge(map1.putMV(key, arbBoolean.next()))
                deltas.merge(map1.putMV(key, arbDouble.next()))
                deltas.merge(map1.putMV(key, arbInt.next()))
                deltas.merge(map1.putMV(key, arbString.next()))
            }
            keys1.map { key ->
                deltas.merge(map1.deleteMVBoolean(key))
                deltas.merge(map1.deleteMVDouble(key))
                deltas.merge(map1.deleteMVInt(key))
                deltas.merge(map1.deleteMVString(key))
            }
            keys2.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                deltas.merge(map1.putMV(key, valBoolean))
                deltas.merge(map1.putMV(key, valDouble))
                deltas.merge(map1.putMV(key, valInt))
                deltas.merge(map1.putMV(key, valString))
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            map2.merge(deltas)

            for ((k, v) in mapBoolean) {
                map1.getMVBoolean(k)!!.shouldHaveSingleElement(v)
                map2.getMVBoolean(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapDouble) {
                map1.getMVDouble(k)!!.shouldHaveSingleElement(v)
                map2.getMVDouble(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapInt) {
                map1.getMVInt(k)!!.shouldHaveSingleElement(v)
                map2.getMVInt(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapString) {
                map1.getMVString(k)!!.shouldHaveSingleElement(v)
                map2.getMVString(k)!!.shouldHaveSingleElement(v)
            }

            val iteratorBoolean = map2.iteratorMVBoolean()
            for ((k, v) in iteratorBoolean) {
                map1.getMVBoolean(k).shouldBe(v)
                map2.getMVBoolean(k).shouldBe(v)
                v.shouldHaveSingleElement(mapBoolean[k])
            }
            val iteratorDouble = map2.iteratorMVDouble()
            for ((k, v) in iteratorDouble) {
                map1.getMVDouble(k).shouldBe(v)
                map2.getMVDouble(k).shouldBe(v)
                v.shouldHaveSingleElement(mapDouble[k])
            }
            val iteratorInt = map2.iteratorMVInt()
            for ((k, v) in iteratorInt) {
                map1.getMVInt(k).shouldBe(v)
                map2.getMVInt(k).shouldBe(v)
                v.shouldHaveSingleElement(mapInt[k])
            }
            val iteratorString = map2.iteratorMVString()
            for ((k, v) in iteratorString) {
                map1.getMVString(k).shouldBe(v)
                map2.getMVString(k).shouldBe(v)
                v.shouldHaveSingleElement(mapString[k])
            }
        }
    }

    "MV generate delta" {
        checkAll(
            250, Arb.list(Arb.string(0..1), 0..15),
            Arb.list(Arb.string(0..1), 0..15)
        ) { keys1, keys2 ->
            val vv = VersionVector()
            val map1 = Map(client1)
            val map2 = Map(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys1.map { key ->
                map1.putMV(key, arbBoolean.next())
                map1.putMV(key, arbDouble.next())
                map1.putMV(key, arbInt.next())
                map1.putMV(key, arbString.next())
            }
            vv.update(client1.tick())
            keys2.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map1.putMV(key, valBoolean)
                map1.putMV(key, valDouble)
                map1.putMV(key, valInt)
                map1.putMV(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            val delta = map1.generateDelta(vv)
            map2.merge(delta)

            for ((k, v) in mapBoolean) {
                map1.getMVBoolean(k)!!.shouldHaveSingleElement(v)
                map2.getMVBoolean(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapDouble) {
                map1.getMVDouble(k)!!.shouldHaveSingleElement(v)
                map2.getMVDouble(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapInt) {
                map1.getMVInt(k)!!.shouldHaveSingleElement(v)
                map2.getMVInt(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapString) {
                map1.getMVString(k)!!.shouldHaveSingleElement(v)
                map2.getMVString(k)!!.shouldHaveSingleElement(v)
            }

            val iteratorBoolean = map2.iteratorMVBoolean()
            for ((k, v) in iteratorBoolean) {
                map1.getMVBoolean(k).shouldBe(v)
                map2.getMVBoolean(k).shouldBe(v)
                v.shouldHaveSingleElement(mapBoolean[k])
            }
            val iteratorDouble = map2.iteratorMVDouble()
            for ((k, v) in iteratorDouble) {
                map1.getMVDouble(k).shouldBe(v)
                map2.getMVDouble(k).shouldBe(v)
                v.shouldHaveSingleElement(mapDouble[k])
            }
            val iteratorInt = map2.iteratorMVInt()
            for ((k, v) in iteratorInt) {
                map1.getMVInt(k).shouldBe(v)
                map2.getMVInt(k).shouldBe(v)
                v.shouldHaveSingleElement(mapInt[k])
            }
            val iteratorString = map2.iteratorMVString()
            for ((k, v) in iteratorString) {
                map1.getMVString(k).shouldBe(v)
                map2.getMVString(k).shouldBe(v)
                v.shouldHaveSingleElement(mapString[k])
            }
        }
    }

    "MV generate delta with delete" {
        checkAll(
            200, Arb.list(Arb.string(0..1), 0..15),
            Arb.list(
                Arb.string(0..1),
                0..15
            ),
            Arb.list(Arb.string(0..1), 0..15)
        ) { keys1, keys2, keys3 ->
            val vv = VersionVector()
            val map1 = Map(client1)
            val map2 = Map(client2)

            val mapBoolean = mutableMapOf<String, Boolean>()
            val mapDouble = mutableMapOf<String, Double>()
            val mapInt = mutableMapOf<String, Int>()
            val mapString = mutableMapOf<String, String>()

            val arbBoolean = Arb.bool()
            val arbDouble = Arb.double()
            val arbInt = Arb.int()
            val arbString = Arb.string()

            keys1.map { key ->
                map1.putMV(key, arbBoolean.next())
                map1.putMV(key, arbDouble.next())
                map1.putMV(key, arbInt.next())
                map1.putMV(key, arbString.next())
            }
            vv.update(client1.tick())
            keys2.map { key ->
                map1.putMV(key, arbBoolean.next())
                map1.putMV(key, arbDouble.next())
                map1.putMV(key, arbInt.next())
                map1.putMV(key, arbString.next())
            }
            keys2.map { key ->
                map1.deleteMVBoolean(key)
                map1.deleteMVDouble(key)
                map1.deleteMVInt(key)
                map1.deleteMVString(key)
            }
            keys3.map { key ->
                val valBoolean = arbBoolean.next()
                val valDouble = arbDouble.next()
                val valInt = arbInt.next()
                val valString = arbString.next()
                map1.putMV(key, valBoolean)
                map1.putMV(key, valDouble)
                map1.putMV(key, valInt)
                map1.putMV(key, valString)
                mapBoolean[key] = valBoolean
                mapDouble[key] = valDouble
                mapInt[key] = valInt
                mapString[key] = valString
            }
            val delta = map1.generateDelta(vv)
            map2.merge(delta)

            for ((k, v) in mapBoolean) {
                map1.getMVBoolean(k)!!.shouldHaveSingleElement(v)
                map2.getMVBoolean(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapDouble) {
                map1.getMVDouble(k)!!.shouldHaveSingleElement(v)
                map2.getMVDouble(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapInt) {
                map1.getMVInt(k)!!.shouldHaveSingleElement(v)
                map2.getMVInt(k)!!.shouldHaveSingleElement(v)
            }
            for ((k, v) in mapString) {
                map1.getMVString(k)!!.shouldHaveSingleElement(v)
                map2.getMVString(k)!!.shouldHaveSingleElement(v)
            }

            val iteratorBoolean = map2.iteratorMVBoolean()
            for ((k, v) in iteratorBoolean) {
                map1.getMVBoolean(k).shouldBe(v)
                map2.getMVBoolean(k).shouldBe(v)
                v.shouldHaveSingleElement(mapBoolean[k])
            }
            val iteratorDouble = map2.iteratorMVDouble()
            for ((k, v) in iteratorDouble) {
                map1.getMVDouble(k).shouldBe(v)
                map2.getMVDouble(k).shouldBe(v)
                v.shouldHaveSingleElement(mapDouble[k])
            }
            val iteratorInt = map2.iteratorMVInt()
            for ((k, v) in iteratorInt) {
                map1.getMVInt(k).shouldBe(v)
                map2.getMVInt(k).shouldBe(v)
                v.shouldHaveSingleElement(mapInt[k])
            }
            val iteratorString = map2.iteratorMVString()
            for ((k, v) in iteratorString) {
                map1.getMVString(k).shouldBe(v)
                map2.getMVString(k).shouldBe(v)
                v.shouldHaveSingleElement(mapString[k])
            }
        }
    }

    "CNT increments/decrement" {
        checkAll(Arb.list(CounterOperationArb, 0..100)) { ops ->
            var res = 0
            val map = Map(client1)

            val key = Arb.string().next()

            ops.map { op ->
                when (op.first) {
                    CounterOpType.INCR -> {
                        res += op.second
                        map.increment(key, op.second)
                    }
                    CounterOpType.DECR -> {
                        res -= op.second
                        map.decrement(key, op.second)
                    }
                }
            }

            val iteratorInt = map.iteratorCntInt()
            if (ops.isNotEmpty()) {
                map.getCntInt(key).shouldBe(res)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(key, res))
            }
            iteratorInt.shouldBeEmpty()
        }
    }

    "CNT R1: increment/decrement; R2: merge" {
        checkAll(Arb.list(CounterOperationArb, 0..100)) { ops ->
            var res = 0
            val map1 = Map(client1)
            val map2 = Map(client2)

            val key = Arb.string().next()

            ops.map { op ->
                when (op.first) {
                    CounterOpType.INCR -> {
                        res += op.second
                        map1.increment(key, op.second)
                    }
                    CounterOpType.DECR -> {
                        res -= op.second
                        map1.decrement(key, op.second)
                    }
                }
            }
            map2.merge(map1)

            val iteratorInt1 = map1.iteratorCntInt()
            if (ops.isNotEmpty()) {
                map1.getCntInt(key).shouldBe(res)
                iteratorInt1.shouldHaveNext()
                iteratorInt1.next().shouldBe(Pair(key, res))
            }
            iteratorInt1.shouldBeEmpty()

            val iteratorInt2 = map2.iteratorCntInt()
            if (ops.isNotEmpty()) {
                map2.getCntInt(key).shouldBe(res)
                iteratorInt2.shouldHaveNext()
                iteratorInt2.next().shouldBe(Pair(key, res))
            }
            iteratorInt2.shouldBeEmpty()
        }
    }

    "CNT R1: multiple operations; R2: multiple operations, merge" {
        checkAll(Arb.list(CounterOperationArb, 0..100)) { ops ->
            var res = 0
            val map1 = Map(client1)
            val map2 = Map(client2)

            val key = Arb.string().next()

            val subListSize = Arb.int(0..ops.size).next()
            val ops1 = ops.subList(0, subListSize)
            val ops2 = ops.subList(subListSize, ops.size)

            ops1.map { op ->
                when (op.first) {
                    CounterOpType.INCR -> {
                        res += op.second
                        map1.increment(key, op.second)
                    }
                    CounterOpType.DECR -> {
                        res -= op.second
                        map1.decrement(key, op.second)
                    }
                }
            }

            ops2.map { op ->
                when (op.first) {
                    CounterOpType.INCR -> {
                        res += op.second
                        map2.increment(key, op.second)
                    }
                    CounterOpType.DECR -> {
                        res -= op.second
                        map2.decrement(key, op.second)
                    }
                }
            }
            map2.merge(map1)

            val iteratorInt2 = map2.iteratorCntInt()
            if (ops.isNotEmpty()) {
                map2.getCntInt(key).shouldBe(res)
                iteratorInt2.shouldHaveNext()
                iteratorInt2.next().shouldBe(Pair(key, res))
            }
            iteratorInt2.shouldBeEmpty()
        }
    }

    "CNT R1: multiple operations; R2: merge, multiple operations" {
        checkAll(Arb.list(CounterOperationArb, 0..100)) { ops ->
            var res = 0
            val map1 = Map(client1)
            val map2 = Map(client2)

            val key = Arb.string().next()

            val subListSize = Arb.int(0..ops.size).next()
            val ops1 = ops.subList(0, subListSize)
            val ops2 = ops.subList(subListSize, ops.size)

            ops1.map { op ->
                when (op.first) {
                    CounterOpType.INCR -> {
                        res += op.second
                        map1.increment(key, op.second)
                    }
                    CounterOpType.DECR -> {
                        res -= op.second
                        map1.decrement(key, op.second)
                    }
                }
            }
            map2.merge(map1)

            ops2.map { op ->
                when (op.first) {
                    CounterOpType.INCR -> {
                        res += op.second
                        map2.increment(key, op.second)
                    }
                    CounterOpType.DECR -> {
                        res -= op.second
                        map2.decrement(key, op.second)
                    }
                }
            }

            val iteratorInt2 = map2.iteratorCntInt()
            if (ops.isNotEmpty()) {
                map2.getCntInt(key).shouldBe(res)
                iteratorInt2.shouldHaveNext()
                iteratorInt2.next().shouldBe(Pair(key, res))
            }
            iteratorInt2.shouldBeEmpty()
        }
    }

    "CNT use delta return by increment and decrement" {
        checkAll(Arb.list(CounterOperationArb, 0..100)) { ops ->
            var res = 0
            val map1 = Map(client1)
            val map2 = Map(client2)

            val key = Arb.string().next()

            ops.map { op ->
                when (op.first) {
                    CounterOpType.INCR -> {
                        res += op.second
                        map2.merge(map1.increment(key, op.second))
                    }
                    CounterOpType.DECR -> {
                        res -= op.second
                        map2.merge(map1.decrement(key, op.second))
                    }
                }
            }

            val iteratorInt1 = map1.iteratorCntInt()
            if (ops.isNotEmpty()) {
                map1.getCntInt(key).shouldBe(res)
                iteratorInt1.shouldHaveNext()
                iteratorInt1.next().shouldBe(Pair(key, res))
            }
            iteratorInt1.shouldBeEmpty()

            val iteratorInt2 = map2.iteratorCntInt()
            if (ops.isNotEmpty()) {
                map2.getCntInt(key).shouldBe(res)
                iteratorInt2.shouldHaveNext()
                iteratorInt2.next().shouldBe(Pair(key, res))
            }
            iteratorInt2.shouldBeEmpty()
        }
    }

    "CNT generate delta" {
        checkAll(Arb.list(Arb.positiveInts(1000), 0..100)) { ops ->
            var res = 0
            val map1 = Map(client1)
            val map2 = Map(client2)

            val key = Arb.string().next()

            val subListSize = Arb.int(0..ops.size).next()
            val ops1 = ops.subList(0, subListSize)
            val ops2 = ops.subList(subListSize, ops.size)

            ops1.map { op ->
                map1.increment(key, op)
            }
            val vv = client1.getState()

            ops2.map { op ->
                res -= op
                map1.decrement(key, op)
            }
            val delta = map1.generateDelta(vv)
            map2.merge(delta)

            val iteratorInt = map2.iteratorCntInt()
            if (ops.isNotEmpty()) {
                map2.getCntInt(key).shouldBe(res)
                iteratorInt.shouldHaveNext()
                iteratorInt.next().shouldBe(Pair(key, res))
            }
            iteratorInt.shouldBeEmpty()
        }
    }

    "deserialize is inverse to serialize" {
        checkAll<String>(500) { key ->
            val map = Map(client1)

            val value1 = Arb.bool().next()
            val value2 = Arb.string().next()
            val value3 = Arb.double().next()
            val value4 = Arb.nats().next()
            val value5 = Arb.nats().next()

            map.putLWW(key, value1)
            map.putLWW(key, value2)
            map.putLWW(key, value3)
            map.putLWW(key, value4)

            map.putMV(key, value1)
            map.putMV(key, value2)
            map.putMV(key, value3)
            map.putMV(key, value4)

            map.increment(key, value4)
            map.decrement(key, value5)

            val mapJson = map.toJson()

            mapJson.shouldBe(Map.fromJson(mapJson).toJson())
        }
    }
})
