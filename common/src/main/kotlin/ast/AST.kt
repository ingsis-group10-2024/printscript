package ast

import common.token.Token
import common.token.TokenType

sealed interface ASTNode

sealed interface Assignation : ASTNode  //Cuando el nodo representa asignaciones
sealed interface BinaryNode : ASTNode //Cuando el nodo puede representar operaciones binarias

data class IdentifierOperatorNode(val identifier: String): BinaryNode // Nombre de variable ej: x
data class StringOperatorNode(val value: String): BinaryNode // String ej: "Hello"
data class NumberOperatorNode(val value: Double): BinaryNode // Nro ej: 5
data class BinaryOperationNode(val symbol: String, val left: ASTNode?, val right: ASTNode?) : BinaryNode // Dos operandos y el símbolo de la operación ej: 2 + 3
data class DeclarationNode(val identifier: String, val type: String): ASTNode // Ej:  let x : number   let y: string
data class DeclarationAssignationNode(val declaration: DeclarationNode, val assignation: BinaryNode): Assignation // Ej: let x: number = 5   let y: string = "Hello"
data class AssignationNode(val identifier: String, val assignation: BinaryNode): Assignation // Ej: x = 5       x = 2 + 3 * 4       x = "Hello"    x = y    x = y * 2
data class MethodNode(val identifier: String, val value: BinaryNode) : ASTNode // Ej: print(x)

