import ast.ASTNode
import ast.Assignation
import ast.AssignationNode
import ast.BinaryOperationNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode

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
                else -> return FailedResponse("Invalid Node Type").message
            }
        }
        return stringBuffer.toString()
    }

    private fun interpretMethod(ast: MethodNode): String? {
        if (ast.identifier == "println") {
            // I would need this method to check whether the node inside the binary node are assignation or just values.
            val value = interpretBinaryNode(ast.value)
            println(value)
            return value
        } else {
            return stringBuffer.append("Invalid Method").toString()
        }
    }

// this function will interpret the assignation node and assign the value to the variable
    private fun interpretAssignation(ast: Assignation) {
        when (ast) {
            is DeclarationAssignationNode -> {
                val variable = Variable(ast.declaration.identifier, ast.declaration.type)
                val value = interpretBinaryNode(ast.assignation)
                variableMap[variable] = value
            }
            is AssignationNode -> {
                val variable = Variable(ast.identifier, "")
                val value = interpretBinaryNode(ast.assignation)
                variableMap[variable] = value
            }
        }
    }

// this function will interpret the binary node and return the value of the node
//    private fun interpretBinaryNode(assignation: ASTNode): String? {
//         return when (assignation) {
//            is NumberOperatorNode ->  stringBuffer.append(assignation.value).toString()
//            is StringOperatorNode ->  stringBuffer.append(assignation.value).toString()
//            is BinaryOperationNode -> {
//                val left = interpretBinaryNode(assignation.left!!)
//                val right = interpretBinaryNode(assignation.right!!)
//                when (assignation.symbol) {
//                    "+" -> (left!!.toDouble() + right!!.toDouble()).toString()
//                    "-" -> (left!!.toDouble() - right!!.toDouble()).toString()
//                    "*" -> (left!!.toDouble() * right!!.toDouble()).toString()
//                    "/" -> (left!!.toDouble() / right!!.toDouble()).toString()
//                    else -> return stringBuffer.append( "Invalid Operation").toString()
//                }
//            }
//           else -> return stringBuffer.append( "Invalid Operation").toString()
//        }
//    }

    private fun interpretBinaryNode(assignation: ASTNode): String? {
        return when (assignation) {
            is NumberOperatorNode -> stringBuffer.append(assignation.value).toString()
            is StringOperatorNode -> {
                // For StringOperatorNode, directly append the value to the buffer
                stringBuffer.append(assignation.value).toString()
                // Return null or an empty string if you don't want to perform any operation here
            }

            is BinaryOperationNode -> {
                val left = interpretBinaryNode(assignation.left!!)
                val right = interpretBinaryNode(assignation.right!!)
                when (assignation.symbol) {
                    "+" -> {
                        // Check if either operand is a string to handle string concatenation
                        if (left.isNullOrEmpty() || right.isNullOrEmpty()) {
                            // If either operand is null or empty, return the other operand
                            left ?: right
                        } else {
                            // Concatenate the operands
                            stringBuffer.append(left).append(right).toString()
                        }
                    }
                    "-", "*", "/" -> {
                        // For these operations, ensure both operands are numbers
                        if (left.isNullOrEmpty() || right.isNullOrEmpty()) {
                            // If either operand is null or empty, return an error message
                            stringBuffer.append("Invalid Operation").toString()
                        } else {
                            // Perform the operation
                            when (assignation.symbol) {
                                "-" -> (left.toDouble() - right.toDouble()).toString()
                                "*" -> (left.toDouble() * right.toDouble()).toString()
                                "/" -> (left.toDouble() / right.toDouble()).toString()
                                else -> stringBuffer.append("Invalid Operation").toString()
                            }
                        }
                    }
                    else -> stringBuffer.append("Invalid Operation").toString()
                }
            }
            else -> stringBuffer.append("Invalid Operation").toString()
        }
    }

    private fun interpretDeclarationNode(ast: DeclarationNode){
        // declare a variable with the given type initialized as null
        variableMap[Variable(ast.identifier, ast.type)] = null
    }
}
