import ast.ASTNode
import ast.Assignation
import ast.AssignationNode
import ast.BinaryOperationNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode

class InterpreterImpl(val variableMap: VariableMap) : Interpreter {
    private val stringBuffer = StringBuffer()

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
        return Pair(varMap, stringBuffer.toString())
    }

    private fun interpretMethod(ast: MethodNode) {
        when (ast.name) {
            "println" -> {
                val value = interpretBinaryNode(ast.value)
                println(value)
                stringBuffer.append(value)
            }
            else -> stringBuffer.append("Invalid Method")
        }
    }

    // this function will interpret the assignation node and assign the value to the variable
    private fun interpretAssignation(ast: Assignation): VariableMap {
        when (ast) {
            is DeclarationAssignationNode -> {
                if (variableMap.containsKey(Variable(ast.declaration.identifier, ast.declaration.type))) {
                    stringBuffer.append("Variable ${ast.declaration.identifier} already declared")
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
                } ?: stringBuffer.append("Variable ${ast.identifier} not declared")
            }
        }
        return variableMap
    }

    // this function will interpret the binary node and return the value of the node
    private fun interpretBinaryNode(ast: ASTNode): String {
        return when (ast) {
            is NumberOperatorNode -> (ast.value).toString()
            is StringOperatorNode -> ast.value
            is IdentifierOperatorNode -> {
                val variable =
                    variableMap.findKey(ast.identifier)
                        ?: throw Exception("Variable ${ast.identifier} not declared")
                return variableMap.variableMap[variable] ?: throw Exception("Variable ${ast.identifier} not initialized")
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
}
