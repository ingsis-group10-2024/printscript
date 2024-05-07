package `interface`

import ast.ASTNode

interface Linter {
    fun lint(astNodes: List<ASTNode>): List<String>
}
