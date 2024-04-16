package sca

import org.junit.Test
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.IdentifierOperatorNode
import ast.StringOperatorNode
import config.ConfigLoader
import config.ConfigRule
import config.VerificationConfig
import junit.framework.TestCase.assertEquals

class StaticCodeAnalizerTest {
    @Test
    fun `test analyze with configuration`() {
        // Simulación de ASTNodes
        val astNodes = listOf(
            DeclarationAssignationNode(DeclarationNode("x", "int"), NumberOperatorNode(5.0)),
            MethodNode("println", IdentifierOperatorNode("x")),
            MethodNode("println", StringOperatorNode("Hello, World!"))
        )

        // Creación de un archivo de configuración temporal
        val configContent = """
            activeRules:
              - name: camel_case
                enabled: true
              - name: printlnArgumentChecker
                enabled: true
        """.trimIndent()
        val configFile = createTempFile("test_config", ".yaml")
        configFile.writeText(configContent)

        // Creación del ConfigLoader y StaticCodeAnalyzer con el archivo de configuración temporal
        val configLoader = object : ConfigLoader {
            override fun loadConfig(): VerificationConfig {
                val yamlData = Yaml().load<Map<String, List<Map<String, Any>>>>(configFile.readText())
                val activeRules = yamlData["activeRules"]?.map {
                    ConfigRule(it["name"] as String, it["enabled"] as Boolean)
                } ?: emptyList()
                return VerificationConfig(activeRules)
            }
        }
        val analyzer = StaticCodeAnalyzer(configLoader)

        // Ejecución del método analyze y verificación de resultados
        val errors = analyzer.analyze(astNodes)
        assertEquals(1, errors.size)
        assertEquals("Variable name 'x' is not in lower camel case", errors[0].message)
    }
}