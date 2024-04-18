import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.file
import config.JsonConfigLoader
import implementation.Lexer
import parser.Parser
import java.io.File

class Cli() : CliktCommand() {
    private val option: String by option().prompt("Option").help("which option do you want to choose?")
    private val file by option().file(mustExist = true, canBeDir = false).prompt("\nFile path").validate { file ->
        if (!file.extension.equals("txt", ignoreCase = true)) {
            fail("File is not a text file: ${file.path}")
        }
    } // validate that the file is a txt file
    private val version = "1.0"
    private val validVersionList = listOf("1.0")

    override fun run() {
        if (version !in validVersionList) {
            echo("Invalid version")
            return
        }
        when (option) {
            "execute" -> { // este tiene que correr el lexer, dsp el parser, dsp el interpreter. y printear lo que devuelve el interpreter
                executeCode(file, version)
            }

            "format" -> {
                formatCode(file, version)
            }

            "validate" -> {
                validateCode(file, version)
            }
            else -> echo("Invalid option")
        }
    }
}

private fun validateCode(
    file: File,
    version: String,
) {
    val lexer = Lexer(file)
    val tokens = lexer.convertToToken()
    val parser = Parser(tokens)
    val ast = parser.generateAST()
    val linter = LinterImpl()
    val errors = linter.lint(ast)
    for (error: String in errors) {
        println(error)
    }
}

private fun formatCode(
    file: File,
    version: String,
) {
    val lexer = Lexer(file)
    val tokens = lexer.convertToToken()
    val parser = Parser(tokens)
    val ast = parser.generateAST()
    val filePath = "formatter/src/main/resources/test_config_formatter.json"
    val jsonConfigLoader = JsonConfigLoader(filePath)
    val formatter = Formatter(jsonConfigLoader)
    val formattedCode = formatter.format(ast)
    println("File formatted $formattedCode")
}

private fun executeCode(
    file: File,
    version: String,
) {
    val lexer = Lexer(file)
    val tokens = lexer.convertToToken()
    val parser = Parser(tokens)
    val ast = parser.generateAST()
    val interpreter = InterpreterImpl(VariableMap(HashMap()))
    val result = interpreter.interpret(ast)
    println(result.second)
}

/*
primero pedir un file, si el file no existe tiro error
si el file existe: elige que quiere hacer.
    - execute: ejecutar el codigo
    - format: formatear el codigo
    - analyze: analizar el codigo
    - validate: validar el codigo
    - exit: salir
promptear paso a paso.
ir a la docu y como pasarle prompts por terminal
 */
fun main(args: Array<String>) {
    println(
        """
| ------- Welcome to PrintScript 1.0 CLI -------
|
| Choose one of the following options:
|
|    validate
|    execute
|    format
|
    """,
    )
    Cli().main(args)
}
