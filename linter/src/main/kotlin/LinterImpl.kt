import ast.*

class LinterImpl() : Linter {
    private val errors = mutableListOf<String>()

    override fun lint(astNodes: List<ASTNode>): List<String> {
        checkSyntax(astNodes)
        // checkStyle(astNodes)

        return errors
    }

    private fun checkSyntax(astNodes: List<ASTNode>) {
        astNodes.forEach { node ->
            when (node) {
                is DeclarationNode -> {
                    if (node.identifier.isEmpty()) {
                        errors.add("Empty identifier")
                    }
                    if (node.type.isEmpty()) {
                        errors.add("Empty type")
                    } else {
                        checkDeclarationSyntax(node)
                    }
                }
                is AssignationNode -> checkAssignationSyntax(node)
                is DeclarationAssignationNode -> TODO()
                is BinaryOperationNode -> checkBinaryOperationSyntax(node)
                is IdentifierOperatorNode -> TODO()
                is NumberOperatorNode -> TODO()
                is StringOperatorNode -> TODO()
                is MethodNode -> checkMethodSyntax(node)
            }
        }
    }

    private fun checkBinaryOperationSyntax(node: BinaryOperationNode) {
        // Check for missing left operand
        if (node.left == null) {
            errors.add("Missing left operand in binary operation.")
        }
        // Check for missing right operand
        if (node.right == null) {
            errors.add("Missing right operand in binary operation.")
        }
        // Check for missing operator
        if (node.symbol.isEmpty()) {
            errors.add("Missing operator in binary operation.")
        }
    }

    private fun checkDeclarationSyntax(node: DeclarationNode) {
        // Check if the type is valid
        if (node.type !in listOf("number", "string")) {
            errors.add("Invalid type")
        }
        // Check for the missing type declaration
        if (node.type.isEmpty()) {
            errors.add("Missing type declaration for variable '${node.identifier}'.")
        }
    }

    private fun checkMethodSyntax(node: MethodNode) {
        // Check for missing method identifier
        if (node.identifier.isEmpty()) {
            errors.add("Missing method identifier.")
        }
    }

    private fun checkAssignationSyntax(node: AssignationNode) {
        // Check for missing identifier in assignation
        if (node.identifier.isEmpty()) {
            errors.add("Missing identifier in assignation.")
        }
    }
}
