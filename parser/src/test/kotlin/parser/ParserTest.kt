package parser

import ast.AssignationNode
import ast.BinaryOperationNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import common.token.Token
import common.token.TokenType
import org.junit.Test
import kotlin.test.assertEquals

class ParserTest {
    @Test
    fun testParseAddition() {
        val tokens =
            listOf(
                Token(TokenType.NUMERIC_LITERAL, "5", 1, 0),
                Token(TokenType.PLUS, "+", 2, 0),
                Token(TokenType.NUMERIC_LITERAL, "3", 3, 0),
            )

        val parser = Parser(tokens)
        val result = parser.parseAddition()

        val expected =
            BinaryOperationNode(
                "+",
                NumberOperatorNode(5),
                NumberOperatorNode(3),
            )

        assertEquals(expected, result)
    }

    @Test
    fun testParseMultiplication() {
        val tokens =
            listOf(
                Token(TokenType.NUMERIC_LITERAL, "2", 1, 0),
                Token(TokenType.MULTIPLY, "*", 2, 0),
                Token(TokenType.NUMERIC_LITERAL, "8", 3, 0),
            )

        val parser = Parser(tokens)
        val result = parser.parseMultiplication()

        val expected =
            BinaryOperationNode(
                "*",
                NumberOperatorNode(2),
                NumberOperatorNode(8),
            )

        assertEquals(expected, result)
    }

    @Test
    fun testParserComplexExpression() {
        val tokens =
            listOf(
                Token(TokenType.NUMERIC_LITERAL, "5", 1, 0),
                Token(TokenType.PLUS, "+", 2, 0),
                Token(TokenType.NUMERIC_LITERAL, "3", 3, 0),
                Token(TokenType.MULTIPLY, "*", 4, 0),
                Token(TokenType.NUMERIC_LITERAL, "2", 5, 0),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        // El resultado esperado es: (5 + (3 * 2))
        val expected =
            listOf(
                BinaryOperationNode(
                    "+",
                    NumberOperatorNode(5),
                    BinaryOperationNode(
                        "*",
                        NumberOperatorNode(3),
                        NumberOperatorNode(2),
                    ),
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun testParseDeclaration() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 1, 0),
                Token(TokenType.IDENTIFIER, "x", 2, 0),
                Token(TokenType.COLON, ":", 3, 0),
                Token(TokenType.NUMERIC_LITERAL, "number", 4, 0),
                Token(TokenType.SEMICOLON, ";", 5, 0),
            )

        val parser = Parser(tokens)
        val result = parser.parseDeclaration()

        val expected = DeclarationNode("x", "number")

        assertEquals(expected, result)
    }

    @Test
    fun testGenerateMixedAST() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 1, 0),
                Token(TokenType.IDENTIFIER, "x", 2, 0),
                Token(TokenType.COLON, ":", 3, 0),
                Token(TokenType.NUMERIC_LITERAL, "number", 4, 0),
                Token(TokenType.SEMICOLON, ";", 5, 1),
                Token(TokenType.NUMERIC_LITERAL, "5", 6, 1),
                Token(TokenType.PLUS, "+", 7, 1),
                Token(TokenType.NUMERIC_LITERAL, "3", 8, 1),
                Token(TokenType.MULTIPLY, "*", 9, 1),
                Token(TokenType.NUMERIC_LITERAL, "2", 10, 1),
                Token(TokenType.NUMERIC_LITERAL, "80", 11, 2),
                Token(TokenType.STRING_TYPE, "Hola", 12, 3),
                // Token(TokenType.IDENTIFIER, "x", 13, 4)
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected =
            listOf(
                DeclarationNode("x", "number"),
                BinaryOperationNode(
                    "+",
                    NumberOperatorNode(5),
                    BinaryOperationNode(
                        "*",
                        NumberOperatorNode(3),
                        NumberOperatorNode(2),
                    ),
                ),
                NumberOperatorNode(80),
                StringOperatorNode("Hola"),
                // IdentifierOperatorNode("x")
            )

        assertEquals(expected, result)
    }

    @Test
    fun testParseNumericAssignation() {
        val tokens =
            listOf(
                Token(TokenType.IDENTIFIER, "x", 1, 0),
                Token(TokenType.EQUALS, "=", 2, 0),
                Token(TokenType.NUMERIC_LITERAL, "5", 4, 0),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected = listOf(AssignationNode("x", NumberOperatorNode(5)))

        assertEquals(expected, result)
    }

    @Test
    fun testParseComplexNumericAssignation() {
        val tokens =
            listOf(
                Token(TokenType.IDENTIFIER, "x", 1, 0),
                Token(TokenType.EQUALS, "=", 2, 0),
                Token(TokenType.NUMERIC_LITERAL, "5", 4, 0),
                Token(TokenType.NUMERIC_LITERAL, "5", 1, 1),
                Token(TokenType.PLUS, "+", 2, 1),
                Token(TokenType.NUMERIC_LITERAL, "3", 3, 1),
                Token(TokenType.MULTIPLY, "*", 4, 1),
                Token(TokenType.NUMERIC_LITERAL, "2", 5, 1),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected =
            listOf(
                AssignationNode("x", NumberOperatorNode(5)),
                BinaryOperationNode(
                    "+",
                    NumberOperatorNode(5),
                    BinaryOperationNode(
                        "*",
                        NumberOperatorNode(3),
                        NumberOperatorNode(2),
                    ),
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun testParseDeclarationAssignation() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 1, 1),
                Token(TokenType.IDENTIFIER, "x", 1, 1),
                Token(TokenType.COLON, ":", 1, 1),
                Token(TokenType.NUMERIC_LITERAL, "number", 1, 1),
                Token(TokenType.EQUALS, "=", 1, 1),
                Token(TokenType.NUMERIC_LITERAL, "5", 1, 1),
                Token(TokenType.SEMICOLON, ";", 1, 1),
            )

        val parser = Parser(tokens)
        val result = parser.parseDeclarationAssignation()

        val expected =
            DeclarationAssignationNode(
                DeclarationNode("x", "number"),
                NumberOperatorNode(5),
            )

        assertEquals(expected, result)
    }
}
