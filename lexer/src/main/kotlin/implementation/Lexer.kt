package implementation

import TokenType
import token.Token
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets


class Lexer(inputStream: InputStream) {
    private var lineNumber: Int = 1
    private val tokens = mutableListOf<Token>()

    init {
        BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).useLines { lines ->
            lines.forEach { line ->
                processLine(line)
                lineNumber++
            }
        }
    }

    private fun processLine(line: String) {
        var position = 0 // posición en la línea
        // val tokens = mutableListOf<Token>()

        while (position < line.length) {
            when (val currentChar = line[position]) {
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
                    while (position < line.length && line[position] != currentChar) {
                        position++
                    }
                    val stringLiteral = line.substring(start + 1, position) // removes the quotes
                    tokens.add(Token(TokenType.STRING_LITERAL, stringLiteral, start + 1, lineNumber))
                    position++ // moves past the closing quote
                }
                else -> {
                    if (currentChar.isLetter()) {
                        val start = position
                        while (position < line.length && line[position].isLetterOrDigit()) {
                            position++
                        }
                        when (val word = line.substring(start, position)) {
                            "let" -> tokens.add(Token(TokenType.LET, word, start + 1, lineNumber))
                            "const" -> tokens.add(Token(TokenType.CONST, word, start + 1, lineNumber))
                            "println" -> tokens.add(Token(TokenType.PRINTLN, word, start + 1, lineNumber))
                            "if" -> tokens.add(Token(TokenType.IF, word, start + 1, lineNumber))
                            "else" -> tokens.add(Token(TokenType.ELSE, word, start + 1, lineNumber))
                            "while" -> tokens.add(Token(TokenType.WHILE, word, start + 1, lineNumber))
                            "return" -> tokens.add(Token(TokenType.RETURN, word, start + 1, lineNumber))
                            "final" -> tokens.add(Token(TokenType.FINAL, word, start + 1, lineNumber))
                            "public" -> tokens.add(Token(TokenType.PUBLIC, word, start + 1, lineNumber))
                            "private" -> tokens.add(Token(TokenType.PRIVATE, word, start + 1, lineNumber))
                            "protected" -> tokens.add(Token(TokenType.PROTECTED, word, start + 1, lineNumber))
                            "String" -> tokens.add(Token(TokenType.STRING_TYPE, word, start + 1, lineNumber))
                            "number" -> tokens.add(Token(TokenType.NUMBER_TYPE, word, start + 1, lineNumber))
                            "Boolean" -> tokens.add(Token(TokenType.BOOLEAN_TYPE, word, start + 1, lineNumber))
                            "true", "false" -> tokens.add(Token(TokenType.BOOLEAN_LITERAL, word, start + 1, lineNumber))
                            "<=" -> tokens.add(Token(TokenType.LESSER_THAN_EQUAL, word, start + 1, lineNumber))
                            ">=" -> tokens.add(Token(TokenType.GREATER_THAN_EQUAL, word, start + 1, lineNumber))
                            "==" -> tokens.add(Token(TokenType.EQUAL_EQUAL, word, start + 1, lineNumber))
                            else -> tokens.add(Token(TokenType.IDENTIFIER, word, start + 1, lineNumber))

                        }
                    } else if (currentChar.isDigit()) {
                        val start = position
                        while (position < line.length && line[position].isDigit()) {
                            position++
                        }
                        tokens.add(Token(TokenType.NUMERIC_LITERAL, line.substring(start, position), start + 1, lineNumber))
                    } else {
                        position++
                    }
                }
            }
        }
    }

    fun getToken(): List<Token> {
        return tokens
    }
}
