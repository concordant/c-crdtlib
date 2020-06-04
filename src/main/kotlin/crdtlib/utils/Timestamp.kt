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
* This class represents a timestamp.
* @property id the datacenter id.
* @property cnt the value associated to the timestamp.
**/
data class Timestamp(val id: DCId, val cnt: Int) : Comparable<Timestamp> {

    /**
    * Compares this timestamp to a given other timestamp.
    * First comparison is made on their values and if equal on their datacenter ids.
    * @param other the other instance of timestamp.
    * @return the results of the comparison between the two timestamp.
    **/
    override fun compareTo(other: Timestamp): Int {
        if(this.cnt != other.cnt)
            return this.cnt - other.cnt
        return this.id.compareTo(other.id)
    }
}
