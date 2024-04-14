package ast

sealed interface ASTNode

sealed interface Assignation : ASTNode // Cuando el nodo representa asignaciones

sealed interface BinaryNode : ASTNode // Cuando el nodo puede representar operaciones binarias

data class IdentifierOperatorNode(val identifier: String) : BinaryNode // Nombre de variable ej: x  SE USA???

data class StringOperatorNode(val value: String) : BinaryNode // String ej: "Hello"

data class BooleanOperatorNode(val value: Boolean) : BinaryNode // Boolean ej: true

data class NumberOperatorNode(val value: Double) : BinaryNode // Nro ej: 5

// Dos operandos y el símbolo de la operación ej: 2 + 3
data class BinaryOperationNode(val symbol: String, val left: ASTNode?, val right: ASTNode?) : BinaryNode

data class DeclarationNode(val identifier: String, val type: String) : ASTNode // Ej:  let x:number   let y:string

// Ej: let x: number = 5   let y: string = "Hello"
data class DeclarationAssignationNode(val declaration: DeclarationNode, val assignation: BinaryNode) : Assignation

// Ej: x=5       x=2+3*4       x="Hello"    x=y    x=y*2
data class AssignationNode(val identifier: String, val assignation: BinaryNode) : Assignation

data class MethodNode(val identifier: String, val value: BinaryNode) : ASTNode // Ej: print(x)

data class IfNode(val condition: BinaryNode, val trueBranch: ASTNode, val falseBranch: ASTNode?) : ASTNode
