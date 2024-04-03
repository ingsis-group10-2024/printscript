package token

import common.token.TokenType

data class Token(
    val type: TokenType,
    val value: String,
    val position: Int,
    val lineNumber: Int,
) {
    constructor(type: TokenType, value: String, position: Int) : this(type, value, position, 0)
}
