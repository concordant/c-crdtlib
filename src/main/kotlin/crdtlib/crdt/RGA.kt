package crdtlib.crdt

import crdtlib.utils.Timestamp
import crdtlib.utils.UnexpectedTypeException
import crdtlib.utils.VersionVector

/**
* This class is a delta-based CRDT Replicated Growable Array (RGA).
*/
class RGA : DeltaCRDT<RGA> {

    /**
    * An array list storing the different elements.
    */
    private val nodes: ArrayList<RGANode>

    /**
    * A version vector storing the local causal context.
    */
    public val causalContext: VersionVector

    /**
    * Default constructor.
    */
    constructor() {
        this.nodes = arrayListOf<RGANode>()
        this.causalContext = VersionVector()
    }

    /**
    * Lookups for the real index of a given targeted index.
    * @param index the targeted index.
    * @return the real index.
    */
    private fun toRealIndex(index: Int): Int {
        var realIdx = -1
        var nRemoved = 0
        do {
            realIdx++
            if (realIdx == this.nodes.size) break
            if (this.nodes.get(realIdx).removed) nRemoved++
        } while (realIdx - nRemoved != index)
        return realIdx
    }

    /**
    * Inserts a given atom to a given index.
    * @param index the index where the atom should be inserted.
    * @param atom the atom that ashould be inserted.
    * @param ts the timestamp associated with the operation.
    * @return the resulting delta operation.
    */
    fun insertAt(index: Int, atom: Char, ts: Timestamp): RGA {
        val realIdx = this.toRealIndex(index)
        val anchor = this.nodes.getOrNull(realIdx - 1)?.uid // Anchor is null if left node is supposed to be at index -1
        val newNode = RGANode(atom, anchor, ts, ts, false)

        this.nodes.add(realIdx, newNode)
        this.causalContext.addTS(ts)

        val delta = RGA()
        delta.nodes.add(newNode)
        delta.causalContext.pointWiseMax(this.causalContext)
        return delta
    }

    /**
    * Removes the atom presents a a given index.
    * @param index the index where atom sould be removed.
    * @param ts the timestamp associated with the operation.
    * @return the resulting delta operation.
    */
    fun removeAt(index: Int, ts: Timestamp): RGA {
        val realIdx = this.toRealIndex(index)
        val node = this.nodes.get(realIdx)
        val newNode = RGANode(node.atom, node.anchor, node.uid, ts, true)

        this.nodes.set(realIdx, newNode)
        this.causalContext.addTS(ts)

        val delta = RGA()
        delta.nodes.add(newNode)
        delta.causalContext.pointWiseMax(this.causalContext)
        return delta
    }

    /**
    * Gets the value associated with the RGA.
    * @return a list containning the values present in the RGA.
    */
    fun value(): List<Char> {
        return this.nodes.filter { it.removed == false }.map { it.atom }
    }
    
    /**
    * Generates a delta of operations recorded and not already present in a given context.
    * @param vv the context used as starting point to generate the delta.
    * @return the corresponding delta of operations.
    */
    override fun generateDelta(vv: VersionVector): Delta<RGA> {
        val delta = RGA()
        for (node in this.nodes) {
            if (!vv.includesTS(node.ts)) {
                delta.nodes.add(node.copy())
            }
        }
        delta.causalContext.pointWiseMax(this.causalContext)
        return delta
    }
    
    /**
    * Merges informations contained in a given delta into the local replica, the merge is unilateral
    * and only local replica is modified.
    * For each node in foreign replica: if the node already exists in the local replica we update it
    * only if its timestamp is not included in local causal context; else we insert it correctly.
    * The correct place is at the right of node's anchor. If other nodes have the same anchor bigger
    * timestamp wins: meaning that the distant node will be either put at the left of the first
    * weaker (with smaller timestamp) node found, or at the end of the array if no weaker node
    * exist.
    * @param delta the delta that should be merge with the local replica.
    */
    override fun merge(delta: Delta<RGA>) {
        if (delta !is RGA) throw UnexpectedTypeException("RGA does not support merging with type:" + delta::class)

        for (node in delta.nodes) {
            if (this.causalContext.includesTS(node.ts)) continue 

            val localNode = this.nodes.find { it.uid == node.uid }
            if (localNode == null) { // First time this node is seen.
                var index = -1
                val anchorNode = this.nodes.find { it.uid == node.anchor }
                if (anchorNode != null) {
                    index = this.nodes.indexOf(anchorNode)
                }
                index++

                val sameAnchorNodes = this.nodes.filter { it.anchor == node.anchor }
                if (sameAnchorNodes.size > 0) { // There exist node with the same anchor.
                    var foundWeaker = false
                    for (tmpNode in sameAnchorNodes) {
                        if (tmpNode.ts < node.ts) { // A weaker node has been found.
                            index = this.nodes.indexOf(tmpNode)
                            foundWeaker = true
                            break
                        }
                    }
                    if (foundWeaker == false) { // No weaker node exist.
                        index = this.nodes.size
                    }
                }

                this.nodes.add(index, node.copy())
            } else { // This node already exists.
                var index = this.nodes.indexOf(localNode)
                this.nodes.set(index, node.copy())
            }
        }
        this.causalContext.pointWiseMax(delta.causalContext)
    }
}
