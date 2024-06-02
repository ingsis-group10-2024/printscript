package ast

sealed interface ASTNode

sealed interface Assignation : ASTNode // Cuando el nodo representa asignaciones

sealed interface BinaryNode : ASTNode // Cuando el nodo puede representar operaciones binarias

// Nombre de variable ej: x
data class IdentifierOperatorNode(val identifier: String, val identifierPosition: Position) : BinaryNode

data class StringOperatorNode(val value: String, val stringPosition: Position) : BinaryNode // String ej: "Hello"

data class BooleanOperatorNode(val value: Boolean, val booleanPosition: Position) : BinaryNode // Boolean ej: true

data class NumberOperatorNode(val value: Double, val numberPosition: Position) : BinaryNode // Nro ej: 5

// Dos operandos y el símbolo de la operación ej: 2 + 3
data class BinaryOperationNode(val symbol: String, val left: ASTNode?, val right: ASTNode?) : BinaryNode

// Ej:  let x:number   let y:string
data class DeclarationNode(val identifier: String, val identifierPosition: Position, val type: String, val typePosition: Position) : ASTNode

// Ej: let x: number = "hello";  let y: string = "Hello"
data class DeclarationAssignationNode(val declaration: DeclarationNode, val assignation: BinaryNode) : Assignation

// Ej: x=5       x=2+3*4       x="Hello"    x=y    x=y*2
data class AssignationNode(val identifier: String, val identifierPosition: Position, val assignation: BinaryNode) : Assignation

data class MethodNode(val name: String, val value: BinaryNode, val methodNamePosition: Position) : ASTNode // Ej: print(x)

data class IfNode(val condition: BinaryNode, val trueBranch: ASTNode, val falseBranch: ASTNode?) : ASTNode // Ej: if(x>5){print(x)} else {print("Hello")}
