import ast.ASTNode
import ast.AssignationNode
import ast.BinaryOperationNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatterTest {
    private val config = loadConfig("config.json")
    private val formatter = Formatter(config)

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
    fun `formats method node`() {
        val nodes =
            listOf(
                MethodNode("print", StringOperatorNode("Hello")),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("\n print(\"Hello\")", result)
    }
}
