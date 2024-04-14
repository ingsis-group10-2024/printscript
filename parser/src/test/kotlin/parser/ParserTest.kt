import ast.AssignationNode
import ast.BinaryOperationNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import token.TokenType
import org.junit.Test
import parser.Parser
import token.Token
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
                NumberOperatorNode(5.0),
                NumberOperatorNode(3.0),
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
                NumberOperatorNode(2.0),
                NumberOperatorNode(8.0),
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
                    NumberOperatorNode(5.0),
                    BinaryOperationNode(
                        "*",
                        NumberOperatorNode(3.0),
                        NumberOperatorNode(2.0),
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
                Token(TokenType.NUMBER_TYPE, "number", 4, 0),
                Token(TokenType.SEMICOLON, ";", 5, 0),
                Token(TokenType.LET, "let", 1, 1),
                Token(TokenType.IDENTIFIER, "y", 2, 1),
                Token(TokenType.COLON, ":", 3, 1),
                Token(TokenType.STRING_TYPE, "string", 4, 1),
                Token(TokenType.SEMICOLON, ";", 5, 1),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected = listOf(DeclarationNode("x", "number"), DeclarationNode("y", "string"))
        // val expected = listOf(DeclarationNode("y", "string"))

        assertEquals(expected, result)
    }

    @Test
    fun testGenerateMixedAST() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 1, 0),
                Token(TokenType.IDENTIFIER, "x", 2, 0),
                Token(TokenType.COLON, ":", 3, 0),
                Token(TokenType.NUMBER_TYPE, "number", 4, 0),
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
                    NumberOperatorNode(5.0),
                    BinaryOperationNode(
                        "*",
                        NumberOperatorNode(3.0),
                        NumberOperatorNode(2.0),
                    ),
                ),
                NumberOperatorNode(80.0),
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
                Token(TokenType.WHITESPACE, " ", 2, 0),
                Token(TokenType.EQUALS, "=", 3, 0),
                Token(TokenType.WHITESPACE, " ", 4, 0),
                Token(TokenType.NUMERIC_LITERAL, "5", 5, 0),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected = listOf(AssignationNode("x", NumberOperatorNode(5.0)))

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
                AssignationNode("x", NumberOperatorNode(5.0)),
                BinaryOperationNode(
                    "+",
                    NumberOperatorNode(5.0),
                    BinaryOperationNode(
                        "*",
                        NumberOperatorNode(3.0),
                        NumberOperatorNode(2.0),
                    ),
                ),
            )

        assertEquals(expected, result)
    }

    // todo: correr usando parser.generateAST()
    @Test
    fun testParseDeclarationAssignation() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 1, 1),
                Token(TokenType.IDENTIFIER, "x", 2, 1),
                Token(TokenType.COLON, ":", 3, 1),
                Token(TokenType.NUMBER_TYPE, "int", 4, 1),
                Token(TokenType.EQUALS, "=", 5, 1),
                Token(TokenType.NUMERIC_LITERAL, "5", 6, 1),
                Token(TokenType.SEMICOLON, ";", 7, 1),
                /* Token(TokenType.LET, "let", 1, 2),
                Token(TokenType.IDENTIFIER, "y", 2, 2),
                Token(TokenType.COLON, ":", 3, 2),
                Token(TokenType.NUMBER_TYPE, "string", 4, 2),
                Token(TokenType.EQUALS, "=", 5, 2),
                Token(TokenType.NUMERIC_LITERAL, "Hello", 6, 2),
                Token(TokenType.SEMICOLON, ";", 7, 2),*/
            )

        val parser = Parser(tokens)
        val result = parser.parseDeclarationAssignation()
        // val result = parser.generateAST()

        val expected =
            DeclarationAssignationNode(
                DeclarationNode("x", "int"),
                NumberOperatorNode(5.0),
            )

        assertEquals(expected, result)
        // assertEquals(listOf(expected), result)
    }

    @Test
    fun testParseComplexNumberDeclarationAssignation() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 1, 2),
                Token(TokenType.IDENTIFIER, "y", 2, 2),
                Token(TokenType.COLON, ":", 3, 2),
                Token(TokenType.NUMBER_TYPE, "int", 4, 2),
                Token(TokenType.EQUALS, "=", 5, 2),
                Token(TokenType.NUMERIC_LITERAL, "5", 6, 2),
                Token(TokenType.PLUS, "+", 7, 2),
                Token(TokenType.NUMERIC_LITERAL, " 3", 8, 2),
                Token(TokenType.SEMICOLON, ";", 9, 2),
            )

        val parser = Parser(tokens)
        val result = parser.parseDeclarationAssignation()
        // val result = parser.generateAST()

        val expected =
            DeclarationAssignationNode(
                DeclarationNode("y", "int"),
                // BinaryOperationNode("+", StringOperatorNode("Hello"), StringOperatorNode(" world"))
                BinaryOperationNode("+", NumberOperatorNode(5.0), NumberOperatorNode(3.0)),
            )

        assertEquals(expected, result)
        // assertEquals(listOf(expected), result)
    }

    @Test
    fun testParseComplexStringDeclarationAssignation() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 1, 1),
                Token(TokenType.IDENTIFIER, "x", 2, 1),
                Token(TokenType.COLON, ":", 3, 1),
                Token(TokenType.STRING_TYPE, "string", 4, 1),
                Token(TokenType.EQUALS, "=", 5, 1),
                Token(TokenType.STRING_LITERAL, "Hello", 6, 1),
                Token(TokenType.PLUS, "+", 7, 1),
                Token(TokenType.STRING_LITERAL, " world", 8, 1),
                Token(TokenType.SEMICOLON, ";", 9, 1),
            )

        val parser = Parser(tokens)
        val result = parser.parseDeclarationAssignation()
        // val result = parser.generateAST()

        val expected =
            DeclarationAssignationNode(
                DeclarationNode("x", "string"),
                BinaryOperationNode("+", StringOperatorNode("Hello"), StringOperatorNode(" world")),
            )

        assertEquals(expected, result)
        // assertEquals(listOf(expected), result)
    }

    @Test
    fun testStringPrintln() {
        val tokens =
            listOf(
                Token(TokenType.PRINTLN, "println", 1, 0),
                Token(TokenType.OPEN_PARENTHESIS, "(", 1, 0),
                Token(TokenType.STRING_LITERAL, "Hello", 1, 0),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 1, 0),
                Token(TokenType.SEMICOLON, ";", 1, 0),
            )

        val parser = Parser(tokens)
        val result = parser.parsePrintln()

        val expected = MethodNode("println", StringOperatorNode("Hello"))

        assertEquals(expected, result)
    }
}
