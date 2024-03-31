
import ast.BinaryOperationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.NumberOperatorNode
import common.token.Token
import common.token.TokenType
import org.junit.Test

import parser.Parser
import kotlin.test.assertEquals

class ParserTest {

    @Test
    fun testParseAddition() {
        val tokens = listOf(
            Token(TokenType.NUMBER_TYPE, "5", 1,0),
            Token(TokenType.PLUS, "+", 2,0),
            Token(TokenType.NUMBER_TYPE, "3", 3,0)
        )

        val parser = Parser(tokens)
        val result = parser.parseAddition()

        val expected = BinaryOperationNode(
            "+",
            NumberOperatorNode(5.0),
            NumberOperatorNode(3.0)
        )

        assertEquals(expected, result)
    }

    @Test
    fun testParseMultiplication() {
        val tokens = listOf(
            Token(TokenType.NUMBER_TYPE, "2", 1,0),
            Token(TokenType.MULTIPLY, "*", 2,0),
            Token(TokenType.NUMBER_TYPE, "8", 3,0)
        )

        val parser = Parser(tokens)
        val result = parser.parseMultiplication()

        val expected = BinaryOperationNode(
            "*",
            NumberOperatorNode(2.0),
            NumberOperatorNode(8.0)
        )

        assertEquals(expected, result)
    }

    @Test
    fun testParserComplexExpression() {
        val tokens = listOf(
            Token(TokenType.NUMBER_TYPE, "5", 1,0),
            Token(TokenType.PLUS, "+", 2,0),
            Token(TokenType.NUMBER_TYPE, "3", 3,0),
            Token(TokenType.MULTIPLY, "*", 4,0),
            Token(TokenType.NUMBER_TYPE, "2", 5,0)
        )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        // El resultado esperado es: (5 + (3 * 2))
        val expected = BinaryOperationNode(
            "+",
            NumberOperatorNode(5.0),
            BinaryOperationNode(
                "*",
                NumberOperatorNode(3.0),
                NumberOperatorNode(2.0)
            )
        )

        assertEquals(expected, result)
    }

    @Test
    fun testParseDeclaration() {
        val tokens = listOf(
            Token(TokenType.LET, "let", 1,0),
            Token(TokenType.IDENTIFIER, "x", 2,0),
            Token(TokenType.COLON, ":", 3,0),
            Token(TokenType.NUMBER_TYPE, "number", 4,0),
            Token(TokenType.SEMICOLON, ";", 5,0)
        )

        val parser = Parser(tokens)
        val result = parser.parseDeclaration()

        val expected = DeclarationNode("x", "number")

        assertEquals(expected, result)
    }

    /*@Test
    fun testGenerateAST() {
        val tokens = listOf(
            Token(TokenType.LET, "let", 1),
            Token(TokenType.IDENTIFIER, "x", 2),
            Token(TokenType.COLON, ":", 3),
            Token(TokenType.NUMBER_TYPE, "number", 4),
            Token(TokenType.SEMICOLON, ";", 5)
        )

        val parser = Parser(tokens)
        val ast = parser.generateAST()

        // Verificar que el AST se haya generado correctamente
        val expectedAST = AST(Node(TokenType.LET, null, null, "LET"))
        val rootNode = expectedAST.getRootNode()
        rootNode?.left = Node(TokenType.IDENTIFIER, null, null, "x")
        rootNode?.right = Node(TokenType.NUMBER_TYPE, null, null, "number")

        assertEquals(expectedAST, ast, "El AST generado no es el esperado")
    }

    @Test
    fun testInvalidToken() {
        val tokens = listOf(
            Token(TokenType.LET, "let", 1),
            Token(TokenType.IDENTIFIER, "x", 2),
            Token(TokenType.COLON, ":", 3),
            Token(TokenType.NUMBER_TYPE, "number", 4),
            Token(TokenType.EQUALS, "=", 5),
            Token(TokenType.INVALID, "$$$", 6) // Token inválido
        )

        // Verificar que el parser arroje una excepción cuando se encuentra con un token inválido
        val parser = Parser(tokens)
        var exceptionThrown = false
        try {
            parser.generateAST()
        } catch (e: RuntimeException) {
            exceptionThrown = true
        }
        assertEquals(true, exceptionThrown, "Se esperaba una excepción al encontrar un token inválido")
    }

    @Test
    fun testParseContent() {
        val tokens = listOf(
            Token(TokenType.STRING_LITERAL, "\"Hello\"", 1),
            Token(TokenType.NUMERIC_LITERAL, "42", 2),
            Token(TokenType.IDENTIFIER, "variable", 3)
        )

        val parser = Parser(tokens)

        // Verificar que el parser pueda parsear correctamente los diferentes tipos de contenido
        val stringContent = parser.parseContent()
        assertEquals(Node(TokenType.STRING_LITERAL, null, null, "\"Hello\""), stringContent)

        val numericContent = parser.parseContent()
        assertEquals(Node(TokenType.NUMERIC_LITERAL, null, null, "42"), numericContent)

        val identifierContent = parser.parseContent()
        assertEquals(Node(TokenType.IDENTIFIER, null, null, "variable"), identifierContent)
    }

    @Test
    fun testParsePrintlnStatement() {
        val tokens = listOf(
            Token(TokenType.PRINTLN, "println", 1),
            Token(TokenType.OPEN_PARENTHESIS, "(", 2),
            Token(TokenType.STRING_LITERAL, "\"Hello, world!\"", 3),
            Token(TokenType.CLOSE_PARENTHESIS, ")", 4),
            Token(TokenType.SEMICOLON, ";", 5)
        )

        val parser = Parser(tokens)

        // Verificar que el parser pueda parsear correctamente la sentencia println
        val printlnStatement = parser.parsePrintlnStatement()

        // Crear el nodo esperado para la sentencia println
        val printlnNode = Node(TokenType.PRINTLN, null, null, "PRINTLN")
        val stringLiteralNode = Node(TokenType.STRING_LITERAL, null, null, "\"Hello, world!\"")
        printlnNode.right = stringLiteralNode

        // Comparar el nodo obtenido con el nodo esperado
        assertEquals(printlnNode, printlnStatement)
    }*/

}
