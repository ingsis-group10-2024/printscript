package strategy.interpreters

import ast.ASTNode
import ast.IdentifierOperatorNode
import strategy.Interpreter
import variable.VariableMap

class IdentifierOperatorNodeInterpreter(val variableMap: VariableMap) : Interpreter {
    override fun interpret(ast: ASTNode): Any {
        require(ast is IdentifierOperatorNode) { "node should be a IdentifierNode" }
        return interpretIdentifier(ast)
    }

    private fun interpretIdentifier(ast: IdentifierOperatorNode): Any {
        val variable =
            variableMap.findKey(ast.identifier)
                ?: throw IllegalArgumentException("variable.variable.Variable ${ast.identifier} not declared")
        return variableMap.variableMap[variable] ?: throw IllegalArgumentException("variable.variable.Variable ${ast.identifier} not initialized")
    }
}
