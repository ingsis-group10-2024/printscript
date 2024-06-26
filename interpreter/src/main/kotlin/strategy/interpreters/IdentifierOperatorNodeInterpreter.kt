package strategy.interpreters

import ast.ASTNode
import strategy.Interpreter
import variable.VariableMap

class IdentifierOperatorNodeInterpreter(val variableMap: VariableMap, val envVariableMap: VariableMap) : Interpreter {
    override fun interpret(node: ASTNode): Any? {
        require(node is ast.IdentifierOperatorNode) { "node should be a IdentifierNode" }
        return "hola"
    }
}
