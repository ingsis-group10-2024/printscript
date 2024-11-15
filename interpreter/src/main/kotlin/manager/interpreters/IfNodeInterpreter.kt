package manager.interpreters

import ast.ASTNode
import ast.IfNode
import emitter.Printer
import manager.Interpreter
import manager.InterpreterManagerImplV11
import reader.Reader
import variable.VariableMap

class IfNodeInterpreter(val variableMap: VariableMap, val reader: Reader, val outputter: Printer) : Interpreter {
    override fun interpret(ast: ASTNode): ArrayList<String> {
        require(ast is IfNode) { "node should be a IfNode" }
        return interpretIf(ast)
    }

    private fun interpretIf(ast: IfNode): ArrayList<String> {
        val condition = BinaryOperationNodeInterpreterV11(variableMap, reader, outputter).interpret(ast.condition)
        return when (condition) {
            "true" -> {
                InterpreterManagerImplV11(variableMap, reader, outputter).interpret(ast.trueBranch).second
            }

            "false" -> {
                InterpreterManagerImplV11(variableMap, reader, outputter).interpret(ast.elseBranch!!).second
            }
            else -> {
                throw IllegalArgumentException("Condition must be a boolean")
            }
        }
    }
}
