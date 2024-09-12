package manager

import InterpreterManager
import ast.ASTNode
import ast.Assignation
import ast.BinaryOperationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import manager.interpreters.AssignationInterpreterV10
import manager.interpreters.BinaryOperationNodeInterpreterV10
import manager.interpreters.DeclarationNodeInterpreterV10
import manager.interpreters.IdentifierOperatorNodeInterpreter
import manager.interpreters.MethodNodeInterpreterV10
import variable.VariableMap

class InterpreterManagerImplV10(val variableMap: VariableMap) : InterpreterManager {
    private val stringList = ArrayList<String>()

    override fun interpret(astList: List<ASTNode>): Pair<VariableMap, ArrayList<String>> {
        if (astList.isEmpty()) return Pair(variableMap, stringList)
        var varMap = variableMap
        for (ast in astList) {
            when (ast) {
                is DeclarationNode -> {
                    varMap = DeclarationNodeInterpreterV10(variableMap).interpret(ast)
                }

                is Assignation -> {
                    varMap = AssignationInterpreterV10(variableMap).interpret(ast).first
                }

                is MethodNode -> {
                    stringList.add(MethodNodeInterpreterV10(variableMap).interpret(ast))
                }

                is NumberOperatorNode -> {
                    stringList.add(BinaryOperationNodeInterpreterV10(variableMap).interpret(ast))
                }

                is StringOperatorNode -> {
                    stringList.add(BinaryOperationNodeInterpreterV10(variableMap).interpret(ast))
                }

                is BinaryOperationNode -> {
                    stringList.add(BinaryOperationNodeInterpreterV10(variableMap).interpret(ast))
                }
                is IdentifierOperatorNode -> {
                    stringList.add(IdentifierOperatorNodeInterpreter(variableMap).interpret(ast).toString())
                }
                else -> throw IllegalArgumentException("Invalid Node Type")
            }
        }
        return Pair(varMap, stringList)
    }
}
