package sca

import ast.ASTNode
import ast.DeclarationAssignationNode
import ast.MethodNode
import config.ConfigLoader
import config.VerificationConfig

class StaticCodeAnalyzer(private val configLoader: ConfigLoader) {
    fun analyze(astNodes: List<ASTNode>): List<StaticCodeAnalyzerError> {
        val config = configLoader.loadConfig()
        val errors = mutableListOf<StaticCodeAnalyzerError>()

        for (node in astNodes) {
            when (node) {
                is DeclarationAssignationNode -> {
                    if (isCamelCaseRequired(config) && !isLowerCamelCase(node.declaration.identifier)) {
                        errors.add(StaticCodeAnalyzerError("Variable name '${node.declaration.identifier}' is not in lower camel case"))
                    }
                }
                is MethodNode -> {
                    if (isPrintlnArgumentCheckerEnabled(config) && node.name == "println") {
                        if (!isValidPrintlnArgument(node.value.toString())) {
                            errors.add(StaticCodeAnalyzerError("Invalid argument '${node.value}' for println method"))
                        }
                    }
                }
                else -> {}
            }
        }
        return errors
    }

    private fun isLowerCamelCase(identifier: String): Boolean {
        return identifier.matches("""^[a-z]+(?:[A-Z][a-z\d])$""".toRegex())
    }

    private fun isValidPrintlnArgument(argument: String): Boolean {
        return argument.matches("""^[\w\d]+$""".toRegex())
    }

    private fun isCamelCaseRequired(config: VerificationConfig): Boolean {
        return config.activeRules.any { it.name == "camel_case" && it.enabled }
    }

    private fun isPrintlnArgumentCheckerEnabled(config: VerificationConfig): Boolean {
        return config.activeRules.any { it.name == "printlnArgumentChecker" && it.enabled }
    }
}


