import ast.*
class InterpreterImpl() : Interpreter {
    private val variableMap = mutableMapOf<Variable, String?>()
    override fun interpret(astList: List<BinaryNode>) : Any?{
        for (ast in astList){
            when(ast){
                is DeclarationNode -> {
                    interpretDeclarationNode(ast)
                }
                is Assignation -> {
                    interpretAssignation(ast)
                }
                is MethodNode -> {
                    interpretMethod(ast)
                }
                else -> FailedResponse("Invalid Node Type")
            }
        }
        return SuccessfulResponse("AST Interpreted Successfully")
    }

    private fun interpretMethod(ast: MethodNode) {
        when(ast.identifier){
            "print" -> {
                val value = interpretBinaryNode(ast.value)
                println(value)
            }
            else -> FailedResponse("Invalid Method")
        }
    }
// this function will interpret the assignation node and assign the value to the variable
    private fun interpretAssignation(ast: Assignation) {
        when(ast){
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
    private fun interpretBinaryNode(assignation: BinaryNode): String? {
        return when(assignation){
            is NumberOperatorNode -> "${assignation.value}"
            is StringOperatorNode -> assignation.value
            is IdentifierOperatorNode -> variableMap[Variable(assignation.identifier, "")] ?: return FailedResponse("Variable not found").message
            is BinaryOperationNode -> {
                val left = interpretBinaryNode(assignation.left!!)
                val right = interpretBinaryNode(assignation.right!!)
                when(assignation.symbol){
                    "+" -> (left!!.toDouble() + right!!.toDouble()).toString()
                    "-" -> (left!!.toDouble() - right!!.toDouble()).toString()
                    "*" -> (left!!.toDouble() * right!!.toDouble()).toString()
                    "/" -> (left!!.toDouble() / right!!.toDouble()).toString()
                    else -> return FailedResponse("Invalid Operation").message
                }
            }
            else -> return FailedResponse("Invalid Node Type").message
        }
    }

    private fun interpretDeclarationNode(ast: DeclarationNode) {
        // declare a variable with the given type initialized as null
        variableMap[Variable(ast.identifier , ast.type)] = null
    }


}