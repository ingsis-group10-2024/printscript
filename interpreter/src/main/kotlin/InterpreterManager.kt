import ast.ASTNode
import variable.VariableMap

interface InterpreterManager {
    fun interpret(astList: List<ASTNode>): Pair<VariableMap, ArrayList<String>>
}
