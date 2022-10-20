package it.dani.medreminder.view

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import it.dani.medreminder.R
import it.dani.medreminder.model.Sample
import it.dani.medreminder.model.SampleDB
import it.dani.medreminder.persistence.SampleDBPersistence
import it.dani.medreminder.persistence.filepersistence.FileSampleDBPersistence
import it.dani.medreminder.view.add.NewSampleActivity
import java.io.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sampleDBPersistence : SampleDBPersistence = FileSampleDBPersistence(this.filesDir.path + File.separator + this.resources.getString(R.string.filename_sample_db))

        val sampleDB = try {
            sampleDBPersistence.loadDB()
        } catch (e : FileNotFoundException) {
            Log.e("SAMPLE_DB_FILE","Error: sampleDB file not found")
            SampleDB()
        }

        //TODO riemipire i vari blocchetti con i dati appena caricati

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.intent.getSerializableExtra("NEW_SAMPLE",Sample::class.java)?.let { sample ->
                sampleDB.samples += sample
            }
        } else {
            val newSample = this.intent.getSerializableExtra("NEW_SAMPLE") as Sample?
            newSample?.let { sample ->
                sampleDB.samples += sample
                sampleDBPersistence.storeDB(sampleDB)
            }
        }

        findViewById<ExtendedFloatingActionButton>(R.id.button_new_sample).apply {
            setOnClickListener {
                Intent(this@MainActivity,NewSampleActivity::class.java).also {
                    this@MainActivity.startActivity(it)
                }
            }
        }
    }
}