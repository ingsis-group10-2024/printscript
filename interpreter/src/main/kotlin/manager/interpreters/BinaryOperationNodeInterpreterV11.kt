package manager.interpreters

import ast.ASTNode
import ast.BinaryOperationNode
import ast.BooleanOperatorNode
import ast.ConditionNode
import ast.IdentifierOperatorNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import emitter.Printer
import manager.Interpreter
import reader.Reader
import variable.VariableMap

class BinaryOperationNodeInterpreterV11(
    val variableMap: VariableMap,
    val reader: Reader,
    val outputter: Printer,
) : Interpreter {
    override fun interpret(ast: ASTNode): String {
        return interpretBinaryNode(ast)
    }

    @Throws(IllegalArgumentException::class)
    private fun interpretBinaryNode(ast: ASTNode): String {
        return when (ast) {
            is NumberOperatorNode -> BinaryOperationNodeInterpreterV10(variableMap, outputter).interpret(ast)
            is StringOperatorNode -> ast.value
            is BooleanOperatorNode -> ast.value.toString()
            is MethodNode -> MethodNodeInterpreterV11(variableMap, reader, outputter).interpret(ast)
            is IdentifierOperatorNode -> IdentifierOperatorNodeInterpreter(variableMap).interpret(ast) as String
            is ConditionNode -> ConditionNodeInterpreter(variableMap, reader, outputter).interpret(ast).toString()
            is BinaryOperationNode -> {
                BinaryOperationNodeInterpreterV10(variableMap, outputter).interpret(ast)
            }
            else -> {
                throw IllegalArgumentException("Invalid Node Type")
            }
        }
    }
}
