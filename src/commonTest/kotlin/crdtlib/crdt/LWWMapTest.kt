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

package crdtlib.test

import crdtlib.crdt.LWWMap
import crdtlib.utils.DCUId
import crdtlib.utils.SimpleEnvironment
import crdtlib.utils.VersionVector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
* Represents a suite test for LWWMap.
**/
class LWWMapTest {
    /**
    * This test evaluates the scenario: get.
    * Call to get should return null
    */
    @Test
    fun emptyGet() {
        val key = "key"
        val map = LWWMap()

        assertNull(map.getBoolean(key))
        assertNull(map.getDouble(key))
        assertNull(map.getInt(key))
        assertNull(map.getString(key))
    }

    /**
    * This test evaluates the scenario: put get.
    * Call to get should return the value set by the put.
    */
    @Test
    fun putGet() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
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

        assertEquals(valueBoolean, map.getBoolean(key))
        assertEquals(valueDouble, map.getDouble(key))
        assertEquals(valueInt, map.getInt(key))
        assertEquals(valueString, map.getString(key))
    }

    /**
    * This test evaluates the scenario: put del get.
    * Call to get should return null.
    */
    @Test
    fun putDelGet() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        dc.updateStateTS(ts4)
        val ts5 = dc.getNewTimestamp()
        dc.updateStateTS(ts5)
        val ts6 = dc.getNewTimestamp()
        dc.updateStateTS(ts6)
        val ts7 = dc.getNewTimestamp()
        dc.updateStateTS(ts7)
        val ts8 = dc.getNewTimestamp()
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

        assertNull(map.getBoolean(key))
        assertNull(map.getDouble(key))
        assertNull(map.getInt(key))
        assertNull(map.getString(key))
    }

    /**
    * This test evaluates the scenario: del get.
    * Call to get should return null.
    */
    @Test
    fun delGet() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val key = "key"
        val map = LWWMap()

        map.deleteBoolean(key, ts1)
        map.deleteDouble(key, ts2)
        map.deleteInt(key, ts3)
        map.deleteString(key, ts4)

        assertNull(map.getBoolean(key))
        assertNull(map.getDouble(key))
        assertNull(map.getInt(key))
        assertNull(map.getString(key))
    }

    /**
    * This test evaluates the scenario: put put get
    * Call to get should return the value set by the second put.
    */
    @Test
    fun putPutGet() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        dc.updateStateTS(ts4)
        val ts5 = dc.getNewTimestamp()
        dc.updateStateTS(ts5)
        val ts6 = dc.getNewTimestamp()
        dc.updateStateTS(ts6)
        val ts7 = dc.getNewTimestamp()
        dc.updateStateTS(ts7)
        val ts8 = dc.getNewTimestamp()
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

        assertEquals(valBoolean2, map.getBoolean(key))
        assertEquals(valDouble2, map.getDouble(key))
        assertEquals(valInt2, map.getInt(key))
        assertEquals(valString2, map.getString(key))
    }

    /**
    * This test evaluates the scenario: put put del get.
    * Call to get should return null.
    */
    @Test
    fun putPutDelGet() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        dc.updateStateTS(ts4)
        val ts5 = dc.getNewTimestamp()
        dc.updateStateTS(ts5)
        val ts6 = dc.getNewTimestamp()
        dc.updateStateTS(ts6)
        val ts7 = dc.getNewTimestamp()
        dc.updateStateTS(ts7)
        val ts8 = dc.getNewTimestamp()
        dc.updateStateTS(ts8)
        val ts9 = dc.getNewTimestamp()
        dc.updateStateTS(ts9)
        val ts10 = dc.getNewTimestamp()
        dc.updateStateTS(ts10)
        val ts11 = dc.getNewTimestamp()
        dc.updateStateTS(ts11)
        val ts12 = dc.getNewTimestamp()
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

        assertNull(map.getBoolean(key))
        assertNull(map.getDouble(key))
        assertNull(map.getInt(key))
        assertNull(map.getString(key))
    }

    /**
    * This test evaluates the scenario: put || merge get.
    * Call to get should return the value set by the put registered in the first replica.
    */
    @Test
    fun put_MergeGet() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
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

        assertEquals(valueBoolean, map1.getBoolean(key))
        assertEquals(valueDouble, map1.getDouble(key))
        assertEquals(valueInt, map1.getInt(key))
        assertEquals(valueString, map1.getString(key))
        assertEquals(valueBoolean, map2.getBoolean(key))
        assertEquals(valueDouble, map2.getDouble(key))
        assertEquals(valueInt, map2.getInt(key))
        assertEquals(valueString, map2.getString(key))
    }

    /**
    * This test evaluates the scenario: put || merge putLWW get.
    * Call to get should return the value set by put registered in the second replica.
    */
    @Test
    fun put_MergePutLWWGet() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts3 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts3)
        val ts5 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts5)
        val ts7 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts4 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts4)
        val ts6 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts6)
        val ts8 = dc2.getNewTimestamp()
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

        assertEquals(valBoolean2, map2.getBoolean(key))
        assertEquals(valDouble2, map2.getDouble(key))
        assertEquals(valInt2, map2.getInt(key))
        assertEquals(valString2, map2.getString(key))
    }

    /**
    * This test evaluates the scenario: put || putLWW merge get
    * Call to get should return the value set by put registered in the second replica.
    */
    @Test
    fun put_PutLWWMergeGet() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts3 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts3)
        val ts5 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts5)
        val ts7 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts4 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts4)
        val ts6 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts6)
        val ts8 = dc2.getNewTimestamp()
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

        assertEquals(valBoolean2, map2.getBoolean(key))
        assertEquals(valDouble2, map2.getDouble(key))
        assertEquals(valInt2, map2.getInt(key))
        assertEquals(valString2, map2.getString(key))
    }

    /**
    * This test evaluates the scenario: putLWW || put merge get.
    * Call to get should return the value set by put registered in the first replica.
    */
    @Test
    fun putLWW_PutMergeGet() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts3 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts3)
        val ts5 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts5)
        val ts7 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts4 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts4)
        val ts6 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts6)
        val ts8 = dc2.getNewTimestamp()
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

        assertEquals(valBoolean1, map2.getBoolean(key))
        assertEquals(valDouble1, map2.getDouble(key))
        assertEquals(valInt1, map2.getInt(key))
        assertEquals(valString1, map2.getString(key))
    }

    /**
    * This test evaluates the scenario: put delLWW || put merge get.
    * Call to get should return null.
    */
    @Test
    fun putDelLWW_PutMergeGet() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts3 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts3)
        val ts5 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts5)
        val ts7 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts4 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts4)
        val ts6 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts6)
        val ts8 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts8)
        val ts9 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts9)
        val ts10 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts10)
        val ts11 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts11)
        val ts12 = dc2.getNewTimestamp()
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

        assertNull(map2.getBoolean(key))
        assertNull(map2.getDouble(key))
        assertNull(map2.getInt(key))
        assertNull(map2.getString(key))
    }

    /**
    * This test evaluates the scenario: put delLWW || put merge(before del) merge(after del) get.
    * Call to get should return null.
    */
    @Test
    fun putDelLWW_PutMergeBeforeDelMergeAfterDelGet() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts3 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts3)
        val ts5 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts5)
        val ts7 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts4 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts4)
        val ts6 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts6)
        val ts8 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts8)
        val ts9 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts9)
        val ts10 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts10)
        val ts11 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts11)
        val ts12 = dc2.getNewTimestamp()
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

        assertNull(map2.getBoolean(key))
        assertNull(map2.getDouble(key))
        assertNull(map2.getInt(key))
        assertNull(map2.getString(key))
    }

    /*
    * This test evaluates the scenario: put del || putLWW merge get.
    * Call to get should return the value set by put registered in the second replica.
    */
    @Test
    fun putDel_PutLWWMergeGet() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts3 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts3)
        val ts5 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts5)
        val ts7 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts7)
        val ts9 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts9)
        val ts11 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts11)
        val ts13 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts13)
        val ts15 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts4 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts4)
        val ts6 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts6)
        val ts8 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts8)
        val ts10 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts10)
        val ts12 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts12)
        val ts14 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts14)
        val ts16 = dc2.getNewTimestamp()
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

        assertEquals(valBoolean2, map2.getBoolean(key))
        assertEquals(valDouble2, map2.getDouble(key))
        assertEquals(valInt2, map2.getInt(key))
        assertEquals(valString2, map2.getString(key))
    }

    /*
    * This test evaluates the scenario: put del || putLWW merge(before del) merge(after del) get.
    * Call to get should return the value set by put registered in the second replica.
    */
    @Test
    fun putDel_PutLWWMergeBeforeDelMergeAfterDelGet() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts3 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts3)
        val ts5 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts5)
        val ts7 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts7)
        val ts9 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts9)
        val ts11 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts11)
        val ts13 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts13)
        val ts15 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts4 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts4)
        val ts6 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts6)
        val ts8 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts8)
        val ts10 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts10)
        val ts12 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts12)
        val ts14 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts14)
        val ts16 = dc2.getNewTimestamp()
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

        assertEquals(valBoolean2, map2.getBoolean(key))
        assertEquals(valDouble2, map2.getDouble(key))
        assertEquals(valInt2, map2.getInt(key))
        assertEquals(valString2, map2.getString(key))
    }

    /*
    * This test evaluates the scenario: put || put || merge1 delLWW merge2 get.
    * Call to get should return null.
    */
    @Test
    fun put_Put_Merge1DelLWWMerge2Get() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val dc3 = SimpleEnvironment(uid3)
        val ts1 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts4 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts4)
        val ts7 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts7)
        val ts10 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts5 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts5)
        val ts8 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts8)
        val ts11 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        dc3.updateStateTS(ts3)
        val ts6 = dc3.getNewTimestamp()
        dc3.updateStateTS(ts6)
        val ts9 = dc3.getNewTimestamp()
        dc3.updateStateTS(ts9)
        val ts12 = dc3.getNewTimestamp()
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

        assertNull(map3.getBoolean(key))
        assertNull(map3.getDouble(key))
        assertNull(map3.getInt(key))
        assertNull(map3.getString(key))
    }

    /*
    * This test evaluates the scenario: putLWW || put || merge1 del merge2 get.
    * Call to get should return the value set by put registered in the second replica.
    */
    @Test
    fun put_PutLWW_Merge1DelMerge2Get() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val uid3 = DCUId("dcid3")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val dc3 = SimpleEnvironment(uid3)
        val ts1 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts4 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts4)
        val ts7 = dc1.getNewTimestamp()
        dc1.updateStateTS(ts7)
        val ts10 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts5 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts5)
        val ts8 = dc2.getNewTimestamp()
        dc2.updateStateTS(ts8)
        val ts11 = dc2.getNewTimestamp()
        val ts3 = dc3.getNewTimestamp()
        dc3.updateStateTS(ts3)
        val ts6 = dc3.getNewTimestamp()
        dc3.updateStateTS(ts6)
        val ts9 = dc3.getNewTimestamp()
        dc3.updateStateTS(ts9)
        val ts12 = dc3.getNewTimestamp()
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
        map1.put(key, valInt1, ts8)
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

        assertEquals(valBoolean2, map3.getBoolean(key))
        assertEquals(valDouble2, map3.getDouble(key))
        assertEquals(valInt2, map3.getInt(key))
        assertEquals(valString2, map3.getString(key))
    }

    /*
    * This test evaluates the use of deltas return by call to put method.
    * Call to get should return the value set by put registered in the first replica.
    */
    @Test
    fun putOp() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
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

        assertEquals(valueBoolean, map1.getBoolean(key))
        assertEquals(valueDouble, map1.getDouble(key))
        assertEquals(valueInt, map1.getInt(key))
        assertEquals(valueString, map1.getString(key))
        assertEquals(valueBoolean, map2.getBoolean(key))
        assertEquals(valueDouble, map2.getDouble(key))
        assertEquals(valueInt, map2.getInt(key))
        assertEquals(valueString, map2.getString(key))
    }

    /*
    * This test evaluates the use of deltas return by call to put and delete methods.
    * Call to get should return null.
    */
    @Test
    fun putDelOp() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        dc.updateStateTS(ts4)
        val ts5 = dc.getNewTimestamp()
        dc.updateStateTS(ts5)
        val ts6 = dc.getNewTimestamp()
        dc.updateStateTS(ts6)
        val ts7 = dc.getNewTimestamp()
        dc.updateStateTS(ts7)
        val ts8 = dc.getNewTimestamp()
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

        assertNull(map1.getBoolean(key))
        assertNull(map1.getDouble(key))
        assertNull(map1.getInt(key))
        assertNull(map1.getString(key))
        assertNull(map2.getBoolean(key))
        assertNull(map2.getDouble(key))
        assertNull(map2.getInt(key))
        assertNull(map2.getString(key))
    }

    /*
    * This test evaluates the merge of deltas return by call to put method.
    * Call to get should return the values set by puts registered in the first replica.
    */
    @Test
    fun putOpFusion() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        dc.updateStateTS(ts4)
        val ts5 = dc.getNewTimestamp()
        dc.updateStateTS(ts5)
        val ts6 = dc.getNewTimestamp()
        dc.updateStateTS(ts6)
        val ts7 = dc.getNewTimestamp()
        dc.updateStateTS(ts7)
        val ts8 = dc.getNewTimestamp()
        dc.updateStateTS(ts8)
        val ts9 = dc.getNewTimestamp()
        dc.updateStateTS(ts9)
        val ts10 = dc.getNewTimestamp()
        dc.updateStateTS(ts10)
        val ts11 = dc.getNewTimestamp()
        dc.updateStateTS(ts11)
        val ts12 = dc.getNewTimestamp()
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

        assertEquals(valBoolean2, map2.getBoolean(key1))
        assertEquals(valDouble2, map2.getDouble(key1))
        assertEquals(valInt2, map2.getInt(key1))
        assertEquals(valString2, map2.getString(key1))
        assertEquals(valBoolean1, map2.getBoolean(key2))
        assertEquals(valDouble1, map2.getDouble(key2))
        assertEquals(valInt1, map2.getInt(key2))
        assertEquals(valString1, map2.getString(key2))
    }

    /*
    * This test evaluates the merge of deltas return by call to put and delete methods.
    * Call to get should return the value set by put or null if it has been deleted.
    */
    @Test
    fun putDelOpFusion() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        dc.updateStateTS(ts4)
        val ts5 = dc.getNewTimestamp()
        dc.updateStateTS(ts5)
        val ts6 = dc.getNewTimestamp()
        dc.updateStateTS(ts6)
        val ts7 = dc.getNewTimestamp()
        dc.updateStateTS(ts7)
        val ts8 = dc.getNewTimestamp()
        dc.updateStateTS(ts8)
        val ts9 = dc.getNewTimestamp()
        dc.updateStateTS(ts9)
        val ts10 = dc.getNewTimestamp()
        dc.updateStateTS(ts10)
        val ts11 = dc.getNewTimestamp()
        dc.updateStateTS(ts11)
        val ts12 = dc.getNewTimestamp()
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

        assertNull(map2.getBoolean(key1))
        assertNull(map2.getDouble(key1))
        assertNull(map2.getInt(key1))
        assertNull(map2.getString(key1))
        assertEquals(valueBoolean, map2.getBoolean(key2))
        assertEquals(valueDouble, map2.getDouble(key2))
        assertEquals(valueInt, map2.getInt(key2))
        assertEquals(valueString, map2.getString(key2))
    }

    /*
    * This test evaluates the generation of delta (only put) plus its merging into another replica.
    * Call to get should return the values set by puts registered in the first replica after w.r.t
    * the given context.
    */
    @Test
    fun generateDelta() {
        val uid = DCUId("dcid1")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        dc.updateStateTS(ts4)
        val ts5 = dc.getNewTimestamp()
        dc.updateStateTS(ts5)
        val ts6 = dc.getNewTimestamp()
        dc.updateStateTS(ts6)
        val ts7 = dc.getNewTimestamp()
        dc.updateStateTS(ts7)
        val ts8 = dc.getNewTimestamp()
        dc.updateStateTS(ts8)
        val ts9 = dc.getNewTimestamp()
        dc.updateStateTS(ts9)
        val ts10 = dc.getNewTimestamp()
        dc.updateStateTS(ts10)
        val ts11 = dc.getNewTimestamp()
        dc.updateStateTS(ts11)
        val ts12 = dc.getNewTimestamp()
        dc.updateStateTS(ts12)
        val ts13 = dc.getNewTimestamp()
        dc.updateStateTS(ts13)
        val ts14 = dc.getNewTimestamp()
        dc.updateStateTS(ts14)
        val ts15 = dc.getNewTimestamp()
        dc.updateStateTS(ts15)
        val ts16 = dc.getNewTimestamp()
        val vv = VersionVector()
        vv.addTS(ts8)
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

        assertNull(map2.getBoolean(key1))
        assertNull(map2.getDouble(key1))
        assertNull(map2.getInt(key1))
        assertNull(map2.getString(key1))
        assertNull(map2.getBoolean(key2))
        assertNull(map2.getDouble(key2))
        assertNull(map2.getInt(key2))
        assertNull(map2.getString(key2))
        assertEquals(valueBoolean, map2.getBoolean(key3))
        assertEquals(valueDouble, map2.getDouble(key3))
        assertEquals(valueInt, map2.getInt(key3))
        assertEquals(valueString, map2.getString(key3))
        assertEquals(valueBoolean, map2.getBoolean(key4))
        assertEquals(valueDouble, map2.getDouble(key4))
        assertEquals(valueInt, map2.getInt(key4))
        assertEquals(valueString, map2.getString(key4))
    }

    /*
    * This test evaluates the generation of delta (including delete) plus its merging into another replica.
    * Call to get should return the values set by puts or null set by delete w.r.t the given context.
    */
    @Test
    fun generateDeltaWithDel() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        dc.updateStateTS(ts4)
        val ts5 = dc.getNewTimestamp()
        dc.updateStateTS(ts5)
        val ts6 = dc.getNewTimestamp()
        dc.updateStateTS(ts6)
        val ts7 = dc.getNewTimestamp()
        dc.updateStateTS(ts7)
        val ts8 = dc.getNewTimestamp()
        dc.updateStateTS(ts8)
        val ts9 = dc.getNewTimestamp()
        dc.updateStateTS(ts9)
        val ts10 = dc.getNewTimestamp()
        dc.updateStateTS(ts10)
        val ts11 = dc.getNewTimestamp()
        dc.updateStateTS(ts11)
        val ts12 = dc.getNewTimestamp()
        dc.updateStateTS(ts12)
        val ts13 = dc.getNewTimestamp()
        dc.updateStateTS(ts13)
        val ts14 = dc.getNewTimestamp()
        dc.updateStateTS(ts14)
        val ts15 = dc.getNewTimestamp()
        dc.updateStateTS(ts15)
        val ts16 = dc.getNewTimestamp()
        val vv = VersionVector()
        vv.addTS(ts4)
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

        assertNull(map2.getBoolean(key1))
        assertNull(map2.getDouble(key1))
        assertNull(map2.getInt(key1))
        assertNull(map2.getString(key1))
        assertNull(map2.getBoolean(key2))
        assertNull(map2.getDouble(key2))
        assertNull(map2.getInt(key2))
        assertNull(map2.getString(key2))
        assertEquals(valueBoolean, map2.getBoolean(key3))
        assertEquals(valueDouble, map2.getDouble(key3))
        assertEquals(valueInt, map2.getInt(key3))
        assertEquals(valueString, map2.getString(key3))
    }

    /**
    * This test evaluates JSON serialization an empty lww map.
    **/
    @Test
    fun emptyToJsonSerialization() {
        val map = LWWMap()

        val mapJson = map.toJson()

        assertEquals("""{"_type":"LWWMap","_metadata":{"entries":{}}}""", mapJson)
    }

    /**
    * This test evaluates JSON deserialization of an empty lww map.
    **/
    @Test
    fun emptyFromJsonDeserialization() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()

        val mapJson = LWWMap.fromJson("""{"_type":"LWWMap","_metadata":{"entries":{}}}""")
        mapJson.put("key1", "value1", ts)

        assertEquals("value1", mapJson.getString("key1"))
        assertNull(mapJson.getString("key2"))
        assertNull(mapJson.getString("key3"))
    }

    /**
    * This test evaluates JSON serialization of a lww map.
    **/
    @Test
    fun toJsonSerialization() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
        dc.updateStateTS(ts4)
        val ts5 = dc.getNewTimestamp()
        dc.updateStateTS(ts5)
        val ts6 = dc.getNewTimestamp()
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

        assertEquals("""{"_type":"LWWMap","_metadata":{"entries":{"key1%INTEGER":{"uid":{"name":"dcid"},"cnt":-2147483648},"key2%STRING":{"uid":{"name":"dcid"},"cnt":-2147483646},"key3%STRING":{"uid":{"name":"dcid"},"cnt":-2147483645},"key4%BOOLEAN":{"uid":{"name":"dcid"},"cnt":-2147483644},"key5%DOUBLE":{"uid":{"name":"dcid"},"cnt":-2147483643}}},"key1%INTEGER":1,"key2%STRING":null,"key3%STRING":"value3","key4%BOOLEAN":true,"key5%DOUBLE":3.14159}""", mapJson)
    }

    /**
    * This test evaluates JSON deserialization of a lww map.
    **/
    @Test
    fun fromJsonDeserialization() {
        val mapJson = LWWMap.fromJson("""{"_type":"LWWMap","_metadata":{"entries":{"key1%INTEGER":{"uid":{"name":"dcid"},"cnt":-2147483648},"key2%STRING":{"uid":{"name":"dcid"},"cnt":-2147483646},"key3%STRING":{"uid":{"name":"dcid"},"cnt":-2147483645},"key4%BOOLEAN":{"uid":{"name":"dcid"},"cnt":-2147483644},"key5%DOUBLE":{"uid":{"name":"dcid"},"cnt":-2147483643}}},"key1%INTEGER":1,"key2%STRING":null,"key3%STRING":"value3","key4%BOOLEAN":true,"key5%DOUBLE":3.14159}""")

        assertEquals(1, mapJson.getInt("key1"))
        assertNull(mapJson.getString("key2"))
        assertEquals("value3", mapJson.getString("key3"))
        assertEquals(true, mapJson.getBoolean("key4"))
        assertEquals(3.14159, mapJson.getDouble("key5"))
    }
}
