package strategy.interpreters

import ast.ASTNode
import ast.BinaryOperationNode
import ast.IdentifierOperatorNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import strategy.Interpreter
import variable.VariableMap

class BinaryOperationNodeInterpreterV10(val variableMap: VariableMap) : Interpreter {
    override fun interpret(ast: ASTNode): String {
        return interpretBinaryNode(ast)
    }

    private fun interpretBinaryNode(ast: ASTNode): String {
        return when (ast) {
            is NumberOperatorNode -> convertToString(ast.value)
            is StringOperatorNode -> ast.value
            is MethodNode -> MethodNodeInterpreterV10(variableMap).interpret(ast)
            is IdentifierOperatorNode -> IdentifierOperatorNodeInterpreter(variableMap).interpret(ast) as String
            is BinaryOperationNode -> interpretBinaryOperation(ast)
            else -> throw IllegalArgumentException("Invalid Operation")
        }
    }

    private fun interpretBinaryOperation(node: BinaryOperationNode): String {
        val leftValue = interpretBinaryNode(node.left!!)
        val rightValue = interpretBinaryNode(node.right!!)

        return when (node.symbol) {
            "+" -> handleAddition(leftValue, rightValue)
            "-", "*", "/" -> handleArithmeticOperation(leftValue, rightValue, node.symbol)
            else -> throw IllegalArgumentException("Invalid Operation")
        }
    }

    private fun handleAddition(
        left: String,
        right: String,
    ): String {
        if (isNumeric(left) && isNumeric(right)) {
            return convertToString(left.toDouble() + right.toDouble())
        } else {
            return left + right
        }
    }

    private fun handleArithmeticOperation(
        left: String,
        right: String,
        symbol: String,
    ): String {
        if (!isNumeric(left) || !isNumeric(right)) {
            throw IllegalArgumentException("Invalid Operation")
        }

        val result =
            when (symbol) {
                "-" -> left.toDouble() - right.toDouble()
                "*" -> left.toDouble() * right.toDouble()
                "/" -> left.toDouble() / right.toDouble()
                else -> throw IllegalArgumentException("Invalid Operation")
            }

        return convertToString(result)
    }

    private fun isNumeric(value: String): Boolean {
        return value.toDoubleOrNull() != null || value.toIntOrNull() != null
    }

    private fun convertToString(value: Double): String {
        val intValue = value.toInt()
        return if (intValue.toDouble() == value) intValue.toString() else value.toString()
    }
}
