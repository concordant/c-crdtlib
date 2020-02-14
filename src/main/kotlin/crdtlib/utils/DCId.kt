package crdtlib.utils

data class DCId(val name: String) {
    fun compareTo(id: DCId): Int {
        return name.compareTo(id.name)
    }
}
