package ast

interface ASTVisitor {
    fun visit(node: IdentifierOperatorNode)

    fun visit(node: StringOperatorNode)

    fun visit(node: BooleanOperatorNode)

    fun visit(node: NumberOperatorNode)

    fun visit(node: BinaryOperationNode)

    fun visit(node: DeclarationNode)

    fun visit(node: DeclarationAssignationNode)

    fun visit(node: AssignationNode)

    fun visit(node: MethodNode)
}
