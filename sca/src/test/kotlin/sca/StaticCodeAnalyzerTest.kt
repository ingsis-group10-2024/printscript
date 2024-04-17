package sca

import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.NumberOperatorNode
import com.fasterxml.jackson.databind.ObjectMapper
import config.ConfigLoader
import config.VerificationConfig
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.io.File

class StaticCodeAnalyzerTest {
    @Test
    fun `test analyze with camel case configuration`() {
        val astNodes =
            listOf(
                DeclarationAssignationNode(DeclarationNode("x", "int"), NumberOperatorNode(5.0)),
                DeclarationAssignationNode(DeclarationNode("myVariable", "int"), NumberOperatorNode(10.0)),
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
}
