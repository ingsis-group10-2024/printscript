import config.ConfigRule
import config.JsonConfigLoader
import config.VerificationConfig
import config.YamlConfigLoader
import org.junit.Test
import java.io.FileNotFoundException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConfigTest {
    @Test
    fun `test VerificationConfig creation`() {
        val rule1 = ConfigRule(name = "rule1", enabled = true)
        val rule2 = ConfigRule(name = "rule2", enabled = false)

        val config = VerificationConfig(listOf(rule1, rule2))

        assertEquals(2, config.activeRules.size)
        assertEquals(rule1, config.activeRules[0])
        assertEquals(rule2, config.activeRules[1])
    }

    @Test
    fun `test JsonConfigLoader loading`() {
        val filePath = "src/test/kotlin/common/resources/test_config.json"
        val jsonConfigLoader = JsonConfigLoader(filePath)

        val config = jsonConfigLoader.loadConfig()

        assertEquals(2, config.activeRules.size)
    }

    @Test
    fun `test YamlConfigLoader loading`() {
        val filePath = "src/test/kotlin/common/resources/test_config.yaml"
        val yamlConfigLoader = YamlConfigLoader(filePath)

        val config = yamlConfigLoader.loadConfig()

        assertEquals(2, config.activeRules.size)
    }

    @Test
    fun `test JsonConfigLoader file not found error`() {
        val filePath = "non_existing_file.json"
        val jsonConfigLoader = JsonConfigLoader(filePath)

        assertFailsWith<FileNotFoundException> { jsonConfigLoader.loadConfig() }
    }

    @Test
    fun `test YamlConfigLoader file not found error`() {
        val filePath = "non_existing_file.yaml"
        val yamlConfigLoader = YamlConfigLoader(filePath)

        assertFailsWith<FileNotFoundException> { yamlConfigLoader.loadConfig() }
    }
}