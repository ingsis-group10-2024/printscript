package strategy

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
import reader.Reader
import strategy.interpreters.AssignationInterpreterV11
import strategy.interpreters.BinaryOperationNodeInterpreterV11
import strategy.interpreters.ConditionNodeInterpreter
import strategy.interpreters.DeclarationNodeInterpreterV11
import strategy.interpreters.IdentifierOperatorNodeInterpreter
import strategy.interpreters.IfNodeInterpreter
import strategy.interpreters.MethodNodeInterpreterV11
import variable.VariableMap

class InterpreterManagerImplStrategyV11(
    val variableMap: VariableMap,
    val reader: Reader,
) : InterpreterManager {
    private val stringList = ArrayList<String>()

    @Throws(IllegalArgumentException::class)
    override fun interpret(astList: List<ASTNode>): Pair<VariableMap, ArrayList<String>> {
        if (astList.isEmpty()) return Pair(variableMap, stringList)
        var varMap = variableMap
        for (ast in astList) {
            when (ast) {
                is DeclarationNode -> {
                    varMap = DeclarationNodeInterpreterV11(variableMap).interpret(ast)
                }

                is Assignation -> {
                    varMap = AssignationInterpreterV11(variableMap, reader).interpret(ast).first
                }

                is MethodNode -> {
                    stringList.add(MethodNodeInterpreterV11(variableMap, reader).interpret(ast))
                }

                is IfNode -> {
                    stringList.addAll(IfNodeInterpreter(variableMap, reader).interpret(ast))
                }

                is NumberOperatorNode -> {
                    stringList.add(BinaryOperationNodeInterpreterV11(variableMap, reader).interpret(ast))
                }

                is StringOperatorNode -> {
                    stringList.add(BinaryOperationNodeInterpreterV11(variableMap, reader).interpret(ast))
                }

                is BinaryOperationNode -> {
                    stringList.add(BinaryOperationNodeInterpreterV11(variableMap, reader).interpret(ast))
                }
                is IdentifierOperatorNode -> {
                    stringList.add(IdentifierOperatorNodeInterpreter(variableMap).interpret(ast).toString())
                }
                is ConditionNode -> {
                    ConditionNodeInterpreter(variableMap, reader).interpret(ast)
//                    stringBuffer.append(BinaryOperationNodeInterpreter(variableMap, envVariableMap).interpret(ast))
                }
                else -> throw IllegalArgumentException("Invalid Node Type")
            }
        }
        return Pair(varMap, stringList)
    }
}
