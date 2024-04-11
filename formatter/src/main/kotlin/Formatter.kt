import ast.*

class Formatter(private val config: FormatterConfig) {
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
                    // Agrega espacios antes y después del ":" si la configuración lo permite
                    val colonWithSpaces = if (config.spaceBeforeColon && config.spaceAfterColon) " : " else ":"
                    builder.append("let ${node.identifier}$colonWithSpaces${node.type}\n")
                }
                is DeclarationAssignationNode -> {
                    // Agrega espacios antes y después del ":" si la configuración lo permite
                    val colonWithSpaces = if (config.spaceBeforeColon && config.spaceAfterColon) " : " else ":"
                    builder.append("let ${node.declaration.identifier}$colonWithSpaces${node.declaration.type} = ")
                    builder.append(formatNode(node.assignation))
                    //builder.append("\n")
                }
                is AssignationNode -> {
                    builder.append("${node.identifier} = ")
                    builder.append(formatNode(node.assignation))
                    //builder.append("\n")
                }
                is IdentifierOperatorNode -> builder.append(node.identifier)
                is MethodNode -> {
                    // Agrega un salto de línea y 0, 1 o 2 espacios antes del llamado a println si la configuración lo permite
                    builder.append("\n${" ".repeat(config.spaceBeforePrintln)}${node.identifier}(")
                    builder.append(formatNode(node.value))
                    builder.append(")")
                }
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
            is DeclarationNode -> {
                // Agrega espacios antes y después del ":"
                "let ${node.identifier} : ${node.type}\n"
            }
            is DeclarationAssignationNode -> {
                // Agrega espacios antes y después del ":"
                "let ${node.declaration.identifier} : ${node.declaration.type} = ${formatNode(node.assignation)}\n"
            }
            is AssignationNode -> {
                "${node.identifier} = ${formatNode(node.assignation)}\n"
            }
            is IdentifierOperatorNode -> node.identifier
            is MethodNode -> {
                // Agrega un salto de línea y 0, 1 o 2 espacios antes del llamado a println
                "\n${" ".repeat(config.spaceBeforePrintln)}${node.identifier}(${formatNode(node.value)})"
            }
            else -> ""
        }
    }
}