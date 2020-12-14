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
import io.kotest.matchers.types.shouldBeInstanceOf

/**
* Represents a suite test for DeltaCRDTFactory.
**/
class DeltaCRDTFactoryTest : StringSpec({

    /**
     * Create a BCounter using the factory.
     */
    "create a BCounter" {
        val counter = DeltaCRDTFactory.createDeltaCRDT("BCounter")
        counter.shouldBeInstanceOf<BCounter>()
    }

    /**
     * Create a PNCounter using the factory.
     */
    "create a PNCounter" {
        val counter = DeltaCRDTFactory.createDeltaCRDT("PNCounter")
        counter.shouldBeInstanceOf<PNCounter>()
    }

    /**
     * Create a MVRegister using the factory.
     */
    "create a MVRegister" {
        val register = DeltaCRDTFactory.createDeltaCRDT("MVRegister")
        register.shouldBeInstanceOf<MVRegister>()
    }

    /**
     * Create a LWWRegister using the factory.
     */
    "create a LWWRegister" {
        val register = DeltaCRDTFactory.createDeltaCRDT("LWWRegister")
        register.shouldBeInstanceOf<LWWRegister>()
    }

    /**
     * Create a Ratchet using the factory.
     */
    "create a Ratchet" {
        val ratchet = DeltaCRDTFactory.createDeltaCRDT("Ratchet")
        ratchet.shouldBeInstanceOf<Ratchet>()
    }

    /**
     * Create a RGA using the factory.
     */
    "create a RGA" {
        val rga = DeltaCRDTFactory.createDeltaCRDT("RGA")
        rga.shouldBeInstanceOf<RGA>()
    }

    /**
     * Create a Map using the factory.
     */
    "create a Map" {
        val map = DeltaCRDTFactory.createDeltaCRDT("Map")
        map.shouldBeInstanceOf<Map>()
    }

    /**
     * Create a LWWMap using the factory.
     */
    "create a LWWMap" {
        val map = DeltaCRDTFactory.createDeltaCRDT("LWWMap")
        map.shouldBeInstanceOf<LWWMap>()
    }

    /**
     * Create a MVMap using the factory.
     */
    "create a MVMap" {
        val map = DeltaCRDTFactory.createDeltaCRDT("MVMap")
        map.shouldBeInstanceOf<MVMap>()
    }

    /**
     * Create a delta crdt with an unknown type.
     */
    "create an unknown type crdt" {
        shouldThrow<IllegalArgumentException> {
            DeltaCRDTFactory.createDeltaCRDT("MyCRDT")
        }
    }
})
