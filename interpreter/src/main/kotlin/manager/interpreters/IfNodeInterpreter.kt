package manager.interpreters

import ast.ASTNode
import ast.IfNode
import reader.Reader
import manager.Interpreter
import manager.InterpreterManagerImplV11
import variable.VariableMap

class IfNodeInterpreter(val variableMap: VariableMap, val reader: Reader) : Interpreter {
    override fun interpret(ast: ASTNode): ArrayList<String> {
        require(ast is IfNode) { "node should be a IfNode" }
        return interpretIf(ast)
    }

    private fun interpretIf(ast: IfNode): ArrayList<String> {
        val condition = BinaryOperationNodeInterpreterV11(variableMap, reader).interpret(ast.condition)
        return when (condition) {
            "true" -> {
                InterpreterManagerImplV11(variableMap, reader).interpret(ast.trueBranch).second
            }

            "false" -> {
                InterpreterManagerImplV11(variableMap, reader).interpret(ast.elseBranch!!).second
            }
            else -> {
                throw IllegalArgumentException("Condition must be a boolean")
            }
        }
    }
}
