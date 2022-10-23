package it.dani.medreminder.persistence.filepersistence

import com.google.gson.Gson
import it.dani.medreminder.model.SampleDB
import java.io.*

class JSONSampleDBPersistence(private val file: File) : FileSampleDBPersistence() {
    override fun loadDB(): SampleDB {
        val result : SampleDB

        val gson = Gson()

        var fileInString: String
        BufferedReader(FileReader(this.file)).use { br ->
            fileInString = br.readText()
        }

        result = gson.fromJson(fileInString, SampleDB::class.java)

        return result
    }

    override fun storeDB(sampleDB: SampleDB) {
        val gson = Gson()

        PrintWriter(FileWriter(this.file)).use { fileOut ->
            fileOut.print(gson.toJson(sampleDB))
        }
    }
}