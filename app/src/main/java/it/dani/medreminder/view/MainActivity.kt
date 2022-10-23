package it.dani.medreminder.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import it.dani.medreminder.R
import it.dani.medreminder.model.Sample
import it.dani.medreminder.model.SampleDB
import it.dani.medreminder.persistence.SampleDBPersistence
import it.dani.medreminder.persistence.filepersistence.CSVSampleDBPersistence
import it.dani.medreminder.persistence.filepersistence.JSONSampleDBPersistence
import it.dani.medreminder.view.add.NewSampleActivity
import java.io.*

/**
 * @author Daniele
 *
 * This class is the main class of the app
 */

class MainActivity : AppCompatActivity() {
    private lateinit var sampleDB : SampleDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sampleDBPersistence : SampleDBPersistence = JSONSampleDBPersistence(File(this.filesDir.path + File.separator + this.resources.getString(R.string.filename_sample_db)))

        this.sampleDB = try {
            sampleDBPersistence.loadDB()
        } catch (e : FileNotFoundException) {
            e.printStackTrace()
            Log.e("SAMPLE_DB_FILE","Error: sampleDB file not found")
            SampleDB()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.intent.getSerializableExtra("NEW_SAMPLE",Sample::class.java)?.let { sample ->
                this.sampleDB.samples += sample
            }
        } else {
            val newSample = this.intent.getSerializableExtra("NEW_SAMPLE") as Sample?
            newSample?.let { sample ->
                this.sampleDB.samples += sample
                sampleDBPersistence.storeDB(this.sampleDB)
            }
        }

        val historyList = findViewById<LinearLayout>(R.id.layout_sample_history_list)
        this.addSampleView(historyList,this.sampleDB.samples)

        findViewById<ExtendedFloatingActionButton>(R.id.button_new_sample).apply {
            setOnClickListener {
                Intent(this@MainActivity,NewSampleActivity::class.java).also {
                    this@MainActivity.startActivity(it)//,ActivityOptions.makeSceneTransitionAnimation(this@MainActivity).toBundle())
                }
            }
        }
    }

    /**
     * This method add a sample_view.xml to the root [LinearLayout] and inflate all the related measures
     *
     * @param[sampleViewRoot] The root view [LinearLayout]
     * @param[sampleList] The list of samples to be displayed
     */
    private fun addSampleView(sampleViewRoot: LinearLayout, sampleList: MutableList<Sample>) {
        sampleList.reversed().forEach { sample ->
            this.layoutInflater.inflate(R.layout.sample_view,sampleViewRoot,false).apply {

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

                val textView = this.findViewById<TextView>(R.id.sample_datetime_label).apply {
                    text = sample.datetime.toString()
                }

                val sampleDateTime = this.findViewById<ConstraintLayout>(R.id.sample_datetime)
                val sampleMeasureView = this.findViewById<ConstraintLayout>(R.id.sample_measure_view)

                this.findViewById<ImageView>(R.id.expand_data).apply {
                    var flagMore = true
                    setOnClickListener {
                        flagMore = when(flagMore) {
                            true -> {
                                this@MainActivity.expandMore(sample,sampleMeasureView,sampleDateTime,textView,this)
                                false
                            }
                            false -> {
                                this@MainActivity.expandLess(sample,sampleMeasureView,sampleDateTime,textView,this)
                                true
                            }
                        }

                    }
                }

            }.also {
                sampleViewRoot.addView(it)
            }
        }
    }

    /**
     * This method execute the expand animation of a sample
     *
     * @param[sample] The target sample
     * @param[sampleMeasureView] The view where all the measure will be displayed
     * @param[sampleDateTime] The [ConstraintLayout] where is the date
     * @param[sampleDateTimeLabel] The date [TextView]
     * @param[imageView] The expand button image
     */
    private fun expandMore(sample: Sample, sampleMeasureView : ConstraintLayout, sampleDateTime : ConstraintLayout, sampleDateTimeLabel : TextView, imageView : ImageView) {
        imageView.setImageResource(R.drawable.expand_less)
        sampleDateTime.apply {
            val params = (this.layoutParams as ConstraintLayout.LayoutParams)
            params.horizontalBias = 0.5f
            layoutParams = params
        }

        sampleDateTimeLabel.apply {
            text = sample.datetime.toStringLong()
        }

        ConstraintSet().apply {
            connect(R.id.sample_measure_view,ConstraintSet.TOP,R.id.sample_datetime,ConstraintSet.BOTTOM)
        }.also {
            it.applyTo(sampleMeasureView)
        }
    }

    /**
     * This method execute the un-expand animation of a sample
     *
     * @param[sample] The target sample
     * @param[sampleMeasureView] The view where all the measure will be displayed
     * @param[sampleDateTime] The [ConstraintLayout] where is the date
     * @param[sampleDateTimeLabel] The date [TextView]
     * @param[imageView] The un-expand button image
     */
    private fun expandLess(sample: Sample, sampleMeasureView : ConstraintLayout, sampleDateTime : ConstraintLayout, sampleDateTimeLabel : TextView, imageView : ImageView) {
        imageView.setImageResource(R.drawable.expand_more)
        sampleDateTime.apply {
            val params = (this.layoutParams as ConstraintLayout.LayoutParams)
            params.horizontalBias = 1f
            layoutParams = params
        }

        sampleDateTimeLabel.apply {
            text = sample.datetime.toStringShort()
        }

        ConstraintSet().apply {
            connect(R.id.sample_measure_view,ConstraintSet.TOP,R.id.sample_datetime,ConstraintSet.TOP)
        }.also {
            it.applyTo(sampleMeasureView)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menuInflater.inflate(R.menu.menu_export,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_export_csv -> {
                return if(this::sampleDB.isInitialized) {
                    val fileOut = File.createTempFile("temp",".csv",this.filesDir)
                    CSVSampleDBPersistence(fileOut,this).also { filePersistence ->
                        filePersistence.storeDB(this.sampleDB)
                    }

                    this.openFile(fileOut)

                } else {
                    Log.e("EXPORT","Error export json: sampleDB not initialized")
                    false
                }
            }
            R.id.menu_export_json -> {
                return if(this::sampleDB.isInitialized) {
                    val fileOut = File.createTempFile("temp",".json",this.filesDir)
                    JSONSampleDBPersistence(fileOut).also { filePersistence ->
                        filePersistence.storeDB(this.sampleDB)
                    }

                    this.openFile(fileOut)

                } else {
                    Log.e("EXPORT","Error export json: sampleDB not initialized")
                    false
                }
            }
            R.id.menu_import_csv -> {
                true
            }
            R.id.menu_import_json -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * This function search an app on the device that can handle the file
     *
     * @param[fileOut] The file to be opened
     */
    private fun openFile(fileOut : File) : Boolean {
        return try {
            Intent().apply {
                action = Intent.ACTION_VIEW
                val fileOutUri = FileProvider.getUriForFile(this@MainActivity,"${this@MainActivity.applicationContext.packageName}.provider",fileOut)
                this.setDataAndType(fileOutUri,this@MainActivity.contentResolver.getType(fileOutUri))
                this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }.also { intent ->
                this.startActivity(intent)
            }

            true
        } catch (e : ActivityNotFoundException) {
            e.printStackTrace()
            Log.e("EXPORT","Error: no activity found that can handle the data: ${e.message}")
            Snackbar.make(this.findViewById(R.id.activity_main),R.string.menu_export_no_activity_available,Snackbar.LENGTH_SHORT).show()
            false
        }
    }
}