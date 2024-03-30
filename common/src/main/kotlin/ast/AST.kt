package ast

sealed interface Assignation // Cuando el nodo representa asignaciones

sealed interface BinaryNode // Cuando el nodo puede representar operaciones binarias

data class IdentifierOperatorNode(val identifier: String) : BinaryNode // Nombre de variable ej: x

data class StringOperatorNode(val value: String) : BinaryNode // String ej: "Hello"

data class NumberOperatorNode(val value: Double) : BinaryNode // Nro ej: 5

// Dos operandos y el símbolo de la operación ej: 2 + 3
data class BinaryOperationNode(val symbol: String, val left: BinaryNode?, val right: BinaryNode?) : BinaryNode

data class DeclarationNode(val identifier: String, val type: String) : BinaryNode // Ej:  let x : number   let y: string

// Ej: let x: number = 5   let y: string = "Hello"
data class DeclarationAssignationNode(val declaration: DeclarationNode, val assignation: BinaryNode) : Assignation

data class AssignationNode(val identifier: String, val assignation: BinaryNode) : Assignation // Ej: x = 5       x = 2 + 3 * 4

data class MethodNode(val identifier: String, val value: BinaryNode) : BinaryNode // Ej: print(x)
