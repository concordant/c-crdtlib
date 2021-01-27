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

import crdtlib.utils.Environment
import crdtlib.utils.Json
import crdtlib.utils.Name
import crdtlib.utils.Timestamp
import crdtlib.utils.VersionVector
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

/**
 * A unique identifier for RGA nodes.
 *
 * We use timestamps as uids since we assume
 * they are distinct and monotonically increasing.
 */
typealias RGAUId = Timestamp

/**
 * A RGA node.
 *
 * @property atom the atom stored within the node.
 * @property anchor the uid of the node to the left of this node,
 *     when it was inserted.
 * @property uid the unique identifier associated with the node.
 * @property ts the timestamp associated with last update of the node
 *           (insertion or removal), used to generate deltas.
 * @property removed a boolean to mark if the node is a tombstone.
 */
@Serializable
data class RGANode(val atom: String, val anchor: RGAUId?, val uid: RGAUId, val ts: Timestamp, val removed: Boolean)

/**
 * A delta-based CRDT Replicated Growable Array (RGA).
 *
 * Array nodes can be inserted and removed concurrently.
 * When inserted, a node is linked ("anchored") to its precedent node.
 * The tree thus formed is flattened to an array by depth-first search,
 * ordering siblings by decreasing insertion timestamp.
 * A node is removed by replacing it with a tombstone:
 * a special empty node with the same uid still used as an anchor
 * for previously inserted nodes.
 *
 * When merging, a remove-wins policy is applied:
 * all nodes are retained except those hidden by a tombstone.
 *
 * Its JSON serialization respects the following schema:
 * ```json
 * {
 *   "type": "RGA",
 *   "metadata": [
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
 * ```
 */
@Serializable
class RGA : DeltaCRDT, Iterable<String> {

    /**
     * An array list storing the different elements.
     */
    @Required
    val nodes: ArrayList<RGANode> = arrayListOf()

    /**
     * Default constructor.
     */
    constructor() : super()
    constructor(env: Environment) : super(env)

    constructor(nodes: List<RGANode>, env: Environment) : super(env) {
        for (node in nodes) {
            this.nodes.add(node)
        }
    }

    /**
     * Returns the real (internal) index of the node at given [index].
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
     * Inserts an [atom] into the RGA at the specified [index].
     *
     * @return the resulting delta operation.
     */
    @Name("insertAt")
    fun insertAt(index: Int, atom: String): RGA {
        val realIdx = this.toRealIndex(index - 1)
        val anchor = this.nodes.getOrNull(realIdx)?.uid // Anchor is null if left node is supposed to be at index -1
        val ts = env.tick()
        val newNode = RGANode(atom, anchor, ts, ts, false)

        this.nodes.add(realIdx + 1, newNode)

        val delta = RGA()
        delta.nodes.add(newNode)
        onWrite(delta)
        return delta
    }

    /**
     * Removes an element at the specified [index] from the RGA.
     *
     * @return the resulting delta operation.
     */
    @Name("removeAt")
    fun removeAt(index: Int): RGA {
        val realIdx = this.toRealIndex(index)
        val node = this.nodes[realIdx]
        val ts = env.tick()
        val newNode = RGANode(node.atom, node.anchor, node.uid, ts, true)

        this.nodes[realIdx] = newNode

        val delta = RGA()
        delta.nodes.add(newNode)
        onWrite(delta)
        return delta
    }

    /**
     * Returns a List containing all elements.
     */
    @Name("get")
    fun get(): List<String> {
        onRead()
        return this.nodes.filter { !it.removed }.map { it.atom }
    }

    /**
     * Returns the element at the specified [index] in the RGA.
     */
    @Name("getAt")
    fun get(index: Int): String {
        onRead()
        val realIndex = toRealIndex(index)
        return this.nodes[realIndex].atom
    }

    override fun generateDelta(vv: VersionVector): RGA {
        val delta = RGA()
        for (node in this.nodes) {
            if (!vv.contains(node.ts)) {
                delta.nodes.add(node.copy())
            }
        }
        return delta
    }

    override fun merge(delta: DeltaCRDT) {
        if (delta !is RGA) throw IllegalArgumentException("RGA unsupported merge argument")

        var lastTs: Timestamp? = null
        for (node in delta.nodes) {
            if (lastTs == null || lastTs < node.ts) {
                lastTs = node.ts
            }
            val localNode = this.nodes.find { it.uid == node.uid }
            if (localNode == null) { // First time this node is seen.
                var index = 0
                var siblings = this.nodes.filter { it.anchor == node.anchor }
                if (siblings.isNotEmpty()) {
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
                            } ?: throw IllegalArgumentException(
                                "RGA can not merge a node with unknown anchor"
                            )
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
                if (!localNode.removed) { // Remove-wins.
                    val index = this.nodes.indexOf(localNode)
                    this.nodes[index] = node.copy()
                }
            }
        }
        onMerge(delta, lastTs)
    }

    override fun toJson(): String {
        val jsonSerializer = JsonRGASerializer(serializer())
        return Json.encodeToString(jsonSerializer, this)
    }

    companion object {
        /**
         * Get the type name for serialization.
         * @return the type as a string.
         */
        @Name("getType")
        fun getType(): String {
            return "RGA"
        }

        /**
         * Deserializes a given json string in a crdt rga.
         * @param json the given json string.
         * @return the resulted crdt rga.
         */
        @Name("fromJson")
        fun fromJson(json: String, env: Environment? = null): RGA {
            val jsonSerializer = JsonRGASerializer(serializer())
            val obj = Json.decodeFromString(jsonSerializer, json)
            if (env != null) obj.env = env
            return obj
        }
    }

    /**
     * Returns an iterator over the elements of this RGA.
     */
    override fun iterator(): Iterator<String> {
        onRead()
        return this.nodes.asSequence().filter { !it.removed }.map { it.atom }.iterator()
    }
}

/**
* This class is a json transformer for RGA, it allows the separation between data and metadata.
*/
class JsonRGASerializer(serializer: KSerializer<RGA>) :
    JsonTransformingSerializer<RGA>(serializer) {

    override fun transformSerialize(element: JsonElement): JsonElement {
        val metadata = mutableListOf<JsonElement>()
        val value = mutableListOf<JsonElement>()
        for (tmpNode in element.jsonObject["nodes"]!!.jsonArray) {
            val removed = tmpNode.jsonObject["removed"]!!.jsonPrimitive.boolean
            var transformedNode = tmpNode
            if (!removed) {
                transformedNode = JsonObject(tmpNode.jsonObject.filterNot { it.key == "atom" })
                value.add(tmpNode.jsonObject["atom"] as JsonElement)
            }
            metadata.add(transformedNode)
        }
        return JsonObject(mapOf("type" to JsonPrimitive("RGA"), "metadata" to JsonArray(metadata), "value" to JsonArray(value)))
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        val value = element.jsonObject["value"]!!.jsonArray
        val nodes = mutableListOf<JsonElement>()
        var idxValue = 0
        for (tmpNode in element.jsonObject["metadata"]!!.jsonArray) {
            val removed = tmpNode.jsonObject["removed"]!!.jsonPrimitive.boolean
            var transformedNode = tmpNode
            if (!removed) {
                transformedNode = JsonObject(tmpNode.jsonObject.plus("atom" to value[idxValue]))
                idxValue++
            }
            nodes.add(transformedNode)
        }
        return JsonObject(mapOf("nodes" to JsonArray(nodes)))
    }
}
