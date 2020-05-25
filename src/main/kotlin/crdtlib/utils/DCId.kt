package crdtlib.utils

import kotlinx.serialization.*

/**
* This class represents a datacenter (DC) identifier (id).
* @property name the name associated with the DC.
*/
@Serializable
data class DCId(val name: String) : Comparable<DCId> {

    /**
    * Compares this DC name to a given other datacenter name.
    * @param other the other instance of datacenter id.
    * @return the results of the comparison between the two DC name.
    */
    override fun compareTo(other: DCId): Int {
        return this.name.compareTo(other.name)
    }

    /**
    * Serializes this datacenter id to a json string.
    * @return the resulted json string.
    */
    fun toJson(): String {
        return Json.stringify(DCId.serializer(), this)
    }

    companion object {
        /**
        * Deserializes a given json string in a datacenter id object.
        * @param json the given json string.
        * @return the resulted datacenter id.
        */
        fun fromJson(json: String): DCId {
            return Json.parse(DCId.serializer(), json)
        }
    }
}
