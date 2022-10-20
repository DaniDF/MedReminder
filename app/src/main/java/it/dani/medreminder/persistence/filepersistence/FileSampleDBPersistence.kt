package it.dani.medreminder.persistence.filepersistence

import com.google.gson.Gson
import it.dani.medreminder.model.SampleDB
import it.dani.medreminder.persistence.SampleDBPersistence
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter
import java.io.PrintWriter

class FileSampleDBPersistence(private val filename : String) : SampleDBPersistence {
    override fun loadDB(): SampleDB {
        val result : SampleDB

        val gson = Gson()

        BufferedReader(FileReader(this.filename)).use {
            result = gson.fromJson(it,SampleDB::class.java)
        }

        return result
    }

    override fun storeDB(sampleDB: SampleDB) {
        val gson = Gson()

        PrintWriter(FileWriter(this.filename)).use {
            it.print(gson.toJson(sampleDB))
        }
    }
}