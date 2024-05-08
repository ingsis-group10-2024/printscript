package lexer

import TokenType
import implementation.Lexer
import implementation.MockInputStream
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class LexerTest {
    @Test
    fun testBasicArithmetic() {
        val input = "+ - * /"
        val lexer = Lexer(input.byteInputStream())
        val tokens = lexer.getToken()
        println(tokens)
        assertEquals(7, tokens.size)
        assertEquals(TokenType.PLUS, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.MINUS, tokens[2].type)
        assertEquals(TokenType.WHITESPACE, tokens[3].type)
        assertEquals(TokenType.TIMES, tokens[4].type)
        assertEquals(TokenType.WHITESPACE, tokens[5].type)
        assertEquals(TokenType.DIVIDE, tokens[6].type)
    }

    @Test
    fun `test convertToToken with small input`() {
        val input =
            "let x = 10;\n" +
                "let y = 20;"
        val inputStream = ByteArrayInputStream(input.toByteArray(StandardCharsets.UTF_8))
        val lexer = Lexer(inputStream)
        val actualTokens = lexer.getToken()
        println(actualTokens)

        assertEquals(TokenType.LET, actualTokens[0].type)
        assertEquals(TokenType.WHITESPACE, actualTokens[1].type)
        assertEquals(TokenType.IDENTIFIER, actualTokens[2].type)
        assertEquals(TokenType.WHITESPACE, actualTokens[3].type)
        assertEquals(TokenType.EQUALS, actualTokens[4].type)
        assertEquals(TokenType.WHITESPACE, actualTokens[5].type)
        assertEquals(TokenType.NUMERIC_LITERAL, actualTokens[6].type)
        assertEquals(TokenType.SEMICOLON, actualTokens[7].type)
    }

    @Test
    fun `test convertToToken with large input`() {
        val input =
            buildString {
                repeat(1000) {
                    append("let x = 10;\n") // 9 tokens
                }
            }
        val inputStream = ByteArrayInputStream(input.toByteArray(StandardCharsets.UTF_8))
        val lexer = Lexer(inputStream)

        val actualTokens = lexer.getToken()

        println(actualTokens.size)

        assertEquals(TokenType.LET, actualTokens[0].type)
        assertEquals(TokenType.WHITESPACE, actualTokens[1].type)
        assertEquals(TokenType.IDENTIFIER, actualTokens[2].type)
        assertEquals(TokenType.WHITESPACE, actualTokens[3].type)
        assertEquals(TokenType.EQUALS, actualTokens[4].type)
        assertEquals(TokenType.WHITESPACE, actualTokens[5].type)
        assertEquals(TokenType.NUMERIC_LITERAL, actualTokens[6].type)
        assertEquals(TokenType.SEMICOLON, actualTokens[7].type)
        // assertEquals(900, actualTokens.size)
    }

    @Test
    fun `test lexer with large file`() {
        val numberOfLines: Int = 32 * 1024
        val message = "This is a text;"
        val line = "println(\"$message\")"
        val mockInputStream = MockInputStream(line, numberOfLines)

        // Initialize the Lexer
        val lexer = Lexer(mockInputStream)

        // Convert the content to tokens
        val tokens = lexer.getToken()

        println(tokens[0])
        println(tokens[1])
        println(tokens[2])
        println(tokens[3])
        println(tokens[4])
        println(tokens[5])
        println(tokens[6])
        println(tokens[7])
        println(tokens[8])
        println(tokens[9])
        println(tokens[10])
        println(tokens[11])
        // Assert that the tokens are as expected
        assertEquals(TokenType.PRINTLN, tokens[0].type)
        assertEquals(TokenType.OPEN_PARENTHESIS, tokens[1].type)
        assertEquals(TokenType.STRING_LITERAL, tokens[2].type)
        assertEquals(TokenType.CLOSE_PARENTHESIS, tokens[3].type)
    }
}
