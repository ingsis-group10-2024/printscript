import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

data class FormatterConfig(
    val indent: Int = 4,
    val maxLineLength: Int = 240,
    val indentString: String = " ".repeat(indent),
    val useSpaces: Boolean = true,
    val maxLineLengthString: String = " ".repeat(maxLineLength),
    val spaceBeforeColon: Boolean = true,
    val spaceAfterColon: Boolean = true,
    val newlineAfterSemicolon: Boolean = true,
    val spaceBeforePrintln: Int = 1
)


fun loadConfig(filePath: String): FormatterConfig {
    val mapper = jacksonObjectMapper()
    return mapper.readValue(File(filePath))
}
