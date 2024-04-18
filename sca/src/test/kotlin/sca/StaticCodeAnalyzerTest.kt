package sca

import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.NumberOperatorNode
import ast.Position
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
                DeclarationAssignationNode(
                    DeclarationNode("x", Position(1, 1), "int", Position(2, 1)),
                    NumberOperatorNode(5.0, Position(3, 1)),
                ),
                DeclarationAssignationNode(
                    DeclarationNode("myVariable", Position(1, 2), "int", Position(2, 2)),
                    NumberOperatorNode(10.0, Position(6, 2)),
                ),
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
