package crdtlib.utils

data class Timestamp(val id: DCId, val cnt: Int) : Comparable<Timestamp> {
    override fun compareTo(other: Timestamp): Int {
        if(this.cnt != other.cnt)
            return this.cnt - other.cnt
        return this.id.compareTo(other.id)
    }
}
