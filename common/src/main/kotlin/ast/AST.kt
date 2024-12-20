package ast

import token.TokenType

sealed interface ASTNode {
    fun accept(visitor: ASTVisitor)
}

sealed interface Assignation : ASTNode // Cuando el nodo representa asignaciones

sealed interface BinaryNode : ASTNode // Cuando el nodo puede representar operaciones binarias

// Nombre de variable ej: x
data class IdentifierOperatorNode(val identifier: String, val identifierPosition: Position) : BinaryNode {
    override fun accept(visitor: ASTVisitor) {
        visitor.visit(this)
    }
}

data class StringOperatorNode(val value: String, val tokenType: TokenType, val stringPosition: Position) : BinaryNode {
    override fun accept(visitor: ASTVisitor) {
        visitor.visit(this)
    }
}

data class BooleanOperatorNode(val value: Boolean, val booleanPosition: Position) : BinaryNode {
    override fun accept(visitor: ASTVisitor) {
        visitor.visit(this)
    }
}

data class NumberOperatorNode(val value: Double, val numberPosition: Position) : BinaryNode {
    override fun accept(visitor: ASTVisitor) {
        visitor.visit(this)
    }
}

// Dos operandos y el símbolo de la operación ej: 2 + 3
data class BinaryOperationNode(val symbol: String, val left: ASTNode?, val right: ASTNode?) : BinaryNode {
    override fun accept(visitor: ASTVisitor) {
        visitor.visit(this)
    }
}

// Ej:  let x:number   let y:string   const z:boolean
data class DeclarationNode(
    val identifier: String, // Nombre de la variable
    val declarationType: TokenType, // Tipo de la variable (const o let)
    val identifierPosition: Position,
    val type: String, // Tipo de la variable (string, number, boolean)
    val typePosition: Position,
) : ASTNode {
    override fun accept(visitor: ASTVisitor) {
        visitor.visit(this)
    }
}

// Ej: let x: number = 8;  let y: string = "Hello"
data class DeclarationAssignationNode(val declaration: DeclarationNode, val assignation: ASTNode) : Assignation {
    override fun accept(visitor: ASTVisitor) {
        visitor.visit(this)
    }
}

// Ej: x=5       x=2+3*4       x="Hello"    x=y    x=y*2
data class AssignationNode(val identifier: String, val identifierPosition: Position, val assignation: BinaryNode) : Assignation {
    override fun accept(visitor: ASTVisitor) {
        visitor.visit(this)
    }
}

data class MethodNode(val name: String, val value: BinaryNode, val methodNamePosition: Position) : ASTNode {
    override fun accept(visitor: ASTVisitor) {
        visitor.visit(this)
    }
}

data class IfNode(val condition: ASTNode, val trueBranch: List<ASTNode>, val elseBranch: List<ASTNode>?) : ASTNode {
    // Ej: if(x>5){print(x)} else {print("Hello")}
    override fun accept(visitor: ASTVisitor) {
        visitor.visit(this)
    }
}

data class ConditionNode(val conditionType: String, val left: ASTNode, val right: ASTNode) : BinaryNode {
    // Ej: x==5 o x!=5
    override fun accept(visitor: ASTVisitor) {
        visitor.visit(this)
    }
}
