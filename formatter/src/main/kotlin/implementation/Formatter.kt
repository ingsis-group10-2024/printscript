package implementation

import ast.ASTNode
import ast.AssignationNode
import ast.BinaryOperationNode
import ast.BooleanOperatorNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.IfNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import config.ConfigRule
import config.JsonConfigLoader

class Formatter(jsonConfigLoader: JsonConfigLoader) {
    private val config = jsonConfigLoader.loadConfig()
    private val rules: List<ConfigRule> = config.activeRules

    fun format(nodes: List<ASTNode>): String {
        val builder = StringBuilder()
        nodes.forEach { node ->
            when (node) {
                is StringOperatorNode -> builder.append("\"${node.value}\"")
                is NumberOperatorNode -> builder.append("${node.value}")
                is BinaryOperationNode -> {
                    builder.append(formatNode(node.left))
                    builder.append(" ${node.symbol} ")
                    builder.append(formatNode(node.right))
                }
                is DeclarationNode -> {
                    // Agrega espacios antes y después del ":" si la configuración lo permite
                    val colonWithSpaces = if (rules[3].enabled && rules[4].enabled) " : " else ":"
                    builder.append("let ${node.identifier}$colonWithSpaces${node.type}\n")
                }
                is DeclarationAssignationNode -> {
                    // Agrega espacios antes y después del ":" si la configuración lo permite
                    val colonWithSpaces = if (rules[3].enabled && rules[4].enabled) " : " else ":"
                    builder.append("let ${node.declaration.identifier}$colonWithSpaces${node.declaration.type} = ")
                    builder.append(formatNode(node.assignation))
                    builder.append(";")
                }
                is AssignationNode -> {
                    builder.append("${node.identifier} = ")
                    builder.append(formatNode(node.assignation))
                    builder.append(";")
                }
                is IdentifierOperatorNode -> builder.append(node.identifier)
                is MethodNode -> {
                    // Agrega un salto de línea y 0, 1 o 2 espacios antes del llamado a println si la configuración lo permite
                    builder.append("\n${" ".repeat(rules[6].value!!)}${node.identifier}(")
                    builder.append(formatNode(node.value))
                    builder.append(");")
                }

                is BooleanOperatorNode -> {
                    builder.append("${node.value}")
                }

                is IfNode -> {
                    val ifBlockIndent = "\n".repeat(rules[7].value!!)
                    builder.append("if (${formatNode(node.condition)}) {")
                    builder.append(ifBlockIndent) // Agrega el salto de línea y los espacios antes de las instrucciones dentro del if
                    builder.append(formatNode(node.trueBranch) + ";")
                    builder.append("\n}")
                    node.falseBranch?.let {
                        builder.append(" else {")
                        builder.append(ifBlockIndent) // Agrega el salto de línea y los espacios antes de las instrucciones dentro del else
                        builder.append(formatNode(it) + ";")
                        builder.append("}")
                    }
                }
            }
        }

        return builder.toString()
    }

    private fun formatNode(node: ASTNode?): String {
        return when (node) {
            is StringOperatorNode -> "\"${node.value}\""
            is NumberOperatorNode -> "${node.value}"
            is BinaryOperationNode -> {
                "${formatNode(node.left)} ${node.symbol} ${formatNode(node.right)}"
            }
            is DeclarationNode -> {
                // Agrega espacios antes y después del ":"
                "let ${node.identifier} : ${node.type}\n"
            }
            is DeclarationAssignationNode -> {
                // Agrega espacios antes y después del ":"
                "let ${node.declaration.identifier} : ${node.declaration.type} = ${formatNode(node.assignation)};\n"
            }
            is AssignationNode -> {
                "${node.identifier} = ${formatNode(node.assignation)};\n"
            }
            is IdentifierOperatorNode -> "${node.identifier}"
            is MethodNode -> {
                // Agrega un salto de línea y 0, 1 o 2 espacios antes del llamado a println
                "${"\n".repeat(rules[6].value!!)}\n${node.identifier}(${formatNode(node.value)})"
            }
            is BooleanOperatorNode -> {
                "${node.value}"
            }
            else -> ""
        }
    }
}