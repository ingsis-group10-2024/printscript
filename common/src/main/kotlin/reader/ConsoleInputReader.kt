package reader

class ConsoleInputReader : Reader {
    override fun read(message: String): String? {
        println(message)
        return readlnOrNull()
    }
}
