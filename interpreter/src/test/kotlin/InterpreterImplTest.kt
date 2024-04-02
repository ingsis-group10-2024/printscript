import ast.*
import org.junit.Test

class InterpreterImplTest {
    @Test
    fun test001_WhenReceivingADeclarationBinaryNodeInterpreterShouldReturnItsDeclaration(){
        // Arrange
        val interpreter = InterpreterImpl()
        val ast = DeclarationNode("x", "Int")
        val astList = listOf(ast)
        // Act
        val response = interpreter.interpret(astList)
        // Assert
        assert(response is SuccessfulResponse)
    }
    @Test
    fun test002_WhenReceivingAnAssignationBinaryNodeInterpreterShouldReturnItsAssignation(){
        // Arrange
        val interpreter = InterpreterImpl()
        val ast = AssignationNode("x", NumberOperatorNode(5.0))
        val astList = listOf(ast)
        // Act
        val response = interpreter.interpret(astList)
        // Assert
        assert(response is SuccessfulResponse)
    }
}
