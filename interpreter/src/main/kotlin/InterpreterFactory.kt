import emitter.PrintCollector
import reader.Reader
import strategy.InterpreterManagerImplStrategyV10
import strategy.InterpreterManagerImplStrategyV11
import variable.VariableMap

class InterpreterFactory(
    val version: String,
    val variableMap: VariableMap,
    val reader: Reader?,
    val printCollector: PrintCollector?,
) {
    @Throws(IllegalArgumentException::class)
    fun buildInterpreter(): InterpreterManager {
        when (version) {
            "1.0" -> return InterpreterManagerImplStrategyV10(variableMap)
            "1.1" -> return InterpreterManagerImplStrategyV11(variableMap, reader!!, printCollector!!)
            else -> throw IllegalArgumentException("Invalid version")
        }
    }
}
