
class Formatter(private val config: FormatterConfig) {
    fun format(text: String): String {
        // Example formatting logic using config
        val formattedText = if (config.useSpaces) {
            text.replace("\t", " ".repeat(config.indent))
        } else {
            text
        }
        return formattedText
    }
}
