import ast.ASTNode
import ast.AssignationNode
import ast.BinaryOperationNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.Position
import ast.StringOperatorNode
import config.JsonConfigLoader
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatterTest {
    private val filePath = "src/main/resources/test_config_formatter.json"
    private val jsonConfigLoader = JsonConfigLoader(filePath)
    private val formatter = Formatter(jsonConfigLoader)

    // let x : number
    @Test
    fun `formats declaration node`() {
        val nodes = listOf(DeclarationNode("x", Position(3, 1), "number", Position(7, 1)))
        val result = formatter.format(nodes)
        println(result)
        assertEquals("let x : number\n", result)
    }

    // 2 + 3
    @Test
    fun `formats binary operation node`() {
        val nodes = listOf(BinaryOperationNode("+", NumberOperatorNode(2.0, Position(1, 1)), NumberOperatorNode(3.0, Position(5, 1))))
        val result = formatter.format(nodes)
        println(result)
        assertEquals("2.0 + 3.0", result)
    }

    @Test
    fun `formats multiple nodes`() {
        val nodes =
            listOf(
                DeclarationNode("x", Position(3, 1), "number", Position(7, 1)),
                BinaryOperationNode("+", NumberOperatorNode(5.0, Position(1, 2)), NumberOperatorNode(3.0, Position(5, 2))),
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
                    BinaryOperationNode("+", NumberOperatorNode(5.0, Position(5, 1)), NumberOperatorNode(3.0, Position(9, 1))),
                ),
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
                    DeclarationNode("x", Position(3, 1), "number", Position(7, 1)),
                    BinaryOperationNode("+", NumberOperatorNode(5.0, Position(11, 1)), NumberOperatorNode(3.0, Position(15, 1))),
                ),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("let x : number = 5.0 + 3.0", result)
    }

//    @Test
//    fun `formats identifier operator node`() {
//        val nodes = listOf(IdentifierOperatorNode("x"))
//        val result = formatter.format(nodes)
//        println(result)
//        assertEquals("x", result)
//    }

    @Test
    fun `formats method node`() {
        val nodes =
            listOf(
                MethodNode("print", StringOperatorNode("Hello", Position(3, 1)), Position(1, 1)),
            )
        val result = formatter.format(nodes)
        println(result)
        assertEquals("\nprint(\"Hello\")", result)
    }
}
