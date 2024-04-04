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
                    interpretBinaryNode(ast)
                }
                is StringOperatorNode -> {
                    interpretBinaryNode(ast)
                }
                else -> stringBuffer.append(FailedResponse("Invalid Node Type").message)
            }
        }
        return stringBuffer.toString()
    }

    private fun interpretMethod(ast: MethodNode) {
        if (ast.identifier == "println") {
            // I would need this method to check whether the node inside the binary node are assignation or just values.
//            val value = interpretBinaryNode(ast.value)
//            println(value)
//            return value
            stringBuffer.append(interpretBinaryNode(ast.value))
            stringBuffer.append("\n")
        } else {
            stringBuffer.append("Invalid Method")
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
        is NumberOperatorNode -> stringBuffer.append(ast.value).toString()
        is StringOperatorNode -> stringBuffer.append(ast.value).toString()
        is IdentifierOperatorNode -> {
            variableMap.keys.find { it.identifier == ast.identifier }?.let {
                stringBuffer.append(variableMap[it]).toString()
            } ?: stringBuffer.append("Variable ${ast.identifier} not declared").toString()
        }

        is BinaryOperationNode -> {
            val left = ast.left!!
            val right = ast.right!!
            when(ast.symbol) {
                "+" -> when {
                    left is StringOperatorNode && right is StringOperatorNode -> return stringBuffer.append(left.value + right.value)
                        .toString()

                    left is NumberOperatorNode && right is NumberOperatorNode -> return stringBuffer.append(left.value + right.value)
                        .toString()

                    left is StringOperatorNode && right is NumberOperatorNode -> return stringBuffer.append(left.value + right.value.toString())
                        .toString()
                    left is NumberOperatorNode && right is StringOperatorNode -> return stringBuffer.append(left.value.toString() + right.value)
                        .toString()

                    else -> return stringBuffer.append("Invalid Method").toString()
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




    private fun interpretDeclarationNode(ast: DeclarationNode){
        // declare a variable with the given type initialized as null
        variableMap[Variable(ast.identifier, ast.type)] = null
    }
}
