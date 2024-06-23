import ast.ASTNode
import variable.VariableMap

interface Interpreter {
    fun interpret(astList: List<ASTNode>): Pair<VariableMap, String?>
}
