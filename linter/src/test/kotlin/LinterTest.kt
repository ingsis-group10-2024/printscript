import ast.*
import implementation.LinterImpl
import org.junit.Assert.assertEquals
import org.junit.Test

class LinterTest {
    private val linter = LinterImpl()

    @Test
    fun `lint empty ast`() {
        val errors = linter.lint(emptyList())
        assertEquals(0, errors.size)
    }

    @Test
    fun `lint ast with empty declaration`() {
        val ast = listOf(DeclarationNode("", Position(1, 1), "", Position(1, 1)))
        val errors = linter.lint(ast)
        assertEquals(2, errors.size)
        assertEquals("Empty identifier", errors[0])
        assertEquals("Empty type", errors[1])
    }

    @Test
    fun `lint ast with invalid declaration assignation`() {
        val ast =
            listOf(
                DeclarationAssignationNode(
                    DeclarationNode("x", Position(1, 1), "number", Position(1, 1)),
                    StringOperatorNode("hola", Position(1, 1)),
                ),
                DeclarationAssignationNode(
                    DeclarationNode("y", Position(1, 1), "boolean", Position(1, 1)),
                    NumberOperatorNode(5.0, Position(1, 1)),
                ),
            )
        val errors = linter.lint(ast)
        assertEquals(2, errors.size)
        assertEquals("Invalid assignation for number type", errors[0])
        assertEquals("Invalid assignation for boolean type", errors[1])
    }

    @Test
    fun `lint ast with valid declaration assignation`() {
        val ast =
            listOf(
                DeclarationAssignationNode(
                    DeclarationNode("x", Position(1, 1), "number", Position(1, 1)),
                    NumberOperatorNode(5.0, Position(1, 1)),
                ),
                DeclarationAssignationNode(
                    DeclarationNode("y", Position(1, 1), "string", Position(1, 1)),
                    StringOperatorNode("hola", Position(1, 1)),
                ),
                DeclarationAssignationNode(
                    DeclarationNode("z", Position(1, 1), "boolean", Position(1, 1)),
                    BooleanOperatorNode(true, Position(1, 1)),
                ),
            )
        val errors = linter.lint(ast)
        assertEquals(0, errors.size)
    }

    @Test
    fun `lint ast with invalid binary operation`() {
        val ast =
            listOf(
                BinaryOperationNode(
                    "+",
                    NumberOperatorNode(5.0, Position(1, 1)),
                    StringOperatorNode("hola", Position(1, 1)),
                ),
            )
        val errors = linter.lint(ast)
        assertEquals(1, errors.size)
        assertEquals("Invalid operation between number and string.", errors[0])
    }

    @Test
    fun `lint ast with valid binary operation`() {
        val ast =
            listOf(
                BinaryOperationNode(
                    "+",
                    NumberOperatorNode(5.0, Position(1, 1)),
                    NumberOperatorNode(5.0, Position(1, 1)),
                ),
            )
        val errors = linter.lint(ast)
        assertEquals(0, errors.size)
    }

    @Test
    fun `lint ast with valid identifier`() {
        val ast =
            listOf(
                IdentifierOperatorNode("x", Position(1, 1)),
            )
        val errors = linter.lint(ast)
        assertEquals(0, errors.size)
    }

    @Test
    fun `lint ast with invalid identifier`() {
        val ast =
            listOf(
                IdentifierOperatorNode("1", Position(1, 1)),
            )
        val errors = linter.lint(ast)
        assertEquals(1, errors.size)
        assertEquals("Invalid identifier", errors[0])
    }

    @Test
    fun `lint ast with invalid binary operation missing left operand`() {
        val ast =
            listOf(
                BinaryOperationNode(
                    "+",
                    null,
                    NumberOperatorNode(5.0, Position(1, 1)),
                ),
            )
        val errors = linter.lint(ast)
        assertEquals(1, errors.size)
        assertEquals("Missing left operand in binary operation.", errors[0])
    }

    @Test
    fun `lint ast with invalid binary operation missing right operand`() {
        val ast =
            listOf(
                BinaryOperationNode(
                    "+",
                    NumberOperatorNode(5.0, Position(1, 1)),
                    null,
                ),
            )
        val errors = linter.lint(ast)
        assertEquals(1, errors.size)
        assertEquals("Missing right operand in binary operation.", errors[0])
    }

    @Test
    fun `lint ast with invalid binary operation missing operator`() {
        val ast =
            listOf(
                BinaryOperationNode(
                    "",
                    NumberOperatorNode(5.0, Position(1, 1)),
                    NumberOperatorNode(5.0, Position(1, 1)),
                ),
            )
        val errors = linter.lint(ast)
        assertEquals(1, errors.size)
        assertEquals("Missing operator in binary operation.", errors[0])
    }

    @Test
    fun `lint ast with invalid binary operation invalid operator`() {
        val ast =
            listOf(
                BinaryOperationNode(
                    ">",
                    NumberOperatorNode(5.0, Position(1, 1)),
                    NumberOperatorNode(5.0, Position(1, 1)),
                ),
            )
        val errors = linter.lint(ast)
        assertEquals(1, errors.size)
        assertEquals("Invalid operator in binary operation.", errors[0])
    }

    @Test
    fun `lint ast with invalid binary operation invalid operation between number and string`() {
        val ast =
            listOf(
                BinaryOperationNode(
                    "+",
                    NumberOperatorNode(5.0, Position(1, 1)),
                    StringOperatorNode("hola", Position(1, 1)),
                ),
            )
        val errors = linter.lint(ast)
        assertEquals(1, errors.size)
        assertEquals("Invalid operation between number and string.", errors[0])
    }

    @Test
    fun `lint ast with valid assignment`() {
        val ast =
            listOf(
                AssignationNode(
                    "x",
                    Position(1, 1),
                    NumberOperatorNode(5.0, Position(1, 1)),
                ),
            )
        val errors = linter.lint(ast)
        assertEquals(0, errors.size)
    }

    @Test
    fun `lint ast with invalid assignment`() {
        val ast =
            listOf(
                AssignationNode(
                    "",
                    Position(1, 1),
                    NumberOperatorNode(5.0, Position(1, 1)),
                ),
            )
        val errors = linter.lint(ast)
        assertEquals(1, errors.size)
        assertEquals("Missing identifier in assignation.", errors[0])
    }

    @Test
    fun `lint ast with valid declaration`() {
        val ast =
            listOf(
                DeclarationNode("x", Position(1, 1), "number", Position(1, 1)),
            )
        val errors = linter.lint(ast)
        assertEquals(0, errors.size)
    }

    @Test
    fun `lint ast with valid method`() {
        val ast =
            listOf(
                MethodNode("print", NumberOperatorNode(5.0, Position(1, 1)), Position(1, 1)),
            )
        val errors = linter.lint(ast)
        assertEquals(0, errors.size)
    }

    @Test
    fun `lint ast with invalid method`() {
        val ast =
            listOf(
                MethodNode(
                    "",
                    NumberOperatorNode(5.0, Position(1, 1)),
                    Position(1, 1),
                ),
            )
        val errors = linter.lint(ast)
        assertEquals(1, errors.size)
        assertEquals("Missing method identifier.", errors[0])
    }
}
