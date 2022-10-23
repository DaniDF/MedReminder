package it.dani.medreminder.persistence.filepersistence

import android.content.Context
import it.dani.medreminder.R
import it.dani.medreminder.model.MeasureTypes
import it.dani.medreminder.model.SampleDB
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

class CSVSampleDBPersistence(private val file: File, private val context: Context) : FileSampleDBPersistence() {
    override fun loadDB(): SampleDB {
        TODO("Not yet implemented")
    }

    override fun storeDB(sampleDB: SampleDB) {
        PrintWriter(FileWriter(this.file)).use { fileOut ->
            fileOut.print(this.context.resources.getString(R.string.export_csv_datetime_label))
            MeasureTypes.values().forEach { measureType ->
                fileOut.print(",${this.context.resources.getString(measureType.humanReadableString)}")
            }
            fileOut.println()

            sampleDB.samples.sortedBy { sample -> sample.datetime  }.forEach { sample ->
                val lineResult = StringBuilder(sample.datetime.toStringLong())

                MeasureTypes.values().forEach { measureType ->
                    sample.measures.firstOrNull { measure -> measure.measureLabel == measureType }?.let { measure ->  //TODO Nel caso di più misure dello stesso tipo nello stesso istante (non so quanto senso abbia ma intanto me lo segno)
                        lineResult.append(",${measure.measureValue}")
                    }
                }

                fileOut.println(lineResult.toString())
            }
        }
    }
}