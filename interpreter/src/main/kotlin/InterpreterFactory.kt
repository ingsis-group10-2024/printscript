import reader.Reader
import strategy.InterpreterManagerImplStrategyV10
import strategy.InterpreterManagerImplStrategyV11
import variable.VariableMap

class InterpreterFactory(val version: String, val variableMap: VariableMap, val envVariableMap: VariableMap, val reader: Reader) {
    @Throws(IllegalArgumentException::class)
    fun buildInterpreter(): InterpreterManager {
        when (version) {
            "1.0" -> return InterpreterManagerImplStrategyV10(variableMap)
            "1.1" -> return InterpreterManagerImplStrategyV11(variableMap, envVariableMap, reader)
            else -> throw IllegalArgumentException("Invalid version")
        }
    }
}
