package strategy

import ast.ASTNode

interface Interpreter {
    fun interpret(ast: ASTNode): Any?
}
