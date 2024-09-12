
import ast.AssignationNode
import ast.BinaryOperationNode
import ast.BooleanOperatorNode
import ast.ConditionNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.IfNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.Position
import ast.StringOperatorNode
import implementation.Formatter
import org.junit.Assert.assertEquals
import token.TokenType
import kotlin.test.Test

class FormatterTest {
    private val filePath = "src/main/resources/test_config_formatter.json"
    private val formatter = Formatter(filePath)

    @Test
    fun testDeclarationNodeFormatting() {
        // Simula un nodo de declaración: let x : number;
        val declarationNode =
            DeclarationNode(
                identifier = "x",
                declarationType = TokenType.LET,
                identifierPosition = Position(1, 1),
                type = "number",
                typePosition = Position(1, 5),
            )

        val result = formatter.format(listOf(declarationNode))

        val expected = "let x : number;\n"
        assertEquals(expected, result)
    }

    @Test
    fun testBinaryOperationNodeFormatting() {
        // Simula una operación binaria: 5 + 5;
        val binaryOperationNode =
            BinaryOperationNode(
                left = NumberOperatorNode(5.0, Position(1, 1)),
                right = NumberOperatorNode(5.0, Position(1, 5)),
                symbol = "+",
            )
        val result = formatter.format(listOf(binaryOperationNode))

        val expected = "5.0 + 5.0"
        assertEquals(expected, result)
    }

    @Test
    fun testBinaryOperationNodeFormattingv2() {
        // Simula una operación binaria: 5 + 5 * 5;
        val binaryOperationNode =
            BinaryOperationNode(
                left = NumberOperatorNode(5.0, Position(1, 1)),
                right =
                    BinaryOperationNode(
                        left = NumberOperatorNode(5.0, Position(1, 5)),
                        right = NumberOperatorNode(5.0, Position(1, 9)),
                        symbol = "*",
                    ),
                symbol = "+",
            )
        val result = formatter.format(listOf(binaryOperationNode))

        val expected = "5.0 + 5.0 * 5.0"
        assertEquals(expected, result)
    }

    @Test
    fun testBinaryOperationNodeFormattingv3() {
        // Simula una operación binaria: 5 + 5;
        val binaryOperationNode =
            BinaryOperationNode(
                left = NumberOperatorNode(5.0, Position(1, 1)),
                right = NumberOperatorNode(5.0, Position(1, 5)),
                symbol = "+",
            )
        val result = formatter.format(listOf(binaryOperationNode))
        val expected = "5.0 + 5.0"
        assertEquals(expected, result)
    }

    @Test
    fun testBinaryOperationNodeFormattingWithDifferentOperator() {
        // Simula una operación binaria: 5 - 5;
        val binaryOperationNode =
            BinaryOperationNode(
                left = NumberOperatorNode(5.0, Position(1, 1)),
                right = NumberOperatorNode(5.0, Position(1, 5)),
                symbol = "-",
            )
        val result = formatter.format(listOf(binaryOperationNode))
        val expected = "5.0 - 5.0"
        assertEquals(expected, result)
    }

    @Test
    fun testBinaryOperationNodeFormattingWithNestedOperations() {
        // Simula una operación binaria: 5 + 5 * 5;
        val binaryOperationNode =
            BinaryOperationNode(
                left = NumberOperatorNode(5.0, Position(1, 1)),
                right =
                    BinaryOperationNode(
                        left = NumberOperatorNode(5.0, Position(1, 5)),
                        right = NumberOperatorNode(5.0, Position(1, 9)),
                        symbol = "*",
                    ),
                symbol = "+",
            )
        val result = formatter.format(listOf(binaryOperationNode))
        val expected = "5.0 + 5.0 * 5.0"
        assertEquals(expected, result)
    }

    @Test
    fun testBooleanOperatorNodeFormatting() {
        // Simula un operador booleano: true;
        val booleanOperatorNode =
            BooleanOperatorNode(true, Position(1, 1))
        val result = formatter.format(listOf(booleanOperatorNode))

        val expected = "true"
        assertEquals(expected, result)
    }

    @Test
    fun testAssignationNodeFormatting() {
        // Simula un nodo de asignación: x = 5;
        val assignationNode =
            AssignationNode(
                identifier = "x",
                identifierPosition = Position(1, 1),
                assignation = NumberOperatorNode(5.0, Position(1, 5)),
            )

        val result = formatter.format(listOf(assignationNode))

        val expected = "x  =  5.0;\n"
        assertEquals(expected, result)
    }

    @Test
    fun testDeclarationAssignationNodeFormatting() {
        // Simula un nodo de declaración y asignación: let y : number = 10;
        val declarationNode =
            DeclarationNode(
                identifier = "y",
                declarationType = TokenType.LET,
                identifierPosition = Position(1, 1),
                type = "number",
                typePosition = Position(1, 5),
            )
        val numberNode = NumberOperatorNode(10.0, Position(1, 10))
        val declarationAssignationNode = DeclarationAssignationNode(declarationNode, numberNode)

        val result = formatter.format(listOf(declarationAssignationNode))

        val expected = "let y : number  =  10.0;\n"
        assertEquals(expected, result)
    }

    @Test
    fun testMethodNodeFormatting() {
        // Simula un println("Hello World!");
        val stringNode = StringOperatorNode("Hello World!", TokenType.STRING_TYPE, Position(1, 1))
        val methodNode = MethodNode("println", stringNode, Position(1, 1))

        val result = formatter.format(listOf(methodNode))

        val expected = "\nprintln(\"Hello World!\");\n"
        assertEquals(expected, result)
    }

    @Test
    fun testIfNodeFormatting() {
        val conditionNode =
            ConditionNode(
                conditionType = "==",
                left = IdentifierOperatorNode("x", Position(1, 1)),
                right = NumberOperatorNode(5.0, Position(1, 5)),
            )

        val trueBranchNode =
            AssignationNode(
                identifier = "x",
                identifierPosition = Position(1, 1),
                assignation = NumberOperatorNode(10.0, Position(1, 10)),
            )

        val ifNode =
            IfNode(
                condition = conditionNode,
                trueBranch = listOf(trueBranchNode),
                elseBranch = null,
            )

        val result = formatter.format(listOf(ifNode))

        val expected = "if (x == 5.0) {\nx  =  10.0;\n}"
        assertEquals(expected, result)
    }

    @Test
    fun testIfElseNodeFormatting() {
        val conditionNode =
            ConditionNode(
                conditionType = "==",
                left = IdentifierOperatorNode("x", Position(1, 1)),
                right = NumberOperatorNode(5.0, Position(1, 5)),
            )

        val trueBranchNode =
            AssignationNode(
                identifier = "x",
                identifierPosition = Position(1, 1),
                assignation = NumberOperatorNode(10.0, Position(1, 10)),
            )

        val elseBranchNode =
            AssignationNode(
                identifier = "x",
                identifierPosition = Position(1, 1),
                assignation = NumberOperatorNode(0.0, Position(1, 10)),
            )

        val ifNode =
            IfNode(
                condition = conditionNode,
                trueBranch = listOf(trueBranchNode),
                elseBranch = listOf(elseBranchNode),
            )

        val result = formatter.format(listOf(ifNode))

        val expected = "if (x == 5.0) {\nx  =  10.0;\n} else {\nx  =  0.0;\n}"
        assertEquals(expected, result)
    }
}
