package token.factory

import token.Token
import token.TokenType

interface TokenFactory {
    fun createToken(
        type: TokenType,
        value: String,
        position: Int,
        lineNumber: Int,
    ): Token
}

class ConcreteTokenFactory : TokenFactory {
    override fun createToken(
        type: TokenType,
        value: String,
        position: Int,
        lineNumber: Int,
    ): Token {
        return Token(type, value, position, lineNumber)
    }
}
