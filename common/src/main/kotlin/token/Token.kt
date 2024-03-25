package common.token

data class Token(
    val type: TokenType,
    val value: String,
    val position: Int, // no usamos initialPosition y finalPosition porque vamos a recorrer
    // los tokens de manera secuencial en el parser
) {
    constructor(type: TokenType, value: String, lineNumber: Int, position: Int) : this(type, value, position)
}
