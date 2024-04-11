import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

data class FormatterConfig(
    val indent: Int = 4,
    val maxLineLength: Int = 240,
    val indentString: String = " ".repeat(indent),
    val useSpaces: Boolean = true,
    val maxLineLengthString: String = " ".repeat(maxLineLength)
)


fun loadConfig(filePath: String): FormatterConfig {
    val mapper = jacksonObjectMapper()
    return mapper.readValue(File(filePath))
}
