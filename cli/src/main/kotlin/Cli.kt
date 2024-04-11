import com.github.ajalt.clikt.core.CliktCommand
import implementation.Lexer
import parser.Parser
import java.io.File

class Cli() : CliktCommand() {

//    val lexer: Lexer = Lexer()
//    var lexerTokens = lexer.convertToToken()
//    val parser: Parser = Parser(lexerTokens)
    val interpreter: Interpreter = InterpreterImpl()
    val formatter: Formatter = Formatter()
    val linter: Linter = LinterImpl()

    override fun run() {
        val sentencesList = getSentenceList()
        for (sentence in sentencesList) {
            when (sentence) {
                "execute" -> {
                    executeCode(sentence)
                }

                "format" -> {
                    formatCode(sentence)
                }

                "analyze" -> {
                    analyzeCode(sentencesList)
                }
                "validate" -> {
                    validateCode(sentence)
                }
            }
        }
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