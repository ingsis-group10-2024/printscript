package node

import common.token.TokenType

data class Node(
    val type: TokenType,
    var left: Node? = null,
    var right: Node? = null,
    val headValue: String,
) : Comparable<Node> {
    override fun compareTo(other: Node): Int {
        return headValue.compareTo(other.headValue)
    }
}
