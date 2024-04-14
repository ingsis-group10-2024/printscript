package config

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

class JsonConfigLoader(private val filePath: String) : ConfigLoader {
    override fun loadConfig(): VerificationConfig {
        val file = File(filePath)
        println("Ruta del archivo: ${file.absolutePath}")
        val mapper = ObjectMapper()
        return mapper.readValue(file, VerificationConfig::class.java)
    }
}
