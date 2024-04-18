package parser

import ast.ASTNode
import ast.AssignationNode
import ast.BinaryNode
import ast.BinaryOperationNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
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
                // Es una assignation:  x=5
                parseAssignation()
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
            val assignation = parseExpression() as BinaryNode
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
        if (!isCurrentToken(TokenType.OPEN_PARENTHESIS)) {
            throwParseException(
                "'('",
                getCurrentSignificantToken().value,
                getCurrentSignificantToken().column,
                getCurrentSignificantToken().line,
            )
        }
        getTokenAndAdvance()
        val content = parseExpression()
        if (!isCurrentToken(TokenType.CLOSE_PARENTHESIS)) {
            throwParseException(
                "')'",
                getCurrentSignificantToken().value,
                getCurrentSignificantToken().column,
                getCurrentSignificantToken().line,
            )
        }
        getTokenAndAdvance()
        if (!isCurrentToken(TokenType.SEMICOLON)) {
            throwParseException(
                "';'",
                getCurrentSignificantToken().value,
                getCurrentSignificantToken().column,
                getCurrentSignificantToken().line,
            )
        }
        getTokenAndAdvance()

        return MethodNode("println", content as BinaryNode, Position(printlnToken.column, printlnToken.line))
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

        throw RuntimeException("La línea no finaliza con punto y coma")
    }

    /*private fun throwParseException(expected: String) {
        val currentToken = getCurrentSignificantToken()
        throw RuntimeException("Expected $expected but found '${currentToken.value}' at ${currentToken.column}")
    }*/

    private fun throwParseException(
        expected: String,
        found: String,
        column: Int,
        line: Int,
    ) {
        throw RuntimeException("Expected $expected but found $found at column $column, line $line")
    }
}
