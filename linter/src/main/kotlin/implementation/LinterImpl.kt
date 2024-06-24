package implementation

import ast.ASTNode
import interfacePackage.Linter

class LinterImpl : Linter {
    override fun lint(astNodes: List<ASTNode>): List<String> {
        val syntaxChecker = SyntaxChecker()
        astNodes.forEach { node ->
            node.accept(syntaxChecker)
        }
        return syntaxChecker.getErrors()
    }
}
