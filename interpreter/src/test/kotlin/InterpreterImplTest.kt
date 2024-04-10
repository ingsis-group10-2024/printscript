import ast.ASTNode
import ast.AssignationNode
import ast.BinaryOperationNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
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
        val ast = DeclarationAssignationNode(DeclarationNode("x", "number"), NumberOperatorNode(5.0))
        val astList = listOf(ast)
        // Act
        val response = interpreter.interpret(astList)
        // Assert
        assertEquals("", response)
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
        val ast = DeclarationAssignationNode(DeclarationNode("X", "number"), NumberOperatorNode(5.0))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("", response)
    }

    @Test
    fun test005_shouldInterpretMethodNode() {
        val ast = MethodNode("println", StringOperatorNode("Hello, World!"))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)

        assertEquals("Hello, World!", response)
    }

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
        assertEquals("Invalid Operation", response)
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
    fun test010_WhenGivenTwoAssignationNodesAndAMethodNodeThatRunsBothOfThemTogetherShouldReturnThePrintedString() {
        // Arrange
        val ast1 = DeclarationAssignationNode(DeclarationNode("x", "number"), NumberOperatorNode(5.0))
        val ast2 = DeclarationAssignationNode(DeclarationNode("y", "number"), NumberOperatorNode(3.0))
        val method = MethodNode("println", BinaryOperationNode("+", IdentifierOperatorNode("x"), IdentifierOperatorNode("y")))
        val astList = listOf(ast1, ast2, method)
        // Act
        val response = interpreter.interpret(astList)
        // Assert
        assertEquals("8.0", response)
    }

    @Test
    fun `test011 interpretBinaryNode with BinaryOperationNode`() {
        val leftNode = NumberOperatorNode(value = 5.0)
        val rightNode = NumberOperatorNode(value = 3.0)
        val binaryNode = BinaryOperationNode(symbol = "+", left = leftNode, right = rightNode)
        val methodNode = MethodNode("println", binaryNode)
        val result = interpreter.interpret(listOf(methodNode))
        assertEquals("8.0", result)
    }

    @Test
    fun `test 012 - should interpret an AST and execute it`() {
        val ast =
            listOf(
                DeclarationAssignationNode(
                    DeclarationNode("a", "string"),
                    BinaryOperationNode(
                        "+",
                        StringOperatorNode("Hello"),
                        NumberOperatorNode(5.0),
                    ),
                ),
                MethodNode("println", BinaryOperationNode("+", IdentifierOperatorNode("a"), StringOperatorNode(""))),
            )
        val result = interpreter.interpret(ast)
        assertEquals("Hello5.0", result)
    }

    @Test
    fun `test 013 - should interpret an AST and execute it`() {
        val ast =
            listOf(
                DeclarationAssignationNode(
                    DeclarationNode("a", "number"),
                    NumberOperatorNode(1.0),
                ),
                DeclarationAssignationNode(
                    DeclarationNode("x", "string"),
                    BinaryOperationNode(
                        "+",
                        IdentifierOperatorNode("a"),
                        StringOperatorNode("Hello"),
                    ),
                ),
                MethodNode("println", IdentifierOperatorNode("x")),
            )
        val result = interpreter.interpret(ast)
        assertEquals("1.0Hello", result)
    }

    @Test
    fun test014_interpretAssignationWithUndeclaredVariable() {
        val ast = AssignationNode("z", NumberOperatorNode(5.0))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("Variable z not declared", response)
    }

    @Test
    fun test015_interpretBinaryNodeWithSubtractionOperation() {
        val ast = BinaryOperationNode("-", NumberOperatorNode(5.0), NumberOperatorNode(3.0))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("2.0", response)
    }

    @Test
    fun test016_interpretBinaryNodeWithIdentifierOperands() {
        val ast1 = DeclarationAssignationNode(DeclarationNode("x", "number"), NumberOperatorNode(5.0))
        val ast2 = DeclarationAssignationNode(DeclarationNode("y", "number"), NumberOperatorNode(3.0))
        val ast3 = MethodNode("println", BinaryOperationNode("+", IdentifierOperatorNode("x"), IdentifierOperatorNode("y")))
        val astList = listOf(ast1, ast2, ast3)
        val response = interpreter.interpret(astList)
        assertEquals("8.0", response)
    }

    @Test
    fun test017_interpretMethodWithInvalidMethod() {
        val ast = MethodNode("invalidMethod", StringOperatorNode("Hello, World!"))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("Invalid Method", response)
    }

    @Test
    fun test018_interpretBinaryNodeWithDivisionOperation() {
        val ast = BinaryOperationNode("/", NumberOperatorNode(10.0), NumberOperatorNode(2.0))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("5.0", response)
    }

    @Test
    fun test019_interpretBinaryNodeWithMultiplicationOperation() {
        val ast = BinaryOperationNode("*", NumberOperatorNode(5.0), NumberOperatorNode(3.0))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("15.0", response)
    }

    @Test
    fun test022_GivenBinaryOperationNodeWithAddition_WhenInterpreted_ShouldReturnResultOfAddition() {
        val binaryOperationNode = BinaryOperationNode("+", NumberOperatorNode(5.0), NumberOperatorNode(3.0))
        val response = interpreter.interpret(listOf(binaryOperationNode))

        assertEquals("8.0", response)
    }

    @Test
    fun test023_GivenBinaryOperationNodeWithSubtraction_WhenInterpreted_ShouldReturnResultOfSubtraction() {
        val binaryOperationNode = BinaryOperationNode("-", NumberOperatorNode(5.0), NumberOperatorNode(3.0))
        val response = interpreter.interpret(listOf(binaryOperationNode))
        assertEquals("2.0", response)
    }

    @Test
    fun test024_GivenBinaryOperationNodeWithMultiplication_WhenInterpreted_ShouldReturnResultOfMultiplication() {
        val binaryOperationNode = BinaryOperationNode("*", NumberOperatorNode(5.0), NumberOperatorNode(3.0))
        val response = interpreter.interpret(listOf(binaryOperationNode))
        assertEquals("15.0", response)
    }

    @Test
    fun test025_GivenBinaryOperationNodeWithDivision_WhenInterpreted_ShouldReturnResultOfDivision() {
        val binaryOperationNode = BinaryOperationNode("/", NumberOperatorNode(10.0), NumberOperatorNode(2.0))
        val response = interpreter.interpret(listOf(binaryOperationNode))
        assertEquals("5.0", response)
    }

    @Test
    fun test026_GivenMethodNodeWithPrintln_WhenInterpreted_ShouldReturnPrintedString() {
        val methodNode = MethodNode("println", StringOperatorNode("Hello, World!"))
        val response = interpreter.interpret(listOf(methodNode))
        assertEquals("Hello, World!", response)
    }

    @Test
    fun test027_GivenMethodNodeWithInvalidMethod_WhenInterpreted_ShouldReturnInvalidMethod() {
        val declarationNode = DeclarationNode("x", "number")
        val assignationNode = AssignationNode("x", NumberOperatorNode(5.0))
        interpreter.interpret(listOf(declarationNode))
        val response = interpreter.interpret(listOf(assignationNode))
        assertEquals("x = 5.0", response)
    }
}
