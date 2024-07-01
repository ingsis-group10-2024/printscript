package strategy

import ast.ASTNode

interface interpreter {
    fun interpret(ast: List<ASTNode>) : Any?
}