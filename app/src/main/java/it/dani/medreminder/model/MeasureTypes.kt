package it.dani.medreminder.model

import it.dani.medreminder.R
import java.io.Serializable

/**
 * @author Daniele
 *
 * This class represents all the possible types af measures
 *
 * @param[humanReadableString] A human readable version of the represented type, the reference of the string resource
 */
enum class MeasureTypes(val humanReadableString : Int, val measure_unit : Int) : Serializable {
    HEART_RATE(R.string.measure_type_heart_rate,R.string.measure_type_heart_rate_measure_unit),
    BLOOD_OXYGENATION(R.string.measure_type_blood_oxygenation,R.string.measure_type_blood_oxygenation_measure_unit),
    BLOOD_PRESSURE(R.string.measure_type_blood_pressure,R.string.measure_type_blood_pressure_measure_unit),
    GLYCEMIA(R.string.measure_type_glycemia,R.string.measure_type_glycemia_measure_unit)
}