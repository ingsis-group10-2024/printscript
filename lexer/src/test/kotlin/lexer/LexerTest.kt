package lexer

import implementation.LexerSingleton
import implementation.MockInputStream
import org.junit.Assert.assertEquals
import org.junit.Test
import token.TokenType
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class LexerTest {
    @Test
    fun testBasicArithmetic() {
        val input = "+ - * /"
        val lexer = LexerSingleton.getInstance(input.byteInputStream())
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
    fun `test read input`() {
        val input = "readInput();"
        val lexer = LexerSingleton.getInstance(input.byteInputStream())
        val tokens = lexer.getToken()
        println(tokens)
        assertEquals(4, tokens.size)
        assertEquals(TokenType.READINPUT, tokens[0].type)
        assertEquals(TokenType.OPEN_PARENTHESIS, tokens[1].type)
        assertEquals(TokenType.CLOSE_PARENTHESIS, tokens[2].type)
        assertEquals(TokenType.SEMICOLON, tokens[3].type)
    }

    @Test
    fun `test read env`() {
        val input = "readEnv();"
        val lexer = LexerSingleton.getInstance(input.byteInputStream())
        val tokens = lexer.getToken()
        println(tokens)
        assertEquals(4, tokens.size)
        assertEquals(TokenType.READENV, tokens[0].type)
        assertEquals(TokenType.OPEN_PARENTHESIS, tokens[1].type)
        assertEquals(TokenType.CLOSE_PARENTHESIS, tokens[2].type)
        assertEquals(TokenType.SEMICOLON, tokens[3].type)
    }

    @Test
    fun `test convertToToken with small input`() {
        val input =
            "let x = 10;\n" +
                "let y = 20;"
        val inputStream = ByteArrayInputStream(input.toByteArray(StandardCharsets.UTF_8))
        val lexer = LexerSingleton.getInstance(inputStream)
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
        val lexer = LexerSingleton.getInstance(inputStream)

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
        assertEquals(8000, actualTokens.size)
    }

    @Test
    fun `test lexer with large file`() {
        val numberOfLines: Int = 32 * 1024
        val message = "This is a text;"
        val line = "println(\"$message\")"
        val mockInputStream = MockInputStream(line, numberOfLines)

        // Initialize the Lexer
        val lexer = LexerSingleton.getInstance(mockInputStream)

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

    @Test
    fun `test lexer with if else`() {
        val input = "if (x > 10) { println(\"Hello\") } else { println(\"World\") }"
        val lexer = LexerSingleton.getInstance(input.byteInputStream())
        val tokens = lexer.getToken()
        for (token in tokens) {
            println(token)
        }
        assertEquals(29, tokens.size)
        assertEquals(TokenType.IF, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.OPEN_PARENTHESIS, tokens[2].type)
        assertEquals(TokenType.IDENTIFIER, tokens[3].type)
        assertEquals(TokenType.WHITESPACE, tokens[4].type)
        assertEquals(TokenType.GREATER_THAN, tokens[5].type)
        assertEquals(TokenType.WHITESPACE, tokens[6].type)
        assertEquals(TokenType.NUMERIC_LITERAL, tokens[7].type)
        assertEquals(TokenType.CLOSE_PARENTHESIS, tokens[8].type)
        assertEquals(TokenType.WHITESPACE, tokens[9].type)
        assertEquals(TokenType.OPEN_BRACKET, tokens[10].type)
        assertEquals(TokenType.WHITESPACE, tokens[11].type)
        assertEquals(TokenType.PRINTLN, tokens[12].type)
        assertEquals(TokenType.OPEN_PARENTHESIS, tokens[13].type)
        assertEquals(TokenType.STRING_LITERAL, tokens[14].type)
        assertEquals(TokenType.CLOSE_PARENTHESIS, tokens[15].type)
        assertEquals(TokenType.WHITESPACE, tokens[16].type)
        assertEquals(TokenType.CLOSE_BRACKET, tokens[17].type)
        assertEquals(TokenType.WHITESPACE, tokens[18].type)
        assertEquals(TokenType.ELSE, tokens[19].type)
        assertEquals(TokenType.WHITESPACE, tokens[20].type)
        assertEquals(TokenType.OPEN_BRACKET, tokens[21].type)
        assertEquals(TokenType.WHITESPACE, tokens[22].type)
        assertEquals(TokenType.PRINTLN, tokens[23].type)
        assertEquals(TokenType.OPEN_PARENTHESIS, tokens[24].type)
        assertEquals(TokenType.STRING_LITERAL, tokens[25].type)
        assertEquals(TokenType.CLOSE_PARENTHESIS, tokens[26].type)
        assertEquals(TokenType.WHITESPACE, tokens[27].type)
        assertEquals(TokenType.CLOSE_BRACKET, tokens[28].type)
    }

    @Test
    fun `test lexer with constants`() {
        val input = "const x = 10;"
        val lexer = LexerSingleton.getInstance(input.byteInputStream())
        val tokens = lexer.getToken()
        for (token in tokens) {
            println(token)
        }
        assertEquals(8, tokens.size)
        assertEquals(TokenType.CONST, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.IDENTIFIER, tokens[2].type)
        assertEquals(TokenType.WHITESPACE, tokens[3].type)
        assertEquals(TokenType.EQUALS, tokens[4].type)
        assertEquals(TokenType.WHITESPACE, tokens[5].type)
        assertEquals(TokenType.NUMERIC_LITERAL, tokens[6].type)
        assertEquals(TokenType.SEMICOLON, tokens[7].type)
    }

    @Test
    fun `test lexer with while loop`() {
        val input = "while (x < 10) { println(\"Hello\") }"
        val lexer = LexerSingleton.getInstance(input.byteInputStream())
        val tokens = lexer.getToken()
        for (token in tokens) {
            println(token)
        }
        assertEquals(18, tokens.size)
        assertEquals(TokenType.WHILE, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.OPEN_PARENTHESIS, tokens[2].type)
        assertEquals(TokenType.IDENTIFIER, tokens[3].type)
        assertEquals(TokenType.WHITESPACE, tokens[4].type)
        assertEquals(TokenType.LESSER_THAN, tokens[5].type)
        assertEquals(TokenType.WHITESPACE, tokens[6].type)
        assertEquals(TokenType.NUMERIC_LITERAL, tokens[7].type)
        assertEquals(TokenType.CLOSE_PARENTHESIS, tokens[8].type)
        assertEquals(TokenType.WHITESPACE, tokens[9].type)
        assertEquals(TokenType.OPEN_BRACKET, tokens[10].type)
        assertEquals(TokenType.WHITESPACE, tokens[11].type)
        assertEquals(TokenType.PRINTLN, tokens[12].type)
        assertEquals(TokenType.OPEN_PARENTHESIS, tokens[13].type)
        assertEquals(TokenType.STRING_LITERAL, tokens[14].type)
        assertEquals(TokenType.CLOSE_PARENTHESIS, tokens[15].type)
        assertEquals(TokenType.WHITESPACE, tokens[16].type)
        assertEquals(TokenType.CLOSE_BRACKET, tokens[17].type)
    }

    @Test
    fun `test lexer with return statement`() {
        val input = "return 10;"
        val lexer = LexerSingleton.getInstance(input.byteInputStream())
        val tokens = lexer.getToken()
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
        val lexer = LexerSingleton.getInstance(input.byteInputStream())
        val tokens = lexer.getToken()
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
        val lexer = LexerSingleton.getInstance(input.byteInputStream())
        val tokens = lexer.getToken()
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
        val lexer = LexerSingleton.getInstance(input.byteInputStream())
        val tokens = lexer.getToken()
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
        val lexer = LexerSingleton.getInstance(input.byteInputStream())
        val tokens = lexer.getToken()
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
        val input = "String x = \"Hello\";"
        val lexer = LexerSingleton.getInstance(input.byteInputStream())
        val tokens = lexer.getToken()
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
        val lexer = LexerSingleton.getInstance(input.byteInputStream())
        val tokens = lexer.getToken()
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
        val input = "Boolean x = true;"
        val lexer = LexerSingleton.getInstance(input.byteInputStream())
        val tokens = lexer.getToken()
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
        val lexer = LexerSingleton.getInstance(input.byteInputStream())
        val tokens = lexer.getToken()
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
        val lexer = LexerSingleton.getInstance(input.byteInputStream())
        val tokens = lexer.getToken()
        for (token in tokens) {
            println(token)
        }
        assertEquals(11, tokens.size)
        assertEquals(TokenType.EQUAL_EQUAL, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.UNEQUAL, tokens[2].type)
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
