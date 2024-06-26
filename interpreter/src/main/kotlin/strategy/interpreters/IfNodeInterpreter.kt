package strategy.interpreters

import ast.ASTNode
import strategy.Interpreter
import variable.VariableMap

class IfNodeInterpreter(val variableMap: VariableMap) : Interpreter {
    override fun interpret(node: ASTNode): Boolean {
        TODO("Not yet implemented")
    }
}
