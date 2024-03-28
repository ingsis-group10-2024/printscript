package parser

import ast.BinaryNode
import ast.BinaryOperationNode
import ast.DeclarationNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import common.token.Token
import common.token.TokenType

class Parser(private val tokens: List<Token>) {
    private var currentTokenIndex = 0

    fun generateAST(): BinaryNode? {
        if (tokens.isEmpty()) {
            return null
        }
        return parseExpression()
    }

    private fun parseExpression(): BinaryNode? {
        return parseAddition()
    }

    fun parseAddition(): BinaryNode? {
        var node: BinaryNode? = parseMultiplication()

        while (currentTokenIndex < tokens.size &&
            (isCurrentToken(TokenType.PLUS) || isCurrentToken(TokenType.MINUS))
        ) {
            val token = getTokenAndAdvance()
            val rightNode = parseMultiplication()
            node = BinaryOperationNode(token.value, node, rightNode)
        }

        return node
    }

    fun parseMultiplication(): BinaryNode? {
        var node: BinaryNode? = parseContent()

        while (currentTokenIndex < tokens.size &&
            (isCurrentToken(TokenType.MULTIPLY) || isCurrentToken(TokenType.DIVIDE))
        ) {
            val token = getTokenAndAdvance()
            val rightNode = parseContent()

            node = BinaryOperationNode(token.value, node, rightNode)
        }

        return node
    }

    fun parseDeclaration(): BinaryNode? {
        getTokenAndAdvance()

        if (!isCurrentToken(TokenType.IDENTIFIER)) {
            throw RuntimeException("Expected identifier after 'let' but found: ${getCurrentToken().value}")
        }
        val identifier = getTokenAndAdvance().value

        if (!isCurrentToken(TokenType.COLON)) {
            throw RuntimeException("Expected ':' after identifier")
        }
        getTokenAndAdvance()

        if (!isCurrentToken(TokenType.NUMBER_TYPE) && !isCurrentToken(TokenType.STRING_TYPE)) {
            throw RuntimeException("Expected type after ':'")
        }
        val type = getTokenAndAdvance().value

        if (!isCurrentToken(TokenType.SEMICOLON)) {
            throw RuntimeException("Expected ';' at the end of the declaration")
        }
        getTokenAndAdvance()

        return DeclarationNode(identifier, type)
    }

    private fun parseContent(): BinaryNode? {
        val currentToken = getCurrentToken()

        return when (currentToken.type) {
            TokenType.NUMBER_TYPE -> {
                getTokenAndAdvance()
                NumberOperatorNode(currentToken.value.toDouble())
            }
            TokenType.IDENTIFIER -> {
                getTokenAndAdvance()
                StringOperatorNode(currentToken.value)
            }
            TokenType.STRING_TYPE -> {
                getTokenAndAdvance()
                StringOperatorNode(currentToken.value)
            }
            TokenType.OPEN_PARENTHESIS -> {
                getTokenAndAdvance()
                val node = parseExpression()
                getTokenAndAdvance()
                node
            }
            TokenType.LET -> {
                parseDeclaration()
            }
            else -> null
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
