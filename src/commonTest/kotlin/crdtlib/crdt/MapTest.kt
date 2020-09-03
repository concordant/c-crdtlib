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

import crdtlib.crdt.Map
import crdtlib.utils.DCUId
import crdtlib.utils.SimpleEnvironment
import crdtlib.utils.VersionVector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
* Represents a suite test for Map.
**/
class MapTest {
    /**
    * This test evaluates the scenario: get.
    * Call to get should return null
    */
    @Test
    fun emptyGet() {
        val key = "key"
        val map = Map()

        assertNull(map.getLWWBoolean(key))
        assertNull(map.getLWWDouble(key))
        assertNull(map.getLWWInt(key))
        assertNull(map.getLWWString(key))
        assertNull(map.getMVBoolean(key))
        assertNull(map.getMVDouble(key))
        assertNull(map.getMVInt(key))
        assertNull(map.getMVString(key))
    }
    
    /**
    * This test evaluates the scenario: put get.
    * Call to get should return the value set by the put.
    */
    @Test
    fun LWW_putGet() {
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
        val map = Map()

        map.putLWW(key, valueBoolean, ts1)
        map.putLWW(key, valueDouble, ts2)
        map.putLWW(key, valueInt, ts3)
        map.putLWW(key, valueString, ts4)

        assertEquals(valueBoolean, map.getLWWBoolean(key))
        assertEquals(valueDouble, map.getLWWDouble(key))
        assertEquals(valueInt, map.getLWWInt(key))
        assertEquals(valueString, map.getLWWString(key))
    }
    
    /**
    * This test evaluates the scenario: put del get.
    * Call to get should return null.
    */
    @Test
    fun LWW_putDelGet() {
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
        val map = Map()

        map.putLWW(key, valueBoolean, ts1)
        map.putLWW(key, valueDouble, ts2)
        map.putLWW(key, valueInt, ts3)
        map.putLWW(key, valueString, ts4)
        map.deleteLWWBoolean(key, ts5)
        map.deleteLWWDouble(key, ts6)
        map.deleteLWWInt(key, ts7)
        map.deleteLWWString(key, ts8)

        assertNull(map.getLWWBoolean(key))
        assertNull(map.getLWWDouble(key))
        assertNull(map.getLWWInt(key))
        assertNull(map.getLWWString(key))
    }
 
    /**
    * This test evaluates the scenario: del get.
    * Call to get should return null.
    */
    @Test
    fun LWW_delGet() {
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
        val map = Map()

        map.deleteLWWBoolean(key, ts1)
        map.deleteLWWDouble(key, ts2)
        map.deleteLWWInt(key, ts3)
        map.deleteLWWString(key, ts4)

        assertNull(map.getLWWBoolean(key))
        assertNull(map.getLWWDouble(key))
        assertNull(map.getLWWInt(key))
        assertNull(map.getLWWString(key))
    }

    /**
    * This test evaluates the scenario: put put get
    * Call to get should return the value set by the second put.
    */
    @Test
    fun LWW_putPutGet() {
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
        val map = Map()

        map.putLWW(key, valBoolean1, ts1)
        map.putLWW(key, valDouble1, ts2)
        map.putLWW(key, valInt1, ts3)
        map.putLWW(key, valString1, ts4)
        map.putLWW(key, valBoolean2, ts5)
        map.putLWW(key, valDouble2, ts6)
        map.putLWW(key, valInt2, ts7)
        map.putLWW(key, valString2, ts8)

        assertEquals(valBoolean2, map.getLWWBoolean(key))
        assertEquals(valDouble2, map.getLWWDouble(key))
        assertEquals(valInt2, map.getLWWInt(key))
        assertEquals(valString2, map.getLWWString(key))
    }

    /**
    * This test evaluates the scenario: put put del get.
    * Call to get should return null.
    */
    @Test
    fun LWW_putPutDelGet() {
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

        assertNull(map.getLWWBoolean(key))
        assertNull(map.getLWWDouble(key))
        assertNull(map.getLWWInt(key))
        assertNull(map.getLWWString(key))
    }

    /**
    * This test evaluates the scenario: put || merge get.
    * Call to get should return the value set by the put registered in the first replica.
    */
    @Test
    fun LWW_put_MergeGet() {
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
        val map1 = Map()
        val map2 = Map()

        map1.putLWW(key, valueBoolean, ts1)
        map1.putLWW(key, valueDouble, ts2)
        map1.putLWW(key, valueInt, ts3)
        map1.putLWW(key, valueString, ts4)
        map1.merge(map2)
        map2.merge(map1)

        assertEquals(valueBoolean, map1.getLWWBoolean(key))
        assertEquals(valueDouble, map1.getLWWDouble(key))
        assertEquals(valueInt, map1.getLWWInt(key))
        assertEquals(valueString, map1.getLWWString(key))
        assertEquals(valueBoolean, map2.getLWWBoolean(key))
        assertEquals(valueDouble, map2.getLWWDouble(key))
        assertEquals(valueInt, map2.getLWWInt(key))
        assertEquals(valueString, map2.getLWWString(key))
    }

    /**
    * This test evaluates the scenario: put || merge putLWW get.
    * Call to get should return the value set by put registered in the second replica.
    */
    @Test
    fun LWW_put_MergePutLWWGet() {
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

        assertEquals(valBoolean2, map2.getLWWBoolean(key))
        assertEquals(valDouble2, map2.getLWWDouble(key))
        assertEquals(valInt2, map2.getLWWInt(key))
        assertEquals(valString2, map2.getLWWString(key))
    }

    /**
    * This test evaluates the scenario: put || putLWW merge get
    * Call to get should return the value set by put registered in the second replica.
    */
    @Test
    fun LWW_put_PutLWWMergeGet() {
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

        assertEquals(valBoolean2, map2.getLWWBoolean(key))
        assertEquals(valDouble2, map2.getLWWDouble(key))
        assertEquals(valInt2, map2.getLWWInt(key))
        assertEquals(valString2, map2.getLWWString(key))
    }
    
    /**
    * This test evaluates the scenario: putLWW || put merge get.
    * Call to get should return the value set by put registered in the first replica.
    */
    @Test
    fun LWW_putLWW_PutMergeGet() {
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

        assertEquals(valBoolean1, map2.getLWWBoolean(key))
        assertEquals(valDouble1, map2.getLWWDouble(key))
        assertEquals(valInt1, map2.getLWWInt(key))
        assertEquals(valString1, map2.getLWWString(key))
    }

    /**
    * This test evaluates the scenario: put delLWW || put merge get.
    * Call to get should return null.
    */
    @Test
    fun LWW_putDelLWW_PutMergeGet() {
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

        assertNull(map2.getLWWBoolean(key))
        assertNull(map2.getLWWDouble(key))
        assertNull(map2.getLWWInt(key))
        assertNull(map2.getLWWString(key))
    }

    /**
    * This test evaluates the scenario: put delLWW || put merge(before del) merge(after del) get.
    * Call to get should return null.
    */
    @Test
    fun LWW_putDelLWW_PutMergeBeforeDelMergeAfterDelGet() {
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

        assertNull(map2.getLWWBoolean(key))
        assertNull(map2.getLWWDouble(key))
        assertNull(map2.getLWWInt(key))
        assertNull(map2.getLWWString(key))
    }

    /*
    * This test evaluates the scenario: put del || putLWW merge get.
    * Call to get should return the value set by put registered in the second replica.
    */
    @Test
    fun LWW_putDel_PutLWWMergeGet() {
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

        assertEquals(valBoolean2, map2.getLWWBoolean(key))
        assertEquals(valDouble2, map2.getLWWDouble(key))
        assertEquals(valInt2, map2.getLWWInt(key))
        assertEquals(valString2, map2.getLWWString(key))
    }

    /*
    * This test evaluates the scenario: put del || putLWW merge(before del) merge(after del) get.
    * Call to get should return the value set by put registered in the second replica.
    */
    @Test
    fun LWW_putDel_PutLWWMergeBeforeDelMergeAfterDelGet() {
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

        assertEquals(valBoolean2, map2.getLWWBoolean(key))
        assertEquals(valDouble2, map2.getLWWDouble(key))
        assertEquals(valInt2, map2.getLWWInt(key))
        assertEquals(valString2, map2.getLWWString(key))
    }

    /*
    * This test evaluates the scenario: put || put || merge1 delLWW merge2 get.
    * Call to get should return null.
    */
    @Test
    fun LWW_put_Put_Merge1DelLWWMerge2Get() {
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

        assertNull(map3.getLWWBoolean(key))
        assertNull(map3.getLWWDouble(key))
        assertNull(map3.getLWWInt(key))
        assertNull(map3.getLWWString(key))
    }

    /*
    * This test evaluates the scenario: putLWW || put || merge1 del merge2 get.
    * Call to get should return the value set by put registered in the second replica.
    */
    @Test
    fun LWW_put_PutLWW_Merge1DelMerge2Get() {
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
        val map1 = Map()
        val map2 = Map()
        val map3 = Map()

        map1.putLWW(key, valBoolean1, ts1)
        map1.putLWW(key, valDouble1, ts4)
        map1.putLWW(key, valInt1, ts8)
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

        assertEquals(valBoolean2, map3.getLWWBoolean(key))
        assertEquals(valDouble2, map3.getLWWDouble(key))
        assertEquals(valInt2, map3.getLWWInt(key))
        assertEquals(valString2, map3.getLWWString(key))
    }

    /*
    * This test evaluates the use of deltas return by call to put method.
    * Call to get should return the value set by put registered in the first replica.
    */
    @Test
    fun LWW_putOp() {
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

        assertEquals(valueBoolean, map1.getLWWBoolean(key))
        assertEquals(valueDouble, map1.getLWWDouble(key))
        assertEquals(valueInt, map1.getLWWInt(key))
        assertEquals(valueString, map1.getLWWString(key))
        assertEquals(valueBoolean, map2.getLWWBoolean(key))
        assertEquals(valueDouble, map2.getLWWDouble(key))
        assertEquals(valueInt, map2.getLWWInt(key))
        assertEquals(valueString, map2.getLWWString(key))
    }

    /*
    * This test evaluates the use of deltas return by call to put and delete methods.
    * Call to get should return null.
    */
    @Test
    fun LWW_putDelOp() {
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

        assertNull(map1.getLWWBoolean(key))
        assertNull(map1.getLWWDouble(key))
        assertNull(map1.getLWWInt(key))
        assertNull(map1.getLWWString(key))
        assertNull(map2.getLWWBoolean(key))
        assertNull(map2.getLWWDouble(key))
        assertNull(map2.getLWWInt(key))
        assertNull(map2.getLWWString(key))
    }

    /*
    * This test evaluates the merge of deltas return by call to put method.
    * Call to get should return the values set by puts registered in the first replica.
    */
    @Test
    fun LWW_putOpFusion() {
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

        assertEquals(valBoolean2, map2.getLWWBoolean(key1))
        assertEquals(valDouble2, map2.getLWWDouble(key1))
        assertEquals(valInt2, map2.getLWWInt(key1))
        assertEquals(valString2, map2.getLWWString(key1))
        assertEquals(valBoolean1, map2.getLWWBoolean(key2))
        assertEquals(valDouble1, map2.getLWWDouble(key2))
        assertEquals(valInt1, map2.getLWWInt(key2))
        assertEquals(valString1, map2.getLWWString(key2))
    }

    /*
    * This test evaluates the merge of deltas return by call to put and delete methods.
    * Call to get should return the value set by put or null if it has been deleted.
    */
    @Test
    fun LWW_putDelOpFusion() {
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

        assertNull(map2.getLWWBoolean(key1))
        assertNull(map2.getLWWDouble(key1))
        assertNull(map2.getLWWInt(key1))
        assertNull(map2.getLWWString(key1))
        assertEquals(valueBoolean, map2.getLWWBoolean(key2))
        assertEquals(valueDouble, map2.getLWWDouble(key2))
        assertEquals(valueInt, map2.getLWWInt(key2))
        assertEquals(valueString, map2.getLWWString(key2))
    }

    /*
    * This test evaluates the generation of delta (only put) plus its merging into another replica.
    * Call to get should return the values set by puts registered in the first replica after w.r.t
    * the given context.
    */
    @Test
    fun LWW_generateDelta() {
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

        assertNull(map2.getLWWBoolean(key1))
        assertNull(map2.getLWWDouble(key1))
        assertNull(map2.getLWWInt(key1))
        assertNull(map2.getLWWString(key1))
        assertNull(map2.getLWWBoolean(key2))
        assertNull(map2.getLWWDouble(key2))
        assertNull(map2.getLWWInt(key2))
        assertNull(map2.getLWWString(key2))
        assertEquals(valueBoolean, map2.getLWWBoolean(key3))
        assertEquals(valueDouble, map2.getLWWDouble(key3))
        assertEquals(valueInt, map2.getLWWInt(key3))
        assertEquals(valueString, map2.getLWWString(key3))
        assertEquals(valueBoolean, map2.getLWWBoolean(key4))
        assertEquals(valueDouble, map2.getLWWDouble(key4))
        assertEquals(valueInt, map2.getLWWInt(key4))
        assertEquals(valueString, map2.getLWWString(key4))
    }

    /*
    * This test evaluates the generation of delta (including delete) plus its merging into another replica.
    * Call to get should return the values set by puts or null set by delete w.r.t the given context.
    */
    @Test
    fun LWW_generateDeltaWithDel() {
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

        assertNull(map2.getLWWBoolean(key1))
        assertNull(map2.getLWWDouble(key1))
        assertNull(map2.getLWWInt(key1))
        assertNull(map2.getLWWString(key1))
        assertNull(map2.getLWWBoolean(key2))
        assertNull(map2.getLWWDouble(key2))
        assertNull(map2.getLWWInt(key2))
        assertNull(map2.getLWWString(key2))
        assertEquals(valueBoolean, map2.getLWWBoolean(key3))
        assertEquals(valueDouble, map2.getLWWDouble(key3))
        assertEquals(valueInt, map2.getLWWInt(key3))
        assertEquals(valueString, map2.getLWWString(key3))
    }
    
    /**
    * This test evaluates the scenario: put get.
    * Call to get should return the value set by the put.
    */
    @Test
    fun MV_putGet() {
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
        val map = Map()

        map.putMV(key, valueBoolean, ts1)
        map.putMV(key, valueDouble, ts2)
        map.putMV(key, valueInt, ts3)
        map.putMV(key, valueString, ts4)

        assertEquals(setOf(valueBoolean), map.getMVBoolean(key))
        assertEquals(setOf(valueDouble), map.getMVDouble(key))
        assertEquals(setOf(valueInt), map.getMVInt(key))
        assertEquals(setOf(valueString), map.getMVString(key))
    }
    
    /**
    * This test evaluates the scenario: put del get.
    * Call to get should return null.
    */
    @Test
    fun MV_putDelGet() {
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
        val map = Map()

        map.putMV(key, valueBoolean, ts1)
        map.putMV(key, valueDouble, ts2)
        map.putMV(key, valueInt, ts3)
        map.putMV(key, valueString, ts4)
        map.deleteMVBoolean(key, ts5)
        map.deleteMVDouble(key, ts6)
        map.deleteMVInt(key, ts7)
        map.deleteMVString(key, ts8)

        assertNull(map.getMVBoolean(key))
        assertNull(map.getMVDouble(key))
        assertNull(map.getMVInt(key))
        assertNull(map.getMVString(key))
    }
 
    /**
    * This test evaluates the scenario: del get.
    * Call to get should return null.
    */
    @Test
    fun MV_delGet() {
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
        val map = Map()

        map.deleteMVBoolean(key, ts1)
        map.deleteMVDouble(key, ts2)
        map.deleteMVInt(key, ts3)
        map.deleteMVString(key, ts4)

        assertNull(map.getMVBoolean(key))
        assertNull(map.getMVDouble(key))
        assertNull(map.getMVInt(key))
        assertNull(map.getMVString(key))
    }

    /**
    * This test evaluates the scenario: put put get.
    * Call to get should return the value set by the second put.
    */
    @Test
    fun MV_putPutGet() {
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
        val map = Map()

        map.putMV(key, valBoolean1, ts1)
        map.putMV(key, valDouble1, ts2)
        map.putMV(key, valInt1, ts3)
        map.putMV(key, valString1, ts4)
        map.putMV(key, valBoolean2, ts5)
        map.putMV(key, valDouble2, ts6)
        map.putMV(key, valInt2, ts7)
        map.putMV(key, valString2, ts8)

        assertEquals(setOf(valBoolean2), map.getMVBoolean(key))
        assertEquals(setOf(valDouble2), map.getMVDouble(key))
        assertEquals(setOf(valInt2), map.getMVInt(key))
        assertEquals(setOf(valString2), map.getMVString(key))
    }

    /**
    * This test evaluates the scenario: put put del get.
    * Call to get should return null.
    */
    @Test
    fun MV_putPutDelGet() {
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

        assertNull(map.getMVBoolean(key))
        assertNull(map.getMVDouble(key))
        assertNull(map.getMVInt(key))
        assertNull(map.getMVString(key))
    }

    /**
    * This test evaluates the scenario: put || merge get.
    * Call to get should return the value set by the put registered in the first replica.
    */
    @Test
    fun MV_put_MergeGet() {
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
        val map1 = Map()
        val map2 = Map()

        map1.putMV(key, valueBoolean, ts1)
        map1.putMV(key, valueDouble, ts2)
        map1.putMV(key, valueInt, ts3)
        map1.putMV(key, valueString, ts4)
        map1.merge(map2)
        map2.merge(map1)

        assertEquals(setOf(valueBoolean), map1.getMVBoolean(key))
        assertEquals(setOf(valueDouble), map1.getMVDouble(key))
        assertEquals(setOf(valueInt), map1.getMVInt(key))
        assertEquals(setOf(valueString), map1.getMVString(key))
        assertEquals(setOf(valueBoolean), map2.getMVBoolean(key))
        assertEquals(setOf(valueDouble), map2.getMVDouble(key))
        assertEquals(setOf(valueInt), map2.getMVInt(key))
        assertEquals(setOf(valueString), map2.getMVString(key))
    }

    /**
    * This test evaluates the scenario: put || merge put get.
    * Call to get should return the value set by put registered in the second replica.
    */
    @Test
    fun MV_put_MergePutGet() {
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

        assertEquals(setOf(valBoolean2), map2.getMVBoolean(key))
        assertEquals(setOf(valDouble2), map2.getMVDouble(key))
        assertEquals(setOf(valInt2), map2.getMVInt(key))
        assertEquals(setOf(valString2), map2.getMVString(key))
    }

    /**
    * This test evaluates the scenario: put || put merge get
    * Call to get should return a set containing the two concurently put values.
    */
    @Test
    fun MV_put_PutMergeGet() {
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

        assertEquals(setOf(valBoolean1, valBoolean2), map2.getMVBoolean(key))
        assertEquals(setOf(valDouble1, valDouble2), map2.getMVDouble(key))
        assertEquals(setOf(valInt1, valInt2), map2.getMVInt(key))
        assertEquals(setOf(valString1, valString2), map2.getMVString(key))
    }

    /**
    * This test evaluates the scenario: put del || put (with older timestamp) merge get.
    * Call to get should return a set containing the value set in the second replica.
    */
    @Test
    fun MV_putDel_PutOlderMergeGet() {
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

        assertEquals(setOf(valBoolean2, null), map2.getMVBoolean(key))
        assertEquals(setOf(valDouble2, null), map2.getMVDouble(key))
        assertEquals(setOf(valInt2, null), map2.getMVInt(key))
        assertEquals(setOf(valString2, null), map2.getMVString(key))
    }

    /**
    * This test evaluates the scenario: put del || putMV(with older timestamp) merge(before del)
    * merge(after del) get.
    * Call to get should return a set containing the value set in the second replica.
    */
    @Test
    fun MV_putDel_PutOlderMergeBeforeDelMergeAfterDelGet() {
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

        assertEquals(setOf(valBoolean2, null), map2.getMVBoolean(key))
        assertEquals(setOf(valDouble2, null), map2.getMVDouble(key))
        assertEquals(setOf(valInt2, null), map2.getMVInt(key))
        assertEquals(setOf(valString2, null), map2.getMVString(key))
    }

    /*
    * This test evaluates the scenario: put del || putMV(with newer timestamp) merge get.
    * Call to get should return the value set by put registered in the second replica.
    */
    @Test
    fun MV_putDel_PutNewerMergeGet() {
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

        assertEquals(setOf(valBoolean2, null), map2.getMVBoolean(key))
        assertEquals(setOf(valDouble2, null), map2.getMVDouble(key))
        assertEquals(setOf(valInt2, null), map2.getMVInt(key))
        assertEquals(setOf(valString2, null), map2.getMVString(key))
        println(map2.toJson())
    }

    /*
    * This test evaluates the scenario: put del || putMV(with newer timstamp) merge(before del)
    * merge(after del) get.
    * Call to get should return the value set by put registered in the second replica.
    */
    @Test
    fun MV_putDel_PutNewerMergeBeforeDelMergeAfterDelGet() {
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

        assertEquals(setOf(valBoolean2, null), map2.getMVBoolean(key))
        assertEquals(setOf(valDouble2, null), map2.getMVDouble(key))
        assertEquals(setOf(valInt2, null), map2.getMVInt(key))
        assertEquals(setOf(valString2, null), map2.getMVString(key))
    }

    /*
    * This test evaluates the scenario: put || put || merge1 del merge2 get.
    * Call to get should return the value set by put registered in the second replica.
    */
    @Test
    fun MV_put_Put_Merge1DelMerge2Get() {
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

        assertEquals(setOf(valBoolean2, null), map3.getMVBoolean(key))
        assertEquals(setOf(valDouble2, null), map3.getMVDouble(key))
        assertEquals(setOf(valInt2, null), map3.getMVInt(key))
        assertEquals(setOf(valString2, null), map3.getMVString(key))
    }

    /*
    * This test evaluates the use of deltas return by call to put method.
    * Call to get should return the value set by put registered in the first replica.
    */
    @Test
    fun MV_putOp() {
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

        assertEquals(setOf(valueBoolean), map1.getMVBoolean(key))
        assertEquals(setOf(valueDouble), map1.getMVDouble(key))
        assertEquals(setOf(valueInt), map1.getMVInt(key))
        assertEquals(setOf(valueString), map1.getMVString(key))
        assertEquals(setOf(valueBoolean), map2.getMVBoolean(key))
        assertEquals(setOf(valueDouble), map2.getMVDouble(key))
        assertEquals(setOf(valueInt), map2.getMVInt(key))
        assertEquals(setOf(valueString), map2.getMVString(key))
    }

    /*
    * This test evaluates the use of deltas return by call to put and delete methods.
    * Call to get should return null.
    */
    @Test
    fun MV_putDelOp() {
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

        assertNull(map1.getMVBoolean(key))
        assertNull(map1.getMVDouble(key))
        assertNull(map1.getMVInt(key))
        assertNull(map1.getMVString(key))
        assertNull(map2.getMVBoolean(key))
        assertNull(map2.getMVDouble(key))
        assertNull(map2.getMVInt(key))
        assertNull(map2.getMVString(key))
    }

    /*
    * This test evaluates the merge of deltas return by call to put method.
    * Call to get should return the values set by puts registered in the first replica.
    */
    @Test
    fun MV_putOpFusion() {
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

        assertEquals(setOf(valBoolean2), map2.getMVBoolean(key1))
        assertEquals(setOf(valDouble2), map2.getMVDouble(key1))
        assertEquals(setOf(valInt2), map2.getMVInt(key1))
        assertEquals(setOf(valString2), map2.getMVString(key1))
        assertEquals(setOf(valBoolean1), map2.getMVBoolean(key2))
        assertEquals(setOf(valDouble1), map2.getMVDouble(key2))
        assertEquals(setOf(valInt1), map2.getMVInt(key2))
        assertEquals(setOf(valString1), map2.getMVString(key2))
    }

    /*
    * This test evaluates the merge of deltas return by call to put and delete methods.
    * Call to get should return the value set by put or null if it has been deleted.
    */
    @Test
    fun MV_putDelOpFusion() {
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

        assertNull(map2.getMVBoolean(key1))
        assertNull(map2.getMVDouble(key1))
        assertNull(map2.getMVInt(key1))
        assertNull(map2.getMVString(key1))
        assertEquals(setOf(valueBoolean), map2.getMVBoolean(key2))
        assertEquals(setOf(valueDouble), map2.getMVDouble(key2))
        assertEquals(setOf(valueInt), map2.getMVInt(key2))
        assertEquals(setOf(valueString), map2.getMVString(key2))
    }

    /*
    * This test evaluates the generation of delta (only put) plus its merging into another replica.
    * Call to get should return the values set by puts registered in the first replica after w.r.t
    * the given context.
    */
    @Test
    fun MV_generateDelta() {
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

        assertNull(map2.getMVBoolean(key1))
        assertNull(map2.getMVDouble(key1))
        assertNull(map2.getMVInt(key1))
        assertNull(map2.getMVString(key1))
        assertNull(map2.getMVBoolean(key2))
        assertNull(map2.getMVDouble(key2))
        assertNull(map2.getMVInt(key2))
        assertNull(map2.getMVString(key2))
        assertEquals(setOf(valueBoolean), map2.getMVBoolean(key3))
        assertEquals(setOf(valueDouble), map2.getMVDouble(key3))
        assertEquals(setOf(valueInt), map2.getMVInt(key3))
        assertEquals(setOf(valueString), map2.getMVString(key3))
        assertEquals(setOf(valueBoolean), map2.getMVBoolean(key4))
        assertEquals(setOf(valueDouble), map2.getMVDouble(key4))
        assertEquals(setOf(valueInt), map2.getMVInt(key4))
        assertEquals(setOf(valueString), map2.getMVString(key4))
    }

    /*
    * This test evaluates the generation of delta (including delete) plus its merging into another replica.
    * Call to get should return the values set by puts or null set by delete w.r.t the given context.
    */
    @Test
    fun MV_generateDeltaWithDel() {
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

        assertNull(map2.getMVBoolean(key1))
        assertNull(map2.getMVDouble(key1))
        assertNull(map2.getMVInt(key1))
        assertNull(map2.getMVString(key1))
        assertNull(map2.getMVBoolean(key2))
        assertNull(map2.getMVDouble(key2))
        assertNull(map2.getMVInt(key2))
        assertNull(map2.getMVString(key2))
        assertEquals(setOf(valueBoolean), map2.getMVBoolean(key3))
        assertEquals(setOf(valueDouble), map2.getMVDouble(key3))
        assertEquals(setOf(valueInt), map2.getMVInt(key3))
        assertEquals(setOf(valueString), map2.getMVString(key3))
    }
    
    /**
    * This test evaluates the scenario: increment get.
    * Call to get should return the value set by increment.
    */
    @Test
    fun cnt_increment() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val inc = 10
        val key = "key"
        val map = Map()

        map.increment(key, inc, ts)

        assertEquals(inc, map.getCntInt(key))
    }


    /**
    * This test evaluates the scenario: decrement get.
    * Call to get should return the inverse of value set by decrement.
    */
    @Test
    fun cnt_decrement() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val dec = 10
        val key = "key"
        val map = Map()

        map.decrement(key, dec, ts)

        assertEquals(-dec, map.getCntInt(key))
    }

    /**
    * This test evaluates the scenario: increment(with a negative value) get.
    * Call to get should return the value set by increment.
    */
    @Test
    fun cnt_incrementNegativeAmount() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val inc = -10
        val key = "key"
        val map = Map()

        map.increment(key, inc, ts)

        assertEquals(inc, map.getCntInt(key))
    }

    /**
    * This test evaluates the scenario: decrement(with a negative value) get.
    * Call to get should return the inverse of value set by decrement.
    */
    @Test
    fun cnt_decrementNegativeAmount() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val dec = -10
        val key = "key"
        val map = Map()

        map.decrement(key, dec, ts)

        assertEquals(-dec, map.getCntInt(key))
    }

    /**
    * This test evaluates the scenario: incremement(multiple times) get.
    * Call to get should return the sum of values set by calls to increment.
    */
    @Test
    fun cnt_multiIncrement() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        val inc1 = 10
        val inc2 = 1
        val inc3 = 100
        val key = "key"
        val map = Map()

        map.increment(key, inc1, ts1)
        map.increment(key, inc2, ts2)
        map.increment(key, inc3, ts3)

        assertEquals(111, map.getCntInt(key))
    }

    /**
    * This test evaluates the scenario: decremement(multiple times) get.
    * Call to get should return the inverse of the sum of values set by calls to decrement.
    */
    @Test
    fun cnt_multiDecrement() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        val dec1 = 10
        val dec2 = 1
        val dec3 = 100
        val key = "key"
        val map = Map()

        map.decrement(key, dec1, ts1)
        map.decrement(key, dec2, ts2)
        map.decrement(key, dec3, ts3)

        assertEquals(-111, map.getCntInt(key))
    }

    /**
    * This test evaluates the scenario: multiple increment and decrement get.
    * Call to get should return the sum of increments minus the sum of decrements.
    */
    @Test
    fun cnt_incrementDecrementPositive() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
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

        assertEquals(47, map.getCntInt(key))
    }

    /**
    * This test evaluates the scenario: multiple increment and decrement get.
    * Call to get should return the sum of increments minus the sum of decrements.
    */
    @Test
    fun cnt_incrementDecrementNegative() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
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

        assertEquals(-14, map.getCntInt(key))
    }

    /**
    * This test evaluates the scenario: increment || merge get.
    * Call to get should return value set by increment in the first replica.
    */
    @Test
    fun cnt_increment_MergeValue() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val inc = 11 
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        map1.increment(key, inc, ts)
        map2.merge(map1)
        map1.merge(map2)

        assertEquals(11, map1.getCntInt(key))
        assertEquals(11, map2.getCntInt(key))
    }

    /**
    * This test evaluates the scenario: decrement || merge get.
    * Call to get should return the inverse value set by decrement in the first replica.
    */
    @Test
    fun cnt_decrement_MergeValue() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val dec = 11 
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        map1.decrement(key, dec, ts)
        map2.merge(map1)
        map1.merge(map2)

        assertEquals(-11, map1.getCntInt(key))
        assertEquals(-11, map2.getCntInt(key))
    }

    /**
    * This test evaluates the scenario: increment || increment merge get.
    * Call to get should return sum of the two increment values.
    */
    @Test
    fun cnt_increment_incrementMergeValue() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val inc1 = 10 
        val inc2 = 1 
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        map1.increment(key, inc1, ts1)
        map2.increment(key, inc2, ts2)
        map2.merge(map1)

        assertEquals(11, map2.getCntInt(key))
    }

    /**
    * This test evaluates the scenario: increment || merge increment get.
    * Call to get should return sum of the two increment values.
    */
    @Test
    fun cnt_increment_mergeIncrementValue() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val inc1 = 10 
        val inc2 = 1 
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        map1.increment(key, inc1, ts1)
        map2.merge(map1)
        map2.increment(key, inc2, ts2)

        assertEquals(11, map2.getCntInt(key))
    }

    /**
    * This test evaluates the scenario: decrement || decrement merge get.
    * Call to get should return the inverse of the sum of the two decrement values.
    */
    @Test
    fun cnt_decrement_decrementMergeValue() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val dec1 = 10 
        val dec2 = 1 
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        map1.decrement(key, dec1, ts1)
        map2.decrement(key, dec2, ts2)
        map2.merge(map1)

        assertEquals(-11, map2.getCntInt(key))
    }

    /**
    * This test evaluates the scenario: decrement || merge decrement get.
    * Call to get should return the inverse of the sum of the two decrement values.
    */
    @Test
    fun cnt_decrement_mergeDecrementValue() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        val dec1 = 10 
        val dec2 = 1 
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        map1.decrement(key, dec1, ts1)
        map2.merge(map1)
        map2.decrement(key, dec2, ts2)

        assertEquals(-11, map2.getCntInt(key))
    }

    /**
    * This test evaluates the scenario: some operations || some operations merge get.
    * Call to get should return the sum of increment values minus the sum of the decrement values.
    */
    @Test
    fun cnt_multipleOperations_multipleOperationMergeValue() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts3 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts4 = dc2.getNewTimestamp()
        dc1.updateStateTS(ts3)
        val ts5 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts4)
        val ts6 = dc2.getNewTimestamp()
        dc1.updateStateTS(ts5)
        val ts7 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts6)
        val ts8 = dc2.getNewTimestamp()
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

        assertEquals(60, map2.getCntInt(key))
    }

    /**
    * This test evaluates the scenario: some operations || merge some operations get.
    * Call to get should return the sum of increment values minus the sum of the decrement values.
    */
    @Test
    fun cnt_multipleOperations_mergeMultipleOperationsValue() {
        val uid1 = DCUId("dcid1")
        val uid2 = DCUId("dcid2")
        val dc1 = SimpleEnvironment(uid1)
        val dc2 = SimpleEnvironment(uid2)
        val ts1 = dc1.getNewTimestamp()
        val ts2 = dc2.getNewTimestamp()
        dc1.updateStateTS(ts1)
        val ts3 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts2)
        val ts4 = dc2.getNewTimestamp()
        dc1.updateStateTS(ts3)
        val ts5 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts4)
        val ts6 = dc2.getNewTimestamp()
        dc1.updateStateTS(ts5)
        val ts7 = dc1.getNewTimestamp()
        dc2.updateStateTS(ts6)
        val ts8 = dc2.getNewTimestamp()
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

        assertEquals(60, map2.getCntInt(key))
    }

    /**
    * This test evaluates the use of delta return by call to increment method.
    * Call to get should return the increment value set in the first replica.
    */
    @Test
    fun cnt_incrementOp() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val inc = 11
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        val incOp = map1.increment(key, inc, ts)
        map2.merge(incOp)
        map1.merge(incOp)

        assertEquals(11, map1.getCntInt(key))
        assertEquals(11, map2.getCntInt(key))
    }

    /**
    * This test evaluates the use of delta return by call to decrement method.
    * Call to get should return the inverse of the decrement value set in the first replica.
    */
    @Test
    fun cnt_decrementOp() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts = dc.getNewTimestamp()
        val dec = 11 
        val key = "key"
        val map1 = Map()
        val map2 = Map()

        val decOp = map1.decrement(key, dec, ts)
        map2.merge(decOp)
        map1.merge(decOp)

        assertEquals(-11, map1.getCntInt(key))
        assertEquals(-11, map2.getCntInt(key))
    }

    /**
    * This test evaluates the use of delta return by call to incremetn and decrement methods.
    * Call to get should return the sum of increment values minus the sum of decrement values.
    */
    @Test
    fun cnt_multipleOp() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
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

        assertEquals(11, map1.getCntInt(key))
        assertEquals(11, map2.getCntInt(key))
    }

    /*
    * This test evaluates the generation of delta plus its merging into another replica.
    * Call to get should return the values set by operations registered in the first replica after
    * w.r.t the given context (here only the decrements).
    */
    @Test
    fun cnt_generateDelta() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val vv = dc.getCurrentState()
        val ts3 = dc.getNewTimestamp()
        dc.updateStateTS(ts3)
        val ts4 = dc.getNewTimestamp()
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

        assertEquals(-30, map2.getCntInt(key))
    }

    /**
    * This test evaluates JSON serialization an empty map.
    **/
    @Test
    fun emptyToJsonSerialization() {
        val map = Map()

        val mapJson = map.toJson()

        assertEquals("""{"_type":"Map","_metadata":{"lwwMap":{"entries":{}},"mvMap":{"entries":{},"causalContext":{"entries":[]}},"cntMap":{}}}""", mapJson)
    }

    /**
    * This test evaluates JSON deserialization of an empty map.
    **/
    @Test
    fun emptyFromJsonDeserialization() {
        val uid = DCUId("dcid")
        val dc = SimpleEnvironment(uid)
        val ts1 = dc.getNewTimestamp()
        dc.updateStateTS(ts1)
        val ts2 = dc.getNewTimestamp()
        dc.updateStateTS(ts2)
        val ts3 = dc.getNewTimestamp()

        val mapJson = Map.fromJson("""{"_type":"Map","_metadata":{"lwwMap":{"entries":{}},"mvMap":{"entries":{},"causalContext":{"entries":[]}},"cntMap":{}}}""")
        mapJson.putLWW("key1", "value1", ts1)
        mapJson.putMV("key1", "value1", ts2)
        mapJson.increment("key1", 42, ts3)

        assertEquals("value1", mapJson.getLWWString("key1"))
        assertEquals(setOf("value1"), mapJson.getMVString("key1"))
        assertEquals(42, mapJson.getCntInt("key1"))
        assertNull(mapJson.getLWWString("key2"))
        assertNull(mapJson.getMVString("key2"))
        assertNull(mapJson.getCntInt("key2"))
    }

    /**
    * This test evaluates JSON serialization of a map.
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
        dc.updateStateTS(ts2)
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

        assertEquals("""{"_type":"Map","_metadata":{"lwwMap":{"entries":{"key%BOOLEAN":{"uid":{"name":"dcid"},"cnt":1},"key%DOUBLE":{"uid":{"name":"dcid"},"cnt":2},"key%INTEGER":{"uid":{"name":"dcid"},"cnt":3},"key%STRING":{"uid":{"name":"dcid"},"cnt":3}}},"mvMap":{"entries":{"key%BOOLEAN":[{"uid":{"name":"dcid"},"cnt":4}],"key%DOUBLE":[{"uid":{"name":"dcid"},"cnt":5}],"key%INTEGER":[{"uid":{"name":"dcid"},"cnt":6}],"key%STRING":[{"uid":{"name":"dcid"},"cnt":7}]},"causalContext":{"entries":[{"name":"dcid"},7]}},"cntMap":{"key":{"increment":[{"name":"dcid"},{"first":42,"second":{"uid":{"name":"dcid"},"cnt":8}}],"decrement":[{"name":"dcid"},{"first":11,"second":{"uid":{"name":"dcid"},"cnt":9}}]}}},"key%BOOLEAN%LWW":true,"key%DOUBLE%LWW":3.14,"key%INTEGER%LWW":42,"key%STRING%LWW":"value","key%BOOLEAN%MV":[true],"key%DOUBLE%MV":[3.14],"key%INTEGER%MV":[42],"key%STRING%MV":["value"],"key%CNT":31}""", mapJson)
    }

    /**
    * This test evaluates JSON deserialization of a map.
    **/
    fun fromJsonDeserialization() {
        val mapJson = Map.fromJson("""{"_type":"Map","_metadata":{"lwwMap":{"entries":{"key%BOOLEAN":{"uid":{"name":"dcid"},"cnt":1},"key%DOUBLE":{"uid":{"name":"dcid"},"cnt":2},"key%INTEGER":{"uid":{"name":"dcid"},"cnt":3},"key%STRING":{"uid":{"name":"dcid"},"cnt":3}}},"mvMap":{"entries":{"key%BOOLEAN":[{"uid":{"name":"dcid"},"cnt":4}],"key%DOUBLE":[{"uid":{"name":"dcid"},"cnt":5}],"key%INTEGER":[{"uid":{"name":"dcid"},"cnt":6}],"key%STRING":[{"uid":{"name":"dcid"},"cnt":7}]},"causalContext":{"entries":[{"name":"dcid"},7]}},"cntMap":{"key":{"increment":[{"name":"dcid"},{"first":42,"second":{"uid":{"name":"dcid"},"cnt":8}}],"decrement":[{"name":"dcid"},{"first":11,"second":{"uid":{"name":"dcid"},"cnt":9}}]}}},"key%BOOLEAN%LWW":true,"key%DOUBLE%LWW":3.14,"key%INTEGER%LWW":42,"key%STRING%LWW":"value","key%BOOLEAN%MV":[true],"key%DOUBLE%MV":[3.14],"key%INTEGER%MV":[42],"key%STRING%MV":["value"],"key%CNT":31}""")

        assertEquals(true, mapJson.getLWWBoolean("key"))
        assertEquals(3.14, mapJson.getLWWDouble("key"))
        assertEquals(42, mapJson.getLWWInt("key"))
        assertEquals("value", mapJson.getLWWString("key"))
        assertEquals(setOf(true), mapJson.getMVBoolean("key"))
        assertEquals(setOf(3.14), mapJson.getMVDouble("key"))
        assertEquals(setOf(42), mapJson.getMVInt("key"))
        assertEquals(setOf("value"), mapJson.getMVString("key"))
        assertEquals(31, mapJson.getCntInt("key"))
    }
}
