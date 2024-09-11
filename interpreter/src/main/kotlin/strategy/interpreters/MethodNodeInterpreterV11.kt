package strategy.interpreters

import ast.ASTNode
import ast.MethodNode
import reader.Reader
import strategy.Interpreter
import variable.VariableMap

class MethodNodeInterpreterV11(val variableMap: VariableMap, val reader: Reader) : Interpreter {
//    private val stringBuffer = StringBuffer()
    private val printCollector = ArrayList<String>()

    override fun interpret(ast: ASTNode): String {
        require(ast is MethodNode) { "Node must be a MethodNode" }
        return interpretMethodNode(ast)
    }

    // todo: fijate si podes hacer que este interpreter devuelva la arraylist de strings
    @Throws(IllegalArgumentException::class)
    private fun interpretMethodNode(ast: MethodNode): String {
        when (ast.name) {
            "println" -> {
                val value = BinaryOperationNodeInterpreterV11(variableMap, reader).interpret(ast.value)
//                stringBuffer.append(value)
                printCollector.add(value)
                return printCollector[0]
            }
            "readInput" -> {
                val message = BinaryOperationNodeInterpreterV11(variableMap, reader).interpret(ast.value)
                printCollector.add(message)
                // Read the input from the user
                val inputValue = readInput(reader, message)
                if (inputValue != null) {
                    printCollector.add(inputValue)
                    return printCollector[1]
                } else {
                    throw IllegalArgumentException("Invalid Input")
                }
            }
            "readEnv" -> {
                val envValue = BinaryOperationNodeInterpreterV10(variableMap).interpret(ast.value)
                return System.getenv(envValue)
            }
            else -> throw IllegalArgumentException(
                "Invalid Method at column ${ast.methodNamePosition.column} row ${ast.methodNamePosition.line}",
            )
        }
//        return stringBuffer.toString()
    }

    private fun readInput(
        reader: Reader,
        message: String,
    ): String? {
        return reader.read(message)
    }
}
