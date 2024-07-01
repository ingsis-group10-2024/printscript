package variable

data class VariableMap(val variableMap: HashMap<Variable, String?>) {
    fun containsKey(variable: Variable): Boolean {
        return variableMap.containsKey(variable)
    }

    fun findKey(identifier: String): Variable? {
        return variableMap.keys.find { it.identifier == identifier }
    }
}
