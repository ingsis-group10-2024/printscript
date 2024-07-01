package strategy.interpreters

import ast.ASTNode
import ast.Assignation
import ast.AssignationNode
import ast.DeclarationAssignationNode
import strategy.Interpreter
import variable.Variable
import variable.VariableMap

class AssignationInterpreter(val variableMap: VariableMap, val envVariableMap: VariableMap) : Interpreter {
    private val stringBuffer = StringBuffer()

    override fun interpret(ast: ASTNode): Pair<VariableMap, String?> {
        require(ast is Assignation) { "ast should be a Assignation" }
        return Pair(interpretAssignation(ast), stringBuffer.toString())
    }

    private fun interpretAssignation(ast: Assignation): VariableMap {
        when (ast) {
            is DeclarationAssignationNode -> {
                if (variableMap.containsKey(Variable(ast.declaration.identifier, ast.declaration.type))) {
                    return variableMap
                }
                val variable = Variable(ast.declaration.identifier, ast.declaration.type)
                val value = BinaryOperationNodeInterpreter(variableMap, envVariableMap).interpret(ast.assignation)
                val newMap = variableMap.copy(variableMap = variableMap.variableMap.apply { put(variable, value) })
                return newMap
            }
            is AssignationNode -> {
                variableMap.findKey(ast.identifier)?.let {
                    val value = BinaryOperationNodeInterpreter(variableMap, envVariableMap).interpret(ast.assignation)
                    val newMap = variableMap.copy(variableMap = variableMap.variableMap.apply { put(it, value) })
                    stringBuffer.append("${it.identifier} = $value")

                    return newMap
                } ?: stringBuffer.append("variable ${ast.identifier} not declared")
            }
        }
        return variableMap
    }
}
