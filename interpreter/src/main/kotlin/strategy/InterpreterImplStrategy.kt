package strategy

import Interpreter
import ast.ASTNode
import ast.Assignation
import ast.BinaryOperationNode
import ast.DeclarationNode
import ast.IfNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import variable.VariableMap

class InterpreterImplStrategy(val variableMap: VariableMap , val envVariableMap: VariableMap) : Interpreter {
    override fun interpret(astList: List<ASTNode>): Pair<VariableMap, String?> {
        if (astList.isEmpty()) return Pair(variableMap, null)
        var varMap = variableMap
//        for (ast in astList) {
//            when (ast) {
//                is DeclarationNode -> {
//                    varMap = interpretDeclarationNode(ast)
//                }
//
//                is Assignation -> {
//                    varMap = interpretAssignation(ast)
//                }
//
//                is MethodNode -> {
//                    interpretMethod(ast)
//                }
//
//                is IfNode -> {
//                    interpretIfNode(ast)
//                }
//
//                is NumberOperatorNode -> {
//                    stringBuffer.append(interpretBinaryNode(ast))
//                }
//
//                is StringOperatorNode -> {
//                    stringBuffer.append(interpretBinaryNode(ast))
//                }
//
//                is BinaryOperationNode -> {
//                    stringBuffer.append(interpretBinaryNode(ast))
//                }
//
//                else -> stringBuffer.append(FailedResponse("Invalid Node Type").message)
//            }
        return Pair(varMap, "wololo")
    }
}