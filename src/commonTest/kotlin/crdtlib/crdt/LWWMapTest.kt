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

import crdtlib.utils.DCUId
import crdtlib.utils.SimpleEnvironment
import crdtlib.utils.VersionVector
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.*
import io.kotest.matchers.nulls.*

/**
* Represents a suite test for LWWMap.
**/
class LWWMapTest : StringSpec({
    /**
    * This test evaluates the scenario: get.
    * Call to get should return null
    */
    "create and get" {
        val key = "key"
        val map = LWWMap()

        map.getBoolean(key).shouldBeNull()
        map.getDouble(key).shouldBeNull()
        map.getInt(key).shouldBeNull()
        map.getString(key).shouldBeNull()
    }

    /**
    * This test evaluates the scenario: put get.
    * Call to get should return the value set by the put.
    */
    "put and get" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map = LWWMap()

        map.put(key, valueBoolean, ts1)
        map.put(key, valueDouble, ts2)
        map.put(key, valueInt, ts3)
        map.put(key, valueString, ts4)

        map.getBoolean(key).shouldBe(valueBoolean)
        map.getDouble(key).shouldBe(valueDouble)
        map.getInt(key).shouldBe(valueInt)
        map.getString(key).shouldBe(valueString)
    }

    /**
    * This test evaluates the scenario: put del get.
    * Call to get should return null.
    */
    "put, delete, get" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val ts5 = dc.tick()
        val ts6 = dc.tick()
        val ts7 = dc.tick()
        val ts8 = dc.tick()
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map = LWWMap()

        map.put(key, valueBoolean, ts1)
        map.put(key, valueDouble, ts2)
        map.put(key, valueInt, ts3)
        map.put(key, valueString, ts4)
        map.deleteBoolean(key, ts5)
        map.deleteDouble(key, ts6)
        map.deleteInt(key, ts7)
        map.deleteString(key, ts8)

        map.getBoolean(key).shouldBeNull()
        map.getDouble(key).shouldBeNull()
        map.getInt(key).shouldBeNull()
        map.getString(key).shouldBeNull()
    }

    /**
    * This test evaluates the scenario: del get.
    * Call to get should return null.
    */
    "delete and get" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val key = "key"
        val map = LWWMap()

        map.deleteBoolean(key, ts1)
        map.deleteDouble(key, ts2)
        map.deleteInt(key, ts3)
        map.deleteString(key, ts4)

        map.getBoolean(key).shouldBeNull()
        map.getDouble(key).shouldBeNull()
        map.getInt(key).shouldBeNull()
        map.getString(key).shouldBeNull()
    }

    /**
    * This test evaluates the scenario: put put get
    * Call to get should return the value set by the second put.
    */
    "put, put, get" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val ts5 = dc.tick()
        val ts6 = dc.tick()
        val ts7 = dc.tick()
        val ts8 = dc.tick()
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map = LWWMap()

        map.put(key, valBoolean1, ts1)
        map.put(key, valDouble1, ts2)
        map.put(key, valInt1, ts3)
        map.put(key, valString1, ts4)
        map.put(key, valBoolean2, ts5)
        map.put(key, valDouble2, ts6)
        map.put(key, valInt2, ts7)
        map.put(key, valString2, ts8)

        map.getBoolean(key).shouldBe(valBoolean2)
        map.getDouble(key).shouldBe(valDouble2)
        map.getInt(key).shouldBe(valInt2)
        map.getString(key).shouldBe(valString2)
    }

    /**
    * This test evaluates the scenario: put put del get.
    * Call to get should return null.
    */
    "put, put, del, get" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val ts5 = dc.tick()
        val ts6 = dc.tick()
        val ts7 = dc.tick()
        val ts8 = dc.tick()
        val ts9 = dc.tick()
        val ts10 = dc.tick()
        val ts11 = dc.tick()
        val ts12 = dc.tick()
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map = LWWMap()

        map.put(key, valBoolean1, ts1)
        map.put(key, valDouble1, ts2)
        map.put(key, valInt1, ts3)
        map.put(key, valString1, ts4)
        map.put(key, valBoolean2, ts5)
        map.put(key, valDouble2, ts6)
        map.put(key, valInt2, ts7)
        map.put(key, valString2, ts8)
        map.deleteBoolean(key, ts9)
        map.deleteDouble(key, ts10)
        map.deleteInt(key, ts11)
        map.deleteString(key, ts12)

        map.getBoolean(key).shouldBeNull()
        map.getDouble(key).shouldBeNull()
        map.getInt(key).shouldBeNull()
        map.getString(key).shouldBeNull()
    }

    /**
    * This test evaluates the scenario: put || merge get.
    * Call to get should return the value set by the put registered in the first replica.
    */
    "R1: put; R2: merge and get" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = LWWMap()
        val map2 = LWWMap()

        map1.put(key, valueBoolean, ts1)
        map1.put(key, valueDouble, ts2)
        map1.put(key, valueInt, ts3)
        map1.put(key, valueString, ts4)
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
    }

    /**
    * This test evaluates the scenario: put || merge putLWW get.
    * Call to get should return the value set by put registered in the second replica.
    */
    "R1: put; R2: merge, put LWW, get" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.tick()
        val ts3 = dc1.tick()
        val ts5 = dc1.tick()
        val ts7 = dc1.tick()
        val ts2 = dc2.tick()
        val ts4 = dc2.tick()
        val ts6 = dc2.tick()
        val ts8 = dc2.tick()
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap()
        val map2 = LWWMap()

        map1.put(key, valBoolean1, ts1)
        map1.put(key, valDouble1, ts3)
        map1.put(key, valInt1, ts5)
        map1.put(key, valString1, ts7)
        map2.merge(map1)
        map2.put(key, valBoolean2, ts2)
        map2.put(key, valDouble2, ts4)
        map2.put(key, valInt2, ts6)
        map2.put(key, valString2, ts8)

        map2.getBoolean(key).shouldBe(valBoolean2)
        map2.getDouble(key).shouldBe(valDouble2)
        map2.getInt(key).shouldBe(valInt2)
        map2.getString(key).shouldBe(valString2)
    }

    /**
    * This test evaluates the scenario: put || putLWW merge get
    * Call to get should return the value set by put registered in the second replica.
    */
    "R1: put; R2: put LWW, merge, get" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.tick()
        val ts3 = dc1.tick()
        val ts5 = dc1.tick()
        val ts7 = dc1.tick()
        val ts2 = dc2.tick()
        val ts4 = dc2.tick()
        val ts6 = dc2.tick()
        val ts8 = dc2.tick()
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap()
        val map2 = LWWMap()

        map1.put(key, valBoolean1, ts1)
        map1.put(key, valDouble1, ts3)
        map1.put(key, valInt1, ts5)
        map1.put(key, valString1, ts7)
        map2.put(key, valBoolean2, ts2)
        map2.put(key, valDouble2, ts4)
        map2.put(key, valInt2, ts6)
        map2.put(key, valString2, ts8)
        map2.merge(map1)

        map2.getBoolean(key).shouldBe(valBoolean2)
        map2.getDouble(key).shouldBe(valDouble2)
        map2.getInt(key).shouldBe(valInt2)
        map2.getString(key).shouldBe(valString2)
    }

    /**
    * This test evaluates the scenario: putLWW || put merge get.
    * Call to get should return the value set by put registered in the first replica.
    */
    "R1: put LWW; R2: put, merge, get" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.tick()
        val ts3 = dc1.tick()
        val ts5 = dc1.tick()
        val ts7 = dc1.tick()
        val ts2 = dc2.tick()
        val ts4 = dc2.tick()
        val ts6 = dc2.tick()
        val ts8 = dc2.tick()
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap()
        val map2 = LWWMap()

        map2.put(key, valBoolean2, ts1)
        map2.put(key, valDouble2, ts3)
        map2.put(key, valInt2, ts5)
        map2.put(key, valString2, ts7)
        map1.put(key, valBoolean1, ts2)
        map1.put(key, valDouble1, ts4)
        map1.put(key, valInt1, ts6)
        map1.put(key, valString1, ts8)
        map2.merge(map1)

        map2.getBoolean(key).shouldBe(valBoolean1)
        map2.getDouble(key).shouldBe(valDouble1)
        map2.getInt(key).shouldBe(valInt1)
        map2.getString(key).shouldBe(valString1)
    }

    /**
    * This test evaluates the scenario: put delLWW || put merge get.
    * Call to get should return null.
    */
    "R1: put, delete LWW; R2: put, merge, get" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.tick()
        val ts3 = dc1.tick()
        val ts5 = dc1.tick()
        val ts7 = dc1.tick()
        val ts2 = dc2.tick()
        val ts4 = dc2.tick()
        val ts6 = dc2.tick()
        val ts8 = dc2.tick()
        val ts9 = dc2.tick()
        val ts10 = dc2.tick()
        val ts11 = dc2.tick()
        val ts12 = dc2.tick()
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap()
        val map2 = LWWMap()

        map2.put(key, valBoolean2, ts1)
        map2.put(key, valDouble2, ts3)
        map2.put(key, valInt2, ts5)
        map2.put(key, valString2, ts7)
        map1.put(key, valBoolean1, ts2)
        map1.put(key, valDouble1, ts4)
        map1.put(key, valInt1, ts6)
        map1.put(key, valString1, ts8)
        map1.deleteBoolean(key, ts9)
        map1.deleteDouble(key, ts10)
        map1.deleteInt(key, ts11)
        map1.deleteString(key, ts12)
        map2.merge(map1)

        map2.getBoolean(key).shouldBeNull()
        map2.getDouble(key).shouldBeNull()
        map2.getInt(key).shouldBeNull()
        map2.getString(key).shouldBeNull()
    }

    /**
    * This test evaluates the scenario: put delLWW || put merge(before del) merge(after del) get.
    * Call to get should return null.
    */
    "R1: put, delete LWW; R2: put, merge before delete, merge after delelte, get" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.tick()
        val ts3 = dc1.tick()
        val ts5 = dc1.tick()
        val ts7 = dc1.tick()
        val ts2 = dc2.tick()
        val ts4 = dc2.tick()
        val ts6 = dc2.tick()
        val ts8 = dc2.tick()
        val ts9 = dc2.tick()
        val ts10 = dc2.tick()
        val ts11 = dc2.tick()
        val ts12 = dc2.tick()
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap()
        val map2 = LWWMap()

        map2.put(key, valBoolean2, ts1)
        map2.put(key, valDouble2, ts3)
        map2.put(key, valInt2, ts5)
        map2.put(key, valString2, ts7)
        map1.put(key, valBoolean1, ts2)
        map1.put(key, valDouble1, ts4)
        map1.put(key, valInt1, ts6)
        map1.put(key, valString1, ts8)
        map2.merge(map1)
        map1.deleteBoolean(key, ts9)
        map1.deleteDouble(key, ts10)
        map1.deleteInt(key, ts11)
        map1.deleteString(key, ts12)
        map2.merge(map1)

        map2.getBoolean(key).shouldBeNull()
        map2.getDouble(key).shouldBeNull()
        map2.getInt(key).shouldBeNull()
        map2.getString(key).shouldBeNull()
    }

    /*
    * This test evaluates the scenario: put del || putLWW merge get.
    * Call to get should return the value set by put registered in the second replica.
    */
    "R1: put, delete; R2: put LWW, merge, get" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.tick()
        val ts3 = dc1.tick()
        val ts5 = dc1.tick()
        val ts7 = dc1.tick()
        val ts9 = dc1.tick()
        val ts11 = dc1.tick()
        val ts13 = dc1.tick()
        val ts15 = dc1.tick()
        dc2.tick()
        dc2.tick()
        dc2.tick()
        dc2.tick()
        val ts10 = dc2.tick()
        val ts12 = dc2.tick()
        val ts14 = dc2.tick()
        val ts16 = dc2.tick()
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap()
        val map2 = LWWMap()

        map1.put(key, valBoolean1, ts1)
        map1.put(key, valDouble1, ts3)
        map1.put(key, valInt1, ts5)
        map1.put(key, valString1, ts7)
        map1.deleteBoolean(key, ts9)
        map1.deleteDouble(key, ts11)
        map1.deleteInt(key, ts13)
        map1.deleteString(key, ts15)
        map2.put(key, valBoolean2, ts10)
        map2.put(key, valDouble2, ts12)
        map2.put(key, valInt2, ts14)
        map2.put(key, valString2, ts16)
        map2.merge(map1)

        map2.getBoolean(key).shouldBe(valBoolean2)
        map2.getDouble(key).shouldBe(valDouble2)
        map2.getInt(key).shouldBe(valInt2)
        map2.getString(key).shouldBe(valString2)
    }

    /*
    * This test evaluates the scenario: put del || putLWW merge(before del) merge(after del) get.
    * Call to get should return the value set by put registered in the second replica.
    */
    "R1: put, delete; R2: put LWW, merge before delete, merge after delete, get" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.tick()
        val ts3 = dc1.tick()
        val ts5 = dc1.tick()
        val ts7 = dc1.tick()
        val ts9 = dc1.tick()
        val ts11 = dc1.tick()
        val ts13 = dc1.tick()
        val ts15 = dc1.tick()
        dc2.tick()
        dc2.tick()
        dc2.tick()
        dc2.tick()
        val ts10 = dc2.tick()
        val ts12 = dc2.tick()
        val ts14 = dc2.tick()
        val ts16 = dc2.tick()
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap()
        val map2 = LWWMap()

        map1.put(key, valBoolean1, ts1)
        map1.put(key, valDouble1, ts3)
        map1.put(key, valInt1, ts5)
        map1.put(key, valString1, ts7)
        map2.put(key, valBoolean2, ts10)
        map2.put(key, valDouble2, ts12)
        map2.put(key, valInt2, ts14)
        map2.put(key, valString2, ts16)
        map2.merge(map1)
        map1.deleteBoolean(key, ts9)
        map1.deleteDouble(key, ts11)
        map1.deleteInt(key, ts13)
        map1.deleteString(key, ts15)
        map2.merge(map1)

        map2.getBoolean(key).shouldBe(valBoolean2)
        map2.getDouble(key).shouldBe(valDouble2)
        map2.getInt(key).shouldBe(valInt2)
        map2.getString(key).shouldBe(valString2)
    }

    /*
    * This test evaluates the scenario: put || put || merge1 delLWW merge2 get.
    * Call to get should return null.
    */
    "R1: put; R2: put; R3: merge R1, delete LWW, merge R2, get" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val dc3 = SimpleEnvironment(uid3)
        val ts1 = dc1.tick()
        val ts4 = dc1.tick()
        val ts7 = dc1.tick()
        val ts10 = dc1.tick()
        val ts2 = dc2.tick()
        val ts5 = dc2.tick()
        val ts8 = dc2.tick()
        val ts11 = dc2.tick()
        val ts3 = dc3.tick()
        val ts6 = dc3.tick()
        val ts9 = dc3.tick()
        val ts12 = dc3.tick()
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap()
        val map2 = LWWMap()
        val map3 = LWWMap()

        map1.put(key, valBoolean1, ts1)
        map1.put(key, valDouble1, ts4)
        map1.put(key, valInt1, ts7)
        map1.put(key, valString1, ts10)
        map3.merge(map1)
        map2.put(key, valBoolean2, ts2)
        map2.put(key, valDouble2, ts5)
        map2.put(key, valInt2, ts8)
        map2.put(key, valString2, ts11)
        map3.deleteBoolean(key, ts3)
        map3.deleteDouble(key, ts6)
        map3.deleteInt(key, ts9)
        map3.deleteString(key, ts12)
        map3.merge(map2)

        map3.getBoolean(key).shouldBeNull()
        map3.getDouble(key).shouldBeNull()
        map3.getInt(key).shouldBeNull()
        map3.getString(key).shouldBeNull()
    }

    /*
    * This test evaluates the scenario: put || putLWW || merge1 del merge2 get.
    * Call to get should return the value set by put registered in the second replica.
    */
    "R1: put; R2: put LWW; R3: merge R1, delete, merge R2, get" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val dc3 = SimpleEnvironment(uid3)
        val ts1 = dc1.tick()
        val ts4 = dc1.tick()
        val ts7 = dc1.tick()
        val ts10 = dc1.tick()
        val ts2 = dc2.tick()
        val ts5 = dc2.tick()
        val ts8 = dc2.tick()
        val ts11 = dc2.tick()
        val ts3 = dc3.tick()
        val ts6 = dc3.tick()
        val ts9 = dc3.tick()
        val ts12 = dc3.tick()
        val key = "key"
        val valBoolean1 = true
        val valBoolean2 = false
        val valDouble1 = 12.3456789
        val valDouble2 = 3.14159
        val valInt1 = 42
        val valInt2 = -100
        val valString1 = "value1"
        val valString2 = "value2"
        val map1 = LWWMap()
        val map2 = LWWMap()
        val map3 = LWWMap()

        map1.put(key, valBoolean1, ts1)
        map1.put(key, valDouble1, ts4)
        map1.put(key, valInt1, ts7)
        map1.put(key, valString1, ts10)
        map3.merge(map1)
        map3.deleteBoolean(key, ts2)
        map3.deleteDouble(key, ts5)
        map3.deleteInt(key, ts8)
        map3.deleteString(key, ts11)
        map2.put(key, valBoolean2, ts3)
        map2.put(key, valDouble2, ts6)
        map2.put(key, valInt2, ts9)
        map2.put(key, valString2, ts12)
        map3.merge(map2)

        map3.getBoolean(key).shouldBe(valBoolean2)
        map3.getDouble(key).shouldBe(valDouble2)
        map3.getInt(key).shouldBe(valInt2)
        map3.getString(key).shouldBe(valString2)
    }

    /*
    * This test evaluates the use of deltas return by call to put method.
    * Call to get should return the value set by put registered in the first replica.
    */
    "use deltas returned by put" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = LWWMap()
        val map2 = LWWMap()

        val opBoolean = map1.put(key, valueBoolean, ts1)
        val opDouble = map1.put(key, valueDouble, ts2)
        val opInt = map1.put(key, valueInt, ts3)
        val opString = map1.put(key, valueString, ts4)
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
    }

    /*
    * This test evaluates the use of deltas return by call to put and delete methods.
    * Call to get should return null.
    */
    "use deltas returned by put and delete" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val ts5 = dc.tick()
        val ts6 = dc.tick()
        val ts7 = dc.tick()
        val ts8 = dc.tick()
        val key = "key"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = LWWMap()
        val map2 = LWWMap()

        val putOpBoolean = map1.put(key, valueBoolean, ts1)
        val putOpDouble = map1.put(key, valueDouble, ts2)
        val putOpInt = map1.put(key, valueInt, ts3)
        val putOpString = map1.put(key, valueString, ts4)
        val delOpBoolean = map1.deleteBoolean(key, ts5)
        val delOpDouble = map1.deleteDouble(key, ts6)
        val delOpInt = map1.deleteInt(key, ts7)
        val delOpString = map1.deleteString(key, ts8)
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
    }

    /*
    * This test evaluates the merge of deltas return by call to put method.
    * Call to get should return the values set by puts registered in the first replica.
    */
    "merge deltas returned by put operations" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val ts5 = dc.tick()
        val ts6 = dc.tick()
        val ts7 = dc.tick()
        val ts8 = dc.tick()
        val ts9 = dc.tick()
        val ts10 = dc.tick()
        val ts11 = dc.tick()
        val ts12 = dc.tick()
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
        val map1 = LWWMap()
        val map2 = LWWMap()

        val opBoolean1 = map1.put(key1, valBoolean1, ts1)
        val opDouble1 = map1.put(key1, valDouble1, ts2)
        val opInt1 = map1.put(key1, valInt1, ts3)
        val opString1 = map1.put(key1, valString1, ts4)
        val opBoolean2 = map1.put(key1, valBoolean2, ts5)
        val opDouble2 = map1.put(key1, valDouble2, ts6)
        val opInt2 = map1.put(key1, valInt2, ts7)
        val opString2 = map1.put(key1, valString2, ts8)
        val opBoolean3 = map1.put(key2, valBoolean1, ts9)
        val opDouble3 = map1.put(key2, valDouble1, ts10)
        val opInt3 = map1.put(key2, valInt1, ts11)
        val opString3 = map1.put(key2, valString1, ts12)
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
    }

    /*
    * This test evaluates the merge of deltas return by call to put and delete methods.
    * Call to get should return the value set by put or null if it has been deleted.
    */
    "merge deltas returned by put and delete operations" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val ts5 = dc.tick()
        val ts6 = dc.tick()
        val ts7 = dc.tick()
        val ts8 = dc.tick()
        val ts9 = dc.tick()
        val ts10 = dc.tick()
        val ts11 = dc.tick()
        val ts12 = dc.tick()
        val key1 = "key1"
        val key2 = "key2"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = LWWMap()
        val map2 = LWWMap()

        val opBoolean1 = map1.put(key1, valueBoolean, ts1)
        val opDouble1 = map1.put(key1, valueDouble, ts2)
        val opInt1 = map1.put(key1, valueInt, ts3)
        val opString1 = map1.put(key1, valueString, ts4)
        val opBoolean2 = map1.deleteBoolean(key1, ts5)
        val opDouble2 = map1.deleteDouble(key1, ts6)
        val opInt2 = map1.deleteInt(key1, ts7)
        val opString2 = map1.deleteString(key1, ts8)
        val opBoolean3 = map1.put(key2, valueBoolean, ts9)
        val opDouble3 = map1.put(key2, valueDouble, ts10)
        val opInt3 = map1.put(key2, valueInt, ts11)
        val opString3 = map1.put(key2, valueString, ts12)
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
    }

    /*
    * This test evaluates the generation of delta (only put) plus its merging into another replica.
    * Call to get should return the values set by puts registered in the first replica after w.r.t
    * the given context.
    */
    "generate delta" {
        val uid = DCUId("dcid1")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val ts5 = dc.tick()
        val ts6 = dc.tick()
        val ts7 = dc.tick()
        val ts8 = dc.tick()
        val ts9 = dc.tick()
        val ts10 = dc.tick()
        val ts11 = dc.tick()
        val ts12 = dc.tick()
        val ts13 = dc.tick()
        val ts14 = dc.tick()
        val ts15 = dc.tick()
        val ts16 = dc.tick()
        val vv = VersionVector()
        vv.update(ts8)
        val key1 = "key1"
        val key2 = "key2"
        val key3 = "key3"
        val key4 = "key4"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = LWWMap()
        val map2 = LWWMap()

        map1.put(key1, valueBoolean, ts1)
        map1.put(key1, valueDouble, ts2)
        map1.put(key1, valueInt, ts3)
        map1.put(key1, valueString, ts4)
        map1.put(key2, valueBoolean, ts5)
        map1.put(key2, valueDouble, ts6)
        map1.put(key2, valueInt, ts7)
        map1.put(key2, valueString, ts8)
        map1.put(key3, valueBoolean, ts9)
        map1.put(key3, valueDouble, ts10)
        map1.put(key3, valueInt, ts11)
        map1.put(key3, valueString, ts12)
        map1.put(key4, valueBoolean, ts13)
        map1.put(key4, valueDouble, ts14)
        map1.put(key4, valueInt, ts15)
        map1.put(key4, valueString, ts16)
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
    }

    /*
    * This test evaluates the generation of delta (including delete) plus its merging into another replica.
    * Call to get should return the values set by puts or null set by delete w.r.t the given context.
    */
    "generate delta with delete" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val ts5 = dc.tick()
        val ts6 = dc.tick()
        val ts7 = dc.tick()
        val ts8 = dc.tick()
        val ts9 = dc.tick()
        val ts10 = dc.tick()
        val ts11 = dc.tick()
        val ts12 = dc.tick()
        val ts13 = dc.tick()
        val ts14 = dc.tick()
        val ts15 = dc.tick()
        val ts16 = dc.tick()
        val vv = VersionVector()
        vv.update(ts4)
        val key1 = "key1"
        val key2 = "key2"
        val key3 = "key3"
        val valueBoolean = true
        val valueDouble = 3.14159
        val valueInt = 42
        val valueString = "value"
        val map1 = LWWMap()
        val map2 = LWWMap()

        map1.put(key1, valueBoolean, ts1)
        map1.put(key1, valueDouble, ts2)
        map1.put(key1, valueInt, ts3)
        map1.put(key1, valueString, ts4)
        map1.put(key2, valueBoolean, ts5)
        map1.put(key2, valueDouble, ts6)
        map1.put(key2, valueInt, ts7)
        map1.put(key2, valueString, ts8)
        map1.deleteBoolean(key2, ts9)
        map1.deleteDouble(key2, ts10)
        map1.deleteInt(key2, ts11)
        map1.deleteString(key2, ts12)
        map1.put(key3, valueBoolean, ts13)
        map1.put(key3, valueDouble, ts14)
        map1.put(key3, valueInt, ts15)
        map1.put(key3, valueString, ts16)
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
    }

    /**
    * This test evaluates JSON serialization an empty LWW map.
    **/
    "empty JSON serialization" {
        val map = LWWMap()

        val mapJson = map.toJson()

        mapJson.shouldBe("""{"_type":"LWWMap","_metadata":{"entries":{}}}""")
    }

    /**
    * This test evaluates JSON deserialization of an empty LWW map.
    **/
    "empty JSON deserialization" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.tick()

        val mapJson = LWWMap.fromJson("""{"_type":"LWWMap","_metadata":{"entries":{}}}""")
        mapJson.put("key1", "value1", ts)

        mapJson.getString("key1").shouldBe("value1")
        mapJson.getString("key2").shouldBeNull()
        mapJson.getString("key3").shouldBeNull()
    }

    /**
    * This test evaluates JSON serialization of a LWW map.
    **/
    "JSON serialization" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val ts5 = dc.tick()
        val ts6 = dc.tick()
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
        val map = LWWMap()

        map.put(key1, value1, ts1)
        map.put(key2, value2, ts2)
        map.deleteString(key2, ts3)
        map.put(key3, value3, ts4)
        map.put(key4, value4, ts5)
        map.put(key5, value5, ts6)
        val mapJson = map.toJson()

        mapJson.shouldBe("""{"_type":"LWWMap","_metadata":{"entries":{"key1%INTEGER":{"uid":{"name":"dcid"},"cnt":-2147483647},"key2%STRING":{"uid":{"name":"dcid"},"cnt":-2147483645},"key3%STRING":{"uid":{"name":"dcid"},"cnt":-2147483644},"key4%BOOLEAN":{"uid":{"name":"dcid"},"cnt":-2147483643},"key5%DOUBLE":{"uid":{"name":"dcid"},"cnt":-2147483642}}},"key1%INTEGER":1,"key2%STRING":null,"key3%STRING":"value3","key4%BOOLEAN":true,"key5%DOUBLE":3.14159}""")
    }

    /**
    * This test evaluates JSON deserialization of a LWW map.
    **/
    "JSON deserialization" {
        val mapJson = LWWMap.fromJson("""{"_type":"LWWMap","_metadata":{"entries":{"key1%INTEGER":{"uid":{"name":"dcid"},"cnt":-2147483648},"key2%STRING":{"uid":{"name":"dcid"},"cnt":-2147483646},"key3%STRING":{"uid":{"name":"dcid"},"cnt":-2147483645},"key4%BOOLEAN":{"uid":{"name":"dcid"},"cnt":-2147483644},"key5%DOUBLE":{"uid":{"name":"dcid"},"cnt":-2147483643}}},"key1%INTEGER":1,"key2%STRING":null,"key3%STRING":"value3","key4%BOOLEAN":true,"key5%DOUBLE":3.14159}""")

        mapJson.getInt("key1").shouldBe(1)
        mapJson.getString("key2").shouldBeNull()
        mapJson.getString("key3").shouldBe("value3")
        mapJson.getBoolean("key4").shouldBe(true)
        mapJson.getDouble("key5").shouldBe(3.14159) 
    }
})
