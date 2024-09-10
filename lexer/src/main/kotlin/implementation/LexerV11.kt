package implementation

import interfaces.Lexer
import token.Token
import token.TokenType
import token.factory.ConcreteTokenFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*

class LexerV11(inputStream: InputStream) : Lexer {
    private val reader: BufferedReader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
    private var lineNumber: Int = 1
    private var currentLine: String? = null
    private var position: Int = 0
    private val tokenFactory = ConcreteTokenFactory()
    private val tokens: Queue<Token> = LinkedList()

    init {
        // Inicializamos con la primera línea para empezar el procesamiento.
        currentLine = reader.readLine()
    }

    override fun getTokens(): List<Token> {
        // Procesa los tokens si no hay más tokens pendientes y aún hay líneas por leer.
        while (currentLine != null) {
            processLine(currentLine!!)
            currentLine = reader.readLine()
            position = 0
            lineNumber++
        }

        return tokens.toList()
    }

    override fun processLine(line: String) {
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
                    tokens.add(tokenFactory.createToken(TokenType.MULTIPLY, currentChar.toString(), position + 1, lineNumber))
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
                    if (currentChar.isLetter() || currentChar == '_') {
                        val start = position
                        while (position < line.length && (line[position].isLetterOrDigit() || line[position] == '_')) {
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
                                "return" -> TokenType.RETURN
                                "readInput" -> TokenType.READINPUT
                                "readEnv" -> TokenType.READENV
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
                        tokens.add(tokenFactory.createToken(tokenType, word, start + 1, lineNumber))
                    } else if (currentChar.isDigit()) {
                        val start = position
                        var hasDecimalPoint = false
                        while (position < line.length && line[position].isDigit() || (line[position] == '.' && !hasDecimalPoint)) {
                            if (line[position] == '.') {
                                hasDecimalPoint = true
                            }
                            position++
                        }
                        tokens.add(
                            tokenFactory.createToken(TokenType.NUMERIC_LITERAL, line.substring(start, position), start + 1, lineNumber),
                        )
                    } else {
                        throw IllegalArgumentException("Unknown character at line $lineNumber, position ${position + 1}")
                    }
                }
            }
        }
    }
}
