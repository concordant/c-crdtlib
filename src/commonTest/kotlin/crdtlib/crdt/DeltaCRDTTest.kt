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

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
* Represents a suite test for DeltaCRDT.
**/
class DeltaCRDTTest : StringSpec({

    /**
     * This test evaluates that fromJson is able to deserialize a PNCounter.
     */
    "fromJson with PNCounter" {
        val counter = PNCounter()
        val counterJson = counter.toJson()
        val deltaCrdt = DeltaCRDT.fromJson(counterJson)
        deltaCrdt.toJson().shouldBe(counterJson)
    }

    /**
     * This test evaluates that fromJson is able to deserialize a BCounter.
     */
    "fromJson with BCounter" {
        val counter = BCounter()
        val counterJson = counter.toJson()
        val deltaCrdt = DeltaCRDT.fromJson(counterJson)
        deltaCrdt.toJson().shouldBe(counterJson)
    }

    /**
     * This test evaluates that fromJson is able to deserialize a LWWRegister.
     */
    "fromJson with LWWRegister" {
        val register = LWWRegister()
        val registerJson = register.toJson()
        val deltaCrdt = DeltaCRDT.fromJson(registerJson)
        deltaCrdt.toJson().shouldBe(registerJson)
    }

    /**
     * This test evaluates that fromJson is able to deserialize a MVRegister.
     */
    "fromJson with MVRegister" {
        val register = MVRegister()
        val registerJson = register.toJson()
        val deltaCrdt = DeltaCRDT.fromJson(registerJson)
        deltaCrdt.toJson().shouldBe(registerJson)
    }

    /**
     * This test evaluates that fromJson is able to deserialize a Ratchet.
     */
    "fromJson with Ratchet" {
        val ratchet = Ratchet("12")
        val ratchetJson = ratchet.toJson()
        val deltaCrdt = DeltaCRDT.fromJson(ratchetJson)
        deltaCrdt.toJson().shouldBe(ratchetJson)
    }

    /**
     * This test evaluates that fromJson is able to deserialize a RGA.
     */
    "fromJson with RGA" {
        val rga = RGA()
        val rgaJson = rga.toJson()
        val deltaCrdt = DeltaCRDT.fromJson(rgaJson)
        deltaCrdt.toJson().shouldBe(rgaJson)
    }

    /**
     * This test evaluates that fromJson is able to deserialize a LWWMap.
     */
    "fromJson with LWWMap" {
        val map = LWWMap()
        val mapJson = map.toJson()
        val deltaCrdt = DeltaCRDT.fromJson(mapJson)
        deltaCrdt.toJson().shouldBe(mapJson)
    }

    /**
     * This test evaluates that fromJson is able to deserialize a MVMap.
     */
    "fromJson with MVMap" {
        val map = MVMap()
        val mapJson = map.toJson()
        val deltaCrdt = DeltaCRDT.fromJson(mapJson)
        deltaCrdt.toJson().shouldBe(mapJson)
    }

    /**
     * This test evaluates that fromJson is able to deserialize a Map.
     */
    "fromJson with Map" {
        val map = Map()
        val mapJson = map.toJson()
        val deltaCrdt = DeltaCRDT.fromJson(mapJson)
        deltaCrdt.toJson().shouldBe(mapJson)
    }

    /**
     * This test evaluates that unknown crdt type deserialization throw an
     * IllegalArgumentException.
     */
    "fromJson with unknown type fail" {
        val crdtJson = """{"type":"MyCRDT"}"""
        shouldThrow<IllegalArgumentException> {
            DeltaCRDT.fromJson(crdtJson)
        }
    }

    /**
     * This test evaluates that deserializing a json crdt with no type key
     * throw an IllegalArgumentException.
     */
    "fromJson with no type key fail" {
        // This is a PNCounter JSON string without type key
        val crdtJson = """{"metadata":{"increment":[],"decrement":[]},"value":0}"""
        shouldThrow<IllegalArgumentException> {
            DeltaCRDT.fromJson(crdtJson)
        }
    }

    /**
     * This test evaluates that deserializing a json crdt with spaces and
     * newlines works.
     */
    "fromJson with spaces works" {
        val counter = PNCounter()
        val counterJson = counter.toJson()
        // This is a PNCounter JSON string with some spaces and newlines
        val prettyCounterJson = """{
            "type" : "PNCounter",
            "metadata" : {
                "increment" : [],
                "decrement":[]
            },
            "value" : 0
        }"""
        val deltaCrdt = DeltaCRDT.fromJson(prettyCounterJson)
        deltaCrdt.toJson().shouldBe(counterJson)
    }

    /**
     * This test evaluates that deserializing a json crdt with newlines works.
     */
    "fromJson with newlines works" {
        val counter = PNCounter()
        val counterJson = counter.toJson()
        // This is a PNCounter JSON string with some spaces and newlines
        val prettyCounterJson = """{
            "type"
            :
            "PNCounter",
            "metadata" : {
                "increment" : [],
                "decrement":[]
            },
            "value" : 0
        }"""
        val deltaCrdt = DeltaCRDT.fromJson(prettyCounterJson)
        deltaCrdt.toJson().shouldBe(counterJson)
    }

    /**
     * This test evaluates that deserializing a json crdt with quotes works.
     */
    "fromJson with simple quotes works" {
        val counter = PNCounter()
        val counterJson = counter.toJson()
        // This is a PNCounter JSON string with some spaces, newlines, and
        // simple quotes
        val prettyCounterJson = """{
            'type' : 'PNCounter',
            'metadata' : {
                'increment' : [],
                'decrement':[]
            },
            'value' : 0
        }"""
        val deltaCrdt = DeltaCRDT.fromJson(prettyCounterJson)
        deltaCrdt.toJson().shouldBe(counterJson)
    }
})
