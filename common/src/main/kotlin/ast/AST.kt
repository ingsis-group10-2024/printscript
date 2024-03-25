package ast

sealed interface Assignation  //Cuando el nodo representa asignaciones
sealed interface BinaryNode //Cuando el nodo puede representar operaciones binarias
data class BinaryOperationNode(val symbol: String, val left: BinaryNode?, val right: BinaryNode?) :
    BinaryNode //Contiene dos operandos y el símbolo de la operación
data class StringOperatorNode(val value: String): BinaryNode
data class NumberOperatorNode(val value: Double): BinaryNode
data class IdentifierOperatorNode(val identifier: String): BinaryNode

/*
data class DeclarationNode(val identifier: String, val type: String): Node
data class DeclarationAssignationNode(val declaration: DeclarationNode, val assignation: BinaryNode): Assignation
data class SimpleAssignationNode(val identifier: String, val assignation: BinaryNode): Assignation
data class MethodNode(val identifier: String, val value: BinaryNode) : Node
*/



