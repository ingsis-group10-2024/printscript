package config

import com.google.gson.Gson
import java.io.File

class JsonReader {
    inline fun <reified T> readJsonFromFile(filename: String): T? {
        val jsonString = File(filename).readText()
        return Gson().fromJson(jsonString, T::class.java)
    }
}
