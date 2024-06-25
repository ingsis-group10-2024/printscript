package ast

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

data class StringOperatorNode(val value: String, val stringPosition: Position) : BinaryNode {
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

// Ej:  let x:number   let y:string
data class DeclarationNode(val identifier: String, val identifierPosition: Position, val type: String, val typePosition: Position) : ASTNode {
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

data class IfNode(val condition: BinaryNode, val trueBranch: ASTNode, val falseBranch: ASTNode?) : ASTNode {
    override fun accept(visitor: ASTVisitor) {
        visitor.visit(this)
    }
}
