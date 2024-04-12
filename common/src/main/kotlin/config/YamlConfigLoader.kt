package config

import org.yaml.snakeyaml.Yaml
import java.io.File

class YamlConfigLoader(private val filePath: String) : ConfigLoader {
    override fun loadConfig(): VerificationConfig {
        val file = File(filePath)
        return Yaml().load(file.readText())
    }
}