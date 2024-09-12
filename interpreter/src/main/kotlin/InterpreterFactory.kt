import manager.InterpreterManagerImplV10
import manager.InterpreterManagerImplV11
import reader.Reader
import variable.VariableMap

class InterpreterFactory(val version: String, val variableMap: VariableMap, val reader: Reader?) {
    @Throws(IllegalArgumentException::class)
    fun buildInterpreter(): InterpreterManager {
        when (version) {
            "1.0" -> return InterpreterManagerImplV10(variableMap)
            "1.1" -> return InterpreterManagerImplV11(variableMap, reader!!)
            else -> throw IllegalArgumentException("Invalid version")
        }
    }
}
