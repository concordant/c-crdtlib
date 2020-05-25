package crdtlib.utils

import kotlinx.serialization.*

/**
* This class represents a timestamp.
* @property id the datacenter id.
* @property cnt the value associated to the timestamp.
*/
@Serializable
data class Timestamp(val id: DCId, val cnt: Int) : Comparable<Timestamp> {

    /**
    * Compares this timestamp to a given other timestamp.
    * First comparison is made on their values and if equal on their datacenter ids.
    * @param other the other instance of timestamp.
    * @return the results of the comparison between the two timestamp.
    */
    override fun compareTo(other: Timestamp): Int {
        if(this.cnt != other.cnt)
            return this.cnt - other.cnt
        return this.id.compareTo(other.id)
    }

    /**
    * Serializes this timestamp to a json string.
    * @return the resulted json string.
    */
    fun toJson(): String {
        return Json.stringify(Timestamp.serializer(), this)
    }

    companion object {
        /**
        * Deserializes a given json string in a timestamp object.
        * @param json the given json string.
        * @return the resulted timestamp.
        */
        fun fromJson(json: String): Timestamp {
            return Json.parse(Timestamp.serializer(), json)
        }
    }
}
