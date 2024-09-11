package strategy.interpreters

import ast.ASTNode
import ast.Assignation
import ast.AssignationNode
import ast.DeclarationAssignationNode
import emitter.PrintCollector
import reader.Reader
import strategy.Interpreter
import token.TokenType
import variable.Variable
import variable.VariableMap

class AssignationInterpreterV11(val variableMap: VariableMap, val reader: Reader, val printCollector: PrintCollector) : Interpreter {
    override fun interpret(ast: ASTNode): Pair<VariableMap, PrintCollector> {
        require(ast is Assignation) { "ast should be a Assignation or DeclarationAssignation" }
        return interpretAssignation(ast)
    }

    private fun interpretAssignation(ast: Assignation): Pair<VariableMap, PrintCollector> {
        when (ast) {
            is DeclarationAssignationNode -> {
                if (ast.declaration.declarationType == TokenType.LET) {
                    if (variableMap.containsKey(Variable(ast.declaration.identifier, ast.declaration.type, true))) {
                        return Pair(variableMap, printCollector)
                    }
                    val variable = Variable(ast.declaration.identifier, ast.declaration.type, true)
                    val value =
                        BinaryOperationNodeInterpreterV11(
                            variableMap,
                            reader,
                            printCollector,
                        ).interpret(ast.assignation)
                    if (verifyTypeCompatibility11(variable, value)) {
                        val newMap =
                            variableMap.copy(variableMap = variableMap.variableMap.apply { put(variable, value) })
                        return Pair(newMap, printCollector)
                    }
                    return Pair(variableMap, printCollector)
                } else {
                    val variable = Variable(ast.declaration.identifier, ast.declaration.type, false)
                    val value =
                        BinaryOperationNodeInterpreterV11(
                            variableMap,
                            reader,
                            printCollector,
                        ).interpret(ast.assignation)
                    if (verifyTypeCompatibility11(variable, value)) {
                        val newMap =
                            variableMap.copy(variableMap = variableMap.variableMap.apply { put(variable, value) })
                        return Pair(newMap, printCollector)
                    }
                    return Pair(variableMap, printCollector)
                }
            }
            is AssignationNode -> {
                variableMap.findKey(ast.identifier)?.let {
                    val value =
                        BinaryOperationNodeInterpreterV11(
                            variableMap,
                            reader,
                            printCollector,
                        ).interpret(ast.assignation)
                    if (it.isMutable && verifyTypeCompatibility11(it, value)) {
                        val newMap = variableMap.copy(variableMap = variableMap.variableMap.apply { put(it, value) })
                        printCollector.printableList.add("${it.identifier} = $value")
                        return Pair(newMap, printCollector)
                    } else {
                        throw IllegalArgumentException("variable ${ast.identifier} is not mutable")
                    }
                } ?: throw IllegalArgumentException("variable ${ast.identifier} not declared")
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun verifyTypeCompatibility11(
        variable: Variable,
        value: String,
    ): Boolean {
        return when (variable.type) {
            "number" -> isNumeric(value)
            "string" -> !isNumeric(value)
            "boolean" -> value.toBooleanStrictOrNull() != null
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
