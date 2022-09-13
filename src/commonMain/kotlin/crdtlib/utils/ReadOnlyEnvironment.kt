/*
* MIT License
*
* Copyright © 2022, Concordant and contributors.
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

package crdtlib.utils

import crdtlib.crdt.DeltaCRDT

/**
 * A simple read-only environment generating increasing monotonic timestamps
 *
 * For simplicity, it also:
 * - Provides access to the last submitted delta
 * - Automatically updates itself on merge operations to ensure
 *   a generated timestamp is always greater than any known timestamp
 *
 * @property uid the client unique identifier associated with this environment.
 */
open class ReadOnlyEnvironment(uid: ClientUId) : SimpleEnvironment(uid) {
    override fun onWrite(obj: DeltaCRDT, delta: DeltaCRDT) {
        throw RuntimeException("Object is not writable.")
    }
}
