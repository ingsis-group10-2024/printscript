package implementation

import ast.ASTNode
import ast.ASTVisitor
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

class Formatter(jsonConfigLoader: JsonConfigLoader) : ASTVisitor {
    private val config = jsonConfigLoader.loadConfig()
    private val rules: List<ConfigRule> = config.activeRules
    private val builder = StringBuilder()

    fun format(nodes: List<ASTNode>): String {
        nodes.forEach { it.accept(this) }
        return builder.toString()
    }

    override fun visit(node: StringOperatorNode) {
        builder.append("\"${node.value}\"")
    }

    override fun visit(node: NumberOperatorNode) {
        builder.append(node.value)
    }

    override fun visit(node: BinaryOperationNode) {
        node.left?.accept(this)
        builder.append(" ${node.symbol} ")
        node.right?.accept(this)
    }

    override fun visit(node: DeclarationNode) {
        val colonWithSpaces = if (rules[3].enabled && rules[4].enabled) " : " else ":"
        builder.append("let ${node.identifier}$colonWithSpaces${node.type}\n")
    }

    override fun visit(node: DeclarationAssignationNode) {
        val colonWithSpaces = if (rules[3].enabled && rules[4].enabled) " : " else ":"
        builder.append("let ${node.declaration.identifier}$colonWithSpaces${node.declaration.type} = ")
        node.assignation.accept(this)
        builder.append(";")
    }

    override fun visit(node: AssignationNode) {
        builder.append("${node.identifier} = ")
        node.assignation.accept(this)
        builder.append(";")
    }

    override fun visit(node: IdentifierOperatorNode) {
        builder.append(node.identifier)
    }

    override fun visit(node: MethodNode) {
        builder.append("\n${" ".repeat(rules[6].value!!)}${node.name}(")
        node.value.accept(this)
        builder.append(");")
    }

    override fun visit(node: BooleanOperatorNode) {
        builder.append("${node.value}")
    }

    override fun visit(node: IfNode) {
        val ifBlockIndent = "\n".repeat(rules[7].value!!)
        builder.append("if (${formatNode(node.condition)}) {")
        builder.append(ifBlockIndent)
        builder.append(formatNode(node.trueBranch))
        builder.append("\n}")
        node.elseBranch?.let {
            builder.append(" else {")
            builder.append(ifBlockIndent)
            builder.append(formatNode(it))
            builder.append("}")
        }
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
                "let ${node.identifier} : ${node.type};"
            }
            is DeclarationAssignationNode -> {
                // Agrega espacios antes y después del ":"
                "let ${node.declaration.identifier} : ${node.declaration.type} = ${formatNode(node.assignation)};"
            }
            is AssignationNode -> {
                "${node.identifier} = ${formatNode(node.assignation)};"
            }
            is IdentifierOperatorNode -> node.identifier
            is MethodNode -> {
                // Agrega un salto de línea y 0, 1 o 2 espacios antes del llamado a println
                "${"\n".repeat(rules[6].value!!)}${node.name}(${formatNode(node.value)});"
            }
            is BooleanOperatorNode -> {
                "${node.value}"
            }
            else -> ""
        }
    }
}
