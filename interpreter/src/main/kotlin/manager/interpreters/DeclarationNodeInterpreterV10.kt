package manager.interpreters

import ast.ASTNode
import ast.DeclarationNode
import manager.Interpreter
import variable.VariableFactory
import variable.VariableMap

class DeclarationNodeInterpreterV10(val variableMap: VariableMap) : Interpreter {
    private val variableFactory = VariableFactory()

    override fun interpret(ast: ASTNode): VariableMap {
        require(ast is DeclarationNode) { "ast should be a DeclarationNode" }
        return interpretDeclaration(ast)
    }

    private fun interpretDeclaration(ast: DeclarationNode): VariableMap {
        // declare a variable with the given type initialized as null
        val newMap =
            variableMap.copy(
                variableMap =
                    variableMap.variableMap.apply {
                        put(variableFactory.createVariable(ast.identifier, ast.type, true), null)
                    },
            )
        return newMap
    }
}
