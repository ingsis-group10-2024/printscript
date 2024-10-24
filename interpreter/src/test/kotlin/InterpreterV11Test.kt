import ast.AssignationNode
import ast.ConditionNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.IfNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.Position
import ast.StringOperatorNode
import emitter.PrinterEmitter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reader.ConsoleInputReader
import reader.Reader
import token.TokenType
import variable.Variable
import variable.VariableMap
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InterpreterV11Test {
    private lateinit var interpreterFactory: InterpreterFactory
    private lateinit var interpreterV11: InterpreterManager
    private val outputter = PrinterEmitter()

    @BeforeEach
    fun setUp() {
        val reader: Reader = ConsoleInputReader()
        interpreterFactory = InterpreterFactory("1.1", VariableMap(HashMap()), reader, outputter)
        interpreterV11 = interpreterFactory.buildInterpreter()
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
        val value = interpreterV11.interpret(listOf(declarationNode))

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
        val value = interpreterV11.interpret(listOf(declarationNode, declarationNode2, declarationNode3))

        assertTrue(value.first.variableMap.keys.size == 3)
    }

    @Test
    fun test003_WhenGivenDeclarationAssignationNode_createsVariableSuccessfully() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val numberOperatorNode = NumberOperatorNode(5.0, Position(0, 1))
        val declarationAssignationNode = DeclarationAssignationNode(declarationNode, numberOperatorNode)
        val value = interpreterV11.interpret(listOf(declarationAssignationNode))

        assertTrue(value.first.variableMap.isNotEmpty())
    }

    @Test
    fun test004_WhenGivenDeclarationNode_ConstType_createsVariableSuccessfully() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.CONST,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val value = interpreterV11.interpret(listOf(declarationNode))

        assertTrue(value.first.variableMap.isNotEmpty())
    }

    @Test
    fun test005_WhenGivenDeclarationAssignationNode_ConstType_createsVariableSuccessfully() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.CONST,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val numberOperatorNode = NumberOperatorNode(5.0, Position(0, 1))
        val declarationAssignationNode = DeclarationAssignationNode(declarationNode, numberOperatorNode)
        val value = interpreterV11.interpret(listOf(declarationAssignationNode))

        assertTrue(!value.first.findKey("x")!!.isMutable)
    }

    @Test
    fun test006_WhenGivenAssignationNode_createsVariableSuccessfully() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val numberOperatorNode = NumberOperatorNode(5.0, Position(0, 1))
        val declarationAssignationNode = DeclarationAssignationNode(declarationNode, numberOperatorNode)
        val value = interpreterV11.interpret(listOf(declarationAssignationNode))

        assertEquals("5", value.first.variableMap[Variable("x", "number", true)].toString())
    }

    @Test
    fun test007_WhenGivenAssignationNodeAndAConstVariable_ThrowsException() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.CONST,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val numberOperatorNode = NumberOperatorNode(5.0, Position(0, 1))
        val declarationAssignationNode = DeclarationAssignationNode(declarationNode, numberOperatorNode)
        val assignation =
            AssignationNode(
                "x",
                Position(0, 1),
                NumberOperatorNode(10.0, Position(0, 1)),
            )

        val exception =
            assertThrows<IllegalArgumentException> {
                interpreterV11.interpret(listOf(declarationAssignationNode, assignation))
            }
        assertEquals("variable x is not mutable", exception.message)
    }

    @Test
    fun test008_WhenGivenAssignationNodeAndVariableNotDeclared_ThrowsException() {
        val assignation =
            AssignationNode(
                "x",
                Position(0, 1),
                NumberOperatorNode(10.0, Position(0, 1)),
            )

        val exception =
            assertThrows<IllegalArgumentException> {
                interpreterV11.interpret(listOf(assignation))
            }
        assertEquals("variable x not declared", exception.message)
    }

    @Test
    fun test009_WhenGivenIfNode_WithTrueConditionItExecutesTheTrueBranch() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val numberOperatorNode =
            NumberOperatorNode(
                5.0,
                Position(0, 1),
            )
        val declarationAssignationNode = DeclarationAssignationNode(declarationNode, numberOperatorNode)
        val conditionNode =
            ConditionNode(
                "==",
                IdentifierOperatorNode("x", Position(0, 1)),
                NumberOperatorNode(5.0, Position(0, 1)),
            )
        val trueBranch = MethodNode("println", StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(0, 1)), Position(0, 1))
        val ifNode = IfNode(conditionNode, listOf(trueBranch), null)
        val value = interpreterV11.interpret(listOf(declarationAssignationNode, ifNode))

        assertTrue(value.second[0] == "Hello")
    }

    @Test
    fun test010_WhenGivenIfNode_WithFalseConditionItExecutesTheElseBranch() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val numberOperatorNode =
            NumberOperatorNode(
                5.0,
                Position(0, 1),
            )
        val declarationAssignationNode = DeclarationAssignationNode(declarationNode, numberOperatorNode)
        val conditionNode =
            ConditionNode(
                "==",
                IdentifierOperatorNode("x", Position(0, 1)),
                NumberOperatorNode(6.0, Position(0, 1)),
            )
        val trueBranch = MethodNode("println", StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(0, 1)), Position(0, 1))
        val falseBranch = MethodNode("println", StringOperatorNode("World", TokenType.STRING_LITERAL, Position(0, 1)), Position(0, 1))
        val ifNode = IfNode(conditionNode, listOf(trueBranch), listOf(falseBranch))
        val value = interpreterV11.interpret(listOf(declarationAssignationNode, ifNode))

        assertTrue(value.second[0] == "World")
    }

    @Test
    fun test011_WhenGivenIfNode_ThrowErrorIfConditionIsNotBoolean() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val numberOperatorNode =
            NumberOperatorNode(
                5.0,
                Position(0, 1),
            )
        val declarationAssignationNode = DeclarationAssignationNode(declarationNode, numberOperatorNode)
        val conditionNode =
            ConditionNode(
                "+",
                IdentifierOperatorNode("x", Position(0, 1)),
                StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(0, 1)),
            )
        val trueBranch = MethodNode("println", StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(0, 1)), Position(0, 1))
        val ifNode = IfNode(conditionNode, listOf(trueBranch), null)

        val exception =
            assertThrows<IllegalArgumentException> {
                interpreterV11.interpret(listOf(declarationAssignationNode, ifNode))
            }
        assertEquals("Invalid operator", exception.message)
    }

    @Test
    fun test012_WhenGivenAssignationNode_BeAbleToGetVariableMap() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val numberOperatorNode =
            NumberOperatorNode(
                5.0,
                Position(0, 1),
            )
        val declarationAssignationNode = DeclarationAssignationNode(declarationNode, numberOperatorNode)

        val value = interpreterV11.interpret(listOf(declarationAssignationNode))
        val testMap = HashMap<Variable, String?>()
        var variableMap = VariableMap(testMap)
        variableMap.variableMap[Variable("x", "number", true)] = "5"
        assertEquals(variableMap, value.first)
    }

    @Test
    fun test013_WhenGivenAssignationNode_BeAbleToChangeTheValueOfAVariable() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val numberOperatorNode =
            NumberOperatorNode(
                5.0,
                Position(0, 1),
            )
        val declarationAssignationNode = DeclarationAssignationNode(declarationNode, numberOperatorNode)
        val newNumber = NumberOperatorNode(20.0, Position(0, 1))
        val assignationNode = AssignationNode("x", Position(0, 1), newNumber)

        val result = interpreterV11.interpret(listOf(declarationAssignationNode, assignationNode))

        assertEquals("20", result.first.variableMap[Variable("x", "number", true)])
    }

    @Test
    fun test014_WhenGivenAssignationNode_WithTypeMismatchThrowError() {
        val declarationNode =
            DeclarationNode(
                "x",
                TokenType.LET,
                Position(0, 1),
                "number",
                Position(0, 1),
            )
        val numberOperatorNode =
            NumberOperatorNode(
                5.0,
                Position(0, 1),
            )
        val declarationAssignationNode = DeclarationAssignationNode(declarationNode, numberOperatorNode)
        val newNumber = StringOperatorNode("Wololo", TokenType.STRING_LITERAL, Position(0, 1))
        val assignationNode = AssignationNode("x", Position(0, 1), newNumber)

        val exception =
            assertThrows<IllegalArgumentException> {
                interpreterV11.interpret(listOf(declarationAssignationNode, assignationNode))
            }
        assertEquals("Type mismatch: number expected for variable x, but got Wololo", exception.message)
    }
}
