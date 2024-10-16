package implementation

import interfaces.Lexer
import token.Token
import token.TokenType
import token.factory.ConcreteTokenFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class LexerV10(inputStream: InputStream) : Lexer {
    private val reader: BufferedReader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
    private var currentChar: Char? = null
    private var lineNumber: Int = 1
    private var position: Int = 0
    private val tokenFactory = ConcreteTokenFactory()

    init {
        readNextChar() // Read the first character
    }

    private fun readNextChar(): Char? {
        currentChar = reader.read().takeIf { it != -1 }?.toChar()
        if (currentChar == '\n') {
            lineNumber++
            position = 0
        } else {
            position++
        }
        return currentChar
    }

    private fun processNextToken(): Token? {
        while (currentChar?.isWhitespace() == true) {
            val startPos = position
            val whitespace =
                buildString {
                    while (currentChar?.isWhitespace() == true) {
                        append(currentChar)
                        readNextChar()
                    }
                }
            if (currentChar == null) {
                // End of input, don't return a whitespace token
                return null
            }
        }

        currentChar ?: return null // End of input

        return when (currentChar) {
            '+' -> {
                readNextChar()
                tokenFactory.createToken(TokenType.PLUS, "+", position - 1, lineNumber)
            }
            '-' -> {
                readNextChar()
                tokenFactory.createToken(TokenType.MINUS, "-", position - 1, lineNumber)
            }
            '*' -> {
                readNextChar()
                tokenFactory.createToken(TokenType.MULTIPLY, "*", position - 1, lineNumber)
            }
            '/' -> {
                readNextChar()
                tokenFactory.createToken(TokenType.DIVIDE, "/", position - 1, lineNumber)
            }
            '=' -> {
                readNextChar()
                if (currentChar == '=') {
                    readNextChar()
                    tokenFactory.createToken(TokenType.EQUALS_EQUALS, "==", position - 2, lineNumber)
                } else {
                    tokenFactory.createToken(TokenType.EQUALS, "=", position - 1, lineNumber)
                }
            }
            '!' -> {
                readNextChar()
                if (currentChar == '=') {
                    readNextChar()
                    tokenFactory.createToken(TokenType.UNEQUALS, "!=", position - 2, lineNumber)
                } else {
                    throw IllegalArgumentException("Unexpected character '$currentChar' at line $lineNumber")
                }
            }
            ';' -> {
                val startPos = position
                readNextChar()
                tokenFactory.createToken(TokenType.SEMICOLON, ";", startPos, lineNumber)
            }
            ':' -> {
                readNextChar()
                tokenFactory.createToken(TokenType.COLON, ":", position - 1, lineNumber)
            }

            '>' -> {
                readNextChar()
                if (currentChar == '=') {
                    readNextChar()
                    tokenFactory.createToken(TokenType.GREATER_THAN_EQUAL, ">=", position - 2, lineNumber)
                } else {
                    tokenFactory.createToken(TokenType.GREATER_THAN, ">", position - 1, lineNumber)
                }
            }
            '<' -> {
                readNextChar()
                if (currentChar == '=') {
                    readNextChar()
                    tokenFactory.createToken(TokenType.LESSER_THAN_EQUAL, "<=", position - 2, lineNumber)
                } else {
                    tokenFactory.createToken(TokenType.LESSER_THAN, "<", position - 1, lineNumber)
                }
            }
            '(' -> {
                readNextChar()
                tokenFactory.createToken(TokenType.OPEN_PARENTHESIS, "(", position - 1, lineNumber)
            }
            ')' -> {
                readNextChar()
                tokenFactory.createToken(TokenType.CLOSE_PARENTHESIS, ")", position - 1, lineNumber)
            }
            '{' -> {
                readNextChar()
                tokenFactory.createToken(TokenType.OPEN_BRACKET, "{", position - 1, lineNumber)
            }
            '}' -> {
                readNextChar()
                tokenFactory.createToken(TokenType.CLOSE_BRACKET, "}", position - 1, lineNumber)
            }
            '"', '\'' -> { // Handle string literals
                val quote = currentChar!!
                readNextChar() // Move past the opening quote
                val start = position - 1
                val stringLiteral =
                    buildString {
                        while (currentChar != quote && currentChar != null) {
                            append(currentChar)
                            readNextChar()
                        }
                    }
                readNextChar() // Move past the closing quote
                tokenFactory.createToken(TokenType.STRING_LITERAL, stringLiteral, start, lineNumber)
            }
            else -> {
                if (currentChar!!.isLetter() || currentChar == '_') {
                    val start = position - 1
                    val identifier =
                        buildString {
                            while (currentChar != null && (currentChar!!.isLetterOrDigit() || currentChar == '_')) {
                                append(currentChar)
                                readNextChar()
                            }
                        }
                    val tokenType =
                        when (identifier) {
                            "let" -> TokenType.LET
                            "println" -> TokenType.PRINTLN
                            "return" -> TokenType.RETURN
                            "final" -> TokenType.FINAL
                            "public" -> TokenType.PUBLIC
                            "private" -> TokenType.PRIVATE
                            "protected" -> TokenType.PROTECTED
                            "string" -> TokenType.STRING_TYPE
                            "number" -> TokenType.NUMBER_TYPE
                            "boolean" -> TokenType.BOOLEAN_TYPE
                            "true", "false" -> TokenType.BOOLEAN_LITERAL
                            else -> TokenType.IDENTIFIER
                        }
                    tokenFactory.createToken(tokenType, identifier, start, lineNumber)
                } else if (currentChar!!.isDigit()) {
                    val start = position - 1
                    var hasDecimalPoint = false
                    val number =
                        buildString {
                            while (currentChar != null && currentChar!!.isDigit() || (currentChar == '.' && !hasDecimalPoint)) {
                                if (currentChar == '.') {
                                    hasDecimalPoint = true
                                }
                                append(currentChar)
                                readNextChar()
                            }
                        }
                    tokenFactory.createToken(
                        TokenType.NUMERIC_LITERAL,
                        number,
                        start,
                        lineNumber,
                    )
                } else {
                    throw IllegalArgumentException("Unknown character at line $lineNumber: '$currentChar'")
                }
            }
        }
    }

    override fun getNextToken(): Token? {
        return processNextToken()
    }
}
