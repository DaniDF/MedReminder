package it.dani.medreminder.view.add

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import it.dani.medreminder.R
import it.dani.medreminder.model.Measure
import it.dani.medreminder.model.MeasureTypes
import it.dani.medreminder.model.Sample
import it.dani.medreminder.view.MainActivity
import java.time.LocalDateTime

class NewSampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_new_sample)

        val measureViewList = findViewById<LinearLayout>(R.id.layout_measure_list)
        val measureList : MutableList<Measure> = ArrayList()

        this.layoutInflater.inflate(R.layout.add_sample_view,measureViewList,false).apply {
            val measure = Measure(MeasureTypes.BLOOD_OXYGENATION, Float.MIN_VALUE)
            measureList += measure

            this.findViewById<Spinner>(R.id.measure_type_chooser).apply {
                val values = ArrayList<String>()
                MeasureTypes.values().toList().forEach { values += it.name }
                values.add(0,measure.measureLabel.name)
                this.adapter = ArrayAdapter(this@NewSampleActivity,android.R.layout.simple_list_item_1,values)

                this.onItemSelectedListener = object : OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        MeasureTypes.values().find { mt ->
                            mt.name.contentEquals(p0?.getItemAtPosition(p2)?.toString())
                        }?.let { mt ->
                            measure.measureLabel = mt
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }
            }

        }.also {
            measureViewList.addView(it)
        }

        this.findViewById<ExtendedFloatingActionButton>(R.id.button_new_sample).apply {
            setOnClickListener {
                val newSample = Sample(measureList,LocalDateTime.now())
                Intent(this@NewSampleActivity,MainActivity::class.java).apply {
                    this.putExtra("NEW_SAMPLE",newSample)
                }.also {
                    this@NewSampleActivity.startActivity(it)
                    this@NewSampleActivity.finish()
                }
            }
        }
    }
}