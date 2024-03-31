import ast.*
class InterpreterImpl() : Interpreter {
    private val variableMap = mutableMapOf<String, String?>()
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

    }

    private fun interpretAssignation(ast: Assignation) {
        TODO("Not yet implemented")
    }

    private fun interpretDeclarationNode(ast: DeclarationNode) {
        variableMap[ast.identifier] = ast.type
    }


}