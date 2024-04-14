package config

import org.yaml.snakeyaml.Yaml
import java.io.File

class YamlConfigLoader(private val filePath: String) : ConfigLoader {
    override fun loadConfig(): VerificationConfig {
        val file = File(filePath)
        println("Ruta del archivo: ${file.absolutePath}")

        val yamlData = Yaml().load<Map<String, List<Map<String, Any>>>>(file.readText())
        val activeRules = yamlData["activeRules"]?.map {
            ConfigRule(it["name"] as String, it["enabled"] as Boolean)
        } ?: emptyList()

        return VerificationConfig(activeRules)
    }
}
