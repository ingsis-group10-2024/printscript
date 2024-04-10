package lexer

import common.token.TokenType
import implementation.Lexer
import org.junit.Test
import java.io.File
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class LexerTest {
    @Test
    fun `test lexer with simple file`() {
        // Create a temporary file with some simple content
        val tempFile = Files.createTempFile("test", ".txt").toFile()
        tempFile.writeText("let x = 10;")

        // Initialize the Lexer with the temporary file
        val lexer = Lexer(tempFile)

        // Convert the file content to tokens
        val tokens = lexer.convertToToken()

        // Assert that the tokens are as expected
        assertEquals(8, tokens.size)
        assertEquals(TokenType.LET, tokens[0].type)
        assertEquals(TokenType.WHITESPACE, tokens[1].type)
        assertEquals(TokenType.IDENTIFIER, tokens[2].type)
        assertEquals(TokenType.WHITESPACE, tokens[3].type)
        assertEquals(TokenType.EQUALS, tokens[4].type)
        assertEquals(TokenType.WHITESPACE, tokens[5].type)
        assertEquals(TokenType.NUMERIC_LITERAL, tokens[6].type)
        assertEquals(TokenType.SEMICOLON, tokens[7].type)

        // Clean up the temporary file
        tempFile.delete()
    }

    @Test
    fun `test lexer with large file`() {
        // Path to your large test file
        val largeFilePath2 = "src/test/resources/test2.pdf"
        val largeFile = File(largeFilePath2)

        if (!largeFile.exists()) {
            println("File does not exist: ${largeFile.absolutePath}")
        }

        // Initialize the Lexer with the large file
        val lexer = Lexer(largeFile)

        // Convert the file content to tokens
        val tokens = lexer.convertToToken()

        val whitespaceTokens = tokens.count { it.type == TokenType.WHITESPACE }
        println("Number of whitespace tokens: $whitespaceTokens")

        // Assert that the tokens are as expected
        // This is a simplified example; you might want to add more specific assertions
        // based on the content of your large test file.
        assertTrue(tokens.isNotEmpty(), "The token list should not be empty.")
        assertTrue(tokens.any { it.type == TokenType.IDENTIFIER }, "The token list should contain at least one identifier.")
        // assertTrue(tokens.any { it.type == TokenType.NUMERIC_LITERAL }, "The token list should contain at least one numeric literal.")
        // Add more assertions as needed
    }
}
