import com.github.ajalt.clikt.core.CliktCommand
import implementation.Lexer
import parser.Parser
import java.io.File

class Cli(val file: File) : CliktCommand() {
    val lexer: Lexer = Lexer(file)
    var lexerTokens = lexer.convertToToken()
    val parser: Parser = Parser(lexerTokens)
    val interpreter: Interpreter = InterpreterImpl()
    val formatter: Formatter = Formatter()

    override fun run() {
        val sentencesList = getSentenceList()
        when (operation) {
            "execute" -> {
                executeCode(sentencesList)
            }

            "format" -> {
                formatCode(sentencesList)
            }

            "analyze" -> {
                analyzeCode(sentencesList)
                // TODO
            }

            "validate" -> {
                // TODO
            }
        }
    }

    private fun formatCode(sentencesList: List<String>) {
        TODO("Not yet implemented")
    }

    private fun executeCode(sentencesList: List<String>) {
        TODO("Not yet implemented")
    }

    private fun getSentenceList(): List<String> {
        TODO("Not yet implemented")
    }

    private fun analyzeCode(sentencesList: List<String>) {
        val ast = parser.generateAST()
        val errors = linter.lint(ast)
        errors.forEach {
            println(it)
        }
    }
}
