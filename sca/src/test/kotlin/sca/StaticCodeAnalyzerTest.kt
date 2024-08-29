package sca

import ast.DeclarationNode
import ast.Position
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import config.ConfigLoader
import config.VerificationConfig
import junit.framework.TestCase.assertEquals
import org.junit.Test
import token.TokenType
import java.io.File

class StaticCodeAnalyzerTest {
    @Test
    fun testAnalyzeWithCamelCaseJSONConfiguration() {
        val astNodes =
            listOf(
                DeclarationNode("my_variable", TokenType.LET, Position(0, 0), "String", Position(1, 0)),
                DeclarationNode("myVariable", TokenType.LET, Position(0, 1), "String", Position(1, 1)),
            )

        val configFilePath = "src/test/kotlin/sca/resources/StaticCodeAnalyzerRules.json"

        val configLoader =
            object : ConfigLoader {
                override fun loadConfig(): VerificationConfig {
                    val file = File(configFilePath)
                    val mapper = ObjectMapper()
                    return mapper.readValue(file, VerificationConfig::class.java)
                }
            }
        val analyzer = StaticCodeAnalyzer(configLoader)

        val errors = analyzer.analyze(astNodes)
        assertEquals(0, errors.size)
    }

    @Test
    fun testAnalyzeWithCamelCaseYAMLConfiguration() {
        val astNodes =
            listOf(
                DeclarationNode("my_variable", TokenType.LET, Position(0, 0), "String", Position(1, 0)),
                DeclarationNode("myVariable", TokenType.LET, Position(0, 1), "String", Position(1, 1)),
            )

        val configFilePath = "src/test/kotlin/sca/resources/StaticCodeAnalyzerRules.yaml"

        val configLoader =
            object : ConfigLoader {
                override fun loadConfig(): VerificationConfig {
                    val file = File(configFilePath)
                    val mapper = ObjectMapper(YAMLFactory())
                    return mapper.readValue(file, VerificationConfig::class.java)
                }
            }
        val analyzer = StaticCodeAnalyzer(configLoader)

        val errors = analyzer.analyze(astNodes)
        assertEquals(0, errors.size)
    }
}
