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
                ?: throw IllegalArgumentException(
                    "variable ${ast.identifier} " +
                        "not declared at column ${ast.identifierPosition.column} " +
                        "line ${ast.identifierPosition.line} ",
                )
        return variableMap.variableMap[variable]
            ?: throw IllegalArgumentException("variable ${ast.identifier} not initialized")
    }
}
