package strategy.interpreters

import ast.ASTNode
import ast.ConditionNode
import strategy.Interpreter
import variable.VariableMap

class ConditionNodeInterpreter(val variableMap: VariableMap, val envVariableMap: VariableMap) : Interpreter {
    override fun interpret(ast: ASTNode): Any? {
        require(ast is ConditionNode) { "node should be a ConditionNode" }
        return interpretCondition(ast)
    }

    private fun interpretCondition(ast: ConditionNode): Boolean {
        val left = BinaryOperationNodeInterpreter(variableMap, envVariableMap).interpret(ast.left)
        val right = BinaryOperationNodeInterpreter(variableMap, envVariableMap).interpret(ast.right)
        return when (ast.conditionType) {
            "==" -> {
                left == right
            }
            "!=" -> {
                left != right
            }
            "<" -> {
                left < right
            }
            ">" -> {
                left > right
            }

            else -> {
                throw IllegalArgumentException("Invalid operator")
            }
        }
    }
}
