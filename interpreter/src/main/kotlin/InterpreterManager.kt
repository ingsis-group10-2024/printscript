import ast.ASTNode

interface InterpreterManager {
    fun interpret(astList: List<ASTNode>): Any?
}
