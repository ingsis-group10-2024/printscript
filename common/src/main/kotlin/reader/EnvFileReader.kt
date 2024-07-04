package reader

import variable.Variable
import variable.VariableFactory
import variable.VariableMap
import java.io.File

class EnvFileReader(filePath: String) : Reader {
    private val variableFactory = VariableFactory()
    private val envFilePath = filePath

    override fun read(message: String): String? {
        println(message)
        return readlnOrNull()
    }

    fun readEnvFile(): VariableMap {
        val envMap = HashMap<Variable, String?>()
        val file = File(envFilePath)

        if (!file.exists()) {
            println("File not found: $envFilePath")
            return VariableMap(envMap)
        }

        file.forEachLine { line ->
            val trimmedLine = line.trim()
            // Ignorar líneas vacías y comentarios
            if (trimmedLine.isNotEmpty() && !trimmedLine.startsWith("#")) {
                val parts = trimmedLine.split("=", limit = 2)
                if (parts.size == 2) {
                    val variableName = parts[0].trim()
                    val variableValue = parts[1].trim().removeSurrounding("\"")
                    val variableKey = variableFactory.createVariable(variableName, "String", false)
                    envMap[variableKey] = variableValue
                } else {
                    println("Skipping malformed line: $line")
                }
            }
        }
        return VariableMap(envMap)
    }
}
