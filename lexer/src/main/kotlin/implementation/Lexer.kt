package implementation

import token.Token
import token.TokenType
import java.io.InputStream
import java.nio.charset.StandardCharsets

class Lexer(inputStream: InputStream) {
    private var position: Int = 0 // position in the input
    private var lineNumber: Int = 1
    private var input: String = ""

    init {
        input = inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
    }

    fun convertToToken(): List<Token> {
        val tokens = mutableListOf<Token>()

        while (position < input.length) {
            when (val currentChar = input[position]) {
                ' ', '\t', '\n', '\r' -> {
                    tokens.add(Token(TokenType.WHITESPACE, currentChar.toString(), lineNumber, position + 1))
                    position++
                }
                '+' -> {
                    tokens.add(Token(TokenType.PLUS, currentChar.toString(), lineNumber, position + 1))
                    position++
                }
                '-' -> {
                    tokens.add(Token(TokenType.MINUS, currentChar.toString(), lineNumber, position + 1))
                    position++
                }
                '*' -> {
                    tokens.add(Token(TokenType.TIMES, currentChar.toString(), lineNumber, position + 1))
                    position++
                }
                '/' -> {
                    tokens.add(Token(TokenType.DIVIDE, currentChar.toString(), lineNumber, position + 1))
                    position++
                }
                '=' -> {
                    tokens.add(Token(TokenType.EQUALS, currentChar.toString(), lineNumber, position + 1))
                    position++
                }
                ';' -> {
                    tokens.add(Token(TokenType.SEMICOLON, currentChar.toString(), lineNumber, position + 1))
                    position++
                }
                ':', ',' -> {
                    tokens.add(Token(TokenType.COLON, currentChar.toString(), lineNumber, position + 1))
                    position++
                }
                '>' -> {
                    tokens.add(Token(TokenType.GREATER_THAN, currentChar.toString(), lineNumber, position + 1))
                    position++
                }
                '<' -> {
                    tokens.add(Token(TokenType.LESSER_THAN, currentChar.toString(), lineNumber, position + 1))
                    position++
                }
                '(' -> {
                    tokens.add(Token(TokenType.OPEN_PARENTHESIS, currentChar.toString(), lineNumber, position + 1))
                    position++
                }
                ')' -> {
                    tokens.add(Token(TokenType.CLOSE_PARENTHESIS, currentChar.toString(), lineNumber, position + 1))
                    position++
                }
                '{' -> {
                    tokens.add(Token(TokenType.OPEN_BRACKET, currentChar.toString(), lineNumber, position + 1))
                    position++
                }
                '}' -> {
                    tokens.add(Token(TokenType.CLOSE_BRACKET, currentChar.toString(), lineNumber, position + 1))
                    position++
                }
                '"', '\'' -> { // this checks for string literals
                    val start = position
                    position++ // moves past the opening quote
                    while (position < input.length && input[position] != currentChar) {
                        position++
                    }
                    val stringLiteral = input.substring(start + 1, position) // removes the quotes
                    tokens.add(Token(TokenType.STRING_LITERAL, stringLiteral, lineNumber, start + 1))
                    position++ // moves past the closing quote
                }
                else -> {
                    if (currentChar.isLetter()) {
                        val start = position
                        while (position < input.length && input[position].isLetterOrDigit()) {
                            position++
                        }
                        when (val word = input.substring(start, position)) {
                            "let" -> tokens.add(Token(TokenType.LET, word, lineNumber, start + 1))
                            "const" -> tokens.add(Token(TokenType.CONST, word, lineNumber, start + 1))
                            "println" -> tokens.add(Token(TokenType.PRINTLN, word, lineNumber, start + 1))
                            "if" -> tokens.add(Token(TokenType.IF, word, lineNumber, start + 1))
                            "else" -> tokens.add(Token(TokenType.ELSE, word, lineNumber, start + 1))
                            "while" -> tokens.add(Token(TokenType.WHILE, word, lineNumber, start + 1))
                            "return" -> tokens.add(Token(TokenType.RETURN, word, lineNumber, start + 1))
                            "final" -> tokens.add(Token(TokenType.FINAL, word, lineNumber, start + 1))
                            "public" -> tokens.add(Token(TokenType.PUBLIC, word, lineNumber, start + 1))
                            "private" -> tokens.add(Token(TokenType.PRIVATE, word, lineNumber, start + 1))
                            "protected" -> tokens.add(Token(TokenType.PROTECTED, word, lineNumber, start + 1))
                            "String" -> tokens.add(Token(TokenType.STRING_TYPE, word, lineNumber, start + 1))
                            "number" -> tokens.add(Token(TokenType.NUMBER_TYPE, word, lineNumber, start + 1))
                            "Boolean" -> tokens.add(Token(TokenType.BOOLEAN_TYPE, word, lineNumber, start + 1))
                            "true" -> tokens.add(Token(TokenType.BOOLEAN_LITERAL, word, lineNumber, start + 1))
                            "false" -> tokens.add(Token(TokenType.BOOLEAN_LITERAL, word, lineNumber, start + 1))
                            "<=" -> tokens.add(Token(TokenType.LESSER_THAN_EQUAL, word, lineNumber, start + 1))
                            ">=" -> tokens.add(Token(TokenType.GREATER_THAN_EQUAL, word, lineNumber, start + 1))
                            "==" -> tokens.add(Token(TokenType.EQUAL_EQUAL, word, lineNumber, start + 1))
                            else -> tokens.add(Token(TokenType.IDENTIFIER, word, lineNumber, start + 1))
                        }
                    } else if (currentChar.isDigit()) {
                        val start = position
                        while (position < input.length && input[position].isDigit()) {
                            position++
                        }
                        tokens.add(Token(TokenType.NUMERIC_LITERAL, input.substring(start, position), lineNumber, start + 1))
                    } else {
                        position++
                    }
                }
            }
        }
        return tokens
    }
}
