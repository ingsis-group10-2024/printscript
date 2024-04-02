import ast.*
import org.junit.Before
import org.junit.Test
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.DefaultAsserter.assertNull
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InterpreterImplTest {
    private lateinit var interpreter: InterpreterImpl

    @Before
    fun setup() {
        interpreter = InterpreterImpl()
    }

    @Test
    fun test001_WhenReceivingADeclarationBinaryNodeInterpreterShouldReturnItsDeclaration() {
        // Arrange
        val interpreter = InterpreterImpl()
        val ast = DeclarationNode("x", "Int")
        val astList = listOf(ast)
        // Act
        val response = interpreter.interpret(astList)
        // Assert
        assertEquals("", response)
    }

    @Test
    fun test002_WhenReceivingAnAssignationBinaryNodeInterpreterShouldReturnItsAssignation() {
        // Arrange
        val interpreter = InterpreterImpl()
        val ast = AssignationNode("x", NumberOperatorNode(5.0))
        val astList = listOf(ast)
        // Act
        val response = interpreter.interpret(astList)
        // Assert
        assertEquals("AST Interpreted Successfully", response)
    }

    @Test
    fun shouldInterpretDeclarationNode() {
        val ast = DeclarationNode("x", "Int")
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("", response)
    }

    @Test
    fun shouldInterpretAssignationNode() {
        val ast = AssignationNode("x", NumberOperatorNode(5.0))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("AST Interpreted Successfully", response)
    }

    @Test
    fun shouldInterpretMethodNode() {
        val ast = MethodNode("println", StringOperatorNode("Hello, World!"))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("", response)
    }

//    @Test
//    fun shouldReturnFailedResponseForInvalidNodeType() {
//        val ast = object : ASTNode {}
//        val astList = listOf(ast)
//        val response = interpreter.interpret(astList)
//        assertEquals("Invalid Node Type", response)
//    }

    @Test
    fun shouldReturnFailedResponseForInvalidMethod() {
        val ast = MethodNode("invalidMethod", StringOperatorNode("Hello, World!"))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("Invalid Method", response)
    }

    @Test
    fun shouldReturnFailedResponseForInvalidOperation() {
        val ast = BinaryOperationNode("invalidOperation", NumberOperatorNode(5.0), NumberOperatorNode(5.0))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("Invalid Node Type", response)
    }

    @Test
    fun shouldReturnNullForEmptyAstList() {
        val astList = emptyList<ASTNode>()
        val response = interpreter.interpret(astList)
        assertNull(response)
    }
    @Test
    fun whenNeededToInterpretANumberOperatorNodeInterpreterShouldReturnItsValue() {
        // Arrange
        val ast = NumberOperatorNode(5.0)
        val astList = listOf(ast)
        // Act
        val response = interpreter.interpret(astList)
        // Assert
        assertEquals("5.0", response)
    }

}
