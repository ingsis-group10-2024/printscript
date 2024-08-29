package strategy.interpreters

import ast.ASTNode
import ast.BinaryOperationNode
import ast.BooleanOperatorNode
import ast.ConditionNode
import ast.IdentifierOperatorNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import reader.Reader
import strategy.Interpreter
import variable.VariableMap

class BinaryOperationNodeInterpreterV11(
    val variableMap: VariableMap,
    val envVariableMap: VariableMap,
    val reader: Reader,
) : Interpreter {
    override fun interpret(ast: ASTNode): String {
        return interpretBinaryNode(ast)
    }

    @Throws(IllegalArgumentException::class)
    private fun interpretBinaryNode(ast: ASTNode): String {
        return when (ast) {
            is NumberOperatorNode -> (ast.value).toString()
            is StringOperatorNode -> ast.value
            is BooleanOperatorNode -> ast.value.toString()
            is MethodNode -> MethodNodeInterpreterV11(variableMap, envVariableMap, reader).interpret(ast)
            is IdentifierOperatorNode -> IdentifierOperatorNodeInterpreter(variableMap).interpret(ast) as String
            is ConditionNode -> ConditionNodeInterpreter(variableMap, envVariableMap, reader).interpret(ast).toString()
            is BinaryOperationNode -> {
                val left = ast.left!!
                val right = ast.right!!
                when (ast.symbol) {
                    "+" ->
                        when {
                            left is StringOperatorNode && right is StringOperatorNode -> return (left.value + right.value)

                            left is NumberOperatorNode && right is NumberOperatorNode -> return (left.value + right.value).toString()

                            left is StringOperatorNode && right is NumberOperatorNode -> return (left.value + right.value.toString())

                            left is NumberOperatorNode && right is StringOperatorNode -> return (left.value.toString() + right.value)

                            left is IdentifierOperatorNode && right is NumberOperatorNode -> {
                                val leftValue = interpretBinaryNode(left)
                                return if (valueIsNumeric(leftValue)) {
                                    (leftValue.toDouble() + right.value).toString()
                                } else {
                                    leftValue + right.value
                                }
                            }
                            left is IdentifierOperatorNode && right is StringOperatorNode -> {
                                val leftValue = interpretBinaryNode(left)
                                return leftValue + right.value
                            }
                            left is StringOperatorNode && right is IdentifierOperatorNode -> {
                                val rightValue = interpretBinaryNode(right)
                                return left.value + rightValue
                            }
                            left is NumberOperatorNode && right is IdentifierOperatorNode -> {
                                val rightValue = interpretBinaryNode(right)
                                return if (valueIsNumeric(rightValue)) {
                                    (left.value + rightValue.toDouble()).toString()
                                } else {
                                    rightValue + left.value
                                }
                            }
                            left is IdentifierOperatorNode && right is IdentifierOperatorNode -> {
                                val leftValue = interpretBinaryNode(left)
                                val rightValue = interpretBinaryNode(right)
                                return if (valueIsNumeric(leftValue) && valueIsNumeric(rightValue)) {
                                    // Both are numbers, perform addition
                                    (leftValue.toDouble() + rightValue.toDouble()).toString()
                                } else {
                                    // At least one is a string, perform concatenation
                                    leftValue + rightValue
                                }
                            }
                            left is BinaryOperationNode || right is BinaryOperationNode -> {
                                val leftValue = interpretBinaryNode(left)
                                val rightValue = interpretBinaryNode(right)
                                return if (valueIsNumeric(leftValue) && valueIsNumeric(rightValue)) {
                                    // Both are numbers, perform addition
                                    (leftValue.toDouble() + rightValue.toDouble()).toString()
                                } else {
                                    // At least one is a string, perform concatenation
                                    leftValue + rightValue
                                }
                            }

                            else -> throw IllegalArgumentException("Invalid Operation")
                        }
                    "-", "*", "/" ->
                        when {
                            left is NumberOperatorNode && right is NumberOperatorNode -> {
                                val result =
                                    when (ast.symbol) {
                                        "-" -> left.value - right.value
                                        "*" -> left.value * right.value
                                        "/" -> left.value / right.value

                                        else -> throw IllegalArgumentException("Invalid Operation")
                                    }
                                return result.toString()
                            }
                            left is IdentifierOperatorNode && right is NumberOperatorNode -> {
                                val leftValue = interpretBinaryNode(left)
                                return if (valueIsNumeric(leftValue)) {
                                    val result =
                                        when (ast.symbol) {
                                            "-" -> leftValue.toDouble() - right.value
                                            "*" -> leftValue.toDouble() * right.value
                                            "/" -> leftValue.toDouble() / right.value

                                            else -> throw IllegalArgumentException("Invalid Operation")
                                        }
                                    result.toString()
                                } else {
                                    throw IllegalArgumentException("Invalid Operation")
                                }
                            }
                            left is IdentifierOperatorNode && right is IdentifierOperatorNode -> {
                                val leftValue = interpretBinaryNode(left)
                                val rightValue = interpretBinaryNode(right)
                                return if (valueIsNumeric(leftValue) && valueIsNumeric(rightValue)) {
                                    val result =
                                        when (ast.symbol) {
                                            "-" -> leftValue.toDouble() - rightValue.toDouble()
                                            "*" -> leftValue.toDouble() * rightValue.toDouble()
                                            "/" -> leftValue.toDouble() / rightValue.toDouble()

                                            else -> throw IllegalArgumentException("Invalid Operation")
                                        }
                                    result.toString()
                                } else {
                                    throw IllegalArgumentException("Invalid Operation")
                                }
                            }
                            left is BinaryOperationNode || right is BinaryOperationNode -> {
                                val leftValue = interpretBinaryNode(left)
                                val rightValue = interpretBinaryNode(right)
                                return if (valueIsNumeric(leftValue) && valueIsNumeric(rightValue)) {
                                    val result =
                                        when (ast.symbol) {
                                            "-" -> leftValue.toDouble() - rightValue.toDouble()
                                            "*" -> leftValue.toDouble() * rightValue.toDouble()
                                            "/" -> leftValue.toDouble() / rightValue.toDouble()

                                            else -> throw IllegalArgumentException("Invalid Operation")
                                        }
                                    result.toString()
                                } else {
                                    throw IllegalArgumentException("Invalid Operation")
                                }
                            }

                            else -> throw IllegalArgumentException("Invalid Operation")
                        }
                    else -> throw IllegalArgumentException("Invalid Operation")
                }
            }

            else -> {
                throw IllegalArgumentException("Invalid Node Type")
            }
        }
    }

    private fun valueIsNumeric(value: String) = value.toDoubleOrNull() != null
}
