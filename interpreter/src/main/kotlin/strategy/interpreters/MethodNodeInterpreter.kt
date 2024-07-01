package strategy.interpreters

import ast.ASTNode
import ast.MethodNode
import emitter.Printer
import strategy.Interpreter
import variable.VariableMap

class MethodNodeInterpreter(val variableMap: VariableMap) : Interpreter {
    private val stringBuffer = StringBuffer()
    private val printer = Printer()

    override fun interpret(ast: ASTNode): String {
        require(ast is MethodNode) { "Node must be a MethodNode" }
        return interpretMethodNode(ast)
    }

    private fun interpretMethodNode(ast: MethodNode): String {
        when (ast.name) {
            "println" -> {
                val value = BinaryOperationNodeInterpreter(variableMap).interpret(ast.value)
                printer.print(value)
                stringBuffer.append(value)
            }
            else -> stringBuffer.append("Invalid Method")
        }
        return stringBuffer.toString()
    }
}
