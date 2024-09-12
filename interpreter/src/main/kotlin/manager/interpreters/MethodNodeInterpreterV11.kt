package manager.interpreters

import ast.ASTNode
import ast.MethodNode
import reader.Reader
import manager.Interpreter
import variable.VariableMap

class MethodNodeInterpreterV11(val variableMap: VariableMap, val reader: Reader) : Interpreter {
    private val stringBuffer = StringBuffer()

    override fun interpret(ast: ASTNode): String {
        require(ast is MethodNode) { "Node must be a MethodNode" }
        return interpretMethodNode(ast)
    }

    @Throws(IllegalArgumentException::class)
    private fun interpretMethodNode(ast: MethodNode): String {
        when (ast.name) {
            "println" -> {
                val value = BinaryOperationNodeInterpreterV11(variableMap, reader).interpret(ast.value)
                stringBuffer.append(value)
            }
            /*
            para el println y el readinput yo tendria que tener un lector para la variable que me dan. Para que sea mas escalable.
            una interfaz lector
            const sararasa = readinpu("gjygjygj")
            let x = readinput("msg")
            let operation = "wololo" + readinput("give me a number")
            readinput tiene que recibir un lector.
             */
            "readInput" -> {
                val message = BinaryOperationNodeInterpreterV11(variableMap, reader).interpret(ast.value)
                // Read the input from the user
                val inputValue = readInput(reader, message)
                if (inputValue != null) {
                    return inputValue
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
        return stringBuffer.toString()
    }

    private fun readInput(
        reader: Reader,
        message: String,
    ): String? {
        return reader.read(message)
    }
}
