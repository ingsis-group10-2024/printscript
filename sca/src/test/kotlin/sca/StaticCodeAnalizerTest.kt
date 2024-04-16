package sca

import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.NumberOperatorNode
import config.ConfigLoader
import config.VerificationConfig
import junit.framework.TestCase.assertEquals
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import org.junit.Test

class StaticCodeAnalizerCamelCaseTest {
    @Test
    fun `test analyze with camel case configuration`() {
        // Simulación de ASTNodes
        val astNodes = listOf(
            DeclarationAssignationNode(DeclarationNode("x", "int"), NumberOperatorNode(5.0)),
            DeclarationAssignationNode(DeclarationNode("myVariable", "int"), NumberOperatorNode(10.0))
        )

        // Ruta del archivo de configuración
        val configFilePath = "src/test/kotlin/sca/resources/StaticCodeAnalizerRules.json"

        // Creación del ConfigLoader y StaticCodeAnalyzer con el archivo de configuración
        val configLoader = object : ConfigLoader {
            override fun loadConfig(): VerificationConfig {
                val file = File(configFilePath)
                val mapper = ObjectMapper()
                return mapper.readValue(file, VerificationConfig::class.java)
            }
        }
        val analyzer = StaticCodeAnalyzer(configLoader)

        // Ejecución del método analyze y verificación de resultados
        val errors = analyzer.analyze(astNodes)
        assertEquals(0, errors.size) // Cambiamos esto a 0 ya que no debería haber errores
    }
}
