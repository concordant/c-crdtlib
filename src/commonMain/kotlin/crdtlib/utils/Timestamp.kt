package crdtlib.utils

/**
* This class represents a timestamp.
* @property id the datacenter id.
* @property cnt the value associated to the timestamp.
**/
data class Timestamp(val id: DCId, val cnt: Int) {

    /**
    * Compares this timestamp to a given other timestamp.
    * First comparison is made on their values and if equal on their datacenter ids.
    * @param other the other instance of timestamp.
    * @return the results of the comparison between the two timestamp.
    **/
    @Name("compareTo")
    operator fun compareTo(other: Timestamp): Int {
        if(this.cnt != other.cnt)
            return this.cnt - other.cnt
        return this.id.compareTo(other.id)
    }
}
