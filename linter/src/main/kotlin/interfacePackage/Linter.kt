package interfacePackage
import ast.ASTNode

interface Linter {
    fun lint(astNodes: List<ASTNode>): List<String>
}
