package common.token

data class Token(
    val type: TokenType,
    val value: String,
    val position: Int,
    val lineNumber: Int,
)
