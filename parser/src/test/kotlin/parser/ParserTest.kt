import ast.AssignationNode
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
import org.junit.Assert.assertThrows
import org.junit.Test
import parser.Parser
import token.Token
import token.TokenType
import kotlin.test.assertEquals

class ParserTest {
    @Test
    fun testParseAddition() {
        val tokens =
            listOf(
                Token(TokenType.NUMERIC_LITERAL, "5", 1, 0),
                Token(TokenType.PLUS, "+", 2, 0),
                Token(TokenType.NUMERIC_LITERAL, "3", 3, 0),
                Token(TokenType.SEMICOLON, ";", 4, 0),
                Token(TokenType.NUMERIC_LITERAL, "6", 1, 0),
                Token(TokenType.MINUS, "-", 2, 0),
                Token(TokenType.NUMERIC_LITERAL, "2", 3, 0),
                Token(TokenType.SEMICOLON, ";", 4, 0),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected =
            listOf(
                BinaryOperationNode(
                    "+",
                    NumberOperatorNode(5.0, Position(1, 0)),
                    NumberOperatorNode(3.0, Position(3, 0)),
                ),
                BinaryOperationNode(
                    "-",
                    NumberOperatorNode(6.0, Position(1, 0)),
                    NumberOperatorNode(2.0, Position(3, 0)),
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun testParseMultiplication() {
        val tokens =
            listOf(
                Token(TokenType.NUMERIC_LITERAL, "2", 1, 0),
                Token(TokenType.MULTIPLY, "*", 2, 0),
                Token(TokenType.NUMERIC_LITERAL, "8", 3, 0),
                Token(TokenType.SEMICOLON, ";", 4, 0),
                Token(TokenType.NUMERIC_LITERAL, "2", 1, 0),
                Token(TokenType.DIVIDE, "/", 2, 0),
                Token(TokenType.NUMERIC_LITERAL, "8", 3, 0),
                Token(TokenType.SEMICOLON, ";", 4, 0),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected =
            listOf(
                BinaryOperationNode(
                    "*",
                    NumberOperatorNode(2.0, Position(1, 0)),
                    NumberOperatorNode(8.0, Position(3, 0)),
                ),
                BinaryOperationNode(
                    "/",
                    NumberOperatorNode(2.0, Position(1, 0)),
                    NumberOperatorNode(8.0, Position(3, 0)),
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun testParserComplexExpression() {
        val tokens =
            listOf(
                Token(TokenType.NUMERIC_LITERAL, "5", 1, 0),
                Token(TokenType.PLUS, "+", 2, 0),
                Token(TokenType.NUMERIC_LITERAL, "3", 3, 0),
                Token(TokenType.MULTIPLY, "*", 4, 0),
                Token(TokenType.NUMERIC_LITERAL, "2", 5, 0),
                Token(TokenType.SEMICOLON, ";", 6, 0),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        // El resultado esperado es: (5 + (3 * 2))
        val expected =
            listOf(
                BinaryOperationNode(
                    "+",
                    NumberOperatorNode(5.0, Position(1, 0)),
                    BinaryOperationNode(
                        "*",
                        NumberOperatorNode(3.0, Position(3, 0)),
                        NumberOperatorNode(2.0, Position(5, 0)),
                    ),
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun testParseDeclaration() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 1, 0),
                Token(TokenType.IDENTIFIER, "x", 2, 0),
                Token(TokenType.COLON, ":", 3, 0),
                Token(TokenType.NUMBER_TYPE, "number", 4, 0),
                Token(TokenType.SEMICOLON, ";", 5, 0),
                Token(TokenType.LET, "let", 1, 1),
                Token(TokenType.IDENTIFIER, "y", 2, 1),
                Token(TokenType.COLON, ":", 3, 1),
                Token(TokenType.STRING_TYPE, "string", 4, 1),
                Token(TokenType.SEMICOLON, ";", 5, 1),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected =
            listOf(
                DeclarationNode("x", Position(2, 0), "number", Position(4, 0)),
                DeclarationNode("y", Position(2, 1), "string", Position(4, 1)),
            )

        assertEquals(expected, result)
    }

    @Test
    fun testGenerateMixedAST() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 1, 0),
                Token(TokenType.IDENTIFIER, "x", 2, 0),
                Token(TokenType.COLON, ":", 3, 0),
                Token(TokenType.NUMBER_TYPE, "number", 4, 0),
                Token(TokenType.SEMICOLON, ";", 1, 0),
                Token(TokenType.NUMERIC_LITERAL, "5", 2, 1),
                Token(TokenType.PLUS, "+", 3, 1),
                Token(TokenType.NUMERIC_LITERAL, "3", 4, 1),
                Token(TokenType.MULTIPLY, "*", 5, 1),
                Token(TokenType.NUMERIC_LITERAL, "2", 6, 1),
                Token(TokenType.SEMICOLON, ";", 1, 1),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected =
            listOf(
                DeclarationNode("x", Position(2, 0), "number", Position(4, 0)),
                BinaryOperationNode(
                    "+",
                    NumberOperatorNode(5.0, Position(2, 1)),
                    BinaryOperationNode(
                        "*",
                        NumberOperatorNode(3.0, Position(4, 1)),
                        NumberOperatorNode(2.0, Position(6, 1)),
                    ),
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun testParseNumericAssignation() {
        val tokens =
            listOf(
                Token(TokenType.IDENTIFIER, "x", 1, 0),
                Token(TokenType.EQUALS, "=", 3, 0),
                Token(TokenType.NUMERIC_LITERAL, "5", 5, 0),
                Token(TokenType.SEMICOLON, ";", 7, 1),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected = listOf(AssignationNode("x", Position(1, 0), NumberOperatorNode(5.0, Position(5, 0))))

        assertEquals(expected, result)
    }

    @Test
    fun testParseComplexNumericAssignation() {
        val tokens =
            listOf(
                Token(TokenType.IDENTIFIER, "x", 1, 0),
                Token(TokenType.EQUALS, "=", 2, 0),
                Token(TokenType.NUMERIC_LITERAL, "5", 4, 0),
                Token(TokenType.PLUS, "+", 2, 1),
                Token(TokenType.NUMERIC_LITERAL, "3", 3, 1),
                Token(TokenType.MULTIPLY, "*", 4, 1),
                Token(TokenType.NUMERIC_LITERAL, "2", 5, 1),
                Token(TokenType.SEMICOLON, ";", 6, 1),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected =
            listOf(
                AssignationNode(
                    "x",
                    Position(1, 0),
                    BinaryOperationNode(
                        "+",
                        NumberOperatorNode(5.0, Position(4, 0)),
                        BinaryOperationNode(
                            "*",
                            NumberOperatorNode(3.0, Position(3, 1)),
                            NumberOperatorNode(2.0, Position(5, 1)),
                        ),
                    ),
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun testParseMixDeclarationAssignations() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 1, 1),
                Token(TokenType.IDENTIFIER, "x", 2, 1),
                Token(TokenType.COLON, ":", 3, 1),
                Token(TokenType.NUMBER_TYPE, "number", 4, 1),
                Token(TokenType.EQUALS, "=", 5, 1),
                Token(TokenType.NUMERIC_LITERAL, "5", 6, 1),
                Token(TokenType.SEMICOLON, ";", 7, 1),
                Token(TokenType.LET, "let", 1, 2),
                Token(TokenType.IDENTIFIER, "y", 2, 2),
                Token(TokenType.COLON, ":", 3, 2),
                Token(TokenType.STRING_TYPE, "string", 4, 2),
                Token(TokenType.EQUALS, "=", 5, 2),
                Token(TokenType.STRING_LITERAL, "Hello", 6, 2),
                Token(TokenType.SEMICOLON, ";", 7, 2),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected =
            listOf(
                DeclarationAssignationNode(
                    DeclarationNode("x", Position(2, 1), "number", Position(4, 1)),
                    NumberOperatorNode(5.0, Position(6, 1)),
                ),
                DeclarationAssignationNode(
                    DeclarationNode("y", Position(2, 2), "string", Position(4, 2)),
                    StringOperatorNode("Hello", Position(6, 2)),
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun testParseComplexNumberDeclarationAssignation() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 1, 2),
                Token(TokenType.IDENTIFIER, "y", 2, 2),
                Token(TokenType.COLON, ":", 3, 2),
                Token(TokenType.NUMBER_TYPE, "int", 4, 2),
                Token(TokenType.EQUALS, "=", 5, 2),
                Token(TokenType.NUMERIC_LITERAL, "5", 6, 2),
                Token(TokenType.PLUS, "+", 7, 2),
                Token(TokenType.NUMERIC_LITERAL, " 3", 8, 2),
                Token(TokenType.SEMICOLON, ";", 9, 2),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected =
            DeclarationAssignationNode(
                DeclarationNode("y", Position(2, 2), "int", Position(4, 2)),
                BinaryOperationNode(
                    "+",
                    NumberOperatorNode(5.0, Position(6, 2)),
                    NumberOperatorNode(3.0, Position(8, 2)),
                ),
            )

        assertEquals(listOf(expected), result)
    }

    @Test
    fun testParseComplexStringDeclarationAssignation() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 1, 1),
                Token(TokenType.IDENTIFIER, "x", 2, 1),
                Token(TokenType.COLON, ":", 3, 1),
                Token(TokenType.STRING_TYPE, "string", 4, 1),
                Token(TokenType.EQUALS, "=", 5, 1),
                Token(TokenType.STRING_LITERAL, "Hello", 6, 1),
                Token(TokenType.PLUS, "+", 7, 1),
                Token(TokenType.STRING_LITERAL, " world", 8, 1),
                Token(TokenType.SEMICOLON, ";", 9, 1),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected =
            listOf(
                DeclarationAssignationNode(
                    DeclarationNode("x", Position(2, 1), "string", Position(4, 1)),
                    BinaryOperationNode(
                        "+",
                        StringOperatorNode("Hello", Position(6, 1)),
                        StringOperatorNode(" world", Position(8, 1)),
                    ),
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun testVariableDeclarationAssignation() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 1, 1),
                Token(TokenType.IDENTIFIER, "w", 2, 1),
                Token(TokenType.COLON, ":", 3, 1),
                Token(TokenType.NUMBER_TYPE, "number", 4, 1),
                Token(TokenType.EQUALS, "=", 5, 1),
                Token(TokenType.IDENTIFIER, "x", 6, 1),
                Token(TokenType.PLUS, "+", 7, 1),
                Token(TokenType.IDENTIFIER, "y", 8, 1),
                Token(TokenType.SEMICOLON, ";", 9, 1),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected =
            DeclarationAssignationNode(
                DeclarationNode("w", Position(2, 1), "number", Position(4, 1)),
                BinaryOperationNode(
                    "+",
                    IdentifierOperatorNode("x", Position(6, 1)),
                    IdentifierOperatorNode("y", Position(8, 1)),
                ),
            )

        assertEquals(listOf(expected), result)
    }

    @Test
    fun testStringPrintln() {
        val tokens =
            listOf(
                Token(TokenType.PRINTLN, "println", 1, 0),
                Token(TokenType.OPEN_PARENTHESIS, "(", 1, 0),
                Token(TokenType.STRING_LITERAL, "Hello", 1, 0),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 1, 0),
                Token(TokenType.SEMICOLON, ";", 1, 0),
                Token(TokenType.PRINTLN, "println", 2, 0),
                Token(TokenType.OPEN_PARENTHESIS, "(", 2, 0),
                Token(TokenType.STRING_LITERAL, "world!", 2, 0),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 2, 0),
                Token(TokenType.SEMICOLON, ";", 2, 0),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected =
            listOf(
                MethodNode("println", StringOperatorNode("Hello", Position(1, 0)), Position(1, 0)),
                MethodNode("println", StringOperatorNode("world!", Position(2, 0)), Position(2, 0)),
            )

        assertEquals(expected, result)
    }

    @Test
    fun testDeclarationAssignationAndPrintln() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 1, 1),
                Token(TokenType.IDENTIFIER, "w", 2, 1),
                Token(TokenType.COLON, ":", 3, 1),
                Token(TokenType.NUMBER_TYPE, "number", 4, 1),
                Token(TokenType.EQUALS, "=", 5, 1),
                Token(TokenType.IDENTIFIER, "x", 6, 1),
                Token(TokenType.PLUS, "-", 7, 1),
                Token(TokenType.IDENTIFIER, "y", 8, 1),
                Token(TokenType.WHITESPACE, " ", 1, 9),
                Token(TokenType.SEMICOLON, ";", 9, 1),
                Token(TokenType.PRINTLN, "println", 1, 0),
                Token(TokenType.OPEN_PARENTHESIS, "(", 1, 0),
                Token(TokenType.STRING_LITERAL, "Hello world!", 1, 0),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 1, 0),
                Token(TokenType.SEMICOLON, ";", 9, 1),
                Token(TokenType.PRINTLN, "println", 1, 0),
                Token(TokenType.OPEN_PARENTHESIS, "(", 1, 0),
                Token(TokenType.IDENTIFIER, "x", 6, 1),
                Token(TokenType.PLUS, "+", 7, 1),
                Token(TokenType.IDENTIFIER, "y", 8, 1),
                Token(TokenType.PLUS, "+", 7, 1),
                Token(TokenType.IDENTIFIER, "z", 8, 1),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 1, 0),
                Token(TokenType.SEMICOLON, ";", 9, 1),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected =
            listOf(
                DeclarationAssignationNode(
                    DeclarationNode("w", Position(2, 1), "number", Position(4, 1)),
                    BinaryOperationNode(
                        "-",
                        IdentifierOperatorNode("x", Position(6, 1)),
                        IdentifierOperatorNode("y", Position(8, 1)),
                    ),
                ),
                MethodNode("println", StringOperatorNode("Hello world!", Position(1, 0)), Position(1, 0)),
                MethodNode(
                    "println",
                    BinaryOperationNode(
                        "+",
                        BinaryOperationNode(
                            "+",
                            IdentifierOperatorNode("x", Position(6, 1)),
                            IdentifierOperatorNode("y", Position(8, 1)),
                        ),
                        IdentifierOperatorNode("z", Position(8, 1)),
                    ),
                    Position(1, 0),
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun testErrorLineWithoutSemicolon() {
        val tokens =
            listOf(
                Token(TokenType.IDENTIFIER, "x", 1, 0),
                Token(TokenType.WHITESPACE, " ", 2, 0),
                Token(TokenType.EQUALS, "=", 3, 0),
                Token(TokenType.WHITESPACE, " ", 4, 0),
                Token(TokenType.NUMERIC_LITERAL, "5", 5, 0),
            )

        val parser = Parser(tokens)

        val exception =
            assertThrows(
                RuntimeException::class.java,
            ) {
                parser.generateAST()
            }

        assertEquals("Se debe terminar el statement con un 'SEMICOLON'", exception.message)
    }

    @Test
    fun testReadInputMethod() {
        val tokens =
            listOf(
                Token(TokenType.READINPUT, "readInput", 1, 0),
                Token(TokenType.OPEN_PARENTHESIS, "(", 1, 1),
                Token(TokenType.STRING_LITERAL, "Ingrese su nombre: ", 1, 2),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 1, 3),
                Token(TokenType.SEMICOLON, ";", 1, 4),
                Token(TokenType.READINPUT, "readInput", 2, 0),
                Token(TokenType.OPEN_PARENTHESIS, "(", 2, 1),
                Token(TokenType.STRING_LITERAL, "Ingrese su apellido: ", 2, 2),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 2, 3),
                Token(TokenType.SEMICOLON, ";", 2, 4),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected =
            listOf(
                MethodNode("readInput", StringOperatorNode("Ingrese su nombre: ", Position(1, 2)), Position(1, 0)),
                MethodNode("readInput", StringOperatorNode("Ingrese su apellido: ", Position(2, 2)), Position(2, 0)),
            )

        assertEquals(expected, result)
    }

    @Test
    fun testReadInputAssignation() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 1, 0),
                Token(TokenType.IDENTIFIER, "name", 1, 1),
                Token(TokenType.COLON, ":", 1, 2),
                Token(TokenType.STRING_TYPE, "string", 1, 3),
                Token(TokenType.EQUALS, "=", 1, 4),
                Token(TokenType.READINPUT, "readInput", 1, 5),
                Token(TokenType.OPEN_PARENTHESIS, "(", 1, 6),
                Token(TokenType.STRING_LITERAL, "Ingrese su nombre: ", 1, 7),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 1, 8),
                Token(TokenType.SEMICOLON, ";", 1, 9),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected =
            listOf(
                DeclarationAssignationNode(
                    DeclarationNode("name", Position(1, 1), "string", Position(1, 3)),
                    MethodNode("readInput", StringOperatorNode("Ingrese su nombre: ", Position(1, 7)), Position(1, 5)),
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun testMix() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 0, 0),
                Token(TokenType.IDENTIFIER, "name", 1, 0),
                Token(TokenType.COLON, ":", 2, 0),
                Token(TokenType.STRING_TYPE, "string", 3, 0),
                Token(TokenType.EQUALS, "=", 4, 0),
                Token(TokenType.READINPUT, "readInput", 5, 0),
                Token(TokenType.OPEN_PARENTHESIS, "(", 6, 0),
                Token(TokenType.STRING_LITERAL, "Ingrese su nombre: ", 7, 0),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 8, 0),
                Token(TokenType.SEMICOLON, ";", 9, 0),
                Token(TokenType.PRINTLN, "println", 1, 0),
                Token(TokenType.OPEN_PARENTHESIS, "(", 1, 0),
                Token(TokenType.STRING_LITERAL, "Hello", 1, 0),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 1, 0),
                Token(TokenType.SEMICOLON, ";", 1, 0),
                Token(TokenType.LET, "let", 1, 1),
                Token(TokenType.IDENTIFIER, "x", 2, 1),
                Token(TokenType.COLON, ":", 3, 1),
                Token(TokenType.STRING_TYPE, "string", 4, 1),
                Token(TokenType.EQUALS, "=", 5, 1),
                Token(TokenType.STRING_LITERAL, "Hello", 6, 1),
                Token(TokenType.PLUS, "+", 7, 1),
                Token(TokenType.STRING_LITERAL, " world", 8, 1),
                Token(TokenType.SEMICOLON, ";", 9, 1),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected =
            listOf(
                DeclarationAssignationNode(
                    DeclarationNode("name", Position(1, 0), "string", Position(3, 0)),
                    MethodNode("readInput", StringOperatorNode("Ingrese su nombre: ", Position(7, 0)), Position(5, 0)),
                ),
                MethodNode("println", StringOperatorNode("Hello", Position(1, 0)), Position(1, 0)),
                DeclarationAssignationNode(
                    DeclarationNode("x", Position(2, 1), "string", Position(4, 1)),
                    BinaryOperationNode(
                        "+",
                        StringOperatorNode("Hello", Position(6, 1)),
                        StringOperatorNode(" world", Position(8, 1)),
                    ),
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun testParseIf() {
        val tokens =
            listOf(
                Token(TokenType.IF, "if", 1, 1),
                Token(TokenType.OPEN_PARENTHESIS, "(", 1, 2),
                Token(TokenType.IDENTIFIER, "x", 1, 3),
                Token(TokenType.EQUALS_EQUALS, "==", 1, 4),
                Token(TokenType.NUMERIC_LITERAL, "1", 1, 5),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 1, 6),
                Token(TokenType.OPEN_BRACKET, "{", 1, 7),
                Token(TokenType.PRINTLN, "print", 2, 0),
                Token(TokenType.OPEN_PARENTHESIS, "(", 2, 1),
                Token(TokenType.IDENTIFIER, "x", 2, 2),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 2, 3),
                Token(TokenType.SEMICOLON, ";", 2, 4),
                Token(TokenType.PRINTLN, "println", 3, 0),
                Token(TokenType.OPEN_PARENTHESIS, "(", 3, 1),
                Token(TokenType.STRING_LITERAL, "Hello", 3, 2),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 3, 3),
                Token(TokenType.SEMICOLON, ";", 3, 4),
                Token(TokenType.CLOSE_BRACKET, "}", 4, 0),
                Token(TokenType.ELSE, "else", 4, 1),
                Token(TokenType.OPEN_BRACKET, "{", 4, 2),
                Token(TokenType.PRINTLN, "print", 5, 0),
                Token(TokenType.OPEN_PARENTHESIS, "(", 5, 1),
                Token(TokenType.IDENTIFIER, "x", 5, 2),
                Token(TokenType.UNEQUALS, "!=", 5, 3),
                Token(TokenType.NUMERIC_LITERAL, "1", 5, 4),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 5, 5),
                Token(TokenType.SEMICOLON, ";", 5, 6),
                Token(TokenType.CLOSE_BRACKET, "}", 6, 0),
            )

        val parser = Parser(tokens)
        val ast = parser.generateAST()

        val expectedAst =
            listOf(
                IfNode(
                    ConditionNode(
                        "==",
                        IdentifierOperatorNode("x", Position(1, 3)),
                        NumberOperatorNode(1.0, Position(1, 5)),
                    ),
                    listOf(
                        MethodNode("println", IdentifierOperatorNode("x", Position(2, 2)), Position(2, 0)),
                        MethodNode("println", StringOperatorNode("Hello", Position(3, 2)), Position(3, 0)),
                    ),
                    listOf(
                        MethodNode("println", ConditionNode("!=", IdentifierOperatorNode("x", Position(5, 2)), NumberOperatorNode(1.0, Position(5, 4))), Position(5, 0)),
                    ),
                ),
            )

        assertEquals(expectedAst, ast)
    }

    @Test
    fun testParseIfMix() {
        val tokens =
            listOf(
                Token(TokenType.IF, "if", 1, 1),
                Token(TokenType.OPEN_PARENTHESIS, "(", 1, 3),
                Token(TokenType.IDENTIFIER, "x", 1, 4),
                Token(TokenType.EQUALS_EQUALS, "==", 1, 5),
                Token(TokenType.NUMERIC_LITERAL, "10", 1, 6),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 1, 7),
                Token(TokenType.OPEN_BRACKET, "{", 1, 8),
                Token(TokenType.PRINTLN, "print", 1, 9),
                Token(TokenType.OPEN_PARENTHESIS, "(", 1, 10),
                Token(TokenType.STRING_LITERAL, "x is 10", 1, 11),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 1, 12),
                Token(TokenType.SEMICOLON, ";", 5, 13),
                Token(TokenType.CLOSE_BRACKET, "}", 1, 14),
                Token(TokenType.ELSE, "else", 1, 15),
                Token(TokenType.OPEN_BRACKET, "{", 1, 16),
                Token(TokenType.PRINTLN, "print", 1, 17),
                Token(TokenType.OPEN_PARENTHESIS, "(", 1, 18),
                Token(TokenType.STRING_LITERAL, "x is not 10", 1, 19),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 1, 20),
                Token(TokenType.SEMICOLON, ";", 5, 21),
                Token(TokenType.CLOSE_BRACKET, "}", 1, 22),
                Token(TokenType.IDENTIFIER, "x", 1, 0),
                Token(TokenType.EQUALS, "=", 3, 0),
                Token(TokenType.NUMERIC_LITERAL, "5", 5, 0),
                Token(TokenType.SEMICOLON, ";", 7, 1),
            )

        val parser = Parser(tokens)
        val ast = parser.generateAST()

        val expectedAst =
            listOf(
                IfNode(
                    ConditionNode(
                        "==",
                        IdentifierOperatorNode("x", Position(1, 4)),
                        NumberOperatorNode(10.0, Position(1, 6)),
                    ),
                    listOf(MethodNode("println", StringOperatorNode("x is 10", Position(1, 11)), Position(1, 9))),
                    listOf(MethodNode("println", StringOperatorNode("x is not 10", Position(1, 19)), Position(1, 17))),
                ),
                AssignationNode("x", Position(1, 0), NumberOperatorNode(5.0, Position(5, 0))),
            )

        assertEquals(expectedAst, ast)
    }

    @Test
    fun testGetStatement() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 1, 0),
                Token(TokenType.IDENTIFIER, "x", 2, 0),
                Token(TokenType.COLON, ":", 3, 0),
                Token(TokenType.NUMBER_TYPE, "number", 4, 0),
                Token(TokenType.SEMICOLON, ";", 5, 0),
                Token(TokenType.LET, "let", 1, 1),
                Token(TokenType.IDENTIFIER, "y", 2, 1),
                Token(TokenType.COLON, ":", 3, 1),
                Token(TokenType.STRING_TYPE, "string", 4, 1),
                Token(TokenType.SEMICOLON, ";", 5, 1),
            )

        val parser = Parser(tokens)
        val statement = parser.getStatement(tokens, 0, TokenType.SEMICOLON)

        val expected =
            listOf(
                Token(TokenType.LET, "let", 1, 0),
                Token(TokenType.IDENTIFIER, "x", 2, 0),
                Token(TokenType.COLON, ":", 3, 0),
                Token(TokenType.NUMBER_TYPE, "number", 4, 0),
            )

        assertEquals(expected, statement)
    }

    @Test
    fun testErrorGetStatementWithoutFinalToken() {
        val tokens =
            listOf(
                Token(TokenType.LET, "let", 1, 0),
                Token(TokenType.IDENTIFIER, "x", 2, 0),
                Token(TokenType.COLON, ":", 3, 0),
                Token(TokenType.NUMBER_TYPE, "number", 4, 0),
            )

        val parser = Parser(tokens)
        val exception =
            assertThrows(
                RuntimeException::class.java,
            ) {
                parser.generateAST()
            }

        assertEquals("Se debe terminar el statement con un 'SEMICOLON'", exception.message)
    }

    @Test
    fun testRedEnv() {
        val tokens =
            listOf(
                Token(TokenType.READENV, "readEnv", 1, 0),
                Token(TokenType.OPEN_PARENTHESIS, "(", 1, 1),
                Token(TokenType.STRING_LITERAL, "gitHubAccessToken", 1, 2),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 1, 3),
                Token(TokenType.SEMICOLON, ";", 1, 4),
                Token(TokenType.READENV, "readEnv", 1, 0),
                Token(TokenType.OPEN_PARENTHESIS, "(", 1, 1),
                Token(TokenType.STRING_LITERAL, "envVariable", 1, 2),
                Token(TokenType.CLOSE_PARENTHESIS, ")", 1, 3),
                Token(TokenType.SEMICOLON, ";", 1, 4),
            )

        val parser = Parser(tokens)
        val result = parser.generateAST()

        val expected =
            listOf(
                MethodNode("readEnv", StringOperatorNode("gitHubAccessToken", Position(1, 2)), Position(1, 0)),
                MethodNode("readEnv", StringOperatorNode("envVariable", Position(1, 2)), Position(1, 0)),
            )

        assertEquals(expected, result)
    }
}
