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
import org.junit.Before
import org.junit.Test
import reader.ConsoleInputReader
import strategy.InterpreterManagerImplStrategyV11
import token.TokenType
import variable.Variable
import variable.VariableMap
import java.util.HashMap
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InterpreterManagerImplTest {
    private lateinit var interpreter: InterpreterManagerImplStrategyV11

    @Before
    fun setup() {
        val variableMap = VariableMap(HashMap())
        val envMap = VariableMap(HashMap())
        val reader = ConsoleInputReader()

        interpreter = InterpreterManagerImplStrategyV11(variableMap, envMap, reader)
    }

    @Test
    fun test001_WhenReceivingADeclarationBinaryNodeInterpreterShouldReturnItsDeclaration() {
        // Arrange
        val ast = DeclarationNode("x", TokenType.LET, Position(1, 1), "Int", Position(2, 1))
        val astList = listOf(ast)
        // Act
        val response = interpreter.interpret(astList)
        // Assert
        assertTrue(response.second.isEmpty())
        assertTrue(interpreter.variableMap.containsKey(Variable("x", "Int", true)))
    }

    @Test
    fun test002_WhenReceivingAnAssignationBinaryNodeInterpreterShouldReturnItsAssignation() {
        // Arrange
        val ast =
            DeclarationAssignationNode(
                DeclarationNode("x", TokenType.LET, Position(1, 1), "number", Position(2, 1)),
                NumberOperatorNode(5.0, Position(3, 1)),
            )
        val astList = listOf(ast)
        // Act
        val response = interpreter.interpret(astList)
        // Assert
        assertTrue(response.second.isEmpty())
        assertTrue(interpreter.variableMap.containsKey(Variable("x", "number", true)))
    }

    @Test
    fun test003_shouldInterpretDeclarationNode() {
        val ast = DeclarationNode("x", TokenType.LET, Position(1, 1), "Int", Position(2, 1))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertTrue(response.second.isEmpty())
        assertTrue(interpreter.variableMap.containsKey(Variable("x", "Int", true)))
    }

    @Test
    fun test004_shouldInterpretAssignationNode() {
        val ast =
            DeclarationAssignationNode(
                DeclarationNode(
                    "X",
                    TokenType.LET,
                    Position(1, 1),
                    "number",
                    Position(2, 1),
                ),
                NumberOperatorNode(5.0, Position(3, 1)),
            )
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertTrue(response.second.isEmpty())
        assertTrue(interpreter.variableMap.containsKey(Variable("X", "number", true)))
    }

    @Test
    fun test005_shouldInterpretMethodNode() {
        val ast = MethodNode("println", StringOperatorNode("Hello, World!", TokenType.STRING_LITERAL, Position(1, 1)), Position(1, 1))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)

        assertEquals("Hello, World!", response.second[0])
    }

//    @Test
//    fun test006_shouldReturnFailedResponseForInvalidMethod() {
//        val ast = MethodNode("invalidMethod", StringOperatorNode("Hello, World!", Position(1, 1)), Position(1, 1))
//        val astList = listOf(ast)
//        val response = interpreter.interpret(astList)
//        assertEquals("Invalid Method", response.second)
//    }
//
//    @Test
//    fun test007_shouldReturnFailedResponseForInvalidOperation() {
//        val ast = BinaryOperationNode("invalidOperation", NumberOperatorNode(5.0, Position(1, 1)), NumberOperatorNode(5.0, Position(2, 1)))
//        val astList = listOf(ast)
//        val response = interpreter.interpret(astList)
//        assertEquals("Invalid Operation", response.second)
//    }
//
//    @Test
//    fun test008_shouldReturnNullForEmptyAstList() {
//        val astList = emptyList<ASTNode>()
//        val response = interpreter.interpret(astList)
//        assertNull(response.second)
//    }
//
    @Test
    fun test009_whenNeededToInterpretANumberOperatorNodeInterpreterShouldReturnItsValue() {
        // Arrange
        val ast = NumberOperatorNode(5.0, Position(1, 1))
        val astList = listOf(ast)
        // Act
        val response = interpreter.interpret(astList)
        // Assert
        assertEquals("5.0", response.second[0])
    }

    @Test
    fun test010_WhenGivenTwoAssignationNodesAndAMethodNodeThatRunsBothOfThemTogetherShouldReturnThePrintedString() {
        // Arrange
        val ast1 =
            DeclarationAssignationNode(
                DeclarationNode("x", TokenType.LET, Position(1, 1), "number", Position(2, 1)),
                NumberOperatorNode(5.0, Position(3, 1)),
            )
        val ast2 =
            DeclarationAssignationNode(
                DeclarationNode("y", TokenType.LET, Position(4, 1), "number", Position(5, 1)),
                NumberOperatorNode(3.0, Position(6, 1)),
            )
        val method =
            MethodNode(
                "println",
                BinaryOperationNode("+", IdentifierOperatorNode("x", Position(7, 1)), IdentifierOperatorNode("y", Position(8, 1))),
                Position(9, 1),
            )
        val astList = listOf(ast1, ast2, method)
        // Act
        val response = interpreter.interpret(astList)
        // Assert
        assertEquals("8.0", response.second[0])
    }

    @Test
    fun `test011 interpretBinaryNode with BinaryOperationNode`() {
        val leftNode = NumberOperatorNode(5.0, Position(1, 1))
        val rightNode = NumberOperatorNode(3.0, Position(2, 1))
        val binaryNode = BinaryOperationNode("+", leftNode, rightNode)
        val methodNode = MethodNode("println", binaryNode, Position(3, 1))
        val result = interpreter.interpret(listOf(methodNode))
        assertEquals("8.0", result.second[0])
    }

    @Test
    fun `test 012 - should interpret an AST and execute it`() {
        val ast =
            listOf(
                DeclarationAssignationNode(
                    DeclarationNode("a", TokenType.LET, Position(1, 1), "string", Position(2, 1)),
                    BinaryOperationNode(
                        "+",
                        StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(3, 1)),
                        NumberOperatorNode(5.0, Position(4, 1)),
                    ),
                ),
                MethodNode(
                    "println",
                    BinaryOperationNode(
                        "+",
                        IdentifierOperatorNode("a", Position(5, 1)),
                        StringOperatorNode(
                            "",
                            TokenType.STRING_LITERAL,
                            Position(6, 1),
                        ),
                    ),
                    Position(7, 1),
                ),
            )
        val result = interpreter.interpret(ast)
        assertEquals("Hello5.0", result.second[0])
    }

    @Test
    fun `test 013 - should interpret an AST and execute it`() {
        val ast =
            listOf(
                DeclarationAssignationNode(
                    DeclarationNode("a", TokenType.LET, Position(1, 1), "number", Position(2, 1)),
                    NumberOperatorNode(1.0, Position(3, 1)),
                ),
                DeclarationAssignationNode(
                    DeclarationNode("x", TokenType.LET, Position(4, 1), "string", Position(5, 1)),
                    BinaryOperationNode(
                        "+",
                        IdentifierOperatorNode("a", Position(6, 1)),
                        StringOperatorNode("Hello", TokenType.STRING_LITERAL, Position(7, 1)),
                    ),
                ),
                MethodNode("println", IdentifierOperatorNode("x", Position(8, 1)), Position(9, 1)),
            )
        val result = interpreter.interpret(ast)
        assertEquals("1.0Hello", result.second[0])
    }

//
// //    @Test
// //    fun test014_interpretAssignationWithUndeclaredVariable() {
// //        val ast = AssignationNode("z", NumberOperatorNode(5.0, Position(1, 1)), Position(2, 1))
// //        val astList = listOf(ast)
// //        val response = interpreter.interpret(astList)
// //        assertEquals("variable.Variable z not declared", response.second)
// //    }
//
    @Test
    fun test015_interpretBinaryNodeWithSubtractionOperation() {
        val ast = BinaryOperationNode("-", NumberOperatorNode(5.0, Position(1, 1)), NumberOperatorNode(3.0, Position(2, 1)))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("2.0", response.second[0])
    }

    @Test
    fun test016_interpretBinaryNodeWithIdentifierOperands() {
        val ast1 =
            DeclarationAssignationNode(
                DeclarationNode("x", TokenType.LET, Position(1, 1), "number", Position(2, 1)),
                NumberOperatorNode(5.0, Position(3, 1)),
            )
        val ast2 =
            DeclarationAssignationNode(
                DeclarationNode("y", TokenType.LET, Position(4, 1), "number", Position(5, 1)),
                NumberOperatorNode(3.0, Position(6, 1)),
            )
        val ast3 =
            MethodNode(
                "println",
                BinaryOperationNode("+", IdentifierOperatorNode("x", Position(7, 1)), IdentifierOperatorNode("y", Position(8, 1))),
                Position(9, 1),
            )
        val astList = listOf(ast1, ast2, ast3)
        val response = interpreter.interpret(astList)
        assertEquals("8.0", response.second[0])
    }

//
//    @Test
//    fun test017_interpretMethodWithInvalidMethod() {
//        val ast = MethodNode("invalidMethod", StringOperatorNode("Hello, World!", Position(1, 1)), Position(2, 1))
//        val astList = listOf(ast)
//        val response = interpreter.interpret(astList)
//        assertEquals("Invalid Method", response.second)
//    }
//
    @Test
    fun test018_interpretBinaryNodeWithDivisionOperation() {
        val ast = BinaryOperationNode("/", NumberOperatorNode(10.0, Position(1, 1)), NumberOperatorNode(2.0, Position(2, 1)))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("5.0", response.second[0])
    }

    @Test
    fun test019_interpretBinaryNodeWithMultiplicationOperation() {
        val ast = BinaryOperationNode("*", NumberOperatorNode(5.0, Position(1, 1)), NumberOperatorNode(3.0, Position(2, 1)))
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertEquals("15.0", response.second[0])
    }

    @Test
    fun test022_GivenBinaryOperationNodeWithAddition_WhenInterpreted_ShouldReturnResultOfAddition() {
        val binaryOperationNode = BinaryOperationNode("+", NumberOperatorNode(5.0, Position(1, 1)), NumberOperatorNode(3.0, Position(2, 1)))
        val response = interpreter.interpret(listOf(binaryOperationNode))
        assertEquals("8.0", response.second[0])
    }

    @Test
    fun test023_GivenBinaryOperationNodeWithSubtraction_WhenInterpreted_ShouldReturnResultOfSubtraction() {
        val binaryOperationNode = BinaryOperationNode("-", NumberOperatorNode(5.0, Position(1, 1)), NumberOperatorNode(3.0, Position(2, 1)))
        val response = interpreter.interpret(listOf(binaryOperationNode))
        assertEquals("2.0", response.second[0])
    }

    @Test
    fun test024_GivenBinaryOperationNodeWithMultiplication_WhenInterpreted_ShouldReturnResultOfMultiplication() {
        val binaryOperationNode = BinaryOperationNode("*", NumberOperatorNode(5.0, Position(1, 1)), NumberOperatorNode(3.0, Position(2, 1)))
        val response = interpreter.interpret(listOf(binaryOperationNode))
        assertEquals("15.0", response.second[0])
    }

    @Test
    fun test025_GivenBinaryOperationNodeWithDivision_WhenInterpreted_ShouldReturnResultOfDivision() {
        val binaryOperationNode =
            BinaryOperationNode("/", NumberOperatorNode(10.0, Position(1, 1)), NumberOperatorNode(2.0, Position(2, 1)))
        val response = interpreter.interpret(listOf(binaryOperationNode))
        assertEquals("5.0", response.second[0])
    }

//
//    @Test
//    fun test026_GivenMethodNodeWithPrintln_WhenInterpreted_ShouldReturnPrintedString() {
//        val methodNode = MethodNode("println", StringOperatorNode("Hello, World!", Position(1, 1)), Position(2, 1))
//        val response = interpreter.interpret(listOf(methodNode))
//        assertEquals("Hello, World!", response.second)
//    }
//
    @Test
    fun `Given IfOperatorNode with true condition, should interpret ifBody`() {
        val ifBodyNode =
            MethodNode(
                "println",
                StringOperatorNode(
                    "If Body Executed",
                    TokenType.STRING_LITERAL,
                    Position(1, 1),
                ),
                Position(1, 1),
            )
        val elseBodyNode =
            MethodNode(
                "println",
                StringOperatorNode(
                    "Else Body Executed",
                    TokenType.STRING_LITERAL,
                    Position(2, 1),
                ),
                Position(2, 1),
            )
        val ifNode = IfNode(BooleanOperatorNode(true, Position(3, 1)), listOf(ifBodyNode), listOf(elseBodyNode))
        val response = interpreter.interpret(listOf(ifNode))
        assertEquals("If Body Executed", response.second[0])
    }

    @Test
    fun `Given IfOperatorNode with false condition, should interpret elseBody`() {
        val ifBodyNode =
            MethodNode(
                "println",
                StringOperatorNode(
                    "If Body Executed",
                    TokenType.STRING_LITERAL,
                    Position(1, 1),
                ),
                Position(1, 1),
            )
        val elseBodyNode =
            MethodNode(
                "println",
                StringOperatorNode(
                    "Else Body Executed",
                    TokenType.STRING_LITERAL,
                    Position(2, 1),
                ),
                Position(2, 1),
            )
        val ifNode = IfNode(BooleanOperatorNode(false, Position(3, 1)), listOf(ifBodyNode), listOf(elseBodyNode))
        val response = interpreter.interpret(listOf(ifNode))
        assertEquals("Else Body Executed", response.second[0])
    }

//    @Test
//    fun test027_Given_an_Env_variable_that_exists_in_the_map_it_should_return_its_value() {
//        val envMap = VariableMap(HashMap())
//        envMap.variableMap.put(Variable("X", "String" , false), "5.0")
//        val interpreter = InterpreterManagerImplStrategyV11(VariableMap(HashMap()), envMap , ConsoleInputReader())
//
//        val ast = MethodNode("readEnv", StringOperatorNode("X", Position(1, 1)), Position(1, 1))
//        val response = interpreter.interpret(listOf(ast))
//        assertEquals("5.0", response.second[0])
//    }
//
//    @Test
//    fun test028_Given_an_Env_variable_that_does_not_exist_in_the_map_it_should_return_it_should_fail() {
//        val envMap = VariableMap(HashMap())
//        val interpreter = InterpreterManagerImplStrategy(VariableMap(HashMap()), envMap)
//
//        val ast = MethodNode("readEnv", StringOperatorNode("x", Position(1, 1)), Position(1, 1))
//        val response = interpreter.interpret(listOf(ast))
//        assertEquals("Environment variable x not found", response.second)
//    }
//
    @Test
    fun test029_GivenADeclarationAssignationNodeWithAnUndeclaredVariableItShouldAddTheVariableToTheMap() {
        val ast =
            DeclarationAssignationNode(
                DeclarationNode("x", TokenType.LET, Position(1, 1), "number", Position(2, 1)),
                NumberOperatorNode(5.0, Position(3, 1)),
            )
        val astList = listOf(ast)
        val response = interpreter.interpret(astList)
        assertTrue(response.second.isEmpty())
        assertTrue(interpreter.variableMap.containsKey(Variable("x", "number", true)))
    }
}
