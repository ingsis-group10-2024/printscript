package implementation

import ast.ASTVisitor
import ast.AssignationNode
import ast.BinaryOperationNode
import ast.BooleanOperatorNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode

class SyntaxChecker : ASTVisitor {
    private val errors = mutableListOf<String>()

    override fun visit(node: IdentifierOperatorNode) {
        if (!node.identifier.matches(Regex("^[a-zA-Z_][a-zA-Z0-9_]*$|^.$")) || node.identifier[0].isDigit()) {
            errors.add("Invalid identifier at ${node.identifierPosition}")
        }
        if (node.identifier.isEmpty()) {
            errors.add("Empty identifier at ${node.identifierPosition}")
        }
    }

    override fun visit(node: StringOperatorNode) {
        if (node.value.isEmpty()) {
            errors.add("Empty string value at ${node.stringPosition}")
        }
    }

    override fun visit(node: BooleanOperatorNode) {
        // Not necessary to check anything for BooleanOperatorNode
    }

    override fun visit(node: NumberOperatorNode) {
        if (node.value.isNaN() || node.value.isInfinite()) {
            errors.add("Invalid number value at ${node.numberPosition}")
        }
    }

    override fun visit(node: BinaryOperationNode) {
        val errorPrefix = "Binary operation error:"

        if (node.left == null) {
            errors.add("$errorPrefix Missing left operand in binary operation.")
        }
        if (node.right == null) {
            errors.add("$errorPrefix Missing right operand in binary operation.")
        }
        if (node.left is NumberOperatorNode && node.right is StringOperatorNode) {
            errors.add("$errorPrefix Invalid operation between number and string.")
        }
        if (node.left is StringOperatorNode && node.right is NumberOperatorNode) {
            errors.add("$errorPrefix Invalid operation between string and number.")
        }
        if (node.symbol !in listOf("+", "-", "*", "/")) {
            if (node.symbol.isEmpty()) {
                errors.add("$errorPrefix Missing operator in binary operation.")
            } else {
                errors.add("$errorPrefix Invalid operator in binary operation.")
            }
        }
    }

    override fun visit(node: DeclarationNode) {
        if (node.identifier.isEmpty()) {
            errors.add("Empty identifier at ${node.identifierPosition}")
        }
        if (node.type.isEmpty()) {
            errors.add("Empty type at ${node.typePosition}")
        } else if (node.type !in listOf("number", "string", "boolean")) {
            errors.add("Invalid type at ${node.typePosition}")
        }
    }

    override fun visit(node: DeclarationAssignationNode) {
        if (node.declaration.type.lowercase() == "number" && node.assignation !is NumberOperatorNode) {
            errors.add("Invalid assignation for number type")
        }
        if (node.declaration.type.lowercase() == "string" && node.assignation !is StringOperatorNode) {
            errors.add("Invalid assignation for string type")
        }
        if (node.declaration.type.lowercase() == "boolean" && node.assignation !is BooleanOperatorNode) {
            errors.add("Invalid assignation for boolean type")
        }
    }

    override fun visit(node: AssignationNode) {
        if (node.identifier.isEmpty()) {
            errors.add("Missing identifier in assignation at ${node.identifierPosition}.")
        }
    }

    override fun visit(node: MethodNode) {
        if (node.name.isEmpty()) {
            errors.add("Missing method identifier at ${node.methodNamePosition}.")
        }
    }

    fun getErrors(): List<String> {
        return errors
    }
}
