import com.fasterxml.jackson.databind.ObjectMapper
import java.nio.file.Files
import java.nio.file.Paths

data class FormatterConfig(
    val indent: Int = 4,
    val maxLineLength: Int = 240,
    val indentString: String = " ".repeat(indent),
    val useSpaces: Boolean = true,
    val maxLineLengthString: String = " ".repeat(maxLineLength),
    val spaceBeforeColon: Boolean = true,
    val spaceAfterColon: Boolean = true,
    val newlineAfterSemicolon: Boolean = true,
    val spaceBeforePrintln: Int = 1,
    val ifBlockIndent: Int = 2,
)

fun loadConfig(filePath: String): FormatterConfig {
    val mapper = ObjectMapper()
    val path = Paths.get("src/main/resources/$filePath")
    val bytes = Files.readAllBytes(path)
    return mapper.readValue(bytes, FormatterConfig::class.java)
}
