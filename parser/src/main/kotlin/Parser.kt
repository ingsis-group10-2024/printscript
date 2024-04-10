package parser

import ast.ASTNode
import ast.AssignationNode
import ast.BinaryNode
import ast.BinaryOperationNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import common.token.TokenType
import token.Token

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
        val currentToken = getCurrentToken()

        return when (currentToken.type) {
            TokenType.NUMERIC_LITERAL -> {
                getTokenAndAdvance()
                NumberOperatorNode(currentToken.value.toDouble())
            }
            TokenType.STRING_LITERAL -> {
                getTokenAndAdvance()
                StringOperatorNode(currentToken.value)
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
            TokenType.PRINTLN -> {
                parsePrintln()
                null
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

        if (!isCurrentToken(TokenType.NUMBER_TYPE) && !isCurrentToken(TokenType.STRING_TYPE)) {
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
                    "Expected num after an identifier in line: ${getCurrentToken().lineNumber} and position: ${getCurrentToken().position}",
                )
            }
        } else {
            throw RuntimeException(
                "Expected '=' after an identifier in line: ${getCurrentToken().lineNumber} and position: ${getCurrentToken().position}",
            )
        }
    }

    fun parseDeclarationAssignation(): ASTNode? {
        val declaration = parseDeclaration()
        if (currentTokenIndex < tokens.size && isCurrentToken(TokenType.EQUALS)) {
            getTokenAndAdvance()
            val assignation = parseExpression() as BinaryNode
            return DeclarationAssignationNode(declaration, assignation)
        } else {
            return declaration
        }
    }

    fun parsePrintln(): MethodNode {
        val methodName = getTokenAndAdvance()
        if (!isCurrentToken(TokenType.OPEN_PARENTHESIS)) {
            throwParseException(getCurrentToken().value, "'('", getCurrentToken().lineNumber, getCurrentToken().position)
        }
        getTokenAndAdvance()
        if (!isCurrentToken(TokenType.STRING_LITERAL)) {
            throwParseException(getCurrentToken().value, "string", getCurrentToken().lineNumber, getCurrentToken().position)
        }
        val content = parseExpression()
        if (!isCurrentToken(TokenType.CLOSE_PARENTHESIS)) {
            throwParseException(getCurrentToken().value, "')'", getCurrentToken().lineNumber, getCurrentToken().position)
        }
        getTokenAndAdvance()
        if (!isCurrentToken(TokenType.SEMICOLON)) {
            throwParseException(getCurrentToken().value, "';'", getCurrentToken().lineNumber, getCurrentToken().position)
        }
        getTokenAndAdvance()

        return MethodNode(methodName.value, content as BinaryNode)
    }

    private fun isCurrentToken(type: TokenType): Boolean {
        return getCurrentToken().type == type
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

    private fun throwParseException(
        found: String,
        expected: String,
        lineNumber: Int,
        position: Int,
    ) {
        throw RuntimeException("Expected $expected but found $found in line $lineNumber column $position")
    }
}
