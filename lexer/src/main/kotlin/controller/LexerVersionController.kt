package controller

import implementation.LexerV10
import implementation.LexerV11
import interfaces.Lexer
import java.io.InputStream

class LexerVersionController {
    fun getLexer(
        version: String,
        inputStream: InputStream,
    ): Lexer {
        return when (version) {
            "1.0" -> LexerV10(inputStream)
            "1.1" -> LexerV11(inputStream)
            else -> throw IllegalArgumentException("Unknown version: $version")
        }
    }
}
