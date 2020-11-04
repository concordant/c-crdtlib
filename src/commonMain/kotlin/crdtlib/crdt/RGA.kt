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

package crdtlib.crdt

import crdtlib.utils.Json
import crdtlib.utils.Name
import crdtlib.utils.Timestamp
import crdtlib.utils.VersionVector
import kotlinx.serialization.*
import kotlinx.serialization.builtins.*
import kotlinx.serialization.json.*

/**
* Represents a unique identifier for RGA nodes. We use timestamps as uids since we assume they are
* distinct and monotonically increasing.
*/
typealias RGAUId = Timestamp

/**
* Data class representing information stored in an RGA node.
* @property atom the atom stored within the node.
* @property anchor the uid of the node to the left of this node, when it was inserted.
* @property uid the unique identifier associated with the node.
* @property ts the timestamp associated with last update of the node.
* @property removed a boolean to mark if the node is a tombstone.
*/
@Serializable
data class RGANode(val atom: String, val anchor: RGAUId?, val uid: RGAUId, val ts: Timestamp, val removed: Boolean)

/**
* This class is a delta-based CRDT Replicated Growable Array (RGA).
* It is serializable to JSON and respect the following schema:
* {
*   "_type": "RGA",
*   "_metadata": [
*       ({
*           ( "atom": $value, )? // If atom is present removed should be true.
*           "anchor": RGAUId.toJson(),
*           "uid": RGAUId.toJson(),
*           "ts": Timestamp.toJson(),
*           "removed" : ( true | false )
*       })
*   ],
*   "value": [
*       // Contains only values for which the corresponding metadata nodes have no atom field.
*       (( $value, )*( $value ))?
*   ]
* }
*/
@Serializable
class RGA : DeltaCRDT<RGA> {

    /**
     * An array list storing the different elements.
     */
    @Required
    val nodes: ArrayList<RGANode> = arrayListOf<RGANode>()

    /**
     * Default constructor.
     */
    constructor() {
    }

    constructor(nodes: List<RGANode>) {
        for (node in nodes) {
            this.nodes.add(node)
        }
    }

    /**
     * Lookups for the real index of a given targeted index.
     * @param index the targeted index.
     * @return the real index.
     */
    private fun toRealIndex(index: Int): Int {
        if (index == -1) return -1

        var realIdx = -1
        var nRemoved = 0
        do {
            realIdx++
            if (realIdx == this.nodes.size) break
            if (this.nodes[realIdx].removed) nRemoved++
        } while (realIdx - nRemoved != index)
        return realIdx
    }

    /**
     * Inserts a given atom at a given index.
     * @param index the index where the atom should be inserted.
     * @param atom the atom that ashould be inserted.
     * @param ts the timestamp associated with the operation.
     * @return the resulting delta operation.
     */
    @Name("insertAt")
    fun insertAt(index: Int, atom: String, ts: Timestamp): RGA {
        val realIdx = this.toRealIndex(index - 1)
        val anchor = this.nodes.getOrNull(realIdx)?.uid // Anchor is null if left node is supposed to be at index -1
        val newNode = RGANode(atom, anchor, ts, ts, false)

        this.nodes.add(realIdx + 1, newNode)

        val delta = RGA()
        delta.nodes.add(newNode)
        return delta
    }

    /**
     * Removes the atom presents a a given index.
     * @param index the index where atom sould be removed.
     * @param ts the timestamp associated with the operation.
     * @return the resulting delta operation.
     */
    @Name("removeAt")
    fun removeAt(index: Int, ts: Timestamp): RGA {
        val realIdx = this.toRealIndex(index)
        val node = this.nodes[realIdx]
        val newNode = RGANode(node.atom, node.anchor, node.uid, ts, true)

        this.nodes[realIdx] = newNode

        val delta = RGA()
        delta.nodes.add(newNode)
        return delta
    }

    /**
     * Gets the value associated with the RGA.
     * @return a list containning the values present in the RGA.
     */
    @Name("get")
    fun get(): List<String> {
        return this.nodes.filter { it.removed == false }.map { it.atom }
    }

    /**
     * Generates a delta of operations recorded and not already present in a given context.
     * @param vv the context used as starting point to generate the delta.
     * @return the corresponding delta of operations.
     */
    override fun generateDelta(vv: VersionVector): RGA {
        val delta = RGA()
        for (node in this.nodes) {
            if (!vv.contains(node.ts)) {
                delta.nodes.add(node.copy())
            }
        }
        return delta
    }

    /**
     * Merges information contained in a given delta into the local replica, the merge is unilateral
     * and only local replica is modified.
     * For each node in the delta: if the node already exists in the local replica, we update it by
     * applying a last-remove-wins policy; else we insert it correctly. The correct place is at the
     * right of the foreign node's anchor. If other nodes have the same anchor, higher timestamp
     * wins: meaning that the delta's node will be either put at the left of the first weaker (with
     * smaller timestamp) node found, or at the end of the array if no weaker node exists.
     * @param delta the delta that should be merge with the local replica.
     */
    override fun merge(delta: RGA) {
        for (node in delta.nodes) {
            val localNode = this.nodes.find { it.uid == node.uid }
            if (localNode == null) { // First time this node is seen.
                var index = 0
                var siblings = this.nodes.filter { it.anchor == node.anchor }
                if (siblings.size > 0) {
                    // There exist nodes with the same anchor.
                    var firstWeakerSiblingNode = siblings.find {
                        it.uid < node.uid
                    }
                    if (firstWeakerSiblingNode != null) {
                        // insert before first weaker sibling
                        index = this.nodes.indexOf(firstWeakerSiblingNode)
                    } else {
                        // no weaker sibling: find next element up the tree
                        var currNode: RGANode = node
                        while (currNode.anchor != null) {
                            val currAnchor = this.nodes.find {
                                it.uid == currNode.anchor
                            }
                            if (currAnchor == null) {
                                throw IllegalArgumentException(
                                    "RGA can not merge a node with unknown anchor"
                                )
                            }
                            currNode = currAnchor
                            siblings = this.nodes.filter {
                                it.anchor == currNode.anchor
                            }
                            firstWeakerSiblingNode = siblings.find {
                                it.uid < currNode.uid
                            }
                            if (firstWeakerSiblingNode != null) {
                                index = this.nodes.indexOf(firstWeakerSiblingNode)
                                break
                            }
                        }
                        if (currNode.anchor == null) {
                            // reached the root of the tree: append
                            index = this.nodes.size
                        }
                    }
                } else if (node.anchor != null) {
                    // fast case: no sibling, add right after anchor
                    index = this.nodes.indexOfFirst { it.uid == node.anchor } + 1
                    if (index == 0) {
                        throw IllegalArgumentException(
                            "RGA can not merge a node with unknown anchor"
                        )
                    }
                } else {
                    // no sibling, no anchor: this is the first node
                    index = 0
                }
                this.nodes.add(index, node.copy())
            } else if (node.removed) {
                // This node already exists and foreign node is a tombstone.
                if (localNode.removed == false) { // Remove-wins.
                    var index = this.nodes.indexOf(localNode)
                    this.nodes[index] = node.copy()
                }
            }
        }
    }

    /**
     * Serializes this crdt rga to a json string.
     * @return the resulted json string.
     */
    override fun toJson(): String {
        val jsonSerializer = JsonRGASerializer(RGA.serializer())
        return Json.encodeToString<RGA>(jsonSerializer, this)
    }

    companion object {
        /**
         * Deserializes a given json string in a crdt rga.
         * @param json the given json string.
         * @return the resulted crdt rga.
         */
        @Name("fromJson")
        fun fromJson(json: String): RGA {
            val jsonSerializer = JsonRGASerializer(RGA.serializer())
            return Json.decodeFromString(jsonSerializer, json)
        }
    }
}

/**
* This class is a json transformer for RGA, it allows the separation between data and metadata.
*/
class JsonRGASerializer(private val serializer: KSerializer<RGA>) :
    JsonTransformingSerializer<RGA>(serializer) {

    override fun transformSerialize(element: JsonElement): JsonElement {
        val metadata = mutableListOf<JsonElement>()
        val value = mutableListOf<JsonElement>()
        for (tmpNode in element.jsonObject["nodes"]!!.jsonArray) {
            val removed = tmpNode.jsonObject["removed"]!!.jsonPrimitive.boolean
            var transformedNode = tmpNode
            if (removed == false) {
                transformedNode = JsonObject(tmpNode.jsonObject.filterNot { it.key == "atom" })
                value.add(tmpNode.jsonObject["atom"] as JsonElement)
            }
            metadata.add(transformedNode)
        }
        return JsonObject(mapOf("_type" to JsonPrimitive("RGA"), "_metadata" to JsonArray(metadata), "value" to JsonArray(value)))
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        val value = element.jsonObject["value"]!!.jsonArray
        val nodes = mutableListOf<JsonElement>()
        var idxValue = 0
        for (tmpNode in element.jsonObject["_metadata"]!!.jsonArray) {
            val removed = tmpNode.jsonObject["removed"]!!.jsonPrimitive.boolean
            var transformedNode = tmpNode
            if (removed == false) {
                transformedNode = JsonObject(tmpNode.jsonObject.plus("atom" to value[idxValue]))
                idxValue++
            }
            nodes.add(transformedNode)
        }
        return JsonObject(mapOf("nodes" to JsonArray(nodes)))
    }
}
