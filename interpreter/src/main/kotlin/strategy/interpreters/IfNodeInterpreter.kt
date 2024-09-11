package strategy.interpreters

import ast.ASTNode
import ast.IfNode
import emitter.PrintCollector
import reader.Reader
import strategy.Interpreter
import strategy.InterpreterManagerImplStrategyV11
import variable.VariableMap

class IfNodeInterpreter(val variableMap: VariableMap, val reader: Reader, val printCollector: PrintCollector) : Interpreter {
    override fun interpret(ast: ASTNode): ArrayList<String> {
        require(ast is IfNode) { "node should be a IfNode" }
        return interpretIf(ast)
    }

    private fun interpretIf(ast: IfNode): ArrayList<String> {
        val condition = BinaryOperationNodeInterpreterV11(variableMap, reader, printCollector).interpret(ast.condition)
        return when (condition) {
            "true" -> {
                InterpreterManagerImplStrategyV11(variableMap, reader, printCollector).interpret(ast.trueBranch).second
            }

            "false" -> {
                InterpreterManagerImplStrategyV11(variableMap, reader, printCollector).interpret(ast.elseBranch!!).second
            }
            else -> {
                throw IllegalArgumentException("Condition must be a boolean")
            }
        }
    }
}
