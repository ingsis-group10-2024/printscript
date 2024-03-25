package parser

import ast.*
import common.token.Token
import common.token.TokenType

class Parser(private val tokens: List<Token>) {

    private var currentTokenIndex = 0

    fun generateAST(): BinaryNode? {
        return parseExpression()
    }

    private fun parseExpression(): BinaryNode? {
        return parseAddition()
    }

    fun parseAddition(): BinaryNode? {
        var node: BinaryNode? = parseMultiplication()

        while (currentTokenIndex < tokens.size && (isCurrentToken(TokenType.PLUS) || isCurrentToken(TokenType.MINUS))) {
            val token = consumeCurrentToken()
            val rightNode = parseMultiplication()
            node = BinaryOperationNode(token.value, node, rightNode)
        }

        return node
    }

    fun parseMultiplication(): BinaryNode? {
        var node: BinaryNode? = parseContent() as? BinaryNode

        while (currentTokenIndex < tokens.size &&
            (isCurrentToken(TokenType.MULTIPLY) || isCurrentToken(TokenType.DIVIDE))) {
            val token = consumeCurrentToken()
            val rightNode = parseContent() as? BinaryNode

            node = BinaryOperationNode(token.value, node, rightNode)
        }

        return node
    }



    fun parseContent(): BinaryNode? {
        val currentToken = getCurrentToken()

        return when (currentToken.type) {
            TokenType.NUMBER_TYPE -> {
                consumeCurrentToken()
                NumberOperatorNode(currentToken.value.toDouble())
            }
            TokenType.IDENTIFIER -> {
                consumeCurrentToken()
                IdentifierOperatorNode(currentToken.value)
            }
            TokenType.STRING_TYPE -> {
                consumeCurrentToken()
                StringOperatorNode(currentToken.value)
            }
            TokenType.OPEN_PARENTHESIS -> {
                consumeCurrentToken()
                val node = parseExpression()
                this.getTokenAndAdvance(TokenType.CLOSE_PARENTHESIS)
                node as? BinaryNode
            }
            else -> null
        }
    }

    private fun isCurrentToken(type: TokenType): Boolean {
        return getCurrentToken().type == type
    }

    private fun consumeCurrentToken(): Token {
        val token = getCurrentToken()
        currentTokenIndex++
        return token
    }

    private fun getTokenAndAdvance(type: TokenType): Token {
        val currentToken = getCurrentToken()
        if (currentToken.type == type) {
            currentTokenIndex++
            return currentToken
        } else {
            throw RuntimeException("Expected token of type $type but found ${currentToken.type}")
        }
    }

    private fun getCurrentToken(): Token {
        return tokens[currentTokenIndex]
    }

    /*private fun insertNode(root: Node?, newNode: Node): Node {
        if (root == null) {
            return newNode
        }

        if (newNode < root) {
            root.left = insertNode(root.left, newNode)
        } else {
            root.right = insertNode(root.right, newNode)
        }

        return root
    }


    *//** DECLARACION DE VARIABLES
     *  Variables con el keyword “let”
     *  Sin inferencia de tipos, es decir se debe aclarar el tipo de la variable (let <identifier> : <type> ; )
     *  Ejemplo: let x: number;
     *  Se puede declarar sólo una variable por sentencia.
     *  Se puede declarar y asignar un valor en una misma sentencia.
     *//*
    private fun parseDeclaration(): DeclarationNode {
        this.getTokenAndAdvance(TokenType.LET)
        val declarationType = TokenType.LET

        val identifierToken = this.getTokenAndAdvance(TokenType.IDENTIFIER)
        val declarationNode = DeclarationNode(identifierToken.toString(), declarationType.toString())
        val identifierNode = Node(identifierToken.type, headValue = identifierToken.value)
        declarationNode.left = identifierNode

        this.getTokenAndAdvance(TokenType.COLON)

        val typeToken = if (getCurrentToken().type == TokenType.NUMBER_TYPE) {
            this.getTokenAndAdvance(TokenType.NUMBER_TYPE)
        } else {
            this.getTokenAndAdvance(TokenType.STRING_TYPE)
        }
        val typeNode = Node(typeToken.type, headValue = typeToken.value)
        declarationNode.right = typeNode

        this.getTokenAndAdvance(TokenType.SEMICOLON)

        return declarationNode
    }


    fun parsePrintlnStatement(): Node {
        this.getTokenAndAdvance(TokenType.PRINTLN)
        val statementType = TokenType.PRINTLN
        val statementNode = Node(statementType, headValue = statementType.toString())
        this.getTokenAndAdvance(TokenType.OPEN_PARENTHESIS)
        val content = parseContent()
        statementNode.right = content
        this.getTokenAndAdvance(TokenType.CLOSE_PARENTHESIS)
        this.getTokenAndAdvance(TokenType.SEMICOLON)

        return statementNode
    }

    fun parseContent(): Node {
        val currentToken = getCurrentToken()
        currentTokenIndex++
        return when (currentToken.type) {
            TokenType.STRING_LITERAL -> {
                Node(currentToken.type, headValue = currentToken.value)
            }
            TokenType.NUMERIC_LITERAL -> {
                Node(currentToken.type, headValue = currentToken.value)
            }
            TokenType.IDENTIFIER -> {
                Node(currentToken.type, headValue = currentToken.value)
            }
            else -> {
                throw RuntimeException("Token de tipo ${currentToken.type} inesperado en la línea ${currentTokenIndex}")
            }
        }
    }*/

}
