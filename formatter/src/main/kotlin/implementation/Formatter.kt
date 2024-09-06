package implementation

import ast.ASTNode
import ast.ASTVisitor
import ast.AssignationNode
import ast.BinaryOperationNode
import ast.BooleanOperatorNode
import ast.ConditionNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.IfNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import config.FormatterRules

class Formatter(configFileName: String) : ASTVisitor {
    private val formatterRules = FormatterRules(configFileName)
    private val builder = StringBuilder()

    fun format(nodes: List<ASTNode>): String {
        nodes.forEach { it.accept(this) }
        return builder.toString()
    }

    private fun createWhitespaceString(n: Int): String {
        return " ".repeat(n)
    }

    override fun visit(node: StringOperatorNode) {
        builder.append("\"${node.value}\"")
    }

    override fun visit(node: NumberOperatorNode) {
        builder.append(node.value)
    }

    override fun visit(node: BinaryOperationNode) {
        node.left?.accept(this)
        builder.append(createWhitespaceString(formatterRules.spacesBeforeAndAfterOperators))
        builder.append(node.symbol)
        builder.append(createWhitespaceString(formatterRules.spacesBeforeAndAfterOperators))
        node.right?.accept(this)
    }

    // let x : number
    override fun visit(node: DeclarationNode) {
        builder.append("${node.declarationType}".lowercase())
        builder.append(createWhitespaceString(formatterRules.spacesBetweenTokens))
        builder.append(node.identifier)
        builder.append(createWhitespaceString(formatterRules.custom.spaceBeforeColon))
        builder.append(":")
        builder.append(createWhitespaceString(formatterRules.custom.spaceAfterColon))
        builder.append(node.type)
        builder.append(";\n")
    }

    override fun visit(node: DeclarationAssignationNode) {
        builder.append("${node.declaration.declarationType}".lowercase())
        builder.append(createWhitespaceString(formatterRules.spacesBetweenTokens))
        builder.append(node.declaration.identifier)
        builder.append(createWhitespaceString(formatterRules.custom.spaceBeforeColon))
        builder.append(":")
        builder.append(createWhitespaceString(formatterRules.custom.spaceAfterColon))
        builder.append(node.declaration.type)
        builder.append(createWhitespaceString(formatterRules.custom.spaceBeforeAndAfterAssignationOperator))
        builder.append("=")
        builder.append(createWhitespaceString(formatterRules.custom.spaceBeforeAndAfterAssignationOperator))
        node.assignation.accept(this)
        builder.append(";")
        builder.append("\n".repeat(formatterRules.newlinesAfterSemicolon))
    }

    // x = 5;
    override fun visit(node: AssignationNode) {
        builder.append(node.identifier)
        builder.append(createWhitespaceString(formatterRules.custom.spaceBeforeAndAfterAssignationOperator))
        builder.append("=")
        builder.append(createWhitespaceString(formatterRules.custom.spaceBeforeAndAfterAssignationOperator))
        node.assignation.accept(this)
        builder.append(";")
        builder.append("\n".repeat(formatterRules.newlinesAfterSemicolon))
    }

    override fun visit(node: IdentifierOperatorNode) {
        builder.append(node.identifier)
    }

    override fun visit(node: MethodNode) {
        builder.append("\n".repeat(formatterRules.custom.newlinesBeforePrintln))
        builder.append("${node.name}(")
        node.value.accept(this)
        builder.append(");")
    }

    override fun visit(node: BooleanOperatorNode) {
        builder.append(node.value)
    }

    override fun visit(node: IfNode) {
        builder.append("if (")
        node.condition.accept(this)
        builder.append(") {")
        builder.append("\n")
        node.trueBranch.forEach {
            it.accept(this)
        }
        builder.append("}")
        node.elseBranch?.let {
            builder.append(" else {")
            builder.append("\n")
            it.forEach {
                it.accept(this)
            }
            builder.append("}")
        }
    }

    override fun visit(node: ConditionNode) {
        node.left.accept(this)
        builder.append(" ${node.conditionType} ")
        node.right.accept(this)
    }
}
