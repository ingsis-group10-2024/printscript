package parser

import ast.ASTNode
import ast.AssignationNode
import ast.BinaryNode
import ast.BinaryOperationNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import common.token.Token
import common.token.TokenType

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

    private fun parseContent(): ASTNode? {
        val currentToken = getCurrentToken()

        return when (currentToken.type) {
            TokenType.NUMERIC_LITERAL -> {
                getTokenAndAdvance()
                NumberOperatorNode(currentToken.value.toInt())
            }
            TokenType.IDENTIFIER -> {
                // Es una assignation:  x=5
                parseAssignation()
            }
            TokenType.STRING_TYPE -> {
                getTokenAndAdvance()
                StringOperatorNode(currentToken.value)
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
            else -> null
        }
    }

    fun parseDeclaration(): DeclarationNode {
        getTokenAndAdvance()
        if (!isCurrentToken(TokenType.IDENTIFIER)) {
            throwParseException(getCurrentToken().value, "identifier", getCurrentToken().lineNumber, getCurrentToken().position)
        }
        val identifier = getTokenAndAdvance().value

        if (!isCurrentToken(TokenType.COLON)) {
            throwParseException(getCurrentToken().value, "':'", getCurrentToken().lineNumber, getCurrentToken().position)
        }
        getTokenAndAdvance()

        if (!isCurrentToken(TokenType.NUMERIC_LITERAL) && !isCurrentToken(TokenType.STRING_TYPE)) {
            throwParseException(getCurrentToken().value, "type", getCurrentToken().lineNumber, getCurrentToken().position)
        }
        val type = getTokenAndAdvance().value

        if (!isCurrentToken(TokenType.SEMICOLON)) {
            if (isCurrentToken(TokenType.EQUALS)) {
                return DeclarationNode(identifier, type)
            }
            throwParseException(getCurrentToken().value, "';'", getCurrentToken().lineNumber, getCurrentToken().position)
        }

        getTokenAndAdvance()
        return DeclarationNode(identifier, type)
    }

    fun parseAssignation(): AssignationNode {
        val initialToken = getTokenAndAdvance()

        if (isCurrentToken(TokenType.EQUALS)) {
            getTokenAndAdvance()

            if (isCurrentToken(TokenType.NUMERIC_LITERAL)) {
                val rightNode = parseExpression()
                return AssignationNode(initialToken.value, rightNode as BinaryNode)
            } else {
                throw RuntimeException(
                    "Expected a number after an identifier in line: " +
                        "${getCurrentToken().lineNumber} and position: ${getCurrentToken().position}",
                )
            }
        } else {
            throw RuntimeException(
                "Expected '=' after an identifier in line: ${getCurrentToken().lineNumber} and position: ${getCurrentToken().position}",
            )
        }
    }

    fun parseDeclarationAssignation(): ASTNode {
        val declaration = parseDeclaration()
        if (isCurrentToken(TokenType.EQUALS)) {
            getTokenAndAdvance()
            val assignation = parseExpression() as BinaryNode
            return DeclarationAssignationNode(declaration, assignation)
        } else {
            return declaration
        }
    }

    private fun isCurrentToken(type: TokenType): Boolean {
        return getCurrentToken().type == type
    }

    private fun getTokenAndAdvance(): Token {
        val currentToken = getCurrentToken()
        currentTokenIndex++
        return currentToken
    }

    private fun getCurrentToken(): Token {
        return tokens[currentTokenIndex]
    }

    fun throwParseException(
        found: String,
        expected: String,
        lineNumber: Int,
        position: Int,
    ) {
        throw RuntimeException("Expected $expected but found $found in line $lineNumber column $position")
    }
}
