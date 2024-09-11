package strategy.interpreters

import ast.ASTNode
import ast.ConditionNode
import emitter.PrintCollector
import reader.Reader
import strategy.Interpreter
import variable.VariableMap

class ConditionNodeInterpreter(val variableMap: VariableMap, val reader: Reader, val printCollector: PrintCollector) : Interpreter {
    override fun interpret(ast: ASTNode): Any {
        require(ast is ConditionNode) { "node should be a ConditionNode" }
        return interpretCondition(ast)
    }

    private fun interpretCondition(ast: ConditionNode): Boolean {
        val left = BinaryOperationNodeInterpreterV11(variableMap, reader, printCollector).interpret(ast.left)
        val right = BinaryOperationNodeInterpreterV11(variableMap, reader, printCollector).interpret(ast.right)
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
