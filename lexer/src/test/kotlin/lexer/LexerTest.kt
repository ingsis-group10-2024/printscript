package lexer

import implementation.Lexer

import implementation.MockInputStream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

import token.TokenType


class LexerTest {
    private val numberOfLine: Int = 32 * 1024

    @Test
    fun `test lexer with simple file`() {
        val message = "This is a text"
        val line = "println(\"$message\")"
        val mockInputStream = MockInputStream(line, numberOfLine)

        // Initialize the Lexer
        val lexer = Lexer(mockInputStream)

        // Convert the content to tokens
        val tokens = lexer.convertToToken()

        // Assert that the tokens are as expected
        assertEquals(131072, tokens.size)
        assertEquals(TokenType.PRINTLN, tokens[0].type)
        assertEquals(TokenType.OPEN_PARENTHESIS, tokens[1].type)
        assertEquals(TokenType.STRING_LITERAL, tokens[2].type)
        assertEquals(TokenType.CLOSE_PARENTHESIS, tokens[3].type)
    }

    /*
    @Test
    fun `test lexer with simple file and multiple lines`() {
        val message = "This is a text"
        val line = "println(\"$message\");\n"
        val mockInputStream = MockInputStream(line, numberOfLine)

        // Initialize the Lexer
        val lexer = Lexer(mockInputStream)

        // Convert the content to tokens
        val tokens = lexer.convertToToken()

        // Assert that the tokens are as expected
        assertEquals(131072, tokens.size)
        assertEquals(TokenType.PRINTLN, tokens[0].type)
        assertEquals(TokenType.OPEN_PARENTHESIS, tokens[1].type)
        assertEquals(TokenType.STRING_LITERAL, tokens[2].type)
        assertEquals(TokenType.CLOSE_PARENTHESIS, tokens[3].type)
    }

     */

    @Test
    fun `test lexer with boolean`() {
        val message = "let x: Boolean = false;"

        // Initialize the Lexer
        val mockInputStream = MockInputStream(message, numberOfLine)

        // Initialize the Lexer
        val lexer = Lexer(mockInputStream)

        // Convert the file content to tokens
        val tokens = lexer.convertToToken()

        // Assert that the tokens are as expected
        // println(tokens)
        assertEquals(360448, tokens.size)
        assertEquals(TokenType.LET, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.IDENTIFIER, tokens[2].type)
        assertEquals(TokenType.COLON, tokens[3].type)
        assertEquals(TokenType.WHITESPACE, tokens[4].type)
        assertEquals(TokenType.BOOLEAN_TYPE, tokens[5].type)
        assertEquals(TokenType.WHITESPACE, tokens[6].type)
        assertEquals(TokenType.EQUALS, tokens[7].type)
        assertEquals(TokenType.WHITESPACE, tokens[8].type)
        assertEquals(TokenType.BOOLEAN_LITERAL, tokens[9].type)
    }
}
