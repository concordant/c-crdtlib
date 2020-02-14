package crdtlib.utils

data class Timestamp(val id: DCId, val cnt: Int) {
    /* Returns -1 if this timestamp is smaller than otherTs,
     0 if it is equal and 1 if it is larger
    */
    fun compareTo(otherTs: Timestamp): Int {
        if( cnt < otherTs.cnt)
            return -1
        else if(cnt > otherTs.cnt)
            return 1
        else
            return id.compareTo(otherTs.id)
    }

    fun smallerThan(otherTs: Timestamp): Boolean {
        return compareTo(otherTs) < 0
    }
}
