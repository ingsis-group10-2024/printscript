package manager.interpreters

import ast.ASTNode
import ast.Assignation
import ast.AssignationNode
import ast.DeclarationAssignationNode
import manager.Interpreter
import variable.Variable
import variable.VariableMap

class AssignationInterpreterV10(val variableMap: VariableMap) : Interpreter {
    private val stringBuffer = StringBuffer()

    override fun interpret(ast: ASTNode): Pair<VariableMap, String?> {
        require(ast is Assignation) { "ast should be a Assignation" }
        return Pair(interpretAssignation(ast), stringBuffer.toString())
    }

    private fun interpretAssignation(ast: Assignation): VariableMap {
        when (ast) {
            is DeclarationAssignationNode -> {
                if (variableMap.containsKey(Variable(ast.declaration.identifier, ast.declaration.type, true))) {
                    return variableMap
                }
                val variable = Variable(ast.declaration.identifier, ast.declaration.type, true)
                val value = BinaryOperationNodeInterpreterV10(variableMap).interpret(ast.assignation)
                if (verifyTypeCompatibility(variable, value)) {
                    val newMap = variableMap.copy(variableMap = variableMap.variableMap.apply { put(variable, value) })
                    return newMap
                }
                return variableMap
            }
            is AssignationNode -> {
                variableMap.findKey(ast.identifier)?.let {
                    val value = BinaryOperationNodeInterpreterV10(variableMap).interpret(ast.assignation)
                    if (verifyTypeCompatibility(it, value)) {
                        val newMap = variableMap.copy(variableMap = variableMap.variableMap.apply { put(it, value) })
                        stringBuffer.append("${it.identifier} = $value")
                        return newMap
                    }
                    return variableMap
                } ?: throw throw IllegalArgumentException("variable ${ast.identifier} not declared")
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun verifyTypeCompatibility(
        variable: Variable,
        value: String,
    ): Boolean {
        return when (variable.type) {
            "number" -> isNumeric(value)
            "string" -> !isNumeric(value)
            else -> false
        }.also {
                result ->
            if (!result) {
                throw IllegalArgumentException(
                    "Type mismatch: ${variable.type} expected for variable ${variable.identifier}, but got $value",
                )
            }
        }
    }

    private fun isNumeric(value: String): Boolean {
        return value.toDoubleOrNull() != null || value.toIntOrNull() != null
    }
}
