import ast.ASTNode
import ast.Assignation
import ast.AssignationNode
import ast.BinaryOperationNode
import ast.BooleanOperatorNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.IfNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import emitter.Printer
import reader.ConsoleInputReader
import reader.Reader
import variable.Variable
import variable.VariableMap

class InterpreterImpl(val variableMap: VariableMap, val envVariables: VariableMap) : Interpreter {
    private val stringBuffer = StringBuffer()
    private var nonGlobalVariables = VariableMap(HashMap())
    private val printer = Printer()

    // the Pair it returns are the variableMap and the result of the interpretation
    override fun interpret(astList: List<ASTNode>): Pair<VariableMap, String?> {
        if (astList.isEmpty()) return Pair(variableMap, null)
        var varMap = variableMap
        for (ast in astList) {
            when (ast) {
                is DeclarationNode -> {
                    varMap = interpretDeclarationNode(ast)
                }

                is Assignation -> {
                    varMap = interpretAssignation(ast)
                }

                is MethodNode -> {
                    interpretMethod(ast)
                }

                is IfNode -> {
                    interpretIfNode(ast)
                }

                is NumberOperatorNode -> {
                    stringBuffer.append(interpretBinaryNode(ast))
                }

                is StringOperatorNode -> {
                    stringBuffer.append(interpretBinaryNode(ast))
                }

                is BinaryOperationNode -> {
                    stringBuffer.append(interpretBinaryNode(ast))
                }

                else -> stringBuffer.append(FailedResponse("Invalid Node Type").message)
            }
        }
//        println(varMap.variableMap)
//        println(envVariables.variableMap)
        return Pair(varMap, stringBuffer.toString())
    }

    private fun interpretMethod(ast: MethodNode): String? {
        when (ast.name) {
            "println" -> {
                val value = interpretBinaryNode(ast.value)
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
                val message = interpretBinaryNode(ast.value)
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
                val envValue = interpretBinaryNode(ast.value)
                if (envVariables.variableMap.containsKey(Variable(envValue, ""))) {
                    val value = envVariables.variableMap[Variable(envValue, "")]
                    stringBuffer.append(value)
                } else {
                    stringBuffer.append("Environment variable.Variable $envValue not found")
                }
            }
            else -> stringBuffer.append("Invalid Method")
        }
        return null
    }

    // this function will interpret the assignation node and assign the value to the variable
    private fun interpretAssignation(ast: Assignation): VariableMap {
        when (ast) {
            is DeclarationAssignationNode -> {
                if (variableMap.containsKey(Variable(ast.declaration.identifier, ast.declaration.type))) {
                    stringBuffer.append("variable.Variable ${ast.declaration.identifier} already declared")
                    return variableMap
                }
                val variable = Variable(ast.declaration.identifier, ast.declaration.type)
                val value = interpretBinaryNode(ast.assignation)
                val newMap = variableMap.copy(variableMap = variableMap.variableMap.apply { put(variable, value) })
                return newMap
            }
            is AssignationNode -> {
                variableMap.findKey(ast.identifier)?.let {
                    val value = interpretBinaryNode(ast.assignation)
                    val newMap = variableMap.copy(variableMap = variableMap.variableMap.apply { put(it, value) })
                    stringBuffer.append("${it.identifier} = $value")

                    return newMap
                } ?: stringBuffer.append("variable.Variable ${ast.identifier} not declared")
            }
        }
        return variableMap
    }

    // this function will interpret the binary node and return the value of the node
    private fun interpretBinaryNode(ast: ASTNode): String {
        return when (ast) {
            is NumberOperatorNode -> (ast.value).toString()
            is StringOperatorNode -> ast.value
            is BooleanOperatorNode -> ast.value.toString()
            is MethodNode -> interpretMethod(ast)!!
            is IdentifierOperatorNode -> {
                val variable =
                    variableMap.findKey(ast.identifier)
                        ?: throw Exception("variable.Variable ${ast.identifier} not declared")
                return variableMap.variableMap[variable] ?: throw Exception("variable.Variable ${ast.identifier} not initialized")
            }
            is BinaryOperationNode -> {
                val left = ast.left!!
                val right = ast.right!!
                when (ast.symbol) {
                    "+" ->
                        when {
                            left is StringOperatorNode && right is StringOperatorNode -> return (left.value + right.value)

                            left is NumberOperatorNode && right is NumberOperatorNode -> return (left.value + right.value).toString()

                            left is StringOperatorNode && right is NumberOperatorNode -> return (left.value + right.value.toString())

                            left is NumberOperatorNode && right is StringOperatorNode -> return (left.value.toString() + right.value)

                            left is IdentifierOperatorNode && right is NumberOperatorNode -> {
                                val leftValue = interpretBinaryNode(left)
                                return if (valueIsNumeric(leftValue)) {
                                    (leftValue.toDouble() + right.value).toString()
                                } else {
                                    leftValue + right.value
                                }
                            }
                            left is IdentifierOperatorNode && right is StringOperatorNode -> {
                                val leftValue = interpretBinaryNode(left)
                                return leftValue + right.value
                            }
                            left is StringOperatorNode && right is IdentifierOperatorNode -> {
                                val rightValue = interpretBinaryNode(right)
                                return left.value + rightValue
                            }
                            left is NumberOperatorNode && right is IdentifierOperatorNode -> {
                                val rightValue = interpretBinaryNode(right)
                                return if (valueIsNumeric(rightValue)) {
                                    (left.value + rightValue.toDouble()).toString()
                                } else {
                                    rightValue + left.value
                                }
                            }
                            left is IdentifierOperatorNode && right is IdentifierOperatorNode -> {
                                val leftValue = interpretBinaryNode(left)
                                val rightValue = interpretBinaryNode(right)
                                return if (valueIsNumeric(leftValue) && valueIsNumeric(rightValue)) {
                                    // Both are numbers, perform addition
                                    (leftValue.toDouble() + rightValue.toDouble()).toString()
                                } else {
                                    // At least one is a string, perform concatenation
                                    leftValue + rightValue
                                }
                            }
                            left is BinaryOperationNode || right is BinaryOperationNode -> {
                                val leftValue = interpretBinaryNode(left)
                                val rightValue = interpretBinaryNode(right)
                                return if (valueIsNumeric(leftValue) && valueIsNumeric(rightValue)) {
                                    // Both are numbers, perform addition
                                    (leftValue.toDouble() + rightValue.toDouble()).toString()
                                } else {
                                    // At least one is a string, perform concatenation
                                    leftValue + rightValue
                                }
                            }

                            else -> return "invalid operation"
                        }
                    "-", "*", "/" ->
                        when {
                            left is NumberOperatorNode && right is NumberOperatorNode -> {
                                val result =
                                    when (ast.symbol) {
                                        "-" -> left.value - right.value
                                        "*" -> left.value * right.value
                                        "/" -> left.value / right.value
                                        else -> return stringBuffer.append("Invalid Operation").toString()
                                    }
                                return result.toString()
                            }
                            left is IdentifierOperatorNode && right is NumberOperatorNode -> {
                                val leftValue = interpretBinaryNode(left)
                                return if (valueIsNumeric(leftValue)) {
                                    val result =
                                        when (ast.symbol) {
                                            "-" -> leftValue.toDouble() - right.value
                                            "*" -> leftValue.toDouble() * right.value
                                            "/" -> leftValue.toDouble() / right.value
                                            else -> return stringBuffer.append("Invalid Operation").toString()
                                        }
                                    result.toString()
                                } else {
                                    stringBuffer.append("Invalid Operation").toString()
                                }
                            }
                            left is IdentifierOperatorNode && right is IdentifierOperatorNode -> {
                                val leftValue = interpretBinaryNode(left)
                                val rightValue = interpretBinaryNode(right)
                                return if (valueIsNumeric(leftValue) && valueIsNumeric(rightValue)) {
                                    val result =
                                        when (ast.symbol) {
                                            "-" -> leftValue.toDouble() - rightValue.toDouble()
                                            "*" -> leftValue.toDouble() * rightValue.toDouble()
                                            "/" -> leftValue.toDouble() / rightValue.toDouble()
                                            else -> return stringBuffer.append("Invalid Operation").toString()
                                        }
                                    result.toString()
                                } else {
                                    stringBuffer.append("Invalid Operation").toString()
                                }
                            }
                            left is BinaryOperationNode || right is BinaryOperationNode -> {
                                val leftValue = interpretBinaryNode(left)
                                val rightValue = interpretBinaryNode(right)
                                return if (valueIsNumeric(leftValue) && valueIsNumeric(rightValue)) {
                                    val result =
                                        when (ast.symbol) {
                                            "-" -> leftValue.toDouble() - rightValue.toDouble()
                                            "*" -> leftValue.toDouble() * rightValue.toDouble()
                                            "/" -> leftValue.toDouble() / rightValue.toDouble()
                                            else -> return stringBuffer.append("Invalid Operation").toString()
                                        }
                                    result.toString()
                                } else {
                                    stringBuffer.append("Invalid Operation").toString()
                                }
                            }

                            else -> return stringBuffer.append("Invalid Operation").toString()
                        }
                    else -> return "Invalid Operation"
                }
            }

            else -> {
                stringBuffer.append("Invalid Node Type").toString()
            }
        }
    }

    private fun valueIsNumeric(value: String) = value.toDoubleOrNull() != null

    private fun interpretDeclarationNode(ast: DeclarationNode): VariableMap {
        // declare a variable with the given type initialized as null
        val newMap = variableMap.copy(variableMap = variableMap.variableMap.apply { put(Variable(ast.identifier, ast.type), null) })
        return newMap
    }

    private fun readInput(
        reader: Reader,
        message: String,
    ): String? {
        return reader.read(message)
    }

    /*
    tenemos que ver esto despues por que no creo que este funcionando bien.
     */
    private fun interpretIfNode(ast: IfNode) {
        val condition = interpretBinaryNode(ast.condition)
        nonGlobalVariables = variableMap.copy()
        when (condition) {
            "true" -> {
                interpret(listOf(ast.trueBranch))
            }
            "false" -> {
                interpret(listOf(ast.falseBranch!!))
            }
            else -> {
                stringBuffer.append("Invalid Condition")
            }
        }
    }
}
