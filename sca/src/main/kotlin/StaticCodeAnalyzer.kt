package sca

import ast.ASTNode
import ast.BinaryNode
import ast.DeclarationAssignationNode
import ast.IdentifierOperatorNode
import ast.MethodNode
import ast.StringOperatorNode
import config.ConfigLoader
import config.VerificationConfig

class StaticCodeAnalyzer(private val configLoader: ConfigLoader) {
    fun analyze(astNodes: List<ASTNode>): List<StaticCodeAnalyzerError> {
        val config = configLoader.loadConfig()
        val errors = mutableListOf<StaticCodeAnalyzerError>()

        for (node in astNodes) {
            when (node) {
                is DeclarationAssignationNode -> {
                    if (isCamelCaseRequired(config) && !isCamelCase(node.declaration.identifier)) {
                        errors.add(StaticCodeAnalyzerError("Variable name '${node.declaration.identifier}' is not in lower camelCase"))
                    }
                    if (isSnakeCaseRequired(config) && !isSnakeCase(node.declaration.identifier)) {
                        errors.add(StaticCodeAnalyzerError("Variable name '${node.declaration.identifier}' is not in snake_case"))
                    }
                }
                is MethodNode -> {
                    // La función “println” solo puede llamarse con un identificador o un literal pero no con
                    // una expresión. Esto puede estar prendido o apagado
                    if (isPrintlnArgumentCheckerEnabled(config) && node.name == "println") {
                        if (!isIdentifierOrLiteral(node.value)) {
                            errors.add(StaticCodeAnalyzerError("Expected identifier or literal but found '${node.value}' in println argument"))
                        }
                    }
                    if (isReadInputCheckerEnabled(config) && node.name == "readInput") {
                        if (!isIdentifierOrLiteral(node.value)) {
                            errors.add(StaticCodeAnalyzerError("Expected identifier or literal but found '${node.value}' in readInput argument"))
                        }
                    }
                }
                else -> {}
            }
        }
        return errors
    }

    fun isCamelCase(identifier: String): Boolean {
        return identifier.matches(Regex("[a-z][a-zA-Z0-9]*"))
    }

    fun isSnakeCase(identifier: String): Boolean {
        return identifier.matches(Regex("[a-z][a-z_0-9]*"))
    }

    private fun isIdentifierOrLiteral(value: BinaryNode): Boolean {
        return when (value) {
            is StringOperatorNode,
            is IdentifierOperatorNode,
            -> true
            else -> false
        }
    }

    fun isCamelCaseRequired(config: VerificationConfig): Boolean {
        return config.activeRules.any { it.name == "camelCase" && it.enabled }
    }

    fun isSnakeCaseRequired(config: VerificationConfig): Boolean {
        return config.activeRules.any { it.name == "snake_case" && it.enabled }
    }

    // Hace que el contenido del print solo pueda ser un identifier o un string pero no una expresion
    private fun isPrintlnArgumentCheckerEnabled(config: VerificationConfig): Boolean {
        return config.activeRules.any { it.name == "printlnArgumentChecker" && it.enabled }
    }

    // Hace que el contenido del readInput solo pueda ser un identifier o un string pero no una expresion
    private fun isReadInputCheckerEnabled(config: VerificationConfig): Boolean {
        return config.activeRules.any { it.name == "readInputChecker" && it.enabled }
    }
}
