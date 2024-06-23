package variable

class VariableFactory {
    fun createVariable(
        identifier: String,
        type: String?,
    ): Variable {
        return Variable(identifier, type)
    }
}
