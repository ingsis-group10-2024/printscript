package strategy.interpreters

import ast.ASTNode
import ast.MethodNode
import emitter.PrintCollector
import reader.Reader
import strategy.Interpreter
import variable.VariableMap

class MethodNodeInterpreterV11(val variableMap: VariableMap, val reader: Reader, val printCollector: PrintCollector) : Interpreter {
//    private val stringBuffer = StringBuffer()

    override fun interpret(ast: ASTNode): PrintCollector {
        require(ast is MethodNode) { "Node must be a MethodNode" }
        return interpretMethodNode(ast)
    }

    // todo: fijate si podes hacer que este interpreter devuelva la arraylist de strings hacelo inmutable.
    @Throws(IllegalArgumentException::class)
    private fun interpretMethodNode(ast: MethodNode): PrintCollector {
        when (ast.name) {
            "println" -> {
                val value = BinaryOperationNodeInterpreterV11(variableMap, reader, printCollector).interpret(ast.value)
//                stringBuffer.append(value)
                printCollector.printableList.add(value)
                return printCollector
            }
            "readInput" -> {
                val message = BinaryOperationNodeInterpreterV11(variableMap, reader, printCollector).interpret(ast.value)
                // Read the input from the user
                val inputValue = readInput(reader, message)
                printCollector.readInputList.add(message)
                if (inputValue != null) {
                    printCollector.printableList.add(inputValue)
                    return printCollector
                } else {
                    throw IllegalArgumentException("Invalid Input")
                }
            }
            "readEnv" -> {
                val envValue = BinaryOperationNodeInterpreterV10(variableMap).interpret(ast.value)
                printCollector.printableList.add(envValue)
                return printCollector
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
