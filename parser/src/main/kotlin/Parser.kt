package parser

import ast.ASTNode
import ast.AssignationNode
import ast.BinaryNode
import ast.BinaryOperationNode
import ast.BooleanOperatorNode
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
        return generateAST(tokens)
    }

    fun generateAST(tokens: List<Token>): List<ASTNode> {
        val nodes = mutableListOf<ASTNode>()
        while (currentTokenIndex < tokens.size - 1) {
            if (isCurrentToken(TokenType.IF)) {
                nodes.add(parseIf())
                currentTokenIndex++
            } else {
                val statementTokens = getStatement(tokens, currentTokenIndex, TokenType.SEMICOLON)
                currentTokenIndex += statementTokens.size
                if (statementTokens.isEmpty()) {
                    throw RuntimeException(
                        "Invalid statement at column ${getCurrentSignificantToken().column}, line ${getCurrentSignificantToken().row}",
                    )
                }
                nodes.add(parseStatement(statementTokens) as ASTNode)
                getTokenAndAdvance() // Salteo el punto y coma
            }
        }
        return nodes
    }

    private fun parseStatement(tokens: List<Token>): Any? {
        currentTokenIndex = currentTokenIndex - tokens.size

        return when (getCurrentSignificantToken().type) {
            TokenType.NUMERIC_LITERAL, TokenType.STRING_LITERAL, TokenType.IDENTIFIER, TokenType.STRING_TYPE,
            TokenType.OPEN_PARENTHESIS, TokenType.LET, TokenType.CONST, TokenType.PRINTLN, TokenType.READINPUT, TokenType.IF,
            TokenType.READENV,
            -> parseExpression()
            else ->
                throwParseException(
                    "valid statement",
                    getCurrentSignificantToken().value,
                    getCurrentSignificantToken().column,
                    getCurrentSignificantToken().row,
                )
        }
    }

    private fun parseExpression(): ASTNode? {
        return parseAddition()
    }

    private fun parseAddition(): ASTNode? {
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

    private fun parseMultiplication(): ASTNode? {
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
        val currentToken = getCurrentSignificantToken()

        return when (currentToken.type) {
            TokenType.NUMERIC_LITERAL -> {
                val token = getTokenAndAdvance()
                NumberOperatorNode(token.value.toDouble(), Position(token.column, token.row))
            }
            TokenType.STRING_LITERAL -> {
                val token = getTokenAndAdvance()
                StringOperatorNode(token.value, TokenType.STRING_LITERAL, Position(token.column, token.row))
            }
            TokenType.BOOLEAN_LITERAL -> {
                val token = getTokenAndAdvance()
                BooleanOperatorNode(token.value.toBoolean(), Position(token.column, token.row))
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
                    IdentifierOperatorNode(token.value, Position(token.column, token.row))
                }
            }
            TokenType.STRING_TYPE -> {
                val token = getTokenAndAdvance()
                StringOperatorNode(token.value, TokenType.STRING_LITERAL, Position(token.column, token.row))
            }

            TokenType.BOOLEAN_TYPE -> {
                val token = getTokenAndAdvance()
                BooleanOperatorNode(token.value.toBoolean(), Position(token.column, token.row))
            }

            TokenType.OPEN_PARENTHESIS -> {
                getTokenAndAdvance()
                val node = parseExpression()
                this.getTokenAndAdvance()
                node
            }
            TokenType.LET -> {
                parseDeclarationAssignation(TokenType.LET)
            }
            TokenType.CONST -> {
                parseDeclarationAssignation(TokenType.CONST)
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

    private fun parseDeclaration(tokenType: TokenType): DeclarationNode {
        getTokenAndAdvance() // let

        expectToken(TokenType.IDENTIFIER, "identifier")
        val identifierToken = getTokenAndAdvance()

        expectToken(TokenType.COLON, "':'")
        getTokenAndAdvance()

        if (!isCurrentToken(TokenType.NUMBER_TYPE) &&
            !isCurrentToken(TokenType.STRING_TYPE) &&
            !isCurrentToken(TokenType.BOOLEAN_TYPE)
        ) {
            val currentToken = getCurrentSignificantToken()
            throwParseException("type", currentToken.value, currentToken.column, currentToken.row)
        }
        val typeToken = getTokenAndAdvance()
        return DeclarationNode(
            identifierToken.value,
            tokenType,
            Position(identifierToken.column, identifierToken.row),
            typeToken.value,
            Position(typeToken.column, typeToken.row),
        )
    }

    private fun parseAssignation(identifierToken: Token): AssignationNode {
        if (isCurrentToken(TokenType.EQUALS) || isCurrentToken(TokenType.EQUALS_EQUALS)) {
            getTokenAndAdvance()
            val expression = parseExpression()
            return AssignationNode(
                identifierToken.value,
                Position(identifierToken.column, identifierToken.row),
                expression as BinaryNode,
            )
        }
        throwParseException(
            "'=' or '=='",
            getCurrentSignificantToken().value,
            getCurrentSignificantToken().column,
            getCurrentSignificantToken().row,
        )
        return AssignationNode("", Position(0, 0), StringOperatorNode(" ", TokenType.STRING_LITERAL, Position(0, 0)))
    }

    private fun parseDeclarationAssignation(tokenType: TokenType): ASTNode {
        val declaration = parseDeclaration(tokenType)
        if (currentTokenIndex < tokens.size && isCurrentToken(TokenType.EQUALS)) {
            getTokenAndAdvance()
            val assignation = parseExpression() as ASTNode
            return DeclarationAssignationNode(
                declaration,
                assignation,
            )
        }
        return declaration
    }

    private fun parsePrintln(): MethodNode {
        val printlnToken = getTokenAndAdvance()

        expectToken(TokenType.OPEN_PARENTHESIS, "'('")
        getTokenAndAdvance()

        val content = parseExpression()
        expectToken(TokenType.CLOSE_PARENTHESIS, "')'")
        getTokenAndAdvance()

        return MethodNode("println", content as BinaryNode, Position(printlnToken.column, printlnToken.row))
    }

    private fun parseReadInput(): MethodNode {
        val readInputToken = getTokenAndAdvance()

        expectToken(TokenType.OPEN_PARENTHESIS, "'('")
        getTokenAndAdvance()

        if (!isCurrentToken(TokenType.STRING_LITERAL) && !isCurrentToken(TokenType.NUMERIC_LITERAL)) {
            throwParseException(
                "string or number",
                getCurrentSignificantToken().value,
                getCurrentSignificantToken().column,
                getCurrentSignificantToken().row,
            )
        }
        val content = getTokenAndAdvance()

        expectToken(TokenType.CLOSE_PARENTHESIS, "')'")
        getTokenAndAdvance()

        return MethodNode(
            "readInput",
            StringOperatorNode(content.value, TokenType.STRING_LITERAL, Position(content.column, content.row)),
            Position(readInputToken.column, readInputToken.row),
        )
    }

    private fun parseIf(): ASTNode {
        getTokenAndAdvance() // if
        expectToken(TokenType.OPEN_PARENTHESIS, "'('")
        getTokenAndAdvance()

        val condition = parseExpression()
        expectToken(TokenType.CLOSE_PARENTHESIS, "')'")
        getTokenAndAdvance()

        expectToken(TokenType.OPEN_BRACKET, "'{'")
        getTokenAndAdvance()

        val trueContent = getStatement(tokens, currentTokenIndex, TokenType.CLOSE_BRACKET)
        val trueBranch = mutableListOf<ASTNode>()
        if (!trueContent.isEmpty()) {
            var index = 0
            while (index < trueContent.size) {
                val statement = getStatement(trueContent, index, TokenType.SEMICOLON)
                currentTokenIndex += statement.size
                trueBranch.add(parseStatement(statement) as ASTNode)
                getTokenAndAdvance()
                index += statement.size + 1
            }
        }

        expectToken(TokenType.CLOSE_BRACKET, "'}'")
        getTokenAndAdvance()

        val elseBranch = mutableListOf<ASTNode>()
        if (isCurrentToken(TokenType.ELSE)) {
            getTokenAndAdvance()
            expectToken(TokenType.OPEN_BRACKET, "'{'")
            getTokenAndAdvance()
            val elseContent = getStatement(tokens, currentTokenIndex, TokenType.CLOSE_BRACKET)
            if (!elseContent.isEmpty()) {
                var index = 0
                while (index < elseContent.size) {
                    val statement = getStatement(elseContent, index, TokenType.SEMICOLON)
                    currentTokenIndex += statement.size
                    elseBranch.add(parseStatement(statement) as ASTNode)
                    getTokenAndAdvance()
                    index += statement.size + 1
                }
            }
            expectToken(TokenType.CLOSE_BRACKET, "'}'")
        }

        return IfNode(
            condition as ASTNode,
            trueBranch,
            elseBranch,
        )
    }

    private fun parseCondition(token: Token): ConditionNode {
        val left = IdentifierOperatorNode(token.value, Position(token.column, token.row))
        val operator = getTokenAndAdvance().value
        val rightStatement = getStatement(tokens, currentTokenIndex, TokenType.CLOSE_PARENTHESIS)
        currentTokenIndex += rightStatement.size
        val right = parseStatement(rightStatement) as ASTNode
        return ConditionNode(operator, left, right)
    }

    private fun parseReadEnv(): MethodNode {
        val readEnvToken = getTokenAndAdvance() // readEnv

        expectToken(TokenType.OPEN_PARENTHESIS, "'('")
        getTokenAndAdvance()

        expectToken(TokenType.STRING_LITERAL, "string")
        val envVariableName = getTokenAndAdvance()

        expectToken(TokenType.CLOSE_PARENTHESIS, "')'")
        getTokenAndAdvance()

        return MethodNode(
            "readEnv",
            StringOperatorNode(
                envVariableName.value,
                TokenType.STRING_LITERAL,
                Position(envVariableName.column, envVariableName.row),
            ),
            Position(readEnvToken.column, readEnvToken.row),
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

    private fun expectToken(
        expectedType: TokenType,
        expectedValue: String,
    ) {
        if (!isCurrentToken(expectedType)) {
            val currentToken = getCurrentSignificantToken()
            throwParseException(expectedValue, currentToken.value, currentToken.column, currentToken.row)
        }
    }

    fun getStatement(
        tokens: List<Token>,
        fromIndex: Int,
        finalTokenType: TokenType,
    ): List<Token> {
        val statement = mutableListOf<Token>()
        var i = fromIndex

        while (i < tokens.size) {
            val token = tokens[i]

            if (token.type == finalTokenType) {
                return statement
            }

            statement.add(token)
            i++
        }

        throw RuntimeException("Se debe terminar el statement con un '${finalTokenType.name}'")
    }
}
