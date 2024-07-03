package implementation

import token.Token
import token.TokenType
import token.factory.ConcreteTokenFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object LexerSingleton {
    private var instance: Lexer? = null
    private var currentInputStream: InputStream? = null

    fun getInstance(inputStream: InputStream): Lexer {
        if (instance == null || inputStream != currentInputStream) {
            instance = Lexer(inputStream)
            currentInputStream = inputStream
        }
        return instance!!
    }
}

class Lexer(inputStream: InputStream) {
    private var lineNumber: Int = 1
    private val tokens = mutableListOf<Token>()
    private val tokenFactory = ConcreteTokenFactory()

    init {
        BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).useLines { lines ->
            lines.forEach { line ->
                processLine(line)
                lineNumber++
            }
        }
    }

    private fun processLine(line: String) {
        var position = 0

        while (position < line.length) {
            when (val currentChar = line[position]) {
                ' ', '\t', '\n' -> {
                    tokens.add(tokenFactory.createToken(TokenType.WHITESPACE, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '+' -> {
                    tokens.add(tokenFactory.createToken(TokenType.PLUS, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '-' -> {
                    tokens.add(tokenFactory.createToken(TokenType.MINUS, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '*' -> {
                    tokens.add(tokenFactory.createToken(TokenType.TIMES, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '/' -> {
                    tokens.add(tokenFactory.createToken(TokenType.DIVIDE, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '=' -> {
                    if (position + 1 < line.length && line[position + 1] == '=') {
                        tokens.add(tokenFactory.createToken(TokenType.EQUALS_EQUALS, "==", position + 1, lineNumber))
                        position += 2
                    } else {
                        tokens.add(tokenFactory.createToken(TokenType.EQUALS, currentChar.toString(), position + 1, lineNumber))
                        position++
                    }
                }
                '!' -> {
                    if (position + 1 < line.length && line[position + 1] == '=') {
                        tokens.add(tokenFactory.createToken(TokenType.UNEQUALS, "!=", position + 1, lineNumber))
                        position += 2
                    } else {
                        position++
                    }
                }
                ';' -> {
                    tokens.add(tokenFactory.createToken(TokenType.SEMICOLON, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                ':' -> {
                    tokens.add(tokenFactory.createToken(TokenType.COLON, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '>' -> {
                    if (position + 1 < line.length && line[position + 1] == '=') {
                        tokens.add(tokenFactory.createToken(TokenType.GREATER_THAN_EQUAL, ">=", position + 1, lineNumber))
                        position += 2
                    } else {
                        tokens.add(tokenFactory.createToken(TokenType.GREATER_THAN, currentChar.toString(), position + 1, lineNumber))
                        position++
                    }
                }
                '<' -> {
                    if (position + 1 < line.length && line[position + 1] == '=') {
                        tokens.add(tokenFactory.createToken(TokenType.LESSER_THAN_EQUAL, "<=", position + 1, lineNumber))
                        position += 2
                    } else {
                        tokens.add(tokenFactory.createToken(TokenType.LESSER_THAN, currentChar.toString(), position + 1, lineNumber))
                        position++
                    }
                }
                '(' -> {
                    tokens.add(tokenFactory.createToken(TokenType.OPEN_PARENTHESIS, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                ')' -> {
                    tokens.add(tokenFactory.createToken(TokenType.CLOSE_PARENTHESIS, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '{' -> {
                    tokens.add(tokenFactory.createToken(TokenType.OPEN_BRACKET, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '}' -> {
                    tokens.add(tokenFactory.createToken(TokenType.CLOSE_BRACKET, currentChar.toString(), position + 1, lineNumber))
                    position++
                }
                '"', '\'' -> { // this checks for string literals
                    val start = position
                    position++ // moves past the opening quote
                    while (position < line.length && line[position] != currentChar) {
                        position++
                    }
                    val stringLiteral = line.substring(start + 1, position) // removes the quotes
                    tokens.add(tokenFactory.createToken(TokenType.STRING_LITERAL, stringLiteral, start + 1, lineNumber))
                    position++ // moves past the closing quote
                }
                else -> {
                    if (currentChar.isLetter()) {
                        val start = position
                        while (position < line.length && line[position].isLetterOrDigit()) {
                            position++
                        }
                        val word = line.substring(start, position)
                        val tokenType =
                            when (word) {
                                "let" -> TokenType.LET
                                "const" -> TokenType.CONST
                                "println" -> TokenType.PRINTLN
                                "if" -> TokenType.IF
                                "else" -> TokenType.ELSE
                                "while" -> TokenType.WHILE
                                "return" -> TokenType.RETURN
                                "readInput" -> TokenType.READINPUT
                                "readEnv" -> TokenType.READENV
                                "final" -> TokenType.FINAL
                                "public" -> TokenType.PUBLIC
                                "private" -> TokenType.PRIVATE
                                "protected" -> TokenType.PROTECTED
                                "String" -> TokenType.STRING_TYPE
                                "number" -> TokenType.NUMBER_TYPE
                                "Boolean" -> TokenType.BOOLEAN_TYPE
                                "true", "false" -> TokenType.BOOLEAN_LITERAL
                                else -> TokenType.IDENTIFIER
                            }
                        tokens.add(tokenFactory.createToken(tokenType, word, start + 1, lineNumber))
                    } else if (currentChar.isDigit()) {
                        val start = position
                        while (position < line.length && line[position].isDigit()) {
                            position++
                        }
                        tokens.add(
                            tokenFactory.createToken(TokenType.NUMERIC_LITERAL, line.substring(start, position), start + 1, lineNumber),
                        )
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
