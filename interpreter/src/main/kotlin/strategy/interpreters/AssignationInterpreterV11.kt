package strategy.interpreters

import ast.ASTNode
import ast.Assignation
import ast.AssignationNode
import ast.DeclarationAssignationNode
import reader.Reader
import strategy.Interpreter
import token.TokenType
import variable.Variable
import variable.VariableMap

class AssignationInterpreterV11(val variableMap: VariableMap, val reader: Reader) : Interpreter {
    private val stringBuffer = StringBuffer()

    override fun interpret(ast: ASTNode): Pair<VariableMap, String?> {
        require(ast is Assignation) { "ast should be a Assignation or DeclarationAssignation" }
        return Pair(interpretAssignation(ast), stringBuffer.toString())
    }

    private fun interpretAssignation(ast: Assignation): VariableMap {
        when (ast) {
            is DeclarationAssignationNode -> { // todo: checkear si es const o no.
                if (ast.declaration.declarationType == TokenType.LET) {
                    if (variableMap.containsKey(Variable(ast.declaration.identifier, ast.declaration.type, true))) {
                        return variableMap
                    }
                    val variable = Variable(ast.declaration.identifier, ast.declaration.type, true)
                    val value =
                        BinaryOperationNodeInterpreterV11(
                            variableMap,
                            reader,
                        ).interpret(ast.assignation)
                    val newMap = variableMap.copy(variableMap = variableMap.variableMap.apply { put(variable, value) })
                    return newMap
                } else {
                    val variable = Variable(ast.declaration.identifier, ast.declaration.type, false)
                    val value =
                        BinaryOperationNodeInterpreterV11(
                            variableMap,
                            reader,
                        ).interpret(ast.assignation)
                    val newMap = variableMap.copy(variableMap = variableMap.variableMap.apply { put(variable, value) })
                    return newMap
                }
            }
            is AssignationNode -> {
                variableMap.findKey(ast.identifier)?.let {
                    if (it.isMutable) {
                        val value =
                            BinaryOperationNodeInterpreterV11(
                                variableMap,
                                reader,
                            ).interpret(ast.assignation)
                        val newMap = variableMap.copy(variableMap = variableMap.variableMap.apply { put(it, value) })
                        stringBuffer.append("${it.identifier} = $value")
                        return newMap
                    } else {
                        throw IllegalArgumentException("variable ${ast.identifier} is not mutable")
                    }
                } ?: throw IllegalArgumentException("variable ${ast.identifier} not declared")
            }
        }
    }
}
