package sca

import ast.DeclarationNode
import ast.MethodNode
import ast.Position
import ast.StringOperatorNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import config.ConfigLoader
import config.ConfigRule
import config.VerificationConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import token.TokenType
import java.io.File

class StaticCodeAnalyzerTest {
    @Test
    fun testAnalyzeWithNoConfiguration() {
        val astNodes = listOf(DeclarationNode("myVariable", TokenType.LET, Position(0, 0), "String", Position(1, 0)))
        val configLoader =
            object : ConfigLoader {
                override fun loadConfig(): VerificationConfig {
                    return VerificationConfig(emptyList())
                }
            }
        val analyzer = StaticCodeAnalyzer(configLoader)
        val errors = analyzer.analyze(astNodes)
        assertEquals(0, errors.size)
    }

    @Test
    fun testAnalyzeWithSnakeCaseEnabled() {
        val astNodes = listOf(DeclarationNode("my_variable", TokenType.LET, Position(0, 0), "String", Position(1, 0)))
        val configLoader =
            object : ConfigLoader {
                override fun loadConfig(): VerificationConfig {
                    return VerificationConfig(
                        listOf(
                            ConfigRule("snake_case", true),
                            ConfigRule("camel_case", false),
                        ),
                    )
                }
            }
        val analyzer = StaticCodeAnalyzer(configLoader)
        val errors = analyzer.analyze(astNodes)
        assertEquals(0, errors.size)
    }

    @Test
    fun testAnalyzeWithPrintlnChecker() {
        val astNodes =
            listOf(
                MethodNode(
                    "println",
                    StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(0, 0)),
                    Position(1, 0),
                ),
            )

        val configLoader =
            object : ConfigLoader {
                override fun loadConfig(): VerificationConfig {
                    return VerificationConfig(
                        listOf(ConfigRule("printlnArgumentChecker", true)),
                    )
                }
            }

        val analyzer = StaticCodeAnalyzer(configLoader)
        val errors = analyzer.analyze(astNodes)
        assertEquals(0, errors.size)
    }

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

    @Test
    fun testIsCamelCaseRequired() {
        val configWithCamelCaseEnabled =
            VerificationConfig(
                activeRules =
                    listOf(
                        ConfigRule("camelCase", true),
                        ConfigRule("snake_case", false),
                    ),
            )
        val configWithCamelCaseDisabled =
            VerificationConfig(
                activeRules =
                    listOf(
                        ConfigRule("camelCase", false),
                        ConfigRule("snake_case", true),
                    ),
            )

        val analyzerWithCamelCaseEnabled =
            StaticCodeAnalyzer(
                object : ConfigLoader {
                    override fun loadConfig() = configWithCamelCaseEnabled
                },
            )
        val analyzerWithCamelCaseDisabled =
            StaticCodeAnalyzer(
                object : ConfigLoader {
                    override fun loadConfig() = configWithCamelCaseDisabled
                },
            )

        assertEquals(true, analyzerWithCamelCaseEnabled.isCamelCaseRequired(configWithCamelCaseEnabled))
        assertEquals(false, analyzerWithCamelCaseDisabled.isCamelCaseRequired(configWithCamelCaseDisabled))
    }

    @Test
    fun testIsSnakeCaseRequired() {
        val configWithSnakeCaseEnabled =
            VerificationConfig(
                activeRules =
                    listOf(
                        ConfigRule("snake_case", true),
                        ConfigRule("camelCase", false),
                    ),
            )
        val configWithSnakeCaseDisabled =
            VerificationConfig(
                activeRules =
                    listOf(
                        ConfigRule("snake_case", false),
                        ConfigRule("camelCase", true),
                    ),
            )

        val analyzerWithSnakeCaseEnabled =
            StaticCodeAnalyzer(
                object : ConfigLoader {
                    override fun loadConfig() = configWithSnakeCaseEnabled
                },
            )
        val analyzerWithSnakeCaseDisabled =
            StaticCodeAnalyzer(
                object : ConfigLoader {
                    override fun loadConfig() = configWithSnakeCaseDisabled
                },
            )

        assertEquals(true, analyzerWithSnakeCaseEnabled.isSnakeCaseRequired(configWithSnakeCaseEnabled))
        assertEquals(false, analyzerWithSnakeCaseDisabled.isSnakeCaseRequired(configWithSnakeCaseDisabled))
    }

    @Test
    fun testIsCamelCase() {
        val analyzer =
            StaticCodeAnalyzer(
                object : ConfigLoader {
                    override fun loadConfig() = VerificationConfig(emptyList())
                },
            )

        assertEquals(true, analyzer.isCamelCase("myVariable"))
        assertEquals(false, analyzer.isCamelCase("my_variable")) // snake_case no es camelCase
        assertEquals(false, analyzer.isCamelCase("MyVariable")) // camelCase debe empezar con letra minúscula
        // assertEquals(false, analyzer.isCamelCase("myvariable")) // camelCase requiere al menos una letra mayúscula o número después de la primera letra
    }

    @Test
    fun testIsSnakeCase() {
        val analyzer =
            StaticCodeAnalyzer(
                object : ConfigLoader {
                    override fun loadConfig() = VerificationConfig(emptyList())
                },
            )

        assertEquals(true, analyzer.isSnakeCase("my_variable"))
        assertEquals(false, analyzer.isSnakeCase("myVariable")) // camelCase no es snake_case
        assertEquals(false, analyzer.isSnakeCase("My_Variable")) // snake_case no debe tener letras mayúsculas
        assertEquals(true, analyzer.isSnakeCase("myvariable")) // snake_case permite solo letras minúsculas y números, pero aquí no se está usando ningún número
    }

    @Test
    fun testStaticCodeAnalyzerErrorInitialization() {
        val errorMessage = "Test error message"
        val error = StaticCodeAnalyzerError(errorMessage)

        assertEquals(errorMessage, error.message, "Error message should be initialized correctly")
    }
}
