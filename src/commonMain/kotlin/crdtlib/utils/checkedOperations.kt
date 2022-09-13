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

package crdtlib.utils

/**
 * Return the sum of two integers, checking for overflow
 *
 * @param a an Int operand
 * @param b an Int operand
 * @return a+b
 * @throws ArithmeticException on overflow
 */
fun checkedSum(a: Int, b: Int): Int {
    val s: Int = a + b
    if ((a > 0 && b > 0 && s <= 0) ||
        (a < 0 && b < 0 && s >= 0)
    ) {
        throw ArithmeticException("Int overflow")
    }
    return s
}

/**
 * Add and subtract non-negative integers, checking for overflow
 *
 * Compute add[*] - sub[*], ensuring an exception is thrown
 * iff the final result overflows
 * (and not because of an intermediate overflowing result).
 *
 * @param add a Sequence of non-negative integers to add
 * @param sub a Sequence of non-negative integers to subtract
 * @return add[*] - sub[*]
 * @throws ArithmeticException on overflow
 */
fun checkedSum(add: Sequence<Int>, sub: Sequence<Int>): Int {
    var s = 0
    val P = add.iterator()
    val M = sub.iterator()
    while (true) {
        if (s <= 0) {
            if (! P.hasNext()) break
            // Different signs: can't overflow
            s += P.next()
        } else {
            if (! M.hasNext()) break
            s -= M.next()
        }
    }
    // one iterator reached its end : add the remaining values
    for (x in P) {
        s = checkedSum(s, x)
    }
    for (x in M) {
        s = checkedSum(s, -x)
    }
    return s
}
