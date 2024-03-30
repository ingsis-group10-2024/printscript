import ast.BinaryNode

interface Interpreter {
    fun interpret(astList: List<BinaryNode>) : Any?
}