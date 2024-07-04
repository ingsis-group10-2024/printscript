package strategy.interpreters

import ast.ASTNode
import ast.DeclarationNode
import strategy.Interpreter
import token.TokenType
import variable.VariableFactory
import variable.VariableMap

class DeclarationNodeInterpreterV11(val variableMap: VariableMap) : Interpreter {
    val variableFactory = VariableFactory()

    override fun interpret(ast: ASTNode): VariableMap {
        require(ast is DeclarationNode) { "ast should be a DeclarationNode" }
        return interpretDeclaration(ast)
    }

    private fun interpretDeclaration(ast: DeclarationNode): VariableMap {
        // declare a variable with the given type initialized as null
        when (ast.declarationType) {
            TokenType.LET -> {
                val newMap =
                    variableMap.copy(
                        variableMap =
                            variableMap.variableMap.apply {
                                put(variableFactory.createVariable(ast.identifier, ast.type, true), null)
                            },
                    )
                return newMap
            }
            TokenType.CONST -> {
                val newMap =
                    variableMap.copy(
                        variableMap =
                            variableMap.variableMap.apply {
                                put(variableFactory.createVariable(ast.identifier, ast.type, false), null)
                            },
                    )
                return newMap
            }
            else -> throw IllegalArgumentException(
                "Invalid Declaration at column ${ast.identifierPosition.column} row ${ast.identifierPosition.line}",
            )
        }
    }
}
