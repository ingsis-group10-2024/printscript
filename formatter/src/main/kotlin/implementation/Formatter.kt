package implementation

import ast.ASTNode
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
}
