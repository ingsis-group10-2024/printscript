import ast.ASTNode

class LinterImpl() : Linter {
    private val errors = mutableListOf<String>()

    override fun lint(astNodes: List<ASTNode>): List<String> {
        for (ast in astNodes){
//            checkSyntax(ast)
//            checkStyle(ast)
        }
        return errors
    }



}