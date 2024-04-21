package `interface`

sealed interface InterpreterResponse

data class FailedResponse(val message: String) : InterpreterResponse

data class SuccessfulResponse(val message: String) : InterpreterResponse
