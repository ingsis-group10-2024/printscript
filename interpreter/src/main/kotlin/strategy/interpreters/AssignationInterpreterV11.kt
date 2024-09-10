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
                    if (verifyTypeCompatibility11(variable, value)) {
                        val newMap =
                            variableMap.copy(variableMap = variableMap.variableMap.apply { put(variable, value) })
                        return newMap
                    }
                    return variableMap
                } else {
                    val variable = Variable(ast.declaration.identifier, ast.declaration.type, false)
                    val value =
                        BinaryOperationNodeInterpreterV11(
                            variableMap,
                            reader,
                        ).interpret(ast.assignation)
                    if (verifyTypeCompatibility11(variable, value)) {
                        val newMap =
                            variableMap.copy(variableMap = variableMap.variableMap.apply { put(variable, value) })
                        return newMap
                    }
                    return variableMap
                }
            }
            is AssignationNode -> {
                variableMap.findKey(ast.identifier)?.let {
                    val value =
                        BinaryOperationNodeInterpreterV11(
                            variableMap,
                            reader,
                        ).interpret(ast.assignation)
                    if (it.isMutable && verifyTypeCompatibility11(it, value)) {
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

    // este metodo verifica que el tipo de la variable sea compatible con el tipo de la asignacion que se le quiere hacer
    private fun verifyTypeCompatibility11(
        variable: Variable,
        value: String,
    ): Boolean {
        when (variable.type) {
            "number" -> {
                if (!isNumeric(value)) {
                    throw IllegalArgumentException("Type mismatch: number expected for variable ${variable.identifier}")
                }
                return true
            }
            "string" -> {
                if (isNumeric(value)) {
                    throw IllegalArgumentException("Type mismatch: string expected for variable ${variable.identifier}")
                }
                return true
            }
            "boolean" -> {
                if (value.toBooleanStrictOrNull() == null) {
                    throw IllegalArgumentException("Type mismatch: boolean expected for variable ${variable.identifier}")
                }
                return true
            }
        }
        throw IllegalArgumentException("Type mismatch")
    }

    private fun isNumeric(value: String): Boolean {
        return value.toDoubleOrNull() != null || value.toIntOrNull() != null
    }
}
