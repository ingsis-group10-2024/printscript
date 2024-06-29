package strategy.interpreters

import ast.ASTNode
import ast.IfNode
import strategy.Interpreter
import strategy.InterpreterManagerImplStrategy
import variable.VariableMap

class IfNodeInterpreter(val variableMap: VariableMap, val envVariableMap: VariableMap) : Interpreter {
    override fun interpret(ast: ASTNode): String {
        require(ast is IfNode) { "node should be a IfNode" }
        return interpretIf(ast)!!
    }

    private fun interpretIf(ast: IfNode): String? {
        val condition = BinaryOperationNodeInterpreter(variableMap, envVariableMap).interpret(ast.condition)
        return when (condition) {
            "true" -> {
                InterpreterManagerImplStrategy(variableMap, envVariableMap).interpret(listOf(ast.trueBranch)).second
            }

            "false" -> {
                InterpreterManagerImplStrategy(variableMap, envVariableMap).interpret(listOf(ast.elseBranch!!)).second
            }
            else -> {
                throw IllegalArgumentException("Condition must be a boolean")
            }
        }
    }
}
