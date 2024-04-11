import ast.ASTNode
import ast.AssignationNode
import ast.BinaryOperationNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode

class Formatter {
    fun format(nodes: List<ASTNode>): String {
        val builder = StringBuilder()

        nodes.forEach { node ->
            when (node) {
                is StringOperatorNode -> builder.append("\"${node.value}\"")
                is NumberOperatorNode -> builder.append(node.value)
                is BinaryOperationNode -> {
                    builder.append(formatNode(node.left))
                    builder.append(" ${node.symbol} ")
                    builder.append(formatNode(node.right))
                }
                is DeclarationNode -> {
                    builder.append("let ${node.identifier}: ${node.type}\n")
                }
                is DeclarationAssignationNode -> {
                    builder.append("let ${node.declaration.identifier}: ${node.declaration.type} = ")
                    builder.append(formatNode(node.assignation))
                    builder.append("\n")
                }
                is AssignationNode -> {
                    builder.append("${node.identifier} = ")
                    builder.append(formatNode(node.assignation))
                    builder.append("\n")
                }
                is IdentifierOperatorNode -> TODO()
                is MethodNode -> TODO()
            }
        }
        return builder.toString()
    }

    private fun formatNode(node: ASTNode?): String {
        return when (node) {
            is StringOperatorNode -> "\"${node.value}\""
            is NumberOperatorNode -> node.value.toString()
            is BinaryOperationNode -> {
                "${formatNode(node.left)} ${node.symbol} ${formatNode(node.right)}"
            }
            is DeclarationNode, is DeclarationAssignationNode, is AssignationNode -> {
                // Estos nodos no deberían ser pasados directamente aquí, pero por simplicidad, los manejamos
                node.toString()
            }
            else -> ""
        }
    }
}
