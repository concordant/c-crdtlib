package crdtlib.utils

/**
* This class represents a datacenter (DC) identifier (id).
* @property name the name associated with the DC.
**/
data class DCId(val name: String) : Comparable<DCId>  {

    /**
    * Compares this DC name to a given other datacenter name.
    * @param other the other instance of datacenter id.
    * @return the results of the comparison between the two DC name.
    **/
    override fun compareTo(other: DCId): Int {
        return this.name.compareTo(other.name)
    }
}
