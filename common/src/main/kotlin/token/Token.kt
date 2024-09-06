package token

data class Token(
    val type: TokenType,
    val value: String,
    val column: Int,
    val row: Int,
)
