import ast.*

class InterpreterImpl : Interpreter {
    private val variableMap = mutableMapOf<Variable, String?>()
    private val stringBuffer = StringBuffer()

    override fun interpret(astList: List<ASTNode>): String? {
        if (astList.isEmpty()) return null
        for (ast in astList) {
             when (ast) {
                is DeclarationNode -> {
                    interpretDeclarationNode(ast)
                }
                is Assignation -> {
                    interpretAssignation(ast)
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
                else -> stringBuffer.append(FailedResponse("Invalid Node Type").message)
            }
        }
        return stringBuffer.toString()
    }

    private fun interpretMethod(ast: MethodNode) {
        when (ast.identifier) {
            "println" -> {
                val value = interpretBinaryNode(ast.value)
                println(value)
                stringBuffer.append(value)
            }
            else -> stringBuffer.append("Invalid Method")
        }
    }

// this function will interpret the assignation node and assign the value to the variable
    private fun interpretAssignation(ast: Assignation) {
        when (ast) {
            is DeclarationAssignationNode -> {
                if (variableMap.containsKey(Variable(ast.declaration.identifier, ast.declaration.type))) {
                    stringBuffer.append("Variable ${ast.declaration.identifier} already declared")
                    return
                }
                val variable = Variable(ast.declaration.identifier, ast.declaration.type)
                val value = interpretBinaryNode(ast.assignation)
                variableMap[variable] = value
            }
            is AssignationNode -> {
                variableMap.keys.find { it.identifier == ast.identifier }?.let {
                    val value = interpretBinaryNode(ast.assignation)
                    variableMap[it] = value
                } ?: stringBuffer.append("Variable ${ast.identifier} not declared")
            }
        }
    }

// this function will interpret the binary node and return the value of the node
    private fun interpretBinaryNode(ast: ASTNode): String {
    return when (ast) {
        is NumberOperatorNode -> (ast.value).toString()
        is StringOperatorNode -> ast.value
        is IdentifierOperatorNode -> {
            val variable = variableMap.keys.find { it.identifier == ast.identifier }
                ?: throw Exception("Variable ${ast.identifier} not declared")
            return variableMap[variable] ?: throw Exception("Variable ${ast.identifier} not initialized")
        }
        is BinaryOperationNode -> {
            val left = ast.left!!
            val right = ast.right!!
            when(ast.symbol) {
                "+" -> when {
                    left is StringOperatorNode && right is StringOperatorNode -> return (left.value + right.value)

                    left is NumberOperatorNode && right is NumberOperatorNode -> return (left.value + right.value).toString()

                    left is StringOperatorNode && right is NumberOperatorNode -> return (left.value + right.value.toString())

                    left is NumberOperatorNode && right is StringOperatorNode -> return (left.value.toString() + right.value)


                    left is IdentifierOperatorNode && right is NumberOperatorNode -> {
                        val leftValue = interpretBinaryNode(left)
                        return leftValue + right.value
                    }
                    left is IdentifierOperatorNode && right is StringOperatorNode -> {
                        val leftValue = interpretBinaryNode(left)
                        return leftValue + right.value
                    }
                    left is StringOperatorNode && right is IdentifierOperatorNode -> {
                        val rightValue = interpretBinaryNode(right)
                        return  left.value + rightValue
                    }
                    left is NumberOperatorNode && right is IdentifierOperatorNode -> {
                        val rightValue = interpretBinaryNode(right)
                        return rightValue + left.value
                    }
                    left is IdentifierOperatorNode && right is IdentifierOperatorNode -> {
                        val leftValue = interpretBinaryNode(left)
                        val rightValue = interpretBinaryNode(right)
                        return if (leftValue.toDoubleOrNull() != null && rightValue.toDoubleOrNull() != null) {
                            // Both are numbers, perform addition
                            (leftValue.toDouble() + rightValue.toDouble()).toString()
                        } else {
                            // At least one is a string, perform concatenation
                            leftValue + rightValue
                        }
                    }

                    else -> return "invalid operation"
                }
                "-" , "*" ,"/" -> when {
                    left is NumberOperatorNode && right is NumberOperatorNode -> {
                        val result = when (ast.symbol) {
                            "-" -> left.value - right.value
                            "*" -> left.value * right.value
                            "/" -> left.value / right.value
                            else -> return stringBuffer.append("Invalid Operation").toString()
                        }
                        return stringBuffer.append(result).toString()
                    }
                    else -> return stringBuffer.append("Invalid Operation").toString()
                }
                else -> return stringBuffer.append("Invalid Operation").toString()
            }
        }

        else -> {
            stringBuffer.append("Invalid Node Type").toString()
        }
    }

}
private fun interpretIdentifierNode(node : IdentifierOperatorNode) : Any{
    variableMap.keys.find { it.identifier == node.identifier }.let {
        return variableMap[it]!!
    }
}
    private fun interpretDeclarationNode(ast: DeclarationNode){
        // declare a variable with the given type initialized as null
        variableMap[Variable(ast.identifier, ast.type)] = null
    }
}
