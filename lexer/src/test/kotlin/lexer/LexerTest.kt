package lexer

import common.token.TokenType
import implementation.Lexer
import org.junit.Test
import kotlin.test.assertEquals

class LexerTest {
    @Test
    fun `convertToToken should return correct tokens for simple arithmetic expression`() {
        val lexer = Lexer("1 + 2 * 3")
        val tokens = lexer.convertToToken()
        assertEquals(9, tokens.size)
        assertEquals(TokenType.NUMERIC_LITERAL, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.PLUS, tokens[2].type)
        assertEquals(TokenType.WHITESPACE, tokens[3].type)
        assertEquals(TokenType.NUMERIC_LITERAL, tokens[4].type)
        assertEquals(TokenType.WHITESPACE, tokens[5].type)
        assertEquals(TokenType.TIMES, tokens[6].type)
        assertEquals(TokenType.WHITESPACE, tokens[7].type)
        assertEquals(TokenType.NUMERIC_LITERAL, tokens[8].type)
    }

    @Test
    fun `convertToToken should return correct tokens for string literal`() {
        val lexer = Lexer("\"Hello, World!\"")
        val tokens = lexer.convertToToken()
        assertEquals(1, tokens.size)
        assertEquals(TokenType.STRING_LITERAL, tokens[0].type)
        assertEquals("Hello, World!", tokens[0].value)
    }

    @Test
    fun `convertToToken should return correct tokens for keywords`() {
        val lexer = Lexer("let name: String = 'John';")
        val tokens = lexer.convertToToken()
        assertEquals(11, tokens.size)
        assertEquals(TokenType.LET, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.IDENTIFIER, tokens[2].type)
        assertEquals(TokenType.COLON, tokens[3].type)
        assertEquals(TokenType.WHITESPACE, tokens[4].type)
        assertEquals(TokenType.STRING_TYPE, tokens[5].type)
        assertEquals(TokenType.WHITESPACE, tokens[6].type)
        assertEquals(TokenType.EQUALS, tokens[7].type)
        assertEquals(TokenType.WHITESPACE, tokens[8].type)
        assertEquals(TokenType.STRING_LITERAL, tokens[9].type)
    }

    @Test
    fun `convertToToken should return correct tokens for identifier`() {
        val lexer = Lexer("myVariable")
        val tokens = lexer.convertToToken()
        assertEquals(1, tokens.size)
        assertEquals(TokenType.IDENTIFIER, tokens[0].type)
        assertEquals("myVariable", tokens[0].value)
    }

    @Test
    fun `convertToToken should return correct tokens for empty input`() {
        val lexer = Lexer("")
        val tokens = lexer.convertToToken()
        assertEquals(0, tokens.size)
    }

    @Test
    fun `convertToToken should return correct tokens for whitespace input`() {
        val lexer = Lexer("    ")
        val tokens = lexer.convertToToken()
        assertEquals(4, tokens.size)
        assertEquals(TokenType.WHITESPACE, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.WHITESPACE, tokens[2].type)
        assertEquals(TokenType.WHITESPACE, tokens[3].type)
    }
}
