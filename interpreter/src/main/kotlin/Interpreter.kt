import ast.ASTNode

interface Interpreter {
    fun interpret(astList: List<ASTNode>): Any?
}
