import ast.*
import org.junit.Before
import org.junit.Test
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
        val ast = DeclarationAssignationNode(DeclarationNode("x" , "number"), NumberOperatorNode(5.0))
        val astList = listOf(ast)
        // Act
        val response = interpreter.interpret(astList)
        // Assert
        assertEquals("5.0", response)
    }

    @Test
    fun test003_shouldInterpretDeclarationNode() {
        val ast = DeclarationNode("x", "Int")
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("", response)
    }

    @Test
    fun test004_shouldInterpretAssignationNode() {
        val ast = DeclarationAssignationNode(DeclarationNode("X" , "number" ), NumberOperatorNode(5.0))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("5.0" , response)
    }

    @Test
    fun test005_shouldInterpretMethodNode() {
        val ast = MethodNode("println", StringOperatorNode("Hello, World!"))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)

        assertEquals("Hello, World!", response)
    }

//    @Test
//    fun shouldReturnFailedResponseForInvalidNodeType() {
//        val ast = object : ASTNode {}
//        val astList = listOf(ast)
//        val response = interpreter.interpret(astList)
//        assertEquals("Invalid Node Type", response)
//    }

    @Test
    fun test006_shouldReturnFailedResponseForInvalidMethod() {
        val ast = MethodNode("invalidMethod", StringOperatorNode("Hello, World!"))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("Invalid Method", response)
    }

    @Test
    fun test007_shouldReturnFailedResponseForInvalidOperation() {
        val ast = BinaryOperationNode("invalidOperation", NumberOperatorNode(5.0), NumberOperatorNode(5.0))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("Invalid Node Type", response)
    }

    @Test
    fun test008_shouldReturnNullForEmptyAstList() {
        val astList = emptyList<ASTNode>()
        val response = interpreter.interpret(astList)
        assertNull(response)
    }

    @Test
    fun test009_whenNeededToInterpretANumberOperatorNodeInterpreterShouldReturnItsValue() {
        // Arrange
        val ast = NumberOperatorNode(5.0)
        val astList = listOf(ast)
        // Act
        val response = interpreter.interpret(astList)
        // Assert
        assertEquals("5.0", response)
    }
    @Test
    fun test010_WhenGivenTwoAssignationNodesAndAMethodNodeThatRunsBothOfThemTogetherShouldReturnThePrintedString(){
        // Arrange
        val ast1 = AssignationNode("x", NumberOperatorNode(5.0))
        val ast2 = AssignationNode("y", NumberOperatorNode(3.0))
        val method = MethodNode("println", BinaryOperationNode("+", IdentifierOperatorNode("x"), IdentifierOperatorNode("y")))
        val astList = listOf(ast1, ast2, method)
        // Act
        val response = interpreter.interpret(astList)
        // Assert
        assertEquals("8.0", response)
    }
    @Test
    fun `test interpretBinaryNode with BinaryOperationNode`() {
        val leftNode = NumberOperatorNode(value = 5.0)
        val rightNode = NumberOperatorNode(value = 3.0)
        val binaryNode = BinaryOperationNode(symbol = "+", left = leftNode, right = rightNode)
        val methodNode = MethodNode("println" , binaryNode)
        val result = interpreter.interpret(listOf(methodNode))
        assertEquals("8.0", result)
    }

}
