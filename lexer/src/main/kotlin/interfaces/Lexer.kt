package interfaces

import token.Token

interface Lexer {
    fun getNextToken(): Token?
}
