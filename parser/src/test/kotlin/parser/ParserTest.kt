import ast.ASTNode
import ast.BinaryOperationNode
import ast.DeclarationNode
import ast.NumberOperatorNode
import common.token.Token
import common.token.TokenType
import org.junit.Test
import parser.Parser
import kotlin.test.assertEquals

class ParserTest {

    @Test
    fun testParseAddition() {
        val tokens = listOf(
            Token(TokenType.NUMBER_TYPE, "5", 1, 0),
            Token(TokenType.PLUS, "+", 2, 0),
            Token(TokenType.NUMBER_TYPE, "3", 3, 0)
        )

        val parser = Parser(tokens)
        val result = parser.parseAddition()

        val expected = BinaryOperationNode(
            "+",
            NumberOperatorNode(5.0),
            NumberOperatorNode(3.0)
        )

        assertEquals(expected, result)
    }

    @Test
    fun testParseMultiplication() {
        val tokens = listOf(
            Token(TokenType.NUMBER_TYPE, "2", 1, 0),
            Token(TokenType.MULTIPLY, "*", 2, 0),
            Token(TokenType.NUMBER_TYPE, "8", 3, 0)
        )

        val parser = Parser(tokens)
        val result = parser.parseMultiplication()

        val expected = BinaryOperationNode(
            "*",
            NumberOperatorNode(2.0),
            NumberOperatorNode(8.0)
        )

        assertEquals(expected, result)
    }

    @Test
    fun testParserComplexExpression() {
        val tokens = listOf(
            Token(TokenType.NUMBER_TYPE, "5", 1, 0),
            Token(TokenType.PLUS, "+", 2, 0),
            Token(TokenType.NUMBER_TYPE, "3", 3, 0),
            Token(TokenType.MULTIPLY, "*", 4, 0),
            Token(TokenType.NUMBER_TYPE, "2", 5, 0)
        )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        // El resultado esperado es: (5 + (3 * 2))
        val expected = BinaryOperationNode(
            "+",
            NumberOperatorNode(5.0),
            BinaryOperationNode(
                "*",
                NumberOperatorNode(3.0),
                NumberOperatorNode(2.0)
            )
        )

        assertEquals(listOf(expected), result)
    }

    @Test
    fun testParseDeclaration() {
        val tokens = listOf(
            Token(TokenType.LET, "let", 1, 0),
            Token(TokenType.IDENTIFIER, "x", 2, 0),
            Token(TokenType.COLON, ":", 3, 0),
            Token(TokenType.NUMBER_TYPE, "number", 4, 0),
            Token(TokenType.SEMICOLON, ";", 5, 0)
        )

        val parser = Parser(tokens)
        val result = parser.parseDeclaration()

        val expected = DeclarationNode("x", "number")

        assertEquals(expected, result)
    }

}
