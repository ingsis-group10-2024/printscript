package strategy

import FailedResponse
import InterpreterManager
import ast.ASTNode
import ast.Assignation
import ast.BinaryOperationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import strategy.interpreters.AssignationInterpreter
import strategy.interpreters.BinaryOperationNodeInterpreter
import strategy.interpreters.DeclarationNodeInterpreter
import strategy.interpreters.IdentifierOperatorNodeInterpreter
import variable.VariableMap

class InterpreterManagerImpl(val variableMap: VariableMap) : InterpreterManager {
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
                    varMap = AssignationInterpreter(variableMap).interpret(ast).first
                    stringBuffer.append(AssignationInterpreter(variableMap).interpret(ast).second)
                }

                is MethodNode -> {
                    stringBuffer.append(strategy.interpreters.MethodNodeInterpreter(variableMap).interpret(ast))
                }
                is NumberOperatorNode -> {
                    stringBuffer.append(BinaryOperationNodeInterpreter(variableMap).interpret(ast))
                }

                is StringOperatorNode -> {
                    stringBuffer.append(BinaryOperationNodeInterpreter(variableMap).interpret(ast))
                }

                is BinaryOperationNode -> {
                    stringBuffer.append(BinaryOperationNodeInterpreter(variableMap).interpret(ast))
                }
                is IdentifierOperatorNode -> {
                    stringBuffer.append(IdentifierOperatorNodeInterpreter(variableMap).interpret(ast))
                }
                else -> stringBuffer.append(FailedResponse("Invalid Node Type").message)
            }
        }
        return Pair(varMap, stringBuffer.toString())
    }
}
