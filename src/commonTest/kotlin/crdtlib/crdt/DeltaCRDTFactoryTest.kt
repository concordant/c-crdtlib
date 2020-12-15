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
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.types.shouldBeInstanceOf

/**
* Represents a suite test for DeltaCRDTFactory.
**/
class DeltaCRDTFactoryTest : StringSpec({

    val uid = ClientUId("clientuid")
    val client = SimpleEnvironment(uid)

    /**
     * Create a BCounter using the factory.
     */
    "create a BCounter" {
        val counter = DeltaCRDTFactory.createDeltaCRDT("BCounter")
        counter.shouldBeInstanceOf<BCounter>()
    }

    /**
     * Create a BCounter with environment using the factory.
     */
    "create a BCounter with environment" {
        val counter = DeltaCRDTFactory.createDeltaCRDT("BCounter", client)
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
     * Create a PNCounter with environment using the factory.
     */
    "create a PNCounter with environment" {
        val counter = DeltaCRDTFactory.createDeltaCRDT("PNCounter", client)
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
     * Create a MVRegister with environment using the factory.
     */
    "create a MVRegister with environment" {
        val register = DeltaCRDTFactory.createDeltaCRDT("MVRegister", client)
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
     * Create a LWWRegister with environment using the factory.
     */
    "create a LWWRegister with environment" {
        val register = DeltaCRDTFactory.createDeltaCRDT("LWWRegister", client)
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
     * Create a Ratchet with environment using the factory.
     */
    "create a Ratchet with environment" {
        val ratchet = DeltaCRDTFactory.createDeltaCRDT("Ratchet", client)
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
     * Create a RGA with environment using the factory.
     */
    "create a RGA with environment" {
        val rga = DeltaCRDTFactory.createDeltaCRDT("RGA", client)
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
     * Create a Map with environment using the factory.
     */
    "create a Map with environment" {
        val map = DeltaCRDTFactory.createDeltaCRDT("Map", client)
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
     * Create a LWWMap with environment using the factory.
     */
    "create a LWWMap with environment" {
        val map = DeltaCRDTFactory.createDeltaCRDT("LWWMap", client)
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
     * Create a MVMap with environment using the factory.
     */
    "create a MVMap with environment" {
        val map = DeltaCRDTFactory.createDeltaCRDT("MVMap", client)
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

    /**
     * Create a delta crdt with an unknown type and environment.
     */
    "create an unknown type crdt with environment" {
        shouldThrow<IllegalArgumentException> {
            DeltaCRDTFactory.createDeltaCRDT("MyCRDT", client)
        }
    }
})
