
import ast.AssignationNode
import ast.BinaryOperationNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.IdentifierOperatorNode
import ast.MethodNode
import ast.NumberOperatorNode
import ast.Position
import ast.StringOperatorNode
import implementation.SyntaxChecker
import org.junit.Assert.assertEquals
import org.junit.Test

class LinterTest {
    @Test
    fun `test lint with identifier operator node`() {
        val node = IdentifierOperatorNode("x", Position(1, 1))
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(0, errors.size)
    }

    @Test
    fun `test lint with invalid identifier operator node`() {
        val node = IdentifierOperatorNode("1x", Position(1, 1))
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(1, errors.size)
        assertEquals("Invalid identifier at Position(column=1, line=1)", errors[0])
    }

    @Test
    fun `test lint with empty identifier operator node`() {
        val node = IdentifierOperatorNode("", Position(1, 1))
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(2, errors.size)
        assertEquals("Invalid identifier at Position(column=1, line=1)", errors[0])
        assertEquals("Empty identifier at Position(column=1, line=1)", errors[1])
    }

    @Test
    fun `test lint with string operator node`() {
        val node = StringOperatorNode("hello", Position(1, 1))
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(0, errors.size)
    }

    @Test
    fun `test lint with empty string operator node`() {
        val node = StringOperatorNode("", Position(1, 1))
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(1, errors.size)
        assertEquals("Empty string value at Position(column=1, line=1)", errors[0])
    }

    @Test
    fun `test lint with number operator node`() {
        val node = NumberOperatorNode(5.0, Position(1, 1))
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(0, errors.size)
    }

    @Test
    fun `test lint with invalid number operator node`() {
        val node = NumberOperatorNode(Double.NaN, Position(1, 1))
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(1, errors.size)
        assertEquals("Invalid number value at Position(column=1, line=1)", errors[0])
    }

    @Test
    fun `test lint with invalid binary operation node`() {
        val left = NumberOperatorNode(5.0, Position(1, 1))
        val right = StringOperatorNode("hello", Position(1, 1))
        val node = BinaryOperationNode("+", left, right)
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(1, errors.size)
        assertEquals("Binary operation error: Invalid operation between number and string.", errors[0])
    }

    @Test
    fun `test lint with invalid binary operator node 2`() {
        val right = NumberOperatorNode(5.0, Position(1, 1))
        val node = BinaryOperationNode("+", null, right)
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(1, errors.size)
        assertEquals("Binary operation error: Missing left operand in binary operation.", errors[0])
    }

    @Test
    fun `test lint with invalid binary operator node 3`() {
        val left = StringOperatorNode("hello", Position(1, 1))
        val node = BinaryOperationNode("+", left, null)
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(1, errors.size)
        assertEquals("Binary operation error: Missing right operand in binary operation.", errors[0])
    }

    @Test
    fun `test lint with invalid binary operation node 4`() {
        val left = NumberOperatorNode(5.0, Position(1, 1))
        val right = StringOperatorNode("hello", Position(1, 1))
        val node = BinaryOperationNode("+", right, left)
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(1, errors.size)
        assertEquals("Binary operation error: Invalid operation between string and number.", errors[0])
    }

    @Test
    fun `test lint with invalid binary operation node 3`() {
        val left = NumberOperatorNode(5.0, Position(1, 1))
        val right = NumberOperatorNode(5.0, Position(1, 1))
        val node = BinaryOperationNode("", left, right)
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(1, errors.size)
        assertEquals("Binary operation error: Missing operator in binary operation.", errors[0])
    }

    @Test
    fun `test lint with declaration node`() {
        val node = DeclarationNode("x", Position(1, 1), "number", Position(1, 1))
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(0, errors.size)
    }

    @Test
    fun `test lint with invalid declaration node`() {
        val node = DeclarationNode("x", Position(1, 1), "", Position(1, 1))
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(1, errors.size)
        assertEquals("Empty type at Position(column=1, line=1)", errors[0])
    }

    @Test
    fun `test lint with declaration assignation node`() {
        val declaration = DeclarationNode("x", Position(1, 1), "number", Position(1, 1))
        val assignation = NumberOperatorNode(5.0, Position(1, 1))
        val node = DeclarationAssignationNode(declaration, assignation)
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(0, errors.size)
    }

    @Test
    fun `test lint with invalid declaration assignation node`() {
        val declaration = DeclarationNode("x", Position(1, 1), "number", Position(1, 1))
        val assignation = StringOperatorNode("hello", Position(1, 1))
        val node = DeclarationAssignationNode(declaration, assignation)
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(1, errors.size)
        assertEquals("Invalid assignation for number type", errors[0])
    }

    @Test
    fun `test lint with assignation node`() {
        val node = AssignationNode("x", Position(1, 1), NumberOperatorNode(5.0, Position(1, 1)))
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(0, errors.size)
    }

    @Test
    fun `test lint with invalid assignation node`() {
        val node = AssignationNode("", Position(1, 1), NumberOperatorNode(5.0, Position(1, 1)))
        val checker = SyntaxChecker()
        checker.visit(node)
        val errors = checker.getErrors()
        assertEquals(1, errors.size)
        assertEquals("Missing identifier in assignation at Position(column=1, line=1).", errors[0])
    }

    @Test
    fun `test lint with method node`() {
        val node = BinaryOperationNode("+", NumberOperatorNode(5.0, Position(1, 1)), NumberOperatorNode(5.0, Position(1, 1)))
        val method = MethodNode("sum", node, Position(1, 1))
        val checker = SyntaxChecker()
        checker.visit(method)
        val errors = checker.getErrors()
        assertEquals(0, errors.size)
    }

    @Test
    fun `test lint with invalid method node`() {
        val node = BinaryOperationNode("+", NumberOperatorNode(5.0, Position(1, 1)), NumberOperatorNode(5.0, Position(1, 1)))
        val method = MethodNode("", node, Position(1, 1))
        val checker = SyntaxChecker()
        checker.visit(method)
        val errors = checker.getErrors()
        assertEquals(1, errors.size)
        assertEquals("Missing method identifier at Position(column=1, line=1).", errors[0])
    }
}
