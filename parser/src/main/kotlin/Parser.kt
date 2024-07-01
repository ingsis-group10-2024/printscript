package parser

import ast.ASTNode
import ast.AssignationNode
import ast.BinaryNode
import ast.BinaryOperationNode
import ast.ConditionNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.IfNode
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
        while (currentTokenIndex < tokens.size - 1) {
            if (isCurrentToken(TokenType.IF)) {
                nodes.add(parseIf())
            } else {
                val statementTokens = getStatement(tokens, TokenType.SEMICOLON)
                if (statementTokens.isEmpty()) {
                    throw RuntimeException("Invalid statement")
                }
                nodes.add(parseStatement(statementTokens) as ASTNode)
            }
        }
        return nodes
    }

    private fun parseStatement(tokens: List<Token>): Any? {
        currentTokenIndex = currentTokenIndex - tokens.size - 1

        return when (getCurrentSignificantToken().type) {
            TokenType.NUMERIC_LITERAL, TokenType.STRING_LITERAL, TokenType.IDENTIFIER, TokenType.STRING_TYPE,
            TokenType.OPEN_PARENTHESIS, TokenType.LET, TokenType.PRINTLN, TokenType.READINPUT, TokenType.IF,
            TokenType.READENV,
            -> parseExpression()
            else ->
                throwParseException(
                    "valid statement",
                    getCurrentSignificantToken().value,
                    getCurrentSignificantToken().column,
                    getCurrentSignificantToken().line,
                )
        }
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
                val token = getTokenAndAdvance()
                if (isCurrentToken(TokenType.EQUALS)) {
                    // Es una assignation:  x=5
                    parseAssignation(token)
                } else if (isCurrentToken(TokenType.EQUALS_EQUALS) || isCurrentToken(TokenType.UNEQUALS)) {
                    // Es una condition: x==algo o x!=algo
                    parseCondition(token)
                } else {
                    // Es un identifier: x
                    IdentifierOperatorNode(token.value, Position(token.column, token.line))
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
        getTokenAndAdvance() // let

        expectToken(TokenType.IDENTIFIER, "identifier")
        val identifierToken = getTokenAndAdvance()

        expectToken(TokenType.COLON, "':'")
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

    fun parseAssignation(identifierToken: Token): AssignationNode {
        if (isCurrentToken(TokenType.EQUALS) || isCurrentToken(TokenType.EQUALS_EQUALS)) {
            getTokenAndAdvance()
            val expression = parseExpression()
            return AssignationNode(
                identifierToken.value,
                Position(identifierToken.column, identifierToken.line),
                expression as BinaryNode,
            )
        }
        throwParseException(
            "'=' or '=='",
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
            getTokenAndAdvance()
            return DeclarationAssignationNode(
                declaration,
                assignation,
            )
        }
        getTokenAndAdvance()
        return declaration
    }

    fun parsePrintln(): MethodNode {
        val printlnToken = getTokenAndAdvance()

        expectToken(TokenType.OPEN_PARENTHESIS, "'('")
        getTokenAndAdvance()

        val content = parseExpression()
        expectToken(TokenType.CLOSE_PARENTHESIS, "')'")
        getTokenAndAdvance()

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

        return MethodNode("readInput", StringOperatorNode(content.value, Position(content.column, content.line)), Position(readInputToken.column, readInputToken.line))
    }

    fun parseIf(): ASTNode {
        getTokenAndAdvance() // if
        expectToken(TokenType.OPEN_PARENTHESIS, "'('")
        getTokenAndAdvance()

        val condition = parseExpression()
        expectToken(TokenType.CLOSE_PARENTHESIS, "')'")
        getTokenAndAdvance()

        expectToken(TokenType.OPEN_BRACKET, "'{'")
        getTokenAndAdvance()

        // todo cambiar para poder tener content con varias lineas
        val content = getStatement(tokens, TokenType.CLOSE_BRACKET)
        val trueBranch = parseStatement(content) as ASTNode
        getTokenAndAdvance() // Salteo el ;

        var elseBranch: ASTNode? = null
        if (isCurrentToken(TokenType.ELSE)) {
            getTokenAndAdvance()
            expectToken(TokenType.OPEN_BRACKET, "'{'")
            getTokenAndAdvance()
            val elseContent = getStatement(tokens, TokenType.CLOSE_BRACKET)
            elseBranch = parseStatement(elseContent) as ASTNode
            getTokenAndAdvance() // Salteo el ;
        }
        return IfNode(
            condition as ASTNode,
            trueBranch,
            elseBranch,
        )
    }

    fun parseCondition(token: Token): ConditionNode {
        val left = IdentifierOperatorNode(token.value, Position(token.column, token.line))
        val operator = getTokenAndAdvance().value
        val right = parseStatement(getStatement(tokens, TokenType.CLOSE_PARENTHESIS)) as ASTNode
        return ConditionNode(operator, left, right)
    }

    fun parseReadEnv(): MethodNode {
        val readEnvToken = getTokenAndAdvance() // readEnv

        expectToken(TokenType.OPEN_PARENTHESIS, "'('")
        getTokenAndAdvance()

        expectToken(TokenType.STRING_LITERAL, "String")
        val envVariableName = getTokenAndAdvance()

        expectToken(TokenType.CLOSE_PARENTHESIS, "')'")
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

        throw RuntimeException("No hay más tokens significativos")
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

    fun getStatement(
        tokens: List<Token>,
        finalTokenType: TokenType,
    ): List<Token> {
        val statement = mutableListOf<Token>()
        var i = currentTokenIndex

        while (i < tokens.size) {
            val token = tokens[i]

            if (token.type == finalTokenType) {
                // Salteo ese finalToken para que el próximo statement empiece desde el siguiente token
                i++
                currentTokenIndex = i
                return statement
            }

            statement.add(token)
            i++
        }

        throw RuntimeException("Se debe terminar el statement con un '${finalTokenType.name}'")
    }
}
