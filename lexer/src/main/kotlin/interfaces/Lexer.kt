package interfaces

import token.Token

interface Lexer {
    fun getTokens(): List<Token>
    fun processLine(line: String)
}
