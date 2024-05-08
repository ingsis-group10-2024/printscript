package implementation

import token.Token
import java.io.InputStream
import java.nio.charset.StandardCharsets

class Lexer(inputStream: InputStream, private val maxReadSize: Int = 4096) {
    private var position: Int = 0 // position in the input
    private var lineNumber: Int = 1
    private var input: String =
        inputStream.bufferedReader(StandardCharsets.UTF_8).use {
            it.readText().take(maxReadSize) // Limiting input to maxReadSize
        }

    fun convertToToken(): List<Token> {
        val tokens = mutableListOf<Token>()

        while (position < input.length) {
            when (val currentChar = input[position]) {
                ' ', '\t', '\n' -> {
                    tokens.add(Token(TokenType.WHITESPACE, currentChar.toString(), position + 1, lineNumber))
                    position++
                    if (currentChar == '\n') {
                        lineNumber++
                    }
                }
                '+' -> {
                    tokens.add(Token(TokenType.PLUS, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '-' -> {
                    tokens.add(Token(TokenType.MINUS, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '*' -> {
                    tokens.add(Token(TokenType.TIMES, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '/' -> {
                    tokens.add(Token(TokenType.DIVIDE, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '=' -> {
                    tokens.add(Token(TokenType.EQUALS, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                ';' -> {
                    tokens.add(Token(TokenType.SEMICOLON, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                ':', ',' -> {
                    tokens.add(Token(TokenType.COLON, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '>' -> {
                    tokens.add(Token(TokenType.GREATER_THAN, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '<' -> {
                    tokens.add(Token(TokenType.LESSER_THAN, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '(' -> {
                    tokens.add(Token(TokenType.OPEN_PARENTHESIS, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                ')' -> {
                    tokens.add(Token(TokenType.CLOSE_PARENTHESIS, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '{' -> {
                    tokens.add(Token(TokenType.OPEN_BRACKET, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '}' -> {
                    tokens.add(Token(TokenType.CLOSE_BRACKET, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '"', '\'' -> { // this checks for string literals
                    val start = position
                    position++ // moves past the opening quote
                    while (position < input.length && input[position] != currentChar) {
                        position++
                    }
                    val stringLiteral = input.substring(start + 1, position) // removes the quotes
                    tokens.add(Token(TokenType.STRING_LITERAL, stringLiteral, start + 1, lineNumber))
                    position++ // moves past the closing quote
                }
                else -> {
                    if (currentChar.isLetter()) {
                        val start = position
                        while (position < input.length && input[position].isLetterOrDigit()) {
                            position++
                        }
                        when (val word = input.substring(start, position)) {
                            "let" -> tokens.add(Token(TokenType.LET, word, start + 1, lineNumber))
                            "println" -> tokens.add(Token(TokenType.PRINTLN, word, start + 1, lineNumber))
                            "while" -> tokens.add(Token(TokenType.WHILE, word, start + 1, lineNumber))
                            "return" -> tokens.add(Token(TokenType.RETURN, word, start + 1, lineNumber))
                            "final" -> tokens.add(Token(TokenType.FINAL, word, start + 1, lineNumber))
                            "public" -> tokens.add(Token(TokenType.PUBLIC, word, start + 1, lineNumber))
                            "private" -> tokens.add(Token(TokenType.PRIVATE, word, start + 1, lineNumber))
                            "protected" -> tokens.add(Token(TokenType.PROTECTED, word, start + 1, lineNumber))
                            "String" -> tokens.add(Token(TokenType.STRING_TYPE, word, start + 1, lineNumber))
                            "number" -> tokens.add(Token(TokenType.NUMBER_TYPE, word, start + 1, lineNumber))
                            "<=" -> tokens.add(Token(TokenType.LESSER_THAN_EQUAL, word, start + 1, lineNumber))
                            ">=" -> tokens.add(Token(TokenType.GREATER_THAN_EQUAL, word, start + 1, lineNumber))
                            "==" -> tokens.add(Token(TokenType.EQUAL_EQUAL, word, start + 1, lineNumber))
                            else -> tokens.add(Token(TokenType.IDENTIFIER, word, start + 1, lineNumber))
                        }
                    } else if (currentChar.isDigit()) {
                        val start = position
                        while (position < input.length && input[position].isDigit()) {
                            position++
                        }
                        tokens.add(Token(TokenType.NUMERIC_LITERAL, input.substring(start, position), start + 1, lineNumber))
                    } else {
                        position++
                    }
                }
            }
        }
        return tokens
    }
}
