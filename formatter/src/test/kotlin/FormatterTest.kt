
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
import ast.StringOperatorNode
import config.JsonConfigLoader
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatterTest {
    private val filePath = "src/main/resources/test_config_formatter.json"
    private val jsonConfigLoader = JsonConfigLoader(filePath)
    private val formatter = Formatter(jsonConfigLoader)

    @Test
    fun `formats declaration node`() {
        val nodes = listOf(DeclarationNode("x", "number"))
        val result = formatter.format(nodes)
        println(result)
        assertEquals("let x : number\n", result)
    }

    @Test
    fun `formats binary operation node`() {
        val nodes = listOf(BinaryOperationNode("+", NumberOperatorNode(2.0), NumberOperatorNode(3.0)))
        val result = formatter.format(nodes)
        println(result)
        assertEquals("2.0 + 3.0", result)
    }

    @Test
    fun `formats multiple nodes`() {
        val nodes =
            listOf(
                DeclarationNode("x", "number"),
                BinaryOperationNode("+", NumberOperatorNode(5.0), NumberOperatorNode(3.0)),
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
                AssignationNode("x", BinaryOperationNode("+", NumberOperatorNode(5.0), NumberOperatorNode(3.0))),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("x = 5.0 + 3.0", result)
    }

    @Test
    fun `formats declaration assignation node`() {
        val nodes =
            listOf(
                DeclarationAssignationNode(
                    DeclarationNode("x", "number"),
                    BinaryOperationNode("+", NumberOperatorNode(5.0), NumberOperatorNode(3.0)),
                ),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("let x : number = 5.0 + 3.0", result)
    }

    @Test
    fun `formats identifier operator node`() {
        val nodes = listOf(IdentifierOperatorNode("x"))
        val result = formatter.format(nodes)
        println(result)
        assertEquals("x", result)
    }

    @Test
    fun `formats method node`() {
        val nodes =
            listOf(
                MethodNode("print", StringOperatorNode("Hello")),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("\nprint(\"Hello\")", result)
    }

    @Test
    fun `formats if node`() {
        val nodes =
            listOf(
                IfNode(
                    BooleanOperatorNode(true),
                    MethodNode("print", StringOperatorNode("Hello")),
                    null,
                ),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("if (true) {\nprint(\"Hello\")\n}", result)
    }

    @Test
    fun `formats if node with equals equals`() {
        val nodes =
            listOf(
                IfNode(
                    BinaryOperationNode("==", IdentifierOperatorNode("x"), NumberOperatorNode(5.0)),
                    MethodNode("print", StringOperatorNode("Hello")),
                    null,
                ),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("if (x == 5.0) {\nprint(\"Hello\")\n}", result)
    }

    @Test
    fun `formats if node with else branch`() {
        val nodes =
            listOf(
                IfNode(
                    BooleanOperatorNode(true),
                    MethodNode("print", StringOperatorNode("Hello")),
                    MethodNode("print", StringOperatorNode("World")),
                ),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("if (true) {\nprint(\"Hello\")\n} else {\nprint(\"World\")}", result)
    }

    @Test
    fun `format boolean operator node`() {
        val nodes = listOf(BooleanOperatorNode(true))
        val result = formatter.format(nodes)
        println(result)
        assertEquals("true", result)
    }
}
