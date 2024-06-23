package parser

import ast.ASTNode
import ast.AssignationNode
import ast.BinaryNode
import ast.BinaryOperationNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.Position
import ast.StringOperatorNode
import token.Token
import token.TokenType

class Parser(private val tokens: List<Token>) {
    private var currentTokenIndex = 0

    fun generateAST(): List<ASTNode> {
        val nodes = mutableListOf<ASTNode>()
        while (currentTokenIndex < tokens.size) {
            val node = parseExpression()
            if (node != null) {
                nodes.add(node)
            }
        }
        return nodes
    }

    private fun parseExpression(): ASTNode? {
        return parseAddition()
    }

    fun parseAddition(): ASTNode? {
        var node: ASTNode? = parseMultiplication()
        while (currentTokenIndex < tokens.size &&
            (isCurrentToken(TokenType.PLUS) || isCurrentToken(TokenType.MINUS))
        ) {
            val token = getTokenAndAdvance()
            val rightNode = parseMultiplication()
            node = BinaryOperationNode(token.value, node, rightNode)
        }
        return node
    }

    fun parseMultiplication(): ASTNode? {
        var node: ASTNode? = parseContent()
        while (currentTokenIndex < tokens.size &&
            (isCurrentToken(TokenType.MULTIPLY) || isCurrentToken(TokenType.DIVIDE))
        ) {
            val token = getTokenAndAdvance()
            val rightNode = parseContent()

            if (node != null && rightNode != null) {
                node = BinaryOperationNode(token.value, node, rightNode as BinaryNode)
            } else {
                throw RuntimeException("Error parsing multiplication")
            }
        }
        return node
    }

    fun parseContent(): ASTNode? {
        val currentToken = getCurrentSignificantToken()

        return when (currentToken.type) {
            TokenType.NUMERIC_LITERAL -> {
                val token = getTokenAndAdvance()
                NumberOperatorNode(token.value.toDouble(), Position(token.column, token.line))
            }
            TokenType.STRING_LITERAL -> {
                val token = getTokenAndAdvance()
                StringOperatorNode(token.value, Position(token.column, token.line))
            }
            TokenType.IDENTIFIER -> {
                // Es una variable solita ej: x
                val nextToken = getNextSignificantToken()
                if (nextToken.type != TokenType.EQUALS) {
                    getTokenAndAdvance() // salteo el identifier
                    IdentifierOperatorNode(currentToken.value, Position(currentToken.column, currentToken.line))
                } else {
                    // Es una operacion:  x=5
                    parseAssignation()
                }
            }
            TokenType.STRING_TYPE -> {
                val token = getTokenAndAdvance()
                StringOperatorNode(token.value, Position(token.column, token.line))
            }
            TokenType.OPEN_PARENTHESIS -> {
                getTokenAndAdvance()
                val node = parseExpression()
                this.getTokenAndAdvance()
                node
            }
            TokenType.LET -> {
                parseDeclarationAssignation()
            }
            TokenType.PRINTLN -> {
                parsePrintln()
            }
            TokenType.READINPUT -> {
                parseReadInput()
            }
            TokenType.READENV -> {
                parseReadEnv()
            }
            else -> null
        }
    }

    fun parseDeclaration(): DeclarationNode {
        getTokenAndAdvance()
        if (!isCurrentToken(TokenType.IDENTIFIER)) {
            val currentToken = getCurrentSignificantToken()
            throwParseException("identifier", currentToken.value, currentToken.column, currentToken.line)
        }
        val identifierToken = getTokenAndAdvance()
        if (!isCurrentToken(TokenType.COLON)) {
            val currentToken = getCurrentSignificantToken()
            throwParseException("':'", currentToken.value, currentToken.column, currentToken.line)
        }
        getTokenAndAdvance()
        if (!isCurrentToken(TokenType.NUMBER_TYPE) && !isCurrentToken(TokenType.STRING_TYPE)) {
            val currentToken = getCurrentSignificantToken()
            throwParseException("type", currentToken.value, currentToken.column, currentToken.line)
        }
        val typeToken = getTokenAndAdvance()
        return DeclarationNode(
            identifierToken.value,
            Position(identifierToken.column, identifierToken.line),
            typeToken.value,
            Position(typeToken.column, typeToken.line),
        )
    }

    fun parseAssignation(): AssignationNode {
        val identifierToken = getTokenAndAdvance()
        if (isCurrentToken(TokenType.EQUALS)) {
            getTokenAndAdvance()
            val expression = parseExpression()
            if (expression != null) {
                if (isCurrentToken(TokenType.SEMICOLON)) {
                    getTokenAndAdvance()
                    return AssignationNode(
                        identifierToken.value,
                        Position(identifierToken.column, identifierToken.line),
                        expression as BinaryNode,
                    )
                } else {
                    throwParseException(
                        "';'",
                        getCurrentSignificantToken().value,
                        getCurrentSignificantToken().column,
                        getCurrentSignificantToken().line,
                    )
                }
            } else {
                throwParseException("expression", "", getCurrentSignificantToken().column, getCurrentSignificantToken().line)
            }
        } else if (isCurrentToken(TokenType.EQUALS_EQUALS)) {
            getTokenAndAdvance()
            val expression = parseExpression()
            return AssignationNode(
                identifierToken.value,
                Position(identifierToken.column, identifierToken.line),
                expression as BinaryNode,
            )
        }
        throwParseException(
            "'='",
            getCurrentSignificantToken().value,
            getCurrentSignificantToken().column,
            getCurrentSignificantToken().line,
        )
        return AssignationNode("", Position(0, 0), StringOperatorNode(" ", Position(0, 0)))
    }

    fun parseDeclarationAssignation(): ASTNode {
        val declaration = parseDeclaration()
        if (currentTokenIndex < tokens.size && isCurrentToken(TokenType.EQUALS)) {
            getTokenAndAdvance()
            val assignation = parseExpression() as ASTNode
            if (isCurrentToken(TokenType.SEMICOLON)) {
                getTokenAndAdvance()
                return DeclarationAssignationNode(
                    declaration,
                    assignation,
                )
            } else {
                throwParseException(
                    "';'",
                    getCurrentSignificantToken().value,
                    getCurrentSignificantToken().column,
                    getCurrentSignificantToken().line,
                )
            }
        }
        if (isCurrentToken(TokenType.SEMICOLON)) {
            getTokenAndAdvance()
            return declaration
        } else {
            throwParseException(
                "';'",
                getCurrentSignificantToken().value,
                getCurrentSignificantToken().column,
                getCurrentSignificantToken().line,
            )
        }
        return DeclarationNode("", Position(0, 0), "", Position(0, 0))
    }

    fun parsePrintln(): MethodNode {
        val printlnToken = getTokenAndAdvance()
        expectToken(TokenType.OPEN_PARENTHESIS, "'('")

        getTokenAndAdvance()
        val content = parseExpression()
        expectToken(TokenType.CLOSE_PARENTHESIS, "')'")

        getTokenAndAdvance()

        expectToken(TokenType.SEMICOLON, "';'")
        getTokenAndAdvance()

        return MethodNode("println", content as BinaryNode, Position(printlnToken.column, printlnToken.line))
    }

    fun parseReadInput(): MethodNode {
        val readInputToken = getTokenAndAdvance()

        expectToken(TokenType.OPEN_PARENTHESIS, "'('")
        getTokenAndAdvance()

        if (!isCurrentToken(TokenType.STRING_LITERAL) && !isCurrentToken(TokenType.NUMERIC_LITERAL)) {
            throwParseException(
                "String or Number",
                getCurrentSignificantToken().value,
                getCurrentSignificantToken().column,
                getCurrentSignificantToken().line,
            )
        }
        val content = getTokenAndAdvance()

        expectToken(TokenType.CLOSE_PARENTHESIS, "')'")
        getTokenAndAdvance()

        expectToken(TokenType.SEMICOLON, "';'")

        return MethodNode("readInput", StringOperatorNode(content.value, Position(content.column, content.line)), Position(readInputToken.column, readInputToken.line))
    }

    fun parseReadEnv(): MethodNode {
        val readEnvToken = getTokenAndAdvance() // readEnv

        expectToken(TokenType.OPEN_PARENTHESIS, "'('")
        getTokenAndAdvance()

        expectToken(TokenType.STRING_LITERAL, "String")
        val envVariableName = getTokenAndAdvance()

        expectToken(TokenType.CLOSE_PARENTHESIS, "')'")
        getTokenAndAdvance()

        expectToken(TokenType.SEMICOLON, "';'")
        getTokenAndAdvance()

        return MethodNode(
            "readEnv",
            StringOperatorNode(envVariableName.value, Position(envVariableName.column, envVariableName.line)),
            Position(readEnvToken.column, readEnvToken.line),
        )
    }

    private fun isCurrentToken(type: TokenType): Boolean {
        return getCurrentSignificantToken().type == type
    }

    private fun getTokenAndAdvance(): Token {
        var currentToken = getCurrentToken()
        while (currentToken.type == TokenType.WHITESPACE) {
            currentTokenIndex++
            currentToken = getCurrentToken()
        }
        currentTokenIndex++
        return currentToken
    }

    private fun getCurrentToken(): Token {
        return tokens[currentTokenIndex]
    }

    private fun getCurrentSignificantToken(): Token {
        var index = currentTokenIndex
        while (index < tokens.size) {
            val token = tokens[index]
            if (token.type != TokenType.WHITESPACE) {
                return token
            }
            index++
        }

        throw RuntimeException("No hay mas tokens significativos")
    }

    private fun getNextSignificantToken(): Token {
        var index = currentTokenIndex + 1
        while (index < tokens.size) {
            val token = tokens[index]
            if (token.type != TokenType.WHITESPACE) {
                return token
            }
            index++
        }

        throw RuntimeException("No hay mas tokens significativos")
    }

    private fun throwParseException(
        expected: String,
        found: String,
        column: Int,
        line: Int,
    ) {
        throw RuntimeException("Expected $expected but found $found at column $column, line $line")
    }

    fun expectToken(
        expectedType: TokenType,
        expectedValue: String,
    ) {
        if (!isCurrentToken(expectedType)) {
            val currentToken = getCurrentSignificantToken()
            throwParseException(expectedValue, currentToken.value, currentToken.column, currentToken.line)
        }
    }
}
