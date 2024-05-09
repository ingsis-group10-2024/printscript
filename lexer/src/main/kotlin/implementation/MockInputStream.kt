package implementation

import java.io.InputStream

class MockInputStream(line: String, private val numberOfLines: Int) : InputStream() {
    private var lineNumber: Int = 0
    private var index = 0
    private val lineBytes: IntArray = line.chars().toArray()

    override fun read(): Int {
        if (index >= lineBytes.size) {
            index = 0
            lineNumber += 1
        }

        if (lineNumber < numberOfLines) {
            val byteValue = lineBytes[index]
            index += 1
            return byteValue
        } else {
            return -1
        }
    }
}
