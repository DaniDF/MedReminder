package it.dani.medreminder.view

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import it.dani.medreminder.R
import it.dani.medreminder.model.Sample
import it.dani.medreminder.model.SampleDB
import it.dani.medreminder.persistence.SampleDBPersistence
import it.dani.medreminder.persistence.filepersistence.FileSampleDBPersistence
import it.dani.medreminder.view.add.NewSampleActivity
import java.io.*

/**
 * @author Daniele
 *
 * This class is the main class of the app
 */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sampleDBPersistence : SampleDBPersistence = FileSampleDBPersistence(this.filesDir.path + File.separator + this.resources.getString(R.string.filename_sample_db))

        val sampleDB = try {
            sampleDBPersistence.loadDB()
        } catch (e : FileNotFoundException) {
            e.printStackTrace()
            Log.e("SAMPLE_DB_FILE","Error: sampleDB file not found")
            SampleDB()
        }

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

        val historyList = findViewById<LinearLayout>(R.id.layout_sample_history_list)

        sampleDB.samples.forEach { sample ->
            this.layoutInflater.inflate(R.layout.sample_view,historyList,false).apply {

                sample.measures.firstOrNull()?.let { measure ->
                    val measureLayoutView = this.findViewById<LinearLayout>(R.id.layout_measure_list)
                    this@MainActivity.layoutInflater.inflate(R.layout.measure_view,measureLayoutView,false).apply {
                        this.findViewById<TextView>(R.id.sample_number).apply {
                            this.text = measure.measureValue.toString()
                        }

                        this.findViewById<TextView>(R.id.sample_number_label).apply {
                            val text = "${this@MainActivity.resources.getString(measure.measureLabel.humanReadableString)}(${this@MainActivity.resources.getString(measure.measureLabel.measure_unit)})"
                            this.text = text.replace(" ","\n")
                        }
                    }.also { measureLayoutView.addView(it) }
                }

                this.findViewById<TextView>(R.id.sample_datetime_label).apply {
                    text = sample.datetime.toString()
                }

            }.also {
                historyList.addView(it)
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