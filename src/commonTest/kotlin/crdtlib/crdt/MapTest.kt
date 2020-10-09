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
import io.kotest.matchers.collections.*
import io.kotest.matchers.nulls.*

/**
* Represents a suite test for Map.
**/
class MapTest : StringSpec({

    /**
    * This test evaluates the scenario: get.
    * Call to get should return null
    */
    "create and get" {
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
    }
    
    /**
    * This test evaluates the scenario: put get.
    * Call to get should return the value set by the put.
    */
    "LWW put and get" {
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
        val map = Map()

        map.putLWW(key, valueBoolean, ts1)
        map.putLWW(key, valueDouble, ts2)
        map.putLWW(key, valueInt, ts3)
        map.putLWW(key, valueString, ts4)

        map.getLWWBoolean(key).shouldBe(valueBoolean)
        map.getLWWDouble(key).shouldBe(valueDouble)
        map.getLWWInt(key).shouldBe(valueInt)
        map.getLWWString(key).shouldBe(valueString)
    }
    
    /**
    * This test evaluates the scenario: put del get.
    * Call to get should return null.
    */
    "LWW put, delelte, get" {
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
        val map = Map()

        map.putLWW(key, valueBoolean, ts1)
        map.putLWW(key, valueDouble, ts2)
        map.putLWW(key, valueInt, ts3)
        map.putLWW(key, valueString, ts4)
        map.deleteLWWBoolean(key, ts5)
        map.deleteLWWDouble(key, ts6)
        map.deleteLWWInt(key, ts7)
        map.deleteLWWString(key, ts8)

        map.getLWWBoolean(key).shouldBeNull()
        map.getLWWDouble(key).shouldBeNull()
        map.getLWWInt(key).shouldBeNull()
        map.getLWWString(key).shouldBeNull()
    }
 
    /**
    * This test evaluates the scenario: del get.
    * Call to get should return null.
    */
    "LWW delete and get" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val key = "key"
        val map = Map()

        map.deleteLWWBoolean(key, ts1)
        map.deleteLWWDouble(key, ts2)
        map.deleteLWWInt(key, ts3)
        map.deleteLWWString(key, ts4)

        map.getLWWBoolean(key).shouldBeNull()
        map.getLWWDouble(key).shouldBeNull()
        map.getLWWInt(key).shouldBeNull()
        map.getLWWString(key).shouldBeNull()
    }

    /**
    * This test evaluates the scenario: put put get
    * Call to get should return the value set by the second put.
    */
    "LWW put, put, get" {
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
        val map = Map()

        map.putLWW(key, valBoolean1, ts1)
        map.putLWW(key, valDouble1, ts2)
        map.putLWW(key, valInt1, ts3)
        map.putLWW(key, valString1, ts4)
        map.putLWW(key, valBoolean2, ts5)
        map.putLWW(key, valDouble2, ts6)
        map.putLWW(key, valInt2, ts7)
        map.putLWW(key, valString2, ts8)

        map.getLWWBoolean(key).shouldBe(valBoolean2)
        map.getLWWDouble(key).shouldBe(valDouble2)
        map.getLWWInt(key).shouldBe(valInt2)
        map.getLWWString(key).shouldBe(valString2)
    }

    /**
    * This test evaluates the scenario: put put del get.
    * Call to get should return null.
    */
    "LWW put, put, delelte, get" {
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
        val map = Map()

        map.putLWW(key, valBoolean1, ts1)
        map.putLWW(key, valDouble1, ts2)
        map.putLWW(key, valInt1, ts3)
        map.putLWW(key, valString1, ts4)
        map.putLWW(key, valBoolean2, ts5)
        map.putLWW(key, valDouble2, ts6)
        map.putLWW(key, valInt2, ts7)
        map.putLWW(key, valString2, ts8)
        map.deleteLWWBoolean(key, ts9)
        map.deleteLWWDouble(key, ts10)
        map.deleteLWWInt(key, ts11)
        map.deleteLWWString(key, ts12)

        map.getLWWBoolean(key).shouldBeNull()
        map.getLWWDouble(key).shouldBeNull()
        map.getLWWInt(key).shouldBeNull()
        map.getLWWString(key).shouldBeNull()
    }

    /**
    * This test evaluates the scenario: put || merge get.
    * Call to get should return the value set by the put registered in the first replica.
    */
    "LWW R1: put; R2: merge, get" {
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
        val map1 = Map()
        val map2 = Map()

        map1.putLWW(key, valueBoolean, ts1)
        map1.putLWW(key, valueDouble, ts2)
        map1.putLWW(key, valueInt, ts3)
        map1.putLWW(key, valueString, ts4)
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
    }

    /**
    * This test evaluates the scenario: put || merge putLWW get.
    * Call to get should return the value set by put registered in the second replica.
    */
    "LWW R1: put; R2: merge, put LWW, get" {
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
        val map1 = Map()
        val map2 = Map()

        map1.putLWW(key, valBoolean1, ts1)
        map1.putLWW(key, valDouble1, ts3)
        map1.putLWW(key, valInt1, ts5)
        map1.putLWW(key, valString1, ts7)
        map2.merge(map1)
        map2.putLWW(key, valBoolean2, ts2)
        map2.putLWW(key, valDouble2, ts4)
        map2.putLWW(key, valInt2, ts6)
        map2.putLWW(key, valString2, ts8)

        map2.getLWWBoolean(key).shouldBe(valBoolean2)
        map2.getLWWDouble(key).shouldBe(valDouble2)
        map2.getLWWInt(key).shouldBe(valInt2)
        map2.getLWWString(key).shouldBe(valString2)
    }

    /**
    * This test evaluates the scenario: put || putLWW merge get
    * Call to get should return the value set by put registered in the second replica.
    */
    "LWW R1: put; R2: put LWW, merge, get" {
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
        val map1 = Map()
        val map2 = Map()

        map1.putLWW(key, valBoolean1, ts1)
        map1.putLWW(key, valDouble1, ts3)
        map1.putLWW(key, valInt1, ts5)
        map1.putLWW(key, valString1, ts7)
        map2.putLWW(key, valBoolean2, ts2)
        map2.putLWW(key, valDouble2, ts4)
        map2.putLWW(key, valInt2, ts6)
        map2.putLWW(key, valString2, ts8)
        map2.merge(map1)

        map2.getLWWBoolean(key).shouldBe(valBoolean2)
        map2.getLWWDouble(key).shouldBe(valDouble2)
        map2.getLWWInt(key).shouldBe(valInt2)
        map2.getLWWString(key).shouldBe(valString2)
    }
    
    /**
    * This test evaluates the scenario: putLWW || put merge get.
    * Call to get should return the value set by put registered in the first replica.
    */
    "LWW R1: put LWW; R2: put, merge, get" {
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
        val map1 = Map()
        val map2 = Map()

        map2.putLWW(key, valBoolean2, ts1)
        map2.putLWW(key, valDouble2, ts3)
        map2.putLWW(key, valInt2, ts5)
        map2.putLWW(key, valString2, ts7)
        map1.putLWW(key, valBoolean1, ts2)
        map1.putLWW(key, valDouble1, ts4)
        map1.putLWW(key, valInt1, ts6)
        map1.putLWW(key, valString1, ts8)
        map2.merge(map1)

        map2.getLWWBoolean(key).shouldBe(valBoolean1)
        map2.getLWWDouble(key).shouldBe(valDouble1)
        map2.getLWWInt(key).shouldBe(valInt1)
        map2.getLWWString(key).shouldBe(valString1)
    }

    /**
    * This test evaluates the scenario: put delLWW || put merge get.
    * Call to get should return null.
    */
    "LWW R1: put, delelte LWW; R2: put, merge, get" {
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
        val map1 = Map()
        val map2 = Map()

        map2.putLWW(key, valBoolean2, ts1)
        map2.putLWW(key, valDouble2, ts3)
        map2.putLWW(key, valInt2, ts5)
        map2.putLWW(key, valString2, ts7)
        map1.putLWW(key, valBoolean1, ts2)
        map1.putLWW(key, valDouble1, ts4)
        map1.putLWW(key, valInt1, ts6)
        map1.putLWW(key, valString1, ts8)
        map1.deleteLWWBoolean(key, ts9)
        map1.deleteLWWDouble(key, ts10)
        map1.deleteLWWInt(key, ts11)
        map1.deleteLWWString(key, ts12)
        map2.merge(map1)

        map2.getLWWBoolean(key).shouldBeNull()
        map2.getLWWDouble(key).shouldBeNull()
        map2.getLWWInt(key).shouldBeNull()
        map2.getLWWString(key).shouldBeNull()
    }

    /**
    * This test evaluates the scenario: put delLWW || put merge(before del) merge(after del) get.
    * Call to get should return null.
    */
    "LWW R1: put, delete LWW; R2: put, merge before delete, merge after delete, get" {
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
        val map1 = Map()
        val map2 = Map()

        map2.putLWW(key, valBoolean2, ts1)
        map2.putLWW(key, valDouble2, ts3)
        map2.putLWW(key, valInt2, ts5)
        map2.putLWW(key, valString2, ts7)
        map1.putLWW(key, valBoolean1, ts2)
        map1.putLWW(key, valDouble1, ts4)
        map1.putLWW(key, valInt1, ts6)
        map1.putLWW(key, valString1, ts8)
        map2.merge(map1)
        map1.deleteLWWBoolean(key, ts9)
        map1.deleteLWWDouble(key, ts10)
        map1.deleteLWWInt(key, ts11)
        map1.deleteLWWString(key, ts12)
        map2.merge(map1)

        map2.getLWWBoolean(key).shouldBeNull()
        map2.getLWWDouble(key).shouldBeNull()
        map2.getLWWInt(key).shouldBeNull()
        map2.getLWWString(key).shouldBeNull()
    }

    /*
    * This test evaluates the scenario: put del || putLWW merge get.
    * Call to get should return the value set by put registered in the second replica.
    */
    "LWW R1: put, delete; R2: put LWW, merge, get" {
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
        val map1 = Map()
        val map2 = Map()

        map1.putLWW(key, valBoolean1, ts1)
        map1.putLWW(key, valDouble1, ts3)
        map1.putLWW(key, valInt1, ts5)
        map1.putLWW(key, valString1, ts7)
        map1.deleteLWWBoolean(key, ts9)
        map1.deleteLWWDouble(key, ts11)
        map1.deleteLWWInt(key, ts13)
        map1.deleteLWWString(key, ts15)
        map2.putLWW(key, valBoolean2, ts10)
        map2.putLWW(key, valDouble2, ts12)
        map2.putLWW(key, valInt2, ts14)
        map2.putLWW(key, valString2, ts16)
        map2.merge(map1)

        map2.getLWWBoolean(key).shouldBe(valBoolean2)
        map2.getLWWDouble(key).shouldBe(valDouble2)
        map2.getLWWInt(key).shouldBe(valInt2)
        map2.getLWWString(key).shouldBe(valString2)
    }

    /*
    * This test evaluates the scenario: put del || putLWW merge(before del) merge(after del) get.
    * Call to get should return the value set by put registered in the second replica.
    */
    "LWW R1: put, delete; R2: put LWW, merge before delete, merge after delete, get" {
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
        val map1 = Map()
        val map2 = Map()

        map1.putLWW(key, valBoolean1, ts1)
        map1.putLWW(key, valDouble1, ts3)
        map1.putLWW(key, valInt1, ts5)
        map1.putLWW(key, valString1, ts7)
        map2.putLWW(key, valBoolean2, ts10)
        map2.putLWW(key, valDouble2, ts12)
        map2.putLWW(key, valInt2, ts14)
        map2.putLWW(key, valString2, ts16)
        map2.merge(map1)
        map1.deleteLWWBoolean(key, ts9)
        map1.deleteLWWDouble(key, ts11)
        map1.deleteLWWInt(key, ts13)
        map1.deleteLWWString(key, ts15)
        map2.merge(map1)

        map2.getLWWBoolean(key).shouldBe(valBoolean2)
        map2.getLWWDouble(key).shouldBe(valDouble2)
        map2.getLWWInt(key).shouldBe(valInt2)
        map2.getLWWString(key).shouldBe(valString2)
    }

    /*
    * This test evaluates the scenario: put || put || merge1 delLWW merge2 get.
    * Call to get should return null.
    */
    "LWW R1: put; R2: put; R3: merge R1, delete LWW, merge R2, get" {
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
        val map1 = Map()
        val map2 = Map()
        val map3 = Map()

        map1.putLWW(key, valBoolean1, ts1)
        map1.putLWW(key, valDouble1, ts4)
        map1.putLWW(key, valInt1, ts7)
        map1.putLWW(key, valString1, ts10)
        map3.merge(map1)
        map2.putLWW(key, valBoolean2, ts2)
        map2.putLWW(key, valDouble2, ts5)
        map2.putLWW(key, valInt2, ts8)
        map2.putLWW(key, valString2, ts11)
        map3.deleteLWWBoolean(key, ts3)
        map3.deleteLWWDouble(key, ts6)
        map3.deleteLWWInt(key, ts9)
        map3.deleteLWWString(key, ts12)
        map3.merge(map2)

        map3.getLWWBoolean(key).shouldBeNull()
        map3.getLWWDouble(key).shouldBeNull()
        map3.getLWWInt(key).shouldBeNull()
        map3.getLWWString(key).shouldBeNull()
    }

    /*
    * This test evaluates the scenario: putLWW || put || merge1 del merge2 get.
    * Call to get should return the value set by put registered in the second replica.
    */
    "LWW R1: put; R2: put LWW; R3: merge R1, delete, merge R2, get" {
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
        val map1 = Map()
        val map2 = Map()
        val map3 = Map()

        map1.putLWW(key, valBoolean1, ts1)
        map1.putLWW(key, valDouble1, ts4)
        map1.putLWW(key, valInt1, ts7)
        map1.putLWW(key, valString1, ts10)
        map3.merge(map1)
        map3.deleteLWWBoolean(key, ts2)
        map3.deleteLWWDouble(key, ts5)
        map3.deleteLWWInt(key, ts8)
        map3.deleteLWWString(key, ts11)
        map2.putLWW(key, valBoolean2, ts3)
        map2.putLWW(key, valDouble2, ts6)
        map2.putLWW(key, valInt2, ts9)
        map2.putLWW(key, valString2, ts12)
        map3.merge(map2)

        map3.getLWWBoolean(key).shouldBe(valBoolean2)
        map3.getLWWDouble(key).shouldBe(valDouble2)
        map3.getLWWInt(key).shouldBe(valInt2)
        map3.getLWWString(key).shouldBe(valString2)
    }

    /*
    * This test evaluates the use of deltas return by call to put method.
    * Call to get should return the value set by put registered in the first replica.
    */
    "LWW use deltas returned by put" {
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
        val map1 = Map()
        val map2 = Map()

        val opBoolean = map1.putLWW(key, valueBoolean, ts1)
        val opDouble = map1.putLWW(key, valueDouble, ts2)
        val opInt = map1.putLWW(key, valueInt, ts3)
        val opString = map1.putLWW(key, valueString, ts4)
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
    }

    /*
    * This test evaluates the use of deltas return by call to put and delete methods.
    * Call to get should return null.
    */
    "LWW use deltas returned by put and delete" {
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
        val map1 = Map()
        val map2 = Map()

        val putOpBoolean = map1.putLWW(key, valueBoolean, ts1)
        val putOpDouble = map1.putLWW(key, valueDouble, ts2)
        val putOpInt = map1.putLWW(key, valueInt, ts3)
        val putOpString = map1.putLWW(key, valueString, ts4)
        val delOpBoolean = map1.deleteLWWBoolean(key, ts5)
        val delOpDouble = map1.deleteLWWDouble(key, ts6)
        val delOpInt = map1.deleteLWWInt(key, ts7)
        val delOpString = map1.deleteLWWString(key, ts8)
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
    }

    /*
    * This test evaluates the merge of deltas return by call to put method.
    * Call to get should return the values set by puts registered in the first replica.
    */
    "LWW merge deltas returned by put operations" {
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
        val map1 = Map()
        val map2 = Map()

        val opBoolean1 = map1.putLWW(key1, valBoolean1, ts1)
        val opDouble1 = map1.putLWW(key1, valDouble1, ts2)
        val opInt1 = map1.putLWW(key1, valInt1, ts3)
        val opString1 = map1.putLWW(key1, valString1, ts4)
        val opBoolean2 = map1.putLWW(key1, valBoolean2, ts5)
        val opDouble2 = map1.putLWW(key1, valDouble2, ts6)
        val opInt2 = map1.putLWW(key1, valInt2, ts7)
        val opString2 = map1.putLWW(key1, valString2, ts8)
        val opBoolean3 = map1.putLWW(key2, valBoolean1, ts9)
        val opDouble3 = map1.putLWW(key2, valDouble1, ts10)
        val opInt3 = map1.putLWW(key2, valInt1, ts11)
        val opString3 = map1.putLWW(key2, valString1, ts12)
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
    }

    /*
    * This test evaluates the merge of deltas return by call to put and delete methods.
    * Call to get should return the value set by put or null if it has been deleted.
    */
    "LWW merge deltas returned by put and delete operations" {
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
        val map1 = Map()
        val map2 = Map()

        val opBoolean1 = map1.putLWW(key1, valueBoolean, ts1)
        val opDouble1 = map1.putLWW(key1, valueDouble, ts2)
        val opInt1 = map1.putLWW(key1, valueInt, ts3)
        val opString1 = map1.putLWW(key1, valueString, ts4)
        val opBoolean2 = map1.deleteLWWBoolean(key1, ts5)
        val opDouble2 = map1.deleteLWWDouble(key1, ts6)
        val opInt2 = map1.deleteLWWInt(key1, ts7)
        val opString2 = map1.deleteLWWString(key1, ts8)
        val opBoolean3 = map1.putLWW(key2, valueBoolean, ts9)
        val opDouble3 = map1.putLWW(key2, valueDouble, ts10)
        val opInt3 = map1.putLWW(key2, valueInt, ts11)
        val opString3 = map1.putLWW(key2, valueString, ts12)
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
    }

    /*
    * This test evaluates the generation of delta (only put) plus its merging into another replica.
    * Call to get should return the values set by puts registered in the first replica after w.r.t
    * the given context.
    */
    "LWW generate delta" {
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
        val map1 = Map()
        val map2 = Map()

        map1.putLWW(key1, valueBoolean, ts1)
        map1.putLWW(key1, valueDouble, ts2)
        map1.putLWW(key1, valueInt, ts3)
        map1.putLWW(key1, valueString, ts4)
        map1.putLWW(key2, valueBoolean, ts5)
        map1.putLWW(key2, valueDouble, ts6)
        map1.putLWW(key2, valueInt, ts7)
        map1.putLWW(key2, valueString, ts8)
        map1.putLWW(key3, valueBoolean, ts9)
        map1.putLWW(key3, valueDouble, ts10)
        map1.putLWW(key3, valueInt, ts11)
        map1.putLWW(key3, valueString, ts12)
        map1.putLWW(key4, valueBoolean, ts13)
        map1.putLWW(key4, valueDouble, ts14)
        map1.putLWW(key4, valueInt, ts15)
        map1.putLWW(key4, valueString, ts16)
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
    }

    /*
    * This test evaluates the generation of delta (including delete) plus its merging into another replica.
    * Call to get should return the values set by puts or null set by delete w.r.t the given context.
    */
    "LWW generate delta with delete" {
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
        val map1 = Map()
        val map2 = Map()

        map1.putLWW(key1, valueBoolean, ts1)
        map1.putLWW(key1, valueDouble, ts2)
        map1.putLWW(key1, valueInt, ts3)
        map1.putLWW(key1, valueString, ts4)
        map1.putLWW(key2, valueBoolean, ts5)
        map1.putLWW(key2, valueDouble, ts6)
        map1.putLWW(key2, valueInt, ts7)
        map1.putLWW(key2, valueString, ts8)
        map1.deleteLWWBoolean(key2, ts9)
        map1.deleteLWWDouble(key2, ts10)
        map1.deleteLWWInt(key2, ts11)
        map1.deleteLWWString(key2, ts12)
        map1.putLWW(key3, valueBoolean, ts13)
        map1.putLWW(key3, valueDouble, ts14)
        map1.putLWW(key3, valueInt, ts15)
        map1.putLWW(key3, valueString, ts16)
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
    }
    
    /**
    * This test evaluates the scenario: put get.
    * Call to get should return the value set by the put.
    */
    "MV put and get" {
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
        val map = Map()

        map.putMV(key, valueBoolean, ts1)
        map.putMV(key, valueDouble, ts2)
        map.putMV(key, valueInt, ts3)
        map.putMV(key, valueString, ts4)

        map.getMVBoolean(key)!!.shouldHaveSingleElement(valueBoolean)
        map.getMVDouble(key)!!.shouldHaveSingleElement(valueDouble)
        map.getMVInt(key)!!.shouldHaveSingleElement(valueInt)
        map.getMVString(key)!!.shouldHaveSingleElement(valueString)
    }
    
    /**
    * This test evaluates the scenario: put del get.
    * Call to get should return null.
    */
    "MV put, delete, get" {
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
        val map = Map()

        map.putMV(key, valueBoolean, ts1)
        map.putMV(key, valueDouble, ts2)
        map.putMV(key, valueInt, ts3)
        map.putMV(key, valueString, ts4)
        map.deleteMVBoolean(key, ts5)
        map.deleteMVDouble(key, ts6)
        map.deleteMVInt(key, ts7)
        map.deleteMVString(key, ts8)

        map.getMVBoolean(key).shouldBeNull()
        map.getMVDouble(key).shouldBeNull()
        map.getMVInt(key).shouldBeNull()
        map.getMVString(key).shouldBeNull()
    }
 
    /**
    * This test evaluates the scenario: del get.
    * Call to get should return null.
    */
    "MV delete and get" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val key = "key"
        val map = Map()

        map.deleteMVBoolean(key, ts1)
        map.deleteMVDouble(key, ts2)
        map.deleteMVInt(key, ts3)
        map.deleteMVString(key, ts4)

        map.getMVBoolean(key).shouldBeNull()
        map.getMVDouble(key).shouldBeNull()
        map.getMVInt(key).shouldBeNull()
        map.getMVString(key).shouldBeNull()
    }

    /**
    * This test evaluates the scenario: put put get.
    * Call to get should return the value set by the second put.
    */
    "MV put, put, get" {
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
        val map = Map()

        map.putMV(key, valBoolean1, ts1)
        map.putMV(key, valDouble1, ts2)
        map.putMV(key, valInt1, ts3)
        map.putMV(key, valString1, ts4)
        map.putMV(key, valBoolean2, ts5)
        map.putMV(key, valDouble2, ts6)
        map.putMV(key, valInt2, ts7)
        map.putMV(key, valString2, ts8)

        map.getMVBoolean(key)!!.shouldHaveSingleElement(valBoolean2)
        map.getMVDouble(key)!!.shouldHaveSingleElement(valDouble2)
        map.getMVInt(key)!!.shouldHaveSingleElement(valInt2)
        map.getMVString(key)!!.shouldHaveSingleElement(valString2)
    }

    /**
    * This test evaluates the scenario: put put del get.
    * Call to get should return null.
    */
    "MV put, put, delete, get" {
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
        val map = Map()

        map.putMV(key, valBoolean1, ts1)
        map.putMV(key, valDouble1, ts2)
        map.putMV(key, valInt1, ts3)
        map.putMV(key, valString1, ts4)
        map.putMV(key, valBoolean2, ts5)
        map.putMV(key, valDouble2, ts6)
        map.putMV(key, valInt2, ts7)
        map.putMV(key, valString2, ts8)
        map.deleteMVBoolean(key, ts9)
        map.deleteMVDouble(key, ts10)
        map.deleteMVInt(key, ts11)
        map.deleteMVString(key, ts12)

        map.getMVBoolean(key).shouldBeNull()
        map.getMVDouble(key).shouldBeNull()
        map.getMVInt(key).shouldBeNull()
        map.getMVString(key).shouldBeNull()
    }

    /**
    * This test evaluates the scenario: put || merge get.
    * Call to get should return the value set by the put registered in the first replica.
    */
    "MV R1: put; R2: merge, get" {
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
        val map1 = Map()
        val map2 = Map()

        map1.putMV(key, valueBoolean, ts1)
        map1.putMV(key, valueDouble, ts2)
        map1.putMV(key, valueInt, ts3)
        map1.putMV(key, valueString, ts4)
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
    }

    /**
    * This test evaluates the scenario: put || merge put get.
    * Call to get should return the value set by put registered in the second replica.
    */
    "MV R1: put; R2: merge, put, get" {
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
        val map1 = Map()
        val map2 = Map()

        map1.putMV(key, valBoolean1, ts1)
        map1.putMV(key, valDouble1, ts3)
        map1.putMV(key, valInt1, ts5)
        map1.putMV(key, valString1, ts7)
        map2.merge(map1)
        map2.putMV(key, valBoolean2, ts2)
        map2.putMV(key, valDouble2, ts4)
        map2.putMV(key, valInt2, ts6)
        map2.putMV(key, valString2, ts8)

        map2.getMVBoolean(key)!!.shouldHaveSingleElement(valBoolean2)
        map2.getMVDouble(key)!!.shouldHaveSingleElement(valDouble2)
        map2.getMVInt(key)!!.shouldHaveSingleElement(valInt2)
        map2.getMVString(key)!!.shouldHaveSingleElement(valString2)
    }

    /**
    * This test evaluates the scenario: put || put merge get
    * Call to get should return a set containing the two concurently put values.
    */
    "MV R1: put; R2: put, merge, get" {
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
        val map1 = Map()
        val map2 = Map()

        map1.putMV(key, valBoolean1, ts1)
        map1.putMV(key, valDouble1, ts3)
        map1.putMV(key, valInt1, ts5)
        map1.putMV(key, valString1, ts7)
        map2.putMV(key, valBoolean2, ts2)
        map2.putMV(key, valDouble2, ts4)
        map2.putMV(key, valInt2, ts6)
        map2.putMV(key, valString2, ts8)
        map2.merge(map1)

        map2.getMVBoolean(key)!!.shouldContainExactlyInAnyOrder(valBoolean1, valBoolean2)
        map2.getMVDouble(key)!!.shouldContainExactlyInAnyOrder(valDouble1, valDouble2)
        map2.getMVInt(key)!!.shouldContainExactlyInAnyOrder(valInt1, valInt2)
        map2.getMVString(key)!!.shouldContainExactlyInAnyOrder(valString1, valString2)
    }

    /**
    * This test evaluates the scenario: put del || put (with older timestamp) merge get.
    * Call to get should return a set containing the value set in the second replica.
    */
    "MV R1: put, delete; R2: put with older timestamp, merge, get" {
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
        val map1 = Map()
        val map2 = Map()

        map2.putMV(key, valBoolean2, ts1)
        map2.putMV(key, valDouble2, ts3)
        map2.putMV(key, valInt2, ts5)
        map2.putMV(key, valString2, ts7)
        map1.putMV(key, valBoolean1, ts2)
        map1.putMV(key, valDouble1, ts4)
        map1.putMV(key, valInt1, ts6)
        map1.putMV(key, valString1, ts8)
        map1.deleteMVBoolean(key, ts9)
        map1.deleteMVDouble(key, ts10)
        map1.deleteMVInt(key, ts11)
        map1.deleteMVString(key, ts12)
        map2.merge(map1)

        map2.getMVBoolean(key)!!.shouldContainExactlyInAnyOrder(valBoolean2, null)
        map2.getMVDouble(key)!!.shouldContainExactlyInAnyOrder(valDouble2, null)
        map2.getMVInt(key)!!.shouldContainExactlyInAnyOrder(valInt2, null)
        map2.getMVString(key)!!.shouldContainExactlyInAnyOrder(valString2, null)
    }

    /**
    * This test evaluates the scenario: put del || put(with older timestamp) merge(before del)
    * merge(after del) get.
    * Call to get should return a set containing the value set in the second replica.
    */
    "MV R1: put, delete; R2: put with older timestamp, merge before delete, merge after delete, get" {
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
        val map1 = Map()
        val map2 = Map()

        map2.putMV(key, valBoolean2, ts1)
        map2.putMV(key, valDouble2, ts3)
        map2.putMV(key, valInt2, ts5)
        map2.putMV(key, valString2, ts7)
        map1.putMV(key, valBoolean1, ts2)
        map1.putMV(key, valDouble1, ts4)
        map1.putMV(key, valInt1, ts6)
        map1.putMV(key, valString1, ts8)
        map2.merge(map1)
        map1.deleteMVBoolean(key, ts9)
        map1.deleteMVDouble(key, ts10)
        map1.deleteMVInt(key, ts11)
        map1.deleteMVString(key, ts12)
        map2.merge(map1)

        map2.getMVBoolean(key)!!.shouldContainExactlyInAnyOrder(valBoolean2, null)
        map2.getMVDouble(key)!!.shouldContainExactlyInAnyOrder(valDouble2, null)
        map2.getMVInt(key)!!.shouldContainExactlyInAnyOrder(valInt2, null)
        map2.getMVString(key)!!.shouldContainExactlyInAnyOrder(valString2, null)
    }

    /*
    * This test evaluates the scenario: put del || put(with newer timestamp) merge get.
    * Call to get should return the value set by put registered in the second replica.
    */
    "MV R1: put, delete; R2: put with newer timstamp, merge, get" {
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
        val map1 = Map()
        val map2 = Map()

        map1.putMV(key, valBoolean1, ts1)
        map1.putMV(key, valDouble1, ts3)
        map1.putMV(key, valInt1, ts5)
        map1.putMV(key, valString1, ts7)
        map1.deleteMVBoolean(key, ts9)
        map1.deleteMVDouble(key, ts11)
        map1.deleteMVInt(key, ts13)
        map1.deleteMVString(key, ts15)
        map2.putMV(key, valBoolean2, ts10)
        map2.putMV(key, valDouble2, ts12)
        map2.putMV(key, valInt2, ts14)
        map2.putMV(key, valString2, ts16)
        map2.merge(map1)

        map2.getMVBoolean(key)!!.shouldContainExactlyInAnyOrder(valBoolean2, null)
        map2.getMVDouble(key)!!.shouldContainExactlyInAnyOrder(valDouble2, null)
        map2.getMVInt(key)!!.shouldContainExactlyInAnyOrder(valInt2, null)
        map2.getMVString(key)!!.shouldContainExactlyInAnyOrder(valString2, null)
    }

    /*
    * This test evaluates the scenario: put del || put(with newer timstamp) merge(before del)
    * merge(after del) get.
    * Call to get should return the value set by put registered in the second replica.
    */
    "MV R1: put, delete; R2: put with newer timestamp, merge before delete, merge after, delete, get" {
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
        val map1 = Map()
        val map2 = Map()

        map1.putMV(key, valBoolean1, ts1)
        map1.putMV(key, valDouble1, ts3)
        map1.putMV(key, valInt1, ts5)
        map1.putMV(key, valString1, ts7)
        map2.putMV(key, valBoolean2, ts10)
        map2.putMV(key, valDouble2, ts12)
        map2.putMV(key, valInt2, ts14)
        map2.putMV(key, valString2, ts16)
        map2.merge(map1)
        map1.deleteMVBoolean(key, ts9)
        map1.deleteMVDouble(key, ts11)
        map1.deleteMVInt(key, ts13)
        map1.deleteMVString(key, ts15)
        map2.merge(map1)

        map2.getMVBoolean(key)!!.shouldContainExactlyInAnyOrder(valBoolean2, null)
        map2.getMVDouble(key)!!.shouldContainExactlyInAnyOrder(valDouble2, null)
        map2.getMVInt(key)!!.shouldContainExactlyInAnyOrder(valInt2, null)
        map2.getMVString(key)!!.shouldContainExactlyInAnyOrder(valString2, null)
    }

    /*
    * This test evaluates the scenario: put || put || merge1 del merge2 get.
    * Call to get should return the value set by put registered in the second replica.
    */
    "MV R1: put; R2: put; R3: merge R1, delete, merge R2, get" {
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
        val map1 = Map()
        val map2 = Map()
        val map3 = Map()

        map1.putMV(key, valBoolean1, ts1)
        map1.putMV(key, valDouble1, ts4)
        map1.putMV(key, valInt1, ts7)
        map1.putMV(key, valString1, ts10)
        map3.merge(map1)
        map2.putMV(key, valBoolean2, ts2)
        map2.putMV(key, valDouble2, ts5)
        map2.putMV(key, valInt2, ts8)
        map2.putMV(key, valString2, ts11)
        map3.deleteMVBoolean(key, ts3)
        map3.deleteMVDouble(key, ts6)
        map3.deleteMVInt(key, ts9)
        map3.deleteMVString(key, ts12)
        map3.merge(map2)

        map3.getMVBoolean(key)!!.shouldContainExactlyInAnyOrder(valBoolean2, null)
        map3.getMVDouble(key)!!.shouldContainExactlyInAnyOrder(valDouble2, null)
        map3.getMVInt(key)!!.shouldContainExactlyInAnyOrder(valInt2, null)
        map3.getMVString(key)!!.shouldContainExactlyInAnyOrder(valString2, null)
    }

    /*
    * This test evaluates the use of deltas return by call to put method.
    * Call to get should return the value set by put registered in the first replica.
    */
    "MV use deltas returned by put" {
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
        val map1 = Map()
        val map2 = Map()

        val opBoolean = map1.putMV(key, valueBoolean, ts1)
        val opDouble = map1.putMV(key, valueDouble, ts2)
        val opInt = map1.putMV(key, valueInt, ts3)
        val opString = map1.putMV(key, valueString, ts4)
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
    }

    /*
    * This test evaluates the use of deltas return by call to put and delete methods.
    * Call to get should return null.
    */
    "MV use deltas returned by put and delete" {
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
        val map1 = Map()
        val map2 = Map()

        val putOpBoolean = map1.putMV(key, valueBoolean, ts1)
        val putOpDouble = map1.putMV(key, valueDouble, ts2)
        val putOpInt = map1.putMV(key, valueInt, ts3)
        val putOpString = map1.putMV(key, valueString, ts4)
        val delOpBoolean = map1.deleteMVBoolean(key, ts5)
        val delOpDouble = map1.deleteMVDouble(key, ts6)
        val delOpInt = map1.deleteMVInt(key, ts7)
        val delOpString = map1.deleteMVString(key, ts8)
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
    }

    /*
    * This test evaluates the merge of deltas return by call to put method.
    * Call to get should return the values set by puts registered in the first replica.
    */
    "MV merge deltas returned by put operations" {
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
        val map1 = Map()
        val map2 = Map()

        val opBoolean1 = map1.putMV(key1, valBoolean1, ts1)
        val opDouble1 = map1.putMV(key1, valDouble1, ts2)
        val opInt1 = map1.putMV(key1, valInt1, ts3)
        val opString1 = map1.putMV(key1, valString1, ts4)
        val opBoolean2 = map1.putMV(key1, valBoolean2, ts5)
        val opDouble2 = map1.putMV(key1, valDouble2, ts6)
        val opInt2 = map1.putMV(key1, valInt2, ts7)
        val opString2 = map1.putMV(key1, valString2, ts8)
        val opBoolean3 = map1.putMV(key2, valBoolean1, ts9)
        val opDouble3 = map1.putMV(key2, valDouble1, ts10)
        val opInt3 = map1.putMV(key2, valInt1, ts11)
        val opString3 = map1.putMV(key2, valString1, ts12)
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
    }

    /*
    * This test evaluates the merge of deltas return by call to put and delete methods.
    * Call to get should return the value set by put or null if it has been deleted.
    */
    "MV merge deltas returned by put and delete operations" {
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
        val map1 = Map()
        val map2 = Map()

        val opBoolean1 = map1.putMV(key1, valueBoolean, ts1)
        val opDouble1 = map1.putMV(key1, valueDouble, ts2)
        val opInt1 = map1.putMV(key1, valueInt, ts3)
        val opString1 = map1.putMV(key1, valueString, ts4)
        val opBoolean2 = map1.deleteMVBoolean(key1, ts5)
        val opDouble2 = map1.deleteMVDouble(key1, ts6)
        val opInt2 = map1.deleteMVInt(key1, ts7)
        val opString2 = map1.deleteMVString(key1, ts8)
        val opBoolean3 = map1.putMV(key2, valueBoolean, ts9)
        val opDouble3 = map1.putMV(key2, valueDouble, ts10)
        val opInt3 = map1.putMV(key2, valueInt, ts11)
        val opString3 = map1.putMV(key2, valueString, ts12)
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
    }

    /*
    * This test evaluates the generation of delta (only put) plus its merging into another replica.
    * Call to get should return the values set by puts registered in the first replica after w.r.t
    * the given context.
    */
    "MV generate delta" {
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
        val map1 = Map()
        val map2 = Map()

        map1.putMV(key1, valueBoolean, ts1)
        map1.putMV(key1, valueDouble, ts2)
        map1.putMV(key1, valueInt, ts3)
        map1.putMV(key1, valueString, ts4)
        map1.putMV(key2, valueBoolean, ts5)
        map1.putMV(key2, valueDouble, ts6)
        map1.putMV(key2, valueInt, ts7)
        map1.putMV(key2, valueString, ts8)
        map1.putMV(key3, valueBoolean, ts9)
        map1.putMV(key3, valueDouble, ts10)
        map1.putMV(key3, valueInt, ts11)
        map1.putMV(key3, valueString, ts12)
        map1.putMV(key4, valueBoolean, ts13)
        map1.putMV(key4, valueDouble, ts14)
        map1.putMV(key4, valueInt, ts15)
        map1.putMV(key4, valueString, ts16)
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
    }

    /*
    * This test evaluates the generation of delta (including delete) plus its merging into another replica.
    * Call to get should return the values set by puts or null set by delete w.r.t the given context.
    */
    "MV generate delta with delete" {
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
        val map1 = Map()
        val map2 = Map()

        map1.putMV(key1, valueBoolean, ts1)
        map1.putMV(key1, valueDouble, ts2)
        map1.putMV(key1, valueInt, ts3)
        map1.putMV(key1, valueString, ts4)
        map1.putMV(key2, valueBoolean, ts5)
        map1.putMV(key2, valueDouble, ts6)
        map1.putMV(key2, valueInt, ts7)
        map1.putMV(key2, valueString, ts8)
        map1.deleteMVBoolean(key2, ts9)
        map1.deleteMVDouble(key2, ts10)
        map1.deleteMVInt(key2, ts11)
        map1.deleteMVString(key2, ts12)
        map1.putMV(key3, valueBoolean, ts13)
        map1.putMV(key3, valueDouble, ts14)
        map1.putMV(key3, valueInt, ts15)
        map1.putMV(key3, valueString, ts16)
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
    }
    
    /**
    * This test evaluates the scenario: increment get.
    * Call to get should return the value set by increment.
    */
    "CNT increment and get" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.tick()
        val inc = 10
        val key = "key"
        val map = Map()

        map.increment(key, inc, ts)

        map.getCntInt(key).shouldBe(inc)
    }


    /**
    * This test evaluates the scenario: decrement get.
    * Call to get should return the inverse of value set by decrement.
    */
    "CNT decrement and get" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.tick()
        val dec = 10
        val key = "key"
        val map = Map()

        map.decrement(key, dec, ts)

        map.getCntInt(key).shouldBe(-dec)
    }

    /**
    * This test evaluates the scenario: increment(with a negative value) get.
    * Call to get should return the value set by increment.
    */
    "CNT increment with negative amount and get" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.tick()
        val inc = -10
        val key = "key"
        val map = Map()

        map.increment(key, inc, ts)

        map.getCntInt(key).shouldBe(inc)
    }

    /**
    * This test evaluates the scenario: decrement(with a negative value) get.
    * Call to get should return the inverse of value set by decrement.
    */
    "CNT decrement with a negative amount and get" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.tick()
        val dec = -10
        val key = "key"
        val map = Map()

        map.decrement(key, dec, ts)

        map.getCntInt(key).shouldBe(-dec)
    }

    /**
    * This test evaluates the scenario: incremement(multiple times) get.
    * Call to get should return the sum of values set by calls to increment.
    */
    "CNT multiple increments and get" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val inc1 = 10
        val inc2 = 1
        val inc3 = 100
        val key = "key"
        val map = Map()

        map.increment(key, inc1, ts1)
        map.increment(key, inc2, ts2)
        map.increment(key, inc3, ts3)

        map.getCntInt(key).shouldBe(111)
    }

    /**
    * This test evaluates the scenario: decremement(multiple times) get.
    * Call to get should return the inverse of the sum of values set by calls to decrement.
    */
    "CNT multiple decrements and get" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val dec1 = 10
        val dec2 = 1
        val dec3 = 100
        val key = "key"
        val map = Map()

        map.decrement(key, dec1, ts1)
        map.decrement(key, dec2, ts2)
        map.decrement(key, dec3, ts3)

        map.getCntInt(key).shouldBe(-111)
    }

    /**
    * This test evaluates the scenario: multiple increment and decrement get.
    * Call to get should return the sum of increments minus the sum of decrements.
    */
    "CNT increment, decrement, get positive value" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val inc1 = 42
        val inc2 = 34
        val dec1 = 27
        val dec2 = 2
        val key = "key"
        val map = Map()

        map.increment(key, inc1, ts1)
        map.decrement(key, dec1, ts2)
        map.increment(key, inc2, ts3)
        map.decrement(key, dec2, ts4)

        map.getCntInt(key).shouldBe(47)
    }

    /**
    * This test evaluates the scenario: multiple increment and decrement get.
    * Call to get should return the sum of increments minus the sum of decrements.
    */
    "CNT increment, decrement, get a negative value" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val inc1 = 42
        val inc2 = 34
        val dec1 = 77
        val dec2 = 13
        val key = "key"
        val map = Map()

        map.increment(key, inc1, ts1)
        map.decrement(key, dec1, ts2)
        map.increment(key, inc2, ts3)
        map.decrement(key, dec2, ts4)

        map.getCntInt(key).shouldBe(-14)
    }

    /**
    * This test evaluates the scenario: increment || merge get.
    * Call to get should return value set by increment in the first replica.
    */
    "CNT R1: increment; R2: merge and get" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.tick()
        val inc = 11 
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        map1.increment(key, inc, ts)
        map2.merge(map1)
        map1.merge(map2)

        map1.getCntInt(key).shouldBe(11)
        map2.getCntInt(key).shouldBe(11)
    }

    /**
    * This test evaluates the scenario: decrement || merge get.
    * Call to get should return the inverse value set by decrement in the first replica.
    */
    "CNT R1: decrement; R2: merge and get" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.tick()
        val dec = 11 
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        map1.decrement(key, dec, ts)
        map2.merge(map1)
        map1.merge(map2)

        map1.getCntInt(key).shouldBe(-11)
        map2.getCntInt(key).shouldBe(-11)
    }

    /**
    * This test evaluates the scenario: increment || increment merge get.
    * Call to get should return sum of the two increment values.
    */
    "CNT R1: increment; R2: increment, merge, get" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.tick()
        val ts2 = dc2.tick()
        val inc1 = 10 
        val inc2 = 1 
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        map1.increment(key, inc1, ts1)
        map2.increment(key, inc2, ts2)
        map2.merge(map1)

        map2.getCntInt(key).shouldBe(11)
    }

    /**
    * This test evaluates the scenario: increment || merge increment get.
    * Call to get should return sum of the two increment values.
    */
    "CNT R1: increment; R2: merge, increment, get" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.tick()
        val ts2 = dc2.tick()
        val inc1 = 10 
        val inc2 = 1 
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        map1.increment(key, inc1, ts1)
        map2.merge(map1)
        map2.increment(key, inc2, ts2)

        map2.getCntInt(key).shouldBe(11)
    }

    /**
    * This test evaluates the scenario: decrement || decrement merge get.
    * Call to get should return the inverse of the sum of the two decrement values.
    */
    "CNT R1: decrement; R2: decrement, merge, get" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.tick()
        val ts2 = dc2.tick()
        val dec1 = 10 
        val dec2 = 1 
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        map1.decrement(key, dec1, ts1)
        map2.decrement(key, dec2, ts2)
        map2.merge(map1)

        map2.getCntInt(key).shouldBe(-11)
    }

    /**
    * This test evaluates the scenario: decrement || merge decrement get.
    * Call to get should return the inverse of the sum of the two decrement values.
    */
    "CNT R1: decrement; R2: merge, decrement, get" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.tick()
        val ts2 = dc2.tick()
        val dec1 = 10 
        val dec2 = 1 
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        map1.decrement(key, dec1, ts1)
        map2.merge(map1)
        map2.decrement(key, dec2, ts2)

        map2.getCntInt(key).shouldBe(-11)
    }

    /**
    * This test evaluates the scenario: some operations || some operations merge get.
    * Call to get should return the sum of increment values minus the sum of the decrement values.
    */
    "CNT R1: multiple operations; R2: multiple operations, merge, get" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.tick()
        val ts2 = dc2.tick()
        val ts3 = dc1.tick()
        val ts4 = dc2.tick()
        val ts5 = dc1.tick()
        val ts6 = dc2.tick()
        val ts7 = dc1.tick()
        val ts8 = dc2.tick()
        val dec1 = 10
        val dec2 = 20
        val dec3 = 30
        val dec4 = 40
        val inc1 = 10
        val inc2 = 30
        val inc3 = 50
        val inc4 = 70
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        map1.decrement(key, dec1, ts1)
        map1.increment(key, inc1, ts3)
        map1.increment(key, inc2, ts5)
        map1.decrement(key, dec2, ts7)
        map2.decrement(key, dec3, ts2)
        map2.increment(key, inc3, ts4)
        map2.increment(key, inc4, ts6)
        map2.decrement(key, dec4, ts8)
        map2.merge(map1)

        map2.getCntInt(key).shouldBe(60)
    }

    /**
    * This test evaluates the scenario: some operations || merge some operations get.
    * Call to get should return the sum of increment values minus the sum of the decrement values.
    */
    "CNT R1: multiple operations; R2: merge, multiple operations, get" {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.tick()
        val ts2 = dc2.tick()
        val ts3 = dc1.tick()
        val ts4 = dc2.tick()
        val ts5 = dc1.tick()
        val ts6 = dc2.tick()
        val ts7 = dc1.tick()
        val ts8 = dc2.tick()
        val dec1 = 10
        val dec2 = 20
        val dec3 = 30
        val dec4 = 40
        val inc1 = 10
        val inc2 = 30
        val inc3 = 50
        val inc4 = 70
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        map1.decrement(key, dec1, ts1)
        map1.increment(key, inc1, ts3)
        map1.increment(key, inc2, ts5)
        map1.decrement(key, dec2, ts7)
        map2.merge(map1)
        map2.decrement(key, dec3, ts2)
        map2.increment(key, inc3, ts4)
        map2.increment(key, inc4, ts6)
        map2.decrement(key, dec4, ts8)

        map2.getCntInt(key).shouldBe(60)
    }

    /**
    * This test evaluates the use of delta return by call to increment method.
    * Call to get should return the increment value set in the first replica.
    */
    "CNT use delta return by increment" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.tick()
        val inc = 11
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        val incOp = map1.increment(key, inc, ts)
        map2.merge(incOp)
        map1.merge(incOp)

        map1.getCntInt(key).shouldBe(11)
        map2.getCntInt(key).shouldBe(11)
    }

    /**
    * This test evaluates the use of delta return by call to decrement method.
    * Call to get should return the inverse of the decrement value set in the first replica.
    */
    "CNT use delta return by decrement" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.tick()
        val dec = 11 
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        val decOp = map1.decrement(key, dec, ts)
        map2.merge(decOp)
        map1.merge(decOp)

        map1.getCntInt(key).shouldBe(-11)
        map2.getCntInt(key).shouldBe(-11)
    }

    /**
    * This test evaluates the use of delta return by call to increment and decrement methods.
    * Call to get should return the sum of increment values minus the sum of decrement values.
    */
    "CNT use delta return by increment and decrement" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val dec = 11 
        val inc = 22 
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        val decOp = map1.decrement(key, dec, ts1)
        val incOp = map1.increment(key, inc, ts2)
        map2.merge(decOp)
        map2.merge(incOp)
        map1.merge(decOp)
        map1.merge(incOp)

        map1.getCntInt(key).shouldBe(11)
        map2.getCntInt(key).shouldBe(11)
    }

    /*
    * This test evaluates the generation of delta plus its merging into another replica.
    * Call to get should return the values set by operations registered in the first replica after
    * w.r.t the given context (here only the decrements).
    */
    "CNT generate delta" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val vv = dc.getState()
        val ts3 = dc.tick()
        val ts4 = dc.tick()
        val inc1 = 11
        val inc2 = 33
        val dec1 = 10
        val dec2 = 20
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        map1.increment(key, inc1, ts1)
        map1.increment(key, inc2, ts2)
        map1.decrement(key, dec1, ts3)
        map1.decrement(key, dec2, ts4)
        val delta = map1.generateDelta(vv)
        map2.merge(delta)

        map2.getCntInt(key).shouldBe(-30)
    }

    /**
    * This test evaluates JSON serialization an empty map.
    **/
    "empty JSON serialization" {
        val map = Map()

        val mapJson = map.toJson()

        mapJson.shouldBe("""{"_type":"Map","_metadata":{"lwwMap":{"entries":{}},"mvMap":{"entries":{},"causalContext":{"entries":[]}},"cntMap":{}}}""")
    }

    /**
    * This test evaluates JSON deserialization of an empty map.
    **/
    "empty JSON deserialization" {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.tick()
        val ts2 = dc.tick()
        val ts3 = dc.tick()

        val mapJson = Map.fromJson("""{"_type":"Map","_metadata":{"lwwMap":{"entries":{}},"mvMap":{"entries":{},"causalContext":{"entries":[]}},"cntMap":{}}}""")
        mapJson.putLWW("key1", "value1", ts1)
        mapJson.putMV("key1", "value1", ts2)
        mapJson.increment("key1", 42, ts3)

        mapJson.getLWWString("key1").shouldBe("value1")
        mapJson.getMVString("key1")!!.shouldHaveSingleElement("value1")
        mapJson.getCntInt("key1").shouldBe(42)
        mapJson.getLWWString("key2").shouldBeNull()
        mapJson.getMVString("key2").shouldBeNull()
        mapJson.getCntInt("key2").shouldBeNull()
    }

    /**
    * This test evaluates JSON serialization of a map.
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
        val ts7 = dc.tick()
        val ts8 = dc.tick()
        val ts9 = dc.tick()
        val ts10 = dc.tick()

        val map = Map()

        map.putLWW("key", true, ts1)
        map.putLWW("key", 3.14, ts2)
        map.putLWW("key", 42, ts3)
        map.putLWW("key", "value", ts4)
        map.putMV("key", true, ts5)
        map.putMV("key", 3.14, ts6)
        map.putMV("key", 42, ts7)
        map.putMV("key", "value", ts8)
        map.increment("key", 42, ts9)
        map.decrement("key", 11, ts10)
        val mapJson = map.toJson()

        mapJson.shouldBe("""{"_type":"Map","_metadata":{"lwwMap":{"entries":{"key%BOOLEAN":{"uid":{"name":"dcid"},"cnt":-2147483647},"key%DOUBLE":{"uid":{"name":"dcid"},"cnt":-2147483646},"key%INTEGER":{"uid":{"name":"dcid"},"cnt":-2147483645},"key%STRING":{"uid":{"name":"dcid"},"cnt":-2147483644}}},"mvMap":{"entries":{"key%BOOLEAN":[{"uid":{"name":"dcid"},"cnt":-2147483643}],"key%DOUBLE":[{"uid":{"name":"dcid"},"cnt":-2147483642}],"key%INTEGER":[{"uid":{"name":"dcid"},"cnt":-2147483641}],"key%STRING":[{"uid":{"name":"dcid"},"cnt":-2147483640}]},"causalContext":{"entries":[{"name":"dcid"},-2147483640]}},"cntMap":{"key":{"increment":[{"name":"dcid"},{"first":42,"second":{"uid":{"name":"dcid"},"cnt":-2147483639}}],"decrement":[{"name":"dcid"},{"first":11,"second":{"uid":{"name":"dcid"},"cnt":-2147483638}}]}}},"key%BOOLEAN%LWW":true,"key%DOUBLE%LWW":3.14,"key%INTEGER%LWW":42,"key%STRING%LWW":"value","key%BOOLEAN%MV":[true],"key%DOUBLE%MV":[3.14],"key%INTEGER%MV":[42],"key%STRING%MV":["value"],"key%CNT":31}""")
    }

    /**
    * This test evaluates JSON deserialization of a map.
    **/
    "JSON deserialization" {
        val mapJson = Map.fromJson("""{"_type":"Map","_metadata":{"lwwMap":{"entries":{"key%BOOLEAN":{"uid":{"name":"dcid"},"cnt":-2147483647},"key%DOUBLE":{"uid":{"name":"dcid"},"cnt":-2147483646},"key%INTEGER":{"uid":{"name":"dcid"},"cnt":-2147483645},"key%STRING":{"uid":{"name":"dcid"},"cnt":-2147483644}}},"mvMap":{"entries":{"key%BOOLEAN":[{"uid":{"name":"dcid"},"cnt":-2147483643}],"key%DOUBLE":[{"uid":{"name":"dcid"},"cnt":-2147483642}],"key%INTEGER":[{"uid":{"name":"dcid"},"cnt":-2147483641}],"key%STRING":[{"uid":{"name":"dcid"},"cnt":-2147483640}]},"causalContext":{"entries":[{"name":"dcid"},-2147483640]}},"cntMap":{"key":{"increment":[{"name":"dcid"},{"first":42,"second":{"uid":{"name":"dcid"},"cnt":-2147483639}}],"decrement":[{"name":"dcid"},{"first":11,"second":{"uid":{"name":"dcid"},"cnt":-2147483638}}]}}},"key%BOOLEAN%LWW":true,"key%DOUBLE%LWW":3.14,"key%INTEGER%LWW":42,"key%STRING%LWW":"value","key%BOOLEAN%MV":[true],"key%DOUBLE%MV":[3.14],"key%INTEGER%MV":[42],"key%STRING%MV":["value"],"key%CNT":31}""")

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
