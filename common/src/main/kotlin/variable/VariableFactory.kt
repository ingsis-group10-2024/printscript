package variable

class VariableFactory {
    fun createVariable(
        identifier: String,
        type: String?,
        isMutabale: Boolean,
    ): Variable {
        return Variable(identifier, type, isMutabale)
    }
}
