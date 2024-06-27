package strategy.interpreters

import ast.ASTNode
import ast.MethodNode
import emitter.Printer
import reader.ConsoleInputReader
import reader.Reader
import strategy.Interpreter
import variable.Variable
import variable.VariableMap

class MethodNodeInterpreter(val variableMap: VariableMap, val envVariableMap: VariableMap) : Interpreter {
    private val stringBuffer = StringBuffer()
    private val printer = Printer()

    override fun interpret(ast: ASTNode): String? {
        require(ast is MethodNode) { "Node must be a MethodNode" }
        return interpretMethodNode(ast)
    }

    private fun interpretMethodNode(ast: MethodNode): String? {
        when (ast.name) {
            "println" -> {
                val value = BinaryOperationNodeInterpreter(variableMap, envVariableMap).interpret(ast.value)
                printer.print(value)
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
                val message = BinaryOperationNodeInterpreter(variableMap, envVariableMap).interpret(ast.value)
                // Read the input from the user
                val reader = ConsoleInputReader()
                val inputValue = readInput(reader, message)
                if (inputValue != null) {
                    return inputValue
                } else {
                    stringBuffer.append("Invalid Input")
                }
            }
            "readEnv" -> {
                val envValue = BinaryOperationNodeInterpreter(variableMap, envVariableMap).interpret(ast.value)
                if (envVariableMap.variableMap.containsKey(Variable(envValue, ""))) {
                    val value = envVariableMap.variableMap[Variable(envValue, "")]
                    stringBuffer.append(value)
                } else {
                    stringBuffer.append("Environment variable $envValue not found")
                }
            }
            else -> stringBuffer.append("Invalid Method")
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
