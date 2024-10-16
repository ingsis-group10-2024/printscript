package token

data class Token(
    val type: TokenType,
    val value: String,
    val column: Int,
    val row: Int,
) {
    // Serialización del Token a una cadena
    override fun toString(): String {
        return "${type.name}|$value|$column|$row"
    }

    // Deserialización del Token desde una cadena
    companion object {
        fun fromString(tokenString: String): Token {
            val parts = tokenString.split("|")
            val type = TokenType.valueOf(parts[0])
            val value = parts[1]
            val column = parts[2].toInt()
            val row = parts[3].toInt()
            return Token(type, value, column, row)
        }
    }
}
