package crdtlib.crdt

import crdtlib.utils.Timestamp

/**
* Data class representing information stored in an RGA node.
* @property atom the atom stored within the node.
* @property anchor the uid of the node at the left of this node when it has been created.
* @property uid the unique identifier (timestamp) associated with the node.
* @property ts the timestamp associated with last update of the node.
* @property removed a boolean to know if the node is a tombstone.
*/
data class RGANode(val atom: Char, val anchor: Timestamp?, val uid: Timestamp, val ts: Timestamp, val removed: Boolean)
