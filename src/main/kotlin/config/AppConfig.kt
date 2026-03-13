package config

import java.io.FileReader
import java.util.*


class AppConfig {
    var apiKey: String=""
    //val dbUrl: String = System.getenv("dbUrl")
    fun loadFromFile(fileName: String) {
        val properties = Properties()
        properties.load(FileReader("src/main/resources/$fileName"))
        apiKey=System.getenv("riotApiKey")?:""
        //apiKey = properties.getProperty("API_KEY")
    }
}