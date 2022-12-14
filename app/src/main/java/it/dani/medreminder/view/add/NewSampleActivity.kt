package it.dani.medreminder.view.add

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import it.dani.medreminder.R
import it.dani.medreminder.model.Measure
import it.dani.medreminder.model.MeasureTypes
import it.dani.medreminder.model.Sample
import it.dani.medreminder.model.TimeRef
import it.dani.medreminder.view.MainActivity
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

/**
 * @author Daniele
 *
 * This class implements the procedure for adding a new sample
 */

class NewSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_new_sample)

        val dateTextView = this.findViewById<TextView>(R.id.text_current_date)
        val timeTextView = this.findViewById<TextView>(R.id.text_current_time)
        this.updateDateTime(dateTextView,timeTextView)

        val measureViewLayout = findViewById<LinearLayout>(R.id.layout_measure_list)
        val measureViewList : MutableList<View> = ArrayList()
        val measureList : MutableList<Measure> = ArrayList()

        this.addMeasureView(measureViewLayout,measureViewList,measureList)

        this.findViewById<ExtendedFloatingActionButton>(R.id.button_new_sample).apply {
            setOnClickListener {
                if(this@NewSampleActivity.areValuesValid(measureList)) {
                    val newSample = Sample(measureList, TimeRef(ZonedDateTime.now()))
                    Intent(this@NewSampleActivity,MainActivity::class.java).apply {
                        this.putExtra("NEW_SAMPLE",newSample)
                    }.also {
                        this@NewSampleActivity.startActivity(it)
                        this@NewSampleActivity.finish()
                    }
                } else {
                    Snackbar.make(this@NewSampleActivity.findViewById(R.id.activity_new_sample),
                        R.string.measure_validity_check_fail_message,
                        Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * This method updates every second the passed [dateTextView] and [timeTextView] according to the current date and time
     *
     * @param[dateTextView] A [TextView] to show the current date
     * @param[timeTextView] A [TextView] to show the current time
     */
    private fun updateDateTime(dateTextView : TextView, timeTextView : TextView) {
        Executors.newSingleThreadExecutor().also { exe ->
            val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.getDefault())
            val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
            exe.execute {
                runOnUiThread {
                    dateTextView.apply {
                        text = LocalDateTime.now().format(dateFormatter)
                    }

                    timeTextView.apply {
                        text = LocalTime.now().format(timeFormatter)
                    }
                }

                Thread.sleep(1000)

                this.updateDateTime(dateTextView,timeTextView)
            }
        }
    }

    /**
     * This method checks is all the measures are not NaN
     *
     * @param[measures] A list of measure to check
     * @return true if all the measureValue are non NaN, false otherwise
     */
    private fun areValuesValid(measures : List<Measure>) : Boolean {
        var result = true

        measures.forEach { measure ->
            result = result && !measure.measureValue.isNaN()
        }

        return result
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
                    val disappearAnimation = AnimationUtils.loadAnimation(this@NewSampleActivity,R.anim.disappear).apply {
                        this.setAnimationListener(object : AnimationListener {
                            override fun onAnimationStart(p0: Animation?) {}
                            override fun onAnimationRepeat(p0: Animation?) {}
                            override fun onAnimationEnd(p0: Animation?) {
                                this@NewSampleActivity.removeMeasureView(measureViewRoot,measureViewList,measureView,measureList,measureList[index])
                            }

                        })
                    }
                    measureView.startAnimation(disappearAnimation)
                    for(postIndex in index+1..measureViewList.lastIndex) {
                        measureViewList[postIndex].startAnimation(AnimationUtils.loadAnimation(this@NewSampleActivity,R.anim.rise))
                    }
                }
            }
        }

        measureViewList += this.layoutInflater.inflate(R.layout.add_sample_view,measureViewRoot,false).apply {
            this.startAnimation(AnimationUtils.loadAnimation(this@NewSampleActivity,R.anim.fall))
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
        try {
            this.measure.measureValue = p0.toString().toFloat()
        } catch (_: NumberFormatException) {}
    }
    override fun afterTextChanged(p0: Editable?) {}

}