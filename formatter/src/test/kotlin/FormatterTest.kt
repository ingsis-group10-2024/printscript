
import ast.ASTNode
import ast.AssignationNode
import ast.BinaryOperationNode
import ast.DeclarationAssignationNode
import ast.DeclarationNode
import ast.NumberOperatorNode
import ast.StringOperatorNode
import org.junit.Test
import kotlin.test.assertEquals

class FormatterTest {
    private val config = loadConfig("config.json")
    val formatter = Formatter(config)

}
