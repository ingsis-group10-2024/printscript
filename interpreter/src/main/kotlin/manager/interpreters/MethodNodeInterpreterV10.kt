package manager.interpreters

import ast.ASTNode
import ast.MethodNode
import emitter.Printer
import manager.Interpreter
import variable.VariableMap

class MethodNodeInterpreterV10(val variableMap: VariableMap, val outputter: Printer) : Interpreter {
    private val stringBuffer = StringBuffer()

    override fun interpret(ast: ASTNode): String {
        require(ast is MethodNode) { "Node must be a MethodNode" }
        return interpretMethodNode(ast)
    }

    @Throws(IllegalArgumentException::class)
    private fun interpretMethodNode(ast: MethodNode): String {
        when (ast.name) {
            "println" -> {
                val value = BinaryOperationNodeInterpreterV10(variableMap, outputter).interpret(ast.value)
                outputter.print(value)
                stringBuffer.append(value)
            }
            else -> throw IllegalArgumentException(
                "Invalid Method at column ${ast.methodNamePosition.column} row ${ast.methodNamePosition.line}",
            )
        }
        return stringBuffer.toString()
    }
}
