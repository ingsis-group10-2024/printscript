package strategy

import ast.ASTNode

interface Interpreter {
    fun interpret(node: ASTNode): Any?
}