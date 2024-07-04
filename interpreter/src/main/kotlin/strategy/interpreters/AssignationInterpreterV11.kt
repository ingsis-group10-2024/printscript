package strategy.interpreters

import ast.ASTNode
import ast.Assignation
import ast.AssignationNode
import ast.DeclarationAssignationNode
import reader.Reader
import strategy.Interpreter
import variable.Variable
import variable.VariableMap

class AssignationInterpreterV11(val variableMap: VariableMap, val envVariableMap: VariableMap, val reader: Reader) : Interpreter {
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
                val value = BinaryOperationNodeInterpreterV11(variableMap, envVariableMap, reader).interpret(ast.assignation)
                val newMap = variableMap.copy(variableMap = variableMap.variableMap.apply { put(variable, value) })
                return newMap
            }
            is AssignationNode -> {
                variableMap.findKey(ast.identifier)?.let {
                    val value = BinaryOperationNodeInterpreterV11(variableMap, envVariableMap, reader).interpret(ast.assignation)
                    val newMap = variableMap.copy(variableMap = variableMap.variableMap.apply { put(it, value) })
                    stringBuffer.append("${it.identifier} = $value")

                    return newMap
                } ?: throw IllegalArgumentException("variable ${ast.identifier} not declared")
            }
        }
    }
}
