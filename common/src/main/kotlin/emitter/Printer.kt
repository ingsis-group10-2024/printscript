package emitter

class Printer : PrintEmitter {
    override fun print(message: String) {
        println(message)
    }
}
