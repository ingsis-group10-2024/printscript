import emitter.Printer
import manager.InterpreterManagerImplV10
import manager.InterpreterManagerImplV11
import reader.Reader
import variable.VariableMap

class InterpreterFactory(val version: String, val variableMap: VariableMap, val reader: Reader?, val outputter: Printer) {
    @Throws(IllegalArgumentException::class)
    fun buildInterpreter(): InterpreterManager {
        when (version) {
            "1.0" -> return InterpreterManagerImplV10(variableMap, outputter)
            "1.1" -> return InterpreterManagerImplV11(variableMap, reader!!, outputter)
            else -> throw IllegalArgumentException("Invalid version")
        }
    }
}
