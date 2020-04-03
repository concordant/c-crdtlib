package crdtlib.crdt

import crdtlib.utils.Timestamp

/**
* Data class representing information stored in an RGA node.
*/
data class RGANode(val atom: Char, val anchor: Timestamp?, val uid: Timestamp, val ts: Timestamp, val removed: Boolean)
