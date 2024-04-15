import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import implementation.Lexer
import parser.Parser

class Cli() : CliktCommand() {
    private val option: String by option().prompt("Option").help("which option do you want to choose?")
    private val file by option().file(mustExist = true, canBeDir = false).prompt("\nFile path")
    val version = "1.0"
    private val validVersionList = listOf("1.0")


    override fun run() {
//            when (option) {
//                "execute" -> {
//                    executeCode()
//                }
//
//                "format" -> {
//                    formatCode()
//                }
//
//                "analyze" -> {
//                    analyzeCode()
//                }
//                "validate" -> {
//                    validateCode()
//                }
//                else -> echo("Invalid option")
//            }
//        }
    }

    private fun validateCode(sentences: String) {
//        val ast = parser.generateAST()
//        val errors = linter.lint(ast)
//        if (errors.isEmpty()) {
//            println("No errors found")
//        } else {
//            errors.forEach {
//                println(it + " in line")
//            }
//        }
    }

    private fun formatCode(sentences: String) {
        TODO("Not yet implemented")
    }

    private fun executeCode(sentences: String) {
        TODO("Not yet implemented")
    }

    private fun getSentenceList(): List<String> {
        TODO("Not yet implemented")
    }

    private fun analyzeCode(sentencesList: List<String>) {
//        val ast = parser.generateAST()
//        val errors = linter.lint(ast)
//        errors.forEach {
//            println(it)
//        }
    }
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
