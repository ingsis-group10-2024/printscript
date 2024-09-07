package interfaces

import token.Token

interface Lexer {
    fun getToken(): List<Token>
}
