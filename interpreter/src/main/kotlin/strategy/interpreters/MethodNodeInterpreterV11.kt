package strategy.interpreters

import ast.ASTNode
import ast.MethodNode
import reader.Reader
import strategy.Interpreter
import variable.Variable
import variable.VariableMap

class MethodNodeInterpreterV11(val variableMap: VariableMap, val envVariableMap: VariableMap, val reader: Reader) : Interpreter {
    private val stringBuffer = StringBuffer()

    override fun interpret(ast: ASTNode): String {
        require(ast is MethodNode) { "Node must be a MethodNode" }
        return interpretMethodNode(ast)
    }

    @Throws(IllegalArgumentException::class)
    private fun interpretMethodNode(ast: MethodNode): String {
        when (ast.name) {
            "println" -> {
                val value = BinaryOperationNodeInterpreterV11(variableMap, envVariableMap, reader).interpret(ast.value)
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
                val message = BinaryOperationNodeInterpreterV11(variableMap, envVariableMap, reader).interpret(ast.value)
                // Read the input from the user
                val inputValue = readInput(reader, message)
                if (inputValue != null) {
                    return inputValue
                } else {
                    throw IllegalArgumentException("Invalid Input")
                }
            }
            "readEnv" -> {
                val envValue = BinaryOperationNodeInterpreterV10(envVariableMap).interpret(ast.value)
                if (envVariableMap.containsKey(Variable(envValue, "String", true))) {
                    val value = envVariableMap.variableMap[Variable(envValue, "String", false)]
                    stringBuffer.append(value)
                } else {
                    throw IllegalArgumentException("Environment variable $envValue not found")
                }
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
