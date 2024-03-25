package parser

import ast.*
import common.token.Token
import common.token.TokenType

class Parser(private val tokens: List<Token>) {

    private var currentTokenIndex = 0

    fun generateAST(): BinaryNode? {
        if(tokens.isEmpty()) {
            return null
        }
        return parseExpression()
    }

    private fun parseExpression(): BinaryNode? {
        return parseAddition()
    }

    fun parseAddition(): BinaryNode? {
        var node: BinaryNode? = parseMultiplication()

        while (currentTokenIndex < tokens.size && (isCurrentToken(TokenType.PLUS) || isCurrentToken(TokenType.MINUS))) {
            val token = getTokenAndAdvance()
            val rightNode = parseMultiplication()
            node = BinaryOperationNode(token.value, node, rightNode)
        }

        return node
    }

    fun parseMultiplication(): BinaryNode? {
        var node: BinaryNode? = parseContent()

        while (currentTokenIndex < tokens.size &&
            (isCurrentToken(TokenType.MULTIPLY) || isCurrentToken(TokenType.DIVIDE))) {
            val token = getTokenAndAdvance()
            val rightNode = parseContent()

            node = BinaryOperationNode(token.value, node, rightNode)
        }

        return node
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
                IdentifierOperatorNode(currentToken.value)
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
            /*TokenType.STRING_LITERAL -> {
                getTokenAndAdvance()
                StringOperatorNode(currentToken.value)
            }
            TokenType.NUMERIC_LITERAL -> {
                getTokenAndAdvance()
                NumberOperatorNode(currentToken.value.toDouble())
            }*/
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
            }*/

}
