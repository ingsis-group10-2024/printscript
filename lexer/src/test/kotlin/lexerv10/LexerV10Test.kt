package lexerv11

import controller.LexerVersionController
import implementation.LexerV10
import implementation.MockInputStream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import token.Token
import token.TokenType
import java.io.ByteArrayInputStream

class LexerV10Test {
    // Start the version controller
    private val versionController = LexerVersionController()

    private fun lexerFromInput(input: String): LexerV10 {
        return LexerV10(ByteArrayInputStream(input.toByteArray()))
    }

    @Test
    fun `test lexer with large file`() {
        val numberOfLines: Int = 32 * 1024
        val message = "This is a text;"
        val line = "println(\"$message\");\n"
        val mockInputStream = MockInputStream(line, numberOfLines)
        val lexer = versionController.getLexer("1.1", mockInputStream)

        val tokensList = mutableListOf<Token>() // Asumiendo que Token es el tipo retornado por getNextToken()

        for (i in 0 until numberOfLines * 4) {
            val token = lexer.getNextToken()
            if (token != null) {
                if (tokensList.size >= 20) break
                tokensList.add(token)
            }
        }
        println(tokensList)
    }

    @Test
    fun `test single token`() {
        val lexer = lexerFromInput("+")
        assertEquals(TokenType.PLUS, lexer.getNextToken()?.type)
    }

    @Test
    fun `test multiple tokens`() {
        val input = "+ - * / = == != ; : ( ) { }"
        val lexer = lexerFromInput(input)

        val expectedTokens =
            listOf(
                TokenType.PLUS,
                TokenType.MINUS,
                TokenType.MULTIPLY,
                TokenType.DIVIDE,
                TokenType.EQUALS,
                TokenType.EQUALS_EQUALS,
                TokenType.UNEQUALS,
                TokenType.SEMICOLON,
                TokenType.COLON,
                TokenType.OPEN_PARENTHESIS,
                TokenType.CLOSE_PARENTHESIS,
                TokenType.OPEN_BRACKET,
                TokenType.CLOSE_BRACKET,
            )

        for (expectedType in expectedTokens) {
            val token = lexer.getNextToken()
            assertEquals(expectedType, token?.type)
        }
    }

    @Test
    fun `test string literals`() {
        val input = "\"hello world\" 'single quote'"
        val lexer = lexerFromInput(input)

        val stringToken1 = lexer.getNextToken()
        assertEquals(TokenType.STRING_LITERAL, stringToken1?.type)
        assertEquals("hello world", stringToken1?.value)

        val stringToken2 = lexer.getNextToken()
        assertEquals(TokenType.STRING_LITERAL, stringToken2?.type)
        assertEquals("single quote", stringToken2?.value)
    }

    @Test
    fun `test identifiers and numbers`() {
        val input = "let 123 45.67 identifier"
        val lexer = lexerFromInput(input)

        val expectedTokens =
            listOf(
                TokenType.LET,
                TokenType.NUMERIC_LITERAL,
                TokenType.NUMERIC_LITERAL,
                TokenType.IDENTIFIER,
            )

        for (expectedType in expectedTokens) {
            val token = lexer.getNextToken()
            assertEquals(expectedType, token?.type)
        }
    }

    @Test
    fun `test final public private protected`() {
        val input = "final public private protected"
        val lexer = lexerFromInput(input)

        val expectedTokens =
            listOf(
                TokenType.FINAL,
                TokenType.PUBLIC,
                TokenType.PRIVATE,
                TokenType.PROTECTED,
            )

        for (expectedType in expectedTokens) {
            val token = lexer.getNextToken()
            assertEquals(expectedType, token?.type)
        }
    }

    @Test
    fun `test types`() {
        val input = "string number boolean"
        val lexer = lexerFromInput(input)

        val expectedTokens =
            listOf(
                TokenType.STRING_TYPE,
                TokenType.NUMBER_TYPE,
                TokenType.BOOLEAN_TYPE,
            )

        for (expectedType in expectedTokens) {
            val token = lexer.getNextToken()
            assertEquals(expectedType, token?.type)
        }
    }

    @Test
    fun `test return`() {
        val input = "return"
        val lexer = lexerFromInput(input)

        val expectedTokens = listOf(TokenType.RETURN)

        for (expectedType in expectedTokens) {
            val token = lexer.getNextToken()
            assertEquals(expectedType, token?.type)
        }
    }

    @Test
    fun `test number literals`() {
        val input = "123 123.456"
        val lexer = lexerFromInput(input)

        val expectedTokens = listOf(TokenType.NUMERIC_LITERAL, TokenType.NUMERIC_LITERAL)

        for (expectedType in expectedTokens) {
            val token = lexer.getNextToken()
            assertEquals(expectedType, token?.type)
        }
    }

    @Test
    fun `test with lesser and greater`() {
        val input = "a < b > c"
        val lexer = lexerFromInput(input)

        val expectedTokens =
            listOf(
                TokenType.IDENTIFIER,
                TokenType.LESSER_THAN,
                TokenType.IDENTIFIER,
                TokenType.GREATER_THAN,
                TokenType.IDENTIFIER,
            )

        for (expectedType in expectedTokens) {
            val token = lexer.getNextToken()
            assertEquals(expectedType, token?.type)
        }
    }

    @Test
    fun `test with lesser-equals and greater-equals`() {
        val input = "a <= b >= c"
        val lexer = lexerFromInput(input)

        val expectedTokens =
            listOf(
                TokenType.IDENTIFIER,
                TokenType.LESSER_THAN_EQUAL,
                TokenType.IDENTIFIER,
                TokenType.GREATER_THAN_EQUAL,
                TokenType.IDENTIFIER,
            )

        for (expectedType in expectedTokens) {
            val token = lexer.getNextToken()
            assertEquals(expectedType, token?.type)
        }
    }
}
