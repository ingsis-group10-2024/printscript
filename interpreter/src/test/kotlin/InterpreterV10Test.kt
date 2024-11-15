import ast.AssignationNode
import ast.BinaryOperationNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.Position
import ast.StringOperatorNode
import emitter.PrinterEmitter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import token.TokenType
import variable.Variable
import variable.VariableMap
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InterpreterV10Test {
    private lateinit var interpreterFactory: InterpreterFactory
    private lateinit var interpreterV10: InterpreterManager
    private val outputter = PrinterEmitter()

    @BeforeEach
    fun setUp() {
        interpreterFactory = InterpreterFactory("1.0", VariableMap(HashMap()), null, outputter)
        interpreterV10 = interpreterFactory.buildInterpreter()
    }

    @Test
    fun test001_declarationNode_createsVariableSuccessfully() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val value = interpreterV10.interpret(listOf(declarationNode))

        assertTrue(value.first.variableMap.isNotEmpty())
    }

    @Test
    fun test002_WhenGiven3declarationNode_createsVariablesSuccessfully() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val declarationNode2 =
            DeclarationNode(
                "y",
                TokenType.LET,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val declarationNode3 =
            DeclarationNode(
                "z",
                TokenType.LET,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val value = interpreterV10.interpret(listOf(declarationNode, declarationNode2, declarationNode3))

        assertTrue(value.first.variableMap.keys.size == 3)
    }

    @Test
    fun test003_WhenGivenAssignationNode_assignsValueToVariable() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val assignationNode = AssignationNode("x", Position(0, 1), NumberOperatorNode(5.0, Position(0, 1)))
        val value = interpreterV10.interpret(listOf(declarationNode, assignationNode))

        assertEquals("5", value.first.variableMap[Variable("x", "number", true)].toString())
    }

    @Test
    fun test004_WhenGivenAssignationNode_OfAVariableThatDoesNotExist_throwsException() {
        val assignationNode = AssignationNode("x", Position(0, 1), NumberOperatorNode(5.0, Position(0, 1)))

        val exception =
            assertThrows<IllegalArgumentException> {
                interpreterV10.interpret(listOf(assignationNode))
            }
        assertEquals("variable x not declared", exception.message)
    }

    @Test
    fun test005_WhenGivenAssignationNode_OfAVariableWithMismatchTypes_ThrowsException() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "string",
                Position(0, 1),
            )
        val assignationNode = AssignationNode("x", Position(0, 1), NumberOperatorNode(5.0, Position(0, 1)))

        val exception =
            assertThrows<IllegalArgumentException> {
                interpreterV10.interpret(listOf(declarationNode, assignationNode))
            }
        assertEquals("Type mismatch: string expected for variable x, but got 5", exception.message)
    }

    @Test
    fun test006_WhenGivenDeclarationAssignationNode_CreateVariableAndAssignValue() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val numberOperatorNode = NumberOperatorNode(5.0, Position(0, 1))
        val assignationDeclarationNode = DeclarationAssignationNode(declarationNode, numberOperatorNode)
        val value = interpreterV10.interpret(listOf(assignationDeclarationNode))

        assertEquals("5", value.first.variableMap[Variable("x", "number", true)].toString())
    }

    @Test
    fun test007_WhenGivenDeclarationAssignationNode_OfAVariableWithMismatchTypes_ThrowsException() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "string",
                Position(0, 1),
            )
        val numberOperatorNode = NumberOperatorNode(5.0, Position(0, 1))
        val assignationDeclarationNode = DeclarationAssignationNode(declarationNode, numberOperatorNode)

        val exception =
            assertThrows<IllegalArgumentException> {
                interpreterV10.interpret(listOf(assignationDeclarationNode))
            }
        assertEquals("Type mismatch: string expected for variable x, but got 5", exception.message)
    }

    @Test
    fun test008_WhenGivenBinaryOperatorNode_AddsTwoNumbers() {
        val numberOperatorNode = NumberOperatorNode(5.0, Position(0, 1))
        val numberOperatorNode2 = NumberOperatorNode(5.0, Position(0, 1))
        val binaryOperationNode = BinaryOperationNode("+", numberOperatorNode, numberOperatorNode2)
        val value = interpreterV10.interpret(listOf(binaryOperationNode))

        assertEquals("10", value.second[0])
    }

    @Test
    fun test009_WhenGivenBinaryOperatorNode_DividesTwoNumbers() {
        val numberOperatorNode = NumberOperatorNode(10.0, Position(0, 1))
        val numberOperatorNode2 = NumberOperatorNode(5.0, Position(0, 1))
        val binaryOperationNode = BinaryOperationNode("/", numberOperatorNode, numberOperatorNode2)
        val value = interpreterV10.interpret(listOf(binaryOperationNode))

        assertEquals("2", value.second[0])
    }

    @Test
    fun test010_WhenGivenBinaryOperatorNode_MultipliesTwoNumbers() {
        val numberOperatorNode = NumberOperatorNode(10.0, Position(0, 1))
        val numberOperatorNode2 = NumberOperatorNode(5.0, Position(0, 1))
        val binaryOperationNode = BinaryOperationNode("*", numberOperatorNode, numberOperatorNode2)
        val value = interpreterV10.interpret(listOf(binaryOperationNode))

        assertEquals("50", value.second[0])
    }

    @Test
    fun test011_WhenGivenBinaryOperatorNode_StringConcatenation() {
        val stringOperatorNode = StringOperatorNode("Hello ", TokenType.STRING_LITERAL, Position(0, 1))
        val stringOperatorNode2 = StringOperatorNode("World", TokenType.STRING_LITERAL, Position(0, 1))
        val binaryOperationNode = BinaryOperationNode("+", stringOperatorNode, stringOperatorNode2)
        val value = interpreterV10.interpret(listOf(binaryOperationNode))

        assertEquals("Hello World", value.second[0])
    }

    @Test
    fun test012_WhenGivenBinaryOperatorNode_StringConcatenationWithNumber() {
        val stringOperatorNode = StringOperatorNode("Hello ", TokenType.STRING_LITERAL, Position(0, 1))
        val numberOperatorNode = NumberOperatorNode(5.0, Position(0, 1))
        val binaryOperationNode = BinaryOperationNode("+", stringOperatorNode, numberOperatorNode)
        val value = interpreterV10.interpret(listOf(binaryOperationNode))

        assertEquals("Hello 5", value.second[0])
    }

    @Test
    fun test013_WhenGivenBinaryOperatorNode_StringError() {
        val stringOperatorNode = StringOperatorNode("Hello ", TokenType.STRING_LITERAL, Position(0, 1))
        val stringOperatorNode2 = StringOperatorNode("World", TokenType.STRING_LITERAL, Position(0, 1))
        val binaryOperationNode = BinaryOperationNode("-", stringOperatorNode, stringOperatorNode2)

        val exception =
            assertThrows<IllegalArgumentException> {
                interpreterV10.interpret(listOf(binaryOperationNode))
            }
        assertEquals("Invalid Operation", exception.message)
    }

    @Test
    fun test014_WhenGivenBinaryOperatorNode_IdentifierOperator() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val numberOperatorNode = NumberOperatorNode(5.0, Position(0, 1))
        val assignationDeclarationNode = DeclarationAssignationNode(declarationNode, numberOperatorNode)
        val identifierOperatorNode = IdentifierOperatorNode("x", Position(0, 1))
        val binaryOperationNode = BinaryOperationNode("+", identifierOperatorNode, numberOperatorNode)
        val value = interpreterV10.interpret(listOf(assignationDeclarationNode, binaryOperationNode))

        assertEquals("10", value.second[0])
    }

    @Test
    fun test015_WhenGivenBinaryOperatorNode_IdentifierOperatorError() {
        val identifierOperatorNode = IdentifierOperatorNode("x", Position(0, 1))
        val numberOperatorNode = NumberOperatorNode(5.0, Position(0, 1))
        val binaryOperationNode = BinaryOperationNode("+", identifierOperatorNode, numberOperatorNode)

        val exception =
            assertThrows<IllegalArgumentException> {
                interpreterV10.interpret(listOf(binaryOperationNode))
            }
        assertEquals("variable x not declared at column 0 line 1 ", exception.message)
    }

    @Test
    fun test016_WhenGivenBinaryOperatorNode_adds2IdentifiersConcatenation() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "string",
                Position(0, 1),
            )
        val stringOperatorNode = StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(0, 1))
        val assignationDeclarationNode = DeclarationAssignationNode(declarationNode, stringOperatorNode)
        val identifierOperatorNode = IdentifierOperatorNode("x", Position(0, 1))
        val binaryOperationNode = BinaryOperationNode("+", identifierOperatorNode, stringOperatorNode)
        val value = interpreterV10.interpret(listOf(assignationDeclarationNode, binaryOperationNode))
        assertEquals("HelloHello", value.second[0])
    }

    @Test
    fun test017_WhenGivenMethodNode_PrintlnMethodNodeValue() {
        val methodNode = MethodNode("println", StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(0, 1)), Position(0, 1))
        val value = interpreterV10.interpret(listOf(methodNode))
        assertEquals("Hello", value.second[0])
    }

    @Test
    fun test018_WhenGivenMethodNode_PrintlnMethodNodeValueWithVariable() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "string",
                Position(0, 1),
            )
        val stringOperatorNode = StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(0, 1))
        val assignationDeclarationNode = DeclarationAssignationNode(declarationNode, stringOperatorNode)
        val identifierOperatorNode = IdentifierOperatorNode("x", Position(0, 1))
        val methodNode = MethodNode("println", identifierOperatorNode, Position(0, 1))
        val value = interpreterV10.interpret(listOf(assignationDeclarationNode, methodNode))
        assertEquals("Hello", value.second[0])
    }

    @Test
    fun test019_WhenGivenMethodNode_PrintlnMethodNodeValueWithVariableError() {
        val identifierOperatorNode = IdentifierOperatorNode("x", Position(0, 1))
        val methodNode = MethodNode("println", identifierOperatorNode, Position(0, 1))

        val exception =
            assertThrows<IllegalArgumentException> {
                interpreterV10.interpret(listOf(methodNode))
            }
        assertEquals("variable x not declared at column 0 line 1 ", exception.message)
    }

    @Test
    fun test020_WhenGivenMethodNode_InvalidMethod() {
        val methodNode = MethodNode("print", StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(0, 1)), Position(0, 1))

        val exception =
            assertThrows<IllegalArgumentException> {
                interpreterV10.interpret(listOf(methodNode))
            }
        assertEquals("Invalid Method at column 0 row 1", exception.message)
    }

    @Test
    fun test021_WhenGivenMethodNode_PrintlnMethodNodeValueWithVariableAndString() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "string",
                Position(0, 1),
            )
        val stringOperatorNode = StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(0, 1))
        val assignationDeclarationNode = DeclarationAssignationNode(declarationNode, stringOperatorNode)
        val identifierOperatorNode = IdentifierOperatorNode("x", Position(0, 1))
        val stringOperatorNode2 = StringOperatorNode(" World", TokenType.STRING_LITERAL, Position(0, 1))
        val binaryOperationNode = BinaryOperationNode("+", identifierOperatorNode, stringOperatorNode2)
        val methodNode = MethodNode("println", binaryOperationNode, Position(0, 1))
        val value = interpreterV10.interpret(listOf(assignationDeclarationNode, methodNode))
        assertEquals("Hello World", value.second[0])
    }

    @Test
    fun test022_WhenGivenBinaryNode_With3StringsConcatenation() {
        val stringOperatorNode = StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(0, 1))
        val stringOperatorNode2 = StringOperatorNode(" World", TokenType.STRING_LITERAL, Position(0, 1))
        val stringOperatorNode3 = StringOperatorNode("!", TokenType.STRING_LITERAL, Position(0, 1))
        val binaryOperationNode = BinaryOperationNode("+", stringOperatorNode, stringOperatorNode2)
        val binaryOperationNode2 = BinaryOperationNode("+", binaryOperationNode, stringOperatorNode3)
        val value = interpreterV10.interpret(listOf(binaryOperationNode2))
        assertEquals("Hello World!", value.second[0])
    }
}
