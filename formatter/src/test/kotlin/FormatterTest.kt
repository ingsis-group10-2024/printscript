
import ast.ASTNode
import ast.AssignationNode
import ast.BinaryOperationNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import org.junit.Test
import kotlin.test.assertEquals

class FormatterTest {
    private val formatter = Formatter()

    @Test
    fun `formats string operator node`() {
        val nodes = listOf(StringOperatorNode("Hello"))
        val result = formatter.format(nodes)
        assertEquals("\"Hello\"", result)
    }

    @Test
    fun `formats number operator node`() {
        val nodes = listOf(NumberOperatorNode(123))
        val result = formatter.format(nodes)
        assertEquals("123", result)
    }

    @Test
    fun `formats binary operation node`() {
        val nodes = listOf(BinaryOperationNode("+", NumberOperatorNode(1), NumberOperatorNode(2)))
        val result = formatter.format(nodes)
        assertEquals("1 + 2", result)
    }

    @Test
    fun `formats declaration node`() {
        val nodes = listOf(DeclarationNode("x", "Int"))
        val result = formatter.format(nodes)
        assertEquals("let x: Int\n", result)
    }

    @Test
    fun `formats declaration assignation node`() {
        val nodes = listOf(DeclarationAssignationNode(DeclarationNode("x", "Int"), NumberOperatorNode(123)))
        val result = formatter.format(nodes)
        assertEquals("let x: Int = 123\n", result)
    }

    @Test
    fun `formats assignation node`() {
        val nodes = listOf(AssignationNode("x", NumberOperatorNode(123)))
        val result = formatter.format(nodes)
        assertEquals("x = 123\n", result)
    }

    @Test
    fun `formats multiple nodes`() {
        val nodes =
            listOf(
                StringOperatorNode("Hello"),
                NumberOperatorNode(123),
                BinaryOperationNode("+", NumberOperatorNode(1), NumberOperatorNode(2)),
                DeclarationNode("x", "Int"),
                DeclarationAssignationNode(DeclarationNode("y", "Int"), NumberOperatorNode(123)),
                AssignationNode("x", NumberOperatorNode(123)),
            )
        val result = formatter.format(nodes)
        assertEquals("\"Hello\"1231 + 2let x: Int\nlet y: Int = 123\nx = 123\n", result)
    }

    @Test
    fun `formats empty list`() {
        val nodes = emptyList<ASTNode>()
        val result = formatter.format(nodes)
        assertEquals("", result)
    }
}
