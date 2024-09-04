import ast.ASTNode
import ast.AssignationNode
import ast.BinaryOperationNode
import ast.BooleanOperatorNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.IfNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.Position
import ast.StringOperatorNode
import config.JsonConfigLoader
import implementation.Formatter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import token.TokenType

class FormatterTest {
    private val filePath = "src/main/resources/test_config_formatter.json"
    private val jsonConfigLoader = JsonConfigLoader(filePath)
    private val formatter = Formatter(jsonConfigLoader)

    @Test
    fun `formats declaration node`() {
        val nodes = listOf(DeclarationNode("x", TokenType.LET, Position(1, 1), "number", Position(1, 1)))
        val result = formatter.format(nodes)
        println(result)
        assertEquals("let x : number\n", result)
    }

    @Test
    fun `formats binary operation node`() {
        val nodes = listOf(BinaryOperationNode("+", NumberOperatorNode(2.0, Position(1, 1)), NumberOperatorNode(3.0, Position(1, 1))))
        val result = formatter.format(nodes)
        println(result)
        assertEquals("2.0 + 3.0", result)
    }

    @Test
    fun `formats multiple nodes`() {
        val nodes =
            listOf(
                DeclarationNode("x", TokenType.LET, Position(1, 1), "number", Position(1, 1)),
                BinaryOperationNode("+", NumberOperatorNode(5.0, Position(1, 1)), NumberOperatorNode(3.0, Position(1, 1))),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("let x : number\n5.0 + 3.0", result)
    }

    @Test
    fun `formats empty list`() {
        val nodes = emptyList<ASTNode>()
        val result = formatter.format(nodes)
        println(result)
        assertEquals("", result)
    }

    @Test
    fun `formats assignation node`() {
        val nodes =
            listOf(
                AssignationNode(
                    "x",
                    Position(1, 1),
                    BinaryOperationNode("+", NumberOperatorNode(5.0, Position(1, 1)), NumberOperatorNode(3.0, Position(1, 1))),
                ),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("x = 5.0 + 3.0;", result)
    }

    @Test
    fun `formats declaration assignation node`() {
        val nodes =
            listOf(
                DeclarationAssignationNode(
                    DeclarationNode("x", TokenType.LET, Position(1, 1), "number", Position(1, 1)),
                    BinaryOperationNode("+", NumberOperatorNode(5.0, Position(1, 1)), NumberOperatorNode(3.0, Position(1, 1))),
                ),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("let x : number = 5.0 + 3.0;", result)
    }

    @Test
    fun `formats identifier operator node`() {
        val nodes = listOf(IdentifierOperatorNode("x", Position(1, 1)))
        val result = formatter.format(nodes)
        println(result)
        assertEquals("x", result)
    }

    @Test
    fun `formats method node`() {
        val nodes =
            listOf(
                MethodNode("print", StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(1, 1)), Position(1, 1)),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("\nprint(\"Hello\");", result)
    }

    @Test
    fun `formats if node`() {
        val nodes =
            listOf(
                IfNode(
                    BooleanOperatorNode(true, Position(1, 1)),
                    listOf(MethodNode("print", StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(1, 1)), Position(1, 1))),
                    null,
                ),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("if (true) {\nprint(\"Hello\");\n}", result)
    }

    @Test
    fun `formats if node with equals equals`() {
        val nodes =
            listOf(
                IfNode(
                    BinaryOperationNode("==", IdentifierOperatorNode("x", Position(1, 1)), NumberOperatorNode(5.0, Position(1, 1))),
                    listOf(MethodNode("print", StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(1, 1)), Position(1, 1))),
                    null,
                ),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("if (x == 5.0) {\nprint(\"Hello\");\n}", result)
    }

    @Test
    fun `formats if node with else branch`() {
        val nodes =
            listOf(
                IfNode(
                    BooleanOperatorNode(true, Position(1, 1)),
                    listOf(MethodNode("print", StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(1, 1)), Position(1, 1))),
                    listOf(MethodNode("print", StringOperatorNode("World", TokenType.STRING_LITERAL, Position(1, 1)), Position(1, 1))),
                ),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("if (true) {\nprint(\"Hello\");\n} else {\nprint(\"World\");}", result)
    }

    @Test
    fun `format boolean operator node`() {
        val nodes = listOf(BooleanOperatorNode(true, Position(1, 1)))
        val result = formatter.format(nodes)
        println(result)
        assertEquals("true", result)
    }

    @Test
    fun `format declaration node with spaces`() {
        val nodes = listOf(DeclarationNode("x", TokenType.LET, Position(1, 1), "number", Position(1, 1)))
        val result = formatter.format(nodes)
        println(result)
        assertEquals("let x : number\n", result)
    }

    @Test
    fun `format declaration assignation node with spaces`() {
        val nodes =
            listOf(
                DeclarationAssignationNode(
                    DeclarationNode("x", TokenType.LET, Position(1, 1), "number", Position(1, 1)),
                    BinaryOperationNode("+", NumberOperatorNode(5.0, Position(1, 1)), NumberOperatorNode(3.0, Position(1, 1))),
                ),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("let x : number = 5.0 + 3.0;", result)
    }

    @Test
    fun `format assignation node with spaces`() {
        val nodes =
            listOf(
                AssignationNode(
                    "x",
                    Position(1, 1),
                    BinaryOperationNode("+", NumberOperatorNode(5.0, Position(1, 1)), NumberOperatorNode(3.0, Position(1, 1))),
                ),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("x = 5.0 + 3.0;", result)
    }

    @Test
    fun `format condition node with spaces`() {
        val nodes = listOf(BinaryOperationNode("==", IdentifierOperatorNode("x", Position(1, 1)), NumberOperatorNode(5.0, Position(1, 1))))
        val result = formatter.format(nodes)
        println(result)
        assertEquals("x == 5.0", result)
    }

    @Test
    fun `format condition node with spaces and method node`() {
        val nodes =
            listOf(
                BinaryOperationNode("==", IdentifierOperatorNode("x", Position(1, 1)), NumberOperatorNode(5.0, Position(1, 1))),
                MethodNode("print", StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(1, 1)), Position(1, 1)),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("x == 5.0\nprint(\"Hello\");", result)
    }
    // todo modificar para que use trueBranch y elseBranch como listas de AST

    @Test
    fun `format if node with spaces`() {
        val nodes =
            listOf(
                IfNode(
                    BooleanOperatorNode(true, Position(1, 1)),
                    listOf(MethodNode("print", StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(1, 1)), Position(1, 1))),
                    null,
                ),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("if (true) {\nprint(\"Hello\");\n}", result)
    }

    @Test
    fun `format if node with declaration assignation node`() {
        val nodes =
            listOf(
                IfNode(
                    BooleanOperatorNode(true, Position(1, 1)),
                    listOf(
                        DeclarationAssignationNode(
                            DeclarationNode("x", TokenType.LET, Position(1, 1), "number", Position(1, 1)),
                            BinaryOperationNode("+", NumberOperatorNode(5.0, Position(1, 1)), NumberOperatorNode(3.0, Position(1, 1))),
                        ),
                    ),
                    null,
                ),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("if (true) {\nlet x : number = 5.0 + 3.0;\n}", result)
    }

    @Test
    fun `format if node with declaration assignation node and else branch`() {
        val nodes =
            listOf(
                IfNode(
                    BooleanOperatorNode(true, Position(1, 1)),
                    listOf(
                        DeclarationAssignationNode(
                            DeclarationNode("x", TokenType.LET, Position(1, 1), "number", Position(1, 1)),
                            BinaryOperationNode("+", NumberOperatorNode(5.0, Position(1, 1)), NumberOperatorNode(3.0, Position(1, 1))),
                        ),
                    ),
                    listOf(
                        DeclarationAssignationNode(
                            DeclarationNode("y", TokenType.LET, Position(1, 1), "number", Position(1, 1)),
                            BinaryOperationNode("+", NumberOperatorNode(5.0, Position(1, 1)), NumberOperatorNode(3.0, Position(1, 1))),
                        ),
                    ),
                ),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("if (true) {\nlet x : number = 5.0 + 3.0;\n} else {\nlet y : number = 5.0 + 3.0;}", result)
    }

    @Test
    fun `format if node with assignation node`() {
        val nodes =
            listOf(
                IfNode(
                    BooleanOperatorNode(true, Position(1, 1)),
                    listOf(
                        AssignationNode(
                            "x",
                            Position(1, 1),
                            BinaryOperationNode("+", NumberOperatorNode(5.0, Position(1, 1)), NumberOperatorNode(3.0, Position(1, 1))),
                        ),
                    ),
                    null,
                ),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("if (true) {\nx = 5.0 + 3.0;\n}", result)
    }

    @Test
    fun `format if node with declaration node`() {
        val nodes =
            listOf(
                IfNode(
                    BooleanOperatorNode(true, Position(1, 1)),
                    listOf(DeclarationNode("x", TokenType.LET, Position(1, 1), "number", Position(1, 1))),
                    null,
                ),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("if (true) {\nlet x : number;\n}", result)
    }
}
