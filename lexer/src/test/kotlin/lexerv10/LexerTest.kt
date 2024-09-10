package lexerv10

import controller.LexerVersionController
import implementation.MockInputStream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import token.TokenType
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class LexerTest {
    private val versionController = LexerVersionController()

    @Test
    fun testBasicArithmetic() {
        val input = "+ - * /"
        val inputStream = ByteArrayInputStream(input.toByteArray(StandardCharsets.UTF_8))
        val lexer = versionController.getLexer("1.0", inputStream)
        val tokens = lexer.getTokens()

        // Definimos los tokens esperados
        val expectedTokens =
            listOf(
                TokenType.PLUS,
                TokenType.WHITESPACE,
                TokenType.MINUS,
                TokenType.WHITESPACE,
                TokenType.MULTIPLY,
                TokenType.WHITESPACE,
                TokenType.DIVIDE,
            )

        // Verificamos que los tipos de tokens generados son los esperados
        assertEquals(expectedTokens.size, tokens.size)
        expectedTokens.forEachIndexed { index, expectedType ->
            assertEquals(expectedType, tokens[index].type)
        }
    }

    @Test
    fun `test lexer with large file`() {
        val numberOfLines: Int = 32 * 1024
        val message = "This is a text;"
        val line = "println(\"$message\")"
        val mockInputStream = MockInputStream(line, numberOfLines)

        // Initialize the Lexer
        val lexer = versionController.getLexer("1.0", mockInputStream)

        // Convert the content to tokens
        val tokens = lexer.getTokens()

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

    @Test
    fun `test convertToToken with small input`() {
        val input =
            "let x = 10;\n" +
                "let y = 20;"
        val inputStream = ByteArrayInputStream(input.toByteArray(StandardCharsets.UTF_8))
        val lexer = versionController.getLexer("1.0", inputStream)
        val actualTokens = lexer.getTokens()
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
                    append("let x = 10;") // 9 tokens
                }
            }
        val inputStream = ByteArrayInputStream(input.toByteArray(StandardCharsets.UTF_8))
        val lexer = versionController.getLexer("1.0", inputStream)

        val actualTokens = lexer.getTokens()

        println(actualTokens.size)

        assertEquals(TokenType.LET, actualTokens[0].type)
        assertEquals(TokenType.WHITESPACE, actualTokens[1].type)
        assertEquals(TokenType.IDENTIFIER, actualTokens[2].type)
        assertEquals(TokenType.WHITESPACE, actualTokens[3].type)
        assertEquals(TokenType.EQUALS, actualTokens[4].type)
        assertEquals(TokenType.WHITESPACE, actualTokens[5].type)
        assertEquals(TokenType.NUMERIC_LITERAL, actualTokens[6].type)
        assertEquals(TokenType.SEMICOLON, actualTokens[7].type)
        assertEquals(8000, actualTokens.size)
    }

    @Test
    fun `test lexer with return statement`() {
        val input = "return 10;"
        val lexer = versionController.getLexer("1.0", input.byteInputStream())
        val tokens = lexer.getTokens()
        for (token in tokens) {
            println(token)
        }
        assertEquals(4, tokens.size)
        assertEquals(TokenType.RETURN, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.NUMERIC_LITERAL, tokens[2].type)
        assertEquals(TokenType.SEMICOLON, tokens[3].type)
    }

    @Test
    fun `test lexer with final keyword`() {
        val input = "final x = 10;"
        val lexer = versionController.getLexer("1.0", input.byteInputStream())
        val tokens = lexer.getTokens()
        for (token in tokens) {
            println(token)
        }
        assertEquals(8, tokens.size)
        assertEquals(TokenType.FINAL, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.IDENTIFIER, tokens[2].type)
        assertEquals(TokenType.WHITESPACE, tokens[3].type)
        assertEquals(TokenType.EQUALS, tokens[4].type)
        assertEquals(TokenType.WHITESPACE, tokens[5].type)
        assertEquals(TokenType.NUMERIC_LITERAL, tokens[6].type)
        assertEquals(TokenType.SEMICOLON, tokens[7].type)
    }

    @Test
    fun `test lexer with public keyword`() {
        val input = "public x = 10;"
        val lexer = versionController.getLexer("1.0", input.byteInputStream())
        val tokens = lexer.getTokens()
        for (token in tokens) {
            println(token)
        }
        assertEquals(8, tokens.size)
        assertEquals(TokenType.PUBLIC, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.IDENTIFIER, tokens[2].type)
        assertEquals(TokenType.WHITESPACE, tokens[3].type)
        assertEquals(TokenType.EQUALS, tokens[4].type)
        assertEquals(TokenType.WHITESPACE, tokens[5].type)
        assertEquals(TokenType.NUMERIC_LITERAL, tokens[6].type)
        assertEquals(TokenType.SEMICOLON, tokens[7].type)
    }

    @Test
    fun `test lexer with private keyword`() {
        val input = "private x = 10;"
        val lexer = versionController.getLexer("1.0", input.byteInputStream())
        val tokens = lexer.getTokens()
        for (token in tokens) {
            println(token)
        }
        assertEquals(8, tokens.size)
        assertEquals(TokenType.PRIVATE, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.IDENTIFIER, tokens[2].type)
        assertEquals(TokenType.WHITESPACE, tokens[3].type)
        assertEquals(TokenType.EQUALS, tokens[4].type)
        assertEquals(TokenType.WHITESPACE, tokens[5].type)
        assertEquals(TokenType.NUMERIC_LITERAL, tokens[6].type)
        assertEquals(TokenType.SEMICOLON, tokens[7].type)
    }

    @Test
    fun `test lexer with protected keyword`() {
        val input = "protected x = 10;"
        val lexer = versionController.getLexer("1.0", input.byteInputStream())
        val tokens = lexer.getTokens()
        for (token in tokens) {
            println(token)
        }
        assertEquals(8, tokens.size)
        assertEquals(TokenType.PROTECTED, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.IDENTIFIER, tokens[2].type)
        assertEquals(TokenType.WHITESPACE, tokens[3].type)
        assertEquals(TokenType.EQUALS, tokens[4].type)
        assertEquals(TokenType.WHITESPACE, tokens[5].type)
        assertEquals(TokenType.NUMERIC_LITERAL, tokens[6].type)
        assertEquals(TokenType.SEMICOLON, tokens[7].type)
    }

    @Test
    fun `test lexer with string type`() {
        val input = "string x = \"Hello\";"
        val lexer = versionController.getLexer("1.0", input.byteInputStream())
        val tokens = lexer.getTokens()
        for (token in tokens) {
            println(token)
        }
        assertEquals(8, tokens.size)
        assertEquals(TokenType.STRING_TYPE, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.IDENTIFIER, tokens[2].type)
        assertEquals(TokenType.WHITESPACE, tokens[3].type)
        assertEquals(TokenType.EQUALS, tokens[4].type)
        assertEquals(TokenType.WHITESPACE, tokens[5].type)
        assertEquals(TokenType.STRING_LITERAL, tokens[6].type)
        assertEquals(TokenType.SEMICOLON, tokens[7].type)
    }

    @Test
    fun `test lexer with number type`() {
        val input = "number x = 10;"
        val lexer = versionController.getLexer("1.0", input.byteInputStream())
        val tokens = lexer.getTokens()
        for (token in tokens) {
            println(token)
        }
        assertEquals(8, tokens.size)
        assertEquals(TokenType.NUMBER_TYPE, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.IDENTIFIER, tokens[2].type)
        assertEquals(TokenType.WHITESPACE, tokens[3].type)
        assertEquals(TokenType.EQUALS, tokens[4].type)
        assertEquals(TokenType.WHITESPACE, tokens[5].type)
        assertEquals(TokenType.NUMERIC_LITERAL, tokens[6].type)
        assertEquals(TokenType.SEMICOLON, tokens[7].type)
    }

    @Test
    fun `test lexer with boolean type`() {
        val input = "boolean x = true;"
        val lexer = versionController.getLexer("1.0", input.byteInputStream())
        val tokens = lexer.getTokens()
        for (token in tokens) {
            println(token)
        }
        assertEquals(8, tokens.size)
        assertEquals(TokenType.BOOLEAN_TYPE, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.IDENTIFIER, tokens[2].type)
        assertEquals(TokenType.WHITESPACE, tokens[3].type)
        assertEquals(TokenType.EQUALS, tokens[4].type)
        assertEquals(TokenType.WHITESPACE, tokens[5].type)
        assertEquals(TokenType.BOOLEAN_LITERAL, tokens[6].type)
        assertEquals(TokenType.SEMICOLON, tokens[7].type)
    }

    @Test
    fun `test lexer with boolean literals`() {
        val input = "true false"
        val lexer = versionController.getLexer("1.0", input.byteInputStream())
        val tokens = lexer.getTokens()
        for (token in tokens) {
            println(token)
        }
        assertEquals(3, tokens.size)
        assertEquals(TokenType.BOOLEAN_LITERAL, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.BOOLEAN_LITERAL, tokens[2].type)
    }

    @Test
    fun `test lexer with operators`() {
        val input = "== != > >= < <="
        val lexer = versionController.getLexer("1.0", input.byteInputStream())
        val tokens = lexer.getTokens()
        for (token in tokens) {
            println(token)
        }
        assertEquals(11, tokens.size)
        assertEquals(TokenType.EQUALS_EQUALS, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.UNEQUALS, tokens[2].type)
        assertEquals(TokenType.WHITESPACE, tokens[3].type)
        assertEquals(TokenType.GREATER_THAN, tokens[4].type)
        assertEquals(TokenType.WHITESPACE, tokens[5].type)
        assertEquals(TokenType.GREATER_THAN_EQUAL, tokens[6].type)
        assertEquals(TokenType.WHITESPACE, tokens[7].type)
        assertEquals(TokenType.LESSER_THAN, tokens[8].type)
        assertEquals(TokenType.WHITESPACE, tokens[9].type)
        assertEquals(TokenType.LESSER_THAN_EQUAL, tokens[10].type)
    }
}
