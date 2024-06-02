import ast.ASTNode

interface Interpreter {
    fun interpret(astList: List<ASTNode>): Pair<VariableMap, String?>
}
