package token

import common.token.TokenType

data class Token(
    val type: TokenType,
    val value: String,
    val column: Int,
    val line: Int,
)
