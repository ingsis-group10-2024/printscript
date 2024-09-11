package lexerv11

import controller.LexerVersionController
import implementation.LexerV11
import implementation.MockInputStream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import token.Token
import token.TokenType
import java.io.ByteArrayInputStream

class LexerTest {
    // Start the version controller
    private val versionController = LexerVersionController()

    private fun lexerFromInput(input: String): LexerV11 {
        return LexerV11(ByteArrayInputStream(input.toByteArray()))
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
                TokenType.WHITESPACE,
                TokenType.MINUS,
                TokenType.WHITESPACE,
                TokenType.MULTIPLY,
                TokenType.WHITESPACE,
                TokenType.DIVIDE,
                TokenType.WHITESPACE,
                TokenType.EQUALS,
                TokenType.WHITESPACE,
                TokenType.EQUALS_EQUALS,
                TokenType.WHITESPACE,
                TokenType.UNEQUALS,
                TokenType.WHITESPACE,
                TokenType.SEMICOLON,
                TokenType.WHITESPACE,
                TokenType.COLON,
                TokenType.WHITESPACE,
                TokenType.OPEN_PARENTHESIS,
                TokenType.WHITESPACE,
                TokenType.CLOSE_PARENTHESIS,
                TokenType.WHITESPACE,
                TokenType.OPEN_BRACKET,
                TokenType.WHITESPACE,
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

        lexer.getNextToken()
        val stringToken2 = lexer.getNextToken()
        assertEquals(TokenType.STRING_LITERAL, stringToken2?.type)
        assertEquals("single quote", stringToken2?.value)
    }

    @Test
    fun `test identifiers and numbers`() {
        val input = "let const 123 45.67 identifier"
        val lexer = lexerFromInput(input)

        val expectedTokens =
            listOf(
                TokenType.LET,
                TokenType.WHITESPACE,
                TokenType.CONST,
                TokenType.WHITESPACE,
                TokenType.NUMERIC_LITERAL,
                TokenType.WHITESPACE,
                TokenType.NUMERIC_LITERAL,
                TokenType.WHITESPACE,
                TokenType.IDENTIFIER,
            )

        for (expectedType in expectedTokens) {
            val token = lexer.getNextToken()
            assertEquals(expectedType, token?.type)
        }
    }

    @Test
    fun `test if else`() {
        val input = "if else"
        val lexer = lexerFromInput(input)

        val expectedTokens = listOf(TokenType.IF, TokenType.WHITESPACE, TokenType.ELSE)

        for (expectedType in expectedTokens) {
            val token = lexer.getNextToken()
            assertEquals(expectedType, token?.type)
        }
    }

    @Test
    fun `test if else with body`() {
        val input = "if (true) { println(\"hello\") } else { println(\"world\") }"
        val lexer = lexerFromInput(input)

        val expectedTokens =
            listOf(
                TokenType.IF,
                TokenType.WHITESPACE,
                TokenType.OPEN_PARENTHESIS,
                TokenType.BOOLEAN_LITERAL,
                TokenType.CLOSE_PARENTHESIS,
                TokenType.WHITESPACE,
                TokenType.OPEN_BRACKET,
                TokenType.WHITESPACE,
                TokenType.PRINTLN,
                TokenType.OPEN_PARENTHESIS,
                TokenType.STRING_LITERAL,
                TokenType.CLOSE_PARENTHESIS,
                TokenType.WHITESPACE,
                TokenType.CLOSE_BRACKET,
                TokenType.WHITESPACE,
                TokenType.ELSE,
                TokenType.WHITESPACE,
                TokenType.OPEN_BRACKET,
                TokenType.WHITESPACE,
                TokenType.PRINTLN,
                TokenType.OPEN_PARENTHESIS,
                TokenType.STRING_LITERAL,
                TokenType.CLOSE_PARENTHESIS,
                TokenType.WHITESPACE,
                TokenType.CLOSE_BRACKET,
            )

        for (expectedType in expectedTokens) {
            val token = lexer.getNextToken()
            assertEquals(expectedType, token?.type)
        }
    }

    @Test
    fun `test readInput readEnv`() {
        val input = "readInput readEnv"
        val lexer = lexerFromInput(input)

        val expectedTokens = listOf(TokenType.READINPUT, TokenType.WHITESPACE, TokenType.READENV)

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
                TokenType.WHITESPACE,
                TokenType.PUBLIC,
                TokenType.WHITESPACE,
                TokenType.PRIVATE,
                TokenType.WHITESPACE,
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
                TokenType.WHITESPACE,
                TokenType.NUMBER_TYPE,
                TokenType.WHITESPACE,
                TokenType.BOOLEAN_TYPE,
            )

        for (expectedType in expectedTokens) {
            val token = lexer.getNextToken()
            assertEquals(expectedType, token?.type)
        }
    }

    @Test
    fun `test boolean literals`() {
        val input = "true false"
        val lexer = lexerFromInput(input)

        val expectedTokens = listOf(TokenType.BOOLEAN_LITERAL, TokenType.WHITESPACE, TokenType.BOOLEAN_LITERAL)

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

        val expectedTokens = listOf(TokenType.NUMERIC_LITERAL, TokenType.WHITESPACE, TokenType.NUMERIC_LITERAL)

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
                TokenType.WHITESPACE,
                TokenType.LESSER_THAN,
                TokenType.WHITESPACE,
                TokenType.IDENTIFIER,
                TokenType.WHITESPACE,
                TokenType.GREATER_THAN,
                TokenType.WHITESPACE,
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
                TokenType.WHITESPACE,
                TokenType.LESSER_THAN_EQUAL,
                TokenType.WHITESPACE,
                TokenType.IDENTIFIER,
                TokenType.WHITESPACE,
                TokenType.GREATER_THAN_EQUAL,
                TokenType.WHITESPACE,
                TokenType.IDENTIFIER,
            )

        for (expectedType in expectedTokens) {
            val token = lexer.getNextToken()
            assertEquals(expectedType, token?.type)
        }
    }

    @Test
    fun `test with if something`() {
        val input =
            "let something: boolean = true;\n" +
                "if (something)\n" +
                "{\n" +
                "  println(\"Entered if\");\n" +
                "}\n"

        val lexer = lexerFromInput(input)
        val expectedTokens =
            listOf(
                TokenType.LET,
                TokenType.WHITESPACE,
                TokenType.IDENTIFIER,
                TokenType.COLON,
                TokenType.WHITESPACE,
                TokenType.BOOLEAN_TYPE,
                TokenType.WHITESPACE,
                TokenType.EQUALS,
                TokenType.WHITESPACE,
                TokenType.BOOLEAN_LITERAL,
                TokenType.SEMICOLON,
                TokenType.WHITESPACE,
                TokenType.IF,
                TokenType.WHITESPACE,
                TokenType.OPEN_PARENTHESIS,
                TokenType.IDENTIFIER,
                TokenType.CLOSE_PARENTHESIS,
                TokenType.WHITESPACE,
                TokenType.OPEN_BRACKET,
                TokenType.WHITESPACE,
                TokenType.PRINTLN,
                TokenType.OPEN_PARENTHESIS,
                TokenType.STRING_LITERAL,
                TokenType.CLOSE_PARENTHESIS,
                TokenType.SEMICOLON,
                TokenType.WHITESPACE,
                TokenType.CLOSE_BRACKET,
            )

        for (expectedType in expectedTokens) {
            val token = lexer.getNextToken()
            assertEquals(expectedType, token?.type)
        }
    }
}
