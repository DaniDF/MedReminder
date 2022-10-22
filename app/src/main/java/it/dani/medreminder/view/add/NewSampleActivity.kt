package it.dani.medreminder.view.add

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.dani.medreminder.R
import it.dani.medreminder.model.Measure
import it.dani.medreminder.model.MeasureTypes
import it.dani.medreminder.model.Sample
import it.dani.medreminder.model.TimeRef
import it.dani.medreminder.view.MainActivity
import java.time.ZonedDateTime

/**
 * @author Daniele
 *
 * This class implements the procedure for adding a new sample
 */

class NewSampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_new_sample)

        val measureViewLayout = findViewById<LinearLayout>(R.id.layout_measure_list)
        val measureViewList : MutableList<View> = ArrayList()
        val measureList : MutableList<Measure> = ArrayList()

        this.addMeasureView(measureViewLayout,measureViewList,measureList)

        this.findViewById<ExtendedFloatingActionButton>(R.id.button_new_sample).apply {
            setOnClickListener {
                val newSample = Sample(measureList, TimeRef(ZonedDateTime.now()))
                Intent(this@NewSampleActivity,MainActivity::class.java).apply {
                    this.putExtra("NEW_SAMPLE",newSample)
                }.also {
                    this@NewSampleActivity.startActivity(it)
                    this@NewSampleActivity.finish()
                }
            }
        }
    }

    /**
     * This method add a add_sample_view.xml to the root [LinearLayout].
     * According this method modifies the previews add buttons in remove buttons and changes their onClickListener
     *
     * @param[measureViewRoot] The [LinearLayout] root, to attach to
     * @param[measureViewList] The list containing all the previews sample view
     * @param[measureList] The list containing all the [Measure] objects
     */
    private fun addMeasureView(measureViewRoot : LinearLayout, measureViewList: MutableList<View>, measureList : MutableList<Measure>) {

        measureViewList.forEachIndexed { index, measureView ->
            measureView.findViewById<FloatingActionButton>(R.id.add_del_measure).apply {
                this.setImageResource(R.drawable.remove)

                setOnClickListener {
                    this@NewSampleActivity.removeMeasureView(measureViewRoot,measureViewList,measureView,measureList,measureList[index])
                }
            }
        }

        measureViewList += this.layoutInflater.inflate(R.layout.add_sample_view,measureViewRoot,false).apply {
            val measure = Measure(MeasureTypes.BLOOD_OXYGENATION, Float.NaN)
            measureList += measure

            this.findViewById<Spinner>(R.id.measure_type_chooser).apply {
                val values = ArrayList<String>()
                MeasureTypes.values().toList().forEach { values += it.name }
                values.add(0,measure.measureLabel.name)
                this.adapter = ArrayAdapter(this@NewSampleActivity,android.R.layout.simple_list_item_1,values)

                this.onItemSelectedListener = LabelSelectorSpinnerSelectedListener(measure)
            }

            this.findViewById<TextView>(R.id.measure_input_value).apply {
                addTextChangedListener(InputValueTextViewWatcher(measure))
            }

            this.findViewById<FloatingActionButton>(R.id.add_del_measure).apply {
                setOnClickListener {
                    this@NewSampleActivity.addMeasureView(measureViewRoot,measureViewList,measureList)
                }
            }

        }.also {
            measureViewRoot.addView(it)
        }
    }

    /**
     * @param[measureViewRoot] The [LinearLayout] root, to attach to
     * @param[measureViewList] The list containing all the previews sample view
     * @param[measureView] The view to be removed
     * @param[measureList] The list containing all the [Measure] objects
     * @param[measure] The measure to be removed
     */
    private fun removeMeasureView(measureViewRoot : LinearLayout, measureViewList: MutableList<View>, measureView : View, measureList : MutableList<Measure>, measure : Measure) {
        measureViewRoot.removeView(measureView)
        measureViewList.remove(measureView)
        measureList.remove(measure)
    }
}

private class LabelSelectorSpinnerSelectedListener(private val measure: Measure) : OnItemSelectedListener {
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        MeasureTypes.values().find { mt ->
            mt.name.contentEquals(p0?.getItemAtPosition(p2)?.toString())
        }?.let { mt ->
            this.measure.measureLabel = mt
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}

private class InputValueTextViewWatcher(private val measure : Measure) : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        this.measure.measureValue = p0.toString().toFloat()
    }
    override fun afterTextChanged(p0: Editable?) {}

}