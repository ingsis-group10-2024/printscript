
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.file
import config.JsonConfigLoader
import controller.LexerVersionController
import emitter.PrinterEmitter
import implementation.Formatter
import parser.Parser
import reader.ConsoleInputReader
import sca.StaticCodeAnalyzer
import sca.StaticCodeAnalyzerError
import token.Token
import variable.VariableMap
import java.io.FileInputStream
import java.io.InputStream

class Cli() : CliktCommand() {
    private val option: String by option().prompt("Option").help("which option do you want to choose?")
    private val version: String by option().prompt("Version").help("which version do you want to use?")
    private val file by option().file(mustExist = true, canBeDir = false).prompt("\nFile path").validate { file ->
        if (!file.extension.equals("txt", ignoreCase = true)) {
            fail("File is not a text file: ${file.path}")
        }
    } // validate that the file is a txt file
    private val validVersionList = listOf("1.0", "1.1")

    override fun run() {
        if (version !in validVersionList) {
            echo("Invalid version")
            return
        }
        val inputStream = FileInputStream(file.path)

        when (option) {
            "execute" -> { // este tiene que correr el lexer, dsp el parser, dsp el interpreter. y printear lo que devuelve el interpreter
                executeCode(version, inputStream)
            }

            "format" -> { // Este devuelve el file formateado
                formatCode(version, inputStream)
            }
            "analyze" -> { // Este tiene que correr el lexer, dsp el parser, dsp el sca. y printear los errores que devuelve el sca
                analyzeCode(version, inputStream)
            }
            else -> echo("Invalid option")
        }
    }

    private fun analyzeCode(
        version: String,
        inputStream: InputStream,
    ) {
        val versionController = LexerVersionController()
        val lexer = versionController.getLexer(version, inputStream)
        val tokens = mutableListOf<Token>()
        var token = lexer.getNextToken()
        while (token != null) {
            tokens.add(token)
            token = lexer.getNextToken()
        }
        val parser = Parser(tokens)
        val ast = parser.generateAST()
        val filePath = "sca/src/test/kotlin/sca/resources/StaticCodeAnalyzerRules.json"
        val configLoader = JsonConfigLoader(filePath)
        val analyzer = StaticCodeAnalyzer(configLoader)
        val errors = analyzer.analyze(ast)
        for (error: StaticCodeAnalyzerError in errors) {
            println(error)
        }
    }
}

private fun formatCode(
    version: String,
    inputStream: InputStream,
) {
    val versionController = LexerVersionController()
    val lexer = versionController.getLexer(version, inputStream)

    val tokens = mutableListOf<Token>()
    var token = lexer.getNextToken()
    while (token != null) {
        tokens.add(token)
        token = lexer.getNextToken()
    }
    val parser = Parser(tokens)
    val ast = parser.generateAST()
    val filePath = "formatter/src/main/resources/test_config_formatter.json"
    val formatter = Formatter(filePath)
    val formattedCode = formatter.format(ast)
    println("File formatted:\n$formattedCode")
}

private fun executeCode(
    version: String,
    inputStream: InputStream,
) {
    val consoleInputReader = ConsoleInputReader()
    val versionController = LexerVersionController()
    val lexer = versionController.getLexer(version, inputStream)

    val tokens = mutableListOf<Token>()
    var token = lexer.getNextToken()
    while (token != null) {
        tokens.add(token)
        token = lexer.getNextToken()
    }
    val parser = Parser(tokens)
    val ast = parser.generateAST()
    val interpreter = InterpreterFactory(version, VariableMap(HashMap()), consoleInputReader).buildInterpreter()
    try {
        val interpretedList = interpreter.interpret(ast)
        for (interpreted in interpretedList.second) {
            PrinterEmitter().print(interpreted)
        }
    } catch (e: Exception) {
        println(e.message)
    }
}

/*
primero pedir un file, si el file no existe tiro error
si el file existe: elige que quiere hacer.
    - execute: ejecutar el codigo
    - format: formatear el codigo
    - analyze: analizar el codigo
    - exit: salir
promptear paso a paso.
ir a la docu y como pasarle prompts por terminal
 */
fun main(args: Array<String>) {
    println(
        """
            

╭╮╭╮╭╮╱╱╭╮╱╱╱╱╱╱╱╱╱╱╱╱╱╱╭╮╱╱╱╱╭━━━╮╱╱╱╱╱╭╮╱╱╱╱╱╱╱╱╱╱╱╱╭╮
┃┃┃┃┃┃╱╱┃┃╱╱╱╱╱╱╱╱╱╱╱╱╱╭╯╰╮╱╱╱┃╭━╮┃╱╱╱╱╭╯╰╮╱╱╱╱╱╱╱╱╱╱╭╯╰╮
┃┃┃┃┃┣━━┫┃╭━━┳━━┳╮╭┳━━╮╰╮╭╋━━╮┃╰━╯┣━┳┳━╋╮╭╋━━┳━━┳━┳┳━┻╮╭╯
┃╰╯╰╯┃┃━┫┃┃╭━┫╭╮┃╰╯┃┃━┫╱┃┃┃╭╮┃┃╭━━┫╭╋┫╭╮┫┃┃━━┫╭━┫╭╋┫╭╮┃┃
╰╮╭╮╭┫┃━┫╰┫╰━┫╰╯┃┃┃┃┃━┫╱┃╰┫╰╯┃┃┃╱╱┃┃┃┃┃┃┃╰╋━━┃╰━┫┃┃┃╰╯┃╰╮
╱╰╯╰╯╰━━┻━┻━━┻━━┻┻┻┻━━╯╱╰━┻━━╯╰╯╱╱╰╯╰┻╯╰┻━┻━━┻━━┻╯╰┫╭━┻━╯
╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱┃┃
╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╰╯
            ╭━━━╮╱╱╱╱╱╱╱╱╱╱╱╱╭╮╭━━━╮
            ┃╭━╮┃╱╱╱╱╱╱╱╱╱╱╱╭╯┃┃╭━╮┃
            ┃┃╱╰╋━┳━━┳╮╭┳━━╮╰╮┃┃┃┃┃┃
            ┃┃╭━┫╭┫╭╮┃┃┃┃╭╮┃╱┃┃┃┃┃┃┃
            ┃╰┻━┃┃┃╰╯┃╰╯┃╰╯┃╭╯╰┫╰━╯┃
            ╰━━━┻╯╰━━┻━━┫╭━╯╰━━┻━━━╯
            ╱╱╱╱╱╱╱╱╱╱╱╱┃┃
            ╱╱╱╱╱╱╱╱╱╱╱╱╰╯

Choose an option:
    - execute: execute the code
    - format: format the code
    - analyze: analyze the code
    """,
    )
    Cli().main(args)
}
