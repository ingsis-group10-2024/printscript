package strategy.interpreters

import ast.ASTNode
import ast.ConditionNode
import reader.Reader
import strategy.Interpreter
import variable.VariableMap

class ConditionNodeInterpreter(val variableMap: VariableMap, val envVariableMap: VariableMap, val reader: Reader) : Interpreter {
    override fun interpret(ast: ASTNode): Any? {
        require(ast is ConditionNode) { "node should be a ConditionNode" }
        return interpretCondition(ast)
    }

    private fun interpretCondition(ast: ConditionNode): Boolean {
        val left = BinaryOperationNodeInterpreterV11(variableMap, envVariableMap, reader).interpret(ast.left)
        val right = BinaryOperationNodeInterpreterV11(variableMap, envVariableMap, reader).interpret(ast.right)
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
