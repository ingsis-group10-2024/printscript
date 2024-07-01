package strategy

import FailedResponse
import InterpreterManager
import ast.ASTNode
import ast.Assignation
import ast.BinaryOperationNode
import ast.ConditionNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.IfNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import strategy.interpreters.AssignationInterpreter
import strategy.interpreters.BinaryOperationNodeInterpreter
import strategy.interpreters.DeclarationNodeInterpreter
import strategy.interpreters.IdentifierOperatorNodeInterpreter
import strategy.interpreters.IfNodeInterpreter
import strategy.interpreters.MethodNodeInterpreter
import variable.VariableMap

class InterpreterManagerImplStrategy(val variableMap: VariableMap, val envVariableMap: VariableMap) : InterpreterManager {
    private val stringBuffer = StringBuffer()

    override fun interpret(astList: List<ASTNode>): Pair<VariableMap, String?> {
        if (astList.isEmpty()) return Pair(variableMap, null)
        var varMap = variableMap
        for (ast in astList) {
            when (ast) {
                is DeclarationNode -> {
                    varMap = DeclarationNodeInterpreter(variableMap).interpret(ast)
                }

                is Assignation -> {
                    varMap = AssignationInterpreter(variableMap, envVariableMap).interpret(ast).first
                    stringBuffer.append(AssignationInterpreter(variableMap, envVariableMap).interpret(ast).second)
                }

                is MethodNode -> {
                    stringBuffer.append(MethodNodeInterpreter(variableMap, envVariableMap).interpret(ast))
                }

                is IfNode -> {
                    stringBuffer.append(IfNodeInterpreter(variableMap, envVariableMap).interpret(ast))
                }

                is NumberOperatorNode -> {
                    stringBuffer.append(BinaryOperationNodeInterpreter(variableMap, envVariableMap).interpret(ast))
                }

                is StringOperatorNode -> {
                    stringBuffer.append(BinaryOperationNodeInterpreter(variableMap, envVariableMap).interpret(ast))
                }

                is BinaryOperationNode -> {
                    stringBuffer.append(BinaryOperationNodeInterpreter(variableMap, envVariableMap).interpret(ast))
                }
                is IdentifierOperatorNode -> {
                    stringBuffer.append(IdentifierOperatorNodeInterpreter(variableMap).interpret(ast))
                }
                is ConditionNode -> {
//                    stringBuffer.append(ConditionNodeInterpreter(variableMap).interpret(ast))
                    stringBuffer.append(BinaryOperationNodeInterpreter(variableMap, envVariableMap).interpret(ast))
                }
                else -> stringBuffer.append(FailedResponse("Invalid Node Type").message)
            }
        }
        return Pair(varMap, stringBuffer.toString())
    }
}
