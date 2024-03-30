package parser

import ast.*
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
            (isCurrentToken(TokenType.PLUS) || isCurrentToken(TokenType.MINUS))) {
            val token = getTokenAndAdvance()
            val rightNode = parseMultiplication()
            node = BinaryOperationNode(token.value, node, rightNode)
        }
        return node
    }

    fun parseMultiplication(): ASTNode? {
        var node: ASTNode? = parseContent()
        while (currentTokenIndex < tokens.size &&
            (isCurrentToken(TokenType.MULTIPLY) || isCurrentToken(TokenType.DIVIDE))) {
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
                NumberOperatorNode(currentToken.value.toDouble())
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
                parseDeclaration()
            }
            else -> null
        }
    }

    fun parseDeclaration(): DeclarationNode {
        getTokenAndAdvance()
        if (!isCurrentToken(TokenType.IDENTIFIER)) {
            throw RuntimeException("Expected identifier after 'let' but found: " + getCurrentToken().value)
        }
        val identifier = getTokenAndAdvance().value

        if (!isCurrentToken(TokenType.COLON)) {
            throw RuntimeException("Expected ':' after identifier")
        }
        getTokenAndAdvance()

        if (!isCurrentToken(TokenType.NUMERIC_LITERAL) && !isCurrentToken(TokenType.STRING_TYPE)) {
            throw RuntimeException("Expected type after ':'")
        }
        val type = getTokenAndAdvance().value

        if (!isCurrentToken(TokenType.SEMICOLON)) {
            throw RuntimeException("Expected ';' at the end of the declaration")
        }
        getTokenAndAdvance()

        return DeclarationNode(identifier, type)
    }

    fun parseAssignation(): AssignationNode {   // x=5
        val initialToken = getTokenAndAdvance()

        if (isCurrentToken(TokenType.EQUALS)) {
            getTokenAndAdvance()

            if (isCurrentToken(TokenType.NUMERIC_LITERAL)) {
                val rightNode = parseExpression()
                return AssignationNode(initialToken.value, rightNode as BinaryNode)
            } else {
                throw RuntimeException("Expected a numeric literal after '=' in line: ${initialToken.lineNumber} and position: ${initialToken.position}")
            }
        } else {
            throw RuntimeException("Expected '=' after an identifier in line: ${initialToken.lineNumber} and position: ${initialToken.position}")
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
}



